package com.ireadygo.app.gamelauncher.ui.account;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.activity.BaseAccountActivity;

public class AccountLoginActivity extends BaseAccountActivity {
	private EditText mUsernameView;
	private EditText mPasswordView;
	private TextView mLoginBtn;
	private TextView mRegisterBtn;
	private TextView mErrorPromptView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_login);
		initView();
	}

	private void initView() {
		initHeaderView(R.string.account_login_btn);
		mErrorPromptView = (TextView) findViewById(R.id.loginErrorPrompt);

		mUsernameView = (EditText) findViewById(R.id.username);
		mPasswordView = (EditText) findViewById(R.id.password);

		mLoginBtn = (TextView) findViewById(R.id.loginBtn);
		mLoginBtn.setOnClickListener(this);

		mRegisterBtn = (TextView) findViewById(R.id.registerBtn);
		mRegisterBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.loginBtn:
			String account = mUsernameView.getEditableText().toString();
			String password = mPasswordView.getEditableText().toString();
			if (TextUtils.isEmpty(account)) {
				mErrorPromptView.setText(R.string.account_login_username_empty_prompt);
				return;
			}
			if (TextUtils.isEmpty(password)) {
				mErrorPromptView.setText(R.string.account_login_password_empty_prompt);
				return;
			}
			generalLogin(account, password);
			break;
		case R.id.registerBtn:
			startRegisterActivity();
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onLoginSuccess() {
		super.onLoginSuccess();
	}

	@Override
	public boolean onSunKey() {
		if (mLoginBtn.hasFocus()) {
			onClick(mLoginBtn);
		} else if (mRegisterBtn.hasFocus()) {
			onClick(mRegisterBtn);
		}
		return super.onSunKey();
	}
	
	@Override
	public boolean onBackKey() {
		return super.onBackKey();
	}

	@Override
	public boolean onMoonKey() {
		return onBackKey();
	}
}
