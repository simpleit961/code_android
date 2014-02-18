package com.example.demointent;

import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	// constant to determine which sub-activity returns
	private static final int REQUEST_CODE = 10;

	  
	public static String LOG_TAG = "DEBUG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
            }
        // Get data via the key
        String value1 = extras.getString(Intent.EXTRA_TEXT);
        if (value1 != null) {
          // do something with the data
        	Log.i(LOG_TAG,"value=="+value1);
        } 
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
	public void finish(){
    	Log.i(LOG_TAG,"User press back button -> function finish() is actived ");
    	super.finish();
    }
    
    
    public void onClick(View view) {
        EditText text = (EditText) findViewById(R.id.inputforintent);
        // used later
        String value = text.getText().toString();
        // TODO 1 create new Intent(context, class)
        // use the activity as context parameter
        // and "ResultActivity.class" for the class parameter
        Intent i = new  Intent(this,ResultActivity.class);
        
        //put data to sub activity
        i.putExtra("value1", value);
        
        // TODO 2 start second activity with
        // startActivity(intent);
        //startActivity(i);
        
        // TODO 3.. now use 
        startActivityForResult(i, REQUEST_CODE);
      }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data ){
    	if(resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
    		String result = data.getExtras().getString("returnkey");
    		if(result!=null && result.length() > 0){
    			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    		}
    	}
    }
}
