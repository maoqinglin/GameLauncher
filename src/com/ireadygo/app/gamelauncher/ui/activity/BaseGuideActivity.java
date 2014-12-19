package com.ireadygo.app.gamelauncher.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;

public class BaseGuideActivity extends BaseActivity implements OnClickListener {
	private TextView mGobackBtn;
	private ImageView mWifiSettingsBtn;
	private View mLineView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void initHeaderView(int headerTextId) {
		mGobackBtn = (TextView) findViewById(R.id.gobackBtn);
		mGobackBtn.setText(headerTextId);
		mGobackBtn.setOnClickListener(this);

		mWifiSettingsBtn = (ImageView) findViewById(R.id.guideWifiSettings);
		mWifiSettingsBtn.setOnClickListener(this);
		
		mLineView = findViewById(R.id.line);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gobackBtn:
			onGobackClick();
			break;
		case R.id.guideWifiSettings:
			onRTBtnClick();
			break;
		}
	}

	protected void onRTBtnClick() {

	}

	protected void onGobackClick() {
		finish();
	}
	
	public ImageView getWifiSettingsBtn(){
		return mWifiSettingsBtn;
	}
	
	protected View getLine() {
		return mLineView;
	}
}
