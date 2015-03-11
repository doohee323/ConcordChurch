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

import android.os.AsyncTask;
import android.util.Log;

import com.tz.concordchurch.util.FileUtil;

public class GetHttpResourceTask extends AsyncTask<String, Void, InputStream> {
  private WebResource listener = new WebResource();
  private String fileNm = null;
  private String filePath = null;

  @Override
  protected InputStream doInBackground(String... urls) {
    InputStream result = null;
    try {
      String strUrl = urls[0];
      fileNm = strUrl.substring(strUrl.lastIndexOf("/") + 1, strUrl.length());
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
      FileUtil.write(ResourceService.STORAGE_DIR + "/" + fileNm, new StringBuffer(sb.toString()), "utf-8", false);
      listener.callbackResourceJson(sb.toString());
    } else {
      OutputStream output = null;
      try {
        byte[] buffer = new byte[8 * 1024];
        if (!new File(filePath).exists())
          new File(filePath).mkdirs();
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
          Log.d("MainActivity",
              filePath + "/" + fileNm + "->" + new File(filePath + "/" + fileNm).exists());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      listener.callbackResources(filePath.substring(ResourceService.STORAGE_DIR.length(),
          filePath.length())
          + "/" + fileNm);
    }
  }

}
