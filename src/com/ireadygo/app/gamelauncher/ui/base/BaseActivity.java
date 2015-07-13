package com.ireadygo.app.gamelauncher.ui.base;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.statusbar.StatusBarService;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class BaseActivity extends KeyEventActivity {

	private static final int MSG_SHOW_LOADING_DIALOG = 201;
	private static final int MSG_DISMISS_LOADING_DIALOG = 202;
	private static final long LOADING_DELAY = 1000;

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
		postMsg(MSG_SHOW_LOADING_DIALOG, LOADING_DELAY);
	}

	protected void dimissLoadingProgress() {
		if (!isDestroyed() && !isFinishing() && mLoadingProgress != null && mLoadingProgress.isShowing()) {
			mLoadingProgress.dismiss();
		}
	}

	protected void removeAllMsg() {
		if (mHandler.hasMessages(MSG_SHOW_LOADING_DIALOG)) {
			mHandler.removeMessages(MSG_SHOW_LOADING_DIALOG);
		}
	}

	private void postMsg(int msgTag,long delay) {
		if (mHandler.hasMessages(msgTag)) {
			mHandler.removeMessages(msgTag);
		}
		Message msg = mHandler.obtainMessage(msgTag);
		mHandler.sendMessageDelayed(msg, delay);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_LOADING_DIALOG:
				if (mLoadingProgress == null) {
					mLoadingProgress = Utils.createLoadingDialog(BaseActivity.this);
					mLoadingProgress.setCancelable(true);
				}
				if (!isDestroyed() && !isFinishing() && !mLoadingProgress.isShowing()) {
					mLoadingProgress.show();
				}
				break;
			case MSG_DISMISS_LOADING_DIALOG:
				dimissLoadingProgress();
				break;
			default:
				break;
			}
		};
	};

}
