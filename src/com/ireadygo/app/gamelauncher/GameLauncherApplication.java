package com.ireadygo.app.gamelauncher;

import android.app.Activity;
import android.content.Intent;
import android.app.Application;

import com.ireadygo.app.gamelauncher.GameLauncher.InitComplete;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.statusbar.StatusBarService;
import com.ireadygo.app.gamelauncher.ui.GameLauncherActivity;
import com.ireadygo.app.gamelauncher.utils.Utils;
import com.umeng.analytics.MobclickAgent;

public class GameLauncherApplication extends Application {

	private static GameLauncherApplication sApp;
	private Activity mCurrentActivity;
	private GameLauncherActivity mGameLauncherActivity;
	private UserInfoItem mUserInfoItem;
	private SoundPoolManager mSoundPoolManager;

	@Override
	public void onCreate() {
		super.onCreate();
		SoundPoolManager.instance(this);
		String currProcessName = Utils.getCurrProcessName(this);
		String pkgName = getPackageName();
		if (pkgName.equals(currProcessName)) {
			sApp = this;
			// 在application中统一绑定服务
			if (!GameLauncher.hasInit()) {
				GameLauncher.init(this, new InitComplete() {
					@Override
					public void onInitCompleted() {
					}
				});
			}
			// 启动消息推送服务
			MobclickAgent.openActivityDurationTrack(false);
		}

		Intent service = new Intent(this, StatusBarService.class);
		startService(service);
	}

	public static GameLauncherApplication getApplication() {
		return sApp;
	}

	public void setCurrentActivity(Activity activity) {
		mCurrentActivity = activity;
	}

	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}

	@Override
	public void onTerminate() {
		SoundPoolManager.instance(this).release();
		AccountManager.getInstance().destory();
		GameLauncherAppState.getInstance(getApplicationContext()).onTerminate();
		stopService(new Intent(this, StatusBarService.class));
		super.onTerminate();
	}


	public UserInfoItem getUserInfoItem() {
		return mUserInfoItem;
	}

	public void setUserInfoItem(UserInfoItem userInfoItem) {
		this.mUserInfoItem = userInfoItem;
	}

	public void setGameLauncherActivity(GameLauncherActivity gameLauncherActivity){
		this.mGameLauncherActivity = gameLauncherActivity;
	}
	
	public GameLauncherActivity getGameLauncherActivity(){
		return this.mGameLauncherActivity;
	}
}
