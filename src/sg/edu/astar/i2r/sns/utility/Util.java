package sg.edu.astar.i2r.sns.utility;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.astar.i2r.sns.model.AccessPointContent;
import sg.edu.astar.i2r.sns.model.Place;
import android.os.Build;

public class Util {
	private final static double CIRCUMFERENCE_OF_EARTH = 40075.04;	// 40075.04 km
	
	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalise(model);
		} else {
			return capitalise(manufacturer) + " " + model;
		}
	}
	
	static String capitalise(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public static double getLatitudeDistance(double distance) {
		return 360*distance/CIRCUMFERENCE_OF_EARTH;
	}

	public static double getLongitudeDistance(double distance, double latitude) {
		return 360*distance/(CIRCUMFERENCE_OF_EARTH * Math.cos(latitude));
	}
	
	public static boolean keywordFilter(AccessPointContent content, String keywordFilter) {
		if (keywordFilter != null) {
			String place = content.getPlace();
			String address = content.getAddress();
			String ssid = content.getSsid().toLowerCase(Locale.US);
			keywordFilter = keywordFilter.toLowerCase(Locale.US);
			
			if (place != null)
				place = place.toLowerCase(Locale.US);
			
			if (address != null)
				address = address.toLowerCase(Locale.US);
			
			if (!ssid.contains(keywordFilter) 
					&& !(address != null && address.contains(keywordFilter))
					&& !(place != null && place.contains(keywordFilter))) {
				return true;
			}
		}
		
		return false;
	}

	public static Place parseJsonPlaceObject(JSONObject json) {
		try {
			String types = json.getString("types");
			if (types.contains("locality") || types.contains("neighborhood"))
				return null;
				
			Place place = new Place();
			JSONObject geometry = (JSONObject) json.get("geometry");
			JSONObject location = (JSONObject) geometry.get("location");
			place.setLatitude((Double) location.get("lat"));
			place.setLongitude((Double) location.get("lng"));
			place.setVicinity(json.getString("vicinity"));
			place.setName(json.getString("name"));
			place.setIcon(json.getString("icon"));
			
			return place;
		} catch (JSONException ex) {
			Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
}
