package com.ireadygo.app.gamelauncher;

import java.io.File;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;

import com.ireadygo.app.gamelauncher.GameLauncher.InitComplete;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.boxmessage.BoxMessageController;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.statusbar.StatusBarService;
import com.ireadygo.app.gamelauncher.ui.GameLauncherActivity;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;
import com.ireadygo.app.gamelauncher.utils.Utils;
import com.umeng.analytics.MobclickAgent;

public class GameLauncherApplication extends Application {

	private static final String USER_PHOTO_SAVE_PATH = File.separator + "user_photo";
	private static final String USER_PHOTO_NAME = "user_photo.png";
	private static GameLauncherApplication sApp;
	private Activity mCurrentActivity;
	private GameLauncherActivity mGameLauncherActivity;
	private UserInfoItem mUserInfoItem;
	private SoundPoolManager mSoundPoolManager;
	private Bitmap mUserPhoto;
	private BoxMessageController mBoxMessageController;
	private String mUserPhotoSavePath;

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
						initBoxMessageService();
					}
				});
			}
			// 启动消息推送服务
			MobclickAgent.openActivityDurationTrack(false);
		}

		Intent service = new Intent(this, StatusBarService.class);
		startService(service);
		initUserPhotoSavePath();
	}

	private void initBoxMessageService() {
		mBoxMessageController = BoxMessageController.getInstance(this);
		mBoxMessageController.init();
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
		mBoxMessageController.shutdown();
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

	public Bitmap getUserPhoto() {
		if (mUserPhoto == null) {
			mUserPhoto = PictureUtil.readBitmap(this, mUserPhotoSavePath + File.separator + USER_PHOTO_NAME);
		}
		return mUserPhoto;
	}

	public void setUserPhoto(Bitmap userPhoto) {
		this.mUserPhoto = userPhoto;
		if (mUserPhoto == null) {
			File userPhotoFile = new File(mUserPhotoSavePath + File.separator + USER_PHOTO_NAME);
			if (userPhotoFile.exists()) {
				userPhotoFile.delete();
			}
		} else {
			PictureUtil.saveBitmap(this, userPhoto, mUserPhotoSavePath + File.separator + USER_PHOTO_NAME);
		}
	}

	private void initUserPhotoSavePath() {
		mUserPhotoSavePath = getFilesDir().getPath() + USER_PHOTO_SAVE_PATH;
		File file = new File(mUserPhotoSavePath);
		if (!file.exists()) {
			file.mkdir();
		}
	}
}
