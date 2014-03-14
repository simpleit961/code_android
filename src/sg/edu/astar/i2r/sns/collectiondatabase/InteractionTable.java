package sg.edu.astar.i2r.sns.collectiondatabase;

import sg.edu.astar.i2r.sns.contentprovider.CollectionContentProvider;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper.CollectionTables;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class InteractionTable {
	public static final String TABLE_NAME = CollectionTables.INTERACTION;
	public static final Uri CONTENT_URI = Uri.parse("content://" + CollectionContentProvider.AUTHORITY + "/" + TABLE_NAME);

	// AccessPoint Table Column names
    public static final String COLUMN_ID = "interaction_id";
    public static final String COLUMN_WITH = "with";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_RSSI = "rssi";
    public static final String COLUMN_ACCURACY = "accuracy";
    public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_LINK_SPEED = "link_speed";	// Mbps
    // Link Speed, Duration

	// 	Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ID 				+ " INTEGER PRIMARY KEY,"
					+ COLUMN_WITH				+ " INTEGER REFERENCES Access_point(_id)," 
					+ COLUMN_LOCATION			+ " INTEGER REFERENCES Location(location_id)," 
					+ COLUMN_TIMESTAMP 			+ " INTEGER NOT NULL,"
					+ COLUMN_LINK_SPEED			+ " INTEGER,"
				    + COLUMN_RSSI	 			+ " INTEGER,"
				    + COLUMN_STATUS 			+ " INTEGER,"		// Failed, successful, slow connect etc..
				    + COLUMN_ACCURACY 			+ " REAL"
    		+ ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(InteractionTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
