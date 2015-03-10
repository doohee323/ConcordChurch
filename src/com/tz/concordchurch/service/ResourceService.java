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
  public static String RESOURCE_DOMAIN = "http://52.0.156.206:3000";
  // static String RESOURCE_DOMAIN = "http://192.168.1.17:3000";
  static int CACHE_LV = 2; // 0:no cached, 1:dirty, 2:cached
  static int REFRESH_TIME = 500000; // refresh interval, milisecond
  static boolean bDownloaded = false;

  private JSONArray allResources = new JSONArray();
  private Context mContext;
  private float downYValue;
  private float upYValue;
  private ActionBar actionBar;
  public static final String SD_DIR = Environment.getExternalStorageDirectory().toString();
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
            + myWebView.getContext().getPackageName() + "/databases/");
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        WebView.setWebContentsDebuggingEnabled(true);
      }
      if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
        webSettings.setAllowUniversalAccessFromFileURLs(true);
      }
      myWebView.addJavascriptInterface(new FromWebService(this), "Android");
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
              Toast.makeText(mContext, "true", Toast.LENGTH_SHORT).show();
            }
            break;
          }
          }
          return false;
        }
      });

      String filePath = STORAGE_DIR + "/index.html";
      boolean bStatus = getStatus();
      Toast.makeText(mContext, filePath + " " + bStatus, Toast.LENGTH_SHORT).show();
      if (bStatus && !AppSettings.getAssetsYn()) {
        // String html = AppUtil.getFromFile(filePath,
        // "utf-8").toString();
        Log.d("MainActivity", filePath + "->" + bStatus);
        myWebView.loadUrl("file:///" + STORAGE_DIR + "/index.html");
        // myWebView.loadUrl("javascript:gfRefresh('/api/bunch/v2/me/')");
      } else {
        myWebView.loadUrl("file:///android_asset/www/index.html");
        AppSettings.setAssetsYn(false);
      }
    } catch (Exception e) {
      e.printStackTrace();
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
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(mPreviousThreadPolicy)
        .permitNetwork().permitDiskReads().permitDiskWrites().build());
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
      bDownloaded = false;
      CACHE_LV = 2;
      StrictMode.enableDefaults();
      for (int i = 0; i < allResources.length(); i++) {
        String downloaded = ((JSONObject) allResources.get(i)).getString("downloaded");
        if (downloaded != null && downloaded.equals("n")) {
          Log.d("MainActivity",
              "========>" + ((JSONObject) allResources.get(i)).getString("resource"));
          CACHE_LV = 1;
          break;
        }
      }
      if (CACHE_LV == 2 && allResources.length() > 0) {
        launchWebView(myWebView);
      }
      new GetHttpResourceTask().execute(RESOURCE_DOMAIN + "/resources.json");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  public JSONArray getAllResources() {
    return allResources;
  }

  public boolean getStatus() {
    try {
      if (allResources.length() > 0) {
        for (int i = 0; i < allResources.length(); i++) {
          String filePath = STORAGE_DIR + ((JSONObject) allResources.get(i)).getString("resource");
          if (!new File(filePath).exists()) {
            Log.d("MainActivity",
                "========>" + ((JSONObject) allResources.get(i)).getString("resource"));
            return false;
          }
        }
        bDownloaded = true;
        return true;
      }
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      Log.d("MainActivity", "!!!!!!!!!!!!!!!!!!!!!" + e.getMessage());
      return false;
    }
  }
}
