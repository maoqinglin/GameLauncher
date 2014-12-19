package com.ireadygo.app.gamelauncher.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;

public class ToastUtils {
	private static Context sContext = GameLauncherApplication.getApplication();
	public static void ToastMsg(final String msg,final boolean isShort) {
		new Handler(sContext.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(sContext, msg, (isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG)).show();
			}
		});
	}
}
