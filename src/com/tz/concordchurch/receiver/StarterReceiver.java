package com.tz.concordchurch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tz.concordchurch.service.ResourceService;

public class StarterReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
				|| intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			ResourceService.getInstance().refresh(null);
		}
	}
}
