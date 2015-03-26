package com.ireadygo.app.gamelauncher.ui.guide;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.ui.GameLauncherActivity;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.activity.BaseGuideActivity;
import com.ireadygo.app.gamelauncher.ui.widget.GuideTwoBtnLayout;
import com.ireadygo.app.gamelauncher.ui.widget.GuideTwoBtnLayout.OnLRBtnClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class GuideAlipayActivity extends BaseGuideActivity {

	private GuideTwoBtnLayout mGuideTwoBtnLayout;
	private OperationTipsLayout mTipsLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starting_guide_alipay_layout);
		initUI();
	}

	private void initUI() {
		initHeaderView(R.string.starting_guide_alipay_title);
		mTipsLayout = (OperationTipsLayout) findViewById(R.id.operationTipsLayout);
		mTipsLayout.setTipsVisible(View.GONE, TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);

		mGuideTwoBtnLayout = (GuideTwoBtnLayout) findViewById(R.id.guideTwoBtnLayout);
		mGuideTwoBtnLayout.setLeftBtnText(R.string.starting_guide_alipay_bind);
		mGuideTwoBtnLayout.setRightBtnText(R.string.starting_guide_alipay_skip);
		mGuideTwoBtnLayout.setOnLRBtnClickListener(new OnLRBtnClickListener() {
			
			@Override
			public void onRightBtnClickListener(View view) {
				Intent intent = new Intent(GuideAlipayActivity.this, GameLauncherActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			}
			
			@Override
			public void onLeftBtnClickListener(View view) {
				LoadAlipayBindUrlTask task = new LoadAlipayBindUrlTask();
				task.execute();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN) {
			if(keyCode == SnailKeyCode.BACK_KEY || keyCode == SnailKeyCode.MOON_KEY) {
				
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void skipWebsite(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	private class LoadAlipayBindUrlTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				return GameInfoHub.instance(GuideAlipayActivity.this).bindPayment();
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				skipWebsite(result);
			} else {
				Toast.makeText(GuideAlipayActivity.this,getString(R.string.user_alipay_account_bind_error), Toast.LENGTH_SHORT).show();
			}
		}
	}

}
