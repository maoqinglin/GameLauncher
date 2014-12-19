package com.ireadygo.app.gamelauncher.ui.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.base.BaseOptionsLayout;
import com.ireadygo.app.gamelauncher.ui.base.OptionsItem;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;

public class AccountOptionsLayout extends BaseOptionsLayout {
	private OptionsItem mWealthBtn;
	private OptionsItem mPersonalBtn;
	private OptionsItem mRechargeBtn;
	private OptionsItem mFreecardBtn;

	public AccountOptionsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AccountOptionsLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AccountOptionsLayout(Context context) {
		super(context);
	}

	@Override
	protected void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.account_options_layout, this, true);
		
		mWealthBtn = (OptionsItem) findViewById(R.id.accountWealthBtn);
		mPersonalBtn = (OptionsItem) findViewById(R.id.accountPersonalBtn);
		mRechargeBtn = (OptionsItem) findViewById(R.id.accountRechargeBtn);
		mFreecardBtn = (OptionsItem) findViewById(R.id.accountFreecardBtn);

		initOptionButton(mWealthBtn);
		initOptionButton(mPersonalBtn);
		initOptionButton(mRechargeBtn);
		initOptionButton(mFreecardBtn);
	}

	public void requestOptionsFocusByTag(Destination destination) {
		OptionsItem optionsItem = getStoreOptionsItemByTag(destination);
		optionsItem.requestFocus();
	}

	private OptionsItem getStoreOptionsItemByTag(Destination destination) {
		switch (destination) {
		case ACCOUNT_WEALTH:
			return mWealthBtn;
		case ACCOUNT_PERSONAL:
			return mPersonalBtn;
		case ACCOUNT_RECHARGE:
			return mRechargeBtn;
		case ACCOUNT_FREECARD:
			return mFreecardBtn;
		default:
			return mWealthBtn;
		}
	}

	@Override
	public boolean onWaterKey() {
		return true;
	}

	@Override
	public boolean onMountKey() {
		return true;
	}
}
