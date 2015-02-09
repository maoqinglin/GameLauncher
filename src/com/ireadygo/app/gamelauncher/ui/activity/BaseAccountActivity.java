package com.ireadygo.app.gamelauncher.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.GameLauncherActivity;
import com.ireadygo.app.gamelauncher.ui.account.AccountDetailActivity;
import com.ireadygo.app.gamelauncher.ui.account.AccountFragment;
import com.ireadygo.app.gamelauncher.ui.account.AccountLoginActivity;
import com.ireadygo.app.gamelauncher.ui.account.AccountRegisterActivity;
import com.ireadygo.app.gamelauncher.ui.account.CustomerLoginResultListener;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;
import com.ireadygo.app.gamelauncher.utils.Utils;
import com.snail.appstore.openapi.accountstatus.AccountStatusManager;
import com.snailgame.mobilesdk.LoginResultListener;

public class BaseAccountActivity extends BaseGuideActivity {
	public static final String START_FLAG = "startFlag";
	public static final int FLAG_START_BY_MAIN_ACTIVITY = 1;
	public static final int FLAG_START_BY_ACCOUNT_DETAIL = 2;
	public static final String ACTION_ACCOUNT_LOGIN = "com.ireadygo.app.gamelauncher.ACTION_ACCOUNT_LOGIN";
	private boolean mIsResumed = false;
	private Dialog mProgressDialog;
	protected int mStartFlag = FLAG_START_BY_MAIN_ACTIVITY;
	private long mLoginStartTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStartFlag = getIntent().getIntExtra(START_FLAG, FLAG_START_BY_MAIN_ACTIVITY);
	}

	protected void showProgressDialog(int msgId) {
		if (mProgressDialog == null) {
			mProgressDialog = Utils.createLoadingDialog(this);
			String msg = getString(msgId);
		}
		mProgressDialog.show();
	}

	protected void hideProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	protected void oneKeyLogin() {
		mLoginStartTime = System.currentTimeMillis();
		showProgressDialog(R.string.account_one_key_logining_prompt);
		AccountManager.getInstance().oneKeyLogin(this, new CustomerLoginResultListener(this, mListener));
	}

	protected void generalLogin(String account, String password) {
		mLoginStartTime = System.currentTimeMillis();
		showProgressDialog(R.string.account_logining_prompt);
		AccountManager.getInstance().generalLogin(this, account, password,
				new CustomerLoginResultListener(this, mListener));
	}

	protected void generalRegister(String account, String password) {
		showProgressDialog(R.string.account_registing_prompt);
		AccountManager.getInstance().generalRegister(this, account, password,
				new CustomerLoginResultListener(this, mListener));
	}

	protected void onLoginSuccess() {
		hideProgressDialog();
		startActivityByFlag(mStartFlag);
		sendAccountLoginBroadcast();
		Toast.makeText(this, R.string.account_login_success, Toast.LENGTH_SHORT).show();
	}

	private void startActivityByFlag(int startFlag){
		Intent intent = new Intent();
		if (mStartFlag == FLAG_START_BY_MAIN_ACTIVITY) {
			intent.setClass(this, GameLauncherActivity.class);
		} else if (mStartFlag == FLAG_START_BY_ACCOUNT_DETAIL) {
			intent.setClass(this, AccountDetailActivity.class);
			intent.putExtra(AccountFragment.ACCOUNT_LAYOUT_FLAG, AccountFragment.LAYOUT_FLAG_MYWEALTH);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
		startActivity(intent);
		finish();
	}
	
	protected void onLoginFailed(int code) {
		hideProgressDialog();
	}

	private LoginResultListener mListener = new LoginResultListener() {

		@Override
		public void onSuccess() {
			if(mIsResumed){
				onLoginSuccess();
			}
			AccountStatusManager.getInstance().setLoginData(AccountManager.getInstance().getLoginUni(getApplicationContext()),
					AccountManager.getInstance().getSessionId(getApplicationContext()));
			//上报登录成功事件，及登录花费的时间
			long loginSuccessUsedTime = System.currentTimeMillis() - mLoginStartTime;
			if (loginSuccessUsedTime < 0) {
				loginSuccessUsedTime = 0;
			}
			StaticsUtils.onLogin(loginSuccessUsedTime, true);
		}

		@Override
		public void onFailure(int code) {
			if (mIsResumed) {
				onLoginFailed(code);
			}
			AccountStatusManager.getInstance().clearLoginData();
			//上报登录失败事件，及登录花费的时间
			long loginFailedUsedTime = System.currentTimeMillis() - mLoginStartTime;
			if (loginFailedUsedTime < 0) {
				loginFailedUsedTime = 0;
			}
			StaticsUtils.onLogin(loginFailedUsedTime, false);
		}
	};

	@Override
	protected void onDestroy() {
		mProgressDialog = null;
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mIsResumed = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mIsResumed = false;
	}

	@Override
	public void finish() {
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
		super.finish();
	}
	protected void startGameLauncherActivity(boolean isCancledLogin) {
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		boolean isFirstLaunch = PreferenceUtils.isFirstLaunch();
//		if (isFirstLaunch) {
//			PreferenceUtils.setFirstLaunch(false);
//			intent.setClass(this, AccountTicketRechargeActivity.class);
//			//上报设备信息
//			StaticsUtils.DeviceActive();
//		} else {
//			intent.setClass(this, GameLauncherActivity.class);
//			Bundle extras = new Bundle();
//			FragmentAnchor.setBundleArgs(extras, FragmentAnchor.TAB_ACCOUNT, FragmentAnchor.ACCOUNT_PERSONAL, true);
//			intent.putExtras(extras);
//		}
//		startActivity(intent);
//		finish();
	}

	protected void startLoginActivity() {
		Intent intent = new Intent(this, AccountLoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(START_FLAG, mStartFlag);
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_ENTER);
		startActivity(intent);
	}

	protected void startEntryActivity() {
		// Intent intent = new Intent(this, GameEntryActivity.class);
		// startActivity(intent);
		// finish();
	}

	protected void startRegisterActivity() {
		Intent intent = new Intent(this, AccountRegisterActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(START_FLAG, mStartFlag);
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_ENTER);
		startActivity(intent);
	}

	protected void startOneKeyLoginActivity() {
		// Intent intent = new Intent(this, OneKeyLoginActivity.class);
		// startActivity(intent);
		// finish();
	}

	private void sendAccountLoginBroadcast() {
		Intent intent = new Intent(ACTION_ACCOUNT_LOGIN);
		sendBroadcast(intent);
	}
}
