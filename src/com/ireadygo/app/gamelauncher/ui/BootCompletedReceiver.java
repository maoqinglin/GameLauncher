package com.ireadygo.app.gamelauncher.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;

public class BootCompletedReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if(!PreferenceUtils.isFirstLaunch()){
			Intent startIntent = new Intent(context, HandleDescriptionActivity.class);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startIntent);
		}
	}
}
