package com.ireadygo.app.gamelauncher.boxmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BoxMessageReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent(context, BoxMessageService.class);
		serviceIntent.setAction(intent.getAction());
		serviceIntent.putExtras(intent);
		serviceIntent.setData(intent.getData());

		context.startService(serviceIntent);
	}

}
