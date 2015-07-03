package com.ireadygo.app.gamelauncher.ui.menu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask.AccountInfoListener;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.ui.AppFragment;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.GameFragment;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.focus.FocusRelativeLayout;
import com.ireadygo.app.gamelauncher.ui.settings.SettingsFragment;
import com.ireadygo.app.gamelauncher.ui.store.StoreFragment;
import com.ireadygo.app.gamelauncher.ui.user.UserFragmentA;
import com.ireadygo.app.gamelauncher.ui.user.UserFragmentB;
import com.ireadygo.app.gamelauncher.ui.user.UserFragmentC;
import com.ireadygo.app.gamelauncher.ui.user.UserLoginFragment;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

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
		FocusRelativeLayout focusView = (FocusRelativeLayout) view.findViewById(R.id.focus_container);
		focusView.setBorderViewBg(R.drawable.menu_nav_focused_bg);
		focusView.setViewGroup(container); // 控制焦点顺序.
		focusView.setChildFocusChangeListener(mItemFocusChangeListener);
		mUserMenu = (MenuItem) view.findViewById(R.id.menu_user);
		mGameMenu = (MenuItem) view.findViewById(R.id.menu_game);
		mStoreMenu = (MenuItem) view.findViewById(R.id.menu_store);
		mAppMenu = (MenuItem) view.findViewById(R.id.menu_app);
		mSettingsMenu = (MenuItem) view.findViewById(R.id.menu_settings);

		addMenuItem(mUserMenu, getUserFragment());
		addMenuItem(mStoreMenu, new StoreFragment(getRootActivity(), this));
		addMenuItem(mGameMenu, new GameFragment(getRootActivity(), this));
		addMenuItem(mAppMenu, new AppFragment(getRootActivity(), this));
		addMenuItem(mSettingsMenu, new SettingsFragment(getRootActivity(), this));
		LoadOBoxTypeTask task = new LoadOBoxTypeTask();
		task.execute();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		setUserPhoto();
		MenuItem menu = getMenuItem(0);
		if (menu != null) {
			boolean isLogin = AccountManager.getInstance().isLogined(getRootActivity());
			boolean isLoginFragment = (menu.getContentFragment() instanceof UserLoginFragment);
			if ((isLogin && isLoginFragment) || (!isLogin && !isLoginFragment)) {
				updateMenuItem(0, getUserFragment());
			}
		}
	}

	private void setUserPhoto() {
		if(!AccountManager.getInstance().isLogined(getRootActivity())){
			((ImageMenu) mUserMenu).getImageView().setImageResource(R.drawable.account_photo_default_circle);
			return;
		}
		Bitmap userPhoto = GameLauncherApplication.getApplication().getUserPhoto();
		if (userPhoto != null) {
			decorateUserPhoto(userPhoto);
		} else {
			UserInfoItem infoItem = GameLauncherApplication.getApplication().getUserInfoItem();
			if (infoItem != null && !TextUtils.isEmpty(infoItem.getCPhoto())) {
				getRemotePhoto(infoItem.getCPhoto());
			} else {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						getAccountInfoAsync();
					}
				}, 500);
			}
		}
	}

	private void getAccountInfoAsync() {
		if (!NetworkUtils.isNetworkConnected(getRootActivity())) {
			Toast.makeText(getRootActivity(), getRootActivity().getString(R.string.no_network), Toast.LENGTH_SHORT)
					.show();
			return;
		}
		new AccountInfoAsyncTask(getRootActivity(), new AccountInfoListener() {

			@Override
			public void onSuccess(UserInfoItem userInfo) {
				String photoUrl = userInfo.getCPhoto();
				if (!TextUtils.isEmpty(photoUrl)) {
					getRemotePhoto(photoUrl);
				}
			}

			@Override
			public void onFailed(int code) {

			}
		}).execute();
	}

	private void getRemotePhoto(String photoUrl) {
		GameInfoHub.instance(getRootActivity()).getImageLoader()
				.displayImage(photoUrl, ((ImageMenu) mUserMenu).getImageView(), new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String arg0, View photoView, Bitmap bmp) {
						if (bmp != null) {
							// GameLauncherApplication.getApplication().setUserPhoto(bmp);
							decorateUserPhoto(bmp);
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void decorateUserPhoto(Bitmap bmp) {
		Bitmap mask = BitmapFactory.decodeResource(getRootActivity().getResources(), R.drawable.icon_mask);
		((ImageMenu) mUserMenu).getImageView().setImageBitmap(
				PictureUtil.decorateIcon(getRootActivity(), mask, bmp, mask));
	}

	private BaseContentFragment getUserFragment() {
		if (!AccountManager.getInstance().isLogined(getRootActivity())) {
			return new UserLoginFragment(getRootActivity(), this);
		}
		String type = PreferenceUtils.getOBoxType();
		if (GameLauncherConfig.OBOX_TYPE_A.equals(type)) {
			return new UserFragmentA(getRootActivity(), this);
		} else if (GameLauncherConfig.OBOX_TYPE_B.equals(type)) {
			return new UserFragmentB(getRootActivity(), this);
		} else if (GameLauncherConfig.OBOX_TYPE_C.equals(type)) {
			return new UserFragmentC(getRootActivity(), this);
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
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						updateMenuItem(0, getUserFragment());
					}
				}, 200);
			}
		}
	}

}
