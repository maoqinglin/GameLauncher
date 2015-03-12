package com.ireadygo.app.gamelauncher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;

public class BaseGuideActivity extends BaseActivity implements OnClickListener {
	private TextView mLTBtn;
	private ImageView mWifiSettingsBtn;
	private View mLineView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void initHeaderView(int headerTextId) {
		mLTBtn = (TextView) findViewById(R.id.gobackBtn);
		mLTBtn.setText(headerTextId);
		mLTBtn.setOnClickListener(this);

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
		startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
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

	protected void setLTBtnText(String title) {
		mLTBtn.setText(title);
	}
}
