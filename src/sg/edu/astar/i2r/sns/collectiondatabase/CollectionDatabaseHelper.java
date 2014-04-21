package sg.edu.astar.i2r.sns.collectiondatabase;

import java.util.Date;

import sg.edu.astar.i2r.sns.contentprovider.CollectionContentProvider;
import sg.edu.astar.i2r.sns.contentprovider.DisplayContentProvider;
import sg.edu.astar.i2r.sns.displaydatabase.AccessPointTable;
import sg.edu.astar.i2r.sns.displaydatabase.LocationTable;
import sg.edu.astar.i2r.sns.displaydatabase.PlaceTable;
import sg.edu.astar.i2r.sns.utility.Constant;
import sg.edu.astar.i2r.sns.utility.Logger;
import sg.edu.astar.i2r.sns.utility.Util;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.util.Log;
import android.widget.Toast;

public class CollectionDatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "CollectionDatabaseHelper";
	private static final String DATABASE_NAME = "Collection.db";
	private static final int DATABASE_VERSION = 7;
	private Context context;

	// This is used for referring to table names in the collection database
	public interface CollectionTables {
		// Entity Tables
		public static final String ACCESS_POINT = "Access_point";
		public static final String ENCOUNTER = "Encounter";
		public static final String INTERACTION = "Interaction";
		public static final String LOCATION = "Location";
		public static final String PLACE = "Place";
		public static final String REPORT = "Report";
		public static final String TEMPREPORT = "TempReport";
		public static final String USER = "User";

		// Relationship Tables
		public static final String USER_RATES_ACCESS_POINT = "User_rates_Access_point";
		public static final String ENCOUNTER_WITH_ACCESS_POINT = "Encounter_with_Access_point";
		public static final String ACCESS_POINT_TAGGED_PLACE = "Access_point_tagged_Place";

		// Joined Table - These tables are created here
		// and then accessed & defined with a Uri in the database helper class
		public static final String ACCESS_POINT_JOIN_PLACE = "Access_point a "
				+ "LEFT OUTER JOIN Place p ON (a.place = p.place_id)";
	}

	public CollectionDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		EncounterWithAccessPointTable.onCreate(db);
		AccessPointTable.onCreate(db);
		InteractionTable.onCreate(db);
		EncounterTable.onCreate(db);
		LocationTable.onCreate(db);
		PlaceTable.onCreate(db);
		ReportTable.onCreate(db);
		TempReportTable.onCreate(db);
		UserTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		EncounterWithAccessPointTable.onUpgrade(db, oldVersion, newVersion);
		AccessPointTable.onUpgrade(db, oldVersion, newVersion);
		InteractionTable.onUpgrade(db, oldVersion, newVersion);
		EncounterTable.onUpgrade(db, oldVersion, newVersion);
		LocationTable.onUpgrade(db, oldVersion, newVersion);
		PlaceTable.onUpgrade(db, oldVersion, newVersion);
		ReportTable.onUpgrade(db, oldVersion, newVersion);
		TempReportTable.onUpgrade(db, oldVersion, newVersion);
		UserTable.onUpgrade(db, oldVersion, newVersion);
	}

	/**
	 * Search the database for a specific access point entry. If it doesn't
	 * exist, it gets created.
	 * 
	 * @param result
	 *            the specific access point to search for
	 * @return the primary key for an access point record
	 */
	public long matchOrCreateAccessPointRecord(ScanResult result) {
		ContentValues accessPointValues = new ContentValues();
		long primaryKey = Constant.INVALID;
		Cursor cursor = null;

		// Check if the Access Point record already exists
		cursor = getAccessPointRecord(result.SSID, result.BSSID);

		if (cursor == null)
			return Constant.INVALID;

		if (cursor.moveToFirst()) {
			primaryKey = cursor.getLong(cursor
					.getColumnIndexOrThrow(AccessPointTable.COLUMN_ID));
			cursor.close();
			return primaryKey;
		}

		// Access Point record doesn't exist, create it
		accessPointValues.put(AccessPointTable.COLUMN_SSID, result.SSID);
		accessPointValues.put(AccessPointTable.COLUMN_BSSID, result.BSSID);

		primaryKey = createRecord(accessPointValues,
				CollectionContentProvider.CONTENT_URI_ACCESS_POINT,
				AccessPointTable.COLUMN_ID);

		return primaryKey;
	}

	/**
	 * Gets the record of an access point
	 * 
	 * @param ssid
	 * @param bssid
	 * @return a cursor pointing to the access point record, or null
	 */
	public Cursor getAccessPointRecord(String ssid, String bssid) {
		Cursor cursor;

		Uri uri = CollectionContentProvider.CONTENT_URI_ACCESS_POINT;
		String selection = AccessPointTable.COLUMN_SSID + "=? AND "
				+ AccessPointTable.COLUMN_BSSID + "=?";

		String[] selectionArgs = { ssid, bssid };
		String[] projection = { AccessPointTable.COLUMN_ID };
		cursor = context.getContentResolver().query(uri, projection, selection,
				selectionArgs, null);

		return cursor;
	}

	/**
	 * Gets the access point primary key in the database
	 * 
	 * @param ssid
	 * @param bssid
	 * @return primary key, or -1
	 */
	public long getAccessPointPrimaryKey(String ssid, String bssid) {
		long accessPointPrimaryKey = Constant.INVALID;
		Cursor cursor = null;

		cursor = getAccessPointRecord(ssid, bssid);
		if (cursor == null)
			return Constant.INVALID;

		if (!cursor.moveToFirst()) { // If there's no AP record
			cursor.close();
			return Constant.INVALID;
		}

		accessPointPrimaryKey = cursor.getInt(cursor
				.getColumnIndexOrThrow(AccessPointTable.COLUMN_ID));

		cursor.close();

		return accessPointPrimaryKey;
	}

	/**
	 * Search for a location record and if it doesn't exist it gets created.
	 * 
	 * @param location
	 * @return the primary key of the location record
	 */
	public long matchOrCreateLocationRecord(Location location) {
		ContentValues locationValues = new ContentValues();
		String[] projection = { LocationTable.COLUMN_ID };
		long primaryKey = Constant.INVALID;
		Cursor cursor = null;

		if (location == null)
			return Constant.INVALID;

		// Check if the Location record already exists
		Uri uri = CollectionContentProvider.CONTENT_URI_LOCATION;
		String selection = LocationTable.COLUMN_LATITUDE + "=? AND "
				+ LocationTable.COLUMN_LONGITUDE + "=?";

		String[] selectionArgs = { Double.toString(location.getLatitude()),
				Double.toString(location.getLongitude()) };
		cursor = context.getContentResolver().query(uri, projection, selection,
				selectionArgs, null);

		if (cursor == null)
			return Constant.INVALID;

		if (cursor.moveToFirst()) {
			primaryKey = cursor.getLong(cursor
					.getColumnIndexOrThrow(LocationTable.COLUMN_ID));
			cursor.close();
			return primaryKey;
		}

		// Location record doesn't exist, create it
		locationValues.put(LocationTable.COLUMN_LATITUDE,
				location.getLatitude());
		locationValues.put(LocationTable.COLUMN_LONGITUDE,
				location.getLongitude());

		primaryKey = createRecord(locationValues,
				CollectionContentProvider.CONTENT_URI_LOCATION,
				LocationTable.COLUMN_ID);

		return primaryKey;
	}

	/**
	 * Create a record and return the primary key
	 * 
	 * @param values
	 * @param projection
	 * @param tableUri
	 * @param columnId
	 * @return the primary key of the newly created record
	 */
	public long createRecord(ContentValues values, Uri tableUri, String columnId) {
		String[] projection = { columnId };
		long primaryKey;
		Cursor cursor = null;

		Uri recordUri = context.getContentResolver().insert(tableUri, values);
		Uri completeUri = Uri.withAppendedPath(
				CollectionContentProvider.AUTHORITY_URI, recordUri.toString());

		// Get the primary key value from the newly created record
		cursor = context.getContentResolver().query(completeUri, projection,
				null, null, null);

		if (cursor == null)
			return Constant.INVALID;

		if (!cursor.moveToFirst()) {
			cursor.close();
			return Constant.INVALID;
		}

		primaryKey = cursor.getLong(cursor.getColumnIndexOrThrow(columnId));

		cursor.close();
		return primaryKey;
	}

	public boolean createRecordReport(ContentValues values, Uri tableUri,
			String columnId) {
		context.getContentResolver().insert(tableUri, values);
		return true;
	}

	public boolean updateRecordReport(ContentValues values, Uri tableUri,
			String selection, String[] selectionArgs) {
		context.getContentResolver().update(tableUri, values, selection,
				selectionArgs);
		return true;
	}

	/**
	 * Creates an encounter record in the database
	 * 
	 * @param timestamp
	 * @param accuracy
	 * @param locationKey
	 * @return the primary key of the newly created record
	 */
	public long createEncounterRecord(long timestamp, double accuracy,
			long locationKey) {
		ContentValues encounterValues = new ContentValues();
		long primaryKey = Constant.INVALID;

		encounterValues.put(EncounterTable.COLUMN_TIMESTAMP, timestamp);

		// If the location information is valid
		if (accuracy != Constant.INVALID && locationKey != Constant.INVALID) {
			encounterValues.put(EncounterTable.COLUMN_ACCURACY, accuracy);
			encounterValues.put(EncounterTable.COLUMN_LOCATION, locationKey);
		}

		primaryKey = createRecord(encounterValues,
				CollectionContentProvider.CONTENT_URI_ENCOUNTER,
				EncounterTable.COLUMN_ID);

		return primaryKey;
	}

	/**
	 * Create an interaction record in the database
	 * 
	 * @param wifiInfo
	 * @param timestamp
	 * @param accuracy
	 * @param accessPointKey
	 * @param locationKey
	 * @param status
	 */
	public void createInteractionRecord(WifiInfo wifiInfo, long timestamp,
			double accuracy, long accessPointKey, long locationKey, int status) {
		ContentValues interactionValues = new ContentValues();

		if (locationKey != Constant.INVALID) {
			interactionValues
					.put(InteractionTable.COLUMN_LOCATION, locationKey);
			interactionValues.put(InteractionTable.COLUMN_ACCURACY, accuracy);
		}

		if (accessPointKey == Constant.INVALID) {
			Log.d(TAG, "Access point key is invaid = Should not occur");
			return;
		}

		interactionValues.put(InteractionTable.COLUMN_STATUS, status);
		interactionValues.put(InteractionTable.COLUMN_WITH, accessPointKey);
		interactionValues.put(InteractionTable.COLUMN_RSSI, wifiInfo.getRssi());
		interactionValues.put(InteractionTable.COLUMN_TIMESTAMP, timestamp);
		interactionValues.put(InteractionTable.COLUMN_LINK_SPEED,
				wifiInfo.getLinkSpeed());

		context.getContentResolver().insert(
				CollectionContentProvider.CONTENT_URI_INTERACTION,
				interactionValues);
	}

	/**
	 * Record an entry for the relationship between encounter and an access
	 * point
	 * 
	 * @param encounterKey
	 * @param accessPointKey
	 * @param level
	 */
	public void createEncounterWithAccessPointRecord(long encounterKey,
			long accessPointKey, int level) {
		ContentValues encounterWithAccessPointValues = new ContentValues();

		if (encounterKey == Constant.INVALID
				|| accessPointKey == Constant.INVALID) {
			Log.d(TAG, "key is invalid - Should not occur");
			return;
		}

		encounterWithAccessPointValues.put(
				EncounterWithAccessPointTable.COLUMN_ENCOUNTER, encounterKey);
		encounterWithAccessPointValues.put(
				EncounterWithAccessPointTable.COLUMN_ACCESS_POINT,
				accessPointKey);
		encounterWithAccessPointValues.put(
				EncounterWithAccessPointTable.COLUMN_SIGNAL_LEVEL, level);

		context.getContentResolver()
				.insert(CollectionContentProvider.CONTENT_URI_ENCOUNTER_WITH_ACCESS_POINT,
						encounterWithAccessPointValues);
	}

	/**
	 * Update the access point entry in the database with a place association
	 * 
	 * @param timestamp
	 * @param ssid
	 * @param bssid
	 * @param speed
	 *            measured speed of the network
	 * @param name
	 * @param address
	 * @param floor
	 * @param room
	 */
	public void tagAccessPointWithPlace(Long timestamp, String ssid,
			String bssid, String name, String address, String floor, String room) {
		ContentValues accessPointValues = new ContentValues();
		ContentValues placeValues = new ContentValues();
		String[] projection = { PlaceTable.COLUMN_ID };
		long accessPointPrimaryKey = Constant.INVALID;
		long placePrimaryKey = Constant.INVALID;
		Cursor cursor;

		// Check if the Place record already exists
		Uri uri = CollectionContentProvider.CONTENT_URI_PLACE;
		String selection = PlaceTable.COLUMN_NAME + "=? AND "
				+ PlaceTable.COLUMN_ADDRESS + "=? AND "
				+ PlaceTable.COLUMN_FLOOR + "=? AND " + PlaceTable.COLUMN_ROOM
				+ "=?";

		String[] selectionArgs = { name, address, floor, room };
		cursor = context.getContentResolver().query(uri, projection, selection,
				selectionArgs, null);

		if (cursor == null)
			return;

		if (cursor.moveToFirst()) {
			placePrimaryKey = cursor.getLong(cursor
					.getColumnIndexOrThrow(PlaceTable.COLUMN_ID));
			cursor.close();
		} else {
			// Place record doesn't exist, create it
			placeValues.put(PlaceTable.COLUMN_NAME, name);
			placeValues.put(PlaceTable.COLUMN_ADDRESS, address);
			placeValues.put(PlaceTable.COLUMN_FLOOR, floor);
			placeValues.put(PlaceTable.COLUMN_ROOM, room);

			placePrimaryKey = createRecord(placeValues,
					CollectionContentProvider.CONTENT_URI_PLACE,
					PlaceTable.COLUMN_ID);
		}

		// Update the access point with place information
		accessPointValues.put(AccessPointTable.COLUMN_PLACE, placePrimaryKey);

		accessPointPrimaryKey = getAccessPointPrimaryKey(ssid, bssid);

		uri = CollectionContentProvider.CONTENT_URI_ACCESS_POINT;
		String where = AccessPointTable.COLUMN_ID + "=?";
		selectionArgs = new String[] { Long.toString(accessPointPrimaryKey) };

		context.getContentResolver().update(uri, accessPointValues, where,
				selectionArgs);
	}

	/*
	 * ((new Date().getTime()), quality,
	 * Long.parseLong(telephonyMgr.getDeviceId()), currentNetwork.getBSSID(),
	 * ssid, login_required, name, address,latitude,longitude,granularity);
	 */
	public void saveReport(Long timestamp, String quality, String idDevice,
			String bssid, String ssid, String login_required, String name,
			String address, String latitude, String longitude,
			String granularity, String floor_room) {
		ContentValues reportValues = new ContentValues();

		reportValues.put(ReportTable.COLUMN_ID, timestamp);
		reportValues.put(ReportTable.COLUMN_LOGIN, login_required);
		reportValues.put(ReportTable.COLUMN_SSID, ssid);
		reportValues.put(ReportTable.COLUMN_BSSID, bssid);
		reportValues.put(ReportTable.COLUMN_QUALITY, quality);
		reportValues.put(ReportTable.COLUMN_NAME, ssid);
		reportValues.put(ReportTable.COLUMN_ADDRESS, address);
		reportValues.put(ReportTable.COLUMN_FLOOR, floor_room);
		reportValues.put(ReportTable.COLUMN_ROOM, floor_room);
		reportValues.put(ReportTable.COLUMN_LATITUDE, latitude);
		reportValues.put(ReportTable.COLUMN_LONGITUDE, longitude);
		reportValues.put(ReportTable.COLUMN_ACCURACY, granularity);
		reportValues.put(ReportTable.COLUMN_ID_DEVICE, idDevice);

		createRecordReport(reportValues,
				CollectionContentProvider.CONTENT_URI_TEMPREPORT,
				TempReportTable.COLUMN_ID);
		Toast.makeText(context, "HOTSPOT SAVED", Toast.LENGTH_LONG).show();
		/*
		 * String[] projection = {ReportTable.COLUMN_ID}; // Check if the Place
		 * record already exists Uri uri =
		 * CollectionContentProvider.CONTENT_URI_REPORT; String selection =
		 * ReportTable.COLUMN_BSSID + " LIKE ? AND " + ReportTable.COLUMN_SSID +
		 * " LIKE ?";
		 * 
		 * String[] selectionArgs = {bssid, ssid}; Cursor cursor =
		 * context.getContentResolver().query(uri, null, selection,
		 * selectionArgs, null);
		 * 
		 * if (cursor == null) return;
		 * 
		 * if (cursor.moveToFirst()) { // Already saved int nbRows =
		 * cursor.getCount(); for (int i=0; i< nbRows; i++) { String id =
		 * cursor.
		 * getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_ID));
		 * String v1ssid =
		 * cursor.getString(cursor.getColumnIndexOrThrow(ReportTable
		 * .COLUMN_SSID)); String v1bssid =
		 * cursor.getString(cursor.getColumnIndexOrThrow
		 * (ReportTable.COLUMN_BSSID)); String v1quality =
		 * cursor.getString(cursor
		 * .getColumnIndexOrThrow(ReportTable.COLUMN_QUALITY)); String v1place =
		 * cursor
		 * .getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_NAME));
		 * String v1address =
		 * cursor.getString(cursor.getColumnIndexOrThrow(ReportTable
		 * .COLUMN_ADDRESS)); String v1floor =
		 * cursor.getString(cursor.getColumnIndexOrThrow
		 * (ReportTable.COLUMN_FLOOR)); String v1room =
		 * cursor.getString(cursor.getColumnIndexOrThrow
		 * (ReportTable.COLUMN_ROOM)); // Report record doesn't exist, create it
		 * reportValues.put(ReportTable.COLUMN_TIMESTAMP, 1234);
		 * reportValues.put(ReportTable.COLUMN_SSID, "ssid");
		 * reportValues.put(ReportTable.COLUMN_BSSID, "bssid");
		 * reportValues.put(ReportTable.COLUMN_QUALITY, "bad");
		 * reportValues.put(ReportTable.COLUMN_NAME, "name");
		 * reportValues.put(ReportTable.COLUMN_ADDRESS, "address");
		 * reportValues.put(ReportTable.COLUMN_FLOOR, "floor");
		 * reportValues.put(ReportTable.COLUMN_ROOM, "room");
		 * reportValues.put(ReportTable.COLUMN_LATITUDE, 1.2);
		 * reportValues.put(ReportTable.COLUMN_LONGITUDE, 1.3);
		 * reportValues.put(ReportTable.COLUMN_ACCURACY, 1.4);
		 * updateRecordReport(reportValues,
		 * CollectionContentProvider.CONTENT_URI_REPORT, selection,
		 * selectionArgs); cursor.moveToNext(); } cursor.close(); } else { //
		 * Report record doesn't exist, create it
		 * reportValues.put(ReportTable.COLUMN_ID, timestamp);
		 * reportValues.put(ReportTable.COLUMN_LOGIN, login_required);
		 * reportValues.put(ReportTable.COLUMN_SSID,ssid);
		 * reportValues.put(ReportTable.COLUMN_BSSID, bssid);
		 * reportValues.put(ReportTable.COLUMN_QUALITY, quality);
		 * reportValues.put(ReportTable.COLUMN_NAME, ssid);
		 * reportValues.put(ReportTable.COLUMN_ADDRESS, address);
		 * reportValues.put(ReportTable.COLUMN_FLOOR, floor_room);
		 * reportValues.put(ReportTable.COLUMN_ROOM, floor_room);
		 * reportValues.put(ReportTable.COLUMN_LATITUDE,latitude);
		 * reportValues.put(ReportTable.COLUMN_LONGITUDE, longitude);
		 * reportValues.put(ReportTable.COLUMN_ACCURACY,granularity);
		 * reportValues.put(ReportTable.COLUMN_ID_DEVICE,idDevice);
		 * 
		 * createRecordReport(reportValues,
		 * CollectionContentProvider.CONTENT_URI_REPORT, ReportTable.COLUMN_ID);
		 * Toast.makeText(context, "HOTSPOT SAVED", Toast.LENGTH_LONG).show(); }
		 */
	}

	// databaseHelper.saveData("bssid", "ssid",
	// "rating","latitude","longitude","radius", "name", "address", "floor",
	// "room");
	/**
	 * @param SSID
	 * @param BSSID
	 * @return A cursor pointing to a table with all the necessary information
	 *         to display to the user
	 */
	public Cursor getAccessPointInformation(String ssid, String bssid) {
		Cursor cursor = null;

		Uri uri = CollectionContentProvider.CONTENT_URI_REPORT;

		String selection = ReportTable.COLUMN_SSID + "=?";// AND "
		// + ReportTable.COLUMN_BSSID + " LIKE ?"; //AND
		// " + AccessPointTable.COLUMN_BSSID + "=?";
		// Log.d(TAG, ssid);
		// Log.d(TAG, bssid);
		String[] selectionArgs = { ssid };// , bssid};
		String[] projection = { ReportTable.COLUMN_ID, ReportTable.COLUMN_SSID,
				ReportTable.COLUMN_BSSID, ReportTable.COLUMN_QUALITY,
				ReportTable.COLUMN_LOGIN, ReportTable.COLUMN_NAME,
				ReportTable.COLUMN_ADDRESS, ReportTable.COLUMN_FLOOR,
				ReportTable.COLUMN_ROOM };

		cursor = context.getContentResolver().query(uri, projection, "ssid=?",
				new String[] { ssid }, null);
		int row = cursor.getCount();

		return cursor;
	}

	public Cursor getReports(String keyword) {
		Cursor cursor = null;

		Uri uri = CollectionContentProvider.CONTENT_URI_REPORT;

		String selection = ReportTable.COLUMN_SSID + " LIKE ?";// AND "
		// + ReportTable.COLUMN_BSSID + " LIKE ?"; //AND
		// " + AccessPointTable.COLUMN_BSSID + "=?";
		// Log.d(TAG, ssid);
		// Log.d(TAG, bssid);

		String[] selectionArgs = { "%" + keyword + "%" };// , bssid};
		String[] projection = { ReportTable.COLUMN_ID, ReportTable.COLUMN_SSID,
				ReportTable.COLUMN_BSSID, ReportTable.COLUMN_QUALITY,
				ReportTable.COLUMN_LOGIN, ReportTable.COLUMN_NAME,
				ReportTable.COLUMN_ADDRESS, ReportTable.COLUMN_FLOOR,
				ReportTable.COLUMN_ROOM };

		cursor = context.getContentResolver().query(uri, projection, selection,
				selectionArgs, null);

		int row = cursor.getCount();

		// gagogg: start
		// gagogg: also search by address
		// if (row == 0 ) { //old code
		
		selection = ReportTable.COLUMN_ADDRESS + " LIKE ?";
		String[] selectionArg = { "%" + keyword + "%" };
		
		Cursor cursor2 = context.getContentResolver().query(uri, projection, selection, selectionArg, null);
		row = cursor2.getCount();
		Cursor merCur = new MergeCursor(new Cursor[] { cursor, cursor2 });
		if (merCur.getCount() < 1) {
			return null;
		}
		merCur.moveToFirst();
		// } //old code
		// return cursor; // olde code 
		return merCur; //gagogg: merCur is the result of merging two cursor: (search address and address)
		//gagogg: end 
	}

	/**
	 * Get a list of nearby access points. </br> Currently it gets all the
	 * access points within a box area, not a circle area. It roughly tries to
	 * calculate the change in degrees for each unit change in distance.
	 * <p>
	 * 
	 * Refer to these links: </br>
	 * https://developer.appcelerator.com/question/143813
	 * /search-based-on-geo-pointslatitude-longitude </br>
	 * http://stackoverflow.com/questions/15258078/latitude-longitude-and-meters
	 * <p>
	 * 
	 * Will need to implement the Haversine formula to properly get a circle
	 * radius of access points.
	 * 
	 * <p>
	 * 
	 * @param currentLatitude
	 *            the latitude to search from
	 * @param currentLongitude
	 *            the longitude to search from
	 * @param radius
	 *            the distance in km to search out
	 * @return a cursor pointing to a table with the result
	 */
	public Cursor getNearbyAccessPoint(double currentLatitude,
			double currentLongitude, double radius) {
		Cursor cursor = null;
		double latDistance = Util.getLatitudeDistance(radius);
		double lonDistance = Util.getLongitudeDistance(radius, currentLatitude);

		double lat_min = currentLatitude - latDistance;
		double lat_max = currentLatitude + latDistance;
		double lon_min = currentLongitude - lonDistance;
		double lon_max = currentLongitude + lonDistance;

		Uri uri = CollectionContentProvider.CONTENT_URI_REPORT;
		String[] projection = { ReportTable.COLUMN_SSID,
				ReportTable.COLUMN_BSSID, ReportTable.COLUMN_ACCURACY,
				ReportTable.COLUMN_QUALITY, ReportTable.COLUMN_LOGIN,
				ReportTable.COLUMN_NAME, ReportTable.COLUMN_ADDRESS,
				ReportTable.COLUMN_FLOOR, ReportTable.COLUMN_ROOM,
				ReportTable.COLUMN_LATITUDE, ReportTable.COLUMN_LONGITUDE };

		String selection = ReportTable.COLUMN_LATITUDE + " >= ? AND "
				+ ReportTable.COLUMN_LATITUDE + " <= ? AND "
				+ ReportTable.COLUMN_LONGITUDE + " >= ? AND "
				+ ReportTable.COLUMN_LONGITUDE + " <= ?";
		String[] selectionArgs = { Double.toString(lat_min),
				Double.toString(lat_max), Double.toString(lon_min),
				Double.toString(lon_max) };

		cursor = context.getContentResolver().query(uri, projection, null,
				null, null);
		Logger log = new Logger();
		if (cursor == null)
			return null;

		if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		do {
			log.addRecordToLog("BSSID="
					+ cursor.getColumnIndexOrThrow(ReportTable.COLUMN_BSSID)
					+ " Lat="
					+ cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LATITUDE)
					+ "  Long="
					+ cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LONGITUDE));
		} while (cursor.moveToNext());

		return cursor;
	}

	public void saveData(String bssid, String ssid, String rating,
			String latitude, String longitude, String radius, String name,
			String address, String floor, String room, boolean login) {
		// TODO Auto-generated method stub
		Logger log = new Logger();
		log.addRecordToLog("bssid from server=" + bssid);
		ContentValues reportValues = new ContentValues();
		String[] projection = { ReportTable.COLUMN_ID };
		// Check if the Place record already exists
		Uri uri = CollectionContentProvider.CONTENT_URI_REPORT;
		String selection = ReportTable.COLUMN_BSSID + " LIKE ? AND "
				+ ReportTable.COLUMN_SSID + " LIKE ?";

		String[] selectionArgs = { bssid, ssid };
		Cursor cursor = context.getContentResolver().query(uri, null,
				selection, selectionArgs, null);

		if (cursor == null)
			return;

		if (cursor.moveToFirst()) {
			// Already saved
			int nbRows = cursor.getCount();
			for (int i = 0; i < nbRows; i++) {
				String id = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_ID));
				String v1ssid = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_SSID));
				String v1bssid = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_BSSID));
				String v1quality = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_QUALITY));
				String v1place = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_NAME));
				String v1address = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_ADDRESS));
				String v1floor = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_FLOOR));
				String v1room = cursor.getString(cursor
						.getColumnIndexOrThrow(ReportTable.COLUMN_ROOM));
				cursor.moveToNext();
			}
			cursor.close();
		} else {
			// Report record doesn't exist, create it
			float login_value = (login == true) ? 1 : 0;
			reportValues.put(ReportTable.COLUMN_LOGIN, login_value);
			reportValues.put(ReportTable.COLUMN_SSID, ssid);
			reportValues.put(ReportTable.COLUMN_BSSID, bssid);
			reportValues.put(ReportTable.COLUMN_QUALITY, rating);
			reportValues.put(ReportTable.COLUMN_NAME, name);
			reportValues.put(ReportTable.COLUMN_ADDRESS, address);
			reportValues.put(ReportTable.COLUMN_FLOOR, floor);
			reportValues.put(ReportTable.COLUMN_ROOM, room);
			reportValues.put(ReportTable.COLUMN_LATITUDE, latitude);
			reportValues.put(ReportTable.COLUMN_LONGITUDE, longitude);
			reportValues.put(ReportTable.COLUMN_ACCURACY, radius);

			createRecordReport(reportValues,
					CollectionContentProvider.CONTENT_URI_REPORT,
					ReportTable.COLUMN_ID);
		}

	}
}
