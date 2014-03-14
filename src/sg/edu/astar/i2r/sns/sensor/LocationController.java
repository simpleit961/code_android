package sg.edu.astar.i2r.sns.sensor;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * The class for providing the location information
 */
public class LocationController implements LocationListener {
	private static LocationManager locationManager;
	private static Location networkLocation = null;
	private static Location gpsLocation = null;
	private static boolean isNetworkEnabled = false;
	private static boolean isGPSEnabled = false;
	
	public LocationController () {}
	
	/**
	 * Tries to get the user's location based on GPS and the network if GPS fails.
	 * Gets the last known location so need to check if it is up to date.
	 * @param context
	 * @return
	 */
	public static Location getLocation(Context context) {
		float networkAccuracy = 0;
		float gpsAccuracy = 0;
		
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (isGPSEnabled) {
			if (locationManager != null) {
//				locationManager.requestLocationUpdates(
//						LocationManager.GPS_PROVIDER,
//						0,
//						0, this);
				gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);	// Check if it's up to date
				if (gpsLocation != null) {
					gpsAccuracy = gpsLocation.getAccuracy();
				}
			}
		}
		
		if (isNetworkEnabled) {
			if (locationManager != null) {
//				locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER,
//                    0,
//                    0, this);
				networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (networkLocation != null) {
					networkAccuracy = networkLocation.getAccuracy();
				}
			}
		}
		
		if ((networkAccuracy == 0 || gpsAccuracy < networkAccuracy) && gpsAccuracy > 0 && gpsLocation != null) {
			return gpsLocation;
		} else if ((gpsAccuracy == 0 || gpsAccuracy > networkAccuracy) && networkAccuracy > 0 && networkLocation != null) {
			return networkLocation;
		}

		return gpsLocation;
	}
	
	/**
	 * Get the distance from the user's current location to the provided end location
	 * @param endLatitude
	 * @param endLongitude
	 * @param context
	 * @return distance in meters
	 */
	public static int getDistance(double endLatitude, double endLongitude, Context context) {
		float[] results = new float[1];
		Location location = getLocation(context);

		if (location != null) {
			double startLatitude = location.getLatitude();
			double startLongitude = location.getLongitude();
			
			try {
				Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
			} catch (IllegalArgumentException e) { 	// Replace with finally
				results[0] = -1;
			}
		}
		
		return (int) results[0];
	}

	@Override
	public void onLocationChanged(Location location) {
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}

