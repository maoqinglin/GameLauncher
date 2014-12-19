package com.ireadygo.app.gamelauncher.ui.base;

import android.content.Intent;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.statusbar.StatusBarService;

public class BaseActivity extends KeyEventActivity {

	@Override
	protected void onPause() {
		Intent unDisplayIntent = new Intent(StatusBarService.ACTION_UNDISPLAY);
		sendBroadcast(unDisplayIntent);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GameLauncherApplication.getApplication().setCurrentActivity(this);
		Intent displayIntent = new Intent(StatusBarService.ACTION_DISPLAY);
		sendBroadcast(displayIntent);
	}

}
