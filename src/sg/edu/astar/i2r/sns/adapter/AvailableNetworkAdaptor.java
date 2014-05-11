package sg.edu.astar.i2r.sns.adapter;

import java.util.List;

import sg.edu.astar.i2r.sns.R;
import sg.edu.astar.i2r.sns.model.AccessPoint;
import sg.edu.astar.i2r.sns.utils.Loger;
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
import android.widget.ImageView;
import android.widget.TextView;

public class AvailableNetworkAdaptor extends BaseAdapter implements OnClickListener{

	private List<AccessPoint> listVisibleAccessPoint;
	private Activity mActivity;
	private static LayoutInflater inflater=null;
	private AccessPoint mAccesspoint;
	public Resources res;
	 
	public AvailableNetworkAdaptor(Activity activity, int customRowNetwork,List<AccessPoint> listVisibleAccessPoint2) {
		mActivity = activity;
		listVisibleAccessPoint = listVisibleAccessPoint2;
		inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        	holder.mImageView = (ImageView) vi.findViewById(R.drawable.wifi_coffee);
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

            
            //holder.mImageView.setImageResource(res.getIdentifier("com.androidexample.customlistview:drawable/"+tempValues.getImage(),null,null));
            holder.mTextView1.setText(mAccesspoint.getSsid());
            holder.mTextView1.setText(mAccesspoint.getBssid());
              
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
        	openBoxToDownloadDataBase();
        }              
    }   
    
    public void openBoxToDownloadDataBase() {
		final Dialog dialog = new Dialog(mActivity);
		dialog.setContentView(R.layout.listview_contex_menu);
		Button buttonOk = (Button) dialog.findViewById(R.id.button_ok);
		buttonOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
}
