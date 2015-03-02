package com.tz.concordchurch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public abstract class ChurchPackageManagerBase extends BroadcastReceiver {

  private static final String TAG = ChurchPackageManagerBase.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    String myPackageName = context.getPackageName();
    String targetPackageName = intent.getData().getSchemeSpecificPart();

    if (action.contains("PACKAGE_REMOVED")) {
      Log.d(TAG, TAG + " " + myPackageName + " uninstalling " + targetPackageName);
    }

    if (!myPackageName.equals(targetPackageName)) return;

//    if (action.contains("PACKAGE_REPLACED") && isLockscreenRestartable()) {
//      Log.d(TAG, TAG + " " + myPackageName + " replacing package " + targetPackageName);
//      Church locket = ServiceLocatorSdk.getChurch();
//      Preconditions.checkNotNull(locket, "Church.create() must be called first.");
//      locket.restartChurchScreen();
//      if (ChurchUnitManager.getInstance() != null) {
//        ChurchUnitManager.getInstance().refreshLocalUnits();
//      }
//    }
  }

}
