package com.ireadygo.app.gamelauncher.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.account.AccountLoginActivity;
import com.ireadygo.app.gamelauncher.ui.account.AccountRegisterActivity;
import com.ireadygo.app.gamelauncher.ui.activity.BaseAccountActivity;
import com.ireadygo.app.gamelauncher.ui.widget.GuideTwoBtnLayout;
import com.ireadygo.app.gamelauncher.ui.widget.GuideTwoBtnLayout.OnLRBtnClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class GuideRegisterOrLoginActivity extends BaseAccountActivity {

	private GuideTwoBtnLayout mGuideTwoBtnLayout;
	private OperationTipsLayout mTipsLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starting_guide_login_register_layout);
		initUI();
	}


	private void initUI() {
		initHeaderView(R.string.starting_guide_account_login_title);
		getWifiSettingsBtn().setVisibility(View.VISIBLE);
		mTipsLayout = (OperationTipsLayout) findViewById(R.id.operationTipsLayout);
		mTipsLayout.setTipsVisible(View.GONE, TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);

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

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode == KeyEvent.KEYCODE_BACK) {
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	@Override
	public boolean onMoonKey() {
		return true;
	}

}
