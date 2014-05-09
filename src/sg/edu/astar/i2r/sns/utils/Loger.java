package sg.edu.astar.i2r.sns.utils;

import sg.edu.astar.i2r.sns.global.Global;
import android.util.Log;

public class Loger {
	public static void debug(String logmessage) {
		if(Global.DEBUG_MODE == true) {
			Log.d(Global.LOG_TAG, logmessage);
		}
	}
	
	public static void info(String logmessage) {
		if(Global.DEBUG_MODE == true) {
			Log.i(Global.LOG_TAG, logmessage);
		}
	}
	
	public static void warning(String logmessage) {
		if(Global.DEBUG_MODE == true) {
			Log.w(Global.LOG_TAG, logmessage);
		}
	}
}
