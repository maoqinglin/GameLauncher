package com.ireadygo.app.gamelauncher.ui.personal;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;

public class UserFragmentC extends BaseContentFragment {

	private TextView mAccount;
	private TextView mAlipayAccountState;

	public UserFragmentC(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_fragment_c, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		mAccount = (TextView)view.findViewById(R.id.account);
		mAlipayAccountState = (TextView)view.findViewById(R.id.alipay_account_state);
		initData();
	}

	@Override
	protected boolean isCurrentFocus() {
		return true;
	}

	private void initData() {
		setAccount();
		initAlipayAccountState();
	}

	private void initAlipayAccountState() {
		setAlipayAccountState(false);
		if (!AccountManager.getInstance().isLogined(getRootActivity())) {
			return;
		}
		LoadAlipayAccountTask task = new LoadAlipayAccountTask();
		task.execute();
	}



	private void setAccount() {
		String account = AccountManager.getInstance().getAccount(getRootActivity());
		String accountState;
		if (!AccountManager.getInstance().isLogined(getRootActivity()) || TextUtils.isEmpty(account)) {
			accountState = getRootActivity().getString(R.string.personal_account_unlogin);
		} else {
			accountState = account;
		}
		mAccount.setText(getRootActivity().getString(R.string.personal_account,accountState));
	}

	private void setAlipayAccountState(boolean hasBond) {
		if (!hasBond) {
			mAlipayAccountState.setVisibility(View.VISIBLE);
		} else {
			mAlipayAccountState.setVisibility(View.GONE);
		}
	}

	private class LoadAlipayAccountTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				return GameInfoHub.instance(getRootActivity()).getPaymentSign();
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
			
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				setAlipayAccountState(true);
				return;
			}
			setAlipayAccountState(false);
		}
	}



}
