package com.gmu.stratego;

import org.json.JSONObject;

import com.gmu.stratego.client.StrategoClient;
import com.gmu.stratego.client.StrategoHttpTask;
import com.gmu.stratego.client.URLS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class CreateUserActivity extends Activity {

	private EditText username, password1, password2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		username = (EditText) findViewById(R.id.new_username);
		password1 = (EditText) findViewById(R.id.new_password1);
		password2 = (EditText) findViewById(R.id.new_password2);

		findViewById(R.id.submit_new_user).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validateForm()) {
					try {
						final JSONObject user = new JSONObject();
						user.put("username", username.getText().toString());
						user.put("password", password1.getText().toString());
						StrategoHttpTask task = new StrategoHttpTask() {
							@Override
							public String performTask(String... urls) {
								return StrategoClient.getInstance().POST(urls[0], user);
							}
							
							@Override
							public void afterTask(String result) {
								try {
									JSONObject response = new JSONObject(result);
									if (result.contains("errors")) {
										popup(response.getJSONObject("errors").getJSONArray("fieldErrors").getJSONObject(0).getString("defaultMessage"));
									} else {
										popup("User created successfully!");
										final Intent i = new Intent(CreateUserActivity.this, MainActivity.class);
										startActivity(i);
									}
								} catch (Exception e) {
									Log.e("create user response", "create user response", e);
								}
								Log.d("RESPONSE!!!", result);
							}
						};
						task.execute(URLS.SIGNUP);
					} catch (Exception e) {
						Log.e("Error caught in creating a user", "Could not create user", e);
					}
				}
			}
		});
	}
	
	private boolean validateForm() {
		boolean formValid = true;
		StringBuilder errorString = new StringBuilder("");
		try {
			String user = username.getText().toString();
			if (user.isEmpty()) {
				Log.e("Error in validation", "username is bad: " + user);
				formValid = false;
			}
			// retrieve the passwords
			String pass1 = password1.getText().toString();
			String pass2 = password2.getText().toString();
			if (pass1 != null && pass2 != null && !pass1.equals(pass2)) {
				Log.e("Error in validation", "Passwords do not match: " + pass1 + " : " + pass2);
				formValid = false;
			}
			if (!formValid)
				popup(errorString.toString());
		} catch (Exception e) {
			Log.e("", "", e);
		}
		return formValid;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_create_user, container, false);
			return rootView;
		}
	}

	private void popup(String msg) {
		Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
	}
}
