package sg.edu.astar.i2r.sns.collectiondatabase;

import sg.edu.astar.i2r.sns.contentprovider.CollectionContentProvider;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper.CollectionTables;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class EncounterTable {
	public static final String TABLE_NAME = CollectionTables.ENCOUNTER;
	public static final Uri CONTENT_URI = Uri.parse("content://" + CollectionContentProvider.AUTHORITY + "/" + TABLE_NAME);

	// Table Column names
    public static final String COLUMN_ID = "encounter_id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_ACCURACY = "accuracy";
    public static final String COLUMN_LOCATION = "location";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ID			+ " INTEGER PRIMARY KEY," 
					+ COLUMN_TIMESTAMP 	+ " INTEGER NOT NULL,"
				    + COLUMN_ACCURACY 	+ " REAL,"
				    + COLUMN_LOCATION	+ " INTEGER REFERENCES Location(location_id)" 
    		+ ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(EncounterTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
