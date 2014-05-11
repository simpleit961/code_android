package sg.edu.astar.i2r.sns.Activity;

import java.util.logging.Logger;

import sg.edu.astar.i2r.sns.R;
import sg.edu.astar.i2r.sns.fragment.HistoryFragment;
import sg.edu.astar.i2r.sns.fragment.MapFragment;
import sg.edu.astar.i2r.sns.fragment.NetworkFragment;
import sg.edu.astar.i2r.sns.global.Global;
import sg.edu.astar.i2r.sns.utils.Loger;
import sg.edu.astar.i2r.sns.utils.MyTabsListener;
import sg.edu.astar.i2r.sns.utils.WifiScoutManager;
import sg.edu.astar.i2r.sns.service.WifiConnectionService;
import sg.edu.astar.i2r.sns.service.WifiReceiver;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar;

public class WifiScoutMainActivity extends Activity {
	private SharedPreferences mPreferences;
	private boolean mIsfirstLaunch;
	private WifiReceiver mWifiReceiver;
	private WifiScoutManager mWifiScoutManager;

	// fragment
	private NetworkFragment mNetworkFragment;
	private MapFragment mMapFragment;
	private HistoryFragment mHistoryFragment;

	public static TextView mtextView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//mtextView = (TextView) findViewById(R.id.maintextview);
		mWifiScoutManager = new WifiScoutManager(this);

		setupUIActionBar();

		mPreferences = this.getSharedPreferences(Global.SHARED_PREFERENCE_NAME,
				MODE_PRIVATE);
		if (isFirstLaunch() == true) {
			Loger.debug("This is first launch, data base will be download");
			openBoxToDownloadDataBase();
		}

		/*if (WifiScoutManager.isConnectionActive())
			mtextView.append("::" + "connection active");
		else
			mtextView.append("::" + "connection in active");
*/
		startService();
		mWifiReceiver = new WifiReceiver();
	}

	@SuppressLint("NewApi")
	public void setupUIActionBar() {
		// ActionBar
		ActionBar actionbar = getActionBar();

		mMapFragment = new MapFragment();
		mNetworkFragment = new NetworkFragment();
		mHistoryFragment = new HistoryFragment();

		if (actionbar != null) {
			actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			// create new tab
			ActionBar.Tab mMapTab = actionbar.newTab().setText("Map");
			ActionBar.Tab mNetworkTab = actionbar.newTab().setText("WifiConnect");
			ActionBar.Tab mHistoryTab = actionbar.newTab().setText("History");

			// bind the fragments to the tabs - set up tabListeners for each tab
			mMapTab.setTabListener(new MyTabsListener(mMapFragment,getApplicationContext()));
			mNetworkTab.setTabListener(new MyTabsListener(mNetworkFragment,getApplicationContext()));
			mHistoryTab.setTabListener(new MyTabsListener(mHistoryFragment,getApplicationContext()));
			
			// add tab
			actionbar.addTab(mMapTab);
			actionbar.addTab(mNetworkTab);
			actionbar.addTab(mHistoryTab);
		} else {
			Loger.debug("action bar is null");
		}
	}

	public void startService() {
		startService(new Intent(this, WifiConnectionService.class));
	}

	public void openBoxToDownloadDataBase() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.custom_dialog);
		Button buttonOk = (Button) dialog.findViewById(R.id.button_ok);
		buttonOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		Loger.debug("On Resume");
		registerReceiver(mWifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		/* | WifiManager.WIFI_STATE_CHANGED_ACTION */

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mWifiReceiver);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Toast.makeText(this,"onSaveInstanceState: tab is"+ getActionBar().getSelectedNavigationIndex(),Toast.LENGTH_SHORT).show();
		outState.putInt(Global.TAB_KEY_INDEX, getActionBar().getSelectedNavigationIndex());
	}

	/*
	 * check if it is the first launch May be show a overlay layout Download
	 * data base
	 */
	public boolean isFirstLaunch() {

		mIsfirstLaunch = mPreferences.getBoolean("IS_FIRST_LAUNCH", true);
		if (mIsfirstLaunch == true) {
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putBoolean("IS_FIRST_LAUNCH", false);
			editor.commit();
			return true;
		}
		return false;
	}
}
