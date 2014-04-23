package com.petitmechant.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RssfeddActivity extends Activity implements MyListFragment.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssfeed);
    }

	@Override
	public void onRssItemSelected(String link) {
		// TODO Auto-generated method stub
		DetailFragment fragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.detailFragment);
		if(fragment != null && fragment.isInLayout()) {
			fragment.setText(link);
			Log.i("vogela::tut1", "Change message please");
		}  else { // in case of portrait mode
			Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
			intent.putExtra(DetailActivity.EXTRA_URL, link);
			startActivity(intent);
		}
			
	}
}
