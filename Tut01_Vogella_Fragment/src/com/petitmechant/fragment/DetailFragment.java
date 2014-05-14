package com.petitmechant.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment{
	
	@Override
	public View onCreateView(LayoutInflater infalter, ViewGroup container,
			Bundle saveInstanceState) {
		View view = infalter.inflate(R.layout.fragment_rssitem_detail, container, false);
		return view;
	}

	public void setText(String item) {
		TextView view = (TextView) getView().findViewById(R.id.detailsText);
		view.setText(item);
	}
}
