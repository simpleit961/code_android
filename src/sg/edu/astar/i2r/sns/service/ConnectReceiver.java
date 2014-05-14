package sg.edu.astar.i2r.sns.service;

import sg.edu.astar.i2r.sns.utils.Loger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;

public class ConnectReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Loger.debug("Network State changed");
		
		
		String action = intent.getAction();

        if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)){

        SupplicantState supplicantState = (SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
        if (supplicantState == (SupplicantState.COMPLETED)){
           /* if (I) Log.i(TAG, "SUPPLICANTSTATE ---> Connected");*/
        	Loger.debug("Supplicantstate --> connected");
                  //do something
        }

        if (supplicantState == (SupplicantState.DISCONNECTED)){
            //if (I) Log.i(TAG, "SUPPLICANTSTATE ---> Disconnected");
        	Loger.debug("Supplicantstate --> Disconected");
                   //do something
        }
        }
	}

}
