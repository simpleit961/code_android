package sg.edu.astar.i2r.sns.fragment;

import java.util.ArrayList;
import java.util.List;

import sg.edu.astar.i2r.sns.activity.DetailActivity;
import sg.edu.astar.i2r.sns.activity.MainActivity;
import sg.edu.astar.i2r.sns.adaptor.NearbyListAdapter;
import sg.edu.astar.i2r.sns.adaptor.VisibleListAdapter;
import sg.edu.astar.i2r.sns.displaydatabase.AccessPointTable;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper;
import sg.edu.astar.i2r.sns.displaydatabase.LocationTable;
import sg.edu.astar.i2r.sns.displaydatabase.PlaceTable;
import sg.edu.astar.i2r.sns.model.AccessPointContent;
import sg.edu.astar.i2r.sns.model.NearbyContent;
import sg.edu.astar.i2r.sns.model.VisibleContent;
import sg.edu.astar.i2r.sns.psense.R;
import sg.edu.astar.i2r.sns.sensor.LocationController;
import sg.edu.astar.i2r.sns.utility.Constant;
import sg.edu.astar.i2r.sns.utility.Util;
import sg.edu.astar.i2r.sns.utility.WifiUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

import com.commonsware.cwac.merge.MergeAdapter;

/**
 * This is for the wifi list fragment.
 * This class manages the visible open wifi networks as well as the nearby ones.
 */
public class WifiFragment extends ListFragment {
	protected static final String TAG = "ListFragment";

	private DisplayDatabaseHelper databaseHelper;
	
	private View view;
	private TextView networkTextView;
	
	private MergeAdapter mergeAdapter;				// Refer to https://github.com/commonsguy/cwac-merge for more information
	private VisibleListAdapter visibleListAdapter;
	private NearbyListAdapter nearbyListAdapter;
	
	private Location location;

	private WifiManager wifiManager;			
	private List<VisibleContent> visibleAccessPointList;	// The visible wifi list being displayed on the screen
	private List<NearbyContent> nearbyAccessPointList;		// The nearby wifi list being displayed on the screen
	
	private String keywordFilter;	// Stores what the user types in the search box
	
	private final BroadcastReceiver wiFiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent intent) {
			if (intent.getAction().compareTo(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) != 0) 
				return;
			
			updateVisibleAccessPointList();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		databaseHelper = new DisplayDatabaseHelper(getActivity());
		view = inflater.inflate(R.layout.fragment_wifi, container, false);
		networkTextView = (TextView) view.findViewById(R.id.networkTextView);
		
		wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

		setupAdapters();
		setHasOptionsMenu(true);
		
		keywordFilter = null;
		wifiManager.startScan();	// Start a wifi scan straight to get the current surrounding networks

		return view;
	}

	/**
	 * Set up the visible list and nearby list adapters.
	 * These two adapters are passed into the merged adapter which
	 * combines the two list so that it becomes one scrollable list.
	 */
	private void setupAdapters() {
		View visibleHeaderView = getActivity().getLayoutInflater().inflate(R.layout.list_header, null, false);
		View nearbyHeaderView = getActivity().getLayoutInflater().inflate(R.layout.list_header, null, false);
		mergeAdapter = new MergeAdapter();
		
		TextView headerName = (TextView) nearbyHeaderView.findViewById(R.id.headerTextView);
		headerName.setText("Nearby");
		
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
		visibleListAdapter = new VisibleListAdapter(getActivity(), R.layout.row_visible, new ArrayList<VisibleContent>());
		nearbyListAdapter = new NearbyListAdapter(getActivity(), R.layout.row_nearby, new ArrayList<NearbyContent>());
	}
	
	/**
	 * Call this to update the visible list displayed to the user
	 */
	private void updateVisibleAccessPointList() {
		List<ScanResult> scanResultList;
		
		scanResultList = wifiManager.getScanResults();
		if (scanResultList == null)
			return;
		
		UpdateVisibleList update = new UpdateVisibleList(scanResultList);
		update.execute();
	}

	/**
	 * Worker thread to perform the work of:
	 * <p> - filtering the wifi scan results for only open networks 
	 * <p> - creating a visibleContent holder for the data and merge data from database, if available 
	 * <p> - selecting a single AP for each Network (unqiue SSID)
	 * <p> - filtering based on user keyword search, if any
	 * <p> - and then finally updating the list adapter and displaying it to the user
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
	 * @param list
	 * @return
	 */
	private List<VisibleContent> createVisibleContentList(List<ScanResult> list) {
		List<VisibleContent> visibleList = new ArrayList<VisibleContent>();
		
		for (ScanResult scanResult: list) {
			VisibleContent visibleContent = new VisibleContent();

			visibleContent.setSsid(scanResult.SSID);
			visibleContent.setBssid(scanResult.BSSID);
			visibleContent.setSignalLevel(scanResult.level);
			
			visibleList.add(visibleContent);
		}
		
		visibleList = extractDatabaseValues(visibleList);
		
		return visibleList;
	}

	/**
	 * Extract database data on the access point if there is any
	 * @param visibleList
	 * @return a list of VisibleContent with database data filled in
	 */
	private List<VisibleContent> extractDatabaseValues(List<VisibleContent> visibleList) {
		
		for (VisibleContent visibleContent: visibleList) {
			int popularity = Constant.INVALID;
			int login = Constant.INVALID;
			int ratedSpeed = Constant.INVALID;
			Cursor cursor = null;
			String place = null;
			String address = null;
			String floor = null;
			String room = null;

			cursor = databaseHelper.getAccessPointInformation(visibleContent.getSsid(), visibleContent.getBssid());

			if (cursor == null)
				continue;
			
			if (!cursor.moveToFirst()) {
				cursor.close();
				continue;
			}
			
			popularity = cursor.getInt(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_POPULARITY));
			ratedSpeed = cursor.getInt(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_RATED_SPEED));
			login = cursor.getInt(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_LOGIN));

			visibleContent.setRatedSpeed(ratedSpeed);
			
			if (popularity == 1)	// Sqlite stores boolean values as 1 - true, 0 - false
				visibleContent.setPopular(true);
			
			if (login == 1) 
				visibleContent.setLogin(true);
			
			place = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_NAME));
			address = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_ADDRESS));
			floor = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_FLOOR));
			room = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_ROOM));
			
			visibleContent.setPlace(place);
			visibleContent.setAddress(address);
			visibleContent.setFloor(floor);
			visibleContent.setRoom(room);
			
			cursor.close();
		}
		
		return visibleList;
	}

	/**
	 * Filter the visible list: </br>
	 * 1. a signal acess point will be selected for each network.
	 * So the user will see a signal access point for any network, instead of multiple access points. <p>
	 * 2. remove an access point from the list if it is already being displayed as connected. <p>
	 * 3. by the search term provided by the user.
	 * @param list the list of visibleContent to filter
	 * @return
	 */
	private List<VisibleContent> getFilteredVisibleAccessPointList(List<VisibleContent> list) {
		List<VisibleContent> filteredContentList = new ArrayList<VisibleContent>();
		List<String> acceptedSsids = new ArrayList<String>();	// Stores the ssids that have already got an AP to represent it
		
		if (list == null)
			return null;
		
		for (VisibleContent content: list) {
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
	 * Select the best AP for the network - currently based on higest signal strength
	 * @param list
	 * @param accessPoint
	 * @return the index for a single AP that is the best among other AP with the same SSID.
	 * Iterate through the list to find other instances of the same SSID. Then compare all the duplicates and find the best
	 */
	private int bestAccessPointForNetwork(List<VisibleContent> list, VisibleContent content) {
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
	 * Worker thread to update the nearby list. </br>
	 * Retrieves a list of nearby access points from the database and then filters it by the user's search term, if any
	 */
	private class UpdateNearbyList extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			Cursor cursor;
			double radius = Constant.NEARBY_RADIUS;
			double currentLatitude = location.getLatitude();
			double currentLongitude = location.getLongitude();
			
			nearbyAccessPointList = new ArrayList<NearbyContent>();
			
			cursor = databaseHelper.getNearbyAccessPoint(currentLatitude, currentLongitude, radius);

			if (cursor == null)
				return null;
			
			if (!cursor.moveToFirst()) {
				cursor.close();
				return null;
			}
			
			do {
				int distance;
				NearbyContent nearbyContent = new NearbyContent();
				
				String ssid = cursor.getString(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_SSID));
				int popularity = cursor.getInt(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_POPULARITY));
				int ratedSpeed = cursor.getInt(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_RATED_SPEED));
				int login = cursor.getInt(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_LOGIN));

				nearbyContent.setRatedSpeed(ratedSpeed);
				
				if (popularity == 1)
					nearbyContent.setPopular(true);
				
				if (login == 1) 
					nearbyContent.setLogin(true);
				
				String place = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_NAME));
				String address = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_ADDRESS));
				String floor = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_FLOOR));
				String room = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_ROOM));

				double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE));
				double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE));
				
				distance = LocationController.getDistance(latitude, longitude, getActivity());
				
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
	 * Check to see if the user is connected to this network
	 * @param ssid the network name to compare agaisnt
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
		getActivity().registerReceiver(wiFiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifiManager.startScan();
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
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
	 * Obtain the position relative in the list. Treats each section as a seperate list.
	 * This position is then used to access either the visibleAccessPointList or nearbyAccessPointList 
	 * @param mergeAdapter
	 * @param position the absolute position inside the merged list
	 * @return the relative position within the section it is clicked
	 */
	private ArrayList<Integer> getCorrectedPosition(MergeAdapter mergeAdapter, int position){
		int correctedPosition = Constant.INVALID;
		int sectionStart = Constant.INVALID;
		int section = Constant.INVALID;
		
		for (int i = 0; i <= position; i++) {
			if(mergeAdapter.getItem(i) instanceof View){
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem searchItem = (MenuItem) menu.findItem(R.id.mainSearch);
	    final SearchView searchView = (SearchView) searchItem.getActionView(); 
	    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

	    	@Override
	    	public boolean onQueryTextSubmit(String string) {
	    		if (!string.isEmpty()) {
	    			keywordFilter = string;
	    			updateAccessPointLists();
	    		}

	    		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(MainActivity.INPUT_METHOD_SERVICE);
	    		imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

	    		return true;
	    	}

	    	// This method gets called on each page swipe as well.
			// So might have to setup a delay before calling updateAccessPointLists - to reduce the laggy appearance
	    	// Or figure out how to disable this for page swipes
	    	@Override
	    	public boolean onQueryTextChange(String string) {
	    		if (string.isEmpty()) {
	    			keywordFilter = null;
	    			updateAccessPointLists();
	    		} else {
	    			searchView.setIconified(false);
	    		}

	    		return true;
	    	}
	    });
	}
	
	/**
	 * Call this to update both visible and nearby list
	 */
	public void updateAccessPointLists() {
		updateVisibleAccessPointList();
		updateNearbyAccessPointList();
	}
}
