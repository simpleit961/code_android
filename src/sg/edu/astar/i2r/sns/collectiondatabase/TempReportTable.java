package sg.edu.astar.i2r.sns.collectiondatabase;

import sg.edu.astar.i2r.sns.contentprovider.CollectionContentProvider;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper.CollectionTables;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class TempReportTable {
	public static final String TABLE_NAME = CollectionTables.TEMPREPORT;
	public static final Uri CONTENT_URI = Uri.parse("content://" + CollectionContentProvider.AUTHORITY + "/" + TABLE_NAME);

	// Table Column names
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_SSID = "ssid";
    public static final String COLUMN_BSSID = "bssid";
    public static final String COLUMN_QUALITY = "quality";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ACCURACY = "accuracy";
    public static final String COLUMN_ID_DEVICE = "iddevice";
    
	// 	Database creation SQL statement
	private static final String DATABASE_CREATE =
					"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ID 		+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				    + COLUMN_LOGIN		+ " REAL,"
				    + COLUMN_SSID		+ " TEXT,"
				    + COLUMN_BSSID 		+ " TEXT,"
				    + COLUMN_QUALITY 	+ " TEXT,"
				    + COLUMN_NAME		+ " TEXT,"
				    + COLUMN_ADDRESS	+ " TEXT,"
				    + COLUMN_FLOOR		+ " TEXT,"
				    + COLUMN_ROOM		+ " TEXT,"
				    + COLUMN_LATITUDE	+ " REAL,"
				    + COLUMN_LONGITUDE	+ " REAL,"
				    + COLUMN_ACCURACY	+ " REAL,"
				     + COLUMN_ID_DEVICE		+ " TEXT"
				    + ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(ReportTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
	
}
