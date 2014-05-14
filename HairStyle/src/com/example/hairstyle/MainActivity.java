package com.example.hairstyle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.accounts.Account;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE = 1;
	public static final String LOG_TAG = "HairStyle:Activity";
	public Vector<PointF> listPoint = new Vector<PointF>();

	public static Bitmap bitmap;
	private ImageView imageVIew;
	private MyCustomView myCustomView;
	private View myview;
	private Paint paint = new Paint();
	private Path path = new Path();
	public static int screenWidth;
	public static int screenHeight;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(LOG_TAG, "OpenCV loaded successfully");
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		Display display = getWindowManager().getDefaultDisplay(); 
		
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		/*
		 * myCustomView = new MyCustomView(this, null);
		 * setContentView(myCustomView);
		 */

		/*
		 * imageVIew = (ImageView) findViewById(R.id.result);
		 * 
		 * imageVIew.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
		 * Auto-generated method stub float eventX = event.getX(); float eventY
		 * = event.getY();
		 * 
		 * switch (event.getAction()) { case MotionEvent.ACTION_DOWN: return
		 * true; case MotionEvent.ACTION_MOVE: break; case
		 * MotionEvent.ACTION_UP: // nothing to do PointF newPoint = new
		 * PointF(eventX, eventY); listPoint.add(newPoint); break; default:
		 * return false; }
		 * 
		 * Log.i(LOG_TAG, "Image Touch" + listPoint.size()); Log.i(LOG_TAG,
		 * "Image Touch" + eventX + "::"+ eventY); // Schedules a repaint.
		 * return true; } });
		 */
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	public void setupPainter() {
		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
	}

	public void pickImage(View view) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		InputStream stream = null;
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			try {
				if (bitmap != null) {
					bitmap.recycle();
				}
				stream = getContentResolver().openInputStream(data.getData());
				bitmap = BitmapFactory.decodeStream(stream);

				// imageVIew.setImageBitmap(bitmap);
				myCustomView = new MyCustomView(this, null);
				setContentView(myCustomView);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (stream != null)
					try {
						stream.close();
					} catch (IOException e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			return true;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			// nothing to do
			PointF newPoint = new PointF(eventX, eventY);
			listPoint.add(newPoint);
			break;
		default:
			return false;
		}

		// Log.i(LOG_TAG, "Touch event" + listPoint.size());
		// Schedules a repaint.
		return true;
	}

	private static long back_pressed;

	@Override
	public void onBackPressed() {
		if (back_pressed + 2000 > System.currentTimeMillis())
			super.onBackPressed();
		else
			Toast.makeText(getBaseContext(), "Press once again to exit!",
					Toast.LENGTH_SHORT).show();
		back_pressed = System.currentTimeMillis();
		setContentView(R.layout.activity_main);
	}
}
