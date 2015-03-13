package com.ireadygo.app.gamelauncher.ui.user;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.account.StatusCode;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.ui.widget.CustomerEditText;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.utils.DeviceUtil;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.snailgame.mobilesdk.OnQueryBalanceListener;
import com.umeng.analytics.MobclickAgent;

public class UserRechargeFragment extends BaseContentFragment implements OnClickListener, OnFocusChangeListener{

	private Spinner mTicketType;
	private int mTicketTypeValue = 0;
	private TextView mRubbitCurrency;
	private TextView mSnailPoint;
	private TextView mRabbitRecharge;
	private TextView mTicketRecharge;
	private RechargeTask mRechargeTask;
	private ProgressDialog mProgressDialog;
	private CustomerEditText mTicketNum;
	private int mResultCount = 0;
	private GameInfoHub mGameInfoHub;

	public UserRechargeFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_recharge, container, false);
		initView(view);
		init();
		return view;
	}

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(mRabbitRecharge,mTicketRecharge,mTicketType);
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(View.GONE, TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mRubbitCurrency = (TextView) view.findViewById(R.id.rabbit_currency);
		mSnailPoint = (TextView) view.findViewById(R.id.snail_point);
		mTicketType = (Spinner)view.findViewById(R.id.snail_point_select);
		mTicketNum = (CustomerEditText) view.findViewById(R.id.gift_certificate_recharge);
		mRabbitRecharge = (TextView) view.findViewById(R.id.rabbit_recharge);
		mTicketRecharge = (TextView) view.findViewById(R.id.gift_certificate_recharge_btn);
	}

	private void init() {
		mGameInfoHub = GameLauncher.instance().getGameInfoHub();
		ArrayAdapter<String> arrayAdapter = new TicketTypeAdapter(getRootActivity(), R.layout.user_recharge_spinner_item,
				R.id.ticket_type_item, this.getResources().getStringArray(R.array.recharge_ticket_types));
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
		
		mRubbitCurrency.setText(String.valueOf(PreferenceUtils.getRabbitCoinBalance()));
		mSnailPoint.setText(String.valueOf(PreferenceUtils.getRabbitTicketBalance()));
		updateBalance();

		mRabbitRecharge.setOnClickListener(this);
		mTicketRecharge.setOnClickListener(this);
		mRabbitRecharge.setOnFocusChangeListener(this);
		mTicketRecharge.setOnFocusChangeListener(this);
	}

	public void updateBalance() {
		AccountManager.getInstance().queryBalance(getRootActivity(), new OnQueryBalanceListener() {

			@Override
			public void onResult(int code, int balance, int balanceQuan) {
				if (code == StatusCode.OPERATION_SUCCESS) {
					mRubbitCurrency.setText(String.valueOf(balance));
					mSnailPoint.setText(String.valueOf(balanceQuan));
					PreferenceUtils.saveRabbitCoinBalance(balance);
					PreferenceUtils.saveRabbitTicketBalance(balanceQuan);
				}
			}
		});
	}
	private class TicketTypeAdapter extends ArrayAdapter<String> {

		public TicketTypeAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			mTicketType = (Spinner) parent;
			View view = super.getView(position, convertView, parent);
			TextView textview = (TextView) view.findViewById(R.id.ticket_type_item);
			textview.setTextAppearance(getRootActivity(), R.style.TextLabelMiddleLight);
			return view;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			final View view = super.getDropDownView(position, convertView, parent);
			TextView textview = (TextView) view.findViewById(R.id.ticket_type_item);
//			textview.setPadding(20, 0, 0, 0);
			if(mTicketType.getSelectedItemPosition() == position){
				ImageView ticketSelectImg = (ImageView)view.findViewById(R.id.iv_ticket_selected);
				ticketSelectImg.setVisibility(View.VISIBLE);
			}
			view.setOnClickListener(null);
			view.setClickable(false);
			return view;
		}
	}

	private void showRechargeDialog() {
		String num = mTicketNum.getText().toString();

		if(!NetworkUtils.isNetworkConnected(getRootActivity())) {
			Toast.makeText(getRootActivity(), getRootActivity().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			return;
		}

		if(TextUtils.isEmpty(num)) {
			Toast.makeText(getRootActivity(), getRootActivity().getString(R.string.recharge_input_not_empty), Toast.LENGTH_SHORT).show();
			return;
		}

		String regEx = "^[A-Za-z0-9]+$";
		Pattern p = Pattern.compile(regEx);
		Matcher passwordMatcher = p.matcher(num);

		if(!passwordMatcher.matches()) {
			Toast.makeText(getRootActivity(), getRootActivity().getString(R.string.recharge_input_contain_illegality), Toast.LENGTH_SHORT).show();
			return;
		}

		final ConfirmDialog confirmDialog = new ConfirmDialog(getRootActivity());
		if(mTicketTypeValue == 0) {
			confirmDialog.setPrompt(getRootActivity().getString(R.string.recharge_rabbit_ticket));
			confirmDialog.setCancelText(getRootActivity().getString(R.string.recharge_change_account));
		} else {
			confirmDialog.setPrompt(getRootActivity().getString(R.string.recharge_arm_ticket_recharge_title));
			confirmDialog.setCancelText(getRootActivity().getString(R.string.cancel));
		}
		confirmDialog.setMsgTextSize(48);
		confirmDialog.setMsg(getRootActivity().getString(R.string.recharge_confirm, AccountManager.getInstance().getAccount(getRootActivity())));
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
				map.put("type", (mTicketTypeValue == 0 ? "蜗牛点券" : "兔兔礼券"));
				MobclickAgent.onEvent(getRootActivity(), "slot_rabbit_ticket_recharge", map);
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
			mProgressDialog = new ProgressDialog(getRootActivity());
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
					mGameInfoHub.bindTicket(num, "", DeviceUtil.getMacAddr(getRootActivity()));
				} else {
					mResultCount = mGameInfoHub.rechargeRabbitTicket(num, "");
				}
				return getRootActivity().getString(R.string.recharge_succeed);
			} catch (InfoSourceException e) {
				if(e.getMessage().equals(InfoSourceException.MSG_RABBIT_TICKET_USED_ERROR)) {
					return getRootActivity().getString(R.string.recharge_rabbit_ticket_used);
				} else if(e.getMessage().equals(InfoSourceException.MSG_RABBIT_TICKET_NOT_EXIST_ERROR)) {
					return getRootActivity().getString(R.string.recharge_rabbit_ticket_not_exist);
				} else if(e.getMessage().equals(InfoSourceException.MSG_RABBIT_TICKET_PASSWORD_WRONG_ERROR)) {
					return getRootActivity().getString(R.string.recharge_rabbit_ticket_password_wrong);
				} else if(e.getMessage().equals(InfoSourceException.MSG_RABBIT_TICKET_TYPE_MATCH_ERROR)) {
					return getRootActivity().getString(R.string.recharge_rabbit_ticket_type_match);
				} else if(e.getMessage().equals(InfoSourceException.MSG_RABBIT_TICKET_ONE_BIND_ERROR)) {
					return getRootActivity().getString(R.string.recharge_rabbit_ticket_one_bind);
				} else if (e.getMessage().equals(InfoSourceException.MSG_BOX_TICKET_INVALID)) {
					getRootActivity().getString(R.string.recharge_snail_point_invalid);
				} else if (e.getMessage().equals(InfoSourceException.MSG_BOX_TICKET_ALREADY_IN_USE)) {
					getRootActivity().getString(R.string.recharge_snail_point_used);
				} else if (e.getMessage().equals(InfoSourceException.MSG_BOX_TICKET_HAS_BINDING)) {
					getRootActivity().getString(R.string.recharge_snail_point_has_bind);
				} else if (e.getMessage().equals(InfoSourceException.MSG_BOX_NOT_EXIST)) {
					getRootActivity().getString(R.string.recharge_box_not_exist);
				} else if (e.getMessage().equals(InfoSourceException.MSG_IMEI_NOT_KNOWN)) {
					return getRootActivity().getString(R.string.recharge_imei_unknown);
				} else if (e.getMessage().equals(InfoSourceException.MSG_ACCOUNT_OUTDATE)) {
					return getRootActivity().getString(R.string.personal_outdate);
				}
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(String msg) {
			if(!isCancelled()) {
				dismissDialog();
				if(msg.equals(getRootActivity().getString(R.string.recharge_succeed))) {
					mTicketType.setSelection(0);
					mTicketNum.setText("");
					updateBalance();
					SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
				} else {
					Toast.makeText(getRootActivity(), msg, Toast.LENGTH_SHORT).show();
				}
			}
			super.onPostExecute(msg);
		}
	}

	@Override
	public boolean onSunKey() {
		if (mRabbitRecharge.hasFocus()) {
			onClick(mRabbitRecharge);
		} else if (mTicketRecharge.hasFocus()) {
			onClick(mTicketRecharge);
		} else if (mTicketType.hasFocus()) {
			onClick(mTicketType);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rabbit_recharge:
			MobclickAgent.onEvent(getRootActivity(), "rabbit_recharge");
			try {
				AccountManager.getInstance().gotoCharge(getRootActivity(), true);
			} catch (Exception e) {
				Toast.makeText(getRootActivity(), R.string.user_identity_overdue, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.gift_certificate_recharge_btn:
			showRechargeDialog();
			break;
		case R.id.snail_point_select:
			mTicketType.performClick();
			break;
		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus && v.isInTouchMode()) {
			v.performClick();
		}
	}
}
