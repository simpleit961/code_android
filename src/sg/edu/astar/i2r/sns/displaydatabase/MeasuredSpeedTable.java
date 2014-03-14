package sg.edu.astar.i2r.sns.displaydatabase;

import sg.edu.astar.i2r.sns.contentprovider.DisplayContentProvider;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper.DisplayTables;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * A table used to store all the values needed 
 * for timeline chart [App Extension]. </br>
 * 
 * Can change it to rated speed instead. It depends on what is required.
 */
public class MeasuredSpeedTable {
	public static final String TABLE_NAME = DisplayTables.MEASURED_SPEED;
	public static final Uri CONTENT_URI = Uri.parse("content://" + DisplayContentProvider.AUTHORITY + "/" + TABLE_NAME);

	// Table Column names
    public static final String COLUMN_ID = "location_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_ACCESS_POINT = "access_point";
    public static final String COLUMN_VALUE = "value";
    
	// 	Database creation SQL statement
	private static final String DATABASE_CREATE =
					"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ID 				+ " INTEGER PRIMARY KEY,"
				    + COLUMN_ACCESS_POINT 		+ " INTEGER REFERENCES access_Point(_id),"
				    + COLUMN_TIMESTAMP			+ " INTEGER,"
				    + COLUMN_VALUE				+ " REAL"
				    + ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(MeasuredSpeedTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
