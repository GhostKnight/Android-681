package com.gmu.stratego.client;

import android.os.AsyncTask;

public abstract class StrategoHttpTask extends AsyncTask<String, Void, String> {
	
	public StrategoHttpTask() {
	}
	
	@Override
	protected String doInBackground(String... urls) {
		return performTask(urls);
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		afterTask(result);
	}

	public abstract String performTask(String... urls);
	public abstract void afterTask(String result);
}
