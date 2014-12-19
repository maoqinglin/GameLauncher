package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.ireadygo.app.gamelauncher.R;
import com.umeng.analytics.MobclickAgentJSInterface;

public class WebViewLayout extends FrameLayout {
	private static final String USER_AGENT = " FreeStoreMuch";

	private String mCacheUrl = "";
	private WebView mWebView;
	private WebviewProgressBar mProgressBar;
	private boolean mIsLoading = false;

	public WebViewLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public WebViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WebViewLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.webview_layout, null);
		mProgressBar = (WebviewProgressBar) view.findViewById(R.id.webviewProgressBar);
		mWebView = (WebView) view.findViewById(R.id.webview);
		mWebView.setWebViewClient(new SampleWebViewClient());
		// mWebView.setWebChromeClient(new AppStoreChomeClient());
		setDefaultWebSettings(mWebView);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(view, lp);
		new MobclickAgentJSInterface(getContext(), mWebView, new WebChromeClient());
		// showProgressBar();
	}

	public void setDefaultWebSettings(WebView webview) {
		webview.setBackgroundColor(0);
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setDefaultTextEncodingName("UTF-8");
		webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setDomStorageEnabled(true);
		webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
		webSettings.setAllowFileAccess(true);
		webSettings.setAppCacheEnabled(true);
		webSettings.setUserAgentString(webSettings.getUserAgentString() + USER_AGENT);
	}

	public WebView getWebView() {
		return mWebView;
	}

	public WebviewProgressBar getProgressBar() {
		return mProgressBar;
	}

	public void loadUrl(String url, boolean isCacheUrl) {
		mWebView.loadUrl(url);
		mProgressBar.setProgress(0);
		if (isCacheUrl) {
			setCacheUrl(url);
		}
	}

	public String getCacheUrl() {
		return mCacheUrl;
	}

	public void setCacheUrl(String cacheUrl) {
		this.mCacheUrl = cacheUrl;
	}

	public void loadUrl(String url) {
		loadUrl(url, true);
	}

	public boolean isLoading() {
		return mIsLoading;
	}

	public void setIsLoading(boolean mIsLoading) {
		this.mIsLoading = mIsLoading;
	}

	private class SampleWebViewClient extends WebViewClient {
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Log.d("liu.js", "onReceivedError--errorCode=" + errorCode + "|failingUrl=" + failingUrl + "|description="
					+ description);
			if (isLoading()) {
				mIsLoading = false;
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d("liu.js", "onPageFinished-url=" + url);
			super.onPageFinished(view, url);
			mIsLoading = false;
		}

	}

}
