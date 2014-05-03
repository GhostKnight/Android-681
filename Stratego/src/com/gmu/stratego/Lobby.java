package com.gmu.stratego;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmu.stratego.client.StrategoClient;
import com.gmu.stratego.client.StrategoHttpTask;
import com.gmu.stratego.client.URLS;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Lobby extends Activity {
	private StrategoClient client;
	private TableLayout gameTable;
	private List<String> listOfGames = new ArrayList<String>(0);
	private View selectedGame = null;
	private String selectedGameID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		// Show the Up button in the action bar.
		setupActionBar();
		
		gameTable = (TableLayout) findViewById(R.id.lobby_table);
		client = StrategoClient.getInstance();
		
		// Set up the join game button
		findViewById(R.id.join_game_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// If there is no game selected, propagate error to user and return
				if (selectedGame == null) {
					Toast.makeText(getBaseContext(), "Must select a game to join", Toast.LENGTH_LONG).show();
					return;
				}
				
				// Else lets start up the new game board
				final Intent boardIntent = new Intent(Lobby.this, StrategoBoardActivity.class);
				final Bundle params = new Bundle();
				params.putString("id", selectedGameID);
				boardIntent.putExtras(params);
				startActivity(boardIntent);
				// Close out the lobby activity
				finish();
			}
		});
		
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
						if (!result.contains("_id"))
							return;
						try {
							JSONObject response = new JSONObject(result);
							addGameToList(response.getString("_id"));
						} catch (JSONException e) {
							Log.e("", "Error on create game response", e);
						}
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
				if (!result.contains("_id"))
					return;
				
				try {
					JSONArray response = new JSONArray(result);
					for (int i=0; i<response.length(); i++) {
						addGameToList(response.getJSONObject(i).getString("_id"));
					}
					refreshTable();
				} catch (Exception e) {
					Log.e("Error", "Error in get games", e);
				}
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
	
	private void refreshTable() {
		gameTable.removeAllViews();
		for (String gameID : listOfGames) {
			addGameView(gameID);
		}
	}
	
	private void addGameView(final String id) {
		TableRow testRow = new TableRow(gameTable.getContext());
		testRow.setClickable(true);
		final TextView test = new TextView(testRow.getContext());
		test.setText(id);
		testRow.setOnClickListener(new OnClickListener() {					
			@Override
			public void onClick(View v) {
				selectGame(v, id);
			}
		});
		testRow.addView(test);
		gameTable.addView(testRow);
	}
	
	private void selectGame(final View game, String id) {
		if (selectedGame != null) {
			selectedGame.setBackgroundColor(Color.WHITE);
			selectedGame.invalidate();
		}
		
		selectedGame = game;
		selectedGameID = id;
		selectedGame.setBackgroundColor(Color.LTGRAY);
		selectedGame.invalidate();
	}
	
	private void addGameToList(String id) {
		if (listOfGames.contains(id))
			return;
		listOfGames.add(id);
		addGameView(id);
	}
}
