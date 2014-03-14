package sg.edu.astar.i2r.sns.fragment;

import sg.edu.astar.i2r.sns.activity.PlaceActivity;
import sg.edu.astar.i2r.sns.collectiondatabase.CollectionDatabaseHelper;
import sg.edu.astar.i2r.sns.psense.R;
import sg.edu.astar.i2r.sns.utility.WifiUtils;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewFragment extends Fragment {
    private static final int PICK_PLACE_REQUEST = 0;

	@SuppressWarnings("unused")
	private static final String TAG = "ReviewFragment";

    private CollectionDatabaseHelper databaseHelper;

    private View view;
    private Button submitButton;
	private Button suggestionButton;
	private EditText placeEditText;
	private EditText addressEditText;
	private EditText floorEditText;
	private EditText roomEditText;
	
	private TextView currentNetworkTextView;
	
	private WifiManager wifiManager;
	private WifiInfo currentNetwork;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_review, container, false);
//		Log.d(TAG, "onCreateView");

		databaseHelper = new CollectionDatabaseHelper(getActivity());
		wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

		currentNetworkTextView = (TextView) view.findViewById(R.id.currentNetworkTextView);
		placeEditText = (EditText) view.findViewById(R.id.placeEditText);
		addressEditText = (EditText) view.findViewById(R.id.addressEditText);
		floorEditText = (EditText) view.findViewById(R.id.floorEditText);
		roomEditText = (EditText) view.findViewById(R.id.roomEditText);

		submitButton = (Button) view.findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String place, address, floor, room;
				int speed = 0;
				
				floor = floorEditText.getText().toString();
				address = addressEditText.getText().toString();
				place = placeEditText.getText().toString();
				room = roomEditText.getText().toString();
				
				
				String ssid = WifiUtils.removeQuotations(currentNetwork.getSSID());
				String bssid = currentNetwork.getBSSID();
				Long timestamp = System.currentTimeMillis();
				
				// Check if currently connected to a network
				if (currentNetwork == null || bssid == null) {
					Toast.makeText(getActivity(), "Not currently connected to a network", Toast.LENGTH_LONG).show();
					return;
				}
				
				// Record place information
				databaseHelper.tagAccessPointWithPlace(timestamp, ssid, bssid, place, address, floor, room);

				// TODO record user feedback (speed) as well
				Toast.makeText(getActivity(), "Review Submitted", Toast.LENGTH_LONG).show();
			}
		});

		suggestionButton = (Button) view.findViewById(R.id.suggestionButton);
		suggestionButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), PlaceActivity.class);
				startActivityForResult(i, PICK_PLACE_REQUEST);
			}
		});
		
		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (data == null || requestCode != PICK_PLACE_REQUEST) return;
		
		String address = data.getStringExtra("address");
		String place = data.getStringExtra("name");
		
		addressEditText.setText(address);
		placeEditText.setText(place);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		MenuItem item = menu.findItem(R.id.mainSearch);
		item.setVisible(false);
		onPrepareOptionsMenu(menu);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		// Updates the textview showing what network the user is connected to.
		// Should actually update more frequently - listen for network state change and update
		// for every new connection instead
		currentNetwork = wifiManager.getConnectionInfo();
		currentNetworkTextView.setText(WifiUtils.removeQuotations(currentNetwork.getSSID()));
	}
}
