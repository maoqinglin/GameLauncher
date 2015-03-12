package com.ireadygo.app.gamelauncher.ui.user;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.RentReliefInfo;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.item.BaseAdapterItem;
import com.ireadygo.app.gamelauncher.ui.item.ImageItem;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;

public class UserFragmentA extends BaseContentFragment {

	private static final float DISABLE_ALPHA = 0.3f;
	private static final float ENABLE_ALPHA = 1.0f;
	private TextView mAccount;
	private TextView mAlipayAccountState;
	private ProgressBar mPlayTime;
	private TextView mPlayTimeState;
	private TextView mFeedbackRent;
	private TextView mExpiredDate;
	private TextView mNotBindTip;
	private TextView mPlayTimeTitle;
	private BaseAdapterItem mSelectedItem;
	private ImageItem mUserCenter,mNotice,mRecharge;
	private Animator mAlipayTextSelectAnimator;
	private Animator mAlipayTextUnSelectAnimator;

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
		mAlipayAccountState.setOnFocusChangeListener(mAlipayTextOnFocusChangeListener);
		mPlayTime = (ProgressBar)view.findViewById(R.id.play_time_progress);
		mPlayTimeState = (TextView)view.findViewById(R.id.play_time_state);
		mFeedbackRent = (TextView)view.findViewById(R.id.feedback_rent);
		mExpiredDate = (TextView)view.findViewById(R.id.expired_date);
		mNotBindTip = (TextView)view.findViewById(R.id.not_bind_account);
		mPlayTimeTitle = (TextView)view.findViewById(R.id.play_time_title);
		mUserCenter = (ImageItem)view.findViewById(R.id.user_center_layout);
		mNotice = (ImageItem)view.findViewById(R.id.user_notice_layout);
		mRecharge = (ImageItem)view.findViewById(R.id.user_recharge_layout);
		mUserCenter.setOnClickListener(mOnClickListener);
		mNotice.setOnClickListener(mOnClickListener);
		mRecharge.setOnClickListener(mOnClickListener);
		mUserCenter.setOnFocusChangeListener(mOnFocusChangeListener);
		mNotice.setOnFocusChangeListener(mOnFocusChangeListener);
		mRecharge.setOnFocusChangeListener(mOnFocusChangeListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	@Override
	protected boolean isCurrentFocus() {
		return true;
	}


	private void initData() {
		setAccount();
		initAlipayAccountState();
		if (!PreferenceUtils.getDeviceBindAccount()
				.equalsIgnoreCase(AccountManager.getInstance().getAccount(getRootActivity()))) {
			mNotBindTip.setVisibility(View.VISIBLE);
			disableFeedbackState();
			disablePlaystate();
			disableExpiredDate();
		} else {
			mNotBindTip.setVisibility(View.GONE);
			initFeedbackState();
			initPlayTime();
			initExpiredDate();
			updateNextFocus();
		}
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
		setAlipayAccountState(true);
		if (!AccountManager.getInstance().isLogined(getRootActivity())) {
			return;
		}
		LoadAlipayAccountTask task = new LoadAlipayAccountTask();
		task.execute();
	}

	private void initPlayTime() {
		setPlayTimeProgress(0,20);
		setPlayTimeState(0, 20);
		if (AccountManager.getInstance().isLogined(getRootActivity())) {
			LoadPlayTimeTask task = new LoadPlayTimeTask();
			task.execute();
		}
		mPlayTime.setAlpha(ENABLE_ALPHA);
		mPlayTimeState.setAlpha(ENABLE_ALPHA);
		mPlayTimeTitle.setAlpha(ENABLE_ALPHA);
	}

	private void initFeedbackState() {
		setFeedbackRent(0);
		mFeedbackRent.setAlpha(ENABLE_ALPHA);
		if (AccountManager.getInstance().isLogined(getRootActivity())) {
			LoadFeedbackMonthTask task = new LoadFeedbackMonthTask();
			task.execute();
		}
	}

	private void initExpiredDate() {
		setExpiredDate("");
		mExpiredDate.setAlpha(ENABLE_ALPHA);
	}

	private void disableFeedbackState() {
		setFeedbackRent(0);
		mFeedbackRent.setAlpha(DISABLE_ALPHA);
		mFeedbackRent.invalidate();
	}

	private void disablePlaystate() {
		setPlayTimeProgress(0, 20);
		setPlayTimeState(0, 20);
		mPlayTime.setAlpha(DISABLE_ALPHA);
		mPlayTimeState.setAlpha(DISABLE_ALPHA);
		mPlayTimeTitle.setAlpha(DISABLE_ALPHA);
	}

	private void disableExpiredDate() {
		setExpiredDate("");
		mExpiredDate.setAlpha(DISABLE_ALPHA);
	}

	private void setAlipayAccountState(boolean hasBond) {
		if (!hasBond) {
			mAlipayAccountState.setVisibility(View.VISIBLE);
		} else {
			mAlipayAccountState.setVisibility(View.GONE);
		}
	}

	private void setPlayTimeProgress(int playHour,int maxHour) {
		mPlayTime.setMax(maxHour);
		mPlayTime.setProgress(playHour);
	}

	private void setPlayTimeState(int playHour,int maxHour) {
		mPlayTimeState.setText(getRootActivity().getString(R.string.personal_play_time_state,playHour,maxHour));
	}

	private void setFeedbackRent(int months) {
		Spanned colorMonths = Html.fromHtml(getRootActivity().getString(R.string.personal_months_reduction_pre)
				+ "<font>" + months + "</font>"
				+ getRootActivity().getString(R.string.personal_months_reduction_post));
		mFeedbackRent.setText(colorMonths);
	}

	private void setExpiredDate(String expiredDate) {
		Spanned date = Html.fromHtml(getRootActivity().getString(R.string.personal_expired_date)
				+ "<font>" + expiredDate + "</font>");
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
			} else {
				setAlipayAccountState(false);
			}
			updateNextFocus();
		}
	}

	private class LoadPlayTimeTask extends AsyncTask<Void, Void, RentReliefInfo> {
		@Override
		protected RentReliefInfo doInBackground(Void... params) {
			try {
				String account = GameInfoHub.instance(getRootActivity()).getSNCorrespondBindAccount(Build.SERIAL);
				if (!TextUtils.isEmpty(account) && account.equalsIgnoreCase(AccountManager.getInstance().getAccount(getRootActivity()))) {
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
				int targetTime = secondToHour(result.getTargetTime());
				setPlayTimeProgress(playTime,targetTime);
				setPlayTimeState(playTime, targetTime);
				setExpiredDate(result.getExpirationDate().toString());
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


	private void skipToBindAlipayAccount() {
		LoadAlipayBindUrlTask task = new LoadAlipayBindUrlTask();
		task.execute();
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Anchor anchor = null;
			switch (v.getId()) {
			case R.id.alipay_account_state:
				skipToBindAlipayAccount();
				break;
			case R.id.user_center_layout:
				anchor = new Anchor(Destination.ACCOUNT_PERSONAL);
				skipToUserUI(anchor);
				break;
			case R.id.user_notice_layout:
				anchor = new Anchor(Destination.ACCOUNT_NOTICE);
				skipToUserUI(anchor);
				break;
			case R.id.user_recharge_layout:
				anchor = new Anchor(Destination.ACCOUNT_RECHARGE);
				skipToUserUI(anchor);
				break;
			default:
				break;
			}
		}

		private void skipToUserUI(Anchor anchor) {
			if (anchor != null) {
				Intent intent = anchor.getIntent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				getRootActivity().startActivity(intent);
				SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
			}
		}
	};

	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				if (mSelectedItem != null) {
					animatorToUnselected(mSelectedItem);
					mSelectedItem = null;
				}
				View selectedView = v;
				if(selectedView != null && selectedView instanceof BaseAdapterItem){
					mSelectedItem = (BaseAdapterItem) selectedView;
					animatorToSelected(mSelectedItem);
				}
				if(v.isInTouchMode()){
					v.performClick();
				}
			} else {
				if (!v.isInTouchMode()) {
					if (mSelectedItem != null) {
						animatorToUnselected(mSelectedItem);
						mSelectedItem = null;
					}
				}
			}
		}
	};

	private void animatorToSelected(BaseAdapterItem item) {
		item.toSelected(null);
	}

	private void animatorToUnselected(BaseAdapterItem item) {
		item.toUnselected(null);
	}

	private OnFocusChangeListener mAlipayTextOnFocusChangeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				mAlipayAccountState.setTextColor(getResources().getColor(R.color.orange));
				if (mAlipayTextUnSelectAnimator != null && mAlipayTextUnSelectAnimator.isRunning()) {
					mAlipayTextUnSelectAnimator.cancel();
				}
				mAlipayTextSelectAnimator = createTextAnimator(mAlipayAccountState, 1.088f);
				mAlipayTextSelectAnimator.start();
			} else {
				mAlipayAccountState.setTextColor(getResources().getColor(R.color.white));
				if (mAlipayTextSelectAnimator != null && mAlipayTextSelectAnimator.isRunning()) {
					mAlipayTextSelectAnimator.cancel();
				}
				mAlipayTextUnSelectAnimator = createTextAnimator(mAlipayAccountState, 1);
				mAlipayTextUnSelectAnimator.start();
			}
		}
	};

	private Animator createTextAnimator(View view,float textScale) {
		AnimatorSet animatorSet = new AnimatorSet();

		PropertyValuesHolder iconScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, textScale);
		PropertyValuesHolder iconScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, textScale);
		ObjectAnimator animatorText = ObjectAnimator.ofPropertyValuesHolder(view, iconScaleXHolder,
				iconScaleYHolder);
		animatorSet.play(animatorText);
		animatorSet.setDuration(200);
		return animatorSet;
	}

	private void updateNextFocus() {
		if (getMenu().getCurrentItem() == null) {
			return;
		}
		if (mAlipayAccountState.getVisibility() == View.GONE) {
			getMenu().getCurrentItem().setNextFocusRightId(R.id.user_center_layout);
			mUserCenter.setNextFocusUpId(R.id.user_center_layout);
			mUserCenter.setNextFocusDownId(R.id.user_center_layout);
			mRecharge.setNextFocusUpId(R.id.user_recharge_layout);
			mRecharge.setNextFocusDownId(R.id.user_recharge_layout);
		} else {
			getMenu().getCurrentItem().setNextFocusRightId(R.id.alipay_account_state);
			mUserCenter.setNextFocusUpId(R.id.alipay_account_state);
			mUserCenter.setNextFocusDownId(R.id.user_center_layout);
			mRecharge.setNextFocusUpId(R.id.alipay_account_state);
			mRecharge.setNextFocusDownId(R.id.user_recharge_layout);
		}
	}

	private void skipWebsite(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		getRootActivity().startActivity(intent);
	}

	private class LoadAlipayBindUrlTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				return GameInfoHub.instance(getRootActivity()).bindPayment();
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				skipWebsite(result);
			}
		}
	}

}
