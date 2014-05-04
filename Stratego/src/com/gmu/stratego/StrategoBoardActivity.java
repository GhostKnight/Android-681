package com.gmu.stratego;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmu.stratego.board.BoardTile;
import com.gmu.stratego.client.StrategoClient;
import com.gmu.stratego.client.StrategoHttpTask;
import com.gmu.stratego.client.URLS;
import com.gmu.stratego.json.StrategoAction;
import com.gmu.stratego.util.StrategoConstants;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

public class StrategoBoardActivity extends Activity {
	private BoardTile selectedTile = null;
	private StrategoClient client;
	private String gameID;
	private String stringColor;
	private int playerColor;
	private String gamePhase;

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
				processGameState(result);
				Log.d("Game State", result);
			}
		};
		task.execute(url);
	}
	
	private void processGameState(final String state) {
		try {
			JSONObject gameState = new JSONObject(state);
			gamePhase = gameState.getString("phase");
			
			// Process what player we are
			if (gameState.getJSONObject("bluePlayer").getString("_id").equals(client.getUser().getString("_id"))) {
				playerColor = Color.BLUE;
			} else {
				playerColor = Color.RED;
			}
			setUpDeploymentBoard();
			
			JSONArray actions = gameState.getJSONArray("actionList");
			for (int i=0; i < actions.length(); i++) {
				JSONObject action = (JSONObject) actions.get(i);
				
			}
		} catch (Exception e) {
			Log.e("", "", e);
		}
	}
	
	/**
	 * This method handles the processing of incoming actions to the game.  When we get
	 * a response from the server we have this method pick through the details that were passed
	 * to the client and from the determine what needs to be drawn.
	 * @param action
	 * @throws JSONException
	 */
	private void processAction(StrategoAction action) throws JSONException {
		final int y = action.getInt("y") - 1; // Adjustment for Chris's board
		final int x = action.getInt("x") - 1;
		final String pieceType = action.getPieceType();
		int piecePower = action.getPieceValue();
		// If the color isn't our piece, change the value to 13 so we hide it
		if (pieceType.equals("RedPiece") && playerColor != Color.RED || 
				pieceType.equals("BluePiece") && playerColor != Color.BLUE) {
			piecePower = 13;
		}
		final boolean redPiece = pieceType.equals("RedPiece");
		StringBuilder tileID = new StringBuilder("tile");
		tileID.append(y);
		tileID.append(x);
		final int id = getResources().getIdentifier(tileID.toString(), "id", getBaseContext().getPackageName());
		final BoardTile tile = (BoardTile) findViewById(id);
		tile.setImage((redPiece) ? Color.RED : Color.BLUE, piecePower);
		
		if (gamePhase.equals("PLACE_PIECES")) {
			// TODO set up the numbers here... Also remember to check if the user has a current game and auto join it
			setUpDeploymentBoard();
		}
	}
	
	/**
	 * @return - This boards gameID
	 */
	public String getGameID() {
		return gameID;
	}
	
	/**
	 * This method handles setting up the board so that the user can begin placing pieces
	 * before the game starts.  First thing that the method checks is the current game phase.
	 * If the phase isn't PLACE_PIECES then no additional processing is required from this method
	 * so we set the deployment table invisible and move on.
	 * 
	 * However, if we are in the correct game phase then set up the deployment board according to
	 * the players color and then highlight the valid deployment zones for the current player.
	 */
	private void setUpDeploymentBoard() {
		View deploymentLabel = findViewById(R.id.deploymentLabel);
		TableLayout deploymentTable = (TableLayout) findViewById(R.id.deploymentTable);
		if (gamePhase.equals("PLACE_PIECES")) {
			deploymentLabel.setVisibility(View.VISIBLE);
			deploymentTable.setVisibility(View.VISIBLE);
		} else {
			deploymentLabel.setVisibility(View.INVISIBLE);
			deploymentTable.setVisibility(View.INVISIBLE);
			return;
		}
		
		((BoardTile) findViewById(R.id.deployment01)).setImage(playerColor, 1);
		((BoardTile) findViewById(R.id.deployment02)).setImage(playerColor, 2);
		((BoardTile) findViewById(R.id.deployment03)).setImage(playerColor, 3);
		((BoardTile) findViewById(R.id.deployment04)).setImage(playerColor, 4);
		((BoardTile) findViewById(R.id.deployment05)).setImage(playerColor, 5);
		((BoardTile) findViewById(R.id.deployment06)).setImage(playerColor, 6);
		((BoardTile) findViewById(R.id.deployment07)).setImage(playerColor, 7);
		((BoardTile) findViewById(R.id.deployment08)).setImage(playerColor, 8);
		((BoardTile) findViewById(R.id.deployment11)).setImage(playerColor, 9);
		((BoardTile) findViewById(R.id.deployment12)).setImage(playerColor, 10);
		((BoardTile) findViewById(R.id.deployment13)).setImage(playerColor, 11);
		((BoardTile) findViewById(R.id.deployment14)).setImage(playerColor, 12);
		
		Integer[] depZone = (playerColor == Color.RED) ? StrategoConstants.RED_DEP_ZONE : StrategoConstants.BLUE_DEP_ZONE; 
		for (int i = 0; i < depZone.length; i++) {
			// TODO highlight square yellow
			((BoardTile)findViewById(depZone[i])).changeYellow();
		}
	}
}
