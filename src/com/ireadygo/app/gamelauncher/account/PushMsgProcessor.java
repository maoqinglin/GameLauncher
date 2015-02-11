package com.ireadygo.app.gamelauncher.account;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.account.pushmsg.SnailPushMessage;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager;
import com.ireadygo.app.gamelauncher.appstore.manager.GameStateManager;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

/**
 * 
 *后台自动处理接收到的各种推送消息，目前只自动处理下载游戏这种类型的消息
 */

public class PushMsgProcessor {

//	type 1 启动客户端--
//	type 2 跳转流量分享界面--
//	type 4 登录首页--
//	type 5 跳转合集 
//	type 6 跳转指定游戏页面
//	type 7 相关活动页面
//	type 8 下载应用

	private static final int TYPE_GAME_COLLECTION = 5;
	private static final int TYPE_GAME_DETAIL = 6;
	private static final int TYPE_GAME_WEB = 7;
	private static final int TYPE_DOWNLOAD_GAME = 8;

	private static volatile PushMsgProcessor sInstance;


	private Activity mContext;

	private PushMsgProcessor() {
		mContext = GameLauncherApplication.getApplication().getCurrentActivity();
	}

	public static PushMsgProcessor getInstance() {
		if (sInstance == null) {
			synchronized (PushMsgProcessor.class) {
				if (sInstance == null) {
					sInstance = new PushMsgProcessor();
				}
			}
		}
		return sInstance;
}

	public void handlePushMsg(final SnailPushMessage msg) {
		GameLauncherThreadPool.getCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				int type = msg.getType();
				switch (type) {
				case TYPE_GAME_COLLECTION:
				case TYPE_GAME_DETAIL:
				case TYPE_GAME_WEB:
					break;
				case TYPE_DOWNLOAD_GAME:
					doProcessDldMsg(msg.getPageId());
					break;
				default:
					break;
				}
			}
		});
		if (msg == null) {
			return;
		}
	}



	private void skipToGameDetail(String appId) {
		Intent intent = new Intent();
		intent.setClass(mContext, DetailActivity.class);
		intent.putExtra(GameLauncherConfig.APP_ID, appId);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		mContext.startActivity(intent);
	}


	private void doProcessDldMsg(String appId) {
		if (!PreferenceUtils.isAppOnlineDownload() 
				|| (!GameLauncherConfig.IGNORE_NETWORTYPE && !NetworkUtils.isWifiConnected(mContext))) {
			return;
		}
		if (!TextUtils.isEmpty(appId) && GameLauncher.hasInit()) {
			try {
				final AppEntity app = GameInfoHub.instance(mContext).obtainItemById(appId);
				if (app != null) {
					GameManager gm = GameLauncher.instance().getGameManager();
					GameStateManager gsm = gm.getGameStateManager();
					if (gsm != null && isNeedDldState(gsm, app.getPkgName())) {
						gm.download(app);
					}
				}
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
		}
	}


	private boolean isNeedDldState(GameStateManager gsm, String pkgName) {
		if (gsm != null && !TextUtils.isEmpty(pkgName)) {
			GameState state = gsm.getGameState(pkgName);
			return (GameState.DEFAULT.equals(state)
					|| (GameState.PAUSED.equals(state)));
		}
		return false;
	}

}
