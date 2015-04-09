package com.ireadygo.app.gamelauncher.ui.base;

import android.app.Dialog;
import android.content.Intent;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.statusbar.StatusBarService;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class BaseActivity extends KeyEventActivity {

	private Dialog mLoadingProgress;

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

	protected void showLoadingProgress() {
		if (mLoadingProgress == null) {
			mLoadingProgress = Utils.createLoadingDialog(BaseActivity.this);
			mLoadingProgress.setCancelable(true);
		}
		if (!mLoadingProgress.isShowing()) {
			mLoadingProgress.show();
		}
	}

	protected void dimissLoadingProgress() {
		if (mLoadingProgress != null && mLoadingProgress.isShowing()) {
			mLoadingProgress.dismiss();
		}
	}

}
