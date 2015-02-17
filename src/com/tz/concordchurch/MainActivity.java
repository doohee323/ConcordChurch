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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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

import com.google.gson.Gson;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		//
		try {
			Thread.sleep(10000);
			new GetHttpResourceTask()
					.execute("http://52.0.156.206:3000/resources.json");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// getFileFromURL("http://52.0.156.206:3000/index.html",
		// getApplicationContext());
		// myWebView.loadUrl("file:///"
		// + getApplicationContext().getFilesDir().getAbsolutePath()
		// + "/index.html");

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
		JSONObject result = new JSONObject();
		public void getResourceJson(String src) {
			try {
				//{\"domain\":\"http://52.0.156.206:3000\",\"forceYn\":false,\"resources\":[{\"resource\":\"/.htaccess\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/404.html\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/favicon.ico\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/index.html\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/robots.txt\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/bower_components/bootstrap-sass-official/assets/fonts/bootstrap/glyphicons-halflings-regular.eot\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/bower_components/bootstrap-sass-official/assets/fonts/bootstrap/glyphicons-halflings-regular.svg\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/bower_components/bootstrap-sass-official/assets/fonts/bootstrap/glyphicons-halflings-regular.ttf\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/bower_components/bootstrap-sass-official/assets/fonts/bootstrap/glyphicons-halflings-regular.woff\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/images/close.de3edd72.png\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/images/extend.418cf82a.png\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/images/fb.b8cdf51b.png\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/images/gp.c7c05ca3.png\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/images/next.a1320497.png\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/images/playstore.d635d155.png\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/images/reload.df4cb324.png\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/images/share.9b4d8f9b.png\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/images/tw.3f76236c.png\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/scripts/oldieshim.a466b7b1.js\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/scripts/scripts.c0e05398.js\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/scripts/vendor.7e20cb2d.js\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/jquery.smartbanner.ada8f1d5.css\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/main.cfc3bc9e.css\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/main.scss\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/style.efa191c6.css\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/views/bottom-sheet-grid-template.html\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/views/bottom-sheet-list-template.html\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/views/main.html\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/views/test.html\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/views/test0.html\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/views/video.html\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"},{\"resource\":\"/views/words.html\", \"cachelevel\":\"static\", \"version\":\"2014.02.16.1\"}]}
				
				JSONArray rootOfPage =  new JSONArray(src);
				Gson gson = new Gson();
				JSONObject orderHeader = gson.fromJson(src, JSONObject.class);
				result.put("test", src);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public String getAllResources(String src) {
			String filePath = null;
			try {
				String test = result.getString("test");

				filePath = src
						.substring(src.lastIndexOf("/") + 1, src.length());
				String path = getApplicationContext().getFilesDir()
						.getAbsolutePath() + File.separator + filePath;
				File file = new File(path);
				if (file.exists()) {
				} else {
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
			String strUrl = urls[0];
			fileNm = strUrl.substring(strUrl.lastIndexOf("/") + 1,
					strUrl.length());

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(strUrl);

			// no idea what this does :)
			// httppost.setEntity(new UrlEncodedFormEntity(postParameters));

			// This is the line that send the request
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				result = entity.getContent();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(InputStream input) {
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();
			String line;
			try {
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
			if (fileNm.equals("resources.json")) {
				listener.getResourceJson(sb.toString());
			} else {
				listener.getAllResources(sb.toString());
			}
		}
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
