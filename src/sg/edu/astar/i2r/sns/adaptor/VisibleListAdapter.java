package sg.edu.astar.i2r.sns.adaptor;

import java.util.List;

import sg.edu.astar.i2r.sns.model.VisibleContent;
import sg.edu.astar.i2r.sns.psense.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VisibleListAdapter extends ArrayAdapter<VisibleContent>{
	private List<VisibleContent> list;
	private Context context;
	
	static class ViewHolder {
		TextView ssidTextView, addressTextView, ratedSpeedTextView;
		TextView popularityTextView, loginTextView;
		ImageView ratedSpeedImageView, popularityImageView; 
		ImageView wifiBackImage, loginImageView, signalStrengthImageView;
	}
	
	public VisibleListAdapter(Context context, int textViewResourceId, List<VisibleContent> visibleAccessPointList) {
		super(context, textViewResourceId, visibleAccessPointList);
		this.context = context;
		list = visibleAccessPointList;							
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		int signalLevel, ratedSpeed; 
		boolean popular, login;
		
		if (view == null) {
			ViewHolder viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_visible, parent, false);
			
			viewHolder.ssidTextView = (TextView) view.findViewById(R.id.ssidTextView);
			viewHolder.addressTextView = (TextView) view.findViewById(R.id.addressTextView);
			viewHolder.ratedSpeedTextView = (TextView) view.findViewById(R.id.ratedSpeedTextView);
			viewHolder.popularityTextView = (TextView) view.findViewById(R.id.popularityTextView);
			viewHolder.loginTextView = (TextView) view.findViewById(R.id.loginTextView);
			viewHolder.ratedSpeedImageView = (ImageView) view.findViewById(R.id.ratedSpeedImageView);
			viewHolder.popularityImageView = (ImageView) view.findViewById(R.id.popularityImageView);
			viewHolder.loginImageView = (ImageView) view.findViewById(R.id.loginImageView);
			viewHolder.signalStrengthImageView = (ImageView) view.findViewById(R.id.signalStrengthImageView);		
			viewHolder.wifiBackImage = (ImageView) view.findViewById(R.id.wifiBackImageView);
			
			view.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) view.getTag();
		
		VisibleContent content = list.get(position);
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
			holder.ratedSpeedImageView.setBackgroundColor(Color.GREEN);
			holder.ratedSpeedTextView.setText("Fast ");
			break;
		case 1:
			holder.ratedSpeedTextView.setVisibility(View.VISIBLE);
			holder.ratedSpeedImageView.setVisibility(View.VISIBLE);
			holder.ratedSpeedImageView.setBackgroundColor(Color.YELLOW);
			holder.ratedSpeedTextView.setText("Medium");
			break;
		case 2:
			holder.ratedSpeedTextView.setVisibility(View.VISIBLE);
			holder.ratedSpeedImageView.setVisibility(View.VISIBLE);
			holder.ratedSpeedImageView.setBackgroundColor(Color.GRAY);
			holder.ratedSpeedTextView.setText("Slow ");
			break;
		default:
			holder.ratedSpeedImageView.setVisibility(View.INVISIBLE);
			holder.ratedSpeedTextView.setVisibility(View.VISIBLE);
			holder.ratedSpeedTextView.setText("");
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
		
		// Signal strength
		signalLevel = content.getSignalLevel();
		if (isBetween(signalLevel, 0, -40)) {
			holder.signalStrengthImageView.setImageResource(R.drawable.ic_wifi_full_bar);
		} else if (isBetween(signalLevel, -41, -60)) {
			holder.signalStrengthImageView.setImageResource(R.drawable.ic_wifi_3_bar);
		} else if (isBetween(signalLevel, -61, -80)) {
			holder.signalStrengthImageView.setImageResource(R.drawable.ic_wifi_2_bar);
		} else if (isBetween(signalLevel, -81, -100)) {
			holder.signalStrengthImageView.setImageResource(R.drawable.ic_wifi_1_bar);
		}

		// Setting the background wifi icon to gray and transparent
		int color;
		color = context.getResources().getColor(R.color.filter_color_grey);
		holder.wifiBackImage.setColorFilter(color, Mode.SRC_ATOP);
		holder.wifiBackImage.setAlpha((float) 0.3);
				
		return view;
	}
	
	// Signal strength level is not normalised. They're in the range 0 to -100, with 0 meaning better signal
	public static boolean isBetween(int x, int upper, int lower) {
		  return lower <= x && x <= upper;
	}
}
