package com.ireadygo.app.gamelauncher.ui.settings;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.mygame.utils.Utilities;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.listview.anim.AnimationAdapter;
import com.ireadygo.app.gamelauncher.ui.menu.MenuFragment;
import com.ireadygo.app.gamelauncher.ui.settings.adapter.SettingsAdapter;
import com.ireadygo.app.gamelauncher.ui.settings.adapter.SettingsAdapter.ViewHolder;
import com.ireadygo.app.gamelauncher.ui.settings.adapter.SettingsItemEntity;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemSelectedListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.SettingsIconView;

@SuppressLint("ValidFragment")
public class SettingsFragment extends BaseContentFragment {

	private static final String TAG = "SettingsFragment";
	protected HListView mHListView;

	protected boolean mIsAttach;
	private boolean mIsViewDestory = false;
	private SettingsAdapter mSettingsAdapter;
	private Activity mActivity;

	public SettingsFragment(Activity activity, MenuFragment menuFragment) {
		super(activity, menuFragment);
		mActivity = activity;
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mHListView = (HListView) view.findViewById(R.id.h_listview_settings);
		mHListView.setOnItemClickListener(mOnItemClickListener);
		mSettingsAdapter = new SettingsAdapter(getRootActivity(), mHListView, initData());
		mHListView.setAdapter(mSettingsAdapter.toAnimationAdapter());
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
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_wallpaper_selector),
				getResources().getString(R.string.settings_wallpaper), SettingsIntentAction.WALL_PAPER));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_help_selector),
				getResources().getString(R.string.settings_help), null));
		settingsList.add(new SettingsItemEntity(getResources().getDrawable(R.drawable.settings_secure_selector),
				getResources().getString(R.string.settings_more), SettingsIntentAction.SETTINGS));
		return settingsList;
	}

	public void notifyDataSet() {
		if (null != mHListView) {
			AnimationAdapter animAdapter = (AnimationAdapter) mHListView.getAdapter();
			if (null != animAdapter) {
				BaseAdapter appAdapter = animAdapter.getDecoratedBaseAdapter();
				appAdapter.notifyDataSetChanged();
			}
		}
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mSettingsAdapter != null) {
				SettingsItemEntity entity = (SettingsItemEntity) mSettingsAdapter.getItem(position);
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

	// @Override
	// public void onResume() {
	// super.onResume();
	// if(mIsViewDestory){
	// mHListView.setSelectionFromLeft(0, mHListView.getPaddingLeft());
	// mIsViewDestory = false;
	// }
	// }

	// @Override
	// public void onDestroyView() {
	// super.onDestroyView();
	// mIsViewDestory = true;
	// }

	public static class SettingsIntentAction {
		public static final String SYSTEM_UPGRADE = "com.ireadygo.app.systemupgrade.activity.UpgradeHomeActivity";
		public static final String PERSONALIZED = "com.ireadygo.app.screencapture.prefactivity";
		public static final String LANGUAGE = Settings.ACTION_LOCALE_SETTINGS;
		public static final String STORE = Settings.ACTION_INTERNAL_STORAGE_SETTINGS;
		public static final String SECURE = Settings.ACTION_SECURITY_SETTINGS;
		public static final String HELP = "";
		public static final String WIFI = Settings.ACTION_WIFI_SETTINGS;
		public static final String SETTINGS = Settings.ACTION_SETTINGS;
		public static final String WALL_PAPER = Intent.ACTION_SET_WALLPAPER;
	}

	@Override
	protected boolean isCurrentFocus() {
		return mHListView.hasFocus();
	}

	@Override
	public boolean onSunKey() {
		View selectedView = mHListView.getSelectedView();
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

	@Override
	public Animator outAnimator(AnimatorListener listener) {
		if(mSettingsAdapter == null){
			return null;
		}
		return mSettingsAdapter.outAnimator(listener);
	}

	@Override
	public int getOutAnimatorDuration() {
		return mSettingsAdapter.getOutAnimatorDuration();
	}

	private void setWallPaper() {
		final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
		Intent chooser = Intent.createChooser(pickWallpaper, "chooser_wallpaper");
		mActivity.startActivity(chooser);
	}
}