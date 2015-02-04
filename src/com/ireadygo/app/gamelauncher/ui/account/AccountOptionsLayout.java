package com.ireadygo.app.gamelauncher.ui.account;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.base.BaseOptionsLayout;
import com.ireadygo.app.gamelauncher.ui.base.OptionsItem;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;

public class AccountOptionsLayout extends BaseOptionsLayout {
	private OptionsItem mPersonalBtn;
	private OptionsItem mRechargeBtn;

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
		
		mPersonalBtn = (OptionsItem) findViewById(R.id.accountPersonalBtn);
		mRechargeBtn = (OptionsItem) findViewById(R.id.accountRechargeBtn);

		initOptionButton(mPersonalBtn);
		initOptionButton(mRechargeBtn);
	}

	public void requestOptionsFocusByTag(Destination destination) {
		OptionsItem optionsItem = getStoreOptionsItemByTag(destination);
		optionsItem.requestFocus();
	}

	private OptionsItem getStoreOptionsItemByTag(Destination destination) {
		switch (destination) {
		case ACCOUNT_PERSONAL:
			return mPersonalBtn;
		case ACCOUNT_RECHARGE:
			return mRechargeBtn;
		default:
			return mPersonalBtn;
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
