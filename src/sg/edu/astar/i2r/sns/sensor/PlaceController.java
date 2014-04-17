package sg.edu.astar.i2r.sns.sensor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.astar.i2r.sns.model.Place;
import sg.edu.astar.i2r.sns.utility.Util;

import android.util.Log;
import android.widget.ProgressBar;

/**
 * The class that communicates with Google Place API.
 * Change the API_KEY
 */
public class PlaceController {
	private static final String API_KEY = "AIzaSyAHsQued_089ZIEQsq97zw78NCP5zOkyaM";
	private static final String RADIUS = "100"; // 100 meters
	
	public ArrayList<Place> findPlaces(double latitude, double longitude, String keyword, ProgressBar progressBar) {
		ArrayList<Place> places = new ArrayList<Place>();
		String urlString = makeUrl(latitude, longitude, keyword);
		
		try {
			String apiResult = getJSON(urlString);
			
			if (apiResult == null) 
				return null;
			
			progressBar.setProgress(2);
			
			JSONObject object = new JSONObject(apiResult);
			JSONArray array = object.getJSONArray("results");
			for (int i = 0; i < array.length(); i++) {
				progressBar.setProgress(progressBar.getProgress() + 1);

				try {
					Place place = Util.parseJsonPlaceObject((JSONObject) array.get(i));
					if (place != null) 
						places.add(place);
				} catch (Exception e) {
					
				}
			}
			
			return places;
		} catch (JSONException ex) {
			Logger.getLogger(PlaceController.class.getName()).log(Level.SEVERE,	null, ex);
		}

		return null;
	}


	private String makeUrl(double latitude, double longitude, String keyword) {
		StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?");
		
		urlString.append("keyword=" + keyword);
		urlString.append("&location=");
		urlString.append(Double.toString(latitude));
		urlString.append(",");
		urlString.append(Double.toString(longitude));
		urlString.append("&radius=" + RADIUS);
		urlString.append("&sensor=true&key=" + API_KEY);
		
		return urlString.toString();
	}
	
	protected String getJSON(String url) {
		return getUrlContents(url);
	}

	private String getUrlContents(String theUrl) {
		StringBuilder content = new StringBuilder();
		
		try {
			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()), 8);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			bufferedReader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return content.toString();
	}

}
