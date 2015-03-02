package com.tz.concordchurch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceStarter extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
        || intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
//      if (LocketSettings.isLocketScreen()) {
//        Intent i = new Intent(context, LockscreenService.class);
//        i.putExtra("screen_state", "true");
//        context.startService(i);
//      }
    }
  }
}
