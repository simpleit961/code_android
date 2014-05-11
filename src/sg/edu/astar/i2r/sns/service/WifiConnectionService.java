package sg.edu.astar.i2r.sns.service;

import sg.edu.astar.i2r.sns.utils.Loger;
import sg.edu.astar.i2r.sns.utils.WifiScoutManager;
import android.R.integer;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

public class WifiConnectionService extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		//super.onStartCommand(intent, flags, startId)
		WifiScoutManager.mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// Check for wifi is disabled
		if (WifiScoutManager.mWifiManager.isWifiEnabled() == false) {
			// If wifi disabled then enable it
			Toast.makeText(getApplicationContext(),
					"wifi is disabled..making it enabled", Toast.LENGTH_LONG)
					.show();
			//WifiScoutManager.mWifiManager.setWifiEnabled(true);
		}
		Loger.debug("Enable wifi");
	}
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		WifiScoutManager.mWifiManager.startScan();
		Loger.debug("start scan");
		return Service.START_STICKY;
	}
}
