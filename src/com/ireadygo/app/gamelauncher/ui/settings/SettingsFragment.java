package com.ireadygo.app.gamelauncher.ui.settings;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.game.utils.Utilities;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.settings.SettingsItem.SettingsItemHoder;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

@SuppressLint("ValidFragment")
public class SettingsFragment extends BaseContentFragment {

	private static final String TAG = "SettingsFragment";
	protected HMultiListView mHMultiListView;

	protected boolean mIsAttach;
	private boolean mIsViewDestory = false;
	private SettingsMultiAdapter mSettingsMultiAdapter;
	private Activity mActivity;

	public SettingsFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
		mActivity = activity;
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_multi_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		mHMultiListView = (HMultiListView) view.findViewById(R.id.mutillist);
		mSettingsMultiAdapter = new SettingsMultiAdapter(getRootActivity(), initData(),2,mHMultiListView);
		mHMultiListView.setAdapter(mSettingsMultiAdapter);
		mHMultiListView.setOnItemClickListener(mOnItemClickListener);
	}

	private List<SettingsInfo> initData() {
		List<SettingsInfo> settingsList = new ArrayList<SettingsInfo>();
		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_wifi_selector),
				getResources().getString(R.string.settings_wifi), SettingsIntentAction.WIFI));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_upgrade_selector),
				getResources().getString(R.string.settings_systemupgrade), SettingsIntentAction.SYSTEM_UPGRADE));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_wx_selector),
				getResources().getString(R.string.settings_wx), SettingsIntentAction.WX));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_handle_selector),
				getResources().getString(R.string.settings_handle_battery), SettingsIntentAction.HANDLE));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_ap_selector),
				getResources().getString(R.string.settings_ap), SettingsIntentAction.AP));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_time_selector),
				getResources().getString(R.string.settings_time), SettingsIntentAction.TIME));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_bluetooth_selector),
				getResources().getString(R.string.settings_bluetooth), SettingsIntentAction.BLUTOOTH));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_keyboard_selector),
				getResources().getString(R.string.settings_keyboard), SettingsIntentAction.KEYBOARD));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_network_selector),
				getResources().getString(R.string.settings_network), SettingsIntentAction.NETWORK));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_language_selector),
				getResources().getString(R.string.settings_language), SettingsIntentAction.LANGUAGE));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_display_selector),
				getResources().getString(R.string.settings_display), SettingsIntentAction.DISPLAY));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_brightness_selector),
				getResources().getString(R.string.settings_brightness), SettingsIntentAction.BRIGHTNESS));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_wallpaper_selector),
				getResources().getString(R.string.settings_wallpaper), SettingsIntentAction.WALL_PAPER));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_hdmi_selector),
				getResources().getString(R.string.settings_hdmi), SettingsIntentAction.HDMI));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_help_selector),
				getResources().getString(R.string.settings_help), SettingsIntentAction.HELP));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_about_selector),
				getResources().getString(R.string.settings_about), SettingsIntentAction.ABOUT));

		return settingsList;
	}

	public void notifyDataSet() {
		if (mHMultiListView != null) {
			mHMultiListView.notifyDataSetChanged();
		}
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mSettingsMultiAdapter != null) {
				SettingsInfo entity = (SettingsInfo) mSettingsMultiAdapter.getItem(position);
				if (entity != null) {
					String action = entity.getIntentAction();
					if (!TextUtils.isEmpty(action)) {
						if (Intent.ACTION_SET_WALLPAPER.equals(action)) {
							setWallPaper();
							return;
						}
						try {
							Utilities.startActivitySafely(view, new Intent(action), null);
						} catch (ActivityNotFoundException e) {
							Log.e(TAG, "ActivityNotFoundException:" + e.getMessage());
						}
					}
				}
			}
		}
	};

	public static class SettingsIntentAction {
		public static final String SYSTEM_UPGRADE = "com.ireadygo.app.systemupgrade.activity.UpgradeHomeActivity";
		public static final String LANGUAGE = Settings.ACTION_LOCALE_SETTINGS;
		public static final String HELP = "com.ireadygo.app.gamelauncher.ui.activity.HelperActivity";
		public static final String WIFI = Settings.ACTION_WIFI_SETTINGS;
		public static final String SETTINGS = Settings.ACTION_SETTINGS;
		public static final String WALL_PAPER = Intent.ACTION_SET_WALLPAPER;
		public static final String HANDLE = "com.ireadygo.app.devicemanager.ui.HandlesBattery";
		public static final String HDMI = "";
		public static final String ABOUT = "";
		public static final String BRIGHTNESS = "";
		public static final String TIME = Settings.ACTION_DATE_SETTINGS;
		public static final String KEYBOARD = "";
		public static final String RESET = "";
		public static final String DISPLAY = "";
		public static final String WX = "";
		public static final String NETWORK = "";
		public static final String AP = "";
		public static final String BLUTOOTH = Settings.ACTION_BLUETOOTH_SETTINGS;
	}

	@Override
	protected boolean isCurrentFocus() {
		return mHMultiListView.isCurrentFocus();
	}

	@Override
	public boolean onSunKey() {
		View selectedView = mHMultiListView.getSelectedView();
		if (selectedView != null) {
			SettingsItemHoder holder = (SettingsItemHoder) selectedView.getTag();
//			SettingsInfo settingsItemEntity = holder.;
//			if (settingsItemEntity != null) {
//				String action = settingsItemEntity.getIntentAction();
//				if (!TextUtils.isEmpty(action)) {
//					Utilities.startActivitySafely(selectedView, new Intent(action), null);
//				}
//			}
		}
		return true;
	}

	@Override
	public boolean onMoonKey() {
		getMenu().getCurrentItem().requestFocus();
		return true;
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}

	private void setWallPaper() {
		final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
		Intent chooser = Intent.createChooser(pickWallpaper, "chooser_wallpaper");
		mActivity.startActivity(chooser);
	}
}