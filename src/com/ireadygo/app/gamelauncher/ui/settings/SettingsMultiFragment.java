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
import com.ireadygo.app.gamelauncher.ui.menu.MenuFragment;
import com.ireadygo.app.gamelauncher.ui.settings.SettingsMultiAdapterTest.ViewHolder;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

@SuppressLint("ValidFragment")
public class SettingsMultiFragment extends BaseContentFragment {

	private static final String TAG = "SettingsFragment";
//	protected HListView mHListView;
	protected HMultiListView mHMultiListViewTest;

	protected boolean mIsAttach;
	private boolean mIsViewDestory = false;
//	private SettingsMultiAdapter mSettingsMultiAdapter;
	private SettingsMultiAdapterTest mSettingsMultiAdapter;
	private Activity mActivity;

	public SettingsMultiFragment(Activity activity, MenuFragment menuFragment) {
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
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mHMultiListViewTest = (HMultiListView) view.findViewById(R.id.mutillist);
		mSettingsMultiAdapter = new SettingsMultiAdapterTest(getRootActivity(), initData(),2,mHMultiListViewTest);
//		mHListView.setAdapter(mSettingsAdapter.toAnimationAdapter());
		mHMultiListViewTest.setAdapter(mSettingsMultiAdapter);
		mHMultiListViewTest.setOnItemClickListener(mOnItemClickListener);
	}

	private List<SettingsItemEntity> initData() {
		List<SettingsItemEntity> settingsList = new ArrayList<SettingsItemEntity>();
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_personalized_selector),
				getResources().getString(R.string.settings_wifi), SettingsIntentAction.WIFI));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_systemupgrade_selector),
				getResources().getString(R.string.settings_systemupgrade), SettingsIntentAction.SYSTEM_UPGRADE));
		if (!GameLauncherConfig.OBOX_VERSION) {
			settingsList.add(new SettingsItemEntity(getResources().getDrawable(
					R.drawable.settings_personalized_selector), getResources()
					.getString(R.string.settings_personalized), SettingsIntentAction.PERSONALIZED));
		}
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_language_selector),
				getResources().getString(R.string.settings_language), SettingsIntentAction.LANGUAGE));
//		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_store_selector),
//				getResources().getString(R.string.settings_store), SettingsIntentAction.STORE));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_joystick_selector),
				getResources().getString(R.string.settings_joystic_battery), SettingsIntentAction.JOYSTICK));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_help_selector),
				getResources().getString(R.string.settings_help), SettingsIntentAction.HELP));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_secure_selector),
				getResources().getString(R.string.settings_more), SettingsIntentAction.SETTINGS));

		///////////////////////////
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_personalized_selector),
				getResources().getString(R.string.settings_wifi), SettingsIntentAction.WIFI));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_systemupgrade_selector),
				getResources().getString(R.string.settings_systemupgrade), SettingsIntentAction.SYSTEM_UPGRADE));
		if (!GameLauncherConfig.OBOX_VERSION) {
			settingsList.add(new SettingsItemEntity(getResources().getDrawable(
					R.drawable.settings_personalized_selector), getResources()
					.getString(R.string.settings_personalized), SettingsIntentAction.PERSONALIZED));
		}
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_language_selector),
				getResources().getString(R.string.settings_language), SettingsIntentAction.LANGUAGE));
//		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_store_selector),
//				getResources().getString(R.string.settings_store), SettingsIntentAction.STORE));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_joystick_selector),
				getResources().getString(R.string.settings_joystic_battery), SettingsIntentAction.JOYSTICK));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_help_selector),
				getResources().getString(R.string.settings_help), SettingsIntentAction.HELP));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_secure_selector),
				getResources().getString(R.string.settings_more), SettingsIntentAction.SETTINGS));
		////////////////////////////////
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_personalized_selector),
				getResources().getString(R.string.settings_wifi), SettingsIntentAction.WIFI));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_systemupgrade_selector),
				getResources().getString(R.string.settings_systemupgrade), SettingsIntentAction.SYSTEM_UPGRADE));
		if (!GameLauncherConfig.OBOX_VERSION) {
			settingsList.add(new SettingsItemEntity(getResources().getDrawable(
					R.drawable.settings_personalized_selector), getResources()
					.getString(R.string.settings_personalized), SettingsIntentAction.PERSONALIZED));
		}
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_language_selector),
				getResources().getString(R.string.settings_language), SettingsIntentAction.LANGUAGE));
//		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_store_selector),
//				getResources().getString(R.string.settings_store), SettingsIntentAction.STORE));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_joystick_selector),
				getResources().getString(R.string.settings_joystic_battery), SettingsIntentAction.JOYSTICK));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_help_selector),
				getResources().getString(R.string.settings_help), SettingsIntentAction.HELP));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_secure_selector),
				getResources().getString(R.string.settings_more), SettingsIntentAction.SETTINGS));
		return settingsList;
	}

	public void notifyDataSet() {
//		if (null != mHListView) {
//			AnimationAdapter animAdapter = (AnimationAdapter) mHListView.getAdapter();
//			if (null != animAdapter) {
//				BaseAdapter appAdapter = animAdapter.getDecoratedBaseAdapter();
//				appAdapter.notifyDataSetChanged();
//			}
//		}
		if (mHMultiListViewTest != null) {
			mHMultiListViewTest.notifyDataSetChanged();
		}
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d("lmq", "onItemClick---position = "+position);
			if (mSettingsMultiAdapter != null) {
				SettingsItemEntity entity = (SettingsItemEntity) mSettingsMultiAdapter.getItem(position);
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
		public static final String PERSONALIZED = "com.ireadygo.app.screencapture.prefactivity";
		public static final String LANGUAGE = Settings.ACTION_LOCALE_SETTINGS;
		public static final String STORE = Settings.ACTION_INTERNAL_STORAGE_SETTINGS;
		public static final String SECURE = Settings.ACTION_SECURITY_SETTINGS;
		public static final String HELP = "com.ireadygo.app.gamelauncher.ui.activity.HelperActivity";
		public static final String WIFI = Settings.ACTION_WIFI_SETTINGS;
		public static final String SETTINGS = Settings.ACTION_SETTINGS;
		public static final String WALL_PAPER = Intent.ACTION_SET_WALLPAPER;
		public static final String JOYSTICK = "com.ireadygo.app.devicemanager.ui.HandlesBattery";
	}

	@Override
	protected boolean isCurrentFocus() {
		return mHMultiListViewTest.isCurrentFocus();
	}

	@Override
	public boolean onSunKey() {
		View selectedView = mHMultiListViewTest.getSelectedView();
		if (selectedView != null) {
			ViewHolder holder = (ViewHolder) selectedView.getTag();
			SettingsItemEntity settingsItemEntity = holder.settingsItem;
			if (settingsItemEntity != null) {
				String action = settingsItemEntity.getIntentAction();
				if (!TextUtils.isEmpty(action)) {
					Utilities.startActivitySafely(selectedView, new Intent(action), null);
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

//	@Override
//	public Animator outAnimator(AnimatorListener listener) {
//		if(mSettingsMultiAdapter == null){
//			return null;
//		}
//		return mSettingsMultiAdapter.outAnimator(listener);
//	}
//
//	@Override
//	public int getOutAnimatorDuration() {
//		return mSettingsMultiAdapter.getOutAnimatorDuration();
//	}

	private void setWallPaper() {
		final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
		Intent chooser = Intent.createChooser(pickWallpaper, "chooser_wallpaper");
		mActivity.startActivity(chooser);
	}
}