package com.ireadygo.app.gamelauncher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherAppState;


public class GameLauncherService extends Service {

	private GameLauncherBinder mGameLauncherBinder;

	@Override
	public void onCreate() {
		super.onCreate();
		mGameLauncherBinder = new GameLauncherBinder(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mGameLauncherBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	public static class GameLauncherBinder extends Binder {
		private GameManager mGameManager;
		private GameInfoHub mGameInfoHub;
		private GameLauncherAppState mGameLauncherAppState;
		
		public GameLauncherBinder(Context context) {
			mGameManager = new GameManager(context);
			mGameInfoHub = GameInfoHub.instance(context);
			mGameLauncherAppState = GameLauncherAppState.getInstance(context);
		}

        public GameManager getGameManager() {
			return mGameManager;
		}

		public GameInfoHub getGameInfoHub() {
			return mGameInfoHub;
		}
		
		public GameLauncherAppState getGameLauncherAppState() {
            return mGameLauncherAppState;
        }
	}


}
