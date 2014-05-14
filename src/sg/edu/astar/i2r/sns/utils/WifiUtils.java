package sg.edu.astar.i2r.sns.utils;

import java.util.List;

import sg.edu.astar.i2r.sns.model.AccessPoint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

public class WifiUtils {
	public WifiManager mWifiManager;
	
	public void init() {
	}
	
	public static boolean isLoginRequired(ScanResult scanResult) {
		
		String capabilities = scanResult.capabilities;
		String[] encryptType = {"WPA2", "EAP", "PSK", "CCMP", "WPA"};  //ESS

		for(String i : encryptType) {
			if(capabilities.contains(i)) {
				return true;
			}
		}
		return false;
	}
	
	// reference link
	//http://stackoverflow.com/questions/8818290/how-to-connect-to-a-specific-wifi-network-in-android-programmatically
	/**
	 * Connect to specific network
	 * @param accesspoint
	 */
	public static void connectToSpecificNetwork(AccessPoint accesspoint) {
		String networkSSID = accesspoint.getSsid();
		String networkPass = "pass";

		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"" + accesspoint.getSsid() + "\"";   // Please note the quotes. String should contain ssid in quotes
		
		/*conf.wepKeys[0] = "\"" + networkPass + "\""; 
		conf.wepTxKeyIndex = 0;
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40); */
		
		//For WPA network you need to add passphrase like this:
		//conf.preSharedKey = "\""+ networkPass +"\"";
		
		//For Open network you need to do this:
		conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		
		//Then, you need to add it to Android wifi manager settings:
		WifiScoutManager.mWifiManager.addNetwork(conf);
		
		//UPD: In case of WEP, if your password is in hex, you do not need to surround it with quotes. 
		
		List<WifiConfiguration> list = WifiScoutManager.mWifiManager.getConfiguredNetworks();
		for( WifiConfiguration i : list ) {
		    if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
		    	WifiScoutManager.mWifiManager.disconnect();
		    	WifiScoutManager.mWifiManager.enableNetwork(i.networkId, true);
		    	WifiScoutManager.mWifiManager.reconnect();               
		         break;
		    }           
		 }
	}
}
