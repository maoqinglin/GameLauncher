package com.ireadygo.app.gamelauncher.appstore.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.download.DldOperator;
import com.ireadygo.app.gamelauncher.appstore.download.IDldOperator;
import com.ireadygo.app.gamelauncher.appstore.download.IDldOperator.DldException;
import com.ireadygo.app.gamelauncher.appstore.download.IDldOperator.DldListener;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.install.IInstaller;
import com.ireadygo.app.gamelauncher.appstore.install.IInstaller.InstallException;
import com.ireadygo.app.gamelauncher.appstore.install.IInstaller.InstallResponse;
import com.ireadygo.app.gamelauncher.appstore.install.InstallManager;
import com.ireadygo.app.gamelauncher.appstore.install.InstallMessage;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;
import com.ireadygo.app.gamelauncher.utils.ToastUtils;
import com.ireadygo.app.gamelauncher.utils.Utils;
import com.ireadygo.app.gamelauncher.widget.GameLauncherNotification;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class GameManager {
	private static final boolean DEBUG = true;
	private static final String TAG = "GameManager";
	private IInstaller mInstaller;
	private GameInfoHub mGameInfoHub;
	private UpdateManager mUpdateManager;
	private MapGameManager mMapGameManager;
	private Context mContext;
	private IDldOperator mDldOperator;
	private GameStateManager mGameStateManager;
	private GameData mGameData;
//	private ARMManager mARMManager;
	private ArrayList<DownloadListener> mDownloadListeners = new ArrayList<GameManager.DownloadListener>();
	private ArrayList<InstallListener> mInstallListeners = new ArrayList<GameManager.InstallListener>();
	private ArrayList<MoveListener> mMoveListeners = new ArrayList<GameManager.MoveListener>();
	private ArrayList<UninstallListener> mUninstallListeners = new ArrayList<GameManager.UninstallListener>();
	private Handler mHandler;
	private ExecutorService mThreadPool = GameLauncherThreadPool.getFixedThreadPool();
	private GameLauncherNotification mGameLauncherNotification;
	private AppRestrictionManager mAppRestrictionManager;
	private FreeFlowManager mFreeFlowManager;
	private static final long MAP_GAME_DELAY = 60 * 1000;
	private static final String ACTION_LOAD_DATA_COMPLETE = "com.ireadygo.app.gamelauncher.ACTION_LOAD_DATA_COMPLETE";

	public GameManager(Context context) {
		mContext = context;
		mInstaller = new InstallManager(mContext);
		mGameInfoHub = GameInfoHub.instance(mContext);
		mDldOperator = new DldOperator(mContext);
		mDldOperator.addDldListener(mDldListener);

		mGameData = GameData.getInstance(context);
		mGameStateManager = new GameStateManager(context, mGameData);
		mGameLauncherNotification = new GameLauncherNotification(mContext,mGameStateManager);
		mUpdateManager = new UpdateManager(mContext,mGameLauncherNotification);
		mMapGameManager = MapGameManager.getInstance(mContext);
		mAppRestrictionManager = AppRestrictionManager.getInstance(mContext);
		mAppRestrictionManager.setGameLauncherNotification(mGameLauncherNotification);
		mGameData.addDataLoadCallback(mAppRestrictionManager);
		mGameData.addDataLoadCallback(mGameStateManager);
		mGameData.addDataLoadCallback(mUpdateManager);
		mGameData.initGameData(mContext);
		if (GameLauncherConfig.ENABLE_FREE_FLOW) {
			mFreeFlowManager = FreeFlowManager.getInstance(mContext);
		}

		GameLauncherAppState.getInstance(mContext).getModel().startLoader();
		mHandler = new Handler(context.getMainLooper());

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_LOAD_DATA_COMPLETE);
		mContext.registerReceiver(mReceiver, intentFilter);
	}

	public GameStateManager getGameStateManager() {
		return mGameStateManager;
	}

	public GameLauncherNotification getGameLauncherNotification() {
		return mGameLauncherNotification;
	}

	public FreeFlowManager getFreeFlowManager() {
		return mFreeFlowManager;
	}

	public void download(final AppEntity app) {
		GameState state = mGameStateManager.getGameState(app.getPkgName());
		if (null == state) {
			return;
		}
		Log.e(TAG,
				"GameManager download:" + app.getPkgName() + "-------"
						+ mGameStateManager.getGameState(app.getPkgName()));
		if (GameStateManager.isDownloadableState(state)) {
			if (!app.isDldPathEmpty(mContext)) {
				mDldOperator.dispatchDldOperator(app);
			} else {
				//下载地址为空，获取应用详情填充数据
				mThreadPool.execute(new Runnable() {
					@Override
					public void run() {
						try {
							AppEntity detailApp = mGameInfoHub.obtainItemById(app.getAppId());
							if (detailApp == null) {
								return;
							}
							mergeAppDetailToApp(app, detailApp);
							if(app.isDldPathEmpty(mContext)){
								Toast.makeText(mContext, mContext.getString(R.string.extend_install_error), Toast.LENGTH_SHORT).show();
								return;
							}
							mDldOperator.dispatchDldOperator(app);
						} catch (InfoSourceException e) {
							//handle exception
							e.printStackTrace();
						}
					}
				});
			}
			//下载海报图标
			if (!TextUtils.isEmpty(app.getPosterIconUrl())) {
				ImageLoader.getInstance().loadImage(app.getPosterIconUrl(), new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
					}
					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					}
					
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						if (arg2 != null) {
							mGameData.updatePosterIcon(app.getPkgName(), arg2);
						}
					}
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						
					}
				});
			}
		}
	}

	public void delete(AppEntity appEntity) {
		mDldOperator.delete(appEntity);
	}

	public void upgrade(AppEntity app) {
		// 更新接口，目前为直接下载，为以后增量更新预留
		if (GameState.UPGRADEABLE.equals(app.getGameState())) {
			download(app);
		}
	}

	public void install(final AppEntity app) {
		if (GameState.INSTALLABLE.equals(mGameStateManager.getGameState(app.getPkgName()))) {
//			String existPkg = checkExistPkg(app);
//			if (!TextUtils.isEmpty(existPkg)) {
//				reInstallApk(existPkg, app);
//				return;
//			}
			doInstall(app);
		}
	}

	public void uninstall(final String pkgName) {
		mInstaller.uninstall(new InstallResponse() {

			@Override
			public void onInstallSuccessfully(Object info) {
				handleGameUninstallSuccessfully(pkgName);
			}

			@Override
			public void onInstallStepStart(String step) {
			}

			@Override
			public void onInstallProgressChange(String step, int progress) {
			}

			@Override
			public void onInstallFailed(InstallException ie) {
				Log.w(TAG, "onInstallFailed:" + ie.getMessage());
				reportUninstallError(pkgName, new GameManagerException(GameManagerException.MSG_UNINSTALL_FAILED, ie));
			}
		}, pkgName); 
	}

	public boolean launch(String pkgName) {
		if(TextUtils.isEmpty(pkgName)){
			return false;
		}
		if (!GameStateManager.isLaunchableState(mGameStateManager.getGameState(pkgName))) {
			return false;
		}
		PackageManager pm = mContext.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(pkgName);
		if (intent == null) {
			//应用未使能，提示用户
			if (mAppRestrictionManager.isAppDisable(mContext,pkgName)) {
				AppEntity app = mGameData.getGameByPkgName(pkgName);
				if (app != null && AppEntity.NOT_OCCUPY_SLOT == app.getIsOccupySlot()) {
					mAppRestrictionManager.setAppEnableWithoutRecord(pkgName, true);
					return true;
				} else {
					Toast.makeText(mContext,mContext.getString(R.string.launch_disable_error),Toast.LENGTH_SHORT).show();
					return false;
				}
			}
			//无法找到对应apk，提示用户重新下载
			Toast.makeText(mContext,mContext.getString(R.string.launch_error),Toast.LENGTH_SHORT).show();
			AppEntity app = mGameData.getGameByPkgName(pkgName);
			if (app != null) {
				mGameStateManager.setGameState(pkgName, GameState.DEFAULT);
				app.setGameState(GameState.DEFAULT);
				reportInstallStateChange(app);
			}
			return false;
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			mContext.startActivity(intent);
			mGameData.updateLastLaunchTime(pkgName, System.currentTimeMillis());
			GameLauncherAppState.getInstance(mContext).getModel().updateModifiedTime(pkgName, System.currentTimeMillis());
		} catch (ActivityNotFoundException anfe) {
			// ignore
			return false;
		}
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put(EventID.KEY_APP_PACKAGE_NAME, pkgName);
//		MobclickAgent.onEvent(mContext, EventID.LAUNCH_APP, map);
		//上报免商店内启动游戏的事件
		AppEntity app = mGameData.getGameByPkgName(pkgName);
		if (app != null && !TextUtils.isEmpty(app.getAppId())) {
			StaticsUtils.openGameInFreeStore(app.getAppId());
		}
		return true;
	}


	public void checkUpgradeApps() {
		mUpdateManager.checkUpgradeApps();
	}

	public void shutdown() {
		mUpdateManager.shutdown();
		mInstaller.shutdown();
		mDldOperator.shutdown();
		mGameLauncherNotification.shutdown();
		mMapGameManager.shutdown();
//		mARMManager.shutdown();
		mAppRestrictionManager.shutdown();
		if (GameLauncherConfig.ENABLE_FREE_FLOW) {
			mFreeFlowManager.shutdown();
		}
	}


	// -----------install-------------------------//

	private void doInstall(final AppEntity app) {
		if(TextUtils.isEmpty(app.getSavedPath())){
			AppEntity otherApp = mGameData.getGameByPkgName(app.getPkgName());
			app.copyFrom(otherApp);
		}
		mInstaller.install(new InstallResponse() {
			@Override
			public void onInstallSuccessfully(Object info) {
				Toast.makeText(mContext, app.getName() + mContext.getString(R.string.extend_install_success), Toast.LENGTH_SHORT).show();
				// 安装成功，将状态改为Launchable
				app.setGameState(GameState.LAUNCHABLE);
				mGameStateManager.setGameState(app.getPkgName(), GameState.LAUNCHABLE);
				reportInstallStateChange(app);
				//上报安装成功事件
				StaticsUtils.installSuccess(app.getAppId());
			}

			@Override
			public void onInstallStepStart(String step) {
				if (IInstaller.STEP_INSTALL.equals(step)) {
					mGameStateManager.setGameState(app.getPkgName(), GameState.INSTALLING);
					app.setGameState(GameState.INSTALLING);
					reportInstallStateChange(app);
				} else if (IInstaller.STEP_UNZIP.equals(step)) {
//					mGameStateManager.setGameState(app.getPkgName(), GameState.UNZIPING);
//					app.setGameState(GameState.UNZIPING);
//					reportInstallStateChange(app);
				}
			}

			@Override
			public void onInstallProgressChange(String step, int progress) {
				reportInstallProgressChange(app, progress);
			}

			@Override
			public void onInstallFailed(InstallException ie) {
//				Toast.makeText(mContext, app.getName() + "安装失败：" + ie.getMessage(), Toast.LENGTH_SHORT).show();
				if (InstallMessage.INCONSISTENT_CERTIFICATES.equals(ie.getMessage())) {
					// 同包名，签名不一致，卸载后再安装
					reInstallApk(app.getPkgName(), app);
				} else if (InstallMessage.PACKAGE_NOT_FOUND.equals(ie.getMessage())
						|| InstallMessage.PACKAGE_UNREADABLE.equals(ie.getMessage())
						|| InstallMessage.INVALID_APK.equals(ie.getMessage())) {
					//文件不存在，修改为下载状态
					app.setGameState(GameState.DEFAULT);
					mGameStateManager.setGameState(app.getPkgName(), GameState.DEFAULT);
					delete(app);
					reportInstallError(app, new GameManagerException(GameManagerException.MSG_INSTALL_FAILED, ie));
				} else {
					app.setGameState(GameState.INSTALLABLE);
					mGameStateManager.setGameState(app.getPkgName(), GameState.INSTALLABLE);
					reportInstallError(app, new GameManagerException(GameManagerException.MSG_INSTALL_FAILED, ie));
				}
			}
		}, generateFilePath(app.getSavedPath(), app.getFileName()),app.getPkgName());
	}

	private String generateFilePath(String filePath, String fileName) {
		return new StringBuffer().append(filePath).append(File.separator).append(fileName).toString();
	}

	/*
	 * 签名冲突的apk，先卸载，再安装
	 */
	private void reInstallApk(final String uninstallPkg, final AppEntity installApp) {
		mInstaller.uninstall(new InstallResponse() {

			@Override
			public void onInstallSuccessfully(Object info) {
				// 卸载成功，继续安装
				doInstall(installApp);
			}

			@Override
			public void onInstallStepStart(String step) {
				// ignore
			}

			@Override
			public void onInstallProgressChange(String step, int progress) {
				// ignore
			}

			@Override
			public void onInstallFailed(InstallException ie) {
				reportUninstallError(uninstallPkg, new GameManagerException(GameManagerException.MSG_UNINSTALL_FAILED,
						ie));
			}
		}, uninstallPkg);
	}

	private String checkExistPkg(AppEntity app) {
		AppEntity existApp = mGameData.getExistApp(app.getPkgName(), app.getName());
		if (null == existApp) {
			return null;
		}
		//如果包名相同，则不处理，直接安装，有系统去检查签名
		if (TextUtils.isEmpty(app.getPkgName()) || (app.getPkgName().equals(existApp.getPkgName()))) {
			return null;
		}
		//如果包名不同，但是应用名相同，说明不是MUCH渠道的应用，返回其包名将其卸载
		return existApp.getPkgName();
	}

	public void mapInstallGame(String pkgName) {
		mMapGameManager.mapNewInstallGame(pkgName);
	}

	// ----------------download-------------------//

	private DldListener mDldListener = new DldListener() {

		@Override
		public void onDldStateChange(AppEntity app, DldException e) {
			mGameStateManager.setGameState(app.getPkgName(), app.getGameState());
			if (GameState.INSTALLABLE.equals(app.getGameState())) {
//				// 下载完成则安装
				install(app);
//				//下载完成，发送通知
				mGameLauncherNotification.addDownloadedNotification();
				reportDownloadStateChange(app);
				//上报下载完成通知
				StaticsUtils.downloadResult(app.getAppId(), true);
			} else if (GameState.ERROR.equals(app.getGameState())) {
					// 出错，则把出错信息回调出去
					reportDownloadError(app, new GameManagerException(GameManagerException.MSG_DOWNLOAD_ERROR,e));
					//上报下载出错
					StaticsUtils.downloadResult(app.getAppId(), false);
			} else {
				reportDownloadStateChange(app);
			}
			//下载状态变化，更新通知
			mGameLauncherNotification.addDownloadingNotification();
		}

		@Override
		public void onDldProgressChange(AppEntity app) {
			reportDownloadProgressChange(app);
		}

		@Override
		public void onDldItemAdd(AppEntity app) {
			reportDownloadItemAdd(app);
		}

		@Override
		public void onDldItemRemove(AppEntity app) {
			// 删除任务，修改状态
			GameState newState = null;
			if (AppEntity.CAN_UPGRADE == app.getIsUpdateable()) {
				//删除更新任务，恢复更新状态
				newState = GameState.UPGRADEABLE;
			} else {
				newState = GameState.DEFAULT;
			}
			app.setGameState(newState);
			mGameStateManager.setGameState(app.getPkgName(), newState);
			reportDownloadStateChange(app);
			//下载状态变化，更新通知
			mGameLauncherNotification.addDownloadingNotification();
		}
	};

	private void mergeAppDetailToApp(AppEntity app,AppEntity detail) {
		app.setAppId(detail.getAppId());
		app.setPkgName(detail.getPkgName());
		app.setTotalSize(detail.getTotalSize());
		app.setRemoteIconUrl(detail.getRemoteIconUrl());
		app.setSign(detail.getSign());
		app.setVersionCode(detail.getVersionCode());
		app.setVersionName(detail.getVersionName());
		app.setName(detail.getName());
		app.setDownloadPath(detail.getDownloadPath());
		app.setDescription(detail.getDescription());
		app.setScreenshotUrl(detail.getScreenshotUrl());
		app.setFreeFlag(detail.getFreeFlag());
		app.setFreeflowDldPath(detail.getFreeflowDldPath());
	}

	//--------------------uninstall----------------------------//

	public void handleGameUninstallSuccessfully(String pkgName) {
		if (hasInstallFile(pkgName)) {//如果卸载完成的应用在手机中有安装包，则变成可安装状态
			mGameStateManager.setGameState(pkgName, GameState.INSTALLABLE);
		} else {
			mGameStateManager.setGameState(pkgName, GameState.DEFAULT);
			reportUninstallComplete(pkgName);
		}
	}

	//检测指定报名的应用是否在手机中保存有安装包
	private boolean hasInstallFile(String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return false;
		}
		AppEntity app = mGameData.getGameByPkgName(pkgName);
		if (null == app) {
			return false;
		}
		String storeFilePath = app.getSavedPath() + File.separator + app.getFileName();
		if (TextUtils.isEmpty(storeFilePath)) {
			return false;
		}
		File file = new File(storeFilePath);
		return file.exists();
	}

	// -------------------listeners-----------------------------//

	/*
	 * 按功能模块抽象回调：DownloadListener和InstallListener
	 */
	public interface DownloadListener {
		void onDownloadItemAdd(AppEntity app);

		void onDownloadStateChange(AppEntity app);

		void onDownloadProgressChange(AppEntity app);

		void onDownloadError(AppEntity app, GameManagerException de);
	}

	public interface InstallListener {
		void onInstallStateChange(AppEntity app);

		void onInstallProgressChange(AppEntity app, int progress);

		void onInstallError(AppEntity app, GameManagerException ie);
	}

	public interface MoveListener {
		void onMoveStateChange(String pkgName, GameState newState);

		void onMoveError(String pkgName, GameManagerException ge);
	}

	public interface UninstallListener {
		void onUninstallComplete(String pkgName);

		void onUninstallError(String pkgName, GameManagerException ge);
	}

	public void addUninstallListener(UninstallListener listener) {
		if (null != listener) {
			synchronized (mUninstallListeners) {
				mUninstallListeners.add(listener);
			}
		}
	}

	public void removeUninstallListener(UninstallListener listener) {
		synchronized (mUninstallListeners) {
			mUninstallListeners.remove(listener);
		}
	}

	public void addInstallListener(InstallListener listener) {
		if (null != listener) {
			synchronized (mInstallListeners) {
				mInstallListeners.add(listener);
			}
		}

	}

	public void removeInstallListener(InstallListener listener) {
		synchronized (mInstallListeners) {
			mInstallListeners.remove(listener);
		}
	}

	public void addMoveListener(MoveListener listener) {
		if (null != listener) {
			synchronized (mMoveListeners) {
				mMoveListeners.add(listener);
			}
		}
	}

	public void removeMoveListener(MoveListener listener) {
		synchronized (mMoveListeners) {
			mMoveListeners.remove(listener);
		}
	}

	public void addDownloadListener(DownloadListener listener) {
		if (null != listener) {
			synchronized (mDownloadListeners) {
				mDownloadListeners.add(listener);
			}
		}
	}

	public void removeDownloadListener(DownloadListener listener) {
		synchronized (mDownloadListeners) {
			mDownloadListeners.remove(listener);
		}
	}

	/*
	 * 两类回调的统一处理函数，把回调抛回主线程处理
	 */

	private void reportDownloadItemAdd(final AppEntity app) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (mDownloadListeners) {
					for (DownloadListener listener : mDownloadListeners) {
						Log.e(TAG, "reportDownloadItemAdd:" + app.getPkgName() + "---" + app.getGameState());
						listener.onDownloadItemAdd(app);
					}
				}
			}
		});
	}

	private void reportDownloadStateChange(final AppEntity app) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (mDownloadListeners) {
					for (DownloadListener listener : mDownloadListeners) {
						Log.e(TAG, "reportDownloadStateChange:" + app.getPkgName() + "---" + app.getGameState());
						listener.onDownloadStateChange(app);
					}
				}
			}
		});
	}

	private void reportDownloadProgressChange(final AppEntity app) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (mDownloadListeners) {
					for (DownloadListener listener : mDownloadListeners) {
						listener.onDownloadProgressChange(app);
					}
				}
			}
		});
	}

	private void reportDownloadError(final AppEntity app, final GameManagerException ge) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (mDownloadListeners) {
					for (DownloadListener listener : mDownloadListeners) {
						listener.onDownloadError(app, ge);
						Log.e(TAG, "reportDownloadError:" + app.getPkgName() + "---" + ge.getMessage());
					}
					ToastUtils.ToastMsg(Utils.handleException(mContext, ge), true);
				}
			}
		});
	}

	private void reportInstallStateChange(final AppEntity app) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (mInstallListeners) {
					for (InstallListener listener : mInstallListeners) {
						listener.onInstallStateChange(app);
						Log.e(TAG, "reportInstallStateChange:" + app.getPkgName() + "---" + app.getGameState());
					}
				}
			}
		});
	}

	private void reportInstallProgressChange(final AppEntity app, final int progress) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (mInstallListeners) {
					for (InstallListener listener : mInstallListeners) {
						listener.onInstallProgressChange(app, progress);
						Log.e(TAG, "reportInstallProgressChange:" + app.getPkgName() + "---" + progress);
					}
				}
			}
		});
	}

	private void reportInstallError(final AppEntity app, final GameManagerException ge) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (mInstallListeners) {
					for (InstallListener listener : mInstallListeners) {
						listener.onInstallError(app, ge);
						Log.e(TAG, "reportInstallError:" + app.getPkgName() + "---" + ge.getMessage());
					}
					ToastUtils.ToastMsg(Utils.handleException(mContext, ge), true);
				}
			}
		});
	}



	private void reportUninstallComplete(final String pkgName) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (mUninstallListeners) {
					for (UninstallListener listener : mUninstallListeners) {
						listener.onUninstallComplete(pkgName);
						Log.e(TAG, "reportUninstallComplete:listener=" + listener);
					}
				}
			}
		});
	}

	private void reportUninstallError(final String pkgName, final GameManagerException ge) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				synchronized (mUninstallListeners) {
					for (UninstallListener listener : mUninstallListeners) {
						listener.onUninstallError(pkgName, ge);
						Log.e(TAG, "reportUninstallError:" + pkgName + "---" + ge.getMessage());
					}
					ToastUtils.ToastMsg(Utils.handleException(mContext, ge), true);
				}
			}
		});
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_LOAD_DATA_COMPLETE.equals(action)) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						List<AppEntity> gameList = mGameData.getAllInstalledApp(true);
						if (gameList != null && gameList.size() > 0) {
							mMapGameManager.mapGameList(gameList);
						}
					}
				}, MAP_GAME_DELAY);
			}
		}
	};

	/*
	 * 封装GameManager层次的出错信息，抛给界面处理
	 */
	public class GameManagerException extends Exception {
		private static final long serialVersionUID = 6958545857923305314L;

		public static final String MSG_DOWNLOAD_ERROR = "DOWNLOAD_ERROR";
		public static final String MSG_INSTALL_FAILED = "INSTALL_FAILED";
		public static final String MSG_MOVE_FAILED = "MOVE_FAILED";
		public static final String MSG_UNINSTALL_FAILED = "UNINSTALL_FAILED";

		public GameManagerException() {
			super();
		}

		public GameManagerException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

		public GameManagerException(String detailMessage) {
			super(detailMessage);
		}

		public GameManagerException(Throwable throwable) {
			super(throwable);
		}
	}
}
