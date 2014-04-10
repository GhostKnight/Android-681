package com.gmu.stratego;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.main_sign_in).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("Button Click", "Click the login button");
				final Intent i = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(i);
			}
		});
		//final Button main = (Button) findViewById(R.id.main_sign_in);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
