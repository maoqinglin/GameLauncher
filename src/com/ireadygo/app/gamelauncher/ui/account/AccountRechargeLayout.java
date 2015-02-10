package com.ireadygo.app.gamelauncher.ui.account;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.account.StatusCode;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.activity.AccountTicketRechargeDoneActivity;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.ui.widget.CustomerEditText;
import com.ireadygo.app.gamelauncher.ui.widget.CustomerSpinner;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.snailgame.mobilesdk.OnQueryBalanceListener;
import com.umeng.analytics.MobclickAgent;

public class AccountRechargeLayout extends AccountBaseContentLayout implements OnClickListener{

	private TextView mAccountName;
	private TextView mRubbitCurrency;
	private TextView mRubbitTicket;
	private CustomerSpinner mTicketType;
	private CustomerEditText mTicketNum;
	private TextView mRabbitRecharge;
	private TextView mRechargeConfirm;
	private RechargeTask mRechargeTask;
	private ProgressDialog mProgressDialog;
	private int mTicketTypeValue = 0;
	private int mResultCount = 0;
	private int mExpiredDays = 0;

	public AccountRechargeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public AccountRechargeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AccountRechargeLayout(Context context, int layoutTag) {
		super(context, layoutTag);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.account_recharge, this, true);
		mAccountName = (TextView)findViewById(R.id.account_name);
		UserInfoItem userInfo = GameLauncherApplication.getApplication().getUserInfoItem();
		if(userInfo != null){
			mAccountName.setText(userInfo.getSNickname());
		}
		mRubbitCurrency = (TextView)findViewById(R.id.rubbitCurrency);
		mRubbitTicket = (TextView)findViewById(R.id.rubbitTicket);
		mTicketType = (CustomerSpinner)findViewById(R.id.ticketType);
		mTicketNum = (CustomerEditText)findViewById(R.id.ticketCode);
		
		mRabbitRecharge = (TextView)findViewById(R.id.rabbitRecharge);
		mRechargeConfirm = (TextView)findViewById(R.id.rechargeConfirm);
		
		ArrayAdapter<String> arrayAdapter = new TicketTypeAdapter(getContext(), R.layout.account_age_textview,
				R.id.accountAgeItem, getContext().getResources().getStringArray(R.array.recharge_ticket_types));
		mTicketType.setAdapter(arrayAdapter);
		mTicketType.setDropDownVerticalOffset(5);
		mTicketType.setSelection(0);
		mTicketType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mTicketTypeValue = position;
				mTicketType.setSelection(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		mRubbitTicket.setOnClickListener(this);
		mRubbitTicket.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && v.isInTouchMode()){
					onClick(v);
				}
			}
		});

		mRubbitCurrency.setText(String.valueOf(PreferenceUtils.getRabbitCoinBalance()));
		mRubbitTicket.setText(String.valueOf(PreferenceUtils.getRabbitTicketBalance()));

		mRabbitRecharge.setOnClickListener(this);
		mRabbitRecharge.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && v.isInTouchMode()){
					onClick(v);
				}
			}
		});
		
		mRechargeConfirm.setOnClickListener(this);
		mRechargeConfirm.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && v.isInTouchMode()){
					onClick(v);
				}
			}
		});
		
		updateBalance();
	}

	@Override
	public void refreshLayout() {
		super.refreshLayout();
		updateBalance();
	}	

	private class TicketTypeAdapter extends ArrayAdapter<String> {

		public TicketTypeAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
			super(context, resource, textViewResourceId, objects);
			setDropDownViewResource(R.layout.account_age_item);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return super.getView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			final View view = super.getDropDownView(position, convertView, parent);
			if(mTicketType.getSelectedItemPosition() == position){
				ImageView ticketSelectImg = (ImageView)view.findViewById(R.id.iv_ticket_selected);
				ticketSelectImg.setVisibility(View.VISIBLE);
			}
			view.setOnClickListener(null);
			view.setClickable(false);
			return view;
		}
	}

	public void updateBalance() {
		AccountManager.getInstance().queryBalance(getContext(), new OnQueryBalanceListener() {

			@Override
			public void onResult(int code, int balance, int balanceQuan) {
				if (code == StatusCode.OPERATION_SUCCESS) {
					mRubbitCurrency.setText(String.valueOf(balance));
					mRubbitTicket.setText(String.valueOf(balanceQuan));
					PreferenceUtils.saveRabbitCoinBalance(balance);
					PreferenceUtils.saveRabbitTicketBalance(balanceQuan);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rabbitRecharge:
			MobclickAgent.onEvent(getContext(), "rabbit_recharge");
			AccountManager.getInstance().gotoCharge((Activity)getContext(), true);
			break;
		case R.id.rechargeConfirm:
			showRechargeDialog();
			break;
		case R.id.ticketType:
			mTicketType.performClick();
			break;
		default:
			break;
		}
	}

	private void showRechargeDialog() {
		String num = mTicketNum.getText().toString();

		if(!NetworkUtils.isNetworkConnected(getContext())) {
			Toast.makeText(getContext(), getContext().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			return;
		}

		if(TextUtils.isEmpty(num)) {
			Toast.makeText(getContext(), getContext().getString(R.string.recharge_input_not_empty), Toast.LENGTH_SHORT).show();
			return;
		}

		String regEx = "^[A-Za-z0-9]+$";
		Pattern p = Pattern.compile(regEx);
		Matcher passwordMatcher = p.matcher(num);

		if(!passwordMatcher.matches()) {
			Toast.makeText(getContext(), getContext().getString(R.string.recharge_input_contain_illegality), Toast.LENGTH_SHORT).show();
			return;
		}

		final ConfirmDialog confirmDialog = new ConfirmDialog(getContext());
		if(mTicketTypeValue == 0) {
			confirmDialog.setPrompt(getContext().getString(R.string.recharge_rabbit_ticket));
			confirmDialog.setCancelText(getContext().getString(R.string.recharge_change_account));
		} else {
			confirmDialog.setPrompt(getContext().getString(R.string.recharge_arm_ticket_recharge_title));
			confirmDialog.setCancelText(getContext().getString(R.string.cancel));
		}
		confirmDialog.setMsgTextSize(16);
		confirmDialog.setMsg(getContext().getString(R.string.recharge_account_number, AccountManager.getInstance().getAccount(getContext())));
		confirmDialog.setCancelClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
				if(mTicketTypeValue == 0) {
					//TODO 未开发
//					skipOneKeyLogin(); 
				}
			}
		});
		confirmDialog.setConfirmClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("type", (mTicketTypeValue == 0 ? "兔兔礼券" : "卡槽礼券"));
				MobclickAgent.onEvent(getContext(), "slot_rabbit_ticket_recharge", map);
				recharge();
			}
		});
		confirmDialog.show();
	}
	
	private void dismissDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	private void showDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getContext());
			mProgressDialog.setMessage("");
			mProgressDialog.setCancelable(true);
		}
		mProgressDialog.show();
	}

	private void recharge() {
		if(mRechargeTask != null) {
			mRechargeTask.cancel(true);
			mRechargeTask = null;
		}
		mRechargeTask = new RechargeTask();
		mRechargeTask.execute();
	}

	private class RechargeTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected void onPreExecute() {
			showDialog();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params) {
			String num = mTicketNum.getText().toString();

			try {
				if (mTicketTypeValue == 0) {
					mResultCount = mGameInfoHub.rechargeRabbitTicket(num, "");
				} else {
					int[] result = mGameInfoHub.rechargeSlotTicket(num);
					mResultCount = result[0];
					mExpiredDays = result[1];
				}
				return getContext().getString(R.string.recharge_succeed);
			} catch (InfoSourceException e) {
				if(e.getMessage().equals(InfoSourceException.MSG_RABBIT_TICKET_USED_ERROR)) {
					return getContext().getString(R.string.recharge_rabbit_ticket_used);
				} else if(e.getMessage().equals(InfoSourceException.MSG_RABBIT_TICKET_NOT_EXIST_ERROR)) {
					return getContext().getString(R.string.recharge_rabbit_ticket_not_exist);
				} else if(e.getMessage().equals(InfoSourceException.MSG_RABBIT_TICKET_PASSWORD_WRONG_ERROR)) {
					return getContext().getString(R.string.recharge_rabbit_ticket_password_wrong);
				} else if(e.getMessage().equals(InfoSourceException.MSG_RABBIT_TICKET_TYPE_MATCH_ERROR)) {
					return getContext().getString(R.string.recharge_rabbit_ticket_type_match);
				} else if(e.getMessage().equals(InfoSourceException.MSG_RABBIT_TICKET_ONE_BIND_ERROR)) {
					return getContext().getString(R.string.recharge_rabbit_ticket_one_bind);
				} else if (e.getMessage().equals(InfoSourceException.MSG_SLOT_TICKET_NOT_EXIST_ERROR)) {
					return getContext().getString(R.string.recharge_slot_ticket_not_exist);
				} else if (e.getMessage().equals(InfoSourceException.MSG_SLOT_TICKET_ONE_BIND_ERROR)) {
					return getContext().getString(R.string.recharge_slot_ticket_one_bind);
				} else if (e.getMessage().equals(InfoSourceException.MSG_SLOT_TICKET_TYPE_MATCH_ERROR)) {
					return getContext().getString(R.string.recharge_slot_ticket_type_match);
				} else if (e.getMessage().equals(InfoSourceException.MSG_SLOT_TICKET_USED_ERROR)) {
					return getContext().getString(R.string.recharge_slot_ticket_used);
				} else if (e.getMessage().equals(InfoSourceException.MSG_IMEI_NOT_KNOWN)) {
					return getContext().getString(R.string.recharge_imei_unknown);
				} else if (e.getMessage().equals(InfoSourceException.MSG_ACCOUNT_OUTDATE)) {
					return getContext().getString(R.string.personal_outdate);
				}
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String msg) {
			if(!isCancelled()) {
				dismissDialog();
				if(msg.equals(getContext().getString(R.string.recharge_succeed))) {
					mTicketType.setSelection(0);
					mTicketNum.setText("");
					updateBalance();
					Intent intent = new Intent(getContext(), AccountTicketRechargeDoneActivity.class);
					intent.putExtra("type", mTicketTypeValue);
					if (mTicketTypeValue != 0) {
						intent.putExtra("expired", mExpiredDays);
					}
					intent.putExtra("count", mResultCount);
					SoundPoolManager.instance(getContext()).play(SoundPoolManager.SOUND_ENTER);
					getContext().startActivity(intent);
				} else {
					Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
				}
			}
			super.onPostExecute(msg);
		}
	}

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(mRabbitRecharge,mRechargeConfirm,mTicketNum,mTicketType);
	}

	@Override
	public boolean onSunKey() {
		if (mRabbitRecharge.hasFocus()) {
			onClick(mRabbitRecharge);
		} else if (mRechargeConfirm.hasFocus()) {
			onClick(mRechargeConfirm);
		} else if (mTicketType.hasFocus()) {
			onClick(mTicketType);
		}
		return super.onSunKey();
	}
}
