package com.ireadygo.app.gamelauncher.ui.account;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;

public class AccountRechargeFragment extends BaseContentFragment {

	public AccountRechargeFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.account_recharge_1, container, false);
		initView(view);
		return view;
	}

	@Override
	protected boolean isCurrentFocus() {
		// TODO Auto-generated method stub
		return false;
	}

}
