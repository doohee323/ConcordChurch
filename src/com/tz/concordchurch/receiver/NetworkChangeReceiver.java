package com.tz.concordchurch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.tz.concordchurch.util.DeviceProperties;

public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		String status = DeviceProperties.getConnectedState(context);
		Toast.makeText(context, status, Toast.LENGTH_LONG).show();
	}
}