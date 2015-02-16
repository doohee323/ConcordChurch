package com.tz.concordchurch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		WebView myWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
		// myWebView.loadUrl("http://52.0.156.206:3000");
		//
		getAllFileFromURL("http://52.0.156.206:3000/resources.json",
				getApplicationContext());
		getFileFromURL("http://52.0.156.206:3000/index.html",
				getApplicationContext());
		myWebView.loadUrl("file:///"
				+ getApplicationContext().getFilesDir().getAbsolutePath()
				+ "/index.html");

		// myWebView.loadDataWithBaseURL("not_needed",
		// "test",
		// "text/html",
		// "utf-8",
		// "not_needed");
	}

	public static String getAllFileFromURL(String src, Context context) {
		String filePath = File.separator;
		try {
			String path = context.getFilesDir().getAbsolutePath();
			File file = new File(path);
			if (file.exists()) {
				// File exists
			} else {
				URL url = new URL(src);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				BufferedReader br = null;
				StringBuilder sb = new StringBuilder();
				String line;
				try {
					br = new BufferedReader(new InputStreamReader(input));
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return filePath;
	}

	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	public static String getFileFromURL(String src, Context context) {
		String filePath = File.separator;
		try {
			String path = context.getFilesDir().getAbsolutePath();
			File file = new File(path);
			if (file.exists()) {
				// File exists
			} else {
				URL url = new URL(src);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				OutputStream output = new FileOutputStream(file);
				try {
					final byte[] buffer = new byte[1024];
					int read;
					while ((read = input.read(buffer)) != -1)
						output.write(buffer, 0, read);
					output.flush();
				} finally {
					output.close();
					input.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return filePath;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
