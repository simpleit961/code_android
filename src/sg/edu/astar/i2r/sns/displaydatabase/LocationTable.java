package sg.edu.astar.i2r.sns.displaydatabase;

import sg.edu.astar.i2r.sns.contentprovider.DisplayContentProvider;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper.DisplayTables;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class LocationTable {
	public static final String TABLE_NAME = DisplayTables.LOCATION;
	public static final Uri CONTENT_URI = Uri.parse("content://" + DisplayContentProvider.AUTHORITY + "/" + TABLE_NAME);

	// Table Column names
    public static final String COLUMN_ID = "location_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    
	// 	Database creation SQL statement
	private static final String DATABASE_CREATE =
					"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ID 			+ " INTEGER PRIMARY KEY,"
				    + COLUMN_LATITUDE 		+ " REAL,"
				    + COLUMN_LONGITUDE 		+ " REAL"
				    + ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(LocationTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
	
}
