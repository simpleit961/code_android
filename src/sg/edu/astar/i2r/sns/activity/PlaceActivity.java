package sg.edu.astar.i2r.sns.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sg.edu.astar.i2r.sns.adaptor.PlaceListAdapter;
import sg.edu.astar.i2r.sns.model.Place;
import sg.edu.astar.i2r.sns.psense.R;
import sg.edu.astar.i2r.sns.sensor.LocationController;
import sg.edu.astar.i2r.sns.sensor.PlaceController;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

public class PlaceActivity extends ListActivity {
	public static final String TAG = "PlaceActivity";
	private static final int NUM_OF_THREADS = 1;	// Max number of worker thread needed at any one time for scheduledExecutorService
	private PlaceController placeController;
	private PlaceListAdapter adapter;
	private ArrayList<Place> places;
	private GetPlaces getPlaces;
	
	private ScheduledExecutorService scheduleTaskExecutor;
	private ProgressBar progressBar;		// The loading bar that appears while content is loading
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_place);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		setTitle("Nearby Places");
    	getActionBar().setDisplayHomeAsUpEnabled(false);
		
		places = new ArrayList<Place>();
		placeController = new PlaceController();
		
		scheduleTaskExecutor = Executors.newScheduledThreadPool(NUM_OF_THREADS);
		
		// Get inital result that gets displayed to the user
		scheduleTaskExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				getPlaces = new GetPlaces();
				getPlaces.execute("");		// An empty keyword is passed in - so Place API search is not based on a keyword
			}

		}, 0, TimeUnit.MILLISECONDS);

		adapter = new PlaceListAdapter(this, R.layout.row_place, places);
		setListAdapter(adapter);
	}

	/**
	 * Gets the list of nearby places
	 *
	 */
	private class GetPlaces extends AsyncTask<String, Void, ArrayList<Place>> {
		private ArrayList<Place> result = null;
		
		@Override
		protected ArrayList<Place> doInBackground(String... keywords) {
			Location location = LocationController.getLocation(getApplicationContext());
			String correctedKeywords = keywords[0].replaceAll("\\s+", "+");	// Coverts all whitespaces to "+" for url format
			
			if (location == null) {
				Toast.makeText(getApplicationContext(), 
						"Could not obtain location. Please make sure GPS and data network are available", Toast.LENGTH_LONG).show();
				return null;
			}
			
			result = placeController.findPlaces(location.getLatitude(), location.getLongitude(), correctedKeywords, progressBar);
			loadBitmaps();
			
			if (result != null)
				return result;
				
			return null;
		}

		/**
		 * Loads the images for the list of places
		 */
		public void loadBitmaps() {
			int count = 0;	// Used to count how many bitmaps have been processed
			
			if (result == null || result.size() == 0)
				cancel(true);
			
			for (Place place : result) {
				Bitmap bitmap = null;
				try {
					bitmap = BitmapFactory.decodeStream((InputStream) new URL(place.getIcon()).getContent());
					if (bitmap == null)	
						continue;
					
					place.setBitmap(bitmap);
					
					if (count == 9)					// Process 9 number of bitmaps then push it to the user, and then process the rest.
						publishProgress();			// Can change it to another value - choose 9 because it is the number of max items the user can see
				} catch (MalformedURLException e) {	// Might be different for different phones
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
				
				count++;
			}
			
			// If the result has less than 9 that means onProgressUpdate() was never called
			if (count < 9)
				progressBar.setProgress(100);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			progressBar.setProgress(100);
			
			adapter.clear();
			adapter.addAll(result);
			adapter.notifyDataSetChanged();
		}
		
		@Override
		protected void onPostExecute(ArrayList<Place> result) {
			if (result == null)	return;
			
			adapter.clear();
			adapter.addAll(result);

			places = result;
			adapter.notifyDataSetChanged();
			
			// Delay hiding the progress bar to make it appear more natural
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			} finally {
				progressBar.setVisibility(View.INVISIBLE);
			} 
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			Toast.makeText(getApplicationContext(), "No searches found", Toast.LENGTH_LONG).show();
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();		
	}

	@Override
	public void onStop() {
		setResult(PlaceActivity.RESULT_CANCELED, null); // Inform the review fragment that there is nothing to send back
		super.onStop();
	}

	
	@Override
	// Once a result is clicked, send the place name and address back to the review fragment
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Place place = (Place) l.getItemAtPosition(position);
		Intent dataIntent = new Intent(); 
		
		dataIntent.putExtra("name", place.getName()); 
		dataIntent.putExtra("address", place.getVicinity()); 
		
		setResult(PlaceActivity.RESULT_OK, dataIntent); 
		
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.place, menu);
		MenuItem searchItem = menu.findItem(R.id.searchView);
		
		final SearchView searchView = (SearchView) searchItem.getActionView();
		
		// Search for nearby place once user submits a search
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			private static final long SEARCH_DELAY = 0;
			private String lastSearched = null;
			
			@Override
			public boolean onQueryTextSubmit(final String query) {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.setProgress(2);					// Arbitrary number to make progress bar seem more natural - Adjust these values

				// Cancel search if query is empty or it was the last searched term
				if (query.isEmpty() || (lastSearched != null && lastSearched.compareTo(query) == 0)) 
					return false;
				
				lastSearched = query;
				
				// Schedule a thread to perform the search
				scheduleTaskExecutor.schedule(new Runnable() {
					@Override
					public void run() {
						progressBar.setProgress(8);		

//						Log.d(TAG, "performing search on " + query + "...");
						getPlaces = new GetPlaces();
						getPlaces.execute(query);
					}
					
				}, SEARCH_DELAY, TimeUnit.MILLISECONDS);
				
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
}

