/**
 * 
 */
package com.gmu.stratego.json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Tim
 * 
 *         { "type" : "PlacePieceAction", 
 *           "user" : { 
 *              "username" : "RedPlayer",
 *              "admin" : false, 
 *              "_id" : "53590d3cb9689c64d11ca52d" 
 *            }, 
 *         "x" : 1, 
 *         "y" : 2, 
 *         "piece" : { 
 *            "type" : "RedPiece", 
 *            "value" : 2 
 *         }, 
 *         "actionId" : 3 }
 */
public class StrategoAction extends JSONObject {
	final public static String PLACE_PIECE = "PlacePieceAction";
	final public static String REPLACE_PIECE = "ReplacePieceAction";
	final public static String COMMIT_ACTION = "CommitAction";
	final public static String MOVE_ACTION = "MoveAction";
	final public static String ATTACK_ACTION = "AttackAction";
	final public static String RED_PIECE = "RedPiece";
	final public static String BLUE_PIECE = "BluePiece";
	
	public StrategoAction(final String actionType, final String username, final boolean admin, 
			final String _id, final int x, final int y, final String pieceType,
			final int pieceValue, final String actionID) throws JSONException {
		super();
		put("user", new JSONObject());
		put("piece", new JSONObject());
		setActionType(actionType);
		setUsername(username);
		setAdmin(admin);
		setID(_id);
		setX(x);
		setY(y);
		setPieceType(pieceType);
		setPieceValue(pieceValue);
		setActionID(actionID);
	}
	
	public void setActionID(final String actionID) throws JSONException {
		put("actionId", actionID);
	}
	
	public String getActionID() throws JSONException {
		return getString("actionId");
	}
	
	public void setPieceValue(final int value) throws JSONException {
		getJSONObject("piece").put("value", value);
	}
	
	public int getPieceValue() throws JSONException {
		return getJSONObject("piece").getInt("value");
	}
	
	public void setPieceType(final String pieceType) throws JSONException {
		getJSONObject("piece").put("type", pieceType);
	}
	
	public String getPieceType() throws JSONException {
		return getJSONObject("piece").getString("type");
	}
	
	public void setActionType(final String type) throws JSONException {
		put("type", type);
	}
	
	public String getActionType() throws JSONException {
		return getString("type");
	}
	
	public void setUsername(final String username) throws JSONException {
		getJSONObject("user").put("username", username);
	}
	
	public String getUsername() throws JSONException {
		return getJSONObject("user").getString("username");
	}
	
	public void setAdmin(final boolean admin) throws JSONException {
		getJSONObject("user").put("admin", admin);
	}
	
	public boolean getAdmin() throws JSONException {
		return getJSONObject("user").getBoolean("admin");
	}
	
	public void setID(final String id) throws JSONException {
		getJSONObject("user").put("_id", id);
	}
	
	public String getID() throws JSONException {
		return getJSONObject("user").getString("_id");
	}
	
	public void setX(final int x) throws JSONException {
		put("x", x);
	}
	
	public int getX() throws JSONException {
		return getInt("x");
	}
	
	public void setY(final int y) throws JSONException {
		put("y", y);
	}
	
	public int getY() throws JSONException {
		return getInt("y");
	}
}