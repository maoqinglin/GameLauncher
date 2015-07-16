package com.ireadygo.app.gamelauncher.appstore.manager;

import java.util.List;
import java.util.concurrent.ExecutorService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.data.GameData.LocalDataLoadCallback;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.widget.GameLauncherNotification;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

public class UpdateManager implements LocalDataLoadCallback {
	public static final String ACTION_UPDATABLE_NOTIFICATION = "com.ireadygo.app.gamelauncher.AppUpdatable";
	
	private static final int MSG_CHECK_UPGRADE = 100;
	private static final long DELAY_CHECK_UPGRADE = 60 * 1000;//1min
	private static final long MIN_UPGRADE_CHECK_DELAY = 2 * 60 * 60 * 1000;//2 hours
	private static final long MIN_UPGRADE_INFO_DELAY = 8 * 60 * 60 * 1000;//8 hours
	private static final String ACTION_MAP_GAME_COMPLETE = "com.ireadygo.app.gamelauncher.ACTION_MAP_GAME_COMPLETE";
	private ExecutorService mThreadPool = GameLauncherThreadPool.getCachedThreadPool();
	private Context mContext;
	private GameLauncherNotification mGameLauncherNotification;
	private GameData mGameData;
	private static boolean sDataInitSuccess = false;

	public UpdateManager(Context context,GameLauncherNotification gameLauncherNotification) {
		mContext = context;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		intentFilter.addAction(ACTION_MAP_GAME_COMPLETE);
		mContext.registerReceiver(mReceiver, intentFilter);
		mGameLauncherNotification = gameLauncherNotification;
		mGameData = GameData.getInstance(context);
	}

	public void shutdown() {
		mContext.unregisterReceiver(mReceiver);
	}

	public void checkUpgradeApps() {
		if (sDataInitSuccess) {
			doCheckUpgradeApps();
		}
	}

	private void doCheckUpgradeApps() {
		long lastUpgradeTime = PreferenceUtils.getLastCheckUpgradeTime();
		if (System.currentTimeMillis() - lastUpgradeTime < MIN_UPGRADE_CHECK_DELAY) {
			return;
		}
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					List<AppEntity> checkAppList = mGameData.getLauncherAbleGames();
					if (checkAppList.size() == 0) {
						return;
					}
					List<AppEntity> upgradeables =
							GameInfoHub.instance(mContext).obtainUpdatableApp(checkAppList);
					PreferenceUtils.saveLastCheckUpgradeTime(System.currentTimeMillis());
					//是否有更新
					if (upgradeables.size() <= 0) {
						PreferenceUtils.setHasUpdatable(false);
						return;
					}else{
						PreferenceUtils.setHasUpdatable(true);
					}
					for (AppEntity app : upgradeables) {//修改应用的更新标志位
						mGameData.updateUpgradeAppData(app);
						GameLauncher.instance().getGameManager().getGameStateManager().setGameState(app.getPkgName(), GameState.UPGRADEABLE);
					}
					//是否通知满足最小通知间隔
					Long lastInfoTime = PreferenceUtils.getLastInfoUpgradeTime();
					if (System.currentTimeMillis() - lastInfoTime < MIN_UPGRADE_INFO_DELAY) {
						return;
					}
					PreferenceUtils.saveLastInfoUpgradeTime(System.currentTimeMillis());
					if (PreferenceUtils.isAppUpdateNotify()) {
						mGameLauncherNotification.addUpgradeNotification(upgradeables.size());
						Intent intent = new Intent(ACTION_UPDATABLE_NOTIFICATION);
						LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
					}
				} catch (InfoSourceException e) {
					//ignore
				}
			}
		});
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				if (!NetworkUtils.isNetworkConnected(mContext)) {
					return;
				}
				postMsg(MSG_CHECK_UPGRADE, DELAY_CHECK_UPGRADE);
			} else if (ACTION_MAP_GAME_COMPLETE.equals(action)) {
				checkUpgradeApps();
			}
		};
	};

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
			case MSG_CHECK_UPGRADE:
				doCheckUpgradeApps();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void loadSuccess() {
//		doCheckUpgradeApps();
		sDataInitSuccess = true;
	}

	@Override
	public void loadFail() {
		sDataInitSuccess = false;
	}


}
