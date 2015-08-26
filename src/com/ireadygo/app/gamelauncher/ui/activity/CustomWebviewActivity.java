package com.ireadygo.app.gamelauncher.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.ui.widget.WebViewLayout;
import com.ireadygo.app.gamelauncher.utils.DeviceUtil;

public class CustomWebviewActivity extends Activity {
	public static final String STORE_ACCESS_KEY_USER_ID = "nUserId";
	public static final String STORE_ACCESS_KEY_APP_ID = "nAppId";
	public static final String STORE_ACCESS_KEY_IDENTITY = "cIdentity";
	public static final String EXTRA_URL = "EXTRA_URL";
	private WebViewLayout mWebView;
	private String mUrl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bind_alipay_account_activity);
		initUI();
	}

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void initUI() {
		mWebView = (WebViewLayout) findViewById(R.id.webview);
		mWebView.getWebView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mWebView.getWebView().addJavascriptInterface(new InternalJsInterface(), "AndroidInterface");
		mUrl = getIntent().getStringExtra(EXTRA_URL);
		if (mUrl != null) {
			WebSettings webSettings = mWebView.getWebView().getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDomStorageEnabled(true);
			webSettings.setAllowFileAccess(true);
			webSettings.setAppCacheEnabled(true);
			webSettings.setDatabaseEnabled(true);
			// 支持缩放，在SDK11以上，不显示缩放按钮
			webSettings.setSupportZoom(true);
			webSettings.setBuiltInZoomControls(true);
			if (Build.VERSION.SDK_INT >= 11) {
				webSettings.setDisplayZoomControls(false);
			}
			// 自适应网页宽度
			webSettings.setUseWideViewPort(true);
			webSettings.setLoadWithOverviewMode(true);
			synCookies(CustomWebviewActivity.this, mUrl);
			mWebView.loadUrl(mUrl);
		}
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

	private void synCookies(Context context, String url) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.setCookie(getDomain(url) + "[0]", STORE_ACCESS_KEY_USER_ID + "="
				+ AccountManager.getInstance().getLoginUni(context));// cookies是在HttpClient中获得的cookie
		cookieManager.setCookie(getDomain(url) + "[1]", STORE_ACCESS_KEY_IDENTITY + "="
				+ AccountManager.getInstance().getSessionId(context));
		cookieManager.setCookie(getDomain(url) + "[2]", STORE_ACCESS_KEY_APP_ID + "=" + GameLauncherConfig.MY_APPID);
		CookieSyncManager.getInstance().sync();
	}

	/**
	 * 取 绝对域名
	 * 
	 * @param url
	 * @return
	 */
	public static String getDomain(String url) {
		if (url == null || url.isEmpty()) {
			return null;
		}

		for (int i = 0; i < url.length(); i++) {
			if (url.charAt(i) == '/') {
				if ((i < url.length() - 1 && url.charAt(i + 1) != '/')) {
					return url.substring(0, i);
				} else if (i < url.length() - 1 && url.charAt(i + 1) == '/') {
					i++;
				}
			}
		}
		return url;
	}

	private class InternalJsInterface {

		@JavascriptInterface
		public String getLoginInfo() {
			AccountManager am = AccountManager.getInstance();
			String userId = am.getLoginUni(CustomWebviewActivity.this);
			if (userId == null) {
				userId = "";
			}
			String identity = am.getSessionId(CustomWebviewActivity.this);
			if (identity == null) {
				identity = "";
			}
			StringBuilder sb = new StringBuilder();
			sb.append("nUserId=");
			sb.append(userId);
			sb.append("&cIdentity=");
			sb.append(identity);
			sb.append("&nAppId=");
			sb.append(GameLauncherConfig.MY_APPID);
			return sb.toString();
		}

		@JavascriptInterface
		public String getAccount() {
			if (!isLogined()) {
				return "";
			}
			String account = AccountManager.getInstance().getAccount(CustomWebviewActivity.this);
			if (TextUtils.isEmpty(account)) {
				return "";
			}
			return account;
		}

//		public boolean getBssAccount() {
//			if (!isLogined()) {
//				return false;
//			}
//			Log.d("liu.js", "getBssAccount--" + PreferenceHelper.isBSSAccount());
//			return PreferenceHelper.isBSSAccount();
//		}

		@JavascriptInterface
		public String getNickName() {
			if (!isLogined()) {
				return "";
			}
			return AccountManager.getInstance().getNickName(CustomWebviewActivity.this);
		}

		@JavascriptInterface
		public String getDeviceId() {
			return Build.SERIAL;
		}

		@JavascriptInterface
		private boolean isLogined() {
			return AccountManager.getInstance().isLogined(CustomWebviewActivity.this);
		}

		@JavascriptInterface
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
