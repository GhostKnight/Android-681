/**
 * 
 */
package com.gmu.stratego.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author Tim
 *
 * This class holds the session and all the GET and POST code
 * that the program uses in order to communicate with the server.
 */
public class StrategoClient {
	
	private static StrategoClient instance;
	private final ConnectivityManager connMgr;
	private final CookieStore cookieStore = new BasicCookieStore();
	private final HttpContext httpContext = new BasicHttpContext();
	private JSONObject user;
	
	/**
	 * This is called by the main activity when the application first starts
	 * @param mainActivity - reference to the main activity
	 * @return
	 */
	public static StrategoClient getInstance(final Activity mainActivity) {
		if (instance == null) {
			instance = new StrategoClient(mainActivity);
		}
		return instance;
	}
	
	public static StrategoClient getInstance(){
		return instance;
	}
	
	private StrategoClient(Activity mainActivity) {
		connMgr = (ConnectivityManager) mainActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	/**
	 * Tests for Connectivity
	 * 
	 * @return
	 */
	public boolean isConnected() {
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}
	
	public String GET(String url) {
		String result = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(new URI(url));
			HttpResponse httpResponse = httpClient.execute(httpGet, httpContext);
			final InputStream msgContent = httpResponse.getEntity().getContent();
			result = convertInputStreamToString(msgContent);
		} catch (Exception e) {
			Log.e("Error", "Error GET", e);
		}
		
		return result;
	}

	public String POST(String url, JSONObject json) {
		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			// Convert the given json object to its stirng form
			final String jsonString = json.toString();
			final StringEntity se = new StringEntity(jsonString);
			// Set the contents of the post
			httpPost.setEntity(se);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader("CsrfToken", "nocheck");
			// Execute the POST
			HttpResponse httpResponse = httpclient.execute(httpPost, httpContext);
			// Get the response
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		
		return result;
	}
	
	/**
	 * This method should only be called when a user logs out, this will
	 * clear the cookiestore and cause the cookies/sessions that were there
	 * to be garbage collected by java
	 */
	public void resetSession() {
		user = null;
		cookieStore.clear();
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	public void setUser(JSONObject user) {
		this.user = user;
	}
	
	public JSONObject getUser() {
		return user;
	}
}
