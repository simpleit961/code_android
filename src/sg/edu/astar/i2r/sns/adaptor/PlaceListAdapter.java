package sg.edu.astar.i2r.sns.adaptor;

import java.util.List;

import sg.edu.astar.i2r.sns.model.Place;
import sg.edu.astar.i2r.sns.psense.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter used to populate the nearby places list
 */
public class PlaceListAdapter extends ArrayAdapter<Place> {
	@SuppressWarnings("unused")
	private static final String TAG = PlaceListAdapter.class.getSimpleName();
	private List<Place> places;
	
	public PlaceListAdapter(Context context, int textViewResourceId, List<Place> places) {
		super(context, textViewResourceId, places);
		this.places = places;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_place, null);
		}
		
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
		TextView locationTextView = (TextView) view.findViewById(R.id.locationTextView);

		icon.setImageBitmap(places.get(position).getBitmap());
		nameTextView.setText(places.get(position).getName());
		locationTextView.setText(places.get(position).getVicinity());

		return view;
	}
	
}
