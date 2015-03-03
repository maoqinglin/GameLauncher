package com.ireadygo.app.gamelauncher.ui.store.category;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.menu.MenuItem;

public class CategoryDetailMenuFragment extends BaseMenuFragment {

	public CategoryDetailMenuFragment(Activity activity) {
		super(activity);
		initCoordinateParams(Config.Menu.INIT_X, Config.Menu.INIT_Y);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.category_detail_menu_fragment, container, false);
		MenuItem slgMenu = (MenuItem) view.findViewById(R.id.category_detail_menu_slg);
		MenuItem stgMenu = (MenuItem) view.findViewById(R.id.category_detail_menu_stg);
		MenuItem pzlMenu = (MenuItem) view.findViewById(R.id.category_detail_menu_pzl);
		MenuItem rpgMenu = (MenuItem) view.findViewById(R.id.category_detail_menu_rpg);
		MenuItem sptMenu = (MenuItem) view.findViewById(R.id.category_detail_menu_spt);
		MenuItem olgMenu = (MenuItem) view.findViewById(R.id.category_detail_menu_olg);
		MenuItem simMenu = (MenuItem) view.findViewById(R.id.category_detail_menu_sim);
		MenuItem rsgMenu = (MenuItem) view.findViewById(R.id.category_detail_menu_rsg);

		addMenuItem(slgMenu, new CategoryDetailContentFragment(getRootActivity(), this,
				CategoryMultiAdapter.CATEGORY_ID_SLG));
		addMenuItem(stgMenu, new CategoryDetailContentFragment(getRootActivity(), this,
				CategoryMultiAdapter.CATEGORY_ID_STG));
		addMenuItem(pzlMenu, new CategoryDetailContentFragment(getRootActivity(), this,
				CategoryMultiAdapter.CATEGORY_ID_PZL));
		addMenuItem(rpgMenu, new CategoryDetailContentFragment(getRootActivity(), this,
				CategoryMultiAdapter.CATEGORY_ID_RPG));
		addMenuItem(sptMenu, new CategoryDetailContentFragment(getRootActivity(), this,
				CategoryMultiAdapter.CATEGORY_ID_SPT));
		addMenuItem(olgMenu, new CategoryDetailContentFragment(getRootActivity(), this,
				CategoryMultiAdapter.CATEGORY_ID_OLG));
		addMenuItem(simMenu, new CategoryDetailContentFragment(getRootActivity(), this,
				CategoryMultiAdapter.CATEGORY_ID_SIM));
		addMenuItem(rsgMenu, new CategoryDetailContentFragment(getRootActivity(), this,
				CategoryMultiAdapter.CATEGORY_ID_RSG));
		return view;
	}
}
