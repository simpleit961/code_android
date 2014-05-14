package com.petitmechant.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.AdapterView.OnItemSelectedListener;

public class MyListFragment extends Fragment {

	private OnItemSelectedListener listerner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup containter,
			Bundle saveInstanceState) {

		View view = inflater.inflate(R.layout.fragment_rsslist_overview,
				containter, false);
		Button button = (Button) view.findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					updateDetail();
			}
		});

		return view;
	}

	public interface OnItemSelectedListener {
		public void onRssItemSelected(String link);
	}
	
	@Override 
	public void onAttach(Activity activity){
		super.onAttach(activity);
		if( activity instanceof OnItemSelectedListener) {
			listerner = (OnItemSelectedListener) activity;
		} else {
			throw new ClassCastException(activity.toString() + " must implement MyListFragment.OnItemSelectedListener");
		}
	}

	public void updateDetail() {
		String newTIme = String.valueOf(System.currentTimeMillis());
		listerner.onRssItemSelected(newTIme);
	}
}
