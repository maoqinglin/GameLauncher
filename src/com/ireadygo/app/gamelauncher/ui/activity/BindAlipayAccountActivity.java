package com.ireadygo.app.gamelauncher.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.ui.widget.WebViewLayout;
import com.ireadygo.app.gamelauncher.utils.DeviceUtil;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;

public class BindAlipayAccountActivity extends Activity {

	private WebViewLayout mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bind_alipay_account_activity);
		initUI();
	}

	@SuppressLint("JavascriptInterface")
	private void initUI() {
		mWebView = (WebViewLayout) findViewById(R.id.webview);
		mWebView.getWebView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		LoadAlipayBindUrlTask task = new LoadAlipayBindUrlTask();
		task.execute();
//		mWebView.loadUrl(ACCOUNT_STORE_URL);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.getWebView().canGoBack()) {
				mWebView.getWebView().goBack();
				return true;
			} else {
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private class LoadAlipayBindUrlTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				return GameInfoHub.instance(BindAlipayAccountActivity.this).bindPayment();
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				mWebView.loadUrl(result);
			}
		}
	}

}
