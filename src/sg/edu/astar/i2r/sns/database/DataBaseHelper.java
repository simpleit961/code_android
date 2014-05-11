package sg.edu.astar.i2r.sns.database;

import sg.edu.astar.i2r.sns.model.AccessPoint;
import sg.edu.astar.i2r.sns.model.Places;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	// Database Version
    private static final int DATABASE_VERSION = 1;
    
    // Database Name
    private static final String DATABASE_NAME = "WifiScoutDatabase";
    
    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";
    
    // Table Names
    private static final String TABLE_ACCESS_POINT = "accesspoints";
    private static final String TABLE_PLACES = "places";
    private static final String TABLE_REPORTS = "reports";
    
    // Access Points Table
    private static final String TABLE_ACCESS_POINT_ACCESS_POINT = "access_point";
    private static final String TABLE_ACCESS_POINT_BSSID = "bssid";
    private static final String TABLE_ACCESS_POINT_NETWORK_NAME = "network_name";
    private static final String TABLE_ACCESS_POINT_LOGIN_REQUIRED = "login_required";

    // Places Table
    private static final String TABLE_PLACES_NAME = "name";
    private static final String TABLE_PLACES_ADDRESS = "bssid";
    private static final String TABLE_PLACES_LATITUDE = "latitude";
    private static final String TABLE_PLACES_LONGITUDE = "longitude";
    private static final String TABLE_PLACES_CATEGORY = "category";
    private static final String TABLE_PLACES_DESCRIPTION = "description";
    private static final String TABLE_PLACES_POSTAL_CODE = "postal_code";
    
    //Reports Table
    private static final String TABLE_REPORTS_REPORT_AT = "reported_at";
    private static final String TABLE_REPORTS_REPORT_AT_DATE_TIME = "reported_at_datetime";
    private static final String TABLE_REPORTS_RATING = "rating";
    private static final String TABLE_REPORTS_DEVICE_ID = "deviceID";
    private static final String TABLE_REPORTS_PLACE_ID = "place";
    private static final String TABLE_REPORTS_ACCESS_POINT_ID = "access_point";
    
    /*private Places mPlace;
	private AccessPoint mAccessPoint;*/
    
    // Table Create Statements
    // Table Access Points
    private static final String CREATE_TABLE_ACCESS_POINT = "CREATE TABLE "
            + TABLE_ACCESS_POINT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
    		+ TABLE_ACCESS_POINT_ACCESS_POINT + " TEXT," 
    		+ TABLE_ACCESS_POINT_BSSID + " TEXT,"  
    		+ TABLE_ACCESS_POINT_NETWORK_NAME + " TEXT," 
    		+ TABLE_ACCESS_POINT_LOGIN_REQUIRED + "INTEGER ,"
    		+ KEY_CREATED_AT + " DATETIME" + ")";
    
    // Table Places
    private static final String CREATE_TABLE_PLACES = "CREATE TABLE "
            + TABLE_PLACES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
    		+ TABLE_PLACES_NAME + " TEXT," 
    		+ TABLE_PLACES_ADDRESS + " TEXT,"  
    		+ TABLE_PLACES_LATITUDE + " REAL," 
    		+ TABLE_PLACES_LATITUDE + "REAL,"
    		+ TABLE_PLACES_CATEGORY + " TEXT,"  
    		+ TABLE_PLACES_DESCRIPTION + " TEXT,"  
    		+ TABLE_PLACES_POSTAL_CODE + " TEXT,"  
    		+ KEY_CREATED_AT + " DATETIME" + ")";
    
    //Table Reports
    private static final String CREATE_TABLE_REPORT = "CREATE TABLE "
            + TABLE_REPORTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
    		+ TABLE_REPORTS_REPORT_AT + " TEXT," 
    		+ TABLE_REPORTS_REPORT_AT_DATE_TIME + " TEXT,"  
    		+ TABLE_REPORTS_RATING + " TEXT," 
    		+ TABLE_REPORTS_DEVICE_ID + "TEXT,"
    		+ TABLE_REPORTS_PLACE_ID +  "INTEGER, "
    		+ TABLE_REPORTS_ACCESS_POINT_ID + "INTEGER, "
    		+ "FOREIGN KEY(" + TABLE_REPORTS_PLACE_ID + ") REFERENCES" + TABLE_PLACES + "(" + KEY_ID + "),"
    		+ "FOREIGN KEY(" + TABLE_REPORTS_ACCESS_POINT_ID + ") REFERENCES" + TABLE_ACCESS_POINT + "(" + KEY_ID + "),"
    		+ KEY_CREATED_AT + " DATETIME" + ")";
    
	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	
}
