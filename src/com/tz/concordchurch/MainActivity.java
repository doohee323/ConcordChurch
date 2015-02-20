package com.tz.concordchurch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends ActionBarActivity {

	// static String RESOURCE_DOMAIN = "http://192.168.43.23:3005";
	static String RESOURCE_DOMAIN = "http://52.0.156.206:3000";
	static Boolean FORCE_YN = false;
	static Boolean ASSETS_YN = false;
	static Boolean START_YN = false;
	public WebView myWebView = null;

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
		setContentView(R.layout.activity_main);
		StrictMode.enableDefaults();
		try {
			WebResource listener = new WebResource();
			ASSETS_YN = true;
			listener.launchWebView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// myWebView.loadUrl("http://192.168.1.5:3000");
	// 안드로이드에서 로컬디스크의 웹자원에 접근하는 방법
	// 1. /ConcordChurch/assets/www/index.html 등을 넣고 접근
	// myWebView.loadUrl("file:///android_asset/www/index.html");
	// AssetManager am = getAssets();
	// am.openNonAssetFd("assets/test.png");
	// 2. 외부 저장소에 넣고 파일로 접근
	// myWebView.loadUrl("file:///" + STORAGE_DIR + "/index.html");
	// 3. 외부 저장소에 넣고 string으로 접근
	// myWebView.loadDataWithBaseURL("file:///" + STORAGE_DIR, html,
	// "text/html", "utf-8", null);

	@Override
	protected void onStart() {
		if(!START_YN) {
			START_YN = true;
			StrictMode.enableDefaults();
			try {
				new GetHttpResourceTask().execute(RESOURCE_DOMAIN
						+ "/resources.json");
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onStart();
		}
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
				FORCE_YN = json.getBoolean("forceYn");
				JSONArray resources = json.getJSONArray("resources");
				for (int i = 0; i < resources.length(); i++) {
					String resource = ((JSONObject) resources.get(i))
							.getString("resource");
					String cachelevel = ((JSONObject) resources.get(i))
							.getString("cachelevel");
					String version = ((JSONObject) resources.get(i))
							.getString("version");
					resource = resource.substring(1, resource.length());
					System.out.println("resource=========>" + resource);
					getResources(resource);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void callbackResources(String fileNm) {
			if (fileNm.equals("index.html")) {
				START_YN = false;
				if (!ASSETS_YN) {
					launchWebView();
					FORCE_YN = false;
				} else {
					ASSETS_YN = false;
				}
			}
		}

		@SuppressLint("SetJavaScriptEnabled")
		public void launchWebView() {
			try {
				myWebView = (WebView) findViewById(R.id.webview);
				WebSettings webSettings = myWebView.getSettings();
				webSettings.setJavaScriptEnabled(true);
				webSettings.setBuiltInZoomControls(true);

				webSettings.setDomStorageEnabled(true);
				webSettings.setDatabaseEnabled(true);
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
					File databasePath = getDatabasePath("yourDbName");
					webSettings.setDatabasePath(databasePath.getPath());
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
				// myWebView.setWebChromeClient(new WebChromeClient() {
				// });
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					WebView.setWebContentsDebuggingEnabled(true);
				}
				if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
					myWebView.getSettings()
							.setAllowUniversalAccessFromFileURLs(true);
				}

				// myWebView.addJavascriptInterface(new WebAppInterface(this),
				// "Android");
				myWebView.setWebChromeClient(new CustomWebChromeClient());

				String filePath = STORAGE_DIR + "/index.html";
				File file = new File(filePath);
				if (file.exists() && !ASSETS_YN) {
					// String html = getFromFile(filePath, "utf-8").toString();
					System.out.println(filePath + "->" + file.exists());
					myWebView.loadUrl("file:///" + STORAGE_DIR + "/index.html");
				} else {
					myWebView.loadUrl("file:///android_asset/www/index.html");
					ASSETS_YN = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String getResources(String fileNm) {
			WebResource listener = new WebResource();
			String filePath = null;
			try {
				filePath = STORAGE_DIR + "/" + fileNm;
				File file = new File(filePath);
				if (file.exists() && !FORCE_YN) {
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
						System.out
								.println(STORAGE_DIR
										+ "/"
										+ fileNm
										+ "->"
										+ new File(STORAGE_DIR + "/" + fileNm)
												.exists());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				listener.callbackResources(fileNm);
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

	/**
	 * <pre>
	 * </pre>
	 *
	 * @param file
	 * @param strChar
	 * @return String
	 */
	public static StringBuffer getFromFile(String fileName, String strChar)
			throws IOException {
		if (strChar == null) {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(new File(fileName))
					.useDelimiter("\\Z");
			String contents = scanner.next();
			scanner.close();
			return new StringBuffer(contents);
		}

		if (strChar.equals(""))
			strChar = null;

		StringBuffer sb = new StringBuffer(1000);
		InputStreamReader is = null;
		BufferedReader in = null;
		String lineSep = System.getProperty("line.separator");

		try {
			File f = new File(fileName);
			if (f.exists()) {
				if (strChar != null)
					is = new InputStreamReader(new FileInputStream(f), strChar);
				else
					is = new InputStreamReader(new FileInputStream(f));
				in = new BufferedReader(is);
				String str = "";

				@SuppressWarnings("unused")
				int readed = 0;
				while ((str = in.readLine()) != null) {
					if (strChar != null)
						readed += (str.getBytes(strChar).length);
					else
						readed += (str.getBytes().length);
					sb.append(str + lineSep);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				is.close();
			if (in != null)
				in.close();
		}
		return sb;
	}
}
