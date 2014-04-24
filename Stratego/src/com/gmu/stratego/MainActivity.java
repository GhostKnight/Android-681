package com.gmu.stratego;

import org.json.JSONObject;

import com.gmu.stratego.client.StrategoClient;
import com.gmu.stratego.client.StrategoHttpTask;
import com.gmu.stratego.client.URLS;
import com.gmu.stratego.json.User;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected User user = null;
	protected EditText userField, passwordField;
	protected StrategoClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		client = StrategoClient.getInstance(this);
		
		userField = (EditText) findViewById(R.id.username);
		passwordField = (EditText) findViewById(R.id.password);
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (!validateUser())
							return;
						try {
							final JSONObject user = new JSONObject();
							user.accumulate("username", userField.getText());
							user.accumulate("password", passwordField.getText());
							final StrategoHttpTask postTask = new StrategoHttpTask() {
								@Override
								public String performTask(String... urls) {
									return client.POST(urls[0], user);
								}

								@Override
								public void afterTask(String result) {
									Toast.makeText(getBaseContext(), "Response received!", Toast.LENGTH_LONG).show();
									Log.d("Got a response!!!", result);
								}
							};
							postTask.execute(URLS.LOGIN);
							// final Intent i = new Intent(MainActivity.this,
							// LoginActivity.class);
							// startActivity(i);
						} catch (Exception e) {
							Log.e("Exception", "Login Exception", e);
						}
					}
				});
		// final Button main = (Button) findViewById(R.id.main_sign_in);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private boolean validateUser() {
		if (userField.getText().toString().trim().equals(""))
			return false;
		else if (passwordField.getText().toString().trim().equals(""))
			return false;
		else
			return true;
	}
}
