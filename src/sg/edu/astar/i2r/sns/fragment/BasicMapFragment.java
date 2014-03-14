package sg.edu.astar.i2r.sns.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.edu.astar.i2r.sns.activity.DetailActivity;
import sg.edu.astar.i2r.sns.activity.MainActivity;
import sg.edu.astar.i2r.sns.displaydatabase.AccessPointTable;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper;
import sg.edu.astar.i2r.sns.displaydatabase.LocationTable;
import sg.edu.astar.i2r.sns.displaydatabase.PlaceTable;
import sg.edu.astar.i2r.sns.model.NearbyContent;
import sg.edu.astar.i2r.sns.psense.R;
import sg.edu.astar.i2r.sns.sensor.LocationController;
import sg.edu.astar.i2r.sns.utility.Constant;
import sg.edu.astar.i2r.sns.utility.Util;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This class represents the Map screen displayed to the user.
 * It's main purpose is to populate the map with access point markers.
 */
public class BasicMapFragment extends Fragment 
						implements LocationListener, OnMarkerClickListener {

	private static final String TAG = "BasicMapFragment";
	private DisplayDatabaseHelper databaseHelper;

	private HashMap<Marker, NearbyContent> extraMarkerInfo;	// used to retrieve the access point content for a particular marker 
	private List<NearbyContent> nearbyAccessPointList;	// The list of currently displayed access points
	private GoogleMap map;
	
	private String keywordFilter;	// This stores what the user types in the search bar when they submit a search
	private Location location;
	
	private boolean isNetworkEnabled = false;
	private boolean isGPSEnabled = false;

	private static View view;
	private Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();

		extraMarkerInfo = new HashMap<Marker, NearbyContent>();
		databaseHelper = new DisplayDatabaseHelper(context);

		// If a map view already exists, remove it first
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		
		view = inflater.inflate(R.layout.fragment_map, container, false);

		setHasOptionsMenu(true);	// Set this to indicate this fragment has additional menu options implementation
		initialiseMap();			// and hence onCreateOptionsMenu() gets called
		
		return view;
	}

	/**
	 * Attempt to load the map
	 */
	public void initialiseMap() {
        if (map == null)
            map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
 
        if (map == null) {
            Toast.makeText(getActivity(), "Sorry, unable to load map", Toast.LENGTH_SHORT).show();
            return;
        }
    	
        // TODO check if location services is on, if not then inform user and redirect to settings page
        
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnMarkerClickListener(this);
        
        // Once the user click on the info window, send the access point information
        // to the detail activity and start it
    	map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
        		Intent i = new Intent(getActivity(), DetailActivity.class);
        		i.putExtra("content", extraMarkerInfo.get(marker));
    			i.putExtra("type", Constant.NEARBY_TYPE);
    			
    			startActivity(i);
            }
        });
    	
    	initialiseMapSettings();
    }
	
	private void initialiseMapSettings() {
		map.setMyLocationEnabled(true); // Disable when location services not availabe & show dialog to settings
	
		// Try to move the camera to where the user's current location
		location = LocationController.getLocation(getActivity());
		if (location == null) 
			return;
		
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
		map.moveCamera(cameraUpdate);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		keywordFilter = null;
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onLocationChanged(Location location) {}

	@Override
	public void onProviderDisabled(String provider) {
//		Toast.makeText(getActivity(), "Disabled new provider " + provider,
//		        Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onProviderEnabled(String provider) {
//		 Toast.makeText(getActivity(), "Enabled new provider " + provider,
//			        Toast.LENGTH_SHORT).show();	
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Setting up the search bar
		MenuItem searchItem = (MenuItem) menu.findItem(R.id.mainSearch);
	    final SearchView searchView = (SearchView) searchItem.getActionView(); 
	    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

	    	@Override
	    	public boolean onQueryTextSubmit(String string) {
	    		if (!string.isEmpty()) {
	    			keywordFilter = string;
	    			updateNearbyAccessPointList();
	    		}

	    		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(MainActivity.INPUT_METHOD_SERVICE);
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
	    			updateNearbyAccessPointList();
	    		}

	    		return true;
	    	}
	    });
	}
	
	/**
	 * Gets all the access points within a certain range of the user,
	 * and then updates the list of markers displayed on the map.
	 */
	public void updateNearbyAccessPointList() {
		location = LocationController.getLocation(getActivity());
		
		if (location == null)
			return;

		map.clear();
		UpdateNearbyList update = new UpdateNearbyList();
		update.execute();
	}
	
	private class UpdateNearbyList extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			Cursor cursor;
			double radius = Constant.NEARBY_RADIUS;
			double currentLatitude = location.getLatitude();
			double currentLongitude = location.getLongitude();
			
			nearbyAccessPointList = new ArrayList<NearbyContent>();
			extraMarkerInfo.clear();

			cursor = databaseHelper.getNearbyAccessPoint(currentLatitude, currentLongitude, radius);

			if (cursor == null)
				return null;
			
			if (!cursor.moveToFirst()) {
				cursor.close();
				return null;
			}
			
			// Store access point information retrieved from the database into nearbyContent
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
		 * Filter the markers based on the search term provided by the user
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
			if (map == null)
				return;
			
			populateMap();
		}

		public void populateMap() {
			for (NearbyContent content: nearbyAccessPointList) {
				double latitude, longitude;
				String ssid;
				
				ssid = content.getSsid();
				latitude = content.getLatitude();
				longitude = content.getLongitude();
				
				Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
						.title(ssid)
                        .snippet(content.getPlace() + " - " + content.getAddress())
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
				
				// Maps the access point contents with a marker
				extraMarkerInfo.put(marker, content);
			}
		}
	}
}
