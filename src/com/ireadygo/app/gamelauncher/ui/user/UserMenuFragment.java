package com.ireadygo.app.gamelauncher.ui.user;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.menu.MenuItem;

public class UserMenuFragment extends BaseMenuFragment {

	public UserMenuFragment(Activity activity) {
		super(activity);
		initCoordinateParams(Config.Menu.INIT_X, Config.Menu.INIT_Y);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.account_menu_fragment, container, false);
		MenuItem personalCenterMenu = (MenuItem) view.findViewById(R.id.account_menu_personal);
//		MenuItem noticeMenu = (MenuItem) view.findViewById(R.id.account_menu_notice);
		MenuItem rechargeMenu = (MenuItem) view.findViewById(R.id.account_menu_recharge);

		addMenuItem(personalCenterMenu, new UserPersonalFragment(getRootActivity(), this));
//		addMenuItem(noticeMenu, new UserNoticeFragment(getRootActivity(), this));
		addMenuItem(rechargeMenu, new UserRechargeFragment(getRootActivity(), this));
		return view;
	}

}
