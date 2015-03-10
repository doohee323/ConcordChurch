package com.tz.concordchurch.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.tz.concordchurch.R;
import com.tz.concordchurch.dao.WordLogDao;
import com.tz.concordchurch.receiver.AppSettings;
import com.tz.concordchurch.service.ResourceService;
import com.tz.concordchurch.util.DeviceProperties;

public class MainActivity extends Activity {

	//static String RESOURCE_DOMAIN = "http://192.168.43.23:3005";
	static String RESOURCE_DOMAIN = "http://52.0.156.206:3000";
	//static String RESOURCE_DOMAIN = "http://192.168.1.17:3000"; 
	static int CACHE_LV = 2; // 0:no cached, 1:dirty, 2:cached
	static int REFRESH_TIME = 500000; // refresh interval, milisecond

	private WebView myWebView = null;
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
			myWebView = (WebView) findViewById(R.id.webview);
			ResourceService.getInstance().launchWebView(myWebView);

			Timer progressTimer = new Timer();
			ProgressTimerTask timeTask = new ProgressTimerTask();
			progressTimer.scheduleAtFixedRate(timeTask, 0, REFRESH_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		WordLogDao dao = new WordLogDao(mContext);
		dao.open();
//		dao.drop();
	}

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
					ResourceService.getInstance().refresh(myWebView);
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
