package com.ireadygo.app.gamelauncher.ui.account;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.activity.BaseAccountActivity;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class AccountRegisterActivity extends BaseAccountActivity {
	private TextView mErrorPromptView;
	private EditText mUsernameView;
	private EditText mPasswordView;
	private EditText mPasswordRepeatView;
	private TextView mRegisterBtn;
	private OperationTipsLayout mTipsLayout;
	private ImageButton mPasswordSee;
	private ImageButton mPasswordSeeRepeat;
	private boolean mPasswordSeeState;
	private boolean mPasswordSeeStateRepeat;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_register);
		initView();
	}
	
	private void initView(){
		initHeaderView(R.string.account_register_btn);
		mTipsLayout = (OperationTipsLayout) findViewById(R.id.operationTipsLayout);
		mTipsLayout.setTipsVisible(View.GONE, TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);

		mErrorPromptView = (TextView)findViewById(R.id.errorPrompt);
		mUsernameView = (EditText)findViewById(R.id.username);
		mPasswordView = (EditText)findViewById(R.id.password);
		mPasswordRepeatView = (EditText)findViewById(R.id.passwordRepeat);
		
		mRegisterBtn = (TextView)findViewById(R.id.registerBtn);
		mRegisterBtn.setOnClickListener(this);
		
		mPasswordSee = (ImageButton) findViewById(R.id.see_password);
		mPasswordSeeRepeat = (ImageButton) findViewById(R.id.see_password_repeat);
		mPasswordSeeState = false;
		mPasswordSeeStateRepeat = false;
		
		mPasswordSee.setOnClickListener(this);
		mPasswordSeeRepeat.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.registerBtn:
			String username = mUsernameView.getEditableText().toString();
			String password = mPasswordView.getEditableText().toString();
			String passwordRepeat = mPasswordRepeatView.getEditableText().toString();
			if(checkUsernameAndPassword(username, password, passwordRepeat)){
				generalRegister(username, password);
			}
			break;
		case R.id.see_password :
			mPasswordSeeState = !mPasswordSeeState;
			if(mPasswordSeeState) {
			mPasswordSee.setBackground(getResources().getDrawable(R.drawable.password_see_selector));
			}
			else {
			mPasswordSee.setBackground(getResources().getDrawable(R.drawable.password_nosee_selector));
			}
			int pos = mPasswordView.getSelectionEnd();
			mPasswordView
					.setInputType(InputType.TYPE_CLASS_TEXT
							| (mPasswordSeeState ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
									: InputType.TYPE_TEXT_VARIATION_PASSWORD));
			if (pos >= 0) {
				((EditText) mPasswordView).setSelection(pos);
			}
			break;
		case R.id.see_password_repeat :
			mPasswordSeeStateRepeat = !mPasswordSeeStateRepeat;
			if(mPasswordSeeStateRepeat) {
			mPasswordSeeRepeat.setBackground(getResources().getDrawable(R.drawable.password_see_selector));
			}
			else {
			mPasswordSeeRepeat.setBackground(getResources().getDrawable(R.drawable.password_nosee_selector));
			}
			int posRepeat = mPasswordRepeatView.getSelectionEnd();
			mPasswordRepeatView
					.setInputType(InputType.TYPE_CLASS_TEXT
							| (mPasswordSeeStateRepeat ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
									: InputType.TYPE_TEXT_VARIATION_PASSWORD));
			if (posRepeat >= 0) {
				((EditText) mPasswordRepeatView).setSelection(posRepeat);
			}
			break;

		default:
			break;
		}
	}
	
	private boolean checkUsernameAndPassword(String username,String password,String passwordRepeat){
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordRepeat)) {
			mErrorPromptView.setText(R.string.account_username_empty_prmopt);
			return false;
		}
		// if(Utils.isEmail(account)){
		// accountRegisterPrompt.setText(R.string.account_email_error);
		// return;
		// }
		if (username.length() < 6 || username.length() > 25) {
			mErrorPromptView.setText(R.string.account_username_length_error);
			return false;
		}
		if (password.length() < 8) {
			mErrorPromptView.setText(R.string.account_password_length_error);
			return false;
		}
		boolean hasDigit = false;
		boolean hasLetter = false;
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			if (c >= '0' && c <= '9') {
				hasDigit = true;
			}
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				hasLetter = true;
			}
		}
		if (hasDigit && hasLetter) {

		} else {
			mErrorPromptView.setText(R.string.account_password_digit_error);
			return false;
		}
		if (!password.equals(passwordRepeat)) {
			mErrorPromptView.setText(R.string.account_password_repeat_error);
			return false;
		}
		return true;
	}

	@Override
	public boolean onSunKey() {
		if (mRegisterBtn.hasFocus()) {
			onClick(mRegisterBtn);
		}
		return super.onSunKey();
	}

	@Override
	public boolean onMoonKey() {
		return super.onBackKey();
	}

	
		
	

}
