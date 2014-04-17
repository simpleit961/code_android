package sg.edu.astar.i2r.sns.service;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * The class responsibile for getting activity recognition results and then broadcasting it
 * to the data collection service. <p>
 * This is a temporary service. <p>
 * An instance of it created during a request for an activity recognition updates.
 * The Android system calls the onHandleIntent method when ever a result is available.
 * This class handles the result by broadcasting the result then terminates.
 */
public class ActivityRecognitionService extends IntentService {
	@SuppressWarnings("unused")
	private static final String TAG = "ActivityRecognitionService";
	
	public ActivityRecognitionService() {
		super("Activity Recognition Service");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
//		Log.d(TAG, "Received Intent");
		 if (ActivityRecognitionResult.hasResult(intent)) {
	         ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent); 
	         if (result != null) {
	        	 Intent i = new Intent("sg.edu.astar.i2r.sns.activity.ACTIVITY_RECOGNITION_DATA");
	        	 i.putExtra("Activity", getType(result.getMostProbableActivity().getType()) );
	        	 i.putExtra("Confidence", result.getMostProbableActivity().getConfidence());
	        	 sendBroadcast(i);
	         }
	     }
		 
		 stopSelf();
	}

	private String getType(int type){
		if (type == DetectedActivity.UNKNOWN) 		return "Unknown";
		if (type == DetectedActivity.IN_VEHICLE)	return "In Vehicle";
		if (type == DetectedActivity.ON_BICYCLE)	return "On Bicycle";
		if (type == DetectedActivity.ON_FOOT)		return "On Foot";
		if (type == DetectedActivity.STILL)			return "Still";
		if (type == DetectedActivity.TILTING)		return "Tilting";
		return "";
	}
}

