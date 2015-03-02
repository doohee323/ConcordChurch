package com.tz.concordchurch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PackageManagement extends BroadcastReceiver {

	private static final String TAG = PackageManagement.class.getSimpleName();

	private static AlarmManager alarmManager = null;
	private static PendingIntent pendingIntent = null;

	public static void stopUpdateInterestsReceiver() {
		Log.d(TAG, TAG + " stoppingUpdateInterestsReceiver ");
		if (alarmManager != null & pendingIntent != null) {
			alarmManager.cancel(pendingIntent);
		}
	}

	public static void startUpdateInterestsReceiver(Context context) {
		Log.d(TAG, TAG + " startUpdateInterestsReceiver ");
		alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		String myPackageName = context.getPackageName();
		String targetPackageName = intent.getData().getSchemeSpecificPart();

		if (action.contains("PACKAGE_REMOVED")) {
			Log.d(TAG, TAG + " " + myPackageName + " uninstalling "
					+ targetPackageName);
		}

		if (context.getPackageName().equals(
				intent.getData().getSchemeSpecificPart())
				&& intent.getAction().contains("PACKAGE_REPLACED")) {
			AppSettings.init(context);
		}
	}

}
