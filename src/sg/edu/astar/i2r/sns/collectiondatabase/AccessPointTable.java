package sg.edu.astar.i2r.sns.collectiondatabase;

import sg.edu.astar.i2r.sns.contentprovider.CollectionContentProvider;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper.CollectionTables;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class AccessPointTable implements BaseColumns {
	public static final String TABLE_NAME = CollectionTables.ACCESS_POINT;
	public static final Uri CONTENT_URI = Uri.parse("content://" + CollectionContentProvider.AUTHORITY + "/" + TABLE_NAME);
	
	// AccessPoint Table Column names
    public static final String COLUMN_ID = BaseColumns._ID;	// Primary Key - intialised used because Android cursorAdapter relies on this underscore format.
    public static final String COLUMN_SSID = "ssid";		// No longer use cursorAdapter but no need to change it
    public static final String COLUMN_BSSID = "bssid";
    public static final String COLUMN_PLACE = "place";

	// 	Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ID 			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ COLUMN_SSID 			+ " TEXT,"
					+ COLUMN_BSSID 			+ " TEXT,"
					+ COLUMN_PLACE 			+ " REFERENCES place(place_id)"
    		+ ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(AccessPointTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
