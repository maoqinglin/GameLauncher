package com.ireadygo.app.gamelauncher.aidl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.ui.account.AccountDetailActivity;
import com.ireadygo.app.gamelauncher.ui.activity.BaseAccountActivity;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

public class GamelauncherRemoteServiceImpl extends IGamelauncherAidlService.Stub {

	private static final int MSG_NICKNAME_RESULT = 1;
	private static final int MSG_LOGIN_ACCOUNT_CHANGE = 2;
	private static final String DIVIDER = ",";
	private static final String INNER_DIVIDER = "-";

	private final RemoteCallbackList<IGameLauncherAidlCallback> mCallbackList = new RemoteCallbackList<IGameLauncherAidlCallback>();
	private Context mContext;

	public GamelauncherRemoteServiceImpl(Context context) {
		mContext = context;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BaseAccountActivity.ACTION_ACCOUNT_LOGIN);
		intentFilter.addAction(AccountDetailActivity.ACTION_ACCOUNT_LOGOUT);
		mContext.registerReceiver(mReceiver, intentFilter);
	}

	//查询当前登录的账户UID
	@Override
	public String getLoginAccount() throws RemoteException {
		return AccountManager.getInstance().getLoginUni(mContext);
	}

	//查询当前登录账户的昵称
	@Override
	public String getLoginNickname() throws RemoteException {
		return AccountManager.getInstance().getNickName(mContext);
	}

	//输入帐号UID，查询对应的昵称。
	@Override
	public void queryNicknames(final String accounts) throws RemoteException {
		if (TextUtils.isEmpty(accounts)) {
			return;
		}
		GameLauncherThreadPool.getCachedThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				String [] account = accounts.split(DIVIDER);
				StringBuffer nickNames = new StringBuffer();
				for (String accountItem : account) {
					try {
						nickNames.append(accountItem).append(INNER_DIVIDER);
						nickNames.append(GameInfoHub.instance(mContext).getUserNickName(accountItem));
						nickNames.append(DIVIDER);
					} catch (InfoSourceException e) {
						e.printStackTrace();
						//远程请求出错，仍需要增加分割符
						nickNames.append(DIVIDER);
					}
				}
				String nickNameResult = nickNames.toString();
				if (!TextUtils.isEmpty(nickNameResult)) {
					callBack(MSG_NICKNAME_RESULT, nickNameResult);
				}
			}
		});
	}

	@Override
	public void registerCallback(IGameLauncherAidlCallback callback) throws RemoteException {
		if (callback != null) {
			synchronized (mCallbackList) {
				mCallbackList.register(callback);
			}
		}
	}

	@Override
	public void unregisterCallback(IGameLauncherAidlCallback callback) throws RemoteException {
		if (callback != null) {
			synchronized (mCallbackList) {
				mCallbackList.unregister(callback);
			}
		}
	}

	private void callBack(int cmd, String param) {
		int N = mCallbackList.beginBroadcast();
		try {
			for (int i = 0; i < N; i++) {
				mCallbackList.getBroadcastItem(i).handlerCommEvent(cmd, param);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		mCallbackList.finishBroadcast();
	}

	public void onDestroy() {
		mCallbackList.kill();
		mContext.unregisterReceiver(mReceiver);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BaseAccountActivity.ACTION_ACCOUNT_LOGIN.equals(action)) {
				callBack(MSG_LOGIN_ACCOUNT_CHANGE,AccountManager.getInstance().getLoginUni(mContext));
			} else if (AccountDetailActivity.ACTION_ACCOUNT_LOGOUT.equals(action)) {
				callBack(MSG_LOGIN_ACCOUNT_CHANGE, "");
			}
		}
	};

}
