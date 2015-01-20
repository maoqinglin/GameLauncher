package com.ireadygo.app.gamelauncher.appstore.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherSettings.Favorites;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;
import com.umeng.analytics.MobclickAgent;

public class MapGameManager {

	private Context mContext;
	private GameData mGameData;
	private GameInfoHub mGameInfoHub;
	private static ExecutorService mThreadPool = GameLauncherThreadPool.getCachedThreadPool();
	private static final long MAP_GAME_DELAY = 2 * 1000;
	private static final String ACTION_MAP_GAME_COMPLETE = "com.ireadygo.app.gamelauncher.ACTION_MAP_GAME_COMPLETE";
	private static volatile MapGameManager sInstance;
	private HandlerThread mMapHandlerThread = new HandlerThread("map_game_thread");

	public static MapGameManager getInstance(Context context) {
		if (sInstance == null) {
			synchronized (MapGameManager.class) {
				if (sInstance == null) {
					sInstance = new MapGameManager(context);
				}
			}
		}
		return sInstance;
	}

	{
		mMapHandlerThread.start();
	}

	private MapGameManager(Context context) {
		mContext = context;
		mGameData = GameData.getInstance(context);
		mGameInfoHub = GameInfoHub.instance(context);
	}

	public void mapGameList(final List<AppEntity> checkGameList) {
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				if (checkGameList.size() > 0) {
					if (doMapGameList(checkGameList)) {
						PreferenceUtils.setMapGameComplete(true);
						sendMapCompleteBroadcast();
					}
				}
			}
		});
	}

	private void sendMapCompleteBroadcast() {
		Intent intent = new Intent(ACTION_MAP_GAME_COMPLETE);
		mContext.sendBroadcast(intent);
	}

	public void mapNewInstallGame(String pkgName) {
		AppEntity app = mGameData.getGameByPkgName(pkgName);
		if (app != null && !TextUtils.isEmpty(app.getAppId())) {
			mapFreeStoreGame(app);
			umengStatistics("FreeStore", pkgName);
		} else {
			mapOutsideGame(pkgName,true);
			umengStatistics("Other", pkgName);
		}
	}

	public void mapInstalledGame(String pkgName) {
		AppEntity app = mGameData.getGameByPkgName(pkgName);
		if (app != null) {
			if (app.getIsInFreeStore() == AppEntity.NOT_IN_FREE_STORE) {
				mapOutsideGame(pkgName, false);
			} else {
				GameLauncherAppState.getInstance(mContext).getModel()
				.updateInstalledAppInfo(app.getPkgName(),checkPkgDisplayState(pkgName)
						,Favorites.APP_TYPE_GAME);
			}
		}
	}

	public void mapDisableGame(String pkgName) {
		AppEntity app = mGameData.getGameByPkgName(pkgName);
		if (app != null) {
			GameLauncherAppState.getInstance(mContext).getModel()
			.updateInstalledAppInfo(pkgName, checkPkgDisplayState(pkgName),app.getIsInFreeStore());
		}
	}

	private void umengStatistics(String source, String pkg) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("source", source + ":" + pkg);
		MobclickAgent.onEvent(mContext, "install_source", map);
	}

	//对外部安装游戏的匹配
	private void mapOutsideGame(final String pkgName,final boolean isInstall) {
		final boolean notOccupySlot = GameLauncherConfig.SLOT_ENABLE ? GameLauncherConfig.isInSlotWhiteList(pkgName)
				|| (isInstall && AppRestrictionManager.getInstance(mContext).isSnailCtrlEnableApp(pkgName)) : true;//在白名单或免商店安装的应用，不占卡槽;
		if (isInstall) {
			mGameData.addGame(mContext, pkgName,!notOccupySlot);
			GameLauncherAppState.getInstance(mContext).getModel()
			.handleGameAddOrUpdate(pkgName, checkPkgDisplayState(pkgName), Favorites.APP_TYPE_APPLICATION);
		} else {
			GameLauncherAppState.getInstance(mContext).getModel()
			.updateInstalledAppInfo(pkgName, checkPkgDisplayState(pkgName), Favorites.APP_TYPE_APPLICATION);
		}

		new Handler(mMapHandlerThread.getLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				doMapOutsideGame(pkgName, isInstall,!notOccupySlot);
				if(GameLauncherConfig.SLOT_ENABLE && isInstall && notOccupySlot){
					AppRestrictionManager.getInstance(mContext).setAppEnableWithoutRecord(pkgName, true);
				}
			}
		}, MAP_GAME_DELAY);
	}

	//对免商店安装游戏的匹配
	private void mapFreeStoreGame(final AppEntity app) {
		//免商店下载的应用，统一使能
		if (GameLauncherConfig.SLOT_ENABLE) {
			AppRestrictionManager.getInstance(mContext).setAppEnableWithoutMapAndRecord(app.getPkgName(), true);
		}
		new Handler(mMapHandlerThread.getLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				//先默认显示在我的应用中
				GameLauncherAppState.getInstance(mContext).getModel()
				.handleGameAddOrUpdate(app.getPkgName(), Favorites.DISPLAY, Favorites.APP_TYPE_APPLICATION);
				doMapFreeStoreGame(app);
			}
		}, MAP_GAME_DELAY);
	}

	public void shutdown() {
	}

	private void doMapFreeStoreGame(AppEntity app) {
		ArrayList<AppEntity> gamelist = new ArrayList<AppEntity>();
		gamelist.add(app);
		try {
			List<Long> results = mGameInfoHub.mapAppWithFreeStore(gamelist);
			if (results.size() > 0) {
				//匹配到游戏，修改标志，更新数据库
				app.setIsInFreeStore(AppEntity.IN_FREE_STORE);
				mGameData.updateMappedAppData(app);
				//匹配到游戏，需要将游戏应用转移到“我的游戏”中显示
				GameLauncherAppState.getInstance(mContext).getModel()
				.updateInstalledAppInfo(app.getPkgName(),checkPkgDisplayState(app.getPkgName()),Favorites.APP_TYPE_GAME);
				return;
			}
		} catch (InfoSourceException e) {
			e.printStackTrace();
		}
	}

	private void doMapOutsideGame(String pkgName,boolean isInstall,boolean isOccupySlot) {
		List<AppEntity> gameList = new ArrayList<AppEntity>();
		PackageInfo pkgInfo = PackageUtils.getPkgInfo(mContext, pkgName);
		if (pkgInfo == null) {
			return;
		}
		AppEntity app = new AppEntity();
		app.setPkgName(pkgName);
		app.setVersionCode(pkgInfo.versionCode);
		gameList.add(app);
		try {
			List<Long> results = mGameInfoHub.mapAppWithFreeStore(gameList);
			if (results.size() > 0) {
				//匹配到游戏，则需要获取游戏详细信息
				long appId = results.get(0);
				AppEntity game = mGameInfoHub.obtainItemById(String.valueOf(appId));
				if (game == null) {
					//请求数据库异常
					return;
				}
				game.setIsInFreeStore(AppEntity.IN_FREE_STORE);
				game.setGameState(GameState.LAUNCHABLE);
				if (game.getIsComeFrmFreeStore() != AppEntity.CAME_FROM_FREE_STORE) {
					game.setIsComeFrmFreeStore(AppEntity.NOT_CAME_FROM_FREE_STORE);
				}
				if(game.getIsOccupySlot() != AppEntity.NOT_OCCUPY_SLOT){
					game.setIsOccupySlot(AppEntity.OCCUPY_SLOT);
				}
				mGameData.updateMappedAppData(game);
				if (GameLauncherConfig.SLOT_ENABLE && isInstall && isOccupySlot) {
					GameLauncherAppState.getInstance(mContext).getModel()
					.handleGameAddOrUpdate(pkgName, Favorites.DONOT_DISPLAY,Favorites.APP_TYPE_GAME);
				} else {
					GameLauncherAppState.getInstance(mContext).getModel()
					.updateInstalledAppInfo(pkgName, checkPkgDisplayState(game.getPkgName()), Favorites.APP_TYPE_GAME);
				}
			}
		} catch (InfoSourceException e) {
			e.printStackTrace();
		} 
	}


	private boolean doMapGameList(List<AppEntity> gameList) {
		if (gameList == null || gameList.size() == 0) {
			return false;
		}
		try {
			List<Long> results = mGameInfoHub.mapAppWithFreeStore(gameList);
			if (results.size() != 0) {
				for (long appId : results) {
					AppEntity app = mGameInfoHub.obtainItemById(String.valueOf(appId));
					if (null == app) {
						continue;
					}
					app.setIsInFreeStore(AppEntity.IN_FREE_STORE);//检测出应用在免商店中，设置该标志
					if (GameState.DEFAULT.equals(app.getGameState())) {
						app.setGameState(GameState.LAUNCHABLE);
					}
					if (app.getIsComeFrmFreeStore() != AppEntity.CAME_FROM_FREE_STORE) {
						app.setIsComeFrmFreeStore(AppEntity.NOT_CAME_FROM_FREE_STORE);
					}
					if(app.getIsOccupySlot() != AppEntity.NOT_OCCUPY_SLOT){
						app.setIsOccupySlot(AppEntity.OCCUPY_SLOT);
					}
					
					mGameData.updateMappedAppData(app);
					GameLauncherAppState.getInstance(mContext).getModel()
					.updateInstalledAppInfo(app.getPkgName(), checkPkgDisplayState(app.getPkgName()),Favorites.APP_TYPE_GAME);
				}
			}
			for (AppEntity app : gameList) {
				if (results != null 
						&& !TextUtils.isEmpty(app.getAppId())
						&&results.contains(Long.parseLong(app.getAppId()))) {
					continue;
				}
				//未匹配上的应用，设置不在免商店的标志
				mGameData.updateInFreeStoreFlag(app.getPkgName(), AppEntity.NOT_IN_FREE_STORE);
				GameLauncherAppState.getInstance(mContext).getModel()
				.updateInstalledAppInfo(app.getPkgName(), checkPkgDisplayState(app.getPkgName()),Favorites.APP_TYPE_APPLICATION);
			}
			return true;
		} catch (InfoSourceException e) {
			e.printStackTrace();
			//do nothing
			return false;
		}
	}

	private int checkPkgDisplayState(String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return Favorites.DONOT_DISPLAY;
		}
		if (AppRestrictionManager.isAppDisable(mContext, pkgName)) {
			return Favorites.DONOT_DISPLAY;
		}
		return Favorites.DISPLAY;
	}

}
