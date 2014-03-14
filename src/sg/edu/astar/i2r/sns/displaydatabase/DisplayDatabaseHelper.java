package sg.edu.astar.i2r.sns.displaydatabase;

import sg.edu.astar.i2r.sns.contentprovider.DisplayContentProvider;
import sg.edu.astar.i2r.sns.utility.Util;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class DisplayDatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DisplayDatabaseHelper";
	private static final String DATABASE_NAME = "Display.db";
	private static final int DATABASE_VERSION = 1;
	private Context context;
	
	public interface DisplayTables {
		// Entity Tables
        public static final String ACCESS_POINT = "Access_point";
        public static final String LOCATION = "Location";
        public static final String PLACE = "Place";
        public static final String MEASURED_SPEED = "Measured_speed";
        
        // Relationship Tables
		public static final String ACCESS_POINT_CONTAINS_LOCATION = "Access_point_contains_Location";
		
		// Joined Table - These tables are created here
		// and then accessed & defined with a Uri in the database helper class
		public static final String ACCESS_POINT_JOIN_PLACE = "Access_point a "
				+ "LEFT OUTER JOIN Place p ON (a.place = p.place_id)";
		
		public static final String JOIN_ALL = "Access_point_contains_Location c " 
				+ "JOIN Access_point a ON (c.access_point = a._id) "
				+ "JOIN Location l ON (c.location = l.location_id) "
				+ "LEFT OUTER JOIN Place p ON (a.place = p.place_id)"; 
	}
	
	public DisplayDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(TAG, "database created");
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		AccessPointContainsLocationTable.onCreate(db);
		AccessPointTable.onCreate(db);
		LocationTable.onCreate(db);
		PlaceTable.onCreate(db);
		
//		***************** TEST DATA **********************
		ContentValues contentValues = new ContentValues();
		contentValues.put(PlaceTable.COLUMN_ADDRESS, "2 Clarke Quay Road, Singapore");
		contentValues.put(PlaceTable.COLUMN_NAME, "Starbucks");
		db.insert(DisplayTables.PLACE, null, contentValues);
		
		contentValues = new ContentValues();
		contentValues.put(PlaceTable.COLUMN_ADDRESS, "4 Dover Road, Yew Tee");
		contentValues.put(PlaceTable.COLUMN_NAME, "Post Office");
		db.insert(DisplayTables.PLACE, null, contentValues);

		contentValues = new ContentValues();
		contentValues.put(PlaceTable.COLUMN_ADDRESS, "1 Fusionopolis Way, Singapore 123");
		contentValues.put(PlaceTable.COLUMN_NAME, "A*Star I2R");
		db.insert(DisplayTables.PLACE, null, contentValues);

		contentValues = new ContentValues();
		contentValues.put(AccessPointTable.COLUMN_SSID, "Starbucks Wireless");
		contentValues.put(AccessPointTable.COLUMN_BSSID, "00:1f:9e:7f:2b:20");
		contentValues.put(AccessPointTable.COLUMN_PLACE, 1);
		contentValues.put(AccessPointTable.COLUMN_RATED_SPEED, 0);
		contentValues.put(AccessPointTable.COLUMN_POPULARITY, 1);
		contentValues.put(AccessPointTable.COLUMN_LOGIN, 1);
		db.insert(DisplayTables.ACCESS_POINT, null, contentValues);

		contentValues = new ContentValues();
		contentValues.put(AccessPointTable.COLUMN_SSID, "SG Post Office Wireless");
		contentValues.put(AccessPointTable.COLUMN_BSSID, "00:1b:9e:7f:2b:20");
		contentValues.put(AccessPointTable.COLUMN_PLACE, 2);
		contentValues.put(AccessPointTable.COLUMN_RATED_SPEED, 2);
		contentValues.put(AccessPointTable.COLUMN_POPULARITY, 0);
		contentValues.put(AccessPointTable.COLUMN_LOGIN, 1);
		db.insert(DisplayTables.ACCESS_POINT, null, contentValues);

		contentValues = new ContentValues();
		contentValues.put(AccessPointTable.COLUMN_SSID, "FP-GUEST");
		contentValues.put(AccessPointTable.COLUMN_BSSID, "00:1f:9e:7f:2b:2f");
		contentValues.put(AccessPointTable.COLUMN_PLACE, 3);
		contentValues.put(AccessPointTable.COLUMN_RATED_SPEED, 0);
		contentValues.put(AccessPointTable.COLUMN_POPULARITY, 1);
		contentValues.put(AccessPointTable.COLUMN_LOGIN, 1);
		db.insert(DisplayTables.ACCESS_POINT, null, contentValues);
		
		contentValues = new ContentValues();
		contentValues.put(LocationTable.COLUMN_LATITUDE, 1.2989152);
		contentValues.put(LocationTable.COLUMN_LONGITUDE, 103.7874528);
		db.insert(DisplayTables.LOCATION, null, contentValues);
		contentValues = new ContentValues();

		contentValues.put(LocationTable.COLUMN_LATITUDE, 1.2990352);
		contentValues.put(LocationTable.COLUMN_LONGITUDE, 103.7874528);
		db.insert(DisplayTables.LOCATION, null, contentValues);
		
		contentValues = new ContentValues();
		contentValues.put(AccessPointContainsLocationTable.COLUMN_ACCESS_POINT, 1);
		contentValues.put(AccessPointContainsLocationTable.COLUMN_LOCATION, 1);
		db.insert(DisplayTables.ACCESS_POINT_CONTAINS_LOCATION, null, contentValues);
		contentValues = new ContentValues();
		
		contentValues.put(AccessPointContainsLocationTable.COLUMN_ACCESS_POINT, 2);
		contentValues.put(AccessPointContainsLocationTable.COLUMN_LOCATION, 2);
		db.insert(DisplayTables.ACCESS_POINT_CONTAINS_LOCATION, null, contentValues);
//		************************ MAY DELETE ***************************
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		AccessPointContainsLocationTable.onUpgrade(db, oldVersion, newVersion);
		AccessPointTable.onUpgrade(db, oldVersion, newVersion);
		LocationTable.onUpgrade(db, oldVersion, newVersion);
		PlaceTable.onUpgrade(db, oldVersion, newVersion);
	}

	
	/**
	 * @param SSID
	 * @param BSSID
	 * @return A cursor pointing to a table with all the necessary information to display to the user
	 */
	public Cursor getAccessPointInformation(String ssid, String bssid) {
		Cursor cursor = null;
		
		Uri uri = DisplayContentProvider.CONTENT_URI_ACCESS_POINT_JOIN_PLACE;
		
		String selection = AccessPointTable.COLUMN_SSID + "=?"; //AND " + AccessPointTable.COLUMN_BSSID + "=?";
//		Log.d(TAG, ssid);
//		Log.d(TAG, bssid);
		String[] selectionArgs = {ssid};//, bssid};
		String[] projection = {
				AccessPointTable.COLUMN_ID, AccessPointTable.COLUMN_SSID,
				AccessPointTable.COLUMN_BSSID, AccessPointTable.COLUMN_POPULARITY,
				AccessPointTable.COLUMN_RATED_SPEED, AccessPointTable.COLUMN_LOGIN,
				PlaceTable.COLUMN_NAME, PlaceTable.COLUMN_ADDRESS, 
				PlaceTable.COLUMN_FLOOR, PlaceTable.COLUMN_ROOM};
		
		cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
		
		return cursor;
	}

	/**
	 * Get a list of nearby access points. </br>
	 * Currently it gets all the access points within a box area, not a circle area. 
	 * It roughly tries to calculate the change in degrees for each unit change in distance.
	 * <p>
	 * 
	 * Refer to these links: </br>
	 * https://developer.appcelerator.com/question/143813/search-based-on-geo-pointslatitude-longitude </br>
	 * http://stackoverflow.com/questions/15258078/latitude-longitude-and-meters
	 * <p>
	 * 
	 * Will need to implement the Haversine formula to properly get a circle radius of access points.
	 * 
	 * <p>
	 * @param currentLatitude the latitude to search from
	 * @param currentLongitude the longitude to search from
	 * @param radius the distance in km to search out
	 * @return a cursor pointing to a table with the result
	 */
	public Cursor getNearbyAccessPoint(double currentLatitude, double currentLongitude, double radius) {
		Cursor cursor = null;
		double latDistance = Util.getLatitudeDistance(radius);
		double lonDistance = Util.getLongitudeDistance(radius, currentLatitude);
		
		double lat_min = currentLatitude - latDistance;
		double lat_max = currentLatitude + latDistance;
		double lon_min = currentLongitude - lonDistance;
		double lon_max = currentLongitude + lonDistance;

		Uri uri = DisplayContentProvider.CONTENT_URI_JOIN_ALL;
		String[] projection = {AccessPointTable.COLUMN_SSID, AccessPointTable.COLUMN_BSSID, 
				AccessPointTable.COLUMN_POPULARITY, AccessPointTable.COLUMN_RATED_SPEED, AccessPointTable.COLUMN_LOGIN,
				PlaceTable.COLUMN_NAME, PlaceTable.COLUMN_ADDRESS, PlaceTable.COLUMN_FLOOR, PlaceTable.COLUMN_ROOM,
				LocationTable.COLUMN_LATITUDE, LocationTable.COLUMN_LONGITUDE};
		
		String selection = LocationTable.COLUMN_LATITUDE + " >= ? AND " + LocationTable.COLUMN_LATITUDE + " <= ? AND " +
							LocationTable.COLUMN_LONGITUDE + " >= ? AND " + LocationTable.COLUMN_LONGITUDE + " <= ?";
		String[] selectionArgs = {Double.toString(lat_min), Double.toString(lat_max), Double.toString(lon_min), Double.toString(lon_max)};
		
		cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

		return cursor;
	}

}

