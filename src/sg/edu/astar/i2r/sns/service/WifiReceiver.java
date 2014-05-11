package sg.edu.astar.i2r.sns.service;

import java.util.ArrayList;
import java.util.List;

import sg.edu.astar.i2r.sns.utils.Loger;
import sg.edu.astar.i2r.sns.utils.WifiScoutManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;

public class WifiReceiver extends BroadcastReceiver {
	public static List<ScanResult> wifiList = new ArrayList<ScanResult>();
	private int number = 0;

	// This method call when number of wifi connections changed
	public void onReceive(Context c, Intent intent) {
		Loger.debug("Wifi connection changed");

		String action = intent.getAction();
		if (WifiManager.EXTRA_SUPPLICANT_CONNECTED.equals(action)) {
			Loger.debug("1:: EXTRA_SUPPLICANT_CONNECTED");
		}

		if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)) {
			Loger.debug("1A:: SUPPLICANT_CONNECTION_CHANGE_ACTION");
			/*SupplicantState supplicantState = (SupplicantState) intent
					.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			if (supplicantState == (SupplicantState.COMPLETED)) {
				 if (I) Log.i(TAG, "SUPPLICANTSTATE ---> Connected"); 
				Loger.debug("Supplicantstate --> connected");
				// do something
			}
			Loger.debug("Supplicantstate --> connected"+ supplicantState.toString());

			if (supplicantState == (SupplicantState.DISCONNECTED)) {
				// if (I) Log.i(TAG, "SUPPLICANTSTATE ---> Disconnected");
				Loger.debug("Supplicantstate --> Disconected");
				// do something
			}*/
		}
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
			Loger.debug("2:: NETWORK_STATE_CHANGED_ACTION");
		}
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
			Loger.debug("3:: WIFI_STATE_CHANGED_ACTION");
		}

		if (wifiList != null)
			wifiList.removeAll(wifiList);

		wifiList = WifiScoutManager.mWifiManager.getScanResults();

		if (wifiList != null) {
			Loger.debug("nuber--->>>>>>>." + number + "::" + wifiList.size());
		} else {
			Loger.debug("nuber--->>>>>>>." + "no wifi detected");
		}
		// WifiScoutMainActivity.mtextView.setText("Wifi changed -->" +
		// number);
		number++;

		WifiScoutManager.updateListVisibleAccessPoint(wifiList);
	}
}