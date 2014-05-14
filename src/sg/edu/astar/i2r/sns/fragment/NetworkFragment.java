package sg.edu.astar.i2r.sns.fragment;

import java.util.ArrayList;
import java.util.List;

import sg.edu.astar.i2r.sns.R;
import sg.edu.astar.i2r.sns.adapter.AvailableNetworkAdaptor;
import sg.edu.astar.i2r.sns.model.AccessPoint;
import sg.edu.astar.i2r.sns.utils.Loger;
import sg.edu.astar.i2r.sns.utils.WifiScoutManager;
import android.app.Dialog;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ListView;

public class NetworkFragment extends Fragment {
	
	static ListView mListViewAvailableContent;
	static AvailableNetworkAdaptor mAvailableNetworkAdaptor;
	static List<AccessPoint> mListAvailableAccesspoint; 
	public View mView;
	private CheckBox freeAp;
	private CheckBox encryptAP;
	public static int filterValue = 0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mView = inflater.inflate(R.layout.network_fragment, container, false);
		mListAvailableAccesspoint = new ArrayList<AccessPoint>();
		
		mListViewAvailableContent = (ListView)mView.findViewById(R.id.list_availabe_network);
		
		
		
		freeAp = (CheckBox)  mView. findViewById(R.id.checkbox_free_AP);
		encryptAP = (CheckBox) mView.findViewById(R.id.checkbox_encrypt_AP);
		
		MyClickListener myClickListener = new MyClickListener();
		
		freeAp.setOnClickListener(myClickListener);
		encryptAP.setOnClickListener(myClickListener);	
		
		setupAdapter();
		
		return mView;
	}

	public class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.equals(freeAp) ){
				if(((CheckBox)v).isChecked() == true) {
					if(mAvailableNetworkAdaptor!=null && mAvailableNetworkAdaptor.getFilter()!=null ) {
						filterValue = 1;
						mAvailableNetworkAdaptor.getFilter().filter(null);
					}	
				} else {
					if(mAvailableNetworkAdaptor!=null && mAvailableNetworkAdaptor.getFilter()!=null ) {
						filterValue = 0;
						mAvailableNetworkAdaptor.getFilter().filter(null);
					}
				} 
			}
			
			if(v.equals(encryptAP) ){
				if(((CheckBox)v).isChecked() == true) {
					if(mAvailableNetworkAdaptor!=null && mAvailableNetworkAdaptor.getFilter()!=null ) {
						filterValue = 2;
						mAvailableNetworkAdaptor.getFilter().filter(null);
					}	
				} else {
					if(mAvailableNetworkAdaptor!=null && mAvailableNetworkAdaptor.getFilter()!=null ) {
						filterValue = 0;
						mAvailableNetworkAdaptor.getFilter().filter(null);
					}
				}
			}
			
			
		}
		
	}
	
	public void setupAdapter() {
		Resources res = getResources();
		mAvailableNetworkAdaptor = new AvailableNetworkAdaptor(getActivity(), R.layout.custom_row_network, WifiScoutManager.listVisibleAccessPoint, res);
		/*if(mListViewAvailableContent == null) {
			Loger.debug("can not get list view");
			return;
		}
		
		if(mAvailableNetworkAdaptor == null) {
			Loger.debug("can not setup Adapter");
		}*/
		
		mListViewAvailableContent.setAdapter(mAvailableNetworkAdaptor);
		//NetworkFragment.updateAdapter();
	}
	
	public static void updateAdapter() {
		if(mAvailableNetworkAdaptor!=null) {
			Loger.debug("List size**********************" + mListAvailableAccesspoint.size());
			mAvailableNetworkAdaptor.notifyDataSetChanged();
		} else {
			Loger.debug("adapter null");
		}
	}
}
