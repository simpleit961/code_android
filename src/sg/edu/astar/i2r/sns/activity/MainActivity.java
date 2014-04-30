package sg.edu.astar.i2r.sns.activity;

import sg.edu.astar.i2r.sns.adaptor.TabsPagerAdapter;
import sg.edu.astar.i2r.sns.psense.R;
import sg.edu.astar.i2r.sns.service.DataCollectionService;
import sg.edu.astar.i2r.sns.utility.Constant;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getSimpleName();

	private String[] tabs = { "Search", "Nearby", "Review" };
	private TabsPagerAdapter tabsAdapter;
	private ActionBar actionBar;
	private ViewPager mPager;

	private Menu mOptionsMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showOverLay();
		getActionBar().setDisplayHomeAsUpEnabled(false);

		// Swipe View
		tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOffscreenPageLimit(Constant.NUM_OFF_SCREEN_FRAGMENT); // Number
																		// of
																		// fragment
																		// to
																		// store
																		// in
																		// memory
		mPager.setAdapter(tabsAdapter);

		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		actionBar.setSelectedNavigationItem(Constant.LIST_TAB_POSITION); // Set
																			// default
																			// scrren
																			// to
																			// wifi
																			// list
	}

	public void initialiseDataCollectionService() {
		Intent intent = new Intent(this, DataCollectionService.class);
		startService(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
		initialiseDataCollectionService();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { MenuInflater
	 * menuInflater = getMenuInflater();
	 * 
	 * menuInflater.inflate(R.menu.main, menu); mOptionsMenu = menu;
	 * 
	 * MenuItem searchItem = (MenuItem) mOptionsMenu.findItem(R.id.mainSearch);
	 * SearchView searchView = (SearchView) searchItem.getActionView();
	 * searchView.setQueryHint("Trip planning( )");
	 * searchView.setIconifiedByDefault(false);
	 * 
	 * 
	 * return true; }
	 */

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		int position = tab.getPosition();
		mPager.setCurrentItem(position);

		if (mOptionsMenu == null)
			return;

		MenuItem searchItem = (MenuItem) mOptionsMenu.findItem(R.id.mainSearch);
		final SearchView searchView = (SearchView) searchItem.getActionView();

		InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);

		if (!searchView.getQuery().toString().isEmpty()
				&& (position == Constant.LIST_TAB_POSITION || position == Constant.MAP_TAB_POSITION)) {
			Log.d(TAG, "null");
			// imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
			// searchView.setIconified(false);
		}

		if (position != Constant.REVIEW_TAB_POSITION)
			return;

		// Close the soft keyboard
		imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

		// Review tab is selected so hide the search menu
		MenuItem item = mOptionsMenu.findItem(R.id.mainSearch);
		item.setVisible(false);

		onPrepareOptionsMenu(mOptionsMenu);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		int position = tab.getPosition();

		if (position != Constant.REVIEW_TAB_POSITION)
			return;
		if (mOptionsMenu == null)
			return;

		// Display the search menu once user leaves the review fragment
		MenuItem item = mOptionsMenu.findItem(R.id.mainSearch);
		item.setVisible(true);

		onPrepareOptionsMenu(mOptionsMenu);
	}

	// start gagogg: Add this code to Press Back two time to exit.
	boolean doubleBackToExitPressedOnce = false;

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit",
				Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	// end: gagogg

	private void showOverLay() {

		final Dialog dialog = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.overlay_view);
		LinearLayout layout = (LinearLayout) dialog
				.findViewById(R.id.overlayLayout);

		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				dialog.dismiss();

			}
		});

		dialog.show();

	}
}