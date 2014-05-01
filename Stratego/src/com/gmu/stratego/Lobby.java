package com.gmu.stratego;

import org.json.JSONObject;

import com.gmu.stratego.client.StrategoClient;
import com.gmu.stratego.client.StrategoHttpTask;
import com.gmu.stratego.client.URLS;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Lobby extends Activity {
	private StrategoClient client;
	private TableLayout gameTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		// Show the Up button in the action bar.
		setupActionBar();
		
		gameTable = (TableLayout) findViewById(R.id.lobby_table);
		client = StrategoClient.getInstance();
		
		findViewById(R.id.refresh_lobby_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshGames();
			}
		});
		
		findViewById(R.id.create_game_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StrategoHttpTask createGameTask = new StrategoHttpTask() {
					@Override
					public String performTask(String... urls) {
						final JSONObject gameReq = new JSONObject();
						try {
							gameReq.put("type", "stratego");
							return client.POST(URLS.POST_CREATE_GAME, gameReq);
						} catch (Exception e) {
							Log.e("", "Error Creating game", e);
						}
						return null;
					}
					
					@Override
					public void afterTask(String result) {
						Log.d("Create game result", result);
						
						// TODO: Move to a new board and set the id
					}
				};
				createGameTask.execute(URLS.POST_CREATE_GAME);
			}
		});
		
		// refresh games when the view opens
		refreshGames();
	}
	
	private void refreshGames() {
		StrategoHttpTask refreshGameList = new StrategoHttpTask() {
			@Override
			public String performTask(String... urls) {
				try {
					return client.GET(URLS.GET_GAMES);
				} catch (Exception e) {
					Log.e("", "Error Getting games", e);
				}
				return null;
			}
			
			@Override
			public void afterTask(String result) {
				Log.d("GET GAMES result", result);
				
				TableRow testRow = new TableRow(gameTable.getContext());
				testRow.setClickable(true);
				testRow.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						Toast.makeText(getBaseContext(), "Clicked on a row!", Toast.LENGTH_SHORT).show();
					}
				});
				TextView test = new TextView(testRow.getContext());
				test.setText("Hello!  This is a test...");
				testRow.addView(test);
				gameTable.addView(testRow);
			}
		};
		refreshGameList.execute(URLS.GET_GAMES);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby, menu);
		return true;
	}

}
