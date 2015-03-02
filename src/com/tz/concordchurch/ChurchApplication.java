package com.tz.concordchurch;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ChurchApplication extends Application {

	private static Context context;
	private static final String TAG = ChurchApplication.class.getSimpleName();

	private static AlarmManager alarmManager;
	private static PendingIntent pendingIntent;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		AppSettings.init(context);
		
        context.startService(new Intent(context, ResourceService.class));
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		// AndroidUtil.showNotification(context, "Low Memory Alert");
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		// AndroidUtil.showNotification(context, "Low/trim Memory Alert " +
		// level);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d(TAG, TAG + "on termination ");
		// AndroidUtil.showNotification(context, " onTermination of locket ");
	}

}
