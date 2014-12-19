package com.ireadygo.app.gamelauncher.appstore.download;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.download.DownloadManager.DownloadListener;
import com.ireadygo.app.gamelauncher.appstore.download.DownloadTask.DownloadState;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;

public class DldOperator implements IDldOperator {

	private static final String TAG = "DldOperator";
	private static final String APP_STORE_DLD_PATH = "iReadyGo" + File.separator + "appstore" + File.separator
			+ "Download";

	private final String mInternalDldPath;
	private final Context mContext;

	private final DownloadManager mDownloadManager;
	private final InnerDownloadListener mInnerDownloadListener = new InnerDownloadListener();
	private final ConcurrentLinkedQueue<DldListener> mDldListeners = new ConcurrentLinkedQueue<DldListener>();

	private IDownloadData mGameData = null;

	public DldOperator(Context context) {
		mContext = context;
		mInternalDldPath = mContext.getFilesDir().getPath();
		mDownloadManager = new DownloadManager(mContext, downloadConfig());
		mGameData = GameData.getInstance(context);
		initDownload();
	}

	@Override
	public void shutdown() {
		mDldListeners.clear();
		mDownloadManager.close();
		try {
			mGameData.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private DownloadConfig downloadConfig() {
		DownloadConfig downloadConfig = DownloadConfig.defaultConfig();
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			downloadConfig.setDownloadPath(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
					+ APP_STORE_DLD_PATH);
		} else {
			downloadConfig.setDownloadPath(mInternalDldPath);
		}

		return downloadConfig;
	}

	private void initDownload() {
		initDownloadStatus();
		mDownloadManager.addDownloadTaskListener(mInnerDownloadListener);
	}

	private void initDownloadStatus() {
		mGameData.resetGameDldStatus();
	}

	@Override
	public void create(AppEntity appEntity) {
		appEntity.setCreateTime(System.currentTimeMillis());
		appEntity.setGameState(GameState.DEFAULT);
//		appEntity.setIsInFreeStore(1);//从免商店下载，因此设置该标志为1
		appEntity.setIsComeFrmFreeStore(AppEntity.CAME_FROM_FREE_STORE);//从免商店下载，因此设置该标志为1
		appEntity.setIsOccupySlot(AppEntity.NOT_OCCUPY_SLOT);
		mDownloadManager.updateDownloadConfig(downloadConfig());
		mDownloadManager.open();
		mDownloadManager.addDownloadTask(appEntity);
		reportDldItemAdd(appEntity);
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put(EventID.KEY_AD_ID, appEntity.getAppId());
//		map.put(EventID.KEY_APP_NAME, appEntity.getName());
//		map.put(EventID.KEY_APP_PACKAGE_NAME, appEntity.getPkgName());
//		MobclickAgent.onEvent(mContext, EventID.DOWNLOAD_APP, map);
	}

	private void reportDldItemAdd(final AppEntity appEntity) {
		new Handler(mContext.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				for (DldListener listener : mDldListeners) {
					listener.onDldItemAdd(appEntity);
				}
			}
		});
	}
	private void reportDldItemRemove(final AppEntity appEntity) {
		new Handler(mContext.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				for (DldListener listener : mDldListeners) {
					listener.onDldItemRemove(appEntity);
				}
			}
		});
	}

	@Override
	public void pause(String id) {
		mDownloadManager.pause(id);
	}

	@Override
	public void resume(AppEntity app) {
		mDownloadManager.updateDownloadConfig(downloadConfig());
		mDownloadManager.resume(app);
	}

	@Override
	public void delete(AppEntity appEntity) {
		if (AppEntity.CAN_UPGRADE == appEntity.getIsUpdateable()) {
			//如果是移除升级的任务，仅删除内存状态及已经下载的文件，并将状态修改为upgradable
			mDownloadManager.removeUpgradeTask(appEntity.getAppId());
		} else {
			mDownloadManager.removeDownloadTask(appEntity.getAppId(), true);
		}
		reportDldItemRemove(appEntity);
	}

	@Override
	public void addDldListener(DldListener listener) {
		mDldListeners.add(listener);
	}

	@Override
	public void removeDldListener(DldListener listener) {
		mDldListeners.remove(listener);
	}

	private void reportDldItemProgressChange(DownloadTask task) {
		final AppEntity appEntity = task.getDownloadEntity();
//		mGameData.updateGameStatus(appEntity);
		new Handler(mContext.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				for (DldListener listener : mDldListeners) {
					listener.onDldProgressChange(appEntity);
				}
			}
		});
	}

	private void reportDldItemStateChange(DownloadTask task, DownloadState state, final DownloadException e) {
		final AppEntity appEntity = task.getDownloadEntity();
		syncState(appEntity, state);
		mGameData.updateGameStatus(appEntity);
		new Handler(mContext.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				DldException de = (e == null ? null : new DldException(e.getMessage(), e));
				for (DldListener listener : mDldListeners) {
					listener.onDldStateChange(appEntity, de);
				}
			}
		});
		if(DownloadState.COMPLETE.equals(state)){
			mDownloadManager.removeDownloadTask(appEntity.getAppId(), false);
		}
	}

	private void syncState(AppEntity appEntity, DownloadState state) {
		if(DownloadState.COMPLETE.equals(state)){
			appEntity.setGameState(GameState.INSTALLABLE);
			//上报更新成功事件
			if (AppEntity.CAN_UPGRADE == appEntity.getIsUpdateable()) {
				StaticsUtils.updateSuccess(appEntity.getAppId());
			}
			//下载完成后，可升级标志应该修改为默认
			appEntity.setIsUpdateable(AppEntity.CAN_NOT_UPGRADE);
		}else{
			appEntity.setGameState(GameState.valueOf(state.toString()));
		}
	}

	private class InnerDownloadListener implements DownloadListener {

		@Override
		public void onDownloadStateChange(DownloadTask task, DownloadState state, DownloadException e) {
			reportDldItemStateChange(task, state, e);
		}

		@Override
		public void onDownloadEntityChange(DownloadTask task, AppEntity appEntity) {
			reportDldItemProgressChange(task);
		}

		@Override
		public void onDownloadTaskAdd(DownloadTask task) {
			mGameData.saveGame(task.getDownloadEntity());
		}

		@Override
		public void onDownloadTaskRemove(DownloadTask task) {
			mGameData.deleteGame(task.getDownloadEntity());
		}
	}

	@Override
	public void dispatchDldOperator(AppEntity appEntity) {
		String appId = appEntity.getAppId();
		if(TextUtils.isEmpty(appId)){
			return;
		}
		DownloadTask currDownloadTask = mDownloadManager.getDownloadTask(appId);
		if (currDownloadTask != null) {
			DownloadState state = currDownloadTask.getDownloadStatus();
			switch (state) {
			case PAUSED:
			case ERROR:
				resume(appEntity);
				break;
			case QUEUING:
			case TRANSFERING:
				pause(appId);
				break;
			case COMPLETE:
				break;
			default:
				break;
			}
		} else {
			create(appEntity);
		}
	}
}