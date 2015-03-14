package com.ireadygo.app.gamelauncher.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.guide.GuideOBoxIntroduceActivity;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class EntryActivity extends BaseActivity {
	private static final String ACTION_LANGUAGE_SETTINGS = "com.ireadygo.app.wizard.language";
	private Dialog mLoadingProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openBluetooth();
		if (PreferenceUtils.isFirstLaunch()) {
			showLoadingProgress();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					dimissLoadingProgress();
					try {
						Intent intent = new Intent(ACTION_LANGUAGE_SETTINGS);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						finish();
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
						Intent intent = new Intent(EntryActivity.this, GuideOBoxIntroduceActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						finish();
					}
				}
			}, 5000);

		} else {
			Intent intent = new Intent(this, GameLauncherActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	private void openBluetooth() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			return;
		}
		if (!adapter.isEnabled()) {
			adapter.enable();
		}
	}

	protected void showLoadingProgress() {
		if (mLoadingProgress == null) {
			mLoadingProgress = Utils.createLoadingDialog(this);
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
