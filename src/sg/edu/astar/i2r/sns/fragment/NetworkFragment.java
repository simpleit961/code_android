package sg.edu.astar.i2r.sns.fragment;

import java.util.ArrayList;
import java.util.List;

import sg.edu.astar.i2r.sns.R;
import sg.edu.astar.i2r.sns.adapter.AvailableNetworkAdaptor;
import sg.edu.astar.i2r.sns.model.AccessPoint;
import sg.edu.astar.i2r.sns.utils.Loger;
import sg.edu.astar.i2r.sns.utils.WifiScoutManager;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ListView;

public class NetworkFragment extends Fragment {
	
	static ListView mListViewAvailableContent;
	static AvailableNetworkAdaptor mAvailableNetworkAdaptor;
	static List<AccessPoint> mListAvailableAccesspoint; 
	public View mView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mView = inflater.inflate(R.layout.network_fragment, container, false);
		mListAvailableAccesspoint = new ArrayList<AccessPoint>();
		
		mListViewAvailableContent = (ListView)mView.findViewById(R.id.list_availabe_network);
		setupAdapter();
		return mView;
	}

	public void setupAdapter() {
		Resources res = getResources();
		mAvailableNetworkAdaptor = new AvailableNetworkAdaptor(getActivity(), R.layout.custom_row_network, WifiScoutManager.listVisibleAccessPoint);
		
		/*if(mListViewAvailableContent == null) {
			Loger.debug("can not get list view");
			return;
		}
		
		if(mAvailableNetworkAdaptor == null) {
			Loger.debug("can not setup Adapter");
		}*/
		
		mListViewAvailableContent.setAdapter(mAvailableNetworkAdaptor);
	}
}
