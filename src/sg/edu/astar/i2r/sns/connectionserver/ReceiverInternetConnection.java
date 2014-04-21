package sg.edu.astar.i2r.sns.connectionserver;

import java.util.HashMap;

import sg.edu.astar.i2r.sns.collectiondatabase.ReportTable;
import sg.edu.astar.i2r.sns.contentprovider.CollectionContentProvider;
import sg.edu.astar.i2r.sns.contentprovider.DisplayContentProvider;
import sg.edu.astar.i2r.sns.displaydatabase.AccessPointTable;
import sg.edu.astar.i2r.sns.displaydatabase.DisplayDatabaseHelper;
import sg.edu.astar.i2r.sns.displaydatabase.PlaceTable;
import sg.edu.astar.i2r.sns.utility.Logger;
import sg.edu.astar.i2r.sns.utility.WifiUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRouter.VolumeCallback;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class ReceiverInternetConnection extends BroadcastReceiver {

	public static HashMap<String,String> listBssid = new HashMap<String,String>();
	private Logger log = new Logger();
	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		ConnectivityManager cm =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE) ;
		if (cm == null)
			return;
		if (cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected()) {
			/// connected
			
			Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
			/*if (WifiUtils.login_web_required()) {
				listBssid.put(WifiUtils.getCurrentSsid(context), "true");
				 log.addRecordToLog(" "+WifiUtils.getCurrentSsid(context)+ " : "+true);
				//Toast.makeText(context, "Login web required", Toast.LENGTH_SHORT).show();
			}
			else
			{	//Toast.makeText(context, "Login web NON required", Toast.LENGTH_SHORT).show();
			    listBssid.put(WifiUtils.getCurrentSsid(context), "false");
			    log.addRecordToLog(" "+WifiUtils.getCurrentSsid(context)+ " : "+false);
			}*/
			//WifiUtils.getCurrentSsid(context);
			//sendInfoToServer(context);
		} else {
			// not connected
		//	Toast.makeText(context, "Not Connected", Toast.LENGTH_LONG).show();
		/*	if (WifiUtils.login_web_required()) {
				listBssid.put(WifiUtils.getCurrentSsid(context), "true");
				 log.addRecordToLog(" "+WifiUtils.getCurrentSsid(context)+ " : "+true);
				//Toast.makeText(context, "Login web required", Toast.LENGTH_SHORT).show();
			}else
				//Toast.makeText(context, "Login web NON required", Toast.LENGTH_SHORT).show();
			    listBssid.put(WifiUtils.getCurrentSsid(context), "false");
			    log.addRecordToLog(" "+WifiUtils.getCurrentSsid(context)+ " : "+false);*/
			  // sendInfoToServer(context);
			//readInfoDatabase(context);
			 Uri uri = CollectionContentProvider.CONTENT_URI_TEMPREPORT;
			int rows = context.getContentResolver().delete(uri, null, null);
			Toast.makeText(context, "Number of rows deleted = "+rows, Toast.LENGTH_LONG).show();

		}
			
	}
	public void sendInfoToServer(Context context) {
		
		 Uri uri = CollectionContentProvider.CONTENT_URI_REPORT;
		
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		
		if (cursor == null)
			return;
		
		if (!cursor.moveToFirst()) {
			cursor.close();
			return;
		}
		int nbRows = cursor.getCount();
		
		for (int i=0; i< nbRows; i++) {
			String id = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_ID));
			String ssid = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_SSID));
			String bssid = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_BSSID));
			String quality = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_QUALITY));
			String place = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_NAME));
			String address = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_ADDRESS));
			String floor = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_FLOOR));
			String room = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_ROOM));
			String login = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LOGIN));
			// Send to server
			log.addRecordToLog("id="+id+" ssid="+ssid+" bssid="+bssid+" rate="+quality+" place="+place
					+" address="+address+ " floor="+floor+ " room="+room+" nbRows="+nbRows+ " Login ="+login);
			cursor.moveToNext();
		}
		// delete all rows
		//context.getContentResolver().delete(uri,null, null);
		
		/*Uri uri = CollectionContentProvider.CONTENT_URI_ACCESS_POINT;
		
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		
		if (cursor == null)
			return;
		
		if (!cursor.moveToFirst()) {
			cursor.close();
			return;
		}
		Logger log = new Logger();
		log.addRecordToLog("All access points");
		int nbRows = cursor.getCount();
		log.addRecordToLog("nb lines ="+nbRows);
		for (int i=0; i< nbRows; i++) {
			String id = cursor.getString(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_ID));
			String ssid = cursor.getString(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_SSID));
			String bssid = cursor.getString(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_BSSID));
			//String login = cursor.getString(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_LOGIN));
			
			// Send to server
			log.addRecordToLog("id="+id+" ssid="+ssid+" bssid="+bssid+" login=");
			cursor.moveToNext();
		}*/
		
	}
	public void readInfoDatabase(Context context) {
		Uri uri = CollectionContentProvider.CONTENT_URI_USER;
		String[] projection = {
				AccessPointTable.COLUMN_ID, AccessPointTable.COLUMN_SSID,
				AccessPointTable.COLUMN_BSSID, AccessPointTable.COLUMN_POPULARITY,
				AccessPointTable.COLUMN_RATED_SPEED, AccessPointTable.COLUMN_LOGIN,
				PlaceTable.COLUMN_NAME, PlaceTable.COLUMN_ADDRESS, 
				PlaceTable.COLUMN_FLOOR, PlaceTable.COLUMN_ROOM};
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		
		if (cursor == null)
			return;
		
		if (!cursor.moveToFirst()) {
			cursor.close();
			return;
		}
		int nbRows = cursor.getCount();
		for (int i=0; i< nbRows; i++) {
			String ssid = cursor.getString(cursor.getColumnIndexOrThrow(AccessPointTable.COLUMN_SSID));
			String place = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_NAME));
			String address = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_ADDRESS));
			String floor = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_FLOOR));
			String room = cursor.getString(cursor.getColumnIndexOrThrow(PlaceTable.COLUMN_ROOM));
			// send informations to the server
			cursor.moveToNext();
		}
		
	}
	public void requestInfoDatabase() {
		// send a request to server and parse the response
		// save data to the display database
	}
}
