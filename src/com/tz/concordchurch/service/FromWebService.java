package com.tz.concordchurch.service;

import java.util.Iterator;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
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
	public void cacheJson(String toast) {
		if (toast != null) {
			String filePath = MainActivity.STORAGE_DIR + "/dataset.json";
			AppUtil.write(filePath, toast, "utf-8", false);
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
				values.put("read_at", AppUtil.getCurrentDateString("yyyy-MM-dd"));
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
		FileUtil.removeDIR(ResourceService.STORAGE_DIR);
		return true;
	}

}