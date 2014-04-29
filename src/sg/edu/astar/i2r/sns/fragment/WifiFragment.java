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
import android.webkit.WebView.FindListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.merge.MergeAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This is for the wifi list fragment. This class manages the visible open wifi
 * networks as well as the nearby ones.
 */
public class WifiFragment extends ListFragment {
	protected static final String TAG = "ListFragment";

	private DisplayDatabaseHelper databaseHelper;
	private CollectionDatabaseHelper dataHelper;

	private View view;
	private TextView networkTextView;

	private MergeAdapter mergeAdapter; // Refer to
										// https://github.com/commonsguy/cwac-merge
										// for more information
	private VisibleListAdapter visibleListAdapter;
	private NearbyListAdapter nearbyListAdapter;

	private Location location;

	private WifiManager wifiManager;
	private List<VisibleContent> visibleAccessPointList; // The visible wifi
															// list being
															// displayed on the
															// screen
	private List<NearbyContent> nearbyAccessPointList; // The nearby wifi list
														// being displayed on
														// the screen
	private List<String> listVisibleBSSID; // The list of visible access points

	

	private String keywordFilter; // Stores what the user types in the search
									// box

	private final BroadcastReceiver wiFiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent intent) {
			if (intent.getAction().compareTo(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) != 0)
				return;

			updateVisibleAccessPointList();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		databaseHelper = new DisplayDatabaseHelper(getActivity());
		dataHelper = new CollectionDatabaseHelper(getActivity());
		view = inflater.inflate(R.layout.fragment_wifi, container, false);
		networkTextView = (TextView) view.findViewById(R.id.networkTextView);

		wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);

		setupAdapters();
		// setHasOptionsMenu(true);

		keywordFilter = null;
		wifiManager.startScan(); // Start a wifi scan straight to get the
									// current surrounding networks
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
		headerName.setText("Neighborhood");

		visibleAccessPointList = new ArrayList<VisibleContent>();
		nearbyAccessPointList = new ArrayList<NearbyContent>();
		listVisibleBSSID = new ArrayList<String>();
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
	 * Call this to update the visible list displayed to the user
	 */
	private void updateVisibleAccessPointList() {
		List<ScanResult> scanResultList;

		scanResultList = wifiManager.getScanResults();
		if (scanResultList == null)
			return;

		listVisibleBSSID = addListVisibleBSSID(scanResultList);
		UpdateVisibleList update = new UpdateVisibleList(scanResultList);
		update.execute();
	}

	/**
	 * Worker thread to perform the work of:
	 * <p>
	 * - filtering the wifi scan results for only open networks
	 * <p>
	 * - creating a visibleContent holder for the data and merge data from
	 * database, if available
	 * <p>
	 * - selecting a single AP for each Network (unqiue SSID)
	 * <p>
	 * - filtering based on user keyword search, if any
	 * <p>
	 * - and then finally updating the list adapter and displaying it to the
	 * user
	 */
	private class UpdateVisibleList extends AsyncTask<String, Void, String> {
		List<ScanResult> validList;
		List<VisibleContent> visibleContentList;
		List<ScanResult> scanResultList;

		public UpdateVisibleList(List<ScanResult> scanResultList) {
			this.scanResultList = scanResultList;
		}

		@Override
		protected String doInBackground(String... arg0) {
			validList = WifiUtils.getValidList(scanResultList);
			visibleContentList = createVisibleContentList(validList);
			visibleAccessPointList = getFilteredVisibleAccessPointList(visibleContentList);
			
			/*for (VisibleContent content : visibleAccessPointList){
				LatLng newposition = new LatLng(53.558, 9.927);
				map.addMarker(new MarkerOptions().position(newposition).title("Hamburg"));
			}*/
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			visibleListAdapter.clear();
			visibleListAdapter.addAll(visibleAccessPointList);

		}
	}

	/**
	 * Creates a visibleContent object for the access point
	 * 
	 * @param list
	 * @return
	 */
	private List<VisibleContent> createVisibleContentList(List<ScanResult> list) {
		List<VisibleContent> visibleList = new ArrayList<VisibleContent>();

		for (ScanResult scanResult : list) {
			VisibleContent visibleContent = new VisibleContent();

			visibleContent.setSsid(scanResult.SSID);
			visibleContent.setBssid(scanResult.BSSID);
			visibleContent.setSignalLevel(scanResult.level);

			visibleList.add(visibleContent);
			// listVisibleBSSID.add(scanResult.BSSID);
		}

		visibleList = extractDatabaseValues(visibleList);

		return visibleList;
	}

	public List<String> addListVisibleBSSID(
			List<ScanResult> visibleAccessPointList2) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		for (ScanResult result : visibleAccessPointList2) {
			list.add(result.BSSID);
			// TODO Auto-generated method stub
			Logger log = new Logger();
			log.addRecordToLog("bssid from scan=" + result.BSSID);
		}
		return list;
	}

	/**
	 * Extract database data on the access point if there is any
	 * 
	 * @param visibleList
	 * @return a list of VisibleContent with database data filled in
	 */
	private List<VisibleContent> extractDatabaseValues(
			List<VisibleContent> visibleList) {

		for (VisibleContent visibleContent : visibleList) {
			int popularity = Constant.INVALID;
			int login = Constant.INVALID;
			int ratedSpeed = Constant.INVALID;
			Cursor cursor = null;
			String place = null;
			String address = null;
			String floor = null;
			String room = null;

			// cursor =
			// databaseHelper.getAccessPointInformation(visibleContent.getSsid(),
			// visibleContent.getBssid());
			cursor = dataHelper.getAccessPointInformation(
					visibleContent.getSsid(), visibleContent.getBssid());

			if (cursor == null)
				continue;

			if (!cursor.moveToFirst()) {
				cursor.close();
				continue;
			}

			popularity = 1;// cursor.getInt(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_POPULARITY));
			ratedSpeed = cursor.getInt(cursor
					.getColumnIndexOrThrow(ReportTable.COLUMN_QUALITY));
			login = cursor.getInt(cursor
					.getColumnIndexOrThrow(ReportTable.COLUMN_LOGIN));

			visibleContent.setRatedSpeed(ratedSpeed);

			if (popularity == 1) // Sqlite stores boolean values as 1 - true, 0
									// - false
				visibleContent.setPopular(true);

			if (login == 1)
				visibleContent.setLogin(true);

			place = cursor.getString(cursor
					.getColumnIndexOrThrow(ReportTable.COLUMN_NAME));
			address = cursor.getString(cursor
					.getColumnIndexOrThrow(ReportTable.COLUMN_ADDRESS));
			floor = cursor.getString(cursor
					.getColumnIndexOrThrow(ReportTable.COLUMN_FLOOR));
			room = cursor.getString(cursor
					.getColumnIndexOrThrow(ReportTable.COLUMN_ROOM));

			visibleContent.setPlace(place);
			visibleContent.setAddress(address);
			visibleContent.setFloor(floor);
			visibleContent.setRoom(room);

			cursor.close();
		}

		return visibleList;
	}

	/**
	 * Filter the visible list: </br> 1. a signal acess point will be selected
	 * for each network. So the user will see a signal access point for any
	 * network, instead of multiple access points.
	 * <p>
	 * 2. remove an access point from the list if it is already being displayed
	 * as connected.
	 * <p>
	 * 3. by the search term provided by the user.
	 * 
	 * @param list
	 *            the list of visibleContent to filter
	 * @return
	 */
	private List<VisibleContent> getFilteredVisibleAccessPointList(
			List<VisibleContent> list) {
		List<VisibleContent> filteredContentList = new ArrayList<VisibleContent>();
		List<String> acceptedSsids = new ArrayList<String>(); // Stores the
																// ssids that
																// have already
																// got an AP to
																// represent it

		if (list == null)
			return null;

		for (VisibleContent content : list) {
			String ssid = content.getSsid();
			int index;

			if (acceptedSsids.contains(ssid) || alreadyConnected(ssid))
				continue;

			// Select AP that best represents the network
			index = bestAccessPointForNetwork(list, content);

			if (Util.keywordFilter(content, keywordFilter))
				continue;

			filteredContentList.add(list.get(index));
			acceptedSsids.add(list.get(index).getSsid());

		}

		return filteredContentList;
	}

	/**
	 * Select the best AP for the network - currently based on higest signal
	 * strength
	 * 
	 * @param list
	 * @param accessPoint
	 * @return the index for a single AP that is the best among other AP with
	 *         the same SSID. Iterate through the list to find other instances
	 *         of the same SSID. Then compare all the duplicates and find the
	 *         best
	 */
	private int bestAccessPointForNetwork(List<VisibleContent> list,
			VisibleContent content) {
		String ssid = content.getSsid();
		int highestLevel = content.getSignalLevel();
		int index = list.indexOf(content);

		for (int i = list.indexOf(content) + 1; i < list.size(); i++) {
			VisibleContent item = list.get(i);
			if (item.getSsid().compareTo(ssid) != 0)
				continue;

			if (item.getSignalLevel() > highestLevel) {
				highestLevel = item.getSignalLevel();
				index = i;
			}
		}

		return index;
	}

	/**
	 * Call this to update the nearby list being displayed to the user
	 */
	public void updateNearbyAccessPointList() {
		location = LocationController.getLocation(getActivity());
		if (location == null)
			return;

		UpdateNearbyList update = new UpdateNearbyList();
		update.execute();
	}

	/**
	 * Worker thread to update the nearby list. </br> Retrieves a list of nearby
	 * access points from the database and then filters it by the user's search
	 * term, if any
	 */
	private class UpdateNearbyList extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			Cursor cursor;
			double radius = Constant.NEARBY_RADIUS;
			double currentLatitude = location.getLatitude();
			double currentLongitude = location.getLongitude();

			nearbyAccessPointList = new ArrayList<NearbyContent>();

			cursor = dataHelper.getNearbyAccessPoint(currentLatitude,
					currentLongitude, radius);

			if (cursor == null)
				return null;

			if (!cursor.moveToFirst()) {
				cursor.close();
				return null;
			}

			do {
				// ** visibleAccessPointList
				if (listVisibleBSSID.contains(cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_BSSID))))
					continue;
				// ***
				int distance;
				NearbyContent nearbyContent = new NearbyContent();
				String bssid = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_BSSID));
				String ssid = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_SSID));
				int popularity = cursor.getInt(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_ACCURACY));
				int ratedSpeed = cursor.getInt(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_QUALITY));
				int login = cursor.getInt(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_LOGIN));

				nearbyContent.setRatedSpeed(ratedSpeed);

				if (popularity == 1)
					nearbyContent.setPopular(true);

				if (login == 1)
					nearbyContent.setLogin(true);

				String place = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_NAME));
				String address = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_ADDRESS));
				String floor = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_FLOOR));
				String room = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_ROOM));

				double latitude = cursor.getDouble(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_LATITUDE));
				double longitude = cursor.getDouble(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_LONGITUDE));

				distance = LocationController.getDistance(latitude, longitude,
						getActivity());

				nearbyContent.setDistance(distance);
				nearbyContent.setLatitude(latitude);
				nearbyContent.setLongitude(longitude);
				nearbyContent.setSsid(ssid);
				nearbyContent.setPlace(place);
				nearbyContent.setAddress(address);
				nearbyContent.setFloor(floor);
				nearbyContent.setRoom(room);

				nearbyAccessPointList.add(nearbyContent);
			} while (cursor.moveToNext());

			cursor.close();

			filterNearbyList();

			return null;
		}

		/**
		 * Filter the nearby list with the user's search term
		 */
		private void filterNearbyList() {
			List<NearbyContent> filteredList = new ArrayList<NearbyContent>();

			for (NearbyContent nearbyContent : nearbyAccessPointList) {
				if (Util.keywordFilter(nearbyContent, keywordFilter))
					continue;

				filteredList.add(nearbyContent);
			}

			nearbyAccessPointList = filteredList;
		}

		@Override
		protected void onPostExecute(String result) {
			nearbyListAdapter.clear();
			nearbyListAdapter.addAll(nearbyAccessPointList);
		}
	}

	/**
	 * Worker thread to get data from server. </br>
	 */
	private class RequestToServer extends AsyncTask<String, Void, String> {

		String keyword = "";
		String latitude = "";
		String longitude = "";

		public RequestToServer(String searchWorld) {
			this.keyword = searchWorld;
		}

		public RequestToServer(String latitude, String longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

		@Override
		protected String doInBackground(String... arg0) {
			Log.d("RequestToServer", "Do in background");

			ConnectivityManager cm = (ConnectivityManager) getActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm == null)
				return null;
			if (cm.getActiveNetworkInfo() != null
					&& cm.getActiveNetworkInfo().isConnected()
					&& (keyword.equals(""))) {
				// / connected
				// send Http request with keyword
				// save in database
				String url = "http://54.255.147.139/api/v1/reports";
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				/*
				 * if (!keyword.equals("")) { nameValuePairs.add(new
				 * BasicNameValuePair("name", keyword)); nameValuePairs.add(new
				 * BasicNameValuePair("address", keyword)); }
				 */
				/*
				 * if (!latitude.equals("")) { nameValuePairs.add(new
				 * BasicNameValuePair("latitude", latitude));
				 * nameValuePairs.add(new BasicNameValuePair("longitude",
				 * longitude)); }
				 */
				// nameValuePairs.add(new BasicNameValuePair("address",
				// keyword));
				HttpClient httpClient = new DefaultHttpClient();
				String paramsString = URLEncodedUtils.format(nameValuePairs,
						"UTF-8");
				HttpGet httpGet = new HttpGet(url + "?" + paramsString);

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
							List<RecordsContent> list = null;

							// jsonObject.getJSONArray(access_points);
							list = fulfillList(list, jsonObject);
							// visibleAccessPointList = list;
							saveData(list);

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
			} /*
			 * else { // request database Cursor cursor = null ; if
			 * (!keyword.equals("")) cursor = dataHelper.getReports(keyword); if
			 * (cursor == null) return null; List<VisibleContent> list = new
			 * ArrayList<VisibleContent>(); if (cursor.moveToFirst()) { int
			 * nbRows = cursor.getCount(); for (int i=0; i< nbRows; i++) {
			 * VisibleContent vContent = new VisibleContent(); String id =
			 * cursor
			 * .getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_ID));
			 * String v1ssid =
			 * cursor.getString(cursor.getColumnIndexOrThrow(ReportTable
			 * .COLUMN_SSID)); String v1bssid =
			 * cursor.getString(cursor.getColumnIndexOrThrow
			 * (ReportTable.COLUMN_BSSID)); String v1quality =
			 * cursor.getString(cursor
			 * .getColumnIndexOrThrow(ReportTable.COLUMN_QUALITY)); String
			 * v1place =
			 * cursor.getString(cursor.getColumnIndexOrThrow(ReportTable
			 * .COLUMN_NAME)); String v1address =
			 * cursor.getString(cursor.getColumnIndexOrThrow
			 * (ReportTable.COLUMN_ADDRESS)); String v1floor =
			 * cursor.getString(cursor
			 * .getColumnIndexOrThrow(ReportTable.COLUMN_FLOOR)); String v1room
			 * =
			 * cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_ROOM
			 * )); String login =
			 * cursor.getString(cursor.getColumnIndexOrThrow(ReportTable
			 * .COLUMN_LOGIN));
			 * 
			 * vContent.setAddress(v1address);v1quality="1";
			 * vContent.setRatedSpeed(Integer.parseInt(v1quality));
			 * vContent.setBssid(v1bssid); vContent.setSsid(v1ssid);
			 * //vContent.setLogin(login == "1" ? true : false);
			 * vContent.setLogin(login == "1" ? true : false);
			 * list.add(vContent); cursor.moveToNext(); } cursor.close(); }
			 * visibleAccessPointList = list; }
			 */

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			visibleListAdapter.clear();
			visibleListAdapter.addAll(visibleAccessPointList);
		}
	}

	public void saveData(List<RecordsContent> listContent) {
		for (int i = 0; i < listContent.size(); i++) {

			dataHelper
					.saveData(listContent.get(i).getBssid(), listContent.get(i)
							.getSsid(),
							listContent.get(i).getRatedSpeed() + "",
							listContent.get(i).getLatitude(), listContent
									.get(i).getLongitude(), listContent.get(i)
									.getAccuracy(), listContent.get(i)
									.getSsid(),
							listContent.get(i).getAddress(), listContent.get(i)
									.getFloor(), listContent.get(i).getRoom(),
							listContent.get(i).getLogin());
		}

	}

	public List<RecordsContent> fulfillList(List<RecordsContent> list,
			JSONObject jsonObject) {
		list = new ArrayList<RecordsContent>();
		RecordsContent vContent;
		JSONArray jsonArray;
		try {
			jsonArray = jsonObject.getJSONArray("reports");
			for (int i = 0; i < jsonArray.length(); i++) {
				vContent = new RecordsContent();
				String rating = jsonArray.getJSONObject(i).getString("rating");
				String aaccessPoint = jsonArray.getJSONObject(i).getString(
						"access_point");
				JSONObject jsonAP = new JSONObject(aaccessPoint);
				String ssid = jsonAP.getString("network_name");
				String bssid = jsonAP.getString("bssid");
				boolean login_required = jsonAP.getBoolean("login_required");

				String place = jsonArray.getJSONObject(i).getString("place");
				JSONObject jsonPlace = new JSONObject(place);
				String name = jsonPlace.getString("name");
				vContent.setName(name);
				String address = jsonPlace.getString("address");
				String lat = jsonPlace.getString("latitude");
				vContent.setLatitude(lat);
				String longi = jsonPlace.getString("longitude");
				vContent.setLongitude(longi);

				vContent.setFloor(address);
				vContent.setRoom(address);
				vContent.setAddress(address);
				vContent.setRatedSpeed(Integer.parseInt(rating));
				vContent.setBssid(bssid);
				vContent.setSsid(ssid);
				vContent.setLogin(login_required);
				list.add(vContent);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Check to see if the user is connected to this network
	 * 
	 * @param ssid
	 *            the network name to compare agaisnt
	 * @return true if it is already connect. False otherwise.
	 */
	private boolean alreadyConnected(String ssid) {
		String currentSsid = WifiUtils.getCurrentSsid(getActivity());

		if (currentSsid == null)
			return false;

		if (currentSsid.compareTo(ssid) == 0)
			return true;

		return false;
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			getActivity().unregisterReceiver(wiFiReceiver);
		} catch (Exception e) {
			// This is a hack since you can't check if it was registered
			// Could use a flag to keep track though
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// Update what is being displayed as connected to
		String connectedToSsid = WifiUtils.getCurrentSsid(getActivity());
		if (connectedToSsid != null)
			networkTextView.setText(connectedToSsid);

		// unfilter the visible and nearby list with no search term
		keywordFilter = null;
		updateAccessPointLists();

		// listen for wifi scan results
		getActivity().registerReceiver(wiFiReceiver,
				new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifiManager.startScan();
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		ArrayList<Integer> array = getCorrectedPosition(mergeAdapter, position);
		int selectedSection = array.get(Constant.SECTION);
		int sectionPosition = array.get(Constant.POSITION);

		Intent i = new Intent(getActivity(), DetailActivity.class);
		AccessPointContent content = null;

		if (selectedSection == Constant.VISIBLE_LIST_SECTION) {
			content = visibleAccessPointList.get(sectionPosition);
			i.putExtra("type", Constant.VISIBLE_TYPE);
		} else if (selectedSection == Constant.NEARBY_LIST_SECTION) {
			content = nearbyAccessPointList.get(sectionPosition);
			i.putExtra("type", Constant.NEARBY_TYPE);
		}

		if (content == null)
			return;

		i.putExtra("content", content);
		startActivity(i);
	}

	/**
	 * Obtain the position relative in the list. Treats each section as a
	 * seperate list. This position is then used to access either the
	 * visibleAccessPointList or nearbyAccessPointList
	 * 
	 * @param mergeAdapter
	 * @param position
	 *            the absolute position inside the merged list
	 * @return the relative position within the section it is clicked
	 */
	private ArrayList<Integer> getCorrectedPosition(MergeAdapter mergeAdapter,
			int position) {
		int correctedPosition = Constant.INVALID;
		int sectionStart = Constant.INVALID;
		int section = Constant.INVALID;

		for (int i = 0; i <= position; i++) {
			if (mergeAdapter.getItem(i) instanceof View) {
				sectionStart = i;
				section++;
			}
		}

		correctedPosition = position - sectionStart - 1;

		ArrayList<Integer> array = new ArrayList<Integer>();
		array.add(section);
		array.add(correctedPosition);

		return array;
	}

	/*
	 * @Override public void onCreateOptionsMenu(Menu menu, MenuInflater
	 * inflater) { MenuItem searchItem = (MenuItem)
	 * menu.findItem(R.id.mainSearch); final SearchView searchView =
	 * (SearchView) searchItem.getActionView();
	 * searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
	 * 
	 * @Override public boolean onQueryTextSubmit(String string) { if
	 * (!string.isEmpty()) { keywordFilter = string; RequestToServer request =
	 * new RequestToServer(keywordFilter); request.execute(); //
	 * getInfoFromServer(keyword); //updateAccessPointLists(); }
	 * 
	 * InputMethodManager imm = (InputMethodManager)
	 * getActivity().getSystemService(MainActivity.INPUT_METHOD_SERVICE);
	 * imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
	 * 
	 * return true; }
	 * 
	 * // This method gets called on each page swipe as well. // So might have
	 * to setup a delay before calling updateAccessPointLists - to reduce the
	 * laggy appearance // Or figure out how to disable this for page swipes
	 * 
	 * @Override public boolean onQueryTextChange(String string) { if
	 * (string.isEmpty()) { keywordFilter = null; updateAccessPointLists(); }
	 * else { searchView.setIconified(false); }
	 * 
	 * return true; } }); }
	 */

	/**
	 * Call this to update both visible and nearby list
	 */
	public void updateAccessPointLists() {
		// updataDatabaseFromServer

		Logger log = new Logger();
		location = LocationController.getLocation(getActivity());
		if (location == null)
			return;
		RequestToServer request = new RequestToServer(location.getLatitude()
				+ "", location.getLongitude() + "");
		request.execute();
		// ****
		updateVisibleAccessPointList();
		updateNearbyAccessPointList();
	}
}
