package com.gmu.stratego;

import java.util.Timer;
import java.util.TimerTask;

import com.gmu.stratego.board.BoardTile;
import com.gmu.stratego.client.StrategoClient;
import com.gmu.stratego.client.StrategoHttpTask;
import com.gmu.stratego.client.URLS;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class StrategoBoardActivity extends Activity {
	private BoardTile selectedTile = null;
	private StrategoClient client;
	private String gameID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stratego_board);
		
		// Get the params that were passed
		if (getIntent().getExtras().containsKey("id")) {
			gameID = getIntent().getExtras().getString("id");
			Toast.makeText(getBaseContext(), "Joined game with ID: " + gameID, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getBaseContext(), "Fatal error occured, no game ID was provided", Toast.LENGTH_LONG).show();
			finish();
		}
		
		client = StrategoClient.getInstance();
		
		// Get rid of water tiles
		findViewById(R.id.tile43).setVisibility(View.INVISIBLE);
		findViewById(R.id.tile42).setVisibility(View.INVISIBLE);
		findViewById(R.id.tile46).setVisibility(View.INVISIBLE);
		findViewById(R.id.tile47).setVisibility(View.INVISIBLE);
		findViewById(R.id.tile53).setVisibility(View.INVISIBLE);
		findViewById(R.id.tile52).setVisibility(View.INVISIBLE);
		findViewById(R.id.tile56).setVisibility(View.INVISIBLE);
		findViewById(R.id.tile57).setVisibility(View.INVISIBLE);
		//board = new Board(getBaseContext());
		//setContentView(board);
		
		final Timer gameState = new Timer();
		gameState.schedule(new TimerTask() {
			@Override
			public void run() {
				Log.d("Running state timer", "Running game state timer");
				getGameState();
			}
		}, 5000, 10000);
		
		setUpDeploymentBoard();
	}
	
	public synchronized void setSelectedSpace(final BoardTile selectedTile) {
		if (this.selectedTile != null) {
			this.selectedTile.changeBlack();
		}
		this.selectedTile = selectedTile;
		selectedTile.changeBlue();	
	}
	
	public synchronized void getGameState() {
		final String url = URLS.GET_GAME_STATE.replace(":id", gameID);
		StrategoHttpTask task = new StrategoHttpTask() {
			@Override
			public String performTask(String... urls) {
				return client.GET(url);
			}
			
			@Override
			public void afterTask(String result) {
				
				Log.d("Game State", result);
			}
		};
		task.execute(url);
	}
	
	public String getGameID() {
		return gameID;
	}
	
	private void setUpDeploymentBoard() {
		((BoardTile) findViewById(R.id.deployment01)).setImage(Color.RED, 1);
		((BoardTile) findViewById(R.id.deployment02)).setImage(Color.RED, 2);
		((BoardTile) findViewById(R.id.deployment03)).setImage(Color.RED, 3);
		((BoardTile) findViewById(R.id.deployment04)).setImage(Color.RED, 4);
		((BoardTile) findViewById(R.id.deployment05)).setImage(Color.RED, 5);
		((BoardTile) findViewById(R.id.deployment06)).setImage(Color.RED, 6);
		((BoardTile) findViewById(R.id.deployment07)).setImage(Color.RED, 7);
		((BoardTile) findViewById(R.id.deployment08)).setImage(Color.RED, 8);
	}
}
