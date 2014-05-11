package sg.edu.astar.i2r.sns.utils;

import sg.edu.astar.i2r.sns.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.widget.Toast;

public class MyTabsListener implements ActionBar.TabListener {
	public Fragment fragment;
	public Context context;

	public MyTabsListener(Fragment fragment, Context context) {
		this.fragment = fragment;
		this.context = context;

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		//Toast.makeText(context, "Selected!", Toast.LENGTH_SHORT).show();
		ft.replace(R.id.fragment_container, fragment);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		//Toast.makeText(context, "Unselected!", Toast.LENGTH_SHORT).show();
		ft.remove(fragment);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		//Toast.makeText(context, "Reselected!", Toast.LENGTH_SHORT).show();
	}
}