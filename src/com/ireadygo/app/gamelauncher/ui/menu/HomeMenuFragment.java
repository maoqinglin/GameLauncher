package com.ireadygo.app.gamelauncher.ui.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.AppFragment;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.GameFragment;
import com.ireadygo.app.gamelauncher.ui.settings.SettingsMultiFragment;
import com.ireadygo.app.gamelauncher.ui.store.StoreFragment;

public class HomeMenuFragment extends BaseMenuFragment {
	private MenuItem mUserMenu;
	private MenuItem mStoreMenu;
	private MenuItem mGameMenu;
	private MenuItem mAppMenu;
	private MenuItem mSettingsMenu;

	public HomeMenuFragment(Activity activity) {
		super(activity);
		initCoordinateParams(Config.Menu.INIT_X, Config.Menu.INIT_Y);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_home, container, false);
		mUserMenu = (MenuItem)view.findViewById(R.id.menu_user);
		mStoreMenu = (MenuItem) view.findViewById(R.id.menu_store);
		mGameMenu = (MenuItem) view.findViewById(R.id.menu_game);
		mAppMenu = (MenuItem) view.findViewById(R.id.menu_app);
		mSettingsMenu = (MenuItem) view.findViewById(R.id.menu_settings);
		
		addMenuItem(mUserMenu, new StoreFragment(getRootActivity(), this));
		addMenuItem(mStoreMenu, new StoreFragment(getRootActivity(), this));
		addMenuItem(mGameMenu, new GameFragment(getRootActivity(), this));
		addMenuItem(mAppMenu, new GameFragment(getRootActivity(), this));
		addMenuItem(mSettingsMenu, new SettingsMultiFragment(getRootActivity(), this));
		return view;
	}

}
