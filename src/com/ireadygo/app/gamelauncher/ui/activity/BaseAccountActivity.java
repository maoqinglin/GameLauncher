package com.ireadygo.app.gamelauncher.ui.activity;

import android.R.integer;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.GameLauncherActivity;
import com.ireadygo.app.gamelauncher.ui.account.AccountLoginActivity;
import com.ireadygo.app.gamelauncher.ui.account.AccountRegisterActivity;
import com.ireadygo.app.gamelauncher.ui.account.CustomerLoginResultListener;
import com.ireadygo.app.gamelauncher.ui.guide.GuideAlipayActivity;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;
import com.ireadygo.app.gamelauncher.utils.Utils;
import com.snail.appstore.openapi.accountstatus.AccountStatusManager;
import com.snailgame.mobilesdk.LoginResultListener;
import com.snailgame.sdkcore.open.InitCompleteListener;

public class BaseAccountActivity extends BaseGuideActivity {
	public static final String START_FLAG = "startFlag";
	public static final int FLAG_START_BY_MAIN_ACTIVITY = 1;
	public static final int FLAG_START_BY_ACCOUNT_DETAIL = 2;
	public static final int SUCCESS_CODE = 1;
	public static final int FAILED_CODE = 0;
	public static final String TYPE_A = "A";
	public static final String TYPE_B = "B";
	public static final String TYPE_C = "C";
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
		sendAccountLoginBroadcast();
		Toast.makeText(this, R.string.account_login_success, Toast.LENGTH_SHORT).show();
		if (!PreferenceUtils.hasDeviceActive()) {
			DeviceActiveTask task = new DeviceActiveTask();
			task.execute();
			LoadOBoxTypeTask loadOBoxTypeTask = new LoadOBoxTypeTask();
			loadOBoxTypeTask.execute();
			return;
		}
		CheckBindAccountTask tast = new CheckBindAccountTask();
		tast.execute();
		startGameLauncherActivity();
	}

//	private void startActivityByFlag(int startFlag){
//		Intent intent = new Intent();
//		if (mStartFlag == FLAG_START_BY_MAIN_ACTIVITY) {
//			intent.setClass(this, GameLauncherActivity.class);
//		} else if (mStartFlag == FLAG_START_BY_ACCOUNT_DETAIL) {
////			intent.setClass(this, AccountDetailActivity.class);
////			intent.putExtra(AccountFragment.ACCOUNT_LAYOUT_FLAG, AccountFragment.LAYOUT_FLAG_MYWEALTH);
//		}
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
//		startActivity(intent);
//		finish();
//	}

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
	protected void startGameLauncherActivity() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		boolean isFirstLaunch = PreferenceUtils.isFirstLaunch();
		if (isFirstLaunch) {
			PreferenceUtils.setFirstLaunch(false);
			intent.setClass(this, GameLauncherActivity.class);
			//上报设备信息
			StaticsUtils.DeviceActive();
		} else {
			intent.setClass(this, GameLauncherActivity.class);
		}
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
		startActivity(intent);
		finish();
	}

	protected void startAlipayActivity() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(this, GuideAlipayActivity.class);
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
		startActivity(intent);
		finish();
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

	private class BindDeviceAccountTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				GameInfoHub.instance(BaseAccountActivity.this).bindAccount();
				return SUCCESS_CODE;
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return FAILED_CODE;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == SUCCESS_CODE) {
				Toast.makeText(BaseAccountActivity.this,getString(R.string.device_bind_account_success), Toast.LENGTH_SHORT).show();
				PreferenceUtils.setDeviceBindAccount(AccountManager.getInstance().getAccount(BaseAccountActivity.this));
			} else {
				Toast.makeText(BaseAccountActivity.this,getString(R.string.device_bind_account_failed), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class DeviceActiveTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				GameInfoHub.instance(BaseAccountActivity.this).activateBox(Build.SERIAL);
				return SUCCESS_CODE;
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return FAILED_CODE;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == SUCCESS_CODE) {
				Toast.makeText(BaseAccountActivity.this,getString(R.string.device_active_success), Toast.LENGTH_SHORT).show();
				PreferenceUtils.setDeviceActive(true);
				//激活成功，绑定帐号
				if (TextUtils.isEmpty(PreferenceUtils.getDeviceBindAccount())) {
					BindDeviceAccountTask task = new BindDeviceAccountTask();
					task.execute();
				}
				//激活成功，设置首次开机标志
				if (PreferenceUtils.isFirstLaunch()) {
					PreferenceUtils.setFirstLaunch(false);
				}
				//激活成功，跳转绑定支付宝
				startAlipayActivity();
			} else {
				Toast.makeText(BaseAccountActivity.this,getString(R.string.device_active_failed), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class CheckBindAccountTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				return GameInfoHub.instance(BaseAccountActivity.this).getSNCorrespondBindAccount(Build.SERIAL);
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				PreferenceUtils.setDeviceBindAccount(result);
			}
		}
	}

	private class LoadOBoxTypeTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				return GameInfoHub.instance(BaseAccountActivity.this).getSaleType(Build.SERIAL);
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
			
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				PreferenceUtils.saveOBoxType(result);
				if (TYPE_A.equals(result)) {
					GameLauncherConfig.sChannel = "1883";
				} else if (TYPE_B.equals(result)) {
					GameLauncherConfig.sChannel = "1884";
				} else {
					GameLauncherConfig.sChannel = "1882";
				}
				AccountManager.getInstance().init(BaseAccountActivity.this, new InitCompleteListener() {
					@Override
					public void onComplete(int arg0) {
						//上报设备信息
						StaticsUtils.DeviceActive();
					}
				});
			}
		}
	}
}
