package sg.edu.astar.i2r.sns.adaptor;

import sg.edu.astar.i2r.sns.fragment.BasicMapFragment;
import sg.edu.astar.i2r.sns.fragment.ReviewFragment;
import sg.edu.astar.i2r.sns.fragment.WifiFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	@SuppressWarnings("unused")
	private static final String TAG = "TabsPagerAdapter";
	
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			return new BasicMapFragment();
		case 1:
			return new WifiFragment();
		case 2:
			return new ReviewFragment();
		}
		
		return null;
	}
}
