package com.ireadygo.app.gamelauncher.ui.account;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.manager.FreeFlowManager;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.activity.BaseAccountActivity;
import com.ireadygo.app.gamelauncher.ui.widget.CustomerEditText;
import com.ireadygo.app.gamelauncher.utils.DeviceUtil;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.widget.SMSObserver;

public class FreeFlowRechargeActivity extends BaseAccountActivity implements OnClickListener {

	private static final String TAG = "FreeFlowRechargeActivity";
	private static final long COUNT_DOWN_TIME = 60 * 1000;//1minutes
	private static final long COUNT_DOWN_INTERVAL = 1000;//1second

	private CustomerEditText mPhoneEditTxt;
	private CustomerEditText mSmsCodeEditTxt;

	private TextView mGetSmsCodeBtn;
	private TextView mUnbindPhoneBtn;
	private TextView mPhoneBindBtn;
	private TextView mPrivilegeBtn;

	private TextView mPhoneNumBindTip;
	private TextView mPrivilegeTip;

	private RelativeLayout mGetSmsCodeLayout;
	private FrameLayout mPrivilegeStatusLayout;
	private LinearLayout mPrivilegeGetLayout;

	private FreeFlowManager mFreeFlowManager;
	private CountTimer mCountTimer = new CountTimer(COUNT_DOWN_TIME, COUNT_DOWN_INTERVAL);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.free_flow_recharge_activity);
		initHeaderView(R.string.free_flow_privilege_title);
		mFreeFlowManager = GameLauncher.instance().getGameManager().getFreeFlowManager();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SMSObserver.ACTION_RECEIVE_SNAIL_SMS);
		registerReceiver(mReceiver, intentFilter);
		initUI();
		initView();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		mCountTimer.cancel();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (!checkNetworkConnect()) {
			return;
		}
		switch (v.getId()) {
		case R.id.get_sms_code_btn:
			String phoneNum = mPhoneEditTxt.getText().toString();
			if (!checkPhoneNum(phoneNum)) {
				return;
			}
			GetSmsCodeTask getSmsCodeTask = new GetSmsCodeTask();
			getSmsCodeTask.execute(phoneNum);
			break;
		case R.id.unbind_phone_btn:
			String unBindPhoneNum = mPhoneEditTxt.getText().toString();
			if (!checkPhoneNum(unBindPhoneNum)) {
				return;
			}
			UnbindPhoneNumTask unbindPhoneNumTask = new UnbindPhoneNumTask();
			unbindPhoneNumTask.execute(unBindPhoneNum);
			break;
		case R.id.phone_bind_btn:
			String bindPhoneNum = mPhoneEditTxt.getText().toString();
			String smsCode = mSmsCodeEditTxt.getText().toString();
			if (!checkPhoneNum(bindPhoneNum) || !checkSmsCode(smsCode)) {
				return;
			}
			BindPhoneNumTask bindPhoneNumTask = new BindPhoneNumTask();
			bindPhoneNumTask.execute(bindPhoneNum,smsCode);
			break;
		case R.id.privilege_btn:
			String bindedPhoneNum = mPhoneEditTxt.getText().toString();
			if (!checkPhoneNum(bindedPhoneNum)) {
				return;
			}
			EnableFreeFlowTask enableFreeFlowTask = new EnableFreeFlowTask();
			enableFreeFlowTask.execute(bindedPhoneNum);
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == SnailKeyCode.SUN_KEY) {
			if (mGetSmsCodeBtn.hasFocus()) {
				onClick(mGetSmsCodeBtn);
			}else if(mPhoneBindBtn.hasFocus()){
				onClick(mPhoneBindBtn);
			} else if (mUnbindPhoneBtn.hasFocus()) {
				onClick(mUnbindPhoneBtn);
			} else if (mPrivilegeBtn.hasFocus()) {
				onClick(mPrivilegeBtn);
			}
		}else if(keyCode == SnailKeyCode.BACK_KEY || keyCode == SnailKeyCode.MOON_KEY){
			onGobackClick();
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean checkPhoneNum(String phoneNum) {
		if (TextUtils.isEmpty(phoneNum)) {//电话号码为空
			Toast.makeText(getApplicationContext(), getString(R.string.free_flow_phone_empty), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (phoneNum.length() < 11) {//电话号码不正确
			Toast.makeText(getApplicationContext(), getString(R.string.free_flow_phone_format_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private boolean checkSmsCode(String smsCode) {
		if (TextUtils.isEmpty(smsCode)) {//电话号码为空
			Toast.makeText(getApplicationContext(), getString(R.string.free_flow_sms_code_empty), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private boolean checkNetworkConnect() {
		if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void initUI() {
		mPhoneEditTxt = (CustomerEditText) findViewById(R.id.phone_number);
		mSmsCodeEditTxt = (CustomerEditText) findViewById(R.id.auto_code);
		mGetSmsCodeBtn = (TextView) findViewById(R.id.get_sms_code_btn);
		mPhoneBindBtn = (TextView) findViewById(R.id.phone_bind_btn);
		mUnbindPhoneBtn = (TextView)findViewById(R.id.unbind_phone_btn);
		mPrivilegeBtn = (TextView) findViewById(R.id.privilege_btn);
		mPhoneNumBindTip = (TextView) findViewById(R.id.bind_phone_tip);
		mPrivilegeTip = (TextView) findViewById(R.id.privilege_tip);
		mGetSmsCodeLayout = (RelativeLayout) findViewById(R.id.layout_auto_code);
		mPrivilegeGetLayout = (LinearLayout) findViewById(R.id.layout_privilege_get);
		mPrivilegeStatusLayout = (FrameLayout) findViewById(R.id.layout_privilege_status);
		mGetSmsCodeBtn.setOnClickListener(this);
		mPhoneBindBtn.setOnClickListener(this);
		mPrivilegeBtn.setOnClickListener(this);
		mUnbindPhoneBtn.setOnClickListener(this);
	}

	private void initView() {
		String bindPhoneNum = PreferenceUtils.getFreeFlowBindPhoneNum();
		String unSupportPhone = PreferenceUtils.getFreeFlowNotSupportNum();
		if (TextUtils.isEmpty(bindPhoneNum)) {
			mGetSmsCodeBtn.setVisibility(View.VISIBLE);
			mUnbindPhoneBtn.setVisibility(View.GONE);
			mPhoneEditTxt.setText(DeviceUtil.getDevicePhoneNum(getApplicationContext()));
			mPhoneEditTxt.setEnabled(true);
			mPhoneEditTxt.setClickable(true);
			mPhoneEditTxt.setFocusable(true);
			mGetSmsCodeLayout.setVisibility(View.VISIBLE);
			mPrivilegeStatusLayout.setVisibility(View.GONE);
			mPhoneNumBindTip.setVisibility(View.GONE);
		} else {
			mGetSmsCodeBtn.setVisibility(View.GONE);
			mUnbindPhoneBtn.setVisibility(View.VISIBLE);
			mPhoneEditTxt.setText(bindPhoneNum);
			mPhoneEditTxt.setEnabled(false);
			mPhoneEditTxt.setFocusable(false);
			mGetSmsCodeLayout.setVisibility(View.GONE);
			mPrivilegeStatusLayout.setVisibility(View.VISIBLE);
			mPhoneNumBindTip.setVisibility(View.VISIBLE);
			if (bindPhoneNum.equals(unSupportPhone)) {
				mPrivilegeGetLayout.setVisibility(View.GONE);
				mPrivilegeTip.setVisibility(View.VISIBLE);
				mPrivilegeBtn.setFocusable(false);
			} else if (!mFreeFlowManager.isFreeFlowDisable()) {
				mPrivilegeGetLayout.setVisibility(View.VISIBLE);
				mPrivilegeTip.setVisibility(View.GONE);
				mPrivilegeBtn.setText(R.string.free_flow_has_get);
				mPrivilegeBtn.setEnabled(false);
				mPrivilegeBtn.setFocusable(false);
			} else {
				mPrivilegeGetLayout.setVisibility(View.VISIBLE);
				mPrivilegeTip.setVisibility(View.GONE);
				mPrivilegeBtn.setText(R.string.free_flow_get);
				mPrivilegeBtn.setEnabled(true);
				mPrivilegeBtn.setFocusable(true);
			}
		}
	}

	private class GetSmsCodeTask extends AsyncTask<String, Void, Boolean> {
		private String errMsg = null;

		@Override
		protected void onPreExecute() {
			showProgressDialog(R.string.free_flow_sms_getting);
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				GameInfoHub.instance(FreeFlowRechargeActivity.this).getSmsCode(params[0]);
				mCountTimer.start();
				return true;
			} catch (InfoSourceException e) {
				errMsg = e.getMessage();
				Log.e(TAG, "Get SMS Code error:" + e.getMessage());
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			hideProgressDialog();
			if (!result) {
				if (InfoSourceException.MSG_FREE_FLOW_SMS_USE_OUT.equals(errMsg)) {
					Toast.makeText(getApplicationContext(), getString(R.string.free_flow_sms_use_out), Toast.LENGTH_SHORT).show();
				} else if (InfoSourceException.MSG_ACCOUNT_OUTDATE.equals(errMsg)) {
					Toast.makeText(getApplicationContext(),getString(R.string.account_outdate), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.free_flow_get_sms_failed), Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	private class BindPhoneNumTask extends AsyncTask<String, Void, Boolean> {
		private String errMsg;
		@Override
		protected void onPreExecute() {
			showProgressDialog(R.string.free_flow_phone_binding);
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				GameInfoHub.instance(FreeFlowRechargeActivity.this).bindFreeFlowPhoneNum(params[0], params[1]);
				return true;
			}catch (InfoSourceException e) {
				errMsg = e.getMessage();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			hideProgressDialog();
			if (isFinishing() || isDestroyed()) {
				return;
			}
			if (result) {
				initView();
				Toast.makeText(getApplicationContext(),getString(R.string.free_flow_bind_phone_success), Toast.LENGTH_SHORT).show();
			} else {
				if (InfoSourceException.MSG_SMS_CODE_ERROR.equals(errMsg)) {
					Toast.makeText(getApplicationContext(),getString(R.string.free_flow_sms_code_not_match), Toast.LENGTH_SHORT).show();
				} else if (InfoSourceException.MSG_ACCOUNT_OUTDATE.equals(errMsg)) {
					Toast.makeText(getApplicationContext(),getString(R.string.account_outdate), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(),getString(R.string.free_flow_bind_phone_failed), Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	private class UnbindPhoneNumTask extends AsyncTask<String, Void, Boolean> {
		String errMsg;
		@Override
		protected void onPreExecute() {
			showProgressDialog(R.string.free_flow_phone_unbinding);
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				GameInfoHub.instance(FreeFlowRechargeActivity.this).unbindFreeFlowPhoneNum(params[0]);
				mFreeFlowManager.disableFreeFlow();
				return true;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InfoSourceException e) {
				errMsg = e.getMessage();
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			hideProgressDialog();
			if (result) {
				initView();
				Toast.makeText(getApplicationContext(),getString(R.string.free_flow_unbind_phone_success), Toast.LENGTH_SHORT).show();
			} else if (InfoSourceException.MSG_ACCOUNT_OUTDATE.equals(errMsg)) {
				Toast.makeText(getApplicationContext(),getString(R.string.account_outdate), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),getString(R.string.free_flow_unbind_phone_failed), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class EnableFreeFlowTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected void onPreExecute() {
			showProgressDialog(R.string.free_flow_enabling);
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			return mFreeFlowManager.enableFreeFlow(params[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			hideProgressDialog();
			initView();
			if (FreeFlowManager.SUCCESS_CODE == result) {
				Toast.makeText(getApplicationContext(),getString(R.string.free_flow_get_success), Toast.LENGTH_SHORT).show();
				finish();
			} else if (FreeFlowManager.ERR_CODE_FREE_FLOW_NOT_SUPPORT == result) {
				Toast.makeText(getApplicationContext(),getString(R.string.free_flow_get_tip), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),getString(R.string.free_flow_enable_failed), Toast.LENGTH_SHORT).show();
			}
		}

	}


	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (SMSObserver.ACTION_RECEIVE_SNAIL_SMS.equals(action)) {
				String validCode = intent.getStringExtra(SMSObserver.KEY_SMS_CONTENT);
				if (isFinishing() || isDestroyed()) {
					return;
				}
				mSmsCodeEditTxt.setText(validCode);
			}
		}
	};

	private class CountTimer extends CountDownTimer {

		public CountTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mGetSmsCodeBtn.setEnabled(true);
			mGetSmsCodeBtn.setBackgroundResource(R.drawable.free_flow_get_sms_selector);
			mGetSmsCodeBtn.setText(R.string.free_flow_bind_phone_status);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			String result = String.format(getString(R.string.free_flow_reget_sms_code), (millisUntilFinished / 1000));
			mGetSmsCodeBtn.setText(result);
			mGetSmsCodeBtn.setEnabled(false);
//			mGetSmsCodeBtn.setBackgroundResource(R.drawable.free_flow_auth_code_disable);
		}
	}

}
