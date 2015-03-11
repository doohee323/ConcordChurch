package com.tz.concordchurch.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.DetailedState;

public class DeviceProperties {

	public static String getConnectedState(Context con) {
		try {
			if (con == null)
				return "";
			final ConnectivityManager connMgr = (ConnectivityManager) con
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final android.net.NetworkInfo wifi = connMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			final android.net.NetworkInfo mobile = connMgr
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (wifi.isAvailable()
					&& wifi.getDetailedState() == DetailedState.CONNECTED) {
				return "wifi";
			} else if (mobile.isAvailable()
					&& mobile.getDetailedState() == DetailedState.CONNECTED) {
				return "3g";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
