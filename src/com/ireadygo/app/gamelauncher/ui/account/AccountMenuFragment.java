package com.ireadygo.app.gamelauncher.ui.account;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.menu.MenuItem;

public class AccountMenuFragment extends BaseMenuFragment {

	public AccountMenuFragment(Activity activity) {
		super(activity);
		initCoordinateParams(Config.Menu.INIT_X, Config.Menu.INIT_Y);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.account_menu_fragment, container, false);
		MenuItem personalCenterMenu = (MenuItem) view.findViewById(R.id.account_menu_personal);
		MenuItem noticeMenu = (MenuItem) view.findViewById(R.id.account_menu_notice);
		MenuItem rechargeMenu = (MenuItem) view.findViewById(R.id.account_menu_recharge);

		addMenuItem(personalCenterMenu, new AccountPersonalFragment(getRootActivity(), this));
		addMenuItem(noticeMenu, new AccountNoticeFragment(getRootActivity(), this));
		addMenuItem(rechargeMenu, new AccountRechargeFragment(getRootActivity(), this));
		return view;
	}

}
