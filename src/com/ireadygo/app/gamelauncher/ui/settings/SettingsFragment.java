package com.ireadygo.app.gamelauncher.ui.settings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.game.utils.Utilities;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

@SuppressLint("ValidFragment")
public class SettingsFragment extends BaseContentFragment {

	private static final String TAG = "SettingsFragment";
	private static final String FLAG_ETHERNET = "ETHERNET";
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
		mHMultiListView.setIsDelayScroll(false);
		mHMultiListView.setOnItemClickListener(mOnItemClickListener);

		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
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

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_keyboard_selector),
				getResources().getString(R.string.settings_keyboard), SettingsIntentAction.KEYBOARD));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_network_selector),
				getResources().getString(R.string.settings_network), SettingsIntentAction.NETWORK));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_language_selector),
				getResources().getString(R.string.settings_language), SettingsIntentAction.LANGUAGE));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_display_selector),
				getResources().getString(R.string.settings_display), SettingsIntentAction.DISPLAY));

//		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_brightness_selector),
//				getResources().getString(R.string.settings_brightness), SettingsIntentAction.BRIGHTNESS));
		
		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_bluetooth_selector),
				getResources().getString(R.string.settings_bluetooth), SettingsIntentAction.BLUTOOTH));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_wallpaper_selector),
				getResources().getString(R.string.settings_wallpaper), SettingsIntentAction.WALL_PAPER));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_hdmi_selector),
				getResources().getString(R.string.settings_hdmi), SettingsIntentAction.HDMI));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_help_selector),
				getResources().getString(R.string.settings_help), SettingsIntentAction.HELP));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_about_selector),
				getResources().getString(R.string.settings_about), SettingsIntentAction.ABOUT));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_reset_selector),
				getResources().getString(R.string.settings_reset), SettingsIntentAction.RESET));
		
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

						if(SettingsIntentAction.AP.equals(action)
								|| SettingsIntentAction.RESET.equals(action)) {
							skipSettings(action);
							return;
						}

						if(SettingsIntentAction.BRIGHTNESS.equals(action)) {
							UserHandle userHandle = reflectUserHandle();
							if(userHandle != null) {
								mActivity.sendBroadcastAsUser(new Intent(action), userHandle);
							}
							return;
						}

						if (SettingsIntentAction.WX.equals(action)) {
							Intent wxIntent = new Intent(action);
							wxIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
							mActivity.startActivity(wxIntent);
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

	private UserHandle reflectUserHandle() {
		try {
			Class cls = UserHandle.class;  
			Class[] paramTypes = { int.class };  
			Object[] params = { -3 };  
			Constructor con = cls.getConstructor(paramTypes);
			UserHandle userHandle = (UserHandle) con.newInstance(params);
			return userHandle;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}  
		return null;
	}

	public static class SettingsIntentAction {
		public static final String SYSTEM_UPGRADE = "com.ireadygo.app.systemupgrade.activity.UpgradeHomeActivity";
		public static final String HELP = "com.ireadygo.app.gamelauncher.ui.activity.HelperActivity";
		public static final String HANDLE = "com.ireadygo.app.devicemanager.ui.HandlesBattery";
		public static final String WX = "com.ireadygo.app.gamelauncher.WeiXinQRcodeActivity";

		public static final String WIFI = Settings.ACTION_WIFI_SETTINGS;
		public static final String BLUTOOTH = Settings.ACTION_BLUETOOTH_SETTINGS;
		public static final String LANGUAGE = Settings.ACTION_LOCALE_SETTINGS;
		public static final String SETTINGS = Settings.ACTION_SETTINGS;
		public static final String ABOUT = Settings.ACTION_DEVICE_INFO_SETTINGS;
		public static final String TIME = Settings.ACTION_DATE_SETTINGS;
		public static final String KEYBOARD = Settings.ACTION_INPUT_METHOD_SETTINGS;

		public static final String WALL_PAPER = Intent.ACTION_SET_WALLPAPER;
		public static final String HDMI = "android.settings.HDMI_SETTINGS";
		public static final String BRIGHTNESS = "android.intent.action.SHOW_BRIGHTNESS_DIALOG";
		public static final String RESET = "com.android.settings.Settings$PrivacySettingsActivity";
		public static final String DISPLAY = "com.nvidia.settings.MIRACAST_SETTINGS";
		public static final String NETWORK = Settings.ACTION_WIRELESS_SETTINGS;
		public static final String AP = "com.android.settings.Settings$TetherSettingsActivity";
	}

	@Override
	protected boolean isCurrentFocus() {
		return mHMultiListView.isCurrentFocus();
	}

	@Override
	public boolean onSunKey() {
		View selectedView = mHMultiListView.getSelectedView();
		if (selectedView != null) {
			SettingsInfo info = (SettingsInfo) selectedView.getTag();
			if (info != null) {
				String action = info.getIntentAction();
				if (!TextUtils.isEmpty(action)) {

					if(SettingsIntentAction.AP.equals(action)
							|| SettingsIntentAction.RESET.equals(action)) {
						skipSettings(action);
						return true;
					}

					if(SettingsIntentAction.BRIGHTNESS.equals(action)) {
						UserHandle userHandle = reflectUserHandle();
						if(userHandle != null) {
							mActivity.sendBroadcastAsUser(new Intent(action), userHandle);
						}
						return true;
					}

					Utilities.startActivitySafely(selectedView, new Intent(action), null);
					return true;
				}
			}
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

	private void skipSettings(String activityName) {
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings", activityName);
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		try {
			mActivity.startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mActivity, "Activity is Not found!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	private void setEthernet(Context context) {
		/*
		Intent intent = new Intent();
//		intent.setClassName("com.android.settings", "com.android.settings.Settings");
		ComponentName cm = new ComponentName("com.android.settings","com.android.settings.SubSettings");
		intent.setComponent(cm);
		intent.setAction(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.ethernet.EthernetSettings");
		try {
			mActivity.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mActivity, "Activity is Not found!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}*/
		
	}
}