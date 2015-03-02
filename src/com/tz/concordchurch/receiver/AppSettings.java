package com.tz.concordchurch.receiver;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {

	private static final String TAG = AppSettings.class.getSimpleName();

	/**
	 * This class holds all the key names used to store data in user
	 * preferences.
	 */
	public final class PrefsKeys {

		/** The name of shared preferences used for prefs */
		public static final String PREFS = "com.tz.intro";

		private PrefsKeys() {
		}
	}

	private static final String ASSETS_YN = "opened_with_assets";

	private static Context context;
	private static SharedPreferences prefs;

	public static void init(Context pContext) {
		context = pContext;
		prefs = context.getSharedPreferences(PrefsKeys.PREFS,
				Context.MODE_PRIVATE);
	}

	public static void clear() {
		prefs.edit().clear().commit();
	}

	public static Boolean getAssetsYn() {
		return prefs.getBoolean(ASSETS_YN, false);
	}

	public static void setAssetsYn(Boolean bYn) {
		prefs.edit().putBoolean(ASSETS_YN, bYn);
	}
}