package com.ireadygo.app.gamelauncher.aidl;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GameLauncherRemoteService extends Service {

	private GamelauncherRemoteServiceImpl mGamelauncherRemoteServiceImpl = 
			new GamelauncherRemoteServiceImpl(GameLauncherApplication.getApplication());

	@Override
	public IBinder onBind(Intent arg0) {
		return mGamelauncherRemoteServiceImpl;
	}

	@Override
	public void onDestroy() {
		mGamelauncherRemoteServiceImpl.onDestroy();
		super.onDestroy();
	}

}
