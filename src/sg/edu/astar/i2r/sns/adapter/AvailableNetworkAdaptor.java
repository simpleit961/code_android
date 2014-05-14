package sg.edu.astar.i2r.sns.adapter;

import java.util.ArrayList;
import java.util.List;

import sg.edu.astar.i2r.sns.R;
import sg.edu.astar.i2r.sns.fragment.NetworkFragment;
import sg.edu.astar.i2r.sns.model.AccessPoint;
import sg.edu.astar.i2r.sns.utils.Loger;
import sg.edu.astar.i2r.sns.utils.WifiScoutManager;
import sg.edu.astar.i2r.sns.utils.WifiUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class AvailableNetworkAdaptor extends BaseAdapter implements OnClickListener, Filterable{

	private List<AccessPoint> listVisibleAccessPoint;
	private Activity mActivity;
	private static LayoutInflater inflater=null;
	private AccessPoint mAccesspoint;
	public Resources res;
	
	public AccessPointFilter mAccessPointFilter;
	public static int filter = 0; 
    
	public AvailableNetworkAdaptor(Activity activity, int customRowNetwork,List<AccessPoint> listVisibleAccessPoint2, Resources resLocal) {
		mActivity = activity;
		listVisibleAccessPoint = listVisibleAccessPoint2;
		inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		res = resLocal;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(listVisibleAccessPoint.size()<=0)
            return 1;
        return listVisibleAccessPoint.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public void onClick(View v) {
            Loger.debug("=====Row button clicked=====");
            filter++;
            filter%=2;
            
            this.getFilter().filter(null);
    }
    
	public static class ViewHolder{
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTexview2;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
        ViewHolder holder;
        if(convertView==null){
        	vi = inflater.inflate(R.layout.custom_row_network, null);

        	holder = new ViewHolder();
        	
        	holder.mImageView = (ImageView) vi.findViewById(R.id.icon);
        	holder.mTextView1 = (TextView)vi.findViewById(R.id.secondLine);
        	holder.mTexview2  =   (TextView)vi.findViewById(R.id.thirdLine);

        	/************  Set holder with LayoutInflater ************/
        	vi.setTag( holder );
        } else  {
            holder=(ViewHolder)vi.getTag();
        }
        
        if(listVisibleAccessPoint.size()<=0) {
            holder.mTextView1.setText("No Data");
        } else {
        	mAccesspoint = null;
            mAccesspoint = listVisibleAccessPoint.get( position );
            
            /************  Set Model values in Holder elements ***********/

            holder.mImageView = (ImageView) vi.findViewById(R.drawable.abc_ab_bottom_solid_light_holo);
            
            //holder.mImageView.setImageResource(res.getIdentifier("com.androidexample.customlistview:drawable/"+tempValues.getImage(),null,null));
            holder.mTextView1.setText(""+ mAccesspoint.getSsid() + "  Mac: " + mAccesspoint.getBssid());
            holder.mTexview2.setText("Strength: "+ mAccesspoint.getLevel()*-1 + "%");
              
             /******** Set Item Click Listner for LayoutInflater for each row *******/

             vi.setOnClickListener(new OnItemClickListener( position ));
        }
		return vi;
	} 
	
	/********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{          
        private int mPosition;
         
        OnItemClickListener(int position){
             mPosition = position;
        }
         
        @Override
        public void onClick(View arg0) {
         /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/
        	Loger.debug("Position click"+mPosition);
        	openBoxToDownloadDataBase(mPosition);
        }              
    }   
    
    public void openBoxToDownloadDataBase(final int Position) {
		final Dialog dialog = new Dialog(mActivity);
		dialog.setContentView(R.layout.listview_contex_menu);
		
		Button buttonOk = (Button) dialog.findViewById(R.id.button_ok);
		buttonOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Loger.debug(listVisibleAccessPoint.get(Position).getSsid());
				AccessPoint accessPoint = listVisibleAccessPoint.get(Position);
				WifiUtils.connectToSpecificNetwork(accessPoint);
				dialog.dismiss();
			}
		});
		Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if(mAccessPointFilter == null)
			mAccessPointFilter  = new AccessPointFilter();
		else 
			return mAccessPointFilter;
		
		return null;
	}
	
	
	public class AccessPointFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			Loger.debug("perform filter");
			FilterResults results = new FilterResults();
			
			listVisibleAccessPoint = WifiScoutManager.listVisibleAccessPoint;
			
			if (NetworkFragment.filterValue == 0) {
		        // No filter implemented we return all the list
		        results.values = listVisibleAccessPoint;
		        results.count = listVisibleAccessPoint.size();
		    } 

			List<AccessPoint> resultList = new ArrayList<AccessPoint>();
			int number = 0;
			
			if(NetworkFragment.filterValue == 1) {
				for(AccessPoint ap : listVisibleAccessPoint) {
					if(ap.getLogin_required() == false) {
						number++;
						resultList.add(ap);
					}
				}
			}
			
			if(NetworkFragment.filterValue == 2) {
				for(AccessPoint ap : listVisibleAccessPoint) {
					if(ap.getLogin_required() == true) {
						number++;
						resultList.add(ap);
					}
				}
			}
			
			results.values = resultList;
			results.count = resultList.size();
			Loger.debug("Number of filter"+results.count);
			
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			if(results.count == 0) {
				notifyDataSetChanged();
			} else {
				listVisibleAccessPoint = (List<AccessPoint>) results.values;
				notifyDataSetInvalidated();
			}
		}
	}
}
