package com.tz.concordchurch.service;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.tz.concordchurch.activity.MainActivity;
import com.tz.concordchurch.util.AppUtil;

public class WebAppService {
	Context mContext;

	WebAppService(Context c) {
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
		//new File(filePath).delete();
		return AppUtil.getFromFile(filePath, "utf-8").toString();
	}
	
}