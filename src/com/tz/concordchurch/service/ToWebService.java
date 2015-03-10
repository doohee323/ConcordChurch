package com.tz.concordchurch.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ToWebService extends WebViewClient {
  boolean timeout = true;
  private Context mContext;

  public ToWebService(Context mContext) {
    this.mContext = mContext;
  }

  @Override
  public void onPageStarted(WebView view, String url, Bitmap favicon) {
    Runnable run = new Runnable() {
      public void run() {
        if (timeout) {
          System.out.println("");
        }
      }
    };
    super.onPageStarted(view, url, favicon);
  }

  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    if (url.startsWith("http://www.youtube.com/")) {
      final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      mContext.startActivity(intent);
      return true;
    } else if (url.endsWith(".mp4")) {
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(url));
      mContext.startActivity(i);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void onPageFinished(WebView view, String url) {
    timeout = false;
    super.onPageFinished(view, url);
  }

}
