package sg.edu.astar.i2r.sns.Activity;

import sg.edu.astar.i2r.sns.R;
import sg.edu.astar.i2r.sns.global.Global;
import sg.edu.astar.i2r.sns.utils.Loger;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;

public class WifiScoutMainActivity extends Activity
{
	private SharedPreferences mPreferences;
	private boolean mIsfirstLaunch; 
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mPreferences = this.getSharedPreferences(Global.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        if(isFirstLaunch() == true) {
        	Loger.debug("This is first launch, data base will be download");
        }
    }
    
    /*
     * check if it is the first launch
     * May be show a overlay layout 
     * Download data base
     * */
    public boolean isFirstLaunch() {
    	
    	mIsfirstLaunch = mPreferences.getBoolean("IS_FIRST_LAUNCH", true);
    	if (mIsfirstLaunch  ==  true) { 
    	    SharedPreferences.Editor editor = mPreferences.edit();
    	    editor.putBoolean("IS_FIRST_LAUNCH", false);
    	    editor.commit();
    	    return true;
    	}
    	return false;
    }
}
