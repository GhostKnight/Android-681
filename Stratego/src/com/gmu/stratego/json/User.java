package com.gmu.stratego.json;

import org.json.JSONException;
import org.json.JSONObject;

public class User extends JSONObject {
	public User(String username, String password) throws JSONException {
		setUsername(username);
		setPassword(password);
	}
	
	public void setUsername(String username) throws JSONException {
		put("username", username);
	}
	
	public String getUsername() throws JSONException {
		return getString("username");
	}
	
	public void setPassword(String password) throws JSONException {
		put("password", password);
	}
	
	public String getPassword() throws JSONException {
		return getString("password");
	}
}
