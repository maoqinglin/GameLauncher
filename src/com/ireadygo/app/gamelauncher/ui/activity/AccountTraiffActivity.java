package com.ireadygo.app.gamelauncher.ui.activity;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.R.id;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.FeeConfigItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.QuotaItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.RechargePhoneItem;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.umeng.analytics.MobclickAgent;

public class AccountTraiffActivity extends BaseAccountActivity implements OnClickListener, OnFocusChangeListener {

	private static final String TRAIFF_TYPE = "type";
	private static final String TRAIFF_PHONE = "phone";
	private static final String TRAIFF_BIND = "bind";
	private TextView mFreeCardNum;
	private TextView mTraiffType;
	private TextView mRabbitCoin;
	private TextView mRabbitTicket;
	private Button mPayBtn;
	private ProgressDialog mProgressDialog;
	private FeeConfigItem mFeeConfigItem;
	private String mBindPhoneNum;
	private boolean isBind;
	private GameInfoHub mGameInfoHub;
	private CheckQuotaTask mCheckQuotaTask;
	private PayTask mPayTask;
	private int rabbitCoin;
	private int rabbitTicket;
	private View mCostCoinLayout;
	private TextView mTraiffPrompt;
	private TextView mAddBtn;
	private TextView mSubtractBtn;

	private int mCurTraiffCount = 1;
	private int mPreTrraiffCount = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_traiff_activity);
		initHeaderView(R.string.account_traiff_recharge);
		mGameInfoHub = GameLauncher.instance().getGameInfoHub();

		if (getIntent() != null && getIntent().getExtras() != null) {
			if (getIntent().getParcelableExtra(TRAIFF_TYPE) != null) {
				mFeeConfigItem = (FeeConfigItem) getIntent().getParcelableExtra(TRAIFF_TYPE);
			}
			mBindPhoneNum = getIntent().getExtras().getString(TRAIFF_PHONE, "");
			isBind = getIntent().getExtras().getBoolean(TRAIFF_BIND);

			if (mFeeConfigItem == null || TextUtils.isEmpty(mBindPhoneNum)) {
				Toast.makeText(this, getString(R.string.recharge_account_empty), Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
		}
		initUI();
		updatePayText();
	}

	private void dismissLoadingDialog() {
		if (isFinishing() || isDestroyed()) {
			return;
		}
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	private void showLoadingDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("");
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}

	private void initUI() {
		mFreeCardNum = (TextView) findViewById(R.id.freeCardNumber);
		mFreeCardNum.setText(mBindPhoneNum);

		mTraiffType = (TextView) findViewById(id.freeCardTraiffType);
		mTraiffType.setText(mFeeConfigItem.getSMobileFeeName() + " X " + mCurTraiffCount);

		mRabbitCoin = (TextView) findViewById(R.id.accountCoin);
		mRabbitCoin.setText("0 " + getString(R.string.rabbit_coin));

		mRabbitTicket = (TextView) findViewById(R.id.accountTicket);
		mRabbitTicket.setText("0 " + getString(R.string.rabbit_ticket));

		mPayBtn = (Button) findViewById(R.id.accountPayBtn);
		mPayBtn.setOnClickListener(this);
		mPayBtn.setOnFocusChangeListener(this);

		mCostCoinLayout = (LinearLayout) findViewById(R.id.cost_coin_layout);
		mTraiffPrompt = (TextView) findViewById(R.id.traiffPayPrompt);

		mAddBtn = (TextView) findViewById(R.id.addBtn);
		mAddBtn.setOnClickListener(this);
		mAddBtn.setOnFocusChangeListener(this);

		mSubtractBtn = (TextView) findViewById(R.id.subtractBtn);
		mSubtractBtn.setOnClickListener(this);
		mSubtractBtn.setOnFocusChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (!NetworkUtils.isNetworkConnected(this)) {
			Toast.makeText(this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			return;
		}

		switch (v.getId()) {
		case R.id.addBtn:
			mPreTrraiffCount = mCurTraiffCount;
			mCurTraiffCount++;
			if (mCurTraiffCount > mFeeConfigItem.getILimit()) {
				mCurTraiffCount = mFeeConfigItem.getILimit();
			}
			updatePayText();
			break;

		case R.id.subtractBtn:
			mPreTrraiffCount = mCurTraiffCount;
			mCurTraiffCount--;
			if (mCurTraiffCount < 1) {
				mCurTraiffCount = 1;
			}
			updatePayText();
			break;

		case R.id.accountPayBtn:
			if (TextUtils.isEmpty(mBindPhoneNum)) {
				Toast.makeText(this, getString(R.string.recharge_account_empty), Toast.LENGTH_SHORT).show();
				return;
			}
			if (mPayBtn.getText().equals(getString(R.string.account_celerity_payment))) {
				pay();
			} else {
				AccountManager.getInstance().gotoCharge(AccountTraiffActivity.this, true);
			}
			break;

		default:
			break;
		}
	}

	private void pay() {
		if (mPayTask != null) {
			mPayTask.cancel(true);
			mPayTask = null;
		}
		mPayTask = new PayTask();
		mPayTask.execute();
	}

	private void updatePayText() {
		if (!NetworkUtils.isNetworkConnected(AccountTraiffActivity.this)) {
			Toast.makeText(AccountTraiffActivity.this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			mCurTraiffCount = mPreTrraiffCount;
			return;
		}
		if (mCheckQuotaTask != null) {
			mCheckQuotaTask.cancel(true);
			mCheckQuotaTask = null;
		}
		mCheckQuotaTask = new CheckQuotaTask();
		mCheckQuotaTask.execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case SnailKeyCode.SUN_KEY:
			if (mAddBtn.hasFocus()) {
				onClick(mAddBtn);
			} else if (mSubtractBtn.hasFocus()) {
				onClick(mSubtractBtn);
			} else if (mPayBtn.hasFocus()) {
				onClick(mPayBtn);
			}
			return true;
		case SnailKeyCode.BACK_KEY:
		case SnailKeyCode.MOON_KEY:
			finish();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			if (v.isInTouchMode()) {
				onClick(v);
			}
		}
	}

	private class CheckQuotaTask extends AsyncTask<Integer, Void, QuotaItem> {

		private String mErrMsg;

		@Override
		protected void onPreExecute() {
			showLoadingDialog();
			super.onPreExecute();
		}

		@Override
		protected QuotaItem doInBackground(Integer... params) {
			QuotaItem item = null;
			try {
				if (isBind) {
					item = mGameInfoHub.calculateQuota(mFeeConfigItem.getIMobileFeeMoney() * mCurTraiffCount, 0);
				} else {
					item = mGameInfoHub.calculateQuota(mFeeConfigItem.getIMobileFeeMoney() * mCurTraiffCount, 1);
				}
			} catch (InfoSourceException e) {
				e.printStackTrace();
				mErrMsg = e.getMessage();
			}
			return item;
		}

		@Override
		protected void onPostExecute(QuotaItem quotaItem) {
			if (!isCancelled()) {
				dismissLoadingDialog();
				if (!NetworkUtils.isNetworkConnected(AccountTraiffActivity.this)) {
					Toast.makeText(AccountTraiffActivity.this, getString(R.string.no_network), Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (quotaItem != null) {
					mTraiffType.setText(mFeeConfigItem.getSMobileFeeName() + " X " + mCurTraiffCount);
					rabbitCoin = quotaItem.getIConsumeRabbitCoin();
					rabbitTicket = quotaItem.getIConsumeRabbitTicket();
					mRabbitCoin.setText(rabbitCoin + " " + getString(R.string.rabbit_coin));
					mRabbitTicket.setText(rabbitTicket + " " + getString(R.string.rabbit_ticket));
					return;
				}
				if (InfoSourceException.MSG_NO_ENOUGH_MONEY_ERROR.equals(mErrMsg)) {
					mCurTraiffCount = mPreTrraiffCount;
					Toast.makeText(AccountTraiffActivity.this, getString(R.string.money_not_enough), Toast.LENGTH_SHORT)
							.show();
					mCostCoinLayout.setVisibility(View.GONE);
					mTraiffPrompt.setVisibility(View.VISIBLE);
					mPayBtn.setText(R.string.slot_recharge);
					return;
				}
				super.onPostExecute(quotaItem);
			}
		}
	}

		private class PayTask extends AsyncTask<Integer, Void, RechargePhoneItem> {

			private String mErrorMsg = "";

			@Override
			protected void onPreExecute() {
				showLoadingDialog();
				super.onPreExecute();
			}

			@Override
			protected RechargePhoneItem doInBackground(Integer... params) {
				RechargePhoneItem item = null;
				try {
					item = mGameInfoHub.rechargePhone(mFeeConfigItem.getNMobileFeeId(), mBindPhoneNum, mCurTraiffCount);
				} catch (InfoSourceException e) {
					e.printStackTrace();
					mErrorMsg = e.getMessage();
				}
				return item;
			}

			@Override
			protected void onPostExecute(RechargePhoneItem item) {
				dismissLoadingDialog();
				if (!NetworkUtils.isNetworkConnected(AccountTraiffActivity.this)) {
					Toast.makeText(AccountTraiffActivity.this, getString(R.string.no_network), Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (item != null) {
					Toast.makeText(AccountTraiffActivity.this, getString(R.string.recharge_succeed), Toast.LENGTH_SHORT)
							.show();
					HashMap<String, String> map = new HashMap<String, String>();
					StringBuilder sb = new StringBuilder();
					sb.append("资费名称：");
					sb.append(mFeeConfigItem.getSMobileFeeName());
					sb.append("---数量：");
					sb.append(String.valueOf(mCurTraiffCount));
					sb.append("---币种：");
					sb.append((isBind ? "通用" : "兔兔币支付"));
					sb.append("---币值：");
					sb.append(mRabbitCoin.getText().toString());
					sb.append("/");
					sb.append(mRabbitTicket.getText().toString());
					map.put("msg", sb.toString());
					map.put("__ct__", String.valueOf(rabbitCoin + rabbitTicket));
					MobclickAgent.onEvent(AccountTraiffActivity.this, "feeconfig_buy_succeed", map);
					AccountTraiffActivity.this.finish();
					return;
				} else {
					if (InfoSourceException.MSG_MONEY_NOT_ENOUGH_ERROR.equals(mErrorMsg)) {
						Toast.makeText(AccountTraiffActivity.this, getString(R.string.recharge_money_not_enough),
								Toast.LENGTH_SHORT).show();
					} else if (InfoSourceException.MSG_PHONE_NOT_BIND_ERROR.equals(mErrorMsg)) {
						Toast.makeText(AccountTraiffActivity.this, getString(R.string.recharge_phone_not_bind),
								Toast.LENGTH_SHORT).show();
					} else if (InfoSourceException.MSG_FEE_LIMINT_ERROR.equals(mErrorMsg)) {
						Toast.makeText(AccountTraiffActivity.this, getString(R.string.recharge_phone_fee_limint),
								Toast.LENGTH_SHORT).show();
					}
				}

				super.onPostExecute(item);
			}

		}
	}
