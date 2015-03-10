package com.tz.concordchurch.service;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.tz.concordchurch.receiver.AppSettings;
import com.tz.concordchurch.util.AppUtil;

public class WebResource {
	public void callbackResourceJson(String src) {
		try {
			JSONArray allResources = ResourceService.getInstance()
					.getAllResources();
			JSONObject json = new JSONObject(src);
			ResourceService.RESOURCE_DOMAIN = json.getString("domain");
			if (json.getBoolean("forceYn"))
				ResourceService.CACHE_LV = 0;
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
				if (!((JSONObject) allResources.get(i)).isNull("downloaded_at")) {
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
				((JSONObject) allResources.get(i))
						.put("downloaded", downloaded);
				getResources(resource);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void callbackResources(String filePath) {
		try {
			JSONArray allResources = ResourceService.getInstance()
					.getAllResources();
			for (int i = 0; i < allResources.length(); i++) {
				String resource;
				resource = ((JSONObject) allResources.get(i))
						.getString("resource");
				if (filePath.equals(resource)) {
					((JSONObject) allResources.get(i)).put("downloaded", "y");
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
			filePath = ResourceService.STORAGE_DIR + "/" + fileNm;
			File file = new File(filePath);
			if (file.exists() && ResourceService.CACHE_LV == 2) {
				Log.d("MainActivity", "filePath exist ==> " + filePath);
				// temporary
				// FileUtil.removeDIR(STORAGE_DIR);
				listener.callbackResources(fileNm);
			} else {
				Log.d("MainActivity", "filePath not exist ==> " + filePath);
				filePath = ResourceService.STORAGE_DIR + "/" + fileNm;
				filePath = filePath.substring(0, filePath.lastIndexOf("/"));
				File dir = new File(filePath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				new GetHttpResourceTask().execute(
						ResourceService.RESOURCE_DOMAIN + "/" + fileNm,
						filePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return filePath;
	}
}
