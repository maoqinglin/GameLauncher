package com.ireadygo.app.gamelauncher.ui.store;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.GameFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.menu.MenuItem;
import com.ireadygo.app.gamelauncher.ui.settings.SettingsMultiFragment;

public class StoreMenuFragment extends BaseMenuFragment {

	public StoreMenuFragment(Activity activity) {
		super(activity);
		initCoordinateParams(Config.Menu.INIT_X, Config.Menu.INIT_Y);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_store, container, false);
		MenuItem searchMenu = (MenuItem) view.findViewById(R.id.store_menu_search);
		MenuItem categoryMenu = (MenuItem) view.findViewById(R.id.store_menu_category);
		MenuItem collectionMenu = (MenuItem) view.findViewById(R.id.store_menu_collection);
		MenuItem appMenu = (MenuItem) view.findViewById(R.id.store_menu_app);
		MenuItem managerMenu = (MenuItem) view.findViewById(R.id.store_menu_manager);

		addMenuItem(searchMenu, new StoreFragment(getRootActivity(), this));
		addMenuItem(categoryMenu, new StoreFragment(getRootActivity(), this));
		addMenuItem(collectionMenu, new GameFragment(getRootActivity(), this));
		addMenuItem(appMenu, new GameFragment(getRootActivity(), this));
		addMenuItem(managerMenu, new SettingsMultiFragment(getRootActivity(), this));
		return view;
	}

}
