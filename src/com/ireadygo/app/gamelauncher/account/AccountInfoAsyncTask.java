package com.ireadygo.app.gamelauncher.account;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;

public class AccountInfoAsyncTask extends AsyncTask<String, Void, UserInfoItem> {
	private Context mContext;
	private AccountInfoListener mAccountInfoListener;

	public AccountInfoAsyncTask(Context context, AccountInfoListener listener) {
		this.mContext = context;
		this.mAccountInfoListener = listener;
	}

	@Override
	protected UserInfoItem doInBackground(String... params) {
		UserInfoItem userInfo = null;
		try {
			userInfo = GameInfoHub.instance(mContext).getUserInfo();
			PreferenceUtils.setBSSAccount(false);
			String phone = userInfo.getCPhone();
			if (!TextUtils.isEmpty(phone)) {
				PreferenceUtils.saveFreeFlowBindPhoneNum(phone);
				boolean isBSSAccount = GameInfoHub.instance(mContext).checkBSSAccount(phone);
				PreferenceUtils.setBSSAccount(isBSSAccount);
			}
		} catch (InfoSourceException e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	@Override
	protected void onPostExecute(UserInfoItem result) {
		super.onPostExecute(result);
		if (result == null) {
			if (mAccountInfoListener != null) {
				mAccountInfoListener.onFailed(StatusCode.LOGIN_FAILED);
			}
		} else {
			GameLauncherApplication.getApplication().setUserInfoItem(result);
			if (mAccountInfoListener != null) {
				mAccountInfoListener.onSuccess(result);
			}
		}
	}

	public interface AccountInfoListener {
		public void onSuccess(UserInfoItem userInfo);

		public void onFailed(int code);
	}
}
