package sg.edu.astar.i2r.sns.contentprovider;

import sg.edu.astar.i2r.sns.displaydatabase.AccessPointTable;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper.DisplayTables;
import sg.edu.astar.i2r.sns.displaydatabase.LocationTable;
import sg.edu.astar.i2r.sns.displaydatabase.PlaceTable;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Content provider for the display database
 */
public class DisplayContentProvider extends ContentProvider {
	@SuppressWarnings("unused")
	private static final String TAG = "DisplayContentProvider";
	public static final String AUTHORITY = "sg.edu.astar.i2r.sns.contentprovider.displaycontentprovider";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	
	// Database
	private DisplayDatabaseHelper database;

    // Define Table URIs
	private static final String PATH_PLACE = DisplayTables.PLACE;
	private static final String PATH_LOCATION = DisplayTables.LOCATION;
	private static final String PATH_ACCESS_POINT = DisplayTables.ACCESS_POINT;
	
	// Joined Table
	private static final String PATH_ACCESS_POINT_JOIN_PLACE = DisplayTables.ACCESS_POINT_JOIN_PLACE;
	private static final String PATH_JOIN_ALL = DisplayTables.JOIN_ALL;
	
	public static final Uri CONTENT_URI_PLACE = Uri.withAppendedPath(AUTHORITY_URI, PATH_PLACE);
	public static final Uri CONTENT_URI_LOCATION = Uri.withAppendedPath(AUTHORITY_URI, PATH_LOCATION);
	public static final Uri CONTENT_URI_ACCESS_POINT = Uri.withAppendedPath(AUTHORITY_URI, PATH_ACCESS_POINT);

	public static final Uri CONTENT_URI_ACCESS_POINT_JOIN_PLACE = Uri.withAppendedPath(AUTHORITY_URI, PATH_ACCESS_POINT_JOIN_PLACE);
	public static final Uri CONTENT_URI_JOIN_ALL = Uri.withAppendedPath(AUTHORITY_URI, PATH_JOIN_ALL);
	
	// Setup UriMatcher
	private static final int PLACE = 10;
	private static final int PLACE_ID = 20;
	private static final int LOCATION = 30;
	private static final int LOCATION_ID = 40;
	private static final int ACCESS_POINT = 50;
	private static final int ACCESS_POINT_ID = 60;

	private static final int ACCESS_POINT_JOIN_PLACE = 70;
	private static final int JOIN_ALL = 80;

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, PATH_PLACE, PLACE);
		sURIMatcher.addURI(AUTHORITY, PATH_LOCATION, LOCATION);
		sURIMatcher.addURI(AUTHORITY, PATH_ACCESS_POINT, ACCESS_POINT);

		sURIMatcher.addURI(AUTHORITY, PATH_PLACE + "/#", PLACE_ID);
		sURIMatcher.addURI(AUTHORITY, PATH_LOCATION + "/#", LOCATION_ID);
		sURIMatcher.addURI(AUTHORITY, PATH_ACCESS_POINT + "/#", ACCESS_POINT_ID);

		sURIMatcher.addURI(AUTHORITY, PATH_ACCESS_POINT_JOIN_PLACE, ACCESS_POINT_JOIN_PLACE);
		sURIMatcher.addURI(AUTHORITY, PATH_JOIN_ALL, JOIN_ALL);
	}
	
	@Override
	public boolean onCreate() {	
		database = new DisplayDatabaseHelper(getContext());
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
		case PLACE_ID:
			queryBuilder.appendWhere(PlaceTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case LOCATION:
		case ACCESS_POINT:
		case ACCESS_POINT_JOIN_PLACE:
		case JOIN_ALL:
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
//		Used for debugging purposes:
//		String s = queryBuilder.buildQuery(projection, selection, null, null, sortOrder, null);
//		Log.d(TAG, s);
		
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

		return cursor;
	}
	
	private static String getTables(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		String table = null;
	
		switch (uriType) {
		case ACCESS_POINT:
		case ACCESS_POINT_ID:
			table = DisplayTables.ACCESS_POINT;
			break; 
		case LOCATION:
		case LOCATION_ID:
			table = DisplayTables.LOCATION;
			break; 
		case ACCESS_POINT_JOIN_PLACE:
			table = DisplayTables.ACCESS_POINT_JOIN_PLACE;
			break;
		case JOIN_ALL:
			table = DisplayTables.JOIN_ALL;
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
		case ACCESS_POINT:
			path = PATH_ACCESS_POINT;
			id = db.insert(DisplayTables.ACCESS_POINT, null, values);
			break;
		case PLACE:
			path = PATH_PLACE;
			id = db.insert(DisplayTables.PLACE, null, values);
			break;
		case LOCATION:
			path = PATH_LOCATION;
			id = db.insert(DisplayTables.LOCATION, null, values);
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
	
	@Override
	public String getType(Uri arg0) {
		return null;
	}
}
