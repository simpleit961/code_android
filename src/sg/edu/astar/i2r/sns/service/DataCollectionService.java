package sg.edu.astar.i2r.sns.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import sg.edu.astar.i2r.sns.collectiondatabase.AccessPointTable;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper;
import sg.edu.astar.i2r.sns.sensor.LocationController;
import sg.edu.astar.i2r.sns.utility.Constant;
import sg.edu.astar.i2r.sns.utility.WifiUtils;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;

public class DataCollectionService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks, 
		GooglePlayServicesClient.OnConnectionFailedListener{
	public static final String TAG = "DataCollectionService";
	
	// Unit in seconds
	private static final int EXPONENTIAL_BACKOFF_UPPER_BOUND = 30 * 60; // 30 minutes
	private static final int WALKING_DELAY = 10;						// The minimum delay (seconds) between each wifi sensor sample when the user is walking (on foot)
	private static final int MINIMAL_DELAY = WALKING_DELAY; 			// The absolute minimum delay in all cases between each wifi sensor sample
	
	private static final float REQUIRED_LOCATION_ACCURACY = 15;			// 68% (1 standard deviation) that it is within 15 meters

	private static final long ACTIVITY_UPDATE_DELAY = 20000;			// 20 seconds
	private ArrayList<String> list = new ArrayList<String>();
	private final IBinder mBinder = new MyBinder();
	
	private WifiManager wifiManager;
	private List<ScanResult> wifiList;

	private ConnectivityManager connectivityManager;
	private NetworkInfo	networkInfo;
	
	private CollectionDatabaseHelper databaseHelper;
	private Cursor cursor;
	
	private ScheduledExecutorService scheduleTaskExecutor;
	private ScheduledFuture<?> scheduledFuture;
	private boolean recordEncounterData;
	private int recordEncounterDelay;
	
	// Activity Recognition variables
	private ActivityRecognitionClient mActivityRecognitionClient;
	private PendingIntent callbackIntent;
	private Long lastActivityRecognitionUpdate;			 	// timestamp of last activity recognition update
	private int activityType;

	private BroadcastReceiver receiver;

	private Context context;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		context = getApplicationContext();
		databaseHelper = new CollectionDatabaseHelper(getApplicationContext());
		
		initialiseWifiComponents();
		initialiseContextAwareness();
		initialiseBroadcastReceiver();
		initialiseDataRecordingSchedule();
		
		// Activity recognition
		// This is needed to connect to the 
		mActivityRecognitionClient = new ActivityRecognitionClient(this, this, this);
    	mActivityRecognitionClient.connect();
	}

	/**
	 * Request for an activity recognition update from the Android system. </br>
	 * Create an instance of ActivityRecognitionService to listen to the response.
	 */
	private void requestActivityUpdate() {
		Intent intent = new Intent(this, ActivityRecognitionService.class);
		callbackIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mActivityRecognitionClient.requestActivityUpdates(ACTIVITY_UPDATE_DELAY, callbackIntent);
    	startService(intent);
	}
	
	/**
	 * The broadcast receiver listens to: </br>
	 * - Wifi network scan results. </br>
	 * This is used to record encounters/wifi sensor measurements. </p>
	 * - Network state change </br>
	 * This is used to record interactions. When ever a user successfully/unsuccessfully connects to an open network
	 * we want to record it. <p>
	 * - Activity recognition data </br>
	 * Received from the ActivityRecognitionService
	 * 
	 */
	private void initialiseBroadcastReceiver() {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent intent) {
				if (intent.getAction().compareTo(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) == 0) {
					wifiScanReceivedAction(); 
				} else if (intent.getAction().compareTo(WifiManager.NETWORK_STATE_CHANGED_ACTION) == 0) {
					networkStateChangedReceiverAction(intent);
				} else if (intent.getAction().compareTo("sg.edu.astar.i2r.sns.activity.ACTIVITY_RECOGNITION_DATA") == 0 
						&& intent.getExtras() != null) {
					activityRecognitionReceivedAction(intent);
				}
			}

			/**
			 * A network state change is detected so we want to record the relevant information.
			 * @param intent
			 */
			public void networkStateChangedReceiverAction(Intent intent) {
				NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				long timestamp = System.currentTimeMillis();
				long locationKey = Constant.INVALID;
				double accuracy = Constant.INVALID;
				int status = Constant.INVALID;
				Location location = null;
				
				// Check if it a wifi connection
				if (networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
					return;
				}
				
				// Get user's current location 
				location = LocationController.getLocation(context);
				locationKey = databaseHelper.matchOrCreateLocationRecord(location);
				if (locationKey != Constant.INVALID)
					accuracy = location.getAccuracy();
				
				recordStateInformation(intent, networkInfo, timestamp, locationKey, accuracy, status);
			}

			public void recordStateInformation(Intent intent, NetworkInfo networkInfo, long timestamp, long locationKey, double accuracy, int status) {
				long accessPointKey = Constant.INVALID; 
				String ssid = null, bssid = null;

				if (NetworkInfo.DetailedState.CONNECTED.compareTo(networkInfo.getDetailedState()) == 0){ // Wifi connected
					WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
					ssid = WifiUtils.removeQuotations(wifiInfo.getSSID());
					bssid = wifiInfo.getBSSID();
					
					Log.d(TAG, ssid + " - connected");
					accessPointKey = databaseHelper.getAccessPointPrimaryKey(ssid, bssid);
					
					// If the access point is not in the database we exit
					if (accessPointKey == Constant.INVALID) 
						return;
					
					databaseHelper.createInteractionRecord(wifiInfo, timestamp, accuracy, accessPointKey, locationKey, status);
				} else if (NetworkInfo.DetailedState.CONNECTING.compareTo(networkInfo.getDetailedState()) == 0){ // Wifi connected
			    	Log.d(TAG, "connecting");
				} else if (NetworkInfo.DetailedState.BLOCKED.compareTo(networkInfo.getDetailedState()) == 0) {
					Log.d(TAG, "blocked");
				} else if (NetworkInfo.DetailedState.FAILED.compareTo(networkInfo.getDetailedState()) == 0) {
					Log.d(TAG, "failed");
				} else if (NetworkInfo.DetailedState.DISCONNECTED.compareTo(networkInfo.getDetailedState()) == 0) {
					Log.d(TAG, "disconnected");
					//} else if (NetworkInfo.DetailedState.VERIFYING_POOR_LINK.compareTo(networkInfo.getDetailedState()) == 0) {
				}
			}

			public void activityRecognitionReceivedAction(Intent intent) {
//				Log.d(TAG, "Recieved acivity recognition broadcast");
//				String v =  "Activity: " + intent.getStringExtra("Activity") + " " + " Confidence: " + intent.getExtras().getInt("Confidence");
//				Toast.makeText(c, v, Toast.LENGTH_SHORT).show();
					
				if (intent.getStringExtra("Activity").compareTo("On Foot") == 0 && intent.getIntExtra("Confidence", Constant.INVALID) > 0.75) {
					activityType = DetectedActivity.ON_FOOT;
					long now = System.currentTimeMillis();
					
					if (lastActivityRecognitionUpdate == null || ((now - lastActivityRecognitionUpdate) > (MINIMAL_DELAY * 1000))) {
						recordEncounterDelay = WALKING_DELAY;	// Need to synchronise
						recordEncounterData = true;
						wifiManager.startScan();
					}
						
					lastActivityRecognitionUpdate = now;
				} else {
					activityType = DetectedActivity.STILL;
				}
			}

			public void wifiScanReceivedAction() {
//				Log.d(TAG, "Recieved wifi scan broadcast");
				if (recordEncounterData) {
					recordEncounterData = false;
//						Log.d(TAG, "Recording data");
					ScanResultsRecorder recordScanResults = new ScanResultsRecorder();
					recordScanResults.execute();
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction("sg.edu.astar.i2r.sns.activity.ACTIVITY_RECOGNITION_DATA");
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(receiver, filter); 		//Use LocalBroadcastManager instead
	}
	
	private void initialiseContextAwareness() {
		activityType = Constant.INVALID;
		lastActivityRecognitionUpdate = null;
	}

	private void initialiseWifiComponents() {
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	private void initialiseDataRecordingSchedule() {
		scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
		recordEncounterDelay = MINIMAL_DELAY;
		recordEncounterData = true;
		wifiManager.startScan();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class MyBinder extends Binder {
		DataCollectionService getService() {
			return DataCollectionService.this;
		}
	}

//	public long getLocationPrimaryKey(Location location) {
//		long primaryKey = Constant.INVALID;
//		
//		if (location != null) {// && location.getAccuracy() < LOCATION_ACCURACY_TOLERANCE) {
//			primaryKey = databaseHelper.matchOrCreateLocationRecord(location);
//			if (primaryKey == Constant.INVALID) {
//				Log.d(TAG, "couldn't find or create location record - Not meant to occur");
//			}
//		}
//		return primaryKey;
//	}
	
	private class ScanResultsRecorder extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			createEncounterEntries();
			recalculateSamplingInterval();
			return null;
		}

		public void recalculateSamplingInterval() {
			if (activityType == DetectedActivity.STILL) {
				recordEncounterData = false;
				performExponentialBackoff();
			} else {
				if (scheduledFuture != null) 
					scheduledFuture.cancel(true);
				recordEncounterDelay = WALKING_DELAY;
				recordEncounterData = true;
				wifiManager.startScan();
			}
		}

		// Extract and store data into database
		private void createEncounterEntries() {
			long timestamp = System.currentTimeMillis();
			long accessPointKey = Constant.INVALID;
			long encounterKey = Constant.INVALID;
			long locationKey = Constant.INVALID;
			double accuracy = Constant.INVALID;
			Location location = null;

			wifiList = wifiManager.getScanResults();
			
			// Create Location Entry
			location = LocationController.getLocation(context);
			locationKey = databaseHelper.matchOrCreateLocationRecord(location);
			if (locationKey != Constant.INVALID)
				accuracy = location.getAccuracy();

			// Create Encounter Entry
			encounterKey = databaseHelper.createEncounterRecord(timestamp, accuracy, locationKey);
			
			// Create Access Point & EncounterWithAccessPoint Entries
//			boolean allEncounteredBefore = true;
			for (ScanResult result: wifiList) {
				if (WifiUtils.isRequired(result)) {
					accessPointKey = databaseHelper.matchOrCreateAccessPointRecord(result);
					databaseHelper.createEncounterWithAccessPointRecord(encounterKey, accessPointKey, result.level);
//					if (haveNotEncountered(result)) allEncounteredBefore = false; 
				}
			}
			
//			networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//			if (networkInfo.isConnected()) {
//				recordInteraction(location.getAccuracy(), locationKey, timestamp);
//			}
		}

//		@Override
//		protected void onPostExecute(String result) {
//			// Get place information
//			try {
//				List<Address> list = geocoder.getFromLocation(testLocation.getLatitude(), testLocation.getLongitude(), 1);
//				for (Address address : list) {
//					Log.d(TAG, address.toString());
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			super.onPostExecute(result);
//		}

		private void performExponentialBackoff() {
			int seconds = (int) (0.0009 * Math.exp(recordEncounterDelay));
			if (seconds > EXPONENTIAL_BACKOFF_UPPER_BOUND) {
				recordEncounterDelay = EXPONENTIAL_BACKOFF_UPPER_BOUND; 
			} else {
				recordEncounterDelay = seconds;
			}
			
			scheduledFuture = scheduleTaskExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					recordEncounterData = true;
					wifiManager.startScan();
				}
				
			}, recordEncounterDelay, TimeUnit.SECONDS);
//			Log.d(TAG, "Exponential backoff result: " + recordEncounterDataDelay);
		}

		private boolean haveNotEncountered(ScanResult result) {
			cursor = databaseHelper.getAccessPointRecord(result.SSID, result.BSSID);
			return (cursor != null && cursor.moveToFirst()) ? false: true;
		}

		public void recordInteraction(float accuracy, long locationKey, long timestamp) {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			String bssid = wifiInfo.getBSSID();
			String ssid = wifiInfo.getSSID();
			int status = Constant.CONNECTED;
			long accessPointKey;
			
			if (ssid != null && bssid != null) {
				ssid = WifiUtils.removeQuotations(ssid);
				cursor = databaseHelper.getAccessPointRecord(ssid, bssid);
				if (cursor != null && cursor.moveToFirst()) {
					accessPointKey = cursor.getLong(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_ID));
					databaseHelper.createInteractionRecord(wifiInfo, timestamp, accuracy, accessPointKey, locationKey, status);
				} else {
					Log.d(TAG, "Can't find " + ssid + " - " + wifiInfo.getBSSID());
				}
				
				if (cursor != null) cursor.close();
			}
		}		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(receiver);
		} catch (Exception e) {
			// This is a hack since you can't check if it was it was initially registered (could use boolean flag)
		}
		
		scheduleTaskExecutor.shutdown();
		databaseHelper.close();
		
		try {
			mActivityRecognitionClient.removeActivityUpdates(callbackIntent);
		} catch (Exception e) {
			
		};
		if(mActivityRecognitionClient != null){
			mActivityRecognitionClient.removeActivityUpdates(callbackIntent);
			mActivityRecognitionClient.disconnect();
		}
	}

	private void enableEncounterRecord() {
		
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d(TAG, "Failed Connection");
	}

	@Override
	public void onConnected(Bundle arg0) {
//		Log.d(TAG, "Connected...");		
		requestActivityUpdate();
	}

	@Override
	public void onDisconnected() {
		
	}
}
