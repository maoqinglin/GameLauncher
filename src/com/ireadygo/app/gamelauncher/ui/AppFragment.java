package com.ireadygo.app.gamelauncher.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.game.adapter.AppAdapter;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherModel.Callbacks;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherSettings.Favorites;
import com.ireadygo.app.gamelauncher.game.info.FolderInfo;
import com.ireadygo.app.gamelauncher.game.info.ItemInfo;
import com.ireadygo.app.gamelauncher.game.info.ShortcutInfo;
import com.ireadygo.app.gamelauncher.game.utils.Utilities;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.HomeMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemSelectedListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;

@SuppressLint("ValidFragment")
public class AppFragment extends BaseContentFragment implements Callbacks {

	private static final int LIST_NUM = 2;
	private List<ItemInfo> mAppList = new ArrayList<ItemInfo>();

	private HMultiListView mHMultiListView;
	private StatisticsTitleView mStatisticsView;
	private AppAdapter mAppAdapter;

	public AppFragment(Activity activity, HomeMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		GameLauncherAppState.getInstance(getRootActivity()).getModel().addCallback(this);
		GameLauncherAppState.getInstance(getRootActivity()).getModel().startLoader(Favorites.APP_TYPE_APPLICATION);
		View view = inflater.inflate(R.layout.myapp_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_WATER, TipFlag.FLAG_TIPS_MOON);
		mHMultiListView = (HMultiListView) view.findViewById(R.id.mutillist);
		mStatisticsView = (StatisticsTitleView) view.findViewById(R.id.statistics_view);
		mAppAdapter = new AppAdapter(getRootActivity(), mAppList, LIST_NUM, mHMultiListView);
		mHMultiListView.setIsDelayScroll(false);
		mHMultiListView.setAdapter(mAppAdapter);
		mHMultiListView.setOnItemClickListener(mOnItemClickListener);
		mHMultiListView.setOnItemSelectedListener(mOnItemSelectedListener);
		if (!mAppList.isEmpty()) {
			mStatisticsView.setCount(mAppList.size());
		}
		bindPagingIndicator(mHMultiListView);
	}

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(mHMultiListView);
	}

	@Override
	public void bindGames(List<ItemInfo> infos) {

	}

	@Override
	public void bindApps(List<ItemInfo> infos) {
		mAppList = infos;
		if (mHMultiListView != null) {
			notifyDataSetChanged();
		}
	}

	@Override
	public void bindFolders(HashMap<Long, FolderInfo> folders) {

	}

	private void notifyDataSetChanged() {
		if (mHMultiListView != null) {
			mHMultiListView.notifyDataSetChanged();
			mStatisticsView.setCount(mAppList.size());
		}
	}

	@Override
	public void gameAddOrUpdate(ItemInfo info, boolean isAdd) {
		notifyDataSetChanged();
	}

	@Override
	public void gameRemove(ItemInfo info) {
		notifyDataSetChanged();
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ItemInfo info = (ItemInfo) mAppAdapter.getItem(position);
			doAction(view, position, info);
		}

	};

	private void doAction(View view, int position, ItemInfo gameInfo) {
		Log.d("lmq", "doAction---position = " + position + "--gameinfo = " + gameInfo);
		if (gameInfo == null) {
			return;
		}

		if (!mAppAdapter.isLongClickable() && gameInfo instanceof ShortcutInfo) {
			Utilities.startActivitySafely(view, gameInfo.getIntent(), null);
			GameData.getInstance(getRootActivity()).updateLastLaunchTime(gameInfo.packageName,
					System.currentTimeMillis());
			GameLauncherAppState.getInstance(getRootActivity()).getModel()
					.updateModifiedTime(gameInfo.packageName, System.currentTimeMillis());
			// 上报外部启动免商店游戏
			AppEntity app = GameData.getInstance(getRootActivity()).getGameByPkgName(gameInfo.packageName);
			if (app != null && !TextUtils.isEmpty(app.getAppId())) {
				StaticsUtils.openGame(app.getAppId());
			}
		}
	}

	OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Object obj = mAppAdapter.getItem(position);
			if (obj == null) {
				int prePos = parent.getSelectedItemPosition();
				parent.setSelection(prePos - 1);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}

	public boolean onMoonKey() {
		getRootActivity().findViewById(R.id.menu_app).requestFocus();
		if (mAppAdapter != null) {
			mAppAdapter.unDisplayGameDeleteView();
		}
		return true;
	}

	public boolean onWaterKey() {
		// 卸载游戏
		ItemInfo item = getCurrentSelectedItem();
		if (item != null) {
			PackageUtils.unInstallApp(getRootActivity(), item.packageName);
			return true;
		}
		return false;
	}

	private ItemInfo getCurrentSelectedItem() {
		ItemInfo info = null;
		if (null != mHMultiListView) {
			Object selectedItem = mHMultiListView.getSelectedItem();
			if (null != selectedItem && selectedItem instanceof ItemInfo) {
				info = (ItemInfo) selectedItem;
			}
		}
		return info;
	}
}