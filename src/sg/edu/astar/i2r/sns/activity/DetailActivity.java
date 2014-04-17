package sg.edu.astar.i2r.sns.activity;

import java.util.List;

import sg.edu.astar.i2r.sns.connectionserver.ReceiverInternetConnection;
import sg.edu.astar.i2r.sns.model.AccessPointContent;
import sg.edu.astar.i2r.sns.model.NearbyContent;
import sg.edu.astar.i2r.sns.psense.R;
import sg.edu.astar.i2r.sns.utility.Constant;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity provides the detailed view of an access point
 */
public class DetailActivity extends FragmentActivity {
	private static final String TAG = "DetailActivity";
	
	private TextView ssidTextView;
	private TextView distanceTextView;
	private TextView placeTextView;
	private TextView addressTextView;
	private ImageView markerImageView;
	private Button button;
	
	private ImageView ratedSpeedImageView;
	private ImageView popularityImageView;
	private ImageView loginImageView;
	private TextView ratedSpeedTextView;
	private TextView popularityTextView;
	private TextView loginTextView;

	private AccessPointContent content;
	private int type = Constant.INVALID;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	getActionBar().setDisplayHomeAsUpEnabled(false);
    	getActionBar().setHomeButtonEnabled(true);
          
    	setContentView(R.layout.activity_detail);
    	
    	ssidTextView = (TextView) findViewById(R.id.ssidTextView);
    	distanceTextView = (TextView) findViewById(R.id.distanceTextView);
    	placeTextView = (TextView) findViewById(R.id.placeTV);
    	addressTextView = (TextView) findViewById(R.id.addressTV);
    	markerImageView = (ImageView) findViewById(R.id.signalStrengthImageView);
    	button = (Button) findViewById(R.id.getDirectionButton);
    	
    	ratedSpeedImageView = (ImageView) findViewById(R.id.ratedSpeedImageView);
    	popularityImageView = (ImageView) findViewById(R.id.popularityImageView);
    	loginImageView = (ImageView) findViewById(R.id.loginImageView);
    	ratedSpeedTextView = (TextView) findViewById(R.id.ratedSpeedTextView);
    	popularityTextView = (TextView) findViewById(R.id.popularityTextView);
    	loginTextView = (TextView) findViewById(R.id.loginTextView);
    	
		Bundle extras = getIntent().getExtras();	// TODO Store into savedInstanceState during onPause()
		
		if (extras == null && savedInstanceState == null) 
			return;
		
		type = extras.getInt("type");
		content = extras.getParcelable("content");	// This contains all the access point information. 
													// It is either a visibleContent or nearbyContent
		populateActivity();
    }

	
	/**
	 * Fills the activity with the access point information
	 */
	private void populateActivity() {
		String place = content.getPlace();
		String address = content.getAddress();
		String ssid = content.getSsid();
		
		ssidTextView.setText(ssid);
		placeTextView.setText("");
		addressTextView.setText("");
		
		if (place != null)
			placeTextView.setText(place);
		
		if (address != null)
			addressTextView.setText(address);
		
		if (type == Constant.VISIBLE_TYPE) {
			button.setText("Connect");
			markerImageView.setVisibility(View.INVISIBLE);
			distanceTextView.setVisibility(View.INVISIBLE);
			
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					connectNow();
				}
			});
		} else if (type == Constant.NEARBY_TYPE) {
			final NearbyContent nearbyContent = (NearbyContent) content;
			
			Log.d(TAG, Integer.toString(nearbyContent.getDistance()));
			button.setText("Get Directions");
			distanceTextView.setText(Integer.toString(nearbyContent.getDistance()) + "m"); 
			
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					navigate(nearbyContent.getLatitude(), nearbyContent.getLongitude());
				}
			});
		} else if (type == Constant.CONNECTED_TYPE) {	// This type hasn't been implemented yet.
			button.setText("Connected");				// Currently clicking on the currently connected to network doesn't do anything.
		}
		
		// Rated speed information
		// TODO Change imageView color for fast/medium/slow
		int ratedSpeed = content.getRatedSpeed();
		switch(ratedSpeed) {
		case 0:
			ratedSpeedTextView.setVisibility(View.VISIBLE);
			ratedSpeedImageView.setVisibility(View.VISIBLE);
			ratedSpeedTextView.setText("Fast");
			break;
		case 1:
			ratedSpeedTextView.setVisibility(View.VISIBLE);
			ratedSpeedImageView.setVisibility(View.VISIBLE);
			ratedSpeedTextView.setText("Medium");
			break;
		case 2:
			ratedSpeedTextView.setVisibility(View.VISIBLE);
			ratedSpeedImageView.setVisibility(View.VISIBLE);
			ratedSpeedTextView.setText("Slow");
			break;
		default:
			ratedSpeedImageView.setVisibility(View.INVISIBLE);
			ratedSpeedTextView.setVisibility(View.INVISIBLE);
			break;
		}

		// Popularity information
		boolean popular = content.isPopular();
		if (popular) {
			popularityImageView.setVisibility(View.VISIBLE);
			popularityTextView.setVisibility(View.VISIBLE);
		} else {
			popularityImageView.setVisibility(View.INVISIBLE);
			popularityTextView.setVisibility(View.INVISIBLE);
		}

		// Login information
		boolean login = content.hasLogin();
		if (login) {
			loginImageView.setVisibility(View.VISIBLE);
			loginTextView.setVisibility(View.VISIBLE);
		} else {
			loginImageView.setVisibility(View.INVISIBLE);
			loginTextView.setVisibility(View.INVISIBLE);
		}
	}
	
	/** 
	 * Opens up GPS navigation with the access point's latitude & longitude as destination
	 * @param longitude 
	 * @param latitude 
	 */
	private void navigate(double latitude, double longitude) {
		String uri = "google.navigation:q="+ latitude + "," + longitude;
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
		intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
		startActivity(intent);
	}
	
	
	/** 
	 * Attempt to connect to a wifi network.
	 * Currently it connects based on SSID, but need to connect based on BSSID instead.
	 */
	private void connectNow() {
		String networkSSID = content.getSsid();

		Toast.makeText(this, "Connecting...", Toast.LENGTH_LONG).show();
		
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"" + networkSSID + "\"";
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
		wifiManager.addNetwork(conf);

		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
		if (list == null)
			return;
		
		for (WifiConfiguration i : list) {
//			Log.d("List", i.SSID);
		    if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
		         wifiManager.disconnect();
		         wifiManager.enableNetwork(i.networkId, true);
		         wifiManager.reconnect();               
		         break;
		    }           
		}
	}
}
;