package com.example.demointent;

import android.app.Activity;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class ResultActivity extends Activity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_result);

		Bundle extras = getIntent().getExtras();
		String inputString = extras.getString("value1");
		TextView view = (TextView) findViewById(R.id.displayintentextra);
		view.setText(inputString);
	}

	@Override
	public void finish() {
		Intent intent = new Intent();
		EditText editext = (EditText) findViewById(R.id.returnValue);
		String string = editext.getText().toString();
		Log.i("DEBUG", string);
		intent.putExtra("returnkey", string);
		setResult(RESULT_OK,intent);
		super.finish();
	}
}
