package com.ireadygo.app.gamelauncher.ui.guide;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.ui.account.AccountLoginActivity;
import com.ireadygo.app.gamelauncher.ui.account.AccountRegisterActivity;
import com.ireadygo.app.gamelauncher.ui.activity.BaseGuideActivity;
import com.ireadygo.app.gamelauncher.ui.widget.GuideTwoBtnLayout;
import com.ireadygo.app.gamelauncher.ui.widget.GuideTwoBtnLayout.OnLRBtnClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;

public class GuideRegisterOrLoginActivity extends BaseGuideActivity {

	private static final int SUCCESS_CODE = 1;
	private static final int FAILED_CODE = 0;
	private GuideTwoBtnLayout mGuideTwoBtnLayout;
	private OperationTipsLayout mTipsLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starting_guide_login_register_layout);
		initUI();
		if (!PreferenceUtils.hasDeviceActive()) {
			DeviceActiveTask task = new DeviceActiveTask();
			task.execute();
		}
	}


	private void initUI() {
		initHeaderView(R.string.starting_guide_account_login_title);
		mTipsLayout = (OperationTipsLayout) findViewById(R.id.operationTipsLayout);
		mTipsLayout.setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mTipsLayout.getPagingIndicator().setVisibility(View.GONE);

		mGuideTwoBtnLayout = (GuideTwoBtnLayout) findViewById(R.id.guideTwoBtnLayout);
		mGuideTwoBtnLayout.setLeftBtnText(R.string.starting_guide_login);
		mGuideTwoBtnLayout.setRightBtnText(R.string.starting_guide_register);
		mGuideTwoBtnLayout.setOnLRBtnClickListener(new OnLRBtnClickListener() {

			@Override
			public void onRightBtnClickListener(View view) {
				Intent intent = new Intent(GuideRegisterOrLoginActivity.this, AccountRegisterActivity.class);
				startActivity(intent);
			}
			
			@Override
			public void onLeftBtnClickListener(View view) {
				Intent intent = new Intent(GuideRegisterOrLoginActivity.this, AccountLoginActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onMoonKey() {
		return true;
	}

	private class DeviceActiveTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			try {
				GameInfoHub.instance(GuideRegisterOrLoginActivity.this).activateBox(Build.SERIAL);
				return SUCCESS_CODE;
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return FAILED_CODE;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == SUCCESS_CODE) {
				Toast.makeText(GuideRegisterOrLoginActivity.this,getString(R.string.device_active_success), Toast.LENGTH_SHORT).show();
				PreferenceUtils.setDeviceActive(true);
			} else {
				Toast.makeText(GuideRegisterOrLoginActivity.this,getString(R.string.device_active_failed), Toast.LENGTH_SHORT).show();
			}
		}
	}

}
