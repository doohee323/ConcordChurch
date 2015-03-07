package com.tz.concordchurch.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

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
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.tz.concordchurch.receiver.AppSettings;
import com.tz.concordchurch.util.AppUtil;
import com.tz.concordchurch.util.FileUtil;

public class ResourceService extends Service {

	// static String RESOURCE_DOMAIN = "http://192.168.43.23:3005";
	static String RESOURCE_DOMAIN = "http://52.0.156.206:3000";
	// static String RESOURCE_DOMAIN = "http://192.168.1.17:3000";
	static int CACHE_LV = 2; // 0:no cached, 1:dirty, 2:cached
	static int REFRESH_TIME = 500000; // refresh interval, milisecond

	private JSONArray allResources = new JSONArray();
	private Context mContext;
	private float downYValue;
	private float upYValue;
	private ActionBar actionBar;
	public static final String SD_DIR = Environment
			.getExternalStorageDirectory().toString();
	public static final String STORAGE_DIR = SD_DIR + "/churchapp";

	private static ResourceService instance;

	public static ResourceService getInstance() {
		if (instance == null) {
			instance = new ResourceService();
			instance.refresh(null);
		}
		return instance;
	}

	public void init(Context mContext) {
		this.mContext = mContext;
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

	public class CustomWebChromeClient extends WebChromeClient {
		@Override
		public void onReceivedIcon(WebView view, Bitmap icon) {
			super.onReceivedIcon(view, icon);
		}
	}

	public class WebResource {
		public void callbackResourceJson(String src) {
			try {
				JSONObject json = new JSONObject(src);
				RESOURCE_DOMAIN = json.getString("domain");
				if (json.getBoolean("forceYn"))
					CACHE_LV = 0;
				JSONArray resources = json.getJSONArray("resources");
				if (allResources.length() == 0)
					allResources = resources;
				for (int i = 0; i < resources.length(); i++) {
					String resource = ((JSONObject) resources.get(i))
							.getString("resource");
					String cachelevel = ((JSONObject) resources.get(i))
							.getString("cachelevel");
					String version = ((JSONObject) resources.get(i))
							.getString("version");
					resource = resource.substring(1, resource.length());

					String downloaded = "y";
					String prevVersion = ((JSONObject) allResources.get(i))
							.getString("version");
					Long prevDownload = (long) 0;
					if (!((JSONObject) allResources.get(i))
							.isNull("downloaded_at")) {
						prevDownload = ((JSONObject) allResources.get(i))
								.getLong("downloaded_at");
					}
					if (!prevVersion.equals(version)) {
						downloaded = "n";
					} else {
						if (cachelevel.equals("nocache")) {
							downloaded = "n";
						} else if (cachelevel.equals("static")) {
							version = version.replaceAll("\\.", "")
									.replaceAll(" ", "").replaceAll(":", "")
									.replaceAll("-", "");
							if (AppUtil.getDate() < Long.parseLong(version)
									&& prevDownload < Long.parseLong(version)) {
								downloaded = "n";
							}
						}
					}
					Log.d("MainActivity", "resource=========>" + resource);
					((JSONObject) allResources.get(i)).put("downloaded",
							downloaded);
					getResources(resource);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void callbackResources(String filePath) {
			try {
				for (int i = 0; i < allResources.length(); i++) {
					String resource;
					resource = ((JSONObject) allResources.get(i))
							.getString("resource");
					if (filePath.equals(resource)) {
						((JSONObject) allResources.get(i)).put("downloaded",
								"y");
						((JSONObject) allResources.get(i)).put("downloaded_at",
								AppUtil.getDate());
						break;
					}
				}
				if (filePath.equals("/index.html")) {
					if (!AppSettings.getAssetsYn()) {
						// launchWebView();
					} else {
						AppSettings.setAssetsYn(false);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public String getResources(String fileNm) {
			WebResource listener = new WebResource();
			String filePath = null;
			try {
				filePath = STORAGE_DIR + "/" + fileNm;
				File file = new File(filePath);
				if (file.exists() && CACHE_LV == 2) {
					Log.d("MainActivity", "filePath exist ==> " + filePath);
					// temporary
					FileUtil.removeDIR(STORAGE_DIR);
					listener.callbackResources(fileNm);
				} else {
					Log.d("MainActivity", "filePath not exist ==> " + filePath);
					filePath = STORAGE_DIR + "/" + fileNm;
					filePath = filePath.substring(0, filePath.lastIndexOf("/"));
					File dir = new File(filePath);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					new GetHttpResourceTask().execute(RESOURCE_DOMAIN + "/"
							+ fileNm, filePath);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return filePath;
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public void launchWebView(WebView myWebView) {
		try {
			StrictMode.enableDefaults();
			// myWebView.setBackgroundColor(Color.TRANSPARENT);
			myWebView.setWebViewClient(new ToWebService(mContext));
			WebSettings webSettings = myWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDomStorageEnabled(true);
			webSettings.setDatabaseEnabled(true);
			webSettings.setBuiltInZoomControls(true);
			webSettings.setDisplayZoomControls(false);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
				webSettings.setDatabasePath(mContext.getFilesDir().getPath()
						+ myWebView.getContext().getPackageName()
						+ "/databases/");
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				WebView.setWebContentsDebuggingEnabled(true);
			}
			if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
				webSettings.setAllowUniversalAccessFromFileURLs(true);
			}
			myWebView
					.addJavascriptInterface(new FromWebService(this), "Android");
			myWebView.setWebChromeClient(new CustomWebChromeClient());

			myWebView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						downYValue = event.getY();
						break;
					}
					case MotionEvent.ACTION_UP: {
						upYValue = event.getY();
						if ((upYValue - downYValue) > 1000) {
							downYValue = 0;
							upYValue = 0;
							Toast.makeText(mContext, "true", Toast.LENGTH_SHORT)
									.show();
						}
						break;
					}
					}
					return false;
				}
			});

			String filePath = STORAGE_DIR + "/index.html";
			File file = new File(filePath);
			Toast.makeText(mContext, Boolean.toString(file.exists()),
					Toast.LENGTH_SHORT).show();
			if (file.exists() && !AppSettings.getAssetsYn()) {
				// String html = AppUtil.getFromFile(filePath,
				// "utf-8").toString();
				Log.d("MainActivity", filePath + "->" + file.exists());
				myWebView.loadUrl("file:///" + STORAGE_DIR + "/index.html");
				myWebView.loadUrl("javascript:gfRefresh('/api/bunch/v2/me/')");
			} else {
				myWebView.loadUrl("file:///android_asset/www/index.html");
				AppSettings.setAssetsYn(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class GetHttpResourceTask extends
			AsyncTask<String, Void, InputStream> {
		private WebResource listener = new WebResource();
		private String fileNm = null;
		private String filePath = null;

		@Override
		protected InputStream doInBackground(String... urls) {
			InputStream result = null;
			try {
				String strUrl = urls[0];
				fileNm = strUrl.substring(strUrl.lastIndexOf("/") + 1,
						strUrl.length());
				if (urls.length > 1)
					filePath = urls[1];
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(strUrl);
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				result = entity.getContent();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(InputStream input) {
			if (fileNm.equals("resources.json")) {
				BufferedReader br = null;
				StringBuilder sb = new StringBuilder();
				try {
					String line;
					br = new BufferedReader(new InputStreamReader(input));
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				listener.callbackResourceJson(sb.toString());
			} else {
				OutputStream output = null;
				try {
					byte[] buffer = new byte[8 * 1024];
					output = new FileOutputStream(filePath + "/" + fileNm);
					int bytesRead;
					while ((bytesRead = input.read(buffer)) != -1) {
						output.write(buffer, 0, bytesRead);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						output.close();
						input.close();
						Log.d("MainActivity", filePath + "/" + fileNm + "->"
								+ new File(filePath + "/" + fileNm).exists());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				listener.callbackResources(filePath.substring(
						STORAGE_DIR.length(), filePath.length())
						+ "/" + fileNm);
			}
		}
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
			StrictMode.setThreadPolicy(mPreviousThreadPolicy);
		}
	}

	public void refresh(WebView myWebView) {
		try {
			CACHE_LV = 2;
			StrictMode.enableDefaults();
			for (int i = 0; i < allResources.length(); i++) {
				String downloaded = ((JSONObject) allResources.get(i))
						.getString("downloaded");
				if (downloaded != null && downloaded.equals("n")) {
					Log.d("MainActivity",
							"========>"
									+ ((JSONObject) allResources.get(i))
											.getString("resource"));
					CACHE_LV = 1;
					break;
				}
			}
			if (CACHE_LV == 2 && allResources.length() > 0) {
				launchWebView(myWebView);
			}
			new GetHttpResourceTask().execute(RESOURCE_DOMAIN
					+ "/resources.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
