package com.ireadygo.app.gamelauncher.armservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class ARMReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
		} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
		} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
		} else if ("ireadygo.intent.action.APP_START_ACTION".equals(action)) {
		}
	}


}
