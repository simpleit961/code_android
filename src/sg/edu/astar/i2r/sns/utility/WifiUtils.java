package sg.edu.astar.i2r.sns.utility;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiUtils {

	public static String getCurrentSsid(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		String ssid = null;

		if (!networkInfo.isConnected())
			return null;

		final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		final WifiInfo connectionInfo = wifiManager.getConnectionInfo();

		if (connectionInfo != null && !connectionInfo.getSSID().isEmpty())
			ssid = connectionInfo.getSSID();

		ssid = removeQuotations(ssid);
		return ssid;
	}
	
	public static List<ScanResult> getValidList(List<ScanResult> wifiList) {
		List<ScanResult> validList = new ArrayList<ScanResult>();
	
		for (ScanResult result: wifiList) { 
			if (isRequired(result)) {
				validList.add(result);
			}
		}
		
		return validList;
	}

	/**
	 * Filter out APs that has empty SSID, has security or is an adhoc network
	 * @param result
	 * @return true if it is required
	 */
	public static boolean isRequired(ScanResult result) {
		final String cap = result.capabilities;
		 final String[] securityModes = {"WEP", "PSK", "EAP"};
	
	    if (result.SSID.isEmpty() || result.BSSID.isEmpty()
	    	|| result.capabilities.contains("-EAP-")		// enterprise secured
	    	|| result.capabilities.contains("[IBSS]")) {	// adhoc network -- Mobile created APs
	    	return false;
	    }
	
	    for (int i = 0; i < securityModes.length; i++) {
	    	if (cap.contains(securityModes[i])) 
	    		return false;
	    }
	    return true;
	}
	
	public static boolean login_web_required() {
		 URL url =null;
		 HttpURLConnection urlConnection = null;
		  try {
		   url = new URL("http://www.android.com/");
		   urlConnection = (HttpURLConnection) url.openConnection();
		   URL u = urlConnection.getURL();
		   String s = u.getHost();
		   String a =s;
		     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
		     if (!url.getHost().equals(urlConnection.getURL().getHost())) {
		       // we were redirected! Kick the user out to the browser to sign on?\
		    	 s = u.getHost();
		    	 return true;
		    	 }
		   } catch (IOException e) {
			   
		   } finally {
		     urlConnection.disconnect();
		   }
		  
		   return false;
	}

	
	public static String removeQuotations(String ssid) {
		return ssid.substring(1, ssid.length() - 1);
	}
}
