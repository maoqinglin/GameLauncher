package com.ireadygo.app.gamelauncher.appstore.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.GameLauncherReceiver;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.data.GameData.LocalDataLoadCallback;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserSlotInfoItem;
import com.ireadygo.app.gamelauncher.ui.account.AccountDetailActivity;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.widget.GameLauncherNotification;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

/*
 * 提供禁止和解禁应用的接口
 */
public class AppRestrictionManager implements LocalDataLoadCallback {

	private static final String TAG = "AppRestrictionManager";
	private static final int MSG_RESPONSE_SUCCESS = 100;
	private static final int MSG_RESPONSE_FAILED = 101;
	private static final int MSG_SYNC_SLOT = 200;
	private static final int MSG_FIX_MISS_ICON = 201;
	private static volatile AppRestrictionManager sInstance;
	private PackageManager mPackageManager;
	private Context mContext;
	private ExecutorService mThreadPool = GameLauncherThreadPool.getCachedThreadPool();
	private MapGameManager mMapGameManager;
	public static final String PKG_LIST = "PKG_LIST";
	public static final String ACTION_PKG_EXPIRING = "com.ireadygo.app.gamelauncher.ACTION_PKG_EXPIRING";
	public static final long SYNC_SLOT_INFO_DELAY = 3600 * 1000;//1hour
	private HandlerThread mSyncSlotInfoThread = new HandlerThread("sync_slot");
	private ArrayList<String> mEnablePkgList = new ArrayList<String>();
	private static final long ONE_DAY = 24 * 3600 * 1000;//1days
	private static final long ONE_MINUTE = 60 * 1000;//1minute
	private static final long MIN_EXPIRING_TIME = 3 * ONE_DAY;//3days
	private static final long MIN_NOTIFY_EXPIRING_SLOT_DELAY = ONE_DAY;
	private static final long PERMANENT_SLOT_EXPIRED_TIME = 631123200000L;//服务器设置的过期时间为1990年1月1日的绝对时间
	private GameLauncherNotification mGameLauncherNotification;
	private ArrayList<String> mExpiringList = new ArrayList<String>();

	public static AppRestrictionManager getInstance(Context context) {
			if (sInstance == null) {
				synchronized (AppRestrictionManager.class) {
					if (sInstance == null) {
						sInstance = new AppRestrictionManager(context);
					}
				}
			}
			return sInstance;
	}

	private AppRestrictionManager(Context context) {
		mContext = context;
		mPackageManager = mContext.getPackageManager();
		mMapGameManager = MapGameManager.getInstance(mContext);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		intentFilter.addAction(GameLauncherReceiver.ACTION_PACKAGE_UNINSTALL);
		intentFilter.addAction(AccountDetailActivity.ACTION_ACCOUNT_LOGOUT);
		intentFilter.addAction(GameLauncherReceiver.ACTION_PACKAGE_INSTALL);
		intentFilter.addAction(GameLauncherReceiver.ACTION_PACKAGE_UPDATE);
		mContext.registerReceiver(mReceiver, intentFilter);
		postHandleMsg(MSG_SYNC_SLOT, ONE_MINUTE, null);
		postHandleMsg(MSG_FIX_MISS_ICON, ONE_MINUTE * 2, null);
	}

	public void shutdown() {
		mContext.unregisterReceiver(mReceiver);
	}

	public void setGameLauncherNotification(GameLauncherNotification notification) {
		mGameLauncherNotification = notification;
	}

	/*
	 * 禁止应用，即应用与卡槽解除绑定
	 */
	public void DisableApp(final String pkgName,final AppRestrictionResponse response) {
		if (TextUtils.isEmpty(pkgName)) {
			postToResponseFailed(response, AppRestrictionResponse.ERR_PARAM_ERROR);
			return;
		}
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (setAppEnable(pkgName, false)) {
						postToResponseSuccess(response);
					} else {
						postToResponseFailed(response, AppRestrictionResponse.ERR_PARAM_ERROR);
					}
				} catch (NumberFormatException e) {
					postToResponseFailed(response, AppRestrictionResponse.ERR_PARAM_ERROR);
					return;
				}
			}
		});
	}

	/*
	 * 解禁应用，即应用与卡槽绑定
	 */
	public void EnableApp(final String pkgName,final AppRestrictionResponse response) {
		if (TextUtils.isEmpty(pkgName)) {
			postToResponseFailed(response, AppRestrictionResponse.ERR_PARAM_ERROR);
			return;
		}
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (setAppEnable(pkgName, true)) {
						postToResponseSuccess(response);
					} else {
						postToResponseFailed(response, AppRestrictionResponse.ERR_DEVICE_NOT_SUPPORT);
					}
				} catch (NumberFormatException e) {
					postToResponseFailed(response, AppRestrictionResponse.ERR_PARAM_ERROR);
					return;
				}
			}
		});
	}

	/*
	 * 根据帐号相关的卡槽信息，对手机中所有的非来自免商店的第三方应用进行禁止状态改变，用于切换帐号后对所有应用进行状态更新
	 */
//	public void UpdateAllAppDisableStates(final AppRestrictionResponse response) {
//		//向服务器获取当前帐号的卡槽使用情况
//		mThreadPool.execute(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					List<UserSlotInfoItem> userSlotInfoItems 
//					= GameInfoHub.instance(mContext).getUserSlotInfoItems();
//					ArrayList<String> enablePkgList = new ArrayList<String>();
//					for (UserSlotInfoItem userSlotInfoItem : userSlotInfoItems) {
//						enablePkgList.add(userSlotInfoItem.getCPackage());
//					}
//					List<AppEntity> gameList = GameData.getInstance(mContext).getGamesComeNotFrmFreeStore();
//					for (AppEntity game : gameList) {
//						if (game.getPkgName().equals(mContext.getPackageName())) {//避免自身被操作
//							continue;
//						}
//						if (enablePkgList.contains(game.getPkgName())) {
//							setAppEnable(game.getPkgName(), true);
//						} else {
//							setAppEnable(game.getPkgName(), false);
//						}
//					}
//					postToResponseSuccess(response);
//				} catch (NumberFormatException e) {
//					postToResponseFailed(response, AppRestrictionResponse.ERR_NO_MORE_SLOT_ERROR);
//				} catch (InfoSourceException e) {
//					postToResponseFailed(response, AppRestrictionResponse.ERR_UNKNOWN_ERROR);
//				}
//			}
//		});
//
//	}

//	public void UpdateAppDisableStates(final String pkgName,final AppRestrictionResponse response) {
//		//向服务器获取当前帐号的卡槽使用情况
//		mThreadPool.execute(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					List<UserSlotInfoItem> userSlotInfoItems
//					= GameInfoHub.instance(mContext).getUserSlotInfoItems();
//					ArrayList<String> enablePkgList = new ArrayList<String>();
//					for (UserSlotInfoItem userSlotInfoItem : userSlotInfoItems) {
//						enablePkgList.add(userSlotInfoItem.getCPackage());
//					}
//					if (mContext.getPackageName().equals(pkgName)) {//避免自身被操作
//						postToResponseSuccess(response);
//						return;
//					}
//					//仅已绑定应用安装时，自动使能，不做禁止操作
//					if (enablePkgList.contains(pkgName)) {
//						setAppEnable(pkgName, true);
//					}
//					postToResponseSuccess(response);
//				} catch (NumberFormatException e) {
//					postToResponseFailed(response, AppRestrictionResponse.ERR_NO_MORE_SLOT_ERROR);
//				} catch (InfoSourceException e) {
//					postToResponseFailed(response, AppRestrictionResponse.ERR_UNKNOWN_ERROR);
//				}
//			}
//		});
//
//	}


	public boolean syncEnablePkgDataWithRemote(final AppRestrictionResponse response) {
		if (System.currentTimeMillis() - PreferenceUtils.getLastSyncSlotTime() < SYNC_SLOT_INFO_DELAY) {
			postToResponseFailed(response, AppRestrictionResponse.ERR_PARAM_ERROR);
			return false;
		}
			final String userId = AccountManager.getInstance().getLoginUni(mContext);
			if (TextUtils.isEmpty(userId)) {
				postToResponseFailed(response, AppRestrictionResponse.ERR_UNLOGIN_ERROR);
				return false;
			}
			mThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						List<UserSlotInfoItem> userSlotInfoItems 
						= GameInfoHub.instance(mContext).getUserSlotInfoItems();
						ArrayList<String> enablePkgList = new ArrayList<String>();
						long minExpiringTime = MIN_EXPIRING_TIME;
						synchronized (mExpiringList) {
							mExpiringList.clear();
							for (UserSlotInfoItem userSlotInfoItem : userSlotInfoItems) {
								enablePkgList.add(userSlotInfoItem.getCPackage());
								if (PERMANENT_SLOT_EXPIRED_TIME == userSlotInfoItem.getDExpire().getTime()) {
									continue;
								}
								long expiringTime = userSlotInfoItem.getDExpire().getTime() - userSlotInfoItem.getReqTime();
								if (expiringTime < MIN_EXPIRING_TIME) {
									//快过期的列表
									mExpiringList.add(userSlotInfoItem.getCPackage());
									if (minExpiringTime > expiringTime) {
										minExpiringTime = expiringTime;
									}
								}
							}
							//有快过期的应用且满足通知间隔，即通知
							if (mExpiringList.size() > 0
									&& System.currentTimeMillis() - PreferenceUtils.getLastNotifySlotExpiredTime() > MIN_NOTIFY_EXPIRING_SLOT_DELAY) {
//								mGameLauncherNotification.addSlotExpiredNotification((int)msToDays(minExpiringTime));
							}
						}
						synchronized (mEnablePkgList) {
							//本地无，远程有，解绑
							for (String remotePkg : enablePkgList) {
								if (!mEnablePkgList.contains(remotePkg)) {
									GameInfoHub.instance(mContext).unbindAppToSlot(remotePkg);
								}
							}
							//本地有，远程无，绑定
							final HashSet<String> bindFailedPkgs = new HashSet<String>();
							for (String localPkg : mEnablePkgList) {
								if (!enablePkgList.contains(localPkg)) {
									try {
										GameInfoHub.instance(mContext).bindAppToSlot(localPkg);
									} catch (InfoSourceException e) {
										if (InfoSourceException.MSG_SLOT_NOT_ENOUGH_ERROR.equals(e.getMessage())) {
											bindFailedPkgs.add(localPkg);
										}
									}
								}
							}
							for (String failedPkg : bindFailedPkgs) {
								setAppEnable(failedPkg, false);
							}
							if (bindFailedPkgs.size() > 0) {
								//因为卡槽不足绑定不成功的应用，视为过期
								mHandler.post(new Runnable() {
									
									@Override
									public void run() {
										Toast.makeText(mContext, bindFailedPkgs.size() + mContext.getString(R.string.Toast_slot_overdue),
												Toast.LENGTH_SHORT).show();
									}
								});
							}
						}
						postToResponseSuccess(response);
					} catch (NumberFormatException e) {
						postToResponseFailed(response, AppRestrictionResponse.ERR_NO_MORE_SLOT_ERROR);
					} catch (InfoSourceException e) {
						postToResponseFailed(response, AppRestrictionResponse.ERR_UNKNOWN_ERROR);
					}
				}
			});
			return false;
		}

	private long msToDays(long ms) {
		if (ms < 0) {
			return 0;
		}
		return ms/ONE_DAY;
	}

	public boolean isExpiringApp(String pkg) {
		if (TextUtils.isEmpty(pkg)) {
			return false;
		}
		synchronized (mExpiringList) {
			return mExpiringList.contains(pkg);
		}
	}

	/*
	 * 返回指定包是否处于被禁止状态
	 */
	public static boolean isAppDisable(Context context, String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return true;
		}
		try {
			int result = context.getPackageManager().getApplicationEnabledSetting(pkgName);
			if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == result) {
				return true;
			} else if (PackageManager.COMPONENT_ENABLED_STATE_ENABLED == result
					|| PackageManager.COMPONENT_ENABLED_STATE_DEFAULT == result) {
				return false;
			} else {
				return true;
			}
		} catch (IllegalArgumentException e) {
			return true;
		}
	}

	public boolean isSnailCtrlEnableApp(String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return false;
		}
		try {
			Method areSnailControlEnabledForPackage = PackageManager.class.getDeclaredMethod("areSnailControlEnabledForPackage", String.class);
			return (Boolean)areSnailControlEnabledForPackage.invoke(mPackageManager, pkgName);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setAppEnable(String pkgName,boolean isEnable) {
		if (TextUtils.isEmpty(pkgName)) {
			return false;
		}
		try {
			//先调用系统封装的接口进行操作
			Method setSnailControlEnabledForPackage = 
					PackageManager.class.getDeclaredMethod("setSnailControlEnabledForPackage", String.class,boolean.class);
			setSnailControlEnabledForPackage.invoke(mPackageManager, pkgName,isEnable);
			if (isEnable) {
				mPackageManager.setApplicationEnabledSetting(pkgName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
				mMapGameManager.mapInstalledGame(pkgName);
				addEnablePkg(pkgName);
			} else {
				mPackageManager.setApplicationEnabledSetting(pkgName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
				mMapGameManager.mapDisableGame(pkgName);
				removeEnablePkg(pkgName);
			}
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setAppEnableWithoutMapAndRecord(String pkgName,boolean isEnable) {
		if (TextUtils.isEmpty(pkgName)) {
			return false;
		}
		try {
			//先调用系统封装的接口进行操作
			Method setSnailControlEnabledForPackage = 
					PackageManager.class.getDeclaredMethod("setSnailControlEnabledForPackage", String.class,boolean.class);
			setSnailControlEnabledForPackage.invoke(mPackageManager, pkgName,isEnable);
			if (isEnable) {
				mPackageManager.setApplicationEnabledSetting(pkgName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
			} else {
				mPackageManager.setApplicationEnabledSetting(pkgName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
			}
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setAppEnableWithoutRecord(String pkgName,boolean isEnable) {
		if (TextUtils.isEmpty(pkgName)) {
			return false;
		}
		try {
			//先调用系统封装的接口进行操作
			Method setSnailControlEnabledForPackage = 
					PackageManager.class.getDeclaredMethod("setSnailControlEnabledForPackage", String.class,boolean.class);
			setSnailControlEnabledForPackage.invoke(mPackageManager, pkgName,isEnable);
			if (isEnable) {
				mPackageManager.setApplicationEnabledSetting(pkgName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
				mMapGameManager.mapInstalledGame(pkgName);
			} else {
				mPackageManager.setApplicationEnabledSetting(pkgName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
				mMapGameManager.mapDisableGame(pkgName);
			}
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}


	private void postToResponseSuccess(AppRestrictionResponse response) {
		if (response == null) {
			Log.w(TAG,"postToResponseSuccess is null!");
			return;
		}
		Message msg = mHandler.obtainMessage(MSG_RESPONSE_SUCCESS, response);
		mHandler.sendMessageDelayed(msg, 0);
	}

	private void postToResponseFailed(AppRestrictionResponse response,int errorCode) {
		if (response == null) {
			Log.w(TAG,"postToResponseFailed:response is null!");
			return;
		}
		Message msg = mHandler.obtainMessage(MSG_RESPONSE_FAILED, errorCode,0,response);
		mHandler.sendMessageDelayed(msg, 0);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESPONSE_SUCCESS:
				if (msg.obj != null) {
					AppRestrictionResponse response = (AppRestrictionResponse)msg.obj;
					response.onSuccess();
				}
				break;
			case MSG_RESPONSE_FAILED:
				if (msg.obj != null) {
					AppRestrictionResponse response = (AppRestrictionResponse)msg.obj;
					response.onFailed(msg.arg1);//arg1 filled with error code
				}
				break;
			default:
				break;
			}
		};
	};

	private void postHandleMsg(int msgTag,long delay,Object object) {
		Message msg;
		if (object != null) {
			msg = mSyncSlotHandler.obtainMessage(msgTag, object);
		} else {
			msg = mSyncSlotHandler.obtainMessage(msgTag);
		}
		mSyncSlotHandler.sendMessageDelayed(msg, delay);
	}

	{
		mSyncSlotInfoThread.start();
	}
	private Handler mSyncSlotHandler = new Handler(mSyncSlotInfoThread.getLooper()) {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SYNC_SLOT:
				syncEnablePkgDataWithRemote(new AppRestrictionResponse() {
					@Override
					public void onSuccess() {
						PreferenceUtils.setLastSyncSlotTime(System.currentTimeMillis());
					}
					@Override
					public void onFailed(int errCode) {
					}
				});
				postHandleMsg(MSG_SYNC_SLOT, SYNC_SLOT_INFO_DELAY, null);
				break;
			case MSG_FIX_MISS_ICON:
				checkAndFixMissApp();
				break;
			default:
				break;
			}
		};
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				if (isNetworkConnected()) {
					syncEnablePkgDataWithRemote(new AppRestrictionResponse() {
						@Override
						public void onSuccess() {
							PreferenceUtils.setLastSyncSlotTime(System.currentTimeMillis());
						}
						@Override
						public void onFailed(int errCode) {
						}
					});
				}
			} else if (GameLauncherReceiver.ACTION_PACKAGE_UNINSTALL.equals(action)) {
				String pkgName = intent.getStringExtra(GameLauncherReceiver.KEY_PKG);
				if (!TextUtils.isEmpty(pkgName)) {
					removeEnablePkg(pkgName);
				}
			} else if (AccountDetailActivity.ACTION_ACCOUNT_LOGOUT.equals(action)) {
				//登出帐号，将全部使能的应用禁止
				synchronized (mEnablePkgList) {
					for (String pkgName : mEnablePkgList) {
						setAppEnableWithoutRecord(pkgName, false);
					}
					mEnablePkgList.clear();
				}
				//总卡槽和已使用卡槽清0
				PreferenceUtils.setSlotNum(GameLauncherConfig.DEFAULT_SLOT_NUM);
				PreferenceUtils.setUsedSlotNum(0);
				PreferenceUtils.saveBindPhoneNum("");
				PreferenceUtils.savePhoneNum("");
				PreferenceUtils.saveFreeFlowBindPhoneNum("");
			} else if (GameLauncherReceiver.ACTION_PACKAGE_INSTALL.equals(action)
					|| GameLauncherReceiver.ACTION_PACKAGE_UPDATE.equals(action)) {
				//新安装或更新的应用，如果不占卡槽，则需要在使能应用列表中移除
				String pkgName = intent.getStringExtra(GameLauncherReceiver.KEY_PKG);
				synchronized (mEnablePkgList) {
					if (!TextUtils.isEmpty(pkgName) && mEnablePkgList.contains(pkgName)) {
						AppEntity app = GameData.getInstance(mContext).getGameByPkgName(pkgName);
						if (app != null) {
							if (AppEntity.NOT_OCCUPY_SLOT == app.getIsOccupySlot()) {
								mEnablePkgList.remove(app.getPkgName());
							}
						}
					}
				}
			}
		}
	};

	private boolean isNetworkConnected() {
		NetworkInfo ni = ((ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		return (ni != null && ni.getState() == NetworkInfo.State.CONNECTED);
	}

	private void addEnablePkg(String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		synchronized (mEnablePkgList) {
			if (!mEnablePkgList.contains(pkgName)) {
				mEnablePkgList.add(pkgName);
			}
		}
	}

	private void removeEnablePkg(String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		synchronized (mEnablePkgList) {
			mEnablePkgList.remove(pkgName);
		}
	}


	private void loadCachedEnablePkg() {
		List<AppEntity> allApp = GameData.getInstance(mContext).getGamesOccupySlot();
		synchronized (mEnablePkgList) {
			mEnablePkgList.clear();
		}
		for (AppEntity app : allApp) {
			if (!isAppDisable(mContext,app.getPkgName())) {
				addEnablePkg(app.getPkgName());
			}
		}
	}

	/*
	 * 检测不占卡槽的应用是否有被禁止的，有则将其使能并显示出来。
	 */
	private void checkAndFixMissApp() {
		List<AppEntity> appList = GameData.getInstance(mContext).getGamesNotOccupySlot();
		for (AppEntity app : appList) {
			if (isAppDisable(mContext,app.getPkgName())) {
				setAppEnableWithoutRecord(app.getPkgName(), true);
			}
		}
	}

	public interface AppRestrictionResponse {
		int ERR_PARAM_ERROR = 1001;
		int ERR_NETWORK_ERROR = 1002;
		int ERR_UNLOGIN_ERROR = 1003;
		int ERR_UNKNOWN_ERROR = 1004;
		int ERR_NO_MORE_SLOT_ERROR = 1005;
		int ERR_DEVICE_NOT_SUPPORT = 1006;
		int ERR_APP_HAS_BIND = 1007;
		void onSuccess();
		void onFailed(int errCode);
	}

	@Override
	public void loadSuccess() {
		loadCachedEnablePkg();
	}

	@Override
	public void loadFail() {
	}
}
