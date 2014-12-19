package com.ireadygo.app.gamelauncher;


import android.content.Context;

import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.service.GameLauncherBindable;
import com.ireadygo.app.gamelauncher.service.IBindable.BindResponse;

public class GameLauncher {

	private static GameLauncher sInstance;
	private static boolean sIsBound = false;
	private static GameLauncherBindable sGameLauncherBindable;

	private GameManager mGameManager;
	private GameInfoHub mGameInfoHub;
	private GameLauncherAppState mGameLauncherAppState;

    public static void init(final Context context,final InitComplete response) {
		if (sIsBound) {
			//如果已经绑定，直接回调绑定成功
			response.onInitCompleted();
			return;
		}

		sGameLauncherBindable = GameLauncherBindable.instance(context.getApplicationContext());
		sGameLauncherBindable.bind(new BindResponse() {
			@Override
			public void onBindSuccessful() {
				GameManager gameManager = sGameLauncherBindable.getAppStoreBinder().getGameManager();
				GameInfoHub gameInfoHub = sGameLauncherBindable.getAppStoreBinder().getGameInfoHub();
				GameLauncherAppState gameLauncherAppState = sGameLauncherBindable.getAppStoreBinder().getGameLauncherAppState();
				sInstance = new GameLauncher(gameManager, gameInfoHub,gameLauncherAppState);
				sIsBound = true;
				response.onInitCompleted();
			}
			@Override
			public void onBindFailed() {
				throw new IllegalStateException("Bind Failed!");
			}
		});
	}

	public static boolean hasInit() {
		return sIsBound;
	}

	public static GameLauncher instance() {
		if (!sIsBound) {
			throw new IllegalStateException("AppStore hasn't init!");
		}
		return sInstance;
	}

	private GameLauncher(GameManager gameManager,GameInfoHub gameInfoHub,GameLauncherAppState gameLauncherAppState) {
		mGameManager = gameManager;
		mGameInfoHub = gameInfoHub;
		mGameLauncherAppState = gameLauncherAppState;
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
	
	public void shutdown() {
		sGameLauncherBindable.unbind();
		sGameLauncherBindable = null;
		sInstance = null;
	}

	public interface InitComplete {
		void onInitCompleted();
	}
}
