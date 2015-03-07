package com.ireadygo.app.gamelauncher.ui.menu;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.ui.AppFragment;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.GameFragment;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.personal.UserFragmentA;
import com.ireadygo.app.gamelauncher.ui.personal.UserFragmentB;
import com.ireadygo.app.gamelauncher.ui.personal.UserFragmentC;
import com.ireadygo.app.gamelauncher.ui.settings.SettingsFragment;
import com.ireadygo.app.gamelauncher.ui.store.StoreFragment;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;

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
		
		addMenuItem(mUserMenu, getUserFragment());
		addMenuItem(mGameMenu, new GameFragment(getRootActivity(), this));
		addMenuItem(mStoreMenu, new StoreFragment(getRootActivity(), this));
		addMenuItem(mAppMenu, new AppFragment(getRootActivity(), this));
		addMenuItem(mSettingsMenu, new SettingsFragment(getRootActivity(), this));

		LoadOBoxTypeTask task = new LoadOBoxTypeTask();
		task.execute();
		return view;
	}

	private BaseContentFragment getUserFragment() {
		String type = PreferenceUtils.getOBoxType();
		if (GameLauncherConfig.OBOX_TYPE_A.equals(type)) {
			return new UserFragmentA(getRootActivity(), this);
		} else if (GameLauncherConfig.OBOX_TYPE_B.equals(type)) {
			return new UserFragmentB(getRootActivity(), this);
		} else if (GameLauncherConfig.OBOX_TYPE_C.equals(type)) {
			return new UserFragmentC(getRootActivity(),this);
		}
		return new UserFragmentC(getRootActivity(), this);
	}

	private class LoadOBoxTypeTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				return GameInfoHub.instance(getRootActivity()).getSaleType(Build.SERIAL);
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
			
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				PreferenceUtils.saveOBoxType(result);
				updateMenuItem(0, getUserFragment());
			}
		}
	}

}
