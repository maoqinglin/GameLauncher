package com.ireadygo.app.gamelauncher.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity;
import com.ireadygo.app.gamelauncher.ui.menu.HomeMenuFragment;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;

public class GameLauncherActivity extends BaseMenuActivity {
	private long mCreateTime = 0;
	private long mResumeTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCreateTime = System.currentTimeMillis();
		// // 初始化个推
		// PushManager.getInstance().initialize(this);
		// // 上报终端个推信息
		// AccountManager.getInstance().uploadGetuiInfo(this);
		// 上报应用启动时间
		StaticsUtils.onCreate();
		setShouldTranslate(true);
		GameLauncherApplication.getApplication().setGameLauncherActivity(this);
		new AccountInfoAsyncTask(this, null).execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 上报应用置前台的时间
		StaticsUtils.onResume();
	}

	@Override
	protected void onPause() {
		// 上报应用置后台的时间
		long frontLastTime = System.currentTimeMillis() - mResumeTime;
		if (frontLastTime >= 0) {
			StaticsUtils.onPause(frontLastTime);
		}
		super.onPause();
	}

	// @Override
	// public boolean onKeyUp(int keyCode, KeyEvent event) {
	// switch (keyCode) {
	// case SnailKeyCode.UP_KEY:
	// case SnailKeyCode.DOWN_KEY:
	// case SnailKeyCode.LEFT_KEY:
	// case SnailKeyCode.RIGHT_KEY:
	// if(mMenuFragment.getState().isFocused()) {
	// SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_MENU);
	// } else if(mMenuFragment.getState().isSelected()) {
	// SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_SELECT);
	// }
	// break;
	//
	// default:
	// break;
	// }
	//
	// return super.onKeyUp(keyCode, event);
	// }

	@Override
	protected void onDestroy() {
		// 上报应用关闭的时间，打开时长
		long openLastTime = System.currentTimeMillis() - mCreateTime;
		if (openLastTime >= 0) {
			StaticsUtils.onDestroy(System.currentTimeMillis() - mCreateTime);
		}
		super.onDestroy();
	}

	public static void startSelf(Context context) {
		Intent intent = new Intent(context, GameLauncherActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		SoundPoolManager.instance(context).play(SoundPoolManager.SOUND_ENTER);
		context.startActivity(intent);
	}

	@Override
	public HomeMenuFragment createMenuFragment() {
		return new HomeMenuFragment(this);
	}

}
