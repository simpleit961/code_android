package sg.edu.astar.i2r.sns.displaydatabase;

import sg.edu.astar.i2r.sns.contentprovider.DisplayContentProvider;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper.DisplayTables;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * The table that represents the access point and location relationship. </br>
 * An access point is associated with a location if available.
 */
public class AccessPointContainsLocationTable {
	public static final String TABLE_NAME = DisplayTables.ACCESS_POINT_CONTAINS_LOCATION;
	public static final Uri CONTENT_URI = Uri.parse("content://" + DisplayContentProvider.AUTHORITY + "/" + TABLE_NAME);
	
	// AccessPoint Table Column names
    public static final String COLUMN_ACCESS_POINT = "access_point";
    public static final String COLUMN_LOCATION = "location";

	// 	Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ACCESS_POINT 		+ " INTEGER REFERENCES Access_point(_id),"
					+ COLUMN_LOCATION 			+ " INTEGER REFERENCES Location(location_id),"
					+ "PRIMARY KEY(access_point, location)"
    		+ ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(AccessPointContainsLocationTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
