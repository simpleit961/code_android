package sg.edu.astar.i2r.sns.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import sg.edu.astar.i2r.sns.Activity.WifiScoutMainActivity;
import sg.edu.astar.i2r.sns.utils.Loger;
import sg.edu.astar.i2r.sns.utils.WifiScoutManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiReceiver extends BroadcastReceiver {
	public static List<ScanResult> wifiList = new ArrayList<ScanResult>();
	private int number = 0;

	// This method call when number of wifi connections changed
	public void onReceive(Context c, Intent intent) {
		Loger.debug("Wifi connection changerd");
		
		wifiList.removeAll(wifiList);
		wifiList = WifiScoutManager.mWifiManager.getScanResults();
		
		Loger.debug("nuber--->>>>>>>." + number);
		//WifiScoutMainActivity.mtextView.setText("Wifi changed -->" + number);
		number ++;
		WifiScoutManager.updateListVisibleAccessPoint(wifiList);
	}
}