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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author Tim
 *
 */
public class StrategoClient {
	
	private static StrategoClient instance;
	private final ConnectivityManager connMgr;
	
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
			HttpResponse httpResponse = httpClient.execute(httpGet);
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
			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);

			// 3. build jsonObject

			// 4. convert JSONObject to JSON to String
			final String jsonString = json.toString();

			// ** Alternative way to convert Person object to JSON string usin
			// Jackson Lib
			// ObjectMapper mapper = new ObjectMapper();
			// json = mapper.writeValueAsString(person);

			// 5. set json to StringEntity
			final StringEntity se = new StringEntity(jsonString);

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// 7. Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// 10. convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		// 11. return result
		return result;
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
}
