package sg.edu.astar.i2r.sns.adaptor;

import java.util.List;

import sg.edu.astar.i2r.sns.model.NearbyContent;
import sg.edu.astar.i2r.sns.psense.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NearbyListAdapter extends ArrayAdapter<NearbyContent> {
	@SuppressWarnings("unused")
	private static final String TAG = "NearbyListAdapter";
	private List<NearbyContent> list;

	static class ViewHolder {
		TextView ssidTextView, addressTextView, ratedSpeedTextView;
		TextView popularityTextView, loginTextView, distanceTextView;
		ImageView ratedSpeedImageView, popularityImageView; 
		ImageView loginImageView, distanceImageView;
	}
	
	public NearbyListAdapter(Context context, int textViewResourceId, List<NearbyContent> nearbyAccessPointList) {
		super(context, textViewResourceId, nearbyAccessPointList);
		list = nearbyAccessPointList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int ratedSpeed; 
		boolean popular, login;
		
		if (view == null) {
			ViewHolder viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_nearby, parent, false);
			
			viewHolder.ssidTextView = (TextView) view.findViewById(R.id.ssidTextView);
			viewHolder.addressTextView = (TextView) view.findViewById(R.id.addressTextView);
			viewHolder.ratedSpeedTextView = (TextView) view.findViewById(R.id.ratedSpeedTextView);
			viewHolder.popularityTextView = (TextView) view.findViewById(R.id.popularityTextView);
			viewHolder.loginTextView = (TextView) view.findViewById(R.id.loginTextView);
			viewHolder.distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
			viewHolder.ratedSpeedImageView = (ImageView) view.findViewById(R.id.ratedSpeedImageView);
			viewHolder.popularityImageView = (ImageView) view.findViewById(R.id.popularityImageView);
			viewHolder.loginImageView = (ImageView) view.findViewById(R.id.loginImageView);
			viewHolder.distanceImageView = (ImageView) view.findViewById(R.id.distanceImageView);
			
			view.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) view.getTag();
		
		NearbyContent content = list.get(position);
		if (content == null)
			return view;
		
		holder.ssidTextView.setText(content.getSsid());
		holder.addressTextView.setText(content.getAddress());
		
		// Speed
		ratedSpeed = content.getRatedSpeed();
		switch(ratedSpeed) {
		case 0:
			holder.ratedSpeedTextView.setVisibility(View.VISIBLE);
			holder.ratedSpeedImageView.setVisibility(View.VISIBLE);
			holder.ratedSpeedTextView.setText("Fast");
			break;
		case 1:
			holder.ratedSpeedTextView.setVisibility(View.VISIBLE);
			holder.ratedSpeedImageView.setVisibility(View.VISIBLE);
			holder.ratedSpeedTextView.setText("Medium");
			break;
		case 2:
			holder.ratedSpeedTextView.setVisibility(View.VISIBLE);
			holder.ratedSpeedImageView.setVisibility(View.VISIBLE);
			holder.ratedSpeedTextView.setText("Slow");
			break;
		default:
			holder.ratedSpeedImageView.setVisibility(View.INVISIBLE);
			holder.ratedSpeedTextView.setVisibility(View.INVISIBLE);
			break;
		}
		
		// Popularity
		popular = content.isPopular();
		if (popular) {
			holder.popularityImageView.setVisibility(View.VISIBLE);
			holder.popularityTextView.setVisibility(View.VISIBLE);
		} else {
			holder.popularityImageView.setVisibility(View.INVISIBLE);
			holder.popularityTextView.setVisibility(View.INVISIBLE);
		}
		
		// Login
		login = content.hasLogin();
		if (login) {
			holder.loginImageView.setVisibility(View.VISIBLE);
			holder.loginTextView.setVisibility(View.VISIBLE);
		} else {
			holder.loginImageView.setVisibility(View.INVISIBLE);
			holder.loginTextView.setVisibility(View.INVISIBLE);
		}
		
		holder.distanceTextView.setText(content.getDistance() + "m");
		
		return view;
	}
	
}
