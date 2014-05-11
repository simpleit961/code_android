package sg.edu.astar.i2r.sns.utils;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiUtils {
	public WifiManager mWifiManager;
	
	public void init() {
	}
	
	public static boolean isLoginRequired(ScanResult scanResult) {
		
		String capabilities = scanResult.capabilities;
		String[] encryptType = {"ESS", "WPA2", "EAP", "PSK", "CCMP", "WPA"};

		for(String i : encryptType) {
			if(capabilities.contains(i)) {
				return true;
			}
		}
		return false;
	}
}
