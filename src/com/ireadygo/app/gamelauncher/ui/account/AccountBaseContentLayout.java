package com.ireadygo.app.gamelauncher.ui.account;

import android.content.Context;
import android.util.AttributeSet;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.ui.base.KeyEventLayout;

public abstract class AccountBaseContentLayout extends KeyEventLayout {

	protected AccountDetailActivity mActivity;
	protected GameInfoHub mGameInfoHub;
	private int mLayoutTag;

	public AccountBaseContentLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mActivity = (AccountDetailActivity) context;
		mGameInfoHub = GameLauncher.instance().getGameInfoHub();
	}

	public AccountBaseContentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mActivity = (AccountDetailActivity) context;
	}

	public AccountBaseContentLayout(Context context, int layoutTag) {
		super(context);
		mActivity = (AccountDetailActivity) context;
		mGameInfoHub = GameLauncher.instance().getGameInfoHub();
		this.mLayoutTag = layoutTag;
	}

	protected boolean isActivityDestoryed() {
		if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
			return true;
		}
		return false;
	}

	protected void refreshLayout() {

	}

	public AccountDetailActivity getActivity() {
		return mActivity;
	}

	public int getLayoutTag(){
		return mLayoutTag;
	}
	
	@Override
	public boolean onMoonKey() {
		getActivity().getOptionsLayout().getCurrentItem().requestFocus();
		return super.onMoonKey();
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}

}
