package com.ireadygo.app.gamelauncher.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity;
import com.ireadygo.app.gamelauncher.ui.guide.GuideOBoxIntroduceActivity;
import com.ireadygo.app.gamelauncher.ui.menu.HomeMenuFragment;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class GameLauncherActivity extends BaseMenuActivity {
	private Dialog mLoadingProgress;
	private long mCreateTime = 0;
	private long mResumeTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (PreferenceUtils.isFirstLaunch()) {
			showLoadingProgress();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					dimissLoadingProgress();
					try {
						Intent startIntent = new Intent(GameLauncherActivity.this, HandleDescriptionActivity.class);
						startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(startIntent);
						finish();
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
						Intent intent = new Intent(GameLauncherActivity.this, GuideOBoxIntroduceActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						finish();
					}
				}
			}, 5000);
		}else{
			initView();
			updateFocusViewNextFocusId(R.id.menu_user);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					getMenuFragment().requestFocusByPosition(0);
				}
			}, 500);
		}
		// // 初始化个推
		// PushManager.getInstance().initialize(this);
		// // 上报终端个推信息
		// AccountManager.getInstance().uploadGetuiInfo(this);
		// 上报应用启动时间
		if (PreferenceUtils.hasDeviceActive()) {
			mCreateTime = System.currentTimeMillis();
			StaticsUtils.onCreate();
			AccountManager.getInstance().init(this, GameLauncherConfig.getChennelId());
		}
		setShouldTranslate(true);
		GameLauncherApplication.getApplication().setGameLauncherActivity(this);
		new AccountInfoAsyncTask(this, null).execute();
		openBluetooth();
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

	@Override
	protected void onResume() {
		super.onResume();
		// 上报应用置前台的时间
		if (PreferenceUtils.hasDeviceActive()) {
			mResumeTime = System.currentTimeMillis();
			StaticsUtils.onResume();
			Utils.saveFreeStoreData(GameLauncherActivity.this);
		}
	}

	@Override
	protected void onPause() {
		if (PreferenceUtils.hasDeviceActive()) {
			// 上报应用置后台的时间
			long frontLastTime = System.currentTimeMillis() - mResumeTime;
			if (frontLastTime >= 0) {
				StaticsUtils.onPause(frontLastTime);
			}
		}
		super.onPause();
	}

	@Override
	public boolean onBackKey() {
		return true;
	}


	@Override
	protected void onDestroy() {
		if (PreferenceUtils.hasDeviceActive()) {
			// 上报应用关闭的时间，打开时长
			long openLastTime = System.currentTimeMillis() - mCreateTime;
			if (openLastTime >= 0) {
				StaticsUtils.onDestroy(System.currentTimeMillis() - mCreateTime);
			}
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

	private void openBluetooth() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			return;
		}
		if (!adapter.isEnabled()) {
			adapter.enable();
		}
	}


}
