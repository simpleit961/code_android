package sg.edu.astar.i2r.sns.collectiondatabase;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class UserTable {
	public static final String TABLE_NAME = "Users";

	// Table Column names
    public static final String COLUMN_ID = "user_id";
    public static final String COLUMN_EMAIL_ADDRESS = "email_address"; 	// Could be another unqiue identifier
	
	// 	Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE " + TABLE_NAME + "("
					+ COLUMN_ID 			+ " INTEGER PRIMARY KEY," 	
					+ COLUMN_EMAIL_ADDRESS 	+ " TEXT NOT NULL"
    		+ ")";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(UserTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}
