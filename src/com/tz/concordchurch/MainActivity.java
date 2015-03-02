package com.tz.concordchurch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	// static String RESOURCE_DOMAIN = "http://192.168.43.23:3005";
	static String RESOURCE_DOMAIN = "http://52.0.156.206:3000";
	// static String RESOURCE_DOMAIN = "http://192.168.1.17:3000";
	static int CACHE_LV = 2; // 0:no cached, 1:dirty, 2:cached
	static int REFRESH_TIME = 500000; // refresh interval, milisecond
	
	public WebView myWebView = null;
	JSONArray allResources = new JSONArray();
	Context mContext;
	float downYValue;
	float upYValue;
	ActionBar actionBar;
	public static final String SD_DIR = Environment
			.getExternalStorageDirectory().toString();
	public static final String STORAGE_DIR = SD_DIR + "/churchapp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		super.onCreate(savedInstanceState);

		mContext = getBaseContext();
		setContentView(R.layout.activity_main);
		try {
			AppSettings.setAssetsYn(true);
			launchWebView();

			Timer progressTimer = new Timer();
			ProgressTimerTask timeTask = new ProgressTimerTask();
			progressTimer.scheduleAtFixedRate(timeTask, 0, REFRESH_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// myWebView.loadUrl("http://192.168.1.5:3000");
	// ��������������������� ������������������ ������������ ������������ ������
	// 1. /ConcordChurch/assets/www/index.html ������ ������ ������
	// myWebView.loadUrl("file:///android_asset/www/index.html");
	// AssetManager am = getAssets();
	// am.openNonAssetFd("assets/test.png");
	// 2. ������ ������������ ������ ��������� ������
	// myWebView.loadUrl("file:///" + STORAGE_DIR + "/index.html");
	// 3. ������ ������������ ������ string������ ������
	// myWebView.loadDataWithBaseURL("file:///" + STORAGE_DIR, html,
	// "text/html", "utf-8", null);

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onBackPressed() {
		if (myWebView.canGoBack()) {
			myWebView.goBack();
		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private StrictMode.ThreadPolicy mPreviousThreadPolicy;

	/**
	 * Prevent exceptions from doing disk and network operations in a service
	 */
	protected void setPermissiveThreadPolicy() {
		// set StrictMode to allow network/disk in service
		// StrictMode.enableDefaults();
		mPreviousThreadPolicy = StrictMode.getThreadPolicy();
		StrictMode.enableDefaults();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(
				mPreviousThreadPolicy).permitNetwork().permitDiskReads()
				.permitDiskWrites().build());
	}

	/**
	 * Reset thread policy to previously known state for consistency
	 */
	protected void resetThreadPolicy() {
		if (mPreviousThreadPolicy != null) {
			// reset to old policy
			StrictMode.setThreadPolicy(mPreviousThreadPolicy);
		}
	}

	private class ProgressTimerTask extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					refreshResource();
				}
			});
		}
	}

	private Button rec, end, play, stop;

	private int x, y, c;
	protected boolean clicked = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
			myWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (clicked) {
			x = (int) event.getX();
			y = (int) event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_UP:
			}
			Log.d("MainActivity", "x = " + x + ", y = " + y);
		}
		return false;
	};

	public void onClick(View v) {
		if (v == rec) {
			clicked = true;
		} else if (v == end) {
			clicked = false;
		} else if (v == play) {
		} else if (v == stop) {
		}
	}
}
