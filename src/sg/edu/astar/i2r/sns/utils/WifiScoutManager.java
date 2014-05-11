package sg.edu.astar.i2r.sns.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import sg.edu.astar.i2r.sns.fragment.NetworkFragment;
import sg.edu.astar.i2r.sns.model.AccessPoint;

public class WifiScoutManager {
	
	public static List<AccessPoint> listVisibleAccessPoint;
	static Context mContext;
	private boolean connectionActive = false;
	public static WifiManager mWifiManager;
	
	public WifiScoutManager(Context context) {
		mContext = context;
		listVisibleAccessPoint = new ArrayList<AccessPoint>();
	}
	
	public static void updateListVisibleAccessPoint( List<ScanResult> listScanResult) {
		
		if(listVisibleAccessPoint == null)
			return;
		
		listVisibleAccessPoint.clear();
		
		if(listScanResult == null ) {
			NetworkFragment.updateAdapter();
			return;
		}
		
		for(ScanResult scanResult: listScanResult) {
			AccessPoint visibleAccessPoint= new AccessPoint();
			visibleAccessPoint.setSsid(scanResult.SSID);
			visibleAccessPoint.setBssid(scanResult.BSSID);
			visibleAccessPoint.setCapabilities(scanResult.capabilities);
			visibleAccessPoint.setLevel(scanResult.level);
			visibleAccessPoint.setFrequency(scanResult.frequency);
			visibleAccessPoint.setLogin_required(WifiUtils.isLoginRequired(scanResult));
			
			listVisibleAccessPoint.add(visibleAccessPoint);
		}
		
		NetworkFragment.updateAdapter();
	}
	
	private static boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	         = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	public static boolean isConnectionActive() {
		 if (isNetworkAvailable()) {
		        try {
		            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
		            urlc.setRequestProperty("User-Agent", "Test");
		            urlc.setRequestProperty("Connection", "close");
		            urlc.setConnectTimeout(1500); 
		            urlc.connect();
		            return (urlc.getResponseCode() == 200);
		        } catch (IOException e) {
		            Loger.debug("Error checking internet connection"+ e.toString());
		        }
		    } else {
		    	Loger.debug("No Network available!");
		    }
		    return false;
	}
}
