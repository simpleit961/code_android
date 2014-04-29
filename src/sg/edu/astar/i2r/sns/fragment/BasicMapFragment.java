package sg.edu.astar.i2r.sns.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.astar.i2r.sns.activity.DetailActivity;
import sg.edu.astar.i2r.sns.activity.MainActivity;
import sg.edu.astar.i2r.sns.adaptor.NearbyListAdapter;
import sg.edu.astar.i2r.sns.adaptor.VisibleListAdapter;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper;
import sg.edu.astar.i2r.sns.collectiondatabase.ReportTable;
import sg.edu.astar.i2r.sns.contentprovider.CollectionContentProvider;
import sg.edu.astar.i2r.sns.displaydatabase.AccessPointTable;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper;
import sg.edu.astar.i2r.sns.displaydatabase.LocationTable;
import sg.edu.astar.i2r.sns.displaydatabase.PlaceTable;
import sg.edu.astar.i2r.sns.model.AccessPointContent;
import sg.edu.astar.i2r.sns.model.NearbyContent;
import sg.edu.astar.i2r.sns.model.RecordsContent;
import sg.edu.astar.i2r.sns.model.VisibleContent;
import sg.edu.astar.i2r.sns.psense.R;
import sg.edu.astar.i2r.sns.sensor.LocationController;
import sg.edu.astar.i2r.sns.utility.Constant;
import sg.edu.astar.i2r.sns.utility.Logger;
import sg.edu.astar.i2r.sns.utility.Util;
import sg.edu.astar.i2r.sns.utility.WifiUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.merge.MergeAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This is for the wifi list fragment. This class manages the visible open wifi
 * networks as well as the nearby ones.
 */
public class BasicMapFragment extends ListFragment {
	protected static final String TAG = "ListFragment";

	private DisplayDatabaseHelper databaseHelper;
	private CollectionDatabaseHelper dataHelper;

	private View view;
	private TextView networkTextView;
	private TextView infoTextView;

	private MergeAdapter mergeAdapter; // Refer to
										// https://github.com/commonsguy/cwac-merge
										// for more information
	private VisibleListAdapter visibleListAdapter;
	private NearbyListAdapter nearbyListAdapter;

	private WifiManager wifiManager;
	private List<VisibleContent> visibleAccessPointList; // The visible wifi
															// list being
															// displayed on the
															// screen
	private List<NearbyContent> nearbyAccessPointList; // The nearby wifi list
														// being displayed on
														// the screen

	private String keywordFilter; // Stores what the user types in the search
									// box

	// gagogg:start
	// add google map
	private GoogleMap map;
	static final LatLng SINGAPORE = new LatLng(1.3, 103.8);
	//1.3000° N, 103.8000° E

	static final LatLng KIEL = new LatLng(53.551, 9.993);
	private SupportMapFragment mMapFragment;
	
	// gagogg:end

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		databaseHelper = new DisplayDatabaseHelper(getActivity());
		dataHelper = new CollectionDatabaseHelper(getActivity());
		view = inflater.inflate(R.layout.fragment_search, container, false);
		// networkTextView = (TextView) view.findViewById(R.id.networkTextView);
		infoTextView = (TextView) view.findViewById(R.id.textView);
		// networkTextView.setText("APs From Server");
		infoTextView.setText("HOTSPOTS FROM SERVER");
		wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		
		map = ((MapFragment) getActivity().getFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		
		/*Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
				.title("Hamburg"));
		Marker kiel = map
				.addMarker(new MarkerOptions()
						.position(KIEL)
						.title("Kiel")
						.snippet("Kiel is cool")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_wifi)));*/
		

		// Move the camera instantly to hamburg with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(SINGAPORE, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		setupAdapters();
		setHasOptionsMenu(true);

		SearchView searchItem = (SearchView) view
				.findViewById(R.id.search_view);
		final SearchView searchView = searchItem;
		searchView.setQueryHint("Trip planninp : search AP");
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String string) {
				if (!string.isEmpty()) {
					keywordFilter = string;
					visibleAccessPointList.clear();
					updateAccessPointLists();
				}

				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(MainActivity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

				return true;
			}

			// This is called even for tab swipes.
			@Override
			public boolean onQueryTextChange(String string) {
				Log.d(TAG, "onQueryTextChange");
				if (string.isEmpty()) {
					keywordFilter = null;
					// Setup a delay for page swipes TODO

					// updateAccessPointLists();
				}

				return true;
			}
		});

		keywordFilter = null;

		return view;
	}

	/**
	 * Set up the visible list and nearby list adapters. These two adapters are
	 * passed into the merged adapter which combines the two list so that it
	 * becomes one scrollable list.
	 */
	private void setupAdapters() {
		View visibleHeaderView = getActivity().getLayoutInflater().inflate(
				R.layout.list_header, null, false);
		View nearbyHeaderView = getActivity().getLayoutInflater().inflate(
				R.layout.list_header, null, false);
		mergeAdapter = new MergeAdapter();

		TextView headerName = (TextView) nearbyHeaderView
				.findViewById(R.id.headerTextView);
		headerName.setText("Nearby");
		headerName = (TextView) visibleHeaderView
				.findViewById(R.id.headerTextView);
		if (keywordFilter == null)
			headerName.setText("List access points ");
		else
			headerName.setText("Visible from " + keywordFilter);

		visibleAccessPointList = new ArrayList<VisibleContent>();
		nearbyAccessPointList = new ArrayList<NearbyContent>();
		initialiseAdapters();

		mergeAdapter.addView(visibleHeaderView);
		mergeAdapter.addAdapter(visibleListAdapter);
		mergeAdapter.addView(nearbyHeaderView);
		mergeAdapter.addAdapter(nearbyListAdapter);

		setListAdapter(mergeAdapter);
	}

	private void initialiseAdapters() {
		visibleListAdapter = new VisibleListAdapter(getActivity(),
				R.layout.row_visible, new ArrayList<VisibleContent>());
		nearbyListAdapter = new NearbyListAdapter(getActivity(),
				R.layout.row_nearby, new ArrayList<NearbyContent>());
	}

	/**
	 * Worker thread to get data from server. </br>
	 */
	private class RequestToServer extends AsyncTask<String, Void, String> {

		String keyword = "";

		public RequestToServer(String searchWorld) {
			this.keyword = searchWorld;
		}

		@Override
		protected String doInBackground(String... arg0) {
			Log.d("RequestToServer", "Do in background");

			ConnectivityManager cm = (ConnectivityManager) getActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm == null)
				return null;
			if (cm.getActiveNetworkInfo() != null
					&& cm.getActiveNetworkInfo().isConnected()) {
				// / connected
				// send Http request with keyword
				// save in database
				String url = "http://54.255.147.139/api/v1/reports";
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				if (!keyword.equals("")) {
					nameValuePairs.add(new BasicNameValuePair("name", keyword));
					nameValuePairs.add(new BasicNameValuePair("address",
							keyword));
				}

				// nameValuePairs.add(new BasicNameValuePair("address",
				// keyword));
				HttpClient httpClient = new DefaultHttpClient();
				String paramsString = URLEncodedUtils.format(nameValuePairs,
						"UTF-8");
				HttpGet httpGet = new HttpGet(url + "?" + paramsString);

				Log.i("PSENSE::BasicmapFragment", url + "?" + paramsString);

				try {
					HttpResponse response = httpClient.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(response.getEntity()
										.getContent(), "UTF-8"));
						String json = reader.readLine();
						// Instantiate a JSON object from the request response
						try {
							JSONObject jsonObject = new JSONObject(json);
							JSONArray jsonArray;
							VisibleContent vContent;
							try {
								jsonArray = jsonObject.getJSONArray("reports");

								Log.i("PSENSE::BasicMapFragment",
										"length of result array"
												+ jsonArray.length());

								for (int i = 0; i < jsonArray.length(); i++) {
									vContent = new VisibleContent();
									String rating = jsonArray.getJSONObject(i)
											.getString("rating");
									String aaccessPoint = jsonArray
											.getJSONObject(i).getString(
													"access_point");
									JSONObject jsonAP = new JSONObject(
											aaccessPoint);
									String ssid = jsonAP
											.getString("network_name");
									String bssid = jsonAP.getString("bssid");
									boolean login_required = jsonAP
											.getBoolean("login_required");

									String place = jsonArray.getJSONObject(i)
											.getString("place");
									JSONObject jsonPlace = new JSONObject(place);

									String address = jsonPlace
											.getString("address");

									// "latitude":"1.428223","longitude":"103.7211"
									double latitude = jsonPlace
											.getDouble("latitude");
									double longitude = jsonPlace
											.getDouble("longitude");

									Log.i("PSENSE::BasicMapFragment", latitude
											+ "::::" + longitude);

									vContent.setFloor(address);
									vContent.setRoom(address);
									vContent.setAddress(address);
									vContent.setRatedSpeed(Integer
											.parseInt(rating));
									vContent.setBssid(bssid);
									vContent.setSsid(ssid);
									vContent.setLogin(login_required);
									vContent.setlatlng(new LatLng(latitude,
											longitude));

									visibleAccessPointList.add(vContent);
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// add jsonObject to visibleAccessPointList
							// do the same for nearbyList : fulfill the list
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//
			} else {
				// request database
				Cursor cursor = null;
				if (keyword == null)
					keyword = "%";
				cursor = dataHelper.getReports(keyword);
				if (cursor == null)
					return null;
				List<VisibleContent> list = new ArrayList<VisibleContent>();
				if (cursor.moveToFirst()) {
					int nbRows = cursor.getCount();
					for (int i = 0; i < nbRows; i++) {
						VisibleContent vContent = new VisibleContent();
						String id = cursor.getString(cursor
								.getColumnIndexOrThrow(ReportTable.COLUMN_ID));
						String v1ssid = cursor
								.getString(cursor
										.getColumnIndexOrThrow(ReportTable.COLUMN_SSID));
						String v1bssid = cursor
								.getString(cursor
										.getColumnIndexOrThrow(ReportTable.COLUMN_BSSID));
						String v1quality = cursor
								.getString(cursor
										.getColumnIndexOrThrow(ReportTable.COLUMN_QUALITY));
						String v1place = cursor
								.getString(cursor
										.getColumnIndexOrThrow(ReportTable.COLUMN_NAME));
						String v1address = cursor
								.getString(cursor
										.getColumnIndexOrThrow(ReportTable.COLUMN_ADDRESS));
						String v1floor = cursor
								.getString(cursor
										.getColumnIndexOrThrow(ReportTable.COLUMN_FLOOR));
						String v1room = cursor
								.getString(cursor
										.getColumnIndexOrThrow(ReportTable.COLUMN_ROOM));
						String login = cursor
								.getString(cursor
										.getColumnIndexOrThrow(ReportTable.COLUMN_LOGIN));

						vContent.setAddress(v1address);
						v1quality = "1";
						vContent.setRatedSpeed(Integer.parseInt(v1quality));
						vContent.setBssid(v1bssid);
						vContent.setSsid(v1ssid);
						// vContent.setLogin(login == "1" ? true : false);
						vContent.setLogin(login == "1" ? true : false);
						list.add(vContent);
						cursor.moveToNext();
					}
					cursor.close();
				}
				visibleAccessPointList = list;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			visibleListAdapter.clear();
			visibleListAdapter.addAll(visibleAccessPointList);
			nearbyListAdapter.clear();
			nearbyListAdapter.addAll(nearbyAccessPointList);
			//gagogg: start
			//add some infomation to map 
			Log.i("PSENSE::BasicMapFragment", "BEFORE UPDATE" + visibleAccessPointList.size());
			for(VisibleContent visibleContent : visibleAccessPointList) {
				
				
				//LatLng newPosition = new LatLng(visibleContent.getlatlng());
				/*Marker hamburg = map.addMarker(new MarkerOptions().position(visibleContent.getlatlng())
						.title(visibleContent.getBssid()));*/
				
				Marker kiel = map
						.addMarker(new MarkerOptions()
								.position(visibleContent.getlatlng())
								.title(visibleContent.getSsid())
								//.snippet("Kiel is cool")
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.ic_wifi)));
			}
			
			// Move the camera instantly to hamburg with a zoom of 5.
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(visibleAccessPointList.get(1).getlatlng(), 10));
			//gagogg:end 
		}
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.main, menu);

		MenuItem searchItem = (MenuItem) menu.findItem(R.id.mainSearch);
		final SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setQueryHint("Trip planninp : search AP");
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String string) {
				if (!string.isEmpty()) {
					keywordFilter = string;
					visibleAccessPointList.clear();
					updateAccessPointLists();
				}

				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(MainActivity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

				return true;
			}

			// This is called even for tab swipes.
			@Override
			public boolean onQueryTextChange(String string) {
				Log.d(TAG, "onQueryTextChange");
				if (string.isEmpty()) {
					keywordFilter = null;
					// Setup a delay for page swipes TODO

					// updateAccessPointLists();
				}

				return true;
			}
		});
	}

	/**
	 * Call this to update both visible and nearby list
	 */
	public void updateAccessPointLists() {
		// updataDatabaseFromServer
		RequestToServer request = new RequestToServer(keywordFilter);
		request.execute();

		/*if(visibleAccessPointList != null){
			Log.i("PSENSE::BasicMapFragment", "Size of AccessPointList" + visibleAccessPointList.size());
			for(VisibleContent visibleContent: visibleAccessPointList) {
				
			}
		}*/
	}
}
