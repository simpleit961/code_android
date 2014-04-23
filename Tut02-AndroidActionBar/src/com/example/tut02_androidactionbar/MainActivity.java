package com.example.tut02_androidactionbar;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {

	public static boolean isActionbarHiden = false;
	public static ActionBar actionbar;

	private MenuItem menuitem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// get the action bar
		actionbar = getActionBar();
		// actionbar.setSubtitle("ActionBar Subtitle");
		// actionbar.setTitle("ActionBar Title");
		// actionbar.setCustomView(R.layout.actionbar_view);

		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);

		actionbar.setHomeButtonEnabled(true);
		// search.setOnEditorActionListener(new )

		// getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		// to make application full screen
		// getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

		// hide some information : about som card for example
		// getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

		// getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_LAYOUT_FLAGS);

		/*
		 * if (savedInstanceState == null) {
		 * getFragmentManager().beginTransaction() .add(R.id.container, new
		 * PlaceholderFragment()).commit(); }
		 */

	}

	public void onTextClick(View view) {
		if (isActionbarHiden) {
			actionbar.show();
			isActionbarHiden = false;
			Toast.makeText(this, "Hide Action Bar", Toast.LENGTH_SHORT).show();
		} else {
			isActionbarHiden = true;
			actionbar.hide();
			Toast.makeText(this, "Show Action Bar", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
		case android.R.id.home:
			// Toast.makeText(this, "Home Button Selected",
			// Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			
			break;

		case R.id.menu_load:

			menuitem = item;
			menuitem.setActionView(R.layout.progressbar);
			menuitem.expandActionView();
			TestTask task = new TestTask();
			task.execute("test");

			break;

		case R.id.action_settings:
			Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	/*
	 * public static class PlaceholderFragment extends Fragment {
	 * 
	 * public PlaceholderFragment() { }
	 * 
	 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
	 * container, Bundle savedInstanceState) { View rootView =
	 * inflater.inflate(R.layout.fragment_main, container, false); return
	 * rootView; } }
	 */
	private class TestTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// Simulate something long running
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			menuitem.collapseActionView();
			menuitem.setActionView(null);
		}
	};

}
