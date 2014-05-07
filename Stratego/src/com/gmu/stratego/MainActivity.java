package com.gmu.stratego;

import org.json.JSONException;
import org.json.JSONObject;

import com.gmu.stratego.board.StrategoBoardActivity;
import com.gmu.stratego.client.StrategoClient;
import com.gmu.stratego.client.StrategoHttpTask;
import com.gmu.stratego.client.URLS;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	protected EditText userField, passwordField;
	protected StrategoClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		client = StrategoClient.getInstance(this);

		userField = (EditText) findViewById(R.id.username);
		passwordField = (EditText) findViewById(R.id.password);
		findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
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
							Log.d("Got a response!!!", result);
							// TODO Add conditional here if the login was
							// successful or not
							// TODO Then use the intent that is below

							if (result.contains("errors") || result.toLowerCase().contains("invalid")) {
								Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
								userField.setText("");
								passwordField.setText("");
							} else {			
								try {
									final JSONObject user = new JSONObject(result);
									client.setUser(user);
									if (user.has("currentGameId")) {
										Lobby.joinGame(user.getString("currentGameId"), MainActivity.this);
									}
								} catch (JSONException e) {
									Log.e("", "", e);
								}
								final Intent i = new Intent(MainActivity.this, Lobby.class);
								startActivity(i);
							}
						}
					};
					postTask.execute(URLS.LOGIN);
				} catch (Exception e) {
					Log.e("Exception", "Login Exception", e);
				}
			}
		});
		
		// TODO use thsi for testing the popup
//		findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				StrategoBoardActivity.displayCombatPopup(MainActivity.this);
//			}
//		});
		findViewById(R.id.create_user).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Go to create user layout
				final Intent i = new Intent(MainActivity.this, CreateUserActivity.class);
				startActivity(i);
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
