package com.ireadygo.app.gamelauncher.ui.settings;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.download.Network;
import com.ireadygo.app.gamelauncher.appstore.download.Network.NetworkListener;
import com.ireadygo.app.gamelauncher.game.utils.Utilities;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

@SuppressLint("ValidFragment")
public class SettingsFragment extends BaseContentFragment {

	private static final String TAG = "SettingsFragment";
	protected HMultiListView mHMultiListView;

	protected boolean mIsAttach;
	private SettingsMultiAdapter mSettingsMultiAdapter;
	private Activity mActivity;
	private Network mNetwork;
	private final InnerNetworkListener mInnerNetworkListener = new InnerNetworkListener();

	public SettingsFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
		mActivity = activity;
		mNetwork = new Network(activity);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mNetwork.addNetworkListener(mInnerNetworkListener);
		View view = inflater.inflate(R.layout.settings_multi_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		mHMultiListView = (HMultiListView) view.findViewById(R.id.mutillist);
		bindPagingIndicator(mHMultiListView);
		mSettingsMultiAdapter = new SettingsMultiAdapter(getRootActivity(), initData(),2,mHMultiListView);
		mHMultiListView.setAdapter(mSettingsMultiAdapter);
		mHMultiListView.setIsDelayScroll(false);
		mHMultiListView.setOnItemClickListener(mOnItemClickListener);

		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
	}

	private List<SettingsInfo> initData() {
		List<SettingsInfo> settingsList = new ArrayList<SettingsInfo>();
		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_network_bg),getResources().getDrawable(R.drawable.settings_ic_connection),
				getResources().getString(R.string.settings_network), "", SettingsIntentAction.NETWORK));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_systemupgrade_bg),getResources().getDrawable(R.drawable.settings_ic_update),
				getResources().getString(R.string.settings_systemupgrade), "", SettingsIntentAction.SYSTEM_UPGRADE));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_weixin_bg),getResources().getDrawable(R.drawable.settings_ic_wechat),
				getResources().getString(R.string.settings_wx), "", SettingsIntentAction.WX));
		
		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_favorite_bg),getResources().getDrawable(R.drawable.settings_ic_favorite),
				getResources().getString(R.string.settings_favorite), "", SettingsIntentAction.FAVORITE));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_help_bg),getResources().getDrawable(R.drawable.settings_ic_help),
				getResources().getString(R.string.settings_help), "", SettingsIntentAction.HELP));

		settingsList.add(new SettingsInfo(getResources().getDrawable(R.drawable.settings_about_bg),getResources().getDrawable(R.drawable.settings_ic_about),
				getResources().getString(R.string.settings_about), "",SettingsIntentAction.ABOUT));

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

						Intent intent = new Intent(action);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						try {
							Utilities.startActivitySafely(view, intent, null);
						} catch (ActivityNotFoundException e) {
							Log.e(TAG, "ActivityNotFoundException:" + e.getMessage());
						}
					}
				}
			}
		}
	};

	public static class SettingsIntentAction {
		public static final String NETWORK = "com.ireadygo.settings.network";
		public static final String SYSTEM_UPGRADE = "com.ireadygo.settings.ui.systemupgrade.start";
		public static final String WX = "com.ireadygo.app.gamelauncher.WeiXinQRcodeActivity";
		public static final String FAVORITE = "android.ireadygo.settings.PreferenceMenuActivity";
		public static final String ABOUT = "com.ireadygo.settings.about";
		public static final String HELP = "com.ireadygo.settings.help";
	}

	@Override
	protected boolean isCurrentFocus() {
		return mHMultiListView.hasFocus();
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
		chooser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		mActivity.startActivity(chooser);
	}

	@Override
	public void onDestoryView() {
		super.onDestoryView();
		mNetwork.removeNetworkListener(mInnerNetworkListener);
	}

	private final class InnerNetworkListener implements NetworkListener {

		@Override
		public void onNetworkConnected() {
			List<SettingsInfo> settingList = (List<SettingsInfo>)mSettingsMultiAdapter.getData();
			for(SettingsInfo settingInfo : settingList){
				if(SettingsIntentAction.NETWORK.equals(settingInfo.getIntentAction())){
					settingInfo.setItemIcon(getResources().getDrawable(R.drawable.settings_ic_connection));
					settingInfo.setTip("");
					break;
				}
			}
			notifyDataSet();
		}

		@Override
		public void onNetworkDisconnected() {
			List<SettingsInfo> settingList = (List<SettingsInfo>)mSettingsMultiAdapter.getData();
			for(SettingsInfo settingInfo : settingList){
				if(SettingsIntentAction.NETWORK.equals(settingInfo.getIntentAction())){
					settingInfo.setItemIcon(getResources().getDrawable(R.drawable.settings_ic_not_connection));
					settingInfo.setTip(getResources().getString(R.string.settings_network_not_connect));
					break;
				}
			}
			notifyDataSet();
		}
	}
}