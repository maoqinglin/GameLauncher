package com.ireadygo.app.gamelauncher;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.GameLauncher.InitComplete;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.download.Network;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager;
import com.ireadygo.app.gamelauncher.appstore.manager.GameStateManager;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.umeng.analytics.MobclickAgent;

public class GameLauncherReceiver extends BroadcastReceiver {

	public static final int MSG_PACKAGE_ACTION = 100;
	public static final String ACTION_PACKAGE_UNINSTALL = "com.ireadygo.app.gamelauncher.ACTION_PACKAGE_UNINSTALL";
	public static final String ACTION_PACKAGE_INSTALL = "com.ireadygo.app.gamelauncher.ACTION_PACKAGE_INSTALL";
	public static final String ACTION_PACKAGE_UPDATE = "com.ireadygo.app.gamelauncher.ACTION_PACKAGE_UPDATE";
	public static final String KEY_PKG = "pkgName";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (!GameLauncher.hasInit()) {
			GameLauncher.init(context, new InitComplete() {
				@Override
				public void onInitCompleted() {
					handleAction(context, intent);
				}
			});
		} else {
			handleAction(context, intent);
		}
	}

	private void handleAction(final Context context,final Intent intent) {
			GameStateManager gsm = GameLauncher.instance().getGameManager().getGameStateManager();
			GameManager gm = GameLauncher.instance().getGameManager();
			String action = intent.getAction();
			//外部存储设备挂载广播，重新开始图标加载
			if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
				String pkgList[] = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
				if (pkgList == null || pkgList.length == 0) {
					return;
				}
				GameLauncherAppState.getInstance(context).getModel().startLoader();
				return;
			}
			//网络变化广播
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				if (NetworkUtils.isNetworkConnected(context)) {
					AccountManager.getInstance().uploadGetuiInfo(context);
				}
				return;
			}
			//应用安装，卸载，更新广播
			String packageName = intent.getDataString().split(":")[1];
			if (TextUtils.isEmpty(packageName)) {
				return;
			}
			/*
			 * 应用安装：Intent.ACTION_PACKAGE_ADDED
			 * 应用卸载：Intent.ACTION_PACKAGE_REMOVED
			 * 应用更新：Intent.ACTION_PACKAGE_REMOVED->Intent.ACTION_PACKAGE_ADDED->Intent.ACTION_PACKAGE_REPLACED
			 */
			if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("PkgName", packageName);
				MobclickAgent.onEvent(context, "install_app", map);

				boolean isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
				if (!isReplacing) {//应用安装
					gsm.setGameState(packageName, GameState.LAUNCHABLE);
					gm.mapInstallGame(packageName);
					sendOutsideInstallBroadcast(context, packageName);
				}
			} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				boolean isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
				if (!isReplacing) {//应用卸载
					gm.handleGameUninstallSuccessfully(packageName);
					GameLauncherAppState.getInstance(context).getModel().handleGameRemove(packageName);
					sendUninstallBroadcast(context, packageName);
				}
			} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {//应用更新
				gsm.setGameState(packageName, GameState.LAUNCHABLE);
				GameLauncherAppState.getInstance(context).getModel().handleGameUpdate(packageName);
				sendUpdateBroadcast(context, packageName);
			}
		}

	private void sendUninstallBroadcast(Context context,String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		Intent intent = new Intent(ACTION_PACKAGE_UNINSTALL);
		intent.putExtra(KEY_PKG, pkgName);
		context.sendBroadcast(intent);
	}

	private void sendOutsideInstallBroadcast(Context context,String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		Intent intent = new Intent(ACTION_PACKAGE_INSTALL);
		intent.putExtra(KEY_PKG, pkgName);
		context.sendBroadcast(intent);
	}

	private void sendUpdateBroadcast(Context context,String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		Intent intent = new Intent(ACTION_PACKAGE_UPDATE);
		intent.putExtra(KEY_PKG, pkgName);
		context.sendBroadcast(intent);
	}
}
