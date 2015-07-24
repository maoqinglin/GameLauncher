package com.ireadygo.app.gamelauncher.aidl.wx;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.GameLauncher.InitComplete;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.DownloadListener;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.GameManagerException;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.InstallListener;
import com.ireadygo.app.gamelauncher.appstore.manager.GameStateManager;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;

public class WXPublicManagerRemoteServiceImpl extends IWXPublicManagerAidlService.Stub{

	private static final String ACTION_ESCROWMANAGER = "com.ireadygo.app.escrow";
	private static final String INTENT_APP_ID = "ID";
	private final Context mContext;
	private GameManager mGameManager;
	private GameStateManager mGameStateManager;
	private InnerOperatorListener mListener = new InnerOperatorListener();

	public WXPublicManagerRemoteServiceImpl(Context context) {
		mContext = context;
	}

	@Override
	public void operator(AppInfo appInfo) throws RemoteException {
		if(!TextUtils.isEmpty(appInfo.getAppId())) {
			mGameManager.download(appInfoToAppEntity(appInfo));
		}
	}

	private AppEntity appInfoToAppEntity(AppInfo appInfo) {
		AppEntity app = new AppEntity();
		app.setAppId(appInfo.getAppId());
		app.setPkgName(appInfo.getPkgName());
		app.setTotalSize(appInfo.getTotalSize());
		app.setRemoteIconUrl(appInfo.getIconUrl());
		app.setSign(appInfo.getMD5());
		app.setVersionCode(appInfo.getVersionCode());
		app.setVersionName(appInfo.getVersionName());
		app.setName(appInfo.getAppName());
		app.setDownloadPath(appInfo.getDownloadUrl());
		app.setDescription(appInfo.getDesc());
		app.setScreenshotUrl(appInfo.getPicUrl());
		app.setFreeFlag(appInfo.getFlowfreeFlag());
		app.setDownloadCounts(appInfo.getDownloadCount());
		app.setStatus(appInfo.getGameStatus());
		return app;
	}

	public void init() {
		if (!GameLauncher.hasInit()) {
			GameLauncher.init(mContext, new InitComplete() {
				@Override
				public void onInitCompleted() {
					mGameManager = GameLauncher.instance().getGameManager();
					mGameStateManager = mGameManager.getGameStateManager();
					mGameManager.addDownloadListener(mListener);
					mGameManager.addInstallListener(mListener);
				}
			});
		} else {
			mGameManager = GameLauncher.instance().getGameManager();
			mGameStateManager = mGameManager.getGameStateManager();
			mGameManager.addDownloadListener(mListener);
			mGameManager.addInstallListener(mListener);
		}
	}

	public void onDestory() {
		mGameManager.removeDownloadListener(mListener);
		mGameManager.removeInstallListener(mListener);
	}

	private class InnerOperatorListener implements DownloadListener, InstallListener {

		@Override
		public void onInstallStateChange(AppEntity app) {
			if(mGameStateManager.getGameState(app.getPkgName()) == GameState.LAUNCHABLE) {
				Intent intent = new Intent(ACTION_ESCROWMANAGER);
				intent.putExtra(INTENT_APP_ID, app.getAppId());
				mContext.sendBroadcast(intent);
			}
		}

		@Override
		public void onInstallProgressChange(AppEntity app, int progress) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onInstallError(AppEntity app, GameManagerException ie) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDownloadItemAdd(AppEntity app) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDownloadStateChange(AppEntity app) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDownloadProgressChange(AppEntity app) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDownloadError(AppEntity app, GameManagerException de) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
