package sg.edu.astar.i2r.sns.displaydatabase;

import sg.edu.astar.i2r.sns.contentprovider.DisplayContentProvider;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper.DisplayTables;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * A table for semantic location information
 */
public class PlaceTable {
	public static final String TABLE_NAME = DisplayTables.PLACE;
	public static final Uri CONTENT_URI = Uri.parse("content://" + DisplayContentProvider.AUTHORITY + "/" + TABLE_NAME);

	// Table Column names
    public static final String COLUMN_ID = "place_id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_ROOM = "room";
    
	// 	Database creation SQL statement
	private static final String DATABASE_CREATE =
					"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ID 		+ " INTEGER PRIMARY KEY,"
				    + COLUMN_ADDRESS	+ " TEXT,"
				    + COLUMN_NAME		+ " TEXT,"
				    + COLUMN_TYPE 		+ " TEXT,"
				    + COLUMN_FLOOR		+ " TEXT,"
				    + COLUMN_ROOM		+ " TEXT"
				    + ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(PlaceTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
	
}
