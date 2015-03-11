package com.ireadygo.app.gamelauncher.ui.user;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryInfo;
import com.ireadygo.app.gamelauncher.appstore.info.item.RentReliefInfo;
import com.ireadygo.app.gamelauncher.ui.activity.BindAlipayAccountActivity;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class UserFragmentA extends BaseContentFragment {

	private TextView mAccount;
	private TextView mAlipayAccountState;
	private ProgressBar mPlayTime;
	private TextView mPlayTimeState;
	private TextView mFeedbackRent;
	private TextView mExpiredDate;
	private int mFeedbackMonth = 0;
	private int mPlayTimeHours = 0;
	private String mAccountStr;

	public UserFragmentA(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_fragment_a, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mAccount = (TextView)view.findViewById(R.id.account);
		mAlipayAccountState = (TextView)view.findViewById(R.id.alipay_account_state);
		mAlipayAccountState.setOnClickListener(mOnClickListener);
		mPlayTime = (ProgressBar)view.findViewById(R.id.play_time_progress);
		mPlayTimeState = (TextView)view.findViewById(R.id.play_time_state);
		mFeedbackRent = (TextView)view.findViewById(R.id.feedback_rent);
		mExpiredDate = (TextView)view.findViewById(R.id.expired_date);
		initData();
	}

	@Override
	protected boolean isCurrentFocus() {
		return true;
	}

	private void initData() {
		setAccount();
		initProgressBar(20);
		initAlipayAccountState();
		initFeedbackState();
		initPlayTime();
		setExpiredDate("2017-5-10");
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

	private void initFeedbackState() {
		setFeedbackRent(0);
		if (AccountManager.getInstance().isLogined(getRootActivity())) {
			LoadFeedbackMonthTask task = new LoadFeedbackMonthTask();
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

	private void setFeedbackRent(int months) {
		Spanned colorMonths = Html.fromHtml(getRootActivity().getString(R.string.personal_months_reduction_pre)
				+ "<font color='#fbae1a' size='40px'>" + months + "</FONT>"
				+ getRootActivity().getString(R.string.personal_months_reduction_post));
		mFeedbackRent.setText(colorMonths);
	}

	private void setExpiredDate(String expiredDate) {
		Spanned date = Html.fromHtml(getRootActivity().getString(R.string.personal_expired_date)
				+ "<font color='#fbae1a' size='40px'>" + expiredDate + "</FONT>");
		mExpiredDate.setText(date);
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

	private class LoadPlayTimeTask extends AsyncTask<Void, Void, RentReliefInfo> {
		@Override
		protected RentReliefInfo doInBackground(Void... params) {
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
		protected void onPostExecute(RentReliefInfo result) {
			if (result != null) {
				int playTime = secondToHour(result.getAppTime());
				int remainTime = secondToHour(result.getAppRemainTime());
				setPlayTimeProgress(playTime);
				setPlayTimeState(playTime, playTime + remainTime);
			}
		}
	}

	private class LoadFeedbackMonthTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				return GameInfoHub.instance(getRootActivity()).getRentReliefMonths();
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				setFeedbackRent(Integer.parseInt(result));
			}
		}
	}

	private int secondToHour(long second) {
		return (int)(second / 3600);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.alipay_account_state:
				skipToBindAlipayAccount(getRootActivity());
				break;

			default:
				break;
			}
		}
	};

	private void skipToBindAlipayAccount(Context context) {
		Intent intent = new Intent(context,BindAlipayAccountActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}


}
