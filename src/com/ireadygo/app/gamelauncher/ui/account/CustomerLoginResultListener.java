package com.ireadygo.app.gamelauncher.ui.account;

import android.content.Context;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask.AccountInfoListener;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;
import com.snailgame.mobilesdk.LoginResultListener;

public class CustomerLoginResultListener implements LoginResultListener {
	private Context mContext;
	private LoginResultListener mListener;

	public CustomerLoginResultListener(Context mContext, LoginResultListener listener) {
		this.mContext = mContext;
		this.mListener = listener;
	}

	@Override
	public void onFailure(int code) {
		if (mListener != null) {
			mListener.onFailure(code);
		}
	}

	@Override
	public void onSuccess() {
		// 获取账号信息
		new AccountInfoAsyncTask(mContext, new AccountInfoListener() {

			@Override
			public void onSuccess(UserInfoItem userInfo) {

			}

			@Override
			public void onFailed(int code) {

			}
		}).execute();

		GameLauncherThreadPool.getCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					GameInfoHub.instance(mContext).initSlotWithAccount();// 初始化卡槽数量
					int[] slotNum = GameInfoHub.instance(mContext).getUserSlotNum();// 获取卡槽数量
					if (slotNum != null && slotNum.length > 1) {
						PreferenceUtils.setSlotNum(slotNum[0]);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InfoSourceException e) {
					if (PreferenceUtils.getSlotNum() == 0) {
						PreferenceUtils.setSlotNum(GameLauncherConfig.DEFAULT_SLOT_NUM);// 初始化卡槽失败,设置一个默认的卡槽数
					}
				}
				//向服务器上传终端个推信息
				AccountManager.getInstance().uploadGetuiInfo(mContext);
			}
		});
		if (mListener != null) {
			mListener.onSuccess();
		}
	}
}
