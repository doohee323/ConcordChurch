// Copyright (C) 2013 Church Inc.
package com.tz.concordchurch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PackageManagement extends ChurchPackageManagerBase {

  private static final String TAG = PackageManagement.class.getSimpleName();

  private static AlarmManager alarmManager = null;
  private static PendingIntent pendingIntent = null;

  public static void stopUpdateInterestsReceiver() {
    // Clear all Units and stop downloader
    Log.d(TAG, TAG + " stoppingUpdateInterestsReceiver ");
    if (alarmManager != null & pendingIntent != null) {
      alarmManager.cancel(pendingIntent);
    }
  }

  public static void startUpdateInterestsReceiver(Context context) {
    Log.d(TAG, TAG + " startUpdateInterestsReceiver ");
    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//    Intent i = new Intent(context, UpdateInterestsReceiver.class);
//    pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
//    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
//        AlarmManager.INTERVAL_DAY, pendingIntent);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    
    // Very important to call super as the last action (instead of first) since
    // ServiceLocatorChurch.init()
    // applies ChurchConfiguratorChurch first.
    super.onReceive(context, intent);
  }

}
