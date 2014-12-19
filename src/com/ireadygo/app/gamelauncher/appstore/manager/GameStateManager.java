package com.ireadygo.app.gamelauncher.appstore.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import android.content.Context;
import android.util.Log;

import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.data.GameData.LocalDataLoadCallback;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

public class GameStateManager implements LocalDataLoadCallback {

	public static final String TAG = "GameStateManager";

	private HashMap<String, GameState> mGameStateMap = new HashMap<String, GameState>();
	private Context mContext;
	private GameData mGameData;
	private ExecutorService mThreadPool = GameLauncherThreadPool.getFixedThreadPool();;
	private static boolean mHasInit = false;

	public GameStateManager(Context context,GameData gameData) {
		mContext = context;
		mGameData = gameData;
	}


	/*
	 * 初始化所有游戏仓相关的应用的状态：所有安装的第三方应用，下载数据库的应用
	 */
	private void initAllGameState() {
		if (mHasInit) {
			return;
		}
		mHasInit = true;
		mGameStateMap.clear();
		ArrayList<AppEntity> allApps = (ArrayList<AppEntity>)mGameData.getAllGames();
		if (null == allApps) {
			return;
		}
		synchronized (mGameStateMap) {
			for (AppEntity app : allApps) {
				mGameStateMap.put(app.getPkgName(), app.getGameState());
			}
		}
		allApps = null;
	}


	public void setGameState(String pkgName,GameState newState) {
		synchronized (mGameStateMap) {
			if (GameState.DEFAULT.equals(newState)) {
				mGameStateMap.remove(pkgName);
				mGameData.removeGame(pkgName);
			} else {
				mGameStateMap.put(pkgName, newState);
				//更新数据库记录的状态值
				if (isRecordableState(newState)) {
					mGameData.updateGameStatus(pkgName, newState.toString());
				}
			}
			Log.d(TAG, "change state map:"+pkgName + "--" +mGameStateMap.get(pkgName));
		}
	}


	public GameState getGameState(String pkgName) {
		synchronized (mGameStateMap) {
			GameState state = mGameStateMap.get(pkgName);
			if (null == state) {
				return GameState.DEFAULT;
			}
			return state;
		}
	}

	public static boolean isDownloadableState(GameState state) {
		if ((GameState.DEFAULT.equals(state))
				|| (GameState.PAUSED.equals(state))
				|| (GameState.QUEUING.equals(state))
				|| (GameState.ERROR.equals(state))
				|| (GameState.TRANSFERING.equals(state))
				|| (GameState.UPGRADEABLE.equals(state))) {
			return true;
		}
		return false;
	}

	public static boolean isLaunchableState(GameState state) {
		if ((GameState.LAUNCHABLE.equals(state))
				|| (GameState.UPGRADEABLE.equals(state))) {
			return true;
		}
		return false;
	}

	public static boolean isDownloadingState(GameState state) {
		if ((GameState.PAUSED.equals(state))
				|| (GameState.QUEUING.equals(state))
				|| (GameState.ERROR.equals(state))
				|| (GameState.TRANSFERING.equals(state))
				|| (GameState.INSTALLABLE.equals(state))) {
			return true;
		}
		return false;
	}

	//可记录在数据库中的状态，DEFAULT 和 INSTALLIING等进行状态不会记录在数据库中
	public static boolean isRecordableState(GameState state) {
		if (isDownloadingState(state)
				|| isLaunchableState(state)) {
			return true;
		}
		return false;
	}

	/*
	 * 获取所有应用状态的列表
	 */
	public HashMap<String, GameState> getAllGameState() {
		synchronized (mGameStateMap) {
			return (HashMap<String, GameState>)mGameStateMap.clone();
		}
	}

	/*
	 * 获取正在下载状态中的列表
	 */
	public HashMap<String, GameState> getDownloadingState() {
		HashMap<String,GameState> result = new HashMap<String, GameState>();
		synchronized (mGameStateMap) {
			for(Map.Entry<String, GameState> entry: mGameStateMap.entrySet()) {
				GameState state = entry.getValue();
				if (isDownloadingState(state)) {
					String pkgName = entry.getKey();
					result.put(pkgName, state);
				}
			}
		}
		return result;
	}

	//GameData初始化结束后，会回调这个方法
	@Override
	public void loadSuccess() {
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				initAllGameState();
			}
		});
	}

	@Override
	public void loadFail() {
		//ignore
	} 
}
