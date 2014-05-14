package com.example.splashscree;

import android.app.Activity;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/*public class AnimationActivity extends Activity {
 *//** Called when the activity is first created. */
/*
 * @Override public void onCreate(Bundle savedInstanceState) {
 * super.onCreate(savedInstanceState); setContentView(R.layout.main);
 * AnimatedButton button = new AnimatedButton(this);
 * button.setText("What a button"); LinearLayout layout = (LinearLayout)
 * findViewById(R.id.root); layout.addView(button); } }
 */

public class SplashScreen extends Activity {

	// how long until we go to the next activity
	protected int _splashTime = 2000;

	private Thread splashTread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final SplashScreen sPlashScreen = this;

		// thread for displaying the SplashScreen
		splashTread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {

						// wait 5 sec
						wait(_splashTime);
					}

				} catch (InterruptedException e) {
					Log.i("LOG", "EXCEPTION");
				} catch (Exception e) {
					// TODO: handle exception
					Log.i("LOG", e.toString());
					Log.i("LOG", "EXCEPTION");
				} finally {
					finish();

					// start a new activity
//					Intent i = new Intent();
//					i.setClass(sPlashScreen, Main.class);
//					startActivity(i);
					
					Intent i = new Intent(sPlashScreen, Main.class);
					startActivity(i); 

					//crash
					//stop();
				}
			}
		};

		splashTread.start();
	}

	// Function that will handle the touch
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (splashTread) {
				splashTread.notifyAll();
			}
		}
		return true;
	}

}