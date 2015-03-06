package com.ireadygo.app.gamelauncher.ui.personal;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.RentReliefItem;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;

public class UserFragmentB extends BaseContentFragment {

	private TextView mAccount;
	private TextView mAlipayAccountState;
	private ProgressBar mPlayTime;
	private TextView mPlayTimeState;
	private TextView mFeedbackMoney;
	private TextView mFeedbackMonth;

	public UserFragmentB(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_fragment_b, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		mAccount = (TextView)view.findViewById(R.id.account);
		mAlipayAccountState = (TextView)view.findViewById(R.id.alipay_account_state);
		mPlayTime = (ProgressBar)view.findViewById(R.id.play_time_progress);
		mPlayTimeState = (TextView)view.findViewById(R.id.play_time_state);
		mFeedbackMoney = (TextView)view.findViewById(R.id.feedback_money);
		mFeedbackMonth = (TextView)view.findViewById(R.id.feedback_months);
		initData();
	}

	@Override
	protected boolean isCurrentFocus() {
		return true;
	}

	private void initData() {
		setAccount();
		setAlipayAccountState(false);
		initProgressBar(20);
		initAlipayAccountState();
		initPlayTime();
		initFeedbackMoney();
		setFeedbackMonth(24);
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

	private void initAlipayAccountState() {
		setAlipayAccountState(false);
		if (!AccountManager.getInstance().isLogined(getRootActivity())) {
			return;
		}
		LoadAlipayAccountTask task = new LoadAlipayAccountTask();
		task.execute();
	}

	private void initPlayTime() {
		setPlayTimeProgress(0);
		setPlayTimeState(0, 20);
		if (AccountManager.getInstance().isLogined(getRootActivity())) {
			LoadPlayTimeTask task = new LoadPlayTimeTask();
			task.execute();
		}
	}

	private void initFeedbackMoney() {
		setFeedbackMoney(0, 1000);
		if (AccountManager.getInstance().isLogined(getRootActivity())) {
			LoadFeedbackMoneyTask task = new LoadFeedbackMoneyTask();
			task.execute();
		}
	}

	private void setAlipayAccountState(boolean hasBond) {
		if (!hasBond) {
			mAlipayAccountState.setVisibility(View.VISIBLE);
		} else {
			mAlipayAccountState.setVisibility(View.GONE);
		}
	}

	private void initProgressBar(int max) {
		mPlayTime.setMax(max);
	}

	private void setPlayTimeProgress(int playHour) {
		mPlayTime.setProgress(playHour);
	}

	private void setPlayTimeState(int playHour,int maxHour) {
		mPlayTimeState.setText(getRootActivity().getString(R.string.personal_play_time_state,playHour,maxHour));
	}

	private void setFeedbackMoney(int feedbackMoney,int totalMoney) {
		mFeedbackMoney.setText(getRootActivity().getString(R.string.personal_feedback_money,feedbackMoney,totalMoney));
	}

	private void setFeedbackMonth(int month) {
		mFeedbackMonth.setText(getRootActivity().getString(R.string.personal_feedback_month,month));
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

	private class LoadPlayTimeTask extends AsyncTask<Void, Void, RentReliefItem> {
		@Override
		protected RentReliefItem doInBackground(Void... params) {
			try {
				String account = GameInfoHub.instance(getRootActivity()).getSNCorrespondBindAccount(Build.SERIAL);
				if (!TextUtils.isEmpty(account) && account.equals(AccountManager.getInstance().getAccount(getRootActivity()))) {
					return GameInfoHub.instance(getRootActivity()).getRentReliefAppTime();
				}
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(RentReliefItem result) {
			if (result != null) {
				int playTime = secondToHour(result.getAppTime());
				int remainTime = secondToHour(result.getAppRemainTime());
				setPlayTimeProgress(playTime);
				setPlayTimeState(playTime, playTime + remainTime);
			}
		}
	}

	private class LoadFeedbackMoneyTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				String account = GameInfoHub.instance(getRootActivity()).getSNCorrespondBindAccount(Build.SERIAL);
				if (!TextUtils.isEmpty(account) && account.equals(AccountManager.getInstance().getAccount(getRootActivity()))) {
					return GameInfoHub.instance(getRootActivity()).getRebateMoney();
				}
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				setFeedbackMoney(Integer.parseInt(result), 1000);
			}
		}
	}

	private int secondToHour(long second) {
		return (int)(second / 3600);
	}


}
