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
import com.ireadygo.app.gamelauncher.ui.store.category.CategoryFragment;
import com.ireadygo.app.gamelauncher.ui.store.collection.CollectionFragment;
import com.ireadygo.app.gamelauncher.ui.store.storemanager.StoreManagerContentFragment;

public class StoreMenuFragment extends BaseMenuFragment {

	public StoreMenuFragment(Activity activity) {
		super(activity);
		initCoordinateParams(Config.Menu.INIT_X, Config.Menu.INIT_Y);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.store_menu_fragment, container, false);
		MenuItem searchMenu = (MenuItem) view.findViewById(R.id.store_menu_search);
		MenuItem categoryMenu = (MenuItem) view.findViewById(R.id.store_menu_category);
		MenuItem collectionMenu = (MenuItem) view.findViewById(R.id.store_menu_collection);
		MenuItem appMenu = (MenuItem) view.findViewById(R.id.store_menu_app);
		MenuItem managerMenu = (MenuItem) view.findViewById(R.id.store_menu_manager);

		addMenuItem(searchMenu, new StoreFragment(getRootActivity(), this));
		addMenuItem(categoryMenu, new CategoryFragment(getRootActivity(), this));
		addMenuItem(collectionMenu, new CollectionFragment(getRootActivity(), this));
		addMenuItem(appMenu, new GameFragment(getRootActivity(), this));
		addMenuItem(managerMenu, new StoreManagerContentFragment(getRootActivity(), this));
		return view;
	}

}
