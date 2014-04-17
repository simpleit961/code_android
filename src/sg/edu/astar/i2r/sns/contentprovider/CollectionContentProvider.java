package sg.edu.astar.i2r.sns.contentprovider;

import sg.edu.astar.i2r.sns.collectiondatabase.AccessPointTable;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper.CollectionTables;
import sg.edu.astar.i2r.sns.collectiondatabase.EncounterTable;
import sg.edu.astar.i2r.sns.collectiondatabase.LocationTable;
import sg.edu.astar.i2r.sns.collectiondatabase.PlaceTable;
import sg.edu.astar.i2r.sns.collectiondatabase.ReportTable;
import sg.edu.astar.i2r.sns.collectiondatabase.TempReportTable;
import sg.edu.astar.i2r.sns.collectiondatabase.UserTable;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper.DisplayTables;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Content provider for the collection database.
 * 
 * <p>
 * 
 * Note: Methods here are not called directly.
 * In other classes (e.g. database helper class), you get the Content Resolver
 * and call the query method via getContentResolver().query(...) 
 * which in turn calls the query method here
 */
public class CollectionContentProvider extends ContentProvider {
	@SuppressWarnings("unused")
	private static final String TAG = "CollectionContentProvider";
	public static final String AUTHORITY = "sg.edu.astar.i2r.sns.contentprovider.collectionconentprovider";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	
	// Database
	private CollectionDatabaseHelper database;
	
    // Define Table URIs
	private static final String PATH_USER = CollectionTables.USER;
	private static final String PATH_PLACE = CollectionTables.PLACE;
	private static final String PATH_REPORT = CollectionTables.REPORT;
	private static final String PATH_TEMPREPORT = CollectionTables.TEMPREPORT;

	private static final String PATH_LOCATION = CollectionTables.LOCATION;
	private static final String PATH_ENCOUNTER = CollectionTables.ENCOUNTER;
	private static final String PATH_INTERACTION = CollectionTables.INTERACTION;
	private static final String PATH_ACCESS_POINT = CollectionTables.ACCESS_POINT;
	private static final String PATH_USER_RATES_ACCESS_POINT = CollectionTables.USER_RATES_ACCESS_POINT;
	private static final String PATH_ACCESS_POINT_TAGGED_PLACE = CollectionTables.ACCESS_POINT_TAGGED_PLACE;
	private static final String PATH_ENCOUNTER_WITH_ACCESS_POINT = CollectionTables.ENCOUNTER_WITH_ACCESS_POINT;
	
	public static final Uri CONTENT_URI_USER = Uri.withAppendedPath(AUTHORITY_URI, PATH_USER);
	public static final Uri CONTENT_URI_PLACE = Uri.withAppendedPath(AUTHORITY_URI, PATH_PLACE);
	public static final Uri CONTENT_URI_REPORT = Uri.withAppendedPath(AUTHORITY_URI, PATH_REPORT);
	public static final Uri CONTENT_URI_TEMPREPORT = Uri.withAppendedPath(AUTHORITY_URI, PATH_TEMPREPORT);

	public static final Uri CONTENT_URI_LOCATION = Uri.withAppendedPath(AUTHORITY_URI, PATH_LOCATION);
	public static final Uri CONTENT_URI_ENCOUNTER = Uri.withAppendedPath(AUTHORITY_URI, PATH_ENCOUNTER);
	public static final Uri CONTENT_URI_INTERACTION = Uri.withAppendedPath(AUTHORITY_URI, PATH_INTERACTION);
	public static final Uri CONTENT_URI_ACCESS_POINT = Uri.withAppendedPath(AUTHORITY_URI, PATH_ACCESS_POINT);
	public static final Uri CONTENT_URI_USER_RATES_ACCESS_POINT = Uri.withAppendedPath(AUTHORITY_URI, PATH_USER_RATES_ACCESS_POINT);
	public static final Uri CONTENT_URI_ACCESS_POINT_TAGGED_PLACE = Uri.withAppendedPath(AUTHORITY_URI, PATH_ACCESS_POINT_TAGGED_PLACE);
	public static final Uri CONTENT_URI_ENCOUNTER_WITH_ACCESS_POINT = Uri.withAppendedPath(AUTHORITY_URI, PATH_ENCOUNTER_WITH_ACCESS_POINT);

	// Joined Table
	private static final String PATH_ACCESS_POINT_JOIN_PLACE = CollectionTables.ACCESS_POINT_JOIN_PLACE;
	public static final Uri CONTENT_URI_ACCESS_POINT_JOIN_PLACE = Uri.withAppendedPath(AUTHORITY_URI, PATH_ACCESS_POINT_JOIN_PLACE);
	
		
	// Setup UriMatcher
	private static final int USER = 10;
	private static final int USER_ID = 20;
	private static final int PLACE = 30;
	private static final int PLACE_ID = 40;
	private static final int LOCATION = 50;
	private static final int LOCATION_ID = 60;
	private static final int ENCOUNTER = 70;
	private static final int ENCOUNTER_ID = 80;
	private static final int INTERACTION = 90;
	private static final int ACCESS_POINT = 100;
	private static final int ACCESS_POINT_ID = 110;
	private static final int USER_RATES_ACCESS_POINT = 120;
	private static final int ACCESS_POINT_TAGGED_PLACE = 130;
	private static final int ENCOUNTER_WITH_ACCESS_POINT = 140;
	private static final int ACCESS_POINT_JOIN_PLACE = 150;
	private static final int REPORT = 160;
	private static final int REPORT_ID = 170;
	private static final int TEMPREPORT = 180;
	private static final int TEMPREPORT_ID = 190;
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, PATH_USER, USER);
		sURIMatcher.addURI(AUTHORITY, PATH_PLACE, PLACE);
		sURIMatcher.addURI(AUTHORITY, PATH_REPORT, REPORT);
		sURIMatcher.addURI(AUTHORITY, PATH_TEMPREPORT, TEMPREPORT);
		
		sURIMatcher.addURI(AUTHORITY, PATH_LOCATION, LOCATION);
		sURIMatcher.addURI(AUTHORITY, PATH_ENCOUNTER, ENCOUNTER);
		sURIMatcher.addURI(AUTHORITY, PATH_INTERACTION, INTERACTION);
		sURIMatcher.addURI(AUTHORITY, PATH_ACCESS_POINT, ACCESS_POINT);

		sURIMatcher.addURI(AUTHORITY, PATH_USER + "/#", USER_ID);
		sURIMatcher.addURI(AUTHORITY, PATH_PLACE + "/#", PLACE_ID);
		sURIMatcher.addURI(AUTHORITY, PATH_REPORT + "/#", REPORT_ID);
		sURIMatcher.addURI(AUTHORITY, PATH_TEMPREPORT + "/#", TEMPREPORT_ID);

		sURIMatcher.addURI(AUTHORITY, PATH_LOCATION + "/#", LOCATION_ID);
		sURIMatcher.addURI(AUTHORITY, PATH_ACCESS_POINT + "/#", ACCESS_POINT_ID);
		sURIMatcher.addURI(AUTHORITY, PATH_ENCOUNTER + "/#", ENCOUNTER_ID);
		
		sURIMatcher.addURI(AUTHORITY, PATH_USER_RATES_ACCESS_POINT, USER_RATES_ACCESS_POINT);
		sURIMatcher.addURI(AUTHORITY, PATH_ENCOUNTER_WITH_ACCESS_POINT, ENCOUNTER_WITH_ACCESS_POINT);
		sURIMatcher.addURI(AUTHORITY, PATH_ACCESS_POINT_TAGGED_PLACE, ACCESS_POINT_TAGGED_PLACE);
		sURIMatcher.addURI(AUTHORITY, PATH_ACCESS_POINT_JOIN_PLACE, ACCESS_POINT_JOIN_PLACE);

	}
	
	@Override
	public boolean onCreate() {	
		database = new CollectionDatabaseHelper(getContext());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		int uriType = sURIMatcher.match(uri);
		
		queryBuilder.setTables(getTables(uri));
		switch (uriType) {
		case ACCESS_POINT_ID:
			queryBuilder.appendWhere(AccessPointTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case LOCATION_ID:
			queryBuilder.appendWhere(LocationTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case ENCOUNTER_ID:
			queryBuilder.appendWhere(EncounterTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case USER_ID:
			queryBuilder.appendWhere(UserTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case PLACE_ID:
			queryBuilder.appendWhere(PlaceTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case REPORT_ID:
			queryBuilder.appendWhere(ReportTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case TEMPREPORT_ID:
			queryBuilder.appendWhere(TempReportTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case PLACE:
		case REPORT:
		case TEMPREPORT:
		case LOCATION:
		case ENCOUNTER:
		case ACCESS_POINT:
		case USER_RATES_ACCESS_POINT:
		case ACCESS_POINT_TAGGED_PLACE:
		case ACCESS_POINT_JOIN_PLACE:
		case ENCOUNTER_WITH_ACCESS_POINT:
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
//		String query = queryBuilder.buildQuery(projection, selection, null, null, sortOrder, null);
//		Log.d(TAG, query);
		
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
	
	/**
	 * Private method used to determine table to access
	 * @param uri the Uri pointing to a table
	 * @return
	 */
	private static String getTables(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		String table = null;
	
		switch (uriType) {
		case USER:
		case USER_ID:
			table = CollectionTables.USER;
			break; 
		case PLACE:
		case PLACE_ID:
			table = CollectionTables.PLACE;
			break; 
		case REPORT:
		case REPORT_ID:
			table = CollectionTables.REPORT;
			break; 
		case TEMPREPORT:
		case TEMPREPORT_ID:
			table = CollectionTables.TEMPREPORT;
			break; 
		case ACCESS_POINT:
		case ACCESS_POINT_ID:
			table = CollectionTables.ACCESS_POINT;
			break; 
		case LOCATION:
		case LOCATION_ID:
			table = CollectionTables.LOCATION;
			break; 
		case ACCESS_POINT_JOIN_PLACE:
			table = CollectionTables.ACCESS_POINT_JOIN_PLACE;
			break;
		case INTERACTION:
			table = CollectionTables.INTERACTION;
			break; 
		case ENCOUNTER:
		case ENCOUNTER_ID:
			table = CollectionTables.ENCOUNTER;
			break; 
		case ENCOUNTER_WITH_ACCESS_POINT:
			table = CollectionTables.ENCOUNTER_WITH_ACCESS_POINT;
			break;
		case ACCESS_POINT_TAGGED_PLACE:
			table = CollectionTables.ACCESS_POINT_TAGGED_PLACE;
			break;
		case USER_RATES_ACCESS_POINT:
			table = CollectionTables.USER_RATES_ACCESS_POINT;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		return table;
	} 
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = database.getWritableDatabase();
		int uriType = sURIMatcher.match(uri);
		String path = null;
		long id = -1;
		
		switch (uriType) {
		case USER:
			path = PATH_USER;
			id = db.insert(CollectionTables.USER, null, values);
			break;
		case ACCESS_POINT:
			path = PATH_ACCESS_POINT;
			id = db.insert(CollectionTables.ACCESS_POINT, null, values);
			break;
		case PLACE:
			path = PATH_PLACE;
			id = db.insert(CollectionTables.PLACE, null, values);
			break;
		case REPORT:
			path = PATH_REPORT;
			id = db.insert(CollectionTables.REPORT, null, values);
			break;
		case TEMPREPORT:
			path = PATH_TEMPREPORT;
			id = db.insert(CollectionTables.TEMPREPORT, null, values);
			break;
		case LOCATION:
			path = PATH_LOCATION;
			id = db.insert(CollectionTables.LOCATION, null, values);
			break;
		case ENCOUNTER:
			path = PATH_ENCOUNTER;
			id = db.insert(CollectionTables.ENCOUNTER, null, values);
			break;	
		case INTERACTION:
			path = PATH_INTERACTION;
			id = db.insert(CollectionTables.INTERACTION, null, values);
			break;	
		case ENCOUNTER_WITH_ACCESS_POINT:
			path = PATH_ENCOUNTER_WITH_ACCESS_POINT;
			id = db.insert(CollectionTables.ENCOUNTER_WITH_ACCESS_POINT, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(path + "/" + id);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = database.getWritableDatabase();
		int uriType = sURIMatcher.match(uri);
		int rowsDeleted = 0;
		String id;
		
		switch (uriType) {
		case USER:
			rowsDeleted = db.delete(UserTable.TABLE_NAME, selection, selectionArgs);
			break;
		case REPORT:
			rowsDeleted = db.delete(ReportTable.TABLE_NAME, selection, selectionArgs);
			break;	
		case TEMPREPORT:
			rowsDeleted = db.delete(TempReportTable.TABLE_NAME, selection, selectionArgs);
			break;	
		case USER_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(UserTable.TABLE_NAME, UserTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(UserTable.TABLE_NAME, UserTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
			
		case ACCESS_POINT:
			rowsDeleted = db.delete(AccessPointTable.TABLE_NAME, selection, selectionArgs);
			break;
			
		case ACCESS_POINT_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(AccessPointTable.TABLE_NAME, AccessPointTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(AccessPointTable.TABLE_NAME, AccessPointTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
			
		case LOCATION:
			rowsDeleted = db.delete(LocationTable.TABLE_NAME, selection, selectionArgs);
			break;
			
		case LOCATION_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(LocationTable.TABLE_NAME, LocationTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(LocationTable.TABLE_NAME, LocationTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsUpdated = 0;
		String id;
		
		switch (uriType) {
		case USER:
			rowsUpdated = db.update(UserTable.TABLE_NAME, values, selection, selectionArgs);
			break;
		case REPORT:
			rowsUpdated = db.update(ReportTable.TABLE_NAME, values, selection, selectionArgs);
			break;	
		case TEMPREPORT:
			rowsUpdated = db.update(TempReportTable.TABLE_NAME, values, selection, selectionArgs);
			break;
		case USER_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(UserTable.TABLE_NAME, values, UserTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(UserTable.TABLE_NAME, values, UserTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
			
		case ACCESS_POINT:
			rowsUpdated = db.update(AccessPointTable.TABLE_NAME, values, selection, selectionArgs);
			break;
			
		case ACCESS_POINT_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(AccessPointTable.TABLE_NAME, values, AccessPointTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(AccessPointTable.TABLE_NAME, values, AccessPointTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
			
		case LOCATION:
			rowsUpdated = db.update(LocationTable.TABLE_NAME, values, selection, selectionArgs);
			break;
			
		case LOCATION_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(LocationTable.TABLE_NAME, values, LocationTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(LocationTable.TABLE_NAME, values, LocationTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return rowsUpdated;
	}
	
	// This is not used.
	@Override
	public String getType(Uri arg0) {
		return null;
	}
}
