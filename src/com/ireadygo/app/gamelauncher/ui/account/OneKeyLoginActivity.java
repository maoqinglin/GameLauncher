package com.ireadygo.app.gamelauncher.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.GameLauncherActivity;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.activity.BaseAccountActivity;

public class OneKeyLoginActivity extends BaseAccountActivity implements OnFocusChangeListener{
	private static final float SCALE = 0.85f;
	private TextView mOneKeyLoginBtn;
	private TextView mAccountLoginBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.onekey_login);
		initView();
	}

	private void initView() {
		initHeaderView(R.string.account_onekey_goback_btn);
		mOneKeyLoginBtn = (TextView) findViewById(R.id.onekeyLogin);
		mOneKeyLoginBtn.setOnClickListener(this);
		mOneKeyLoginBtn.setOnFocusChangeListener(this);
		mOneKeyLoginBtn.setScaleX(SCALE);
		mOneKeyLoginBtn.setScaleY(SCALE);
		
		mAccountLoginBtn = (TextView) findViewById(R.id.accountLogin);
		mAccountLoginBtn.setOnClickListener(this);
		mAccountLoginBtn.setOnFocusChangeListener(this);
		mAccountLoginBtn.setScaleX(SCALE);
		mAccountLoginBtn.setScaleY(SCALE);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.onekeyLogin:
			oneKeyLogin();
			break;
		case R.id.accountLogin:
			startLoginActivity();
			break;

		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			v.setScaleX(1.0f);
			v.setScaleY(1.0f);
		}else{
			float scale = SCALE;
			v.setScaleX(scale);
			v.setScaleY(scale);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case SnailKeyCode.SUN_KEY:
			if (mOneKeyLoginBtn.hasFocus()) {
				onClick(mOneKeyLoginBtn);
			} else if (mAccountLoginBtn.hasFocus()) {
				onClick(mAccountLoginBtn);
			}
			return true;
		case SnailKeyCode.MOON_KEY:
		case SnailKeyCode.BACK_KEY:
			if (BaseAccountActivity.FLAG_START_BY_ACCOUNT_DETAIL == mStartFlag) {
				Intent intent = new Intent(OneKeyLoginActivity.this,GameLauncherActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
				startActivity(intent);
			}
			finish();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
