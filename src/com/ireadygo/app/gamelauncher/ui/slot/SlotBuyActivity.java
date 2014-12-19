package com.ireadygo.app.gamelauncher.ui.slot;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.account.StatusCode;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.QuotaItem;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.snailgame.mobilesdk.OnQueryBalanceListener;
import com.umeng.analytics.MobclickAgent;

public class SlotBuyActivity extends BaseActivity implements OnClickListener {
	private static final int WHAT_SHOW_PROGRESS = 1;
	private static final int SHOW_PROGRESS_DELAY = 1000;

	private static final int SLOT_MEAL_1_COST = 20;
	private static final int SLOT_MEAL_2_COST = 180;
	private View mSlotGobackBtn;// 返回按钮

	private TextView mRabbitCoinView;// 兔兔币余额
	private TextView mRabbitTicketView;// 兔兔券余额
	private TextView mBuyBtn;// 支付按钮
	private TextView mSlotPayCoin;// 兔兔币应付金额
	private TextView mSlotPayTicket;// 兔兔券应付金额
	private TextView mPayablePrompt;
	private View mPayableLayout;

	private CheckedTextView mSlotMeal1;// 套餐一
	private CheckedTextView mSlotMeal2;// 套餐二
	private int mCurrentRabbitCoin;// 兔兔币余额数
	private int mCurrentRabbitTicket;// 兔兔券余额数

	private ProgressDialog mPromptDialog;
	private CheckQuotaTask mCheckQuotaTask;// 检查余额是否足够支付的task
	private BuySlotTask mBuySlotTask;
	private GameInfoHub mGameInfoHub;
	private int mPayRabbitCoin;// 兔兔币应付金额
	private int mPayRabbitTicket;// 兔兔券应付金额

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_SHOW_PROGRESS:
				showProgressDialog();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slot_buy_activity);
		mCurrentRabbitCoin = PreferenceUtils.getRabbitCoinBalance();
		mCurrentRabbitTicket = PreferenceUtils.getRabbitTicketBalance();
		mGameInfoHub = GameLauncher.instance().getGameInfoHub();
		initView();
	}

	private void initView() {
		mSlotGobackBtn = findViewById(R.id.slotGobackBtn);
		mSlotGobackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		mRabbitCoinView = (TextView) findViewById(R.id.slotCoin);
		mRabbitTicketView = (TextView) findViewById(R.id.slotTicket);

		mPayablePrompt = (TextView) findViewById(R.id.slotPayPrompt);
		mPayableLayout = findViewById(R.id.slotPayableLayout);
		mSlotPayCoin = (TextView) findViewById(R.id.slotPayCoin);
		mSlotPayTicket = (TextView) findViewById(R.id.slotPayTicket);

		mSlotMeal1 = (CheckedTextView) findViewById(R.id.slotMeal1);
		mSlotMeal1.setOnClickListener(this);

		mSlotMeal2 = (CheckedTextView) findViewById(R.id.slotMeal2);
		mSlotMeal2.setOnClickListener(this);

		mBuyBtn = (TextView) findViewById(R.id.slotBuyBtn);
		mBuyBtn.setOnClickListener(this);

		checkCanPayAsyn(SLOT_MEAL_1_COST);
		updateCurrentBalanceText(mCurrentRabbitCoin, mCurrentRabbitTicket);
		updatePayableText();
	}

	@Override
	protected void onStart() {
		super.onStart();
		updateCurrentBalanceAsyn();
	}

	private void updateCurrentBalanceAsyn() {
		AccountManager.getInstance().queryBalance(this, new OnQueryBalanceListener() {

			@Override
			public void onResult(int code, int balance, int balanceQuan) {
				if (code == StatusCode.OPERATION_SUCCESS) {
					mCurrentRabbitCoin = balance;
					mCurrentRabbitTicket = balanceQuan;
					PreferenceUtils.saveRabbitCoinBalance(balance);
					PreferenceUtils.saveRabbitTicketBalance(balanceQuan);
					updatePayableText();
					updateCurrentBalanceText(balance, balanceQuan);
				} else if(code == StatusCode.USER_IDENTITY_EXPIRED){
					Toast.makeText(SlotBuyActivity.this, R.string.user_identity_overdue, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(SlotBuyActivity.this, R.string.slot_query_balance_prompt, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void updateCurrentBalanceText(int balance, int balanceQuan) {
		mRabbitCoinView.setText(getResources().getString(R.string.slot_balance, balance));
		mRabbitTicketView.setText(String.valueOf(balanceQuan));
	}

	private void checkCanPayAsyn(int cost) {
		if (mHandler.hasMessages(WHAT_SHOW_PROGRESS)) {
			mHandler.removeMessages(WHAT_SHOW_PROGRESS);
		}
		if (mCheckQuotaTask != null) {
			mCheckQuotaTask.cancel(true);
			mCheckQuotaTask = null;
		}
		if (!NetworkUtils.isNetworkConnected(SlotBuyActivity.this)) {
			Toast.makeText(SlotBuyActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
			return;
		}
		mHandler.sendEmptyMessageDelayed(WHAT_SHOW_PROGRESS, SHOW_PROGRESS_DELAY);
		mCheckQuotaTask = new CheckQuotaTask(cost);
		mCheckQuotaTask.execute();
	}

	// 获取套餐对应的ID
	private int getCheckedConfigId() {
		if (mSlotMeal1.isChecked()) {
			return 2;
		} else {
			return 3;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.slotMeal1:
			if(!mSlotMeal1.isChecked()){
				mSlotMeal1.setChecked(true);
				mSlotMeal2.setChecked(false);
				checkCanPayAsyn(SLOT_MEAL_1_COST);
			}
			break;
		case R.id.slotMeal2:
			if(!mSlotMeal2.isChecked()){
				mSlotMeal1.setChecked(false);
				mSlotMeal2.setChecked(true);
				checkCanPayAsyn(SLOT_MEAL_2_COST);
			}
			break;
		case R.id.slotBuyBtn:
			if (isCanPay()) {// 购买
				final ConfirmDialog dialog = new ConfirmDialog(SlotBuyActivity.this);
				dialog.setPrompt(R.string.slot_title).setMsg(R.string.slot_buy_dialog_prompt)
						.setConfirmClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								// 购买卡槽
								String userId = AccountManager.getInstance().getLoginUni(SlotBuyActivity.this);
								if (TextUtils.isEmpty(userId)) {
									Toast.makeText(SlotBuyActivity.this, R.string.slot_should_login, Toast.LENGTH_SHORT)
											.show();
									return;
								}
								showProgressDialog();
								buySlot();
							}
						}).show();
			} else {// 充值
				AccountManager.getInstance().gotoCharge(SlotBuyActivity.this, true);
			}
			break;
		default:
			break;
		}
	}

	private void buySlot() {
		if (mBuySlotTask != null) {
			mBuySlotTask.cancel(true);
			mBuySlotTask = null;
		}
		mBuySlotTask = new BuySlotTask();
		mBuySlotTask.execute();
	}

	private boolean isCanPay() {
		int cost = mSlotMeal1.isChecked() ? SLOT_MEAL_1_COST : SLOT_MEAL_2_COST;
		return (mCurrentRabbitCoin + mCurrentRabbitTicket - cost) >= 0;
	}

	private void updatePayableText() {
		if (isCanPay()) {
			mBuyBtn.setText(R.string.slot_buy_now);
			mPayablePrompt.setVisibility(View.GONE);
			mPayableLayout.setVisibility(View.VISIBLE);
		} else {
			mBuyBtn.setText(R.string.slot_recharge);
			mPayablePrompt.setVisibility(View.VISIBLE);
			mPayableLayout.setVisibility(View.GONE);
		}
		mSlotPayCoin.setText(String.valueOf(mPayRabbitCoin));
		mSlotPayTicket.setText(String.valueOf(mPayRabbitTicket));
	}

	@Override
	public boolean onSunKey() {
		if (mSlotMeal1.hasFocus()) {
			onClick(mSlotMeal1);
		} else if (mSlotMeal2.hasFocus()) {
			onClick(mSlotMeal2);
		} else if (mBuyBtn.hasFocus()) {
			onClick(mBuyBtn);
		}
		return true;
	}
	
	@Override
	public boolean onBackKey() {
		setResult(RESULT_CANCELED);
		finish();
		return true;
	}
	
	private void showProgressDialog() {
		if (mPromptDialog == null) {
			mPromptDialog = new ProgressDialog(this);
			mPromptDialog.setCancelable(true);
		}
		mPromptDialog.show();
	}

	private void hideProgressDialog() {
		Log.d("liu.js", "hideProgressDialog");
		if (mPromptDialog != null) {
			mPromptDialog.dismiss();
		}
	}

	@Override
	protected void onStop() {
		if (mCheckQuotaTask != null) {
			mCheckQuotaTask.cancel(true);
			mCheckQuotaTask = null;
		}
		if (mBuySlotTask != null) {
			mBuySlotTask.cancel(true);
			mBuySlotTask = null;
		}
		super.onStop();
	}

	private class BuySlotTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				GameInfoHub.instance(SlotBuyActivity.this).purchaseMuchSlot(getCheckedConfigId());
				return true;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			hideProgressDialog();
			if (isCancelled()) {
				return;
			}
			if (result) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("__ct__", String.valueOf(mPayRabbitCoin + mPayRabbitTicket));
				map.put("msg", "信息： "
						+ (mSlotMeal1.isChecked() ? mSlotMeal1.getText().toString() : mSlotMeal2.getText().toString())
						+ "---币种：通用" + "---币值：" + mSlotPayCoin.getText().toString() + "/"
						+ mSlotPayTicket.getText().toString());
				MobclickAgent.onEvent(SlotBuyActivity.this, "slot_buy_succeed", map);
				Toast.makeText(SlotBuyActivity.this, R.string.slot_buy_success, Toast.LENGTH_SHORT).show();
				updateCurrentBalanceAsyn();
			} else {
				Toast.makeText(SlotBuyActivity.this, R.string.slot_buy_failed, Toast.LENGTH_SHORT).show();
			}
		}

	}

	private class CheckQuotaTask extends AsyncTask<Integer, Void, QuotaItem> {
		private int mCost;
		private String mErrMsg;

		public CheckQuotaTask(int cost) {
			mCost = cost;
		}

		@Override
		protected QuotaItem doInBackground(Integer... params) {
			QuotaItem item = null;
			try {
				item = mGameInfoHub.calculateQuota(mCost, 0);
			} catch (InfoSourceException e) {
				e.printStackTrace();
				mErrMsg = e.getMessage();
			}
			return item;
		}

		@Override
		protected void onPostExecute(QuotaItem quotaItem) {
			Log.d("liu.js", "CheckQuotaTask--onPostExecute--result=" + quotaItem);
			if (mHandler.hasMessages(WHAT_SHOW_PROGRESS)) {
				mHandler.removeMessages(WHAT_SHOW_PROGRESS);
			}
			hideProgressDialog();
			if (isCancelled()) {
				return;
			}
			if (quotaItem != null) {
				mPayRabbitCoin = quotaItem.getIConsumeRabbitCoin();
				mPayRabbitTicket = quotaItem.getIConsumeRabbitTicket();
				updatePayableText();
				return;
			}
			if (InfoSourceException.MSG_NO_ENOUGH_MONEY_ERROR.equals(mErrMsg)) {
				// Toast.makeText(SlotBuyActivity.this,
				// getString(R.string.money_not_enough),
				// Toast.LENGTH_SHORT).show();
				updatePayableText();
				return;
			}
		}

	}
}
