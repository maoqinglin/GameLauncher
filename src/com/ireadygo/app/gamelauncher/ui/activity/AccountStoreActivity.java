package com.ireadygo.app.gamelauncher.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.widget.WebViewLayout;
import com.ireadygo.app.gamelauncher.utils.DeviceUtil;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;

public class AccountStoreActivity extends BaseActivity {

	private static final String ACCOUNT_STORE_URL = "http://10040.snail.com";
	private static final int DEFAULT_APP_ID = 2;
	private WebViewLayout mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_store_activity);
		initUI();
	}

	@SuppressLint("JavascriptInterface")
	private void initUI() {
		mWebView = (WebViewLayout) findViewById(R.id.webview);
		mWebView.getWebView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mWebView.getWebView().addJavascriptInterface(new InternalJsInterface(), "AndroidInterface");
		mWebView.loadUrl(ACCOUNT_STORE_URL);
	}

	@Override
	public boolean onBackKey() {
		if (mWebView.getWebView().canGoBack()) {
			mWebView.getWebView().goBack();
			return true;
		} else {
			finish();
			return true;
		}
	}

	private class InternalJsInterface {

		public String getLoginInfo() {
			AccountManager am = AccountManager.getInstance();
			String userId = am.getLoginUni(AccountStoreActivity.this);
			if (userId == null) {
				userId = "";
			}
			String identity = am.getSessionId(AccountStoreActivity.this);
			if (identity == null) {
				identity = "";
			}
			StringBuilder sb = new StringBuilder();
			sb.append("nUserId=");
			sb.append(userId);
			sb.append("&cIdentity=");
			sb.append(identity);
			sb.append("&nAppId=");
			sb.append(DEFAULT_APP_ID);
			Log.d("liu.js", "getLoginInfo--" + sb.toString());
			return sb.toString();
		}

		public String getAccount() {
			if (!isLogined()) {
				return "";
			}
			String account = AccountManager.getInstance().getAccount(AccountStoreActivity.this);
			if (TextUtils.isEmpty(account)) {
				return "";
			}
			Log.d("liu.js", "getAccount--" + account);
			return account;
		}

		public boolean getBssAccount() {
			if (!isLogined()) {
				return false;
			}
			Log.d("liu.js", "getBssAccount--" + PreferenceUtils.isBSSAccount());
			return PreferenceUtils.isBSSAccount();
		}

		public String getNickName() {
			if (!isLogined()) {
				return "";
			}
			return AccountManager.getInstance().getNickName(AccountStoreActivity.this);
		}

		public String getDeviceId() {
			return DeviceUtil.getIMEI(AccountStoreActivity.this);
		}

		private boolean isLogined() {
			return AccountManager.getInstance().isLogined(AccountStoreActivity.this);
		}

		public String getPhoneNumber() {
			if (!isLogined()) {
				return "";
			}
			UserInfoItem userInfo = GameLauncherApplication.getApplication().getUserInfoItem();
			if (userInfo == null) {
				return "";
			}
			return userInfo.getCPhone();
		}
	}
}
