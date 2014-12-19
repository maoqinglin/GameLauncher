package com.ireadygo.app.gamelauncher.ui.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.BindPhoneItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.FeeConfigItem;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.activity.AccountStoreActivity;
import com.ireadygo.app.gamelauncher.ui.activity.AccountTraiffActivity;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.ui.widget.CustomerEditText;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.umeng.analytics.MobclickAgent;

public class AccountFreecardLayout extends AccountBaseContentLayout implements OnClickListener, OnFocusChangeListener {

	private static final String TRAIFF_TYPE = "type";
	private static final String TRAIFF_PHONE = "phone";
	private static final String TRAIFF_BIND = "bind";
	private static final int TYPE_ERROR = -1;
	private static final int TYPE_ALL = 0;
	private static final int TYPE_PHONE_BINE = 1;
	private static final int TYPE_FEE_CONFIG = 2;

	private TextView gotoStoreBtn;
	private CustomerEditText mBindPhoneEditText;
	private TextView mBindBtn;
	private View mVoiceBtn;
	private View mFlowBtn;
	private View mSmsBtn;

	private TextView mVoicePacketContent;
	private TextView mVoicePacketPay;
	private TextView mFlowPackageContent;
	private TextView mFlowPackagePay;
	private TextView mSmsPackageContent;
	private TextView mSmsPackagePay;
	private String mBindPhoneNum = "";
	private String mPhoneNum = "";
	private BindPhoneItem mBindPhoneItem;
	private List<FeeConfigItem> mFeeConfigItems = new ArrayList<FeeConfigItem>();

	private ProgressDialog mProgressDialog;
	private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);
	private boolean isBind;

	public AccountFreecardLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public AccountFreecardLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AccountFreecardLayout(Context context, int layoutTag) {
		super(context, layoutTag);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.account_freecard, this, true);

		initView();
		mBindPhoneNum = PreferenceUtils.getBindPhoneNum();
		mPhoneNum = PreferenceUtils.getPhoneNum();
		updatePhoneBindUI();
		getData(TYPE_ALL);
	}

	private void initView() {
		mBindPhoneEditText = (CustomerEditText) findViewById(R.id.freecardPhoneNumber);
		mVoicePacketContent = (TextView) findViewById(R.id.voicePackageContent);
		mVoicePacketPay = (TextView) findViewById(R.id.voicePackagePay);
		mFlowPackageContent = (TextView) findViewById(R.id.flowPackageContent);
		mFlowPackagePay = (TextView) findViewById(R.id.flowPackagePay);
		mSmsPackageContent = (TextView) findViewById(R.id.smsPackageContent);
		mSmsPackagePay = (TextView) findViewById(R.id.smsPackagePay);

		mBindBtn = (TextView) findViewById(R.id.bindPhoneNumber);
		mBindBtn.setOnClickListener(this);
		mBindBtn.setOnFocusChangeListener(this);

		mVoiceBtn = findViewById(R.id.voicePackage);
		mVoiceBtn.setOnClickListener(this);
		mVoiceBtn.setOnFocusChangeListener(mPackageFocusChangeListener);

		mFlowBtn = findViewById(R.id.flowPackage);
		mFlowBtn.setOnClickListener(this);
		mFlowBtn.setOnFocusChangeListener(mPackageFocusChangeListener);

		mSmsBtn = findViewById(R.id.smsPackage);
		mSmsBtn.setOnClickListener(this);
		mSmsBtn.setOnFocusChangeListener(mPackageFocusChangeListener);

		gotoStoreBtn = (TextView) findViewById(R.id.inputFreecardOffice);
		gotoStoreBtn.setOnClickListener(this);
		gotoStoreBtn.setOnFocusChangeListener(this);
	}

	@Override
	protected void refreshLayout() {
		updatePhoneBindUI();
		getData(TYPE_ALL);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			// if(!isAdded()) {
			// return;
			// }

			switch (msg.what) {
			case TYPE_PHONE_BINE:
				updatePhoneBindUI();
				break;

			case TYPE_FEE_CONFIG:
				updateFeeConfigUI();
				break;

			case TYPE_ERROR:
				String error = (String) msg.obj;
				if (InfoSourceException.MSG_ILLEGALITY_BSS_ACCOUNT_ERROR.equals(error)) {
					Toast.makeText(getContext(), getContext().getString(R.string.bind_failed_illegality_bss_account),
							Toast.LENGTH_SHORT).show();
				} else if (InfoSourceException.MSG_PHONE_ALREADY_BIND_ERROR.equals(error)) {
					Toast.makeText(getContext(), getContext().getString(R.string.bind_failed_already_bind),
							Toast.LENGTH_SHORT).show();
				} else if (InfoSourceException.MSG_PHONE_BIND_FAILED_ERROR.equals(error)) {
					Toast.makeText(getContext(), getContext().getString(R.string.bind_failed_phone_wrong),
							Toast.LENGTH_SHORT).show();
				} else if (InfoSourceException.MSG_BIND_PHONE_WITHOUT_RECHARGE_ERROR.equals(error)) {
					Toast.makeText(getContext(), getContext().getString(R.string.bind_failed_without_recharge),
							Toast.LENGTH_SHORT).show();
				} else if (InfoSourceException.MSG_NETWORK_ERROR.equals(error)) {
					Toast.makeText(getContext(), getContext().getString(R.string.no_network), Toast.LENGTH_SHORT)
							.show();
				} else if (InfoSourceException.MSG_ACCOUNT_OUTDATE.equals(error)) {
					Toast.makeText(getContext(), getContext().getString(R.string.account_outdate), Toast.LENGTH_SHORT)
							.show();
				}
				break;

			default:
				break;
			}

			dismissLoadingDialog();
		};
	};

	private void updatePhoneBindUI() {
		if (!TextUtils.isEmpty(mBindPhoneNum)) {
			isBind = true;
			mBindPhoneEditText.setText(mBindPhoneNum);
			mBindPhoneEditText.setEnabled(false);
			mBindPhoneEditText.setBackgroundColor(Color.GRAY);
			// mBindBtn.setBackgroundResource(R.drawable.account_unbind_selector);
			mBindBtn.setText(getContext().getString(R.string.phone_unbind));
		} else {
			isBind = false;
			mBindPhoneEditText.setText("");
			mBindPhoneEditText.setEnabled(true);
			mBindPhoneEditText.setBackgroundResource(R.drawable.recharge_edittext_bg_selector);
			// mBindBtn.setBackgroundResource(R.drawable.account_save_selector);
			mBindBtn.setText(getContext().getString(R.string.phone_bind));
		}
	}

	private void updateFeeConfigUI() {
		if (mFeeConfigItems.size() == 3) {
			mVoicePacketContent.setText(mActivity.getString(R.string.freecard_voice_package));
			mVoicePacketPay.setText(
					mFeeConfigItems.get(2).getIMobileFeeMoney()
					+ " "
					+ getContext().getString(R.string.rabbit_coin_or_ticket));
			mVoiceBtn.setTag(mFeeConfigItems.get(2));

			mFlowPackageContent.setText(mActivity.getString(R.string.freecard_flow_package));
			mFlowPackagePay.setText(
					mFeeConfigItems.get(0).getIMobileFeeMoney()
					+" "
					+ getContext().getString(R.string.rabbit_coin_or_ticket));
			mFlowBtn.setTag(mFeeConfigItems.get(0));

			mSmsPackageContent.setText(mActivity.getString(R.string.freecard_sms_package));
			mSmsPackagePay.setText(mFeeConfigItems.get(1).getIMobileFeeMoney()
					+ " "
					+ getContext().getString(R.string.rabbit_coin_or_ticket));
			mSmsBtn.setTag(mFeeConfigItems.get(1));
		}
	}

	private void dismissLoadingDialog() {
		if (isActivityDestoryed()) {
			return;
		}
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	private void showLoadingDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getContext());
			mProgressDialog.setMessage("");
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}

	private void getData(final int type) {
		mExecutorService.execute(new Runnable() {

			@Override
			public void run() {
				switch (type) {
				case TYPE_ALL:
					getBindPhoneData();
					getFeeConfig();
					break;

				case TYPE_PHONE_BINE:
					bindPhone();
					break;

				default:
					break;
				}
			}
		});

		if (type == TYPE_PHONE_BINE) {
			showLoadingDialog();
		}
	}

	private void bindPhone() {
		try {
			mGameInfoHub.bindTelToRabbit(mBindPhoneEditText.getText().toString());
			mBindPhoneNum = mBindPhoneEditText.getText().toString();
			PreferenceUtils.saveBindPhoneNum(mBindPhoneNum);
			sendRefreshMsg(TYPE_PHONE_BINE, null);
		} catch (InfoSourceException e) {
			e.printStackTrace();
			sendRefreshMsg(TYPE_ERROR, e.getMessage());
		}
	}

	private void getBindPhoneData() {
		try {
			mBindPhoneItem = mGameInfoHub.getBindPhoneNum();
			mBindPhoneNum = mBindPhoneItem.getBindPhoneNum();
			mPhoneNum = mBindPhoneItem.getPhoneNum();
			if (!isActivityDestoryed()) {
				PreferenceUtils.saveBindPhoneNum(mBindPhoneNum);
				PreferenceUtils.savePhoneNum(mPhoneNum);
				sendRefreshMsg(TYPE_PHONE_BINE, null);
			}
		} catch (InfoSourceException e) {
			e.printStackTrace();
			sendRefreshMsg(TYPE_ERROR, e.getMessage());
		}
	}

	private void getFeeConfig() {
		try {
			mFeeConfigItems = mGameInfoHub.getFeeConfig();
			if (!isActivityDestoryed()) {
				sendRefreshMsg(TYPE_FEE_CONFIG, null);
			}
		} catch (InfoSourceException e) {
			e.printStackTrace();
			sendRefreshMsg(TYPE_ERROR, e.getMessage());
		}
	}

	private void sendRefreshMsg(int type, String error) {
		Message msg = new Message();
		msg.what = type;
		if (!TextUtils.isEmpty(error)) {
			msg.obj = error;
		}
		mHandler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		if (!NetworkUtils.isNetworkConnected(getContext())) {
			Toast.makeText(getContext(), getContext().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.inputFreecardOffice:
			MobclickAgent.onEvent(getContext(), "gotoStore");
			intent.setClass(getContext(), AccountStoreActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			SoundPoolManager.instance(getContext()).play(SoundPoolManager.SOUND_ENTER);
			getContext().startActivity(intent);
			break;

		case R.id.bindPhoneNumber:
			if (isBind) {
				isBind = false;
				mBindPhoneEditText.setText("");
				mBindPhoneEditText.setEnabled(true);
				mBindPhoneEditText.setBackgroundResource(R.drawable.account_edittext_bg_selector);
				mBindPhoneEditText.setTextSize(getResources().getDimension(R.dimen.user_cup_text_size));
				mBindBtn.setText(getContext().getString(R.string.phone_bind));
			} else {
				if (PreferenceUtils.getFirstBindPhoneLook()) {
					PreferenceUtils.saveFirstBindPhoneLook(false);
					showBindDialog();
				} else {
					checkPhoneLegal();
				}
			}
			break;

		case R.id.voicePackage:
		case R.id.flowPackage:
		case R.id.smsPackage:
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.setClass(getContext(), AccountTraiffActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(TRAIFF_PHONE, mBindPhoneNum);
			if (!TextUtils.isEmpty(mBindPhoneNum) && !TextUtils.isEmpty(mPhoneNum)) {
				bundle.putBoolean(TRAIFF_BIND, mPhoneNum.equals(mBindPhoneNum));
			}
			if (v.getTag() != null) {
				if (v.getTag() instanceof FeeConfigItem) {
					FeeConfigItem feeConfigItem = (FeeConfigItem) v.getTag();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(TRAIFF_TYPE, feeConfigItem.getSMobileFeeName());
					MobclickAgent.onEvent(getContext(), "feeconfig_type", map);
					bundle.putParcelable(TRAIFF_TYPE, feeConfigItem);
				}
			}
			intent.putExtras(bundle);
			getContext().startActivity(intent);
			break;

		default:
			break;
		}
	}

	private void showBindDialog() {
		final ConfirmDialog confirmDialog = new ConfirmDialog(getContext());
		confirmDialog.setPrompt(getContext().getString(R.string.account_free_card_rule));
		confirmDialog.setMsg(getContext().getString(R.string.account_rule_msg));
		confirmDialog.setMsgTextGravity(Gravity.LEFT);
		confirmDialog.setMsgTextSize(15);
		confirmDialog.setConfirmText(getContext().getString(R.string.account_confirm_bind));
		confirmDialog.setCancelClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmDialog.dismiss();
			}
		});
		confirmDialog.setConfirmClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkPhoneLegal();
				confirmDialog.dismiss();
			}
		});
		confirmDialog.show();
	}

	private void checkPhoneLegal() {
		if (TextUtils.isEmpty(mBindPhoneEditText.getText().toString())) {
			Toast.makeText(getContext(), getContext().getString(R.string.bind_phone_not_empty), Toast.LENGTH_SHORT)
					.show();
			return;
		}

		if (mBindPhoneEditText.getText().length() != 11) {
			Toast.makeText(getContext(), getContext().getString(R.string.bind_phone_length_wrong), Toast.LENGTH_SHORT)
					.show();
			return;
		}

		getData(TYPE_PHONE_BINE);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			if (v.isInTouchMode()) {
				onClick(v);
			}
		}
	}

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(gotoStoreBtn, mBindPhoneEditText, mBindBtn, mVoiceBtn, mFlowBtn, mSmsBtn);
	}

	@Override
	public boolean onSunKey() {
		if (gotoStoreBtn.hasFocus()) {
			onClick(gotoStoreBtn);
		} else if (mBindBtn.hasFocus()) {
			onClick(mBindBtn);
		} else if (mVoiceBtn.hasFocus()) {
			onClick(mVoiceBtn);
		} else if (mFlowBtn.hasFocus()) {
			onClick(mFlowBtn);
		} else if (mSmsBtn.hasFocus()) {
			onClick(mSmsBtn);
		}
		return super.onSunKey();
	}

	private Animator createPackageAnimator(View packageView, AnimatorListener listener, float scale) {
		PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, scale);
		PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, scale);
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(packageView, scaleXHolder, scaleYHolder);
		if (listener != null) {
			animator.addListener(listener);
		}
		return animator;
	}

	private void doPackageSelectedAnimator(View packageView) {
		Animator animator = createPackageAnimator(packageView, null, 1);
		animator.setDuration(Config.Animator.DURATION_SELECTED);
		animator.start();
	}

	private void doPackageUnselectedAnimator(View packageView) {
		Animator animator = createPackageAnimator(packageView, null, 0.85f);
		animator.setDuration(Config.Animator.DURATION_UNSELECTED);
		animator.start();
	}

	private void setPackageSelectedBackground(View packageView) {
		switch (packageView.getId()) {
		case R.id.voicePackage:
			packageView.setBackgroundResource(R.drawable.freecard_voice_pressed);
			break;
		case R.id.flowPackage:
			packageView.setBackgroundResource(R.drawable.freecard_flow_bg_pressed);
			break;
		case R.id.smsPackage:
			packageView.setBackgroundResource(R.drawable.freecard_sms_bg_pressed);
			break;
		default:
			break;
		}
	}

	private void setPackageUnselectedBackground(View packageView) {
		switch (packageView.getId()) {
		case R.id.voicePackage:
			packageView.setBackgroundResource(R.drawable.freecard_voice_bg);
			break;
		case R.id.flowPackage:
			packageView.setBackgroundResource(R.drawable.freecard_flow_bg);
			break;
		case R.id.smsPackage:
			packageView.setBackgroundResource(R.drawable.freecard_sms_bg);
			break;
		default:
			break;
		}
	}

	private OnFocusChangeListener mPackageFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				if (v.isInTouchMode()) {
					onClick(v);
				} else {
					doPackageSelectedAnimator(v);
				}
			} else {
				doPackageUnselectedAnimator(v);
			}
		}
	};
}
