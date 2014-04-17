package sg.edu.astar.i2r.sns.collectiondatabase;

import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper.CollectionTables;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserRatesAccessPointTable {
	public static final String TABLE_NAME = CollectionTables.USER_RATES_ACCESS_POINT;

	// Table Column names
    public static final String COLUMN_USER = "user"; 
    public static final String COLUMN_ACCESS_POINT = "access_point"; 
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_TIMESTAMP = "timestamp"; 
	
	// 	Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_USER 				+ " INTEGER REFERENCES User(user_id),"
					+ COLUMN_ACCESS_POINT   	+ " INTEGER REFERENCES Access_point(_id),"
					+ COLUMN_SPEED 				+ " INTEGER,"
					+ COLUMN_TIMESTAMP			+ " INTEGER,"
					+ "PRIMARY KEY(" + COLUMN_USER + ", " + COLUMN_ACCESS_POINT + ", " + COLUMN_TIMESTAMP + ")"
    		+ ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(UserRatesAccessPointTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
