package sg.edu.astar.i2r.sns.collectiondatabase;

import sg.edu.astar.i2r.sns.contentprovider.CollectionContentProvider;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper.CollectionTables;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class EncounterWithAccessPointTable {
	public static final String TABLE_NAME = CollectionTables.ENCOUNTER_WITH_ACCESS_POINT;
	public static final Uri CONTENT_URI = Uri.parse("content://" + CollectionContentProvider.AUTHORITY + "/" + TABLE_NAME);

	// AccessPoint Table Column names
	public static final String COLUMN_ENCOUNTER = "encounter";
	public static final String COLUMN_ACCESS_POINT = "access_point";
	public static final String COLUMN_SIGNAL_LEVEL = "signal_level";

	// 	Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ENCOUNTER 		+ " INTEGER REFERENCES Encounter(encounter_id),"	// What does adding NOT NULL mean?
					+ COLUMN_ACCESS_POINT 	+ " INTEGER REFERENCES Access_point(_id),"			// It doesn't enforce total participation, does it?
					+ COLUMN_SIGNAL_LEVEL 	+ " INTEGER,"
					+ "PRIMARY KEY(" + COLUMN_ENCOUNTER + ", " + COLUMN_ACCESS_POINT + ")" 
					+ ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(EncounterWithAccessPointTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
