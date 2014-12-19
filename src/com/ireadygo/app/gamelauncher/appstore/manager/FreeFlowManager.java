package com.ireadygo.app.gamelauncher.appstore.manager;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.FreeFlowStatusItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.FreeFlowType;
import com.ireadygo.app.gamelauncher.ui.account.AccountDetailActivity;
import com.ireadygo.app.gamelauncher.ui.account.FreeFlowRechargeActivity;
import com.ireadygo.app.gamelauncher.ui.activity.BaseAccountActivity;
import com.ireadygo.app.gamelauncher.ui.widget.SimpleConfirmDialog;
import com.ireadygo.app.gamelauncher.utils.DeviceUtil;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.snail.appstore.openapi.AppPlatFormConfig;

public class FreeFlowManager {

	private Context mContext;
	private static volatile FreeFlowManager sInstance;

	private static final String TAG = "FreeFlowManager";

	public static final int SUCCESS_CODE = 0;
	public static final int ERR_CODE_FREE_FLOW_NOT_SUPPORT = 1;
	public static final int ERR_CODE_FREE_FLOW_ENABLE_FAILED = 2;

	private static final int TYPE_OPERATE_START_UP = 0;
	private static final int TYPE_OPERATE_LOGIN = 1;
	private static final int TYPE_OPERATE_LOGOUT = 2;

	private static final int MSG_PROCESS_START_UP = 100;
	private static final int MSG_PROCESS_LOG_IN = 101;
	private static final int MSG_PROCESS_LOG_OUT = 102;

	private static final long DELAY_PROCESS_START_UP = 1 * 60 * 1000;//1 minute
	private static final long DELAY_PROCESS_LOG_IN = 10 * 1000;//10 seconds
	private static final long DELAY_PROCESS_LOG_OUT = 10 * 1000;//10 seconds

	private static final String APPID_DIVIDER = ",";
	private static final Object DOMAIN_LOCK = new Object();
	private static final Object MODE_LOCK = new Object();

	private static String mCurFreeFlowMode;
	private String mCurDomain;
	private GameInfoHub mGameInfoHub;
	private HandlerThread mProcessThread = new HandlerThread("process");
	private static final String NEED_REPLACED_DOMAIN_1 = "app.snail.com";
	private static final String NEED_REPLACED_DOMAIN_2 = "app1.snail.com";


	public static FreeFlowManager getInstance(Context context) {
		if (sInstance == null) {
			synchronized (FreeFlowManager.class) {
				if (sInstance == null) {
					sInstance = new FreeFlowManager(context);
				}
			}
		}
		return sInstance;
	}

	private FreeFlowManager(Context context) {
		mContext = context;
		mCurFreeFlowMode = PreferenceUtils.getFreeFlowMode();
		mCurDomain = PreferenceUtils.getFreeFlowDomain();
		mGameInfoHub = GameInfoHub.instance(mContext);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AccountDetailActivity.ACTION_ACCOUNT_LOGOUT);
		intentFilter.addAction(BaseAccountActivity.ACTION_ACCOUNT_LOGIN);
		context.registerReceiver(mReceiver, intentFilter);
		postMsg(MSG_PROCESS_START_UP, DELAY_PROCESS_START_UP, null);
	}

	public void shutdown() {
		mContext.unregisterReceiver(mReceiver);
	}

	public int enableFreeFlow(String phoneNum) {

		int resultCode = subScribeFreeFlow(phoneNum);
		if (SUCCESS_CODE == resultCode) {
			try {
				FreeFlowStatusItem status = mGameInfoHub.getFreeFlowStatus(phoneNum);
				String type = status.getmFreeFlowType();
				String domain = status.getmDomainUrl();
				nativeEnableFreeFlow(type, domain);
				mGameInfoHub.cleanCached();
				return SUCCESS_CODE;
			} catch (InfoSourceException e) {
				e.printStackTrace();
				return ERR_CODE_FREE_FLOW_ENABLE_FAILED;
			}
		}
		return resultCode;
	}

	/*
	 * 开通免流量
	 */
	private int subScribeFreeFlow(String phoneNum) {
		//远程开通免流量接口
		try {
			mGameInfoHub.subScribeFreeFlow(phoneNum);
			return SUCCESS_CODE;
		} catch (InfoSourceException e) {
			String errMsg = e.getMessage();
			if (InfoSourceException.MSG_FREE_FLOW_HAS_OPENED.equals(errMsg)) {
				return SUCCESS_CODE;
			} else if (InfoSourceException.MSG_FREE_FLOW_NOT_SUPPORT.equals(errMsg)) {
				PreferenceUtils.saveFreeFlowNotSupportNum(phoneNum);
				return ERR_CODE_FREE_FLOW_NOT_SUPPORT;
			}
		}
		return ERR_CODE_FREE_FLOW_ENABLE_FAILED;
	}


	/*
	 * 关闭免流量
	 */
	public boolean disableFreeFlow() {
		return nativeDisableFreeFlow(getFreeFlowMode());
	}

	/*
	 * 跳转绑定免流量手机号界面
	 */

	public void showBindPhoneUI(final boolean hasBindPhone) {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				final Activity currentActivity = GameLauncherApplication.getApplication().getCurrentActivity();
				if(currentActivity == null || currentActivity.isFinishing() || currentActivity.isDestroyed()){
					return;
				}
				final SimpleConfirmDialog dialog = new SimpleConfirmDialog(currentActivity);
				dialog
				.setPrompt(R.string.free_flow_confirm_prompt)
				.setMsg(hasBindPhone ? R.string.free_flow_phone_conflict_prompt : R.string.free_flow_phone_empty_prompt)
				.setConfirmClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext,FreeFlowRechargeActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
								currentActivity.startActivity(intent);
								SoundPoolManager.instance(mContext).play(SoundPoolManager.SOUND_ENTER);
								dialog.dismiss();
							}
						});
				dialog.show();
			}
		});
	}

	public String getFreeFlowMode() {
		synchronized (MODE_LOCK) {
			return mCurFreeFlowMode;
		}
	}

	public String getDomain() {
		synchronized (DOMAIN_LOCK) {
			return mCurDomain;
		}
	}

	public boolean isUnProxyMode() {
		synchronized (MODE_LOCK) {
			return (FreeFlowType.TYPE_FLOW_PACKET_INTERFACE.equals(mCurFreeFlowMode)
					|| FreeFlowType.TYPE_FLOW_PACKET_WEB.equals(mCurFreeFlowMode)
					|| FreeFlowType.TYPE_FLOW_POOL.equals(mCurFreeFlowMode)
					|| FreeFlowType.TYPE_MIX.equals(mCurFreeFlowMode));
		}
	}

	public boolean isProxyMode() {
		synchronized (MODE_LOCK) {
			return (FreeFlowType.TYPE_AGENT.equals(mCurFreeFlowMode));
		}
	}

	public boolean isFreeFlowDisable() {
		synchronized (MODE_LOCK) {
			return (FreeFlowType.TYPE_DISABLE.equals(mCurFreeFlowMode));
		}
	}

	/*
	 * 本地实现开通免流量的函数
	 */
	private boolean nativeEnableFreeFlow(String type,String domain) {
		cachedFreeFlowMode(type);
		if (isUnProxyMode()) {
			if (!TextUtils.isEmpty(domain)) {
				cachedFreeFlowDomain(domain);
				enableWithUnProxyMode(domain);
				return true;
			}
			return false;
		}
		if (isProxyMode()) {
			cachedFreeFlowDomain(GameLauncherConfig.DEFAULT_REMOTE_REQUEST_DOMAIN);
			enableWithProxyMode();
			return true;
		}
		return false;
	}

	private boolean nativeDisableFreeFlow(String type) {
		if (isUnProxyMode()) {
			disableWithUnProxyMode();
		}
		if (isProxyMode()) {
			disableWithProxyMode();
		}
		cachedFreeFlowMode(FreeFlowType.TYPE_DISABLE);
		cachedFreeFlowDomain(GameLauncherConfig.DEFAULT_REMOTE_REQUEST_DOMAIN);
		return true;
	}

	/*
	 * 实现开通代理模式免流量
	 */
	private boolean enableWithProxyMode() {
		/*
		 * 1.下载免的状态重新获取下载地址
		 */
		replaceDownloadUrl();
		return false;
	}

	/*
	 * 关闭免流量代理模式
	 */
	private boolean disableWithProxyMode() {
		//恢复数据库中各项下载地址
//		resetDownloadUrl();
		return true;
	}

	/*
	 * 实现开通非代理模式免流量
	 */
	private boolean enableWithUnProxyMode(String domain) {
		return changeDomain(domain);
	}

	/*
	 * 关闭非代理模式免流量
	 */
	private boolean disableWithUnProxyMode() {
		return resetDomain();
	}

	/*
	 * 域名改变
	 */
	private boolean changeDomain(String domain) {
		/*
		 * 1.更换远程请求接口域名
		 * 2.替换数据库中所有链接的域名(下载地址，截图地址不替换，图标地址不替换)
		 */
		if (TextUtils.isEmpty(domain)) {
			return false;
		}
		String newHostUrl = replaceDomain(GameLauncherConfig.DEFAULT_REMOTE_REQUEST_DOMAIN);
		if (!TextUtils.isEmpty(newHostUrl)) {
			AppPlatFormConfig.setHttpHost(newHostUrl);
		}
		replaceDownloadUrlDomain(domain);

		return true;
	}

	private boolean resetDomain() {
		AppPlatFormConfig.setHttpHost(GameLauncherConfig.DEFAULT_REMOTE_REQUEST_DOMAIN);
		return false;
	}


	{
		mProcessThread.start();
	}

	private Handler mHandler = new Handler(mProcessThread.getLooper()) {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_PROCESS_START_UP:
				processStartUp();
				break;
			case MSG_PROCESS_LOG_IN:
				processLogin();
				break;
			case MSG_PROCESS_LOG_OUT:
				processLogout();
				break;
			default:
				break;
			}
		};
	};

	private void postMsg(int msgTag,long delay,Object object) {
		Message msg;
		if (object == null) {
			msg = mHandler.obtainMessage(msgTag, object);
		} else {
			msg = mHandler.obtainMessage(msgTag);
		}
		mHandler.sendMessageDelayed(msg, delay);
	}

	private void processStartUp() {
		doProcessFreeFlow(TYPE_OPERATE_START_UP);
	}

	private void processLogin() {
		doProcessFreeFlow(TYPE_OPERATE_LOGIN);
	}

	private void processLogout() {
		disableFreeFlow();
	}

	/*
	 * 检测及开通免流量的主流程
	 */
	private void doProcessFreeFlow(int operateType) {
		/*
		 * 1.读取免帐号的手机号
		 * 2.获取本机手机号
		 * 3.比对两个手机号是否一致
		 * 4.一致，进行更新状态操作，不一致提示重新绑定
		 */
		if (!NetworkUtils.isNetworkConnected(mContext)) {
			Log.w(TAG, "Process Free Flow network is not conneted!");
			return;
		}
		if (!AccountManager.getInstance().isLogined(mContext)) {
			Log.w(TAG, "Process Free Flow Account is not login!");
			return;
		}
		String accountPhoneNum = PreferenceUtils.getFreeFlowBindPhoneNum();
		String devicePhoneNum = DeviceUtil.getDevicePhoneNum(mContext);
		if (TextUtils.isEmpty(devicePhoneNum)) {
			return;
		}

		if (devicePhoneNum.equals(accountPhoneNum)) {
			try {
				FreeFlowStatusItem freeFlowStatusItem = mGameInfoHub.getFreeFlowStatus(devicePhoneNum);
				boolean remoteFreeFlowEnable = freeFlowStatusItem.getmIsEnableFreeFlow();
				boolean localFreeFlowEnable = !isFreeFlowDisable();
				if (remoteFreeFlowEnable != localFreeFlowEnable) {
					int result = enableFreeFlow(devicePhoneNum);
					if (SUCCESS_CODE == result) {
						Toast.makeText(mContext, mContext.getString(R.string.free_flow_auto_get_success), Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mContext, mContext.getString(R.string.free_flow_auto_get_failed), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
		} else {
			if (!PreferenceUtils.getFreeFlowNotSupportNum().equals(devicePhoneNum)) {//不支持的开通的号码不重复提示
				showBindPhoneUI(!TextUtils.isEmpty(accountPhoneNum));
			}
		}
	}

	/*
	 * 替换数据库中所有项的下载地址域名
	 */
	private void replaceDownloadUrlDomain(String domain) {
		List<AppEntity> allApp = GameData.getInstance(mContext).getAllGames();
		for (AppEntity app : allApp) {
			if (TextUtils.isEmpty(app.getDownloadPath())) {
				continue;
			}
			String newDownloadUrl = replaceDomain(app.getDownloadPath());
			if (TextUtils.isEmpty(newDownloadUrl)) {
				continue;
			}
			GameData.getInstance(mContext).updateFreeflowDownloadPath(app.getPkgName(),newDownloadUrl);
		}
	}

	/*
	 * 替换一条地址的域名，用于免流量替换域名
	 */
	public String replaceDomain(String url) {
		if (TextUtils.isEmpty(url)) {
			return new String();
		}
		if (url.contains(NEED_REPLACED_DOMAIN_1)) {
			return url.replace(NEED_REPLACED_DOMAIN_1, getDomain());
		}
		if (url.contains(NEED_REPLACED_DOMAIN_2)) {
			return url.replace(NEED_REPLACED_DOMAIN_2, getDomain());
		}
		return url;
	}

	private void replaceDownloadUrl() {
		List<AppEntity> allApp = GameData.getInstance(mContext).getAllGames();
		for (AppEntity app : allApp) {
			if (TextUtils.isEmpty(app.getDownloadPath())) {
				continue;
			}
			try {
				String freeflowDldPath = mGameInfoHub.getAgentDownloadUrl(app.getAppId());
				if (!TextUtils.isEmpty(freeflowDldPath)) {
					GameData.getInstance(mContext).updateFreeflowDownloadPath(app.getPkgName(), freeflowDldPath);
				}
			} catch (InfoSourceException e) {
				//暂不处理，后面下载时再重新获取
			}
		}
	}

//	private void resetDownloadUrl() {
//		List<AppEntity> allApp = GameData.getInstance(mContext).getAllGames();
//		for (AppEntity app : allApp) {
//			if (TextUtils.isEmpty(app.getDownloadPath())) {
//				continue;
//			}
//			String originUrl;
//			try {
//				originUrl = mGameInfoHub.obtainDownloadUrl(Long.parseLong(app.getAppId()));
//				if (TextUtils.isEmpty(originUrl)) {
//					GameData.getInstance(mContext).updateDownloadPath(app.getPkgName(),originUrl);
//				}
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//			} catch (InfoSourceException e) {
//				e.printStackTrace();
//			}
//		}
//	}


	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AccountDetailActivity.ACTION_ACCOUNT_LOGOUT.equals(action)) {
				postMsg(MSG_PROCESS_LOG_OUT, DELAY_PROCESS_LOG_OUT, null);
			} else if (BaseAccountActivity.ACTION_ACCOUNT_LOGIN.equals(action)) {
				postMsg(MSG_PROCESS_LOG_IN, DELAY_PROCESS_LOG_IN, null);
			}
		};
	};

	private void cachedFreeFlowMode(String mode) {
		synchronized (MODE_LOCK) {
			mCurFreeFlowMode = mode;
			PreferenceUtils.saveFreeFlowMode(mCurFreeFlowMode);
		}
	}

	private void cachedFreeFlowDomain(String domain) {
		synchronized (DOMAIN_LOCK) {
			mCurDomain = domain;
			PreferenceUtils.saveFreeFlowDomain(mCurDomain);
		}
	}




}
