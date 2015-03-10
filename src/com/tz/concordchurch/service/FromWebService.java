package com.tz.concordchurch.service;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.tz.concordchurch.activity.MainActivity;
import com.tz.concordchurch.dao.WordLogDao;
import com.tz.concordchurch.util.AppUtil;
import com.tz.concordchurch.util.FileUtil;

public class FromWebService {
	Context mContext;

	FromWebService(Context c) {
		mContext = c;
	}

	@JavascriptInterface
	public void cacheJson(String input) {
		try {
			if (input != null) {
				String filePath = MainActivity.STORAGE_DIR + "/dataset.json";
				JSONArray jsonArry = new JSONArray(input);
				for (int i = 0; i < jsonArry.length(); i++) {
					JSONObject json = new JSONObject(jsonArry.getString(i));
					if (!json.isNull("img")
							&& !json.getString("img").equals("")) {
						String image = json.getString("img");
						String fileNm = image.substring(
								image.indexOf("/", image.indexOf("//")) + 1,
								image.length());
						filePath = fileNm.substring(0, fileNm.lastIndexOf("/"));
						filePath = MainActivity.STORAGE_DIR + "/images"
								+ filePath;
						new GetHttpResourceTask().execute(image, filePath);
						((JSONObject) (jsonArry.get(i))).put("img", fileNm);
					}
				}
				AppUtil.write(filePath, jsonArry.toString(), "utf-8", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public String getJson() {
		String filePath = MainActivity.STORAGE_DIR + "/dataset.json";
		// new File(filePath).delete();
		return AppUtil.getFromFile(filePath, "utf-8").toString();
	}

	@JavascriptInterface
	public void viewVideo(String input) {
		try {
			if (input != null) {
				JSONObject json = new JSONObject(input);
				ContentValues values = new ContentValues();
				Iterator<?> keys = json.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					if (!key.equals("$$hashKey")) {
						String val = json.getString(key);
						values.put(key, val);
					}
				}
				values.put("read_at",
						AppUtil.getCurrentDateString("yyyy-MM-dd"));
				new WordLogDao(mContext).write(values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@JavascriptInterface
	public String getLogsByReadAt(String params) {
		return new WordLogDao(mContext).getLogsByReadAt(params).toString();
	}

	@JavascriptInterface
	public boolean refresh() {
		try {
			FileUtil.removeDIR(ResourceService.STORAGE_DIR);
			ResourceService.getInstance().refresh(null);
			for (int i = 0; i < 20; i++) {
				if (ResourceService.getInstance().getStatus()) {
					return true;
				}
				Thread.sleep(20000);
			}
		} catch (Exception e) {
			Log.d("MainActivity", "!!!!!!!!!!!!!!!!!!!!!" + e.getMessage());
			e.printStackTrace();
		}
		return true;
	}

}