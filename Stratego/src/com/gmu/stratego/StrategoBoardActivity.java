package com.gmu.stratego;

import org.json.JSONObject;

import com.gmu.stratego.board.Board;
import com.gmu.stratego.board.BoardTile;
import com.gmu.stratego.client.StrategoClient;
import com.gmu.stratego.client.StrategoHttpTask;
import com.gmu.stratego.client.URLS;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class StrategoBoardActivity extends Activity {
	private Board board;
	private BoardTile selectedTile = null;
	private StrategoClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stratego_board);
		
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stratego_board, menu);
		return true;
	}
	
	public synchronized void setSelectedSpace(final BoardTile selectedTile) {
		if (this.selectedTile != null) {
			this.selectedTile.changeBlack();
		}
		this.selectedTile = selectedTile;
		selectedTile.changeBlue();	
	}
	
	public synchronized void getGameState() {
		StrategoHttpTask task = new StrategoHttpTask() {
			
			@Override
			public String performTask(String... urls) {
				return client.GET(URLS.GET_GAME_STATE);//client;
			}
			
			@Override
			public void afterTask(String result) {
				
			}
		};
	}
}
