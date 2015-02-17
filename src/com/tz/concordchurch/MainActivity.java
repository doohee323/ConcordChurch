package com.tz.concordchurch;

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

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends ActionBarActivity {

	static String STORAGE_DIR = null;
	static String RESOURCE_DOMAIN = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onStart() {
		StrictMode.enableDefaults();
		WebView myWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
		myWebView.setWebChromeClient(new CustomWebChromeClient());
		// myWebView.loadUrl("http://52.0.156.206:3000");
		STORAGE_DIR = getApplicationContext().getFilesDir().getAbsolutePath()
				+ File.separator;

		try {
			// Thread.sleep(10000);
			// new GetHttpResourceTask()
			// .execute("http://52.0.156.206:3000/resources.json");
			
			String fileNm = STORAGE_DIR + "index.html";
			File test = new File(fileNm);
			System.out.println(fileNm + "->"
					+ test.exists());
			myWebView.loadUrl("file:///" + STORAGE_DIR + "index.html");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// getFileFromURL("http://52.0.156.206:3000/index.html",
		// getApplicationContext());

		// myWebView.loadDataWithBaseURL("not_needed",
		// "test",
		// "text/html",
		// "utf-8",
		// "not_needed");
		super.onStart();
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
				Boolean forceYn = json.getBoolean("forceYn");
				JSONArray resources = json.getJSONArray("resources");
				for (int i = 0; i < resources.length(); i++) {
					String resource = ((JSONObject) resources.get(i))
							.getString("resource");
					String cachelevel = ((JSONObject) resources.get(i))
							.getString("cachelevel");
					String version = ((JSONObject) resources.get(i))
							.getString("version");
					resource = resource.substring(1, resource.length());
					getResources(resource);
				}
				System.out.println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String callbackResources(String src) {
			return "";
		}

		public String getResources(String fileNm) {
			String filePath = null;
			try {
				filePath = STORAGE_DIR + fileNm;
				File dir = new File(STORAGE_DIR);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(filePath);
				if (file.exists()) {
				} else {
					// StrictMode.enableDefaults();
					new GetHttpResourceTask().execute(RESOURCE_DOMAIN + "/"
							+ fileNm);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return filePath;
		}
	}

	class GetHttpResourceTask extends AsyncTask<String, Void, InputStream> {
		private WebResource listener = new WebResource();
		private String fileNm = null;

		@Override
		protected InputStream doInBackground(String... urls) {
			InputStream result = null;
			try {
				String strUrl = urls[0];
				fileNm = strUrl.substring(strUrl.lastIndexOf("/") + 1,
						strUrl.length());
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
					output = new FileOutputStream(STORAGE_DIR + fileNm);
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
						File test = new File(STORAGE_DIR + fileNm);
						System.out.println(STORAGE_DIR + fileNm + "->"
								+ test.exists());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				listener.callbackResources("");
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
}
