package com.gmu.stratego.board;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmu.stratego.R;
import com.gmu.stratego.R.id;
import com.gmu.stratego.R.layout;
import com.gmu.stratego.client.StrategoClient;
import com.gmu.stratego.client.StrategoHttpTask;
import com.gmu.stratego.client.URLS;
import com.gmu.stratego.json.StrategoAction;
import com.gmu.stratego.util.StrategoConstants;

import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.sax.StartElementListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.Toast;

public class StrategoBoardActivity extends Activity {
	private BoardTile selectedTile = null;
	private StrategoClient client;
	private String gameID;
	private String stringColor;
	private int playerColor;
	private String gamePhase;
	private boolean deploymentBoardSetup = false;
	private Timer gameStateTimer;
	private int latestAction;

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
		getGameState();
		
		findViewById(R.id.commitButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String url = URLS.POST_ACTION.replace(":id", gameID);
				StrategoHttpTask task = new StrategoHttpTask() {
					@Override
					public String performTask(String... urls) {
						try {
							return client.POST(url, new StrategoAction(StrategoAction.COMMIT_ACTION, client.getUser()));
						} catch (JSONException e) {
							Log.e("", "", e);
						}
						return null;
					}
					
					@Override
					public void afterTask(String result) {
						Log.d("Commit results: ", result);
					}
				};
				task.execute(url);
			}
		});
	}
	
	public synchronized void setSelectedSpace(final BoardTile selectedTile) {
		cleanupBoard();
		this.selectedTile = selectedTile;
		selectedTile.changeBlue();
		
		// Dont do the rest if we are placing pieces.
		if (gamePhase.equals("PLACE_PIECES"))
			return;
		
		final int x = selectedTile.getXLoc();
		final int y = selectedTile.getYLoc();
		Log.d("X&Y", "x: "+x+", y: "+y);
		boolean xPosDone = false;
		boolean xNegDone = false;
		boolean yNegDone = false;
		boolean yPosDone = false;
		for (int i=1; i <= StrategoConstants.allowedMoves[selectedTile.getUnitPower()]; i++) {
			// Positive X direction
			if (x < (10-i) && !xPosDone) {
				BoardTile xPos = (BoardTile)findViewById(StrategoConstants.boardIDs[y][x + i]);
				if (xPos.getTeamColor() != playerColor)
					xPos.changeYellow();
				if (xPos.getUnitPower() != 0 || xPos.getVisibility() == View.INVISIBLE)
					xPosDone = true;
			}
			// Negative X direction
			if (x > (0+i) && !xNegDone) {
				BoardTile xNeg = (BoardTile)findViewById(StrategoConstants.boardIDs[y][x - i]);
				if (xNeg.getTeamColor() != playerColor)
					xNeg.changeYellow();
				if (xNeg.getUnitPower() != 0 || xNeg.getVisibility() == View.INVISIBLE) {
					xNegDone = true;
				}
			}
			// Positive Y direction
			if (y < (10-i) && !yPosDone) {
				BoardTile yPos = (BoardTile) findViewById(StrategoConstants.boardIDs[y + i][x]);
				if (yPos.getTeamColor() != playerColor)
					yPos.changeYellow();
				if (yPos.getUnitPower() != 0 || yPos.getVisibility() == View.INVISIBLE) {
					yPosDone = true;
				}
			}
			// Negative Y direction
			if (y > (0+i) && !yNegDone) {
				BoardTile yNeg = (BoardTile) findViewById(StrategoConstants.boardIDs[y - i][x]);
				if (yNeg.getTeamColor() != playerColor)
					yNeg.changeYellow();
				if (yNeg.getUnitPower() != 0 || yNeg.getVisibility() == View.INVISIBLE) {
					yNegDone = true;
				}
			}
		}
	}
	
	/**
	 * Request the game state, upon receiving a response go ahead and start the
	 * polling timer for new actions.
	 */
	public synchronized void getGameState() {
		final String url = URLS.GET_GAME_STATE.replace(":id", gameID);
		Log.d("URL for get game state", url);
		StrategoHttpTask task = new StrategoHttpTask() {
			@Override
			public String performTask(String... urls) {
				return client.GET(url);
			}
			
			@Override
			public void afterTask(String result) {
				processGameState(result);
				Log.d("Game State", result);
				startGameCheckTimer();
			}
		};
		task.execute(url);
	}
	
	/**
	 * Process a GameState message type
	 * @param state
	 */
	private void processGameState(final String state) {
		try {
			JSONObject gameState = new JSONObject(state);
			gamePhase = gameState.getString("phase");
			
			// Process what player we are
			if (gameState.has("bluePlayer") && gameState.getJSONObject("bluePlayer").getString("username").equals(client.getUser().getString("username"))) {
				playerColor = Color.BLUE;
			} else {
				playerColor = Color.RED;
			}
			if (gamePhase.equals("PLACE_PIECES"))
				setUpDeploymentBoard();
			else
				hideDeploymentBoard();
			
			final JSONArray actions = gameState.getJSONArray("actionList");
			for (int i=0; i < actions.length(); i++) {
				processAction(new StrategoAction(actions.getJSONObject(i)));
			}
			cleanupBoard();
		} catch (Exception e) {
			Log.e("", "", e);
		}
	}
	
	/**
	 * Hide deployment board, also cleans up the yellow spaces on the board
	 */
	private void hideDeploymentBoard() {
		// clean up label, commit button and the deployment board (should not be visible regardless)
		findViewById(R.id.deploymentLabel).setVisibility(View.INVISIBLE);
		findViewById(R.id.commitButton).setVisibility(View.INVISIBLE);
		for (int dep : StrategoConstants.DEP_TABLE_IDS) {
			findViewById(dep).setVisibility(View.INVISIBLE);
		}
		// Cleanup deployment zones
		final Integer[] cleanup = (playerColor == Color.RED) ? StrategoConstants.RED_DEP_ZONE : StrategoConstants.BLUE_DEP_ZONE;
		for (int dep : cleanup) {
			((BoardTile) findViewById(dep)).changeBlack();
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
		latestAction = action.getActionID();
		if (action.getActionType().equals(StrategoAction.COMMIT_ACTION))
			return;
		
		int y = action.getInt("y") - 1; // Adjustment for Chris's board
		int x = action.getInt("x") - 1;
		final int id = StrategoConstants.boardIDs[y][x];
		final BoardTile tile = (BoardTile) findViewById(id);
		if (action.getActionType().equals(StrategoAction.MOVE_ACTION)) {
			// Movement actions don't come with this information so lets get it ourselves
			final int pieceType = tile.getTeamColor();
			final int piecePower = tile.getUnitPower();
			tile.setImage(pieceType,  0); // mark old tile blank
			x = action.getNewX() - 1;
			y = action.getNewY() - 1;
			// Mark the new tile with the new unit
			((BoardTile) findViewById(StrategoConstants.boardIDs[y][x])).setImage(pieceType, piecePower);
			return; // Handled MOVE_ACTION
		} else if (action.getActionType().equals(StrategoAction.ATTACK_ACTION)) {
			// Handle an attack message
			final int attackerStr = action.getJSONObject("attacker").getInt("value");
			final int defenderStr = action.getJSONObject("defender").getInt("value");
			final int attackerColor = (action.getJSONObject("attacker").getString("type").equals("RedPiece")) ? Color.RED : Color.BLUE;
			final int bluePiece = (attackerColor != Color.RED) ? StrategoConstants.BLUE_PIECES[attackerStr] : StrategoConstants.BLUE_PIECES[defenderStr];
			final int redPiece = (attackerColor != Color.BLUE) ? StrategoConstants.RED_PIECES[attackerStr] : StrategoConstants.RED_PIECES[defenderStr];
			final String attackResult = action.getString("result");
			final boolean redWins = attackerColor == Color.RED &&  !attackResult.equals("ATTACKER_DIES");
			// display the combat dialog
			displayCombatPopup(bluePiece, redPiece, redWins);

			final int oldX = action.getInt("x") - 1;
			final int oldY = action.getInt("y") - 1;
			
			// Update the GUI
			if (!attackResult.equals("ATTACKER_DIES")) {
				final int newX = action.getInt("newX") - 1;
				final int newY = action.getInt("newY") - 1;
				((BoardTile)findViewById(StrategoConstants.boardIDs[oldY][oldX])).setImage(attackerColor, 0); // clear off old tile
				((BoardTile)findViewById(StrategoConstants.boardIDs[newY][newX])).setImage(attackerColor, attackerStr);
			} else {
				// The attacker died, so just eliminate him from the map
				((BoardTile)findViewById(StrategoConstants.boardIDs[oldY][oldX])).setImage(attackerColor, 0); // clear off old tile
			}
			return;
		}
		
		final String pieceType = action.getPieceType();
		int piecePower = action.getPieceValue();
		// If the color isn't our piece, change the value to 13 so we hide it
		if (pieceType.equals("RedPiece") && playerColor != Color.RED || 
				pieceType.equals("BluePiece") && playerColor != Color.BLUE) {
			piecePower = 13;
		}
		final boolean redPiece = pieceType.equals("RedPiece");
		tile.setImage((redPiece) ? Color.RED : Color.BLUE, piecePower);
		
		if (gamePhase.equals("PLACE_PIECES") && piecePower != 13) {
			// TODO set up the numbers here... Also remember to check if the user has a current game and auto join it
			final StringBuilder deploymentID = new StringBuilder("deployment");
			piecePower = action.getPieceValue();
			if (piecePower >= 9) {
				deploymentID.append("1").append(piecePower - 8);
			} else {
				deploymentID.append("0").append(piecePower);
			}
			final BoardTile deploymentTile = (BoardTile) findViewById(getResources().getIdentifier(deploymentID.toString(), "id", getBaseContext().getPackageName()));
			if (deploymentTile != null)
				deploymentTile.useOneUnit();
			setUpDeploymentBoard();
		}
	}
	
	/**
	 * @return - This boards gameID
	 */
	public String getGameID() {
		return gameID;
	}
	
	public void startGameCheckTimer() {
		if (gameStateTimer != null) {
			gameStateTimer.cancel();
			gameStateTimer.purge();
			gameStateTimer = null;
		}
		gameStateTimer = new Timer();
		gameStateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				getNewActions();
			}
		}, 0, 7000);
	}
	
	private void getNewActions() {
		final String url = URLS.GET_GAME_ACTIONS.replace(":id", gameID) + ((latestAction > 0) ? "?lastActionId=" + latestAction : "");
		Log.d("URL for getting newer actions: ", url);
		StrategoHttpTask task = new StrategoHttpTask() {
			@Override
			public String performTask(String... urls) {
				return client.GET(url);
			}
			
			@Override
			public void afterTask(String result) {
				Log.d("new actions", result);
				if (!result.equals("[ ]")) {  // if there are new actions
					try {
						processNewActions(result);
					} catch (JSONException e) {
						Log.e("", "", e);
					}
				}
			}
		};
		task.execute(url);
	}
	
	private void processNewActions(String actions) throws JSONException {
		Log.e("Actions!", actions);
		JSONArray jsonActions = new JSONArray(actions);
		for (int i=0; i < jsonActions.length(); i++) {
			processAction(new StrategoAction(jsonActions.getJSONObject(i)));
		}
		cleanupBoard();
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
		
		if (!deploymentBoardSetup ) {
			((BoardTile) findViewById(R.id.deployment01)).setImage(playerColor, 1).setNumUnit(1); // One Spy
			((BoardTile) findViewById(R.id.deployment02)).setImage(playerColor, 2).setNumUnit(8); // Eight scouts
			((BoardTile) findViewById(R.id.deployment03)).setImage(playerColor, 3).setNumUnit(5); // Five Sappers/Miners
			((BoardTile) findViewById(R.id.deployment04)).setImage(playerColor, 4).setNumUnit(4); // Four Sergeant
			((BoardTile) findViewById(R.id.deployment05)).setImage(playerColor, 5).setNumUnit(4); // Four Lieutenant
			((BoardTile) findViewById(R.id.deployment06)).setImage(playerColor, 6).setNumUnit(4); // Four Captain
			((BoardTile) findViewById(R.id.deployment07)).setImage(playerColor, 7).setNumUnit(3); // Three Majors
			((BoardTile) findViewById(R.id.deployment08)).setImage(playerColor, 8).setNumUnit(2); // Two Colonels
			((BoardTile) findViewById(R.id.deployment11)).setImage(playerColor, 9).setNumUnit(1); // One General
			((BoardTile) findViewById(R.id.deployment12)).setImage(playerColor, 10).setNumUnit(1); // One Marshal
			((BoardTile) findViewById(R.id.deployment13)).setImage(playerColor, 11).setNumUnit(6); // Six bombs
			((BoardTile) findViewById(R.id.deployment14)).setImage(playerColor, 12).setNumUnit(1); // One Flag
			deploymentBoardSetup = true;
		}
		
		Integer[] depZone = (playerColor == Color.RED) ? StrategoConstants.RED_DEP_ZONE : StrategoConstants.BLUE_DEP_ZONE; 
		for (int i = 0; i < depZone.length; i++) {
			// TODO highlight square yellow
			((BoardTile)findViewById(depZone[i])).changeYellow();
		}
	}
	
	public void displayCombatPopup(int bluePiece, int redPiece, boolean redWins) {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(StrategoBoardActivity.this);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setView(new StrategoCombatView(StrategoBoardActivity.this, bluePiece, redPiece, redWins))
			.setTitle("COMBAT");

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.setCancelable(true);
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) StrategoBoardActivity.this.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);//getResources().getDisplayMetrics().density;
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = 380 * (int)metrics.scaledDensity;
	    lp.height = 250 * (int)metrics.scaledDensity;
	    dialog.show();
	    dialog.getWindow().setAttributes(lp);
	}
	
	private void cleanupBoard() {
		if (gamePhase.equals("PLACE_PIECES"))
			return;
		
		for (int x=0; x < 10; x++) {
			for (int y=0; y < 10; y++) {
				((BoardTile) findViewById(StrategoConstants.boardIDs[y][x])).changeBlack();
			}
		}
	}
}
