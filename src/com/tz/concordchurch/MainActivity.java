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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends ActionBarActivity {

	// static String RESOURCE_DOMAIN = "http://192.168.43.23:3005";
	static String RESOURCE_DOMAIN = "http://52.0.156.206:3000";
	// static String RESOURCE_DOMAIN = "http://192.168.1.17:3000";
	static int CACHE_LV = 2; // 0:no cached, 1:dirty, 2:cached
	static int REFRESH_TIME = 500000; // refresh interval, milisecond
	static Boolean ASSETS_YN = false;
	public WebView myWebView = null;
	JSONArray allResources = new JSONArray();
	Context mContext;

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
		
//		if (Build.VERSION.SDK_INT < 16) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
		
		mContext = getBaseContext();
		
//	    //Remove notification bar
//	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);


		try {
			StrictMode.enableDefaults();
			ASSETS_YN = true;

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
					System.out.println("resource=========>" + resource);
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
					if (!ASSETS_YN) {
						// launchWebView();
					} else {
						ASSETS_YN = false;
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
					System.out.println("filePath exist ==> " + filePath);
					listener.callbackResources(fileNm);
				} else {
					System.out.println("filePath not exist ==> " + filePath);
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
	public void launchWebView() {
		try {
			myWebView = (WebView) findViewById(R.id.webview);
			myWebView.setBackgroundColor(Color.RED);
			WebSettings webSettings = myWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setBuiltInZoomControls(true);
			webSettings.setDomStorageEnabled(true);
			webSettings.setDatabaseEnabled(true);
			webSettings.setBuiltInZoomControls(true);
			webSettings.setDisplayZoomControls(false);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
				webSettings.setDatabasePath(mContext.getFilesDir().getPath()
						+ myWebView.getContext().getPackageName()
						+ "/databases/");
			}
			
			myWebView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
				}
			});
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				WebView.setWebContentsDebuggingEnabled(true);
			}
			if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
				webSettings.setAllowUniversalAccessFromFileURLs(true);
			}
			myWebView.addJavascriptInterface(new WebAppInterface(this),
					"Android");
			myWebView.setWebChromeClient(new CustomWebChromeClient());

			String filePath = STORAGE_DIR + "/index.html";
			File file = new File(filePath);
			// Toast.makeText(mContext, Boolean.toString(file.exists()),
			// Toast.LENGTH_SHORT).show();
			if (file.exists() && !ASSETS_YN) {
				// String html = AppUtil.getFromFile(filePath,
				// "utf-8").toString();
				System.out.println(filePath + "->" + file.exists());
				myWebView.loadUrl("file:///" + STORAGE_DIR + "/index.html");
				myWebView.loadUrl("javascript:gfRefresh('/api/bunch/v2/me/')");
			} else {
				myWebView.loadUrl("file:///android_asset/www/index.html");
				ASSETS_YN = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class GetHttpResourceTask extends AsyncTask<String, Void, InputStream> {
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
						System.out.println(filePath + "/" + fileNm + "->"
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

	private void refreshResource() {
		try {
			CACHE_LV = 2;
			StrictMode.enableDefaults();
			for (int i = 0; i < allResources.length(); i++) {
				String downloaded = ((JSONObject) allResources.get(i))
						.getString("downloaded");
				if (downloaded != null && downloaded.equals("n")) {
					System.out.println("========>"
							+ ((JSONObject) allResources.get(i))
									.getString("resource"));
					CACHE_LV = 1;
					break;
				}
			}
			if (CACHE_LV == 2 && allResources.length() > 0) {
				launchWebView();
			}
			new GetHttpResourceTask().execute(RESOURCE_DOMAIN
					+ "/resources.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
