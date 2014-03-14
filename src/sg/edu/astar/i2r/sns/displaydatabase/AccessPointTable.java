package sg.edu.astar.i2r.sns.displaydatabase;

import sg.edu.astar.i2r.sns.contentprovider.DisplayContentProvider;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper.DisplayTables;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class AccessPointTable implements BaseColumns {
	public static final String TABLE_NAME = DisplayTables.ACCESS_POINT;
	public static final Uri CONTENT_URI = Uri.parse("content://" + DisplayContentProvider.AUTHORITY + "/" + TABLE_NAME);
	
	// AccessPoint Table Column names
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_SSID = "ssid";
    public static final String COLUMN_BSSID = "bssid";
    public static final String COLUMN_RATED_SPEED = "rated_speed";
    public static final String COLUMN_PLACE = "place";
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_LOGIN = "login";

	// 	Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ID 			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ COLUMN_PLACE			+ " INTEGER REFERENCES Place(place_id),"
					+ COLUMN_SSID 			+ " TEXT,"
					+ COLUMN_BSSID 			+ " TEXT,"
					+ COLUMN_RATED_SPEED	+ " INTEGER,"
					+ COLUMN_LOGIN			+ " INTEGER,"
					+ COLUMN_POPULARITY		+ " INTEGER"
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
