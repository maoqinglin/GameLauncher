package com.ireadygo.app.gamelauncher.aidl.keyadapter;

import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

public class KeyAdapterRemoteServiceImpl extends IKeyAdapterAidlService.Stub {

	private static final int MSG_NICKNAME_RESULT = 1;
	private static final String DIVIDER = ",";
	private static final String INNER_DIVIDER = "-";

	private final RemoteCallbackList<IKeyAdapterAidlCallback> mCallbackList = new RemoteCallbackList<IKeyAdapterAidlCallback>();
	private Context mContext;

	public KeyAdapterRemoteServiceImpl(Context context) {
		mContext = context;
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
	public void registerCallback(IKeyAdapterAidlCallback callback) throws RemoteException {
		if (callback != null) {
			synchronized (mCallbackList) {
				mCallbackList.register(callback);
			}
		}
	}

	@Override
	public void unregisterCallback(IKeyAdapterAidlCallback callback) throws RemoteException {
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
	}

}
