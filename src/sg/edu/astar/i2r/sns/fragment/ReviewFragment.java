package sg.edu.astar.i2r.sns.fragment;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.astar.i2r.sns.activity.PlaceActivity;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper;
import sg.edu.astar.i2r.sns.connectionserver.ReceiverInternetConnection;
import sg.edu.astar.i2r.sns.psense.R;
import sg.edu.astar.i2r.sns.sensor.LocationController;
import sg.edu.astar.i2r.sns.utility.WifiUtils;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ReviewFragment extends Fragment {
    private static final int PICK_PLACE_REQUEST = 0;

	@SuppressWarnings("unused")
	private static final String TAG = "ReviewFragment";

    private CollectionDatabaseHelper databaseHelper;

    private View view;
    private Button submitButton;
	private Button suggestionButton;
	private EditText placeEditText;
	private EditText addressEditText;
	private EditText floorEditText;
	private EditText roomEditText;
	private int quality=1;
	private TextView currentNetworkTextView;
	private Location location;
	private TelephonyManager telephonyMgr;
	private int nbRatings=0;
	private String	login = "false";
	
	private WifiManager wifiManager;
	private WifiInfo currentNetwork;
	private ToggleButton  toggleButtonSlow,toggleButtonAverage,toggleButtonFast;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_review, container, false);
//		Log.d(TAG, "onCreateView");

		databaseHelper = new CollectionDatabaseHelper(getActivity());
		wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		
		currentNetworkTextView = (TextView) view.findViewById(R.id.currentNetworkTextView);
		placeEditText = (EditText) view.findViewById(R.id.placeEditText);
		addressEditText = (EditText) view.findViewById(R.id.addressEditText);
		floorEditText = (EditText) view.findViewById(R.id.floorEditText);
		roomEditText = (EditText) view.findViewById(R.id.roomEditText);
		
		toggleButtonSlow = (ToggleButton) view.findViewById(R.id.slowToggleButton);
		toggleButtonAverage = (ToggleButton) view.findViewById(R.id.averageToggleButton);
		toggleButtonFast = (ToggleButton) view.findViewById(R.id.fastToggleButton);

		toggleButtonSlow.setOnCheckedChangeListener(changeChecker);
		toggleButtonAverage.setOnCheckedChangeListener(changeChecker);
		toggleButtonFast.setOnCheckedChangeListener(changeChecker);
		
		location = LocationController.getLocation(getActivity());
		telephonyMgr=(TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		
		submitButton = (Button) view.findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String place, address="Fusionopolis", floor, room;
				int speed = 0;
				
				floor = floorEditText.getText().toString();
				address = addressEditText.getText().toString();
				place = placeEditText.getText().toString();
				room = roomEditText.getText().toString();
				
				
				String ssid = WifiUtils.removeQuotations(currentNetwork.getSSID());
				String bssid = currentNetwork.getBSSID();
				Long timestamp = System.currentTimeMillis();
				
				// Check if currently connected to a network
				if (currentNetwork == null || bssid == null) {
					Toast.makeText(getActivity(), "Not currently connected to a network", Toast.LENGTH_LONG).show();
					return;
				}
				/*// save into database
				String login = ReceiverInternetConnection.listBssid.get(currentNetwork.getBSSID());
				if (login == null)*/
			/*	String	login = "false";
				if (WifiUtils.login_web_required())
					login = "true";
				else
					login = "false";*/
			//	boolean login_required = login.equals("false") ? false : true;
				databaseHelper.saveReport((new Date().getTime()), quality+"", ""+Long.parseLong(telephonyMgr.getDeviceId()),
						currentNetwork.getBSSID(), ssid, login, ssid, address,location.getLatitude()+"",
						location.getLongitude()+"",location.getAccuracy()+"",ssid);
				LoginWeb loginweb = new LoginWeb(ssid,address);
				loginweb.execute();
				/*databaseHelper.saveReport((new Date().getTime()), quality+"", ""+Long.parseLong(telephonyMgr.getDeviceId()),
						currentNetwork.getBSSID(), ssid, login, ssid, address,location.getLatitude()+"",
						location.getLongitude()+"",location.getAccuracy()+"",ssid);*/
				/// check if the AP required a login 
				//Toast.makeText(getActivity(), "Login required = "+ReceiverInternetConnection.listBssid.get(WifiUtils.getCurrentSsid(getActivity())), Toast.LENGTH_SHORT).show();
				RequestToServer request = new RequestToServer(timestamp,ssid,address,place+" "+floor+" "+room);
    			request.execute();
				 
			}
		});

		suggestionButton = (Button) view.findViewById(R.id.suggestionButton);
		suggestionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), PlaceActivity.class);
				startActivityForResult(i, PICK_PLACE_REQUEST);
			}
		});
		
		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (data == null || requestCode != PICK_PLACE_REQUEST) return;
		
		String address = data.getStringExtra("address");
		String place = data.getStringExtra("name");
		
		addressEditText.setText(address);
		placeEditText.setText(place);
	}
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.rating, menu);
	}
	 public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
			switch(item.getItemId()){
			
			case R.id.settings:
			 Toast.makeText(getActivity(), "Number of network reviewed = "+nbRatings, Toast.LENGTH_LONG).show();
				break;
			
			}
			
			return true;
		}

	@Override
	public void onResume() {
		super.onResume();
		
		// Updates the textview showing what network the user is connected to.
		// Should actually update more frequently - listen for network state change and update
		// for every new connection instead
		currentNetwork = wifiManager.getConnectionInfo();
		
		if (!currentNetwork.getSSID().equals(""))
			currentNetworkTextView.setText(WifiUtils.removeQuotations(currentNetwork.getSSID()));
	}
	OnCheckedChangeListener changeChecker = new OnCheckedChangeListener() {

	    @Override
	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	        if (isChecked){
	            if (buttonView == toggleButtonSlow) {
	                toggleButtonAverage.setChecked(false);
	                toggleButtonFast.setChecked(false);
	                quality=1;
	            }
	            if (buttonView == toggleButtonAverage) {
	            	toggleButtonSlow.setChecked(false);
	            	toggleButtonFast.setChecked(false);
	            	quality=2;
	            }
	            if (buttonView == toggleButtonFast) {
	            	toggleButtonSlow.setChecked(false);
	                toggleButtonAverage.setChecked(false);
	                quality=3;
	            }
	        }
	    }
	};
	
	/*
	 * 
	 * 
	 *
	 */
	private class RequestToServer extends AsyncTask<String, Void, String> {

		String ssid;
		String address;
		String name;
		HttpResponse response;
		public RequestToServer(Long timestampp,String ssidd,String addresss,String namee) {
			
			this.ssid =ssidd;
			this.address = addresss;
			this.name = namee;
		}	
		@Override
		protected String doInBackground(String... arg0) {
			Log.d("RequestToServer","Do in background");
			String url = "http://54.255.147.139/api/v1/reports";
			JSONObject json=new JSONObject();
			JSONObject jsonReport=new JSONObject();
			
			 try {
			
			json.put("reported_at", ""+(new Date().getTime()));
			json.put("rating", quality);
			json.put("deviceID", Long.parseLong(telephonyMgr.getDeviceId()));
			
			JSONObject ap= new JSONObject();
			ap.put("bssid", currentNetwork.getBSSID());
			ap.put("network_name", ssid);
			
			ap.put("login_required", login);
			json.put("access_point", ap);
			JSONObject placeJson = new JSONObject();
			placeJson.put("name",name);
			placeJson.put("address", address);
			placeJson.put("latitude", location.getLatitude());
			placeJson.put("longitude", location.getLongitude());
			placeJson.put("location_granularity", location.getAccuracy());
			json.put("place",placeJson);
			jsonReport.put("report",json);
			
					
			
			HttpClient httpClient = new DefaultHttpClient();
		    HttpPost httpost = new HttpPost(url);
		  //sets a request header so the page receving the request
		    //will know what to do with it
		   httpost.setHeader("Accept", "application/json");
		   httpost.setHeader("Content-type", "application/json");
			 //passes the results to a string builder/entity
		    StringEntity se = new StringEntity(jsonReport.toString(),"UTF-8");
		   
		    httpost.setEntity(se);  
		  	  
		    response = httpClient.execute(httpost);
		    
				 
		
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			 if  (response.getStatusLine().getStatusCode() == 200) {
				 Toast.makeText(getActivity(), "Review submitted", Toast.LENGTH_LONG).show();
				 nbRatings++;
			 }
		}
	}
	
	private class LoginWeb extends AsyncTask<String, Void, String> {

		String ssid;
		String address;
	public LoginWeb(String ssidd,String addresss) {
			
			this.ssid = ssidd;
			this.address = addresss;
			
		}	
		@Override
		protected String doInBackground(String... arg0) {
			 URL url =null;
			 HttpURLConnection urlConnection = null;
			  try {
			   url = new URL("http://www.android.com/");
			   urlConnection = (HttpURLConnection) url.openConnection();
			  /* URL u = urlConnection.getURL();
			   String s = u.getHost();
			   String a =s;*/
			     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			   Map<String,List<String>> map = urlConnection.getHeaderFields();
			   if (map.toString().contains("HTTP Appgw")) {
				   login="true";
			   } else {
				   login ="false";
			   }
			     if (!url.getHost().equals(urlConnection.getURL().getHost())) {
			       // we were redirected! Kick the user out to the browser to sign on?\
			    	 //s = u.getHost();
			    //	 login="true";
			    	 }
			   } catch (IOException e) {
				   
			   } finally {
			     urlConnection.disconnect();
			   }
			  
			 // login="false";
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			
		}
	}


}
