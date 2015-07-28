package com.ireadygo.app.gamelauncher.ui.store.storemanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.DownloadListener;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.GameManagerException;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.InstallListener;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.UninstallListener;
import com.ireadygo.app.gamelauncher.appstore.manager.UpdateManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.menu.ImageTextMenu;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;

public class StoreManagerContentFragment extends BaseContentFragment implements OnClickListener, OnFocusChangeListener {
	private HMultiListView mMultiListView;
	private StatisticsTitleView mTitleLayout;
	private ImageTextMenu mDldMenuItem;
	private ImageTextMenu mUpgradeMenuItem;
	private ImageTextMenu mInstalledMenuItem;

	private GameManager mGameManager;
	private GameManagerType mManagerType = GameManagerType.DOWNLOAD;

	private List<AppEntity> mDownloadApps = new ArrayList<AppEntity>();
	private StoreManagerAdapter mDownloadAdapter;

	private List<AppEntity> mUpgradeApps = new ArrayList<AppEntity>();
	private StoreManagerAdapter mUpgradeAdapter;

	private List<AppEntity> mInstalledApps = new ArrayList<AppEntity>();
	private StoreManagerInstalledAdapter mInstalledAdapter;

	public StoreManagerContentFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
		mGameManager = GameLauncher.instance().getGameManager();
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mGameManager.addDownloadListener(mDownloadListener);
		mGameManager.addInstallListener(mInstallListener);
		mGameManager.addUninstallListener(mUninstallListener);
		IntentFilter filter = new IntentFilter(UpdateManager.ACTION_UPDATABLE_NOTIFICATION);
		LocalBroadcastManager.getInstance(getRootActivity()).registerReceiver(mUpdateReceiver, filter);

		View view = inflater.inflate(R.layout.store_manager_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	public void onDestoryView() {
		super.onDestoryView();
		mGameManager.removeDownloadListener(mDownloadListener);
		mGameManager.removeInstallListener(mInstallListener);
		mGameManager.removeUninstallListener(mUninstallListener);
		LocalBroadcastManager.getInstance(getRootActivity()).unregisterReceiver(mUpdateReceiver);
		mDownloadApps.clear();
		mDownloadAdapter = null;
		mUpgradeApps.clear();
		mUpgradeAdapter = null;
		mInstalledApps.clear();
		mInstalledAdapter = null;
		mManagerType = GameManagerType.DOWNLOAD;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_WATER, TipFlag.FLAG_TIPS_MOON);
		mTitleLayout = (StatisticsTitleView) view.findViewById(R.id.store_manager_title_layout);

		mMultiListView = (HMultiListView) view.findViewById(R.id.manager_viewpager);
		mMultiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AppEntity app = null;
				switch (mManagerType) {
				case DOWNLOAD:
					if (position > 0 && position < mDownloadApps.size()) {
						app = mDownloadApps.get(position);
					}
					break;
				case UPGRADE:
					if (position > 0 && position < mUpgradeApps.size()) {
						app = mUpgradeApps.get(position);
					}
					break;
				case INSTALLED:
					if (position > 0 && position < mInstalledApps.size()) {
						app = mInstalledApps.get(position);
					}
					break;
				}
				if (app != null) {
					operatorItem(app);
				}
			}
		});

		mDldMenuItem = (ImageTextMenu) view.findViewById(R.id.manager_download);
		mDldMenuItem.setOnFocusChangeListener(this);
		mDldMenuItem.setOnClickListener(this);

		mUpgradeMenuItem = (ImageTextMenu) view.findViewById(R.id.manager_upgrade);
		mUpgradeMenuItem.setOnFocusChangeListener(this);
		mUpgradeMenuItem.setOnClickListener(this);

		mInstalledMenuItem = (ImageTextMenu) view.findViewById(R.id.manager_installed);
		mInstalledMenuItem.setOnFocusChangeListener(this);
		mInstalledMenuItem.setOnClickListener(this);

		updateLayoutByType(mManagerType);

		newAdapter();
		setAdapter(mManagerType);
		bindPagingIndicator(mMultiListView);
	}

	private void newAdapter() {
		if (mDownloadAdapter == null) {
			mDownloadAdapter = new StoreManagerAdapter(getRootActivity(), mMultiListView, mDownloadApps,
					GameManagerType.DOWNLOAD);
		}
		if (mUpgradeAdapter == null) {
			mUpgradeAdapter = new StoreManagerAdapter(getRootActivity(), mMultiListView, mUpgradeApps,
					GameManagerType.UPGRADE);
		}
		if (mInstalledAdapter == null) {
			mInstalledAdapter = new StoreManagerInstalledAdapter(getRootActivity(), mInstalledApps, 2, mMultiListView);
		}
		refreshAllData();
	}

	private void setAdapter(GameManagerType type) {
		switch (type) {
		case DOWNLOAD:
			mMultiListView.setAdapter(mDownloadAdapter);
			break;
		case UPGRADE:
			mMultiListView.setAdapter(mUpgradeAdapter);
			break;
		case INSTALLED:
			mMultiListView.setAdapter(mInstalledAdapter);
			break;
		}
	}

	private void updateLayoutByType(GameManagerType type) {
		mDldMenuItem.setSelected(false);
		mUpgradeMenuItem.setSelected(false);
		mInstalledMenuItem.setSelected(false);
		switch (type) {
		case DOWNLOAD:
			mTitleLayout.setCount(mDownloadApps.size());
			mTitleLayout.setTitle(getResources().getString(R.string.store_manager_downloading)
					+ getResources().getString(R.string.store_manager_count));
			setEmptyView(mMultiListView, R.string.game_empty_title, View.GONE, 0);
			mMultiListView.setNextFocusLeftId(R.id.manager_download);
			mMultiListView.setIsDelayScroll(true);
			mDldMenuItem.setSelected(true);
			break;
		case UPGRADE:
			mTitleLayout.setCount(mUpgradeApps.size());
			mTitleLayout.setTitle(getResources().getString(R.string.store_manager_updatable)
					+ getResources().getString(R.string.store_manager_count));
			setEmptyView(mMultiListView, R.string.update_empty_title, View.GONE, 0);
			mMultiListView.setNextFocusLeftId(R.id.manager_upgrade);
			mMultiListView.setIsDelayScroll(true);
			mUpgradeMenuItem.setSelected(true);
			break;
		case INSTALLED:
			mTitleLayout.setCount(mInstalledApps.size());
			mTitleLayout.setTitle(getResources().getString(R.string.store_manager_launchable)
					+ getResources().getString(R.string.store_manager_count));
			setEmptyView(mMultiListView, R.string.game_empty_title, View.GONE, 0);
			mMultiListView.setNextFocusLeftId(R.id.manager_installed);
			mMultiListView.setIsDelayScroll(false);
			mInstalledMenuItem.setSelected(true);
			break;
		}
	}

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(mDldMenuItem, mUpgradeMenuItem, mInstalledMenuItem, mMultiListView);
	}

	private void operatorItem(AppEntity appEntity) {
		switch (appEntity.getGameState()) {
		case INSTALLABLE:
		case INSTALLING:
			mGameManager.install(appEntity);
			break;
		case LAUNCHABLE:
			mGameManager.launch(appEntity.getPkgName());
			break;
		case UPGRADEABLE:
			mGameManager.upgrade(appEntity);
			break;
		default:
			mGameManager.download(appEntity);
			break;
		}
	}

	@Override
	public boolean onBackKey() {
		if (mMultiListView.hasFocus()) {
			switch (mManagerType) {
			case DOWNLOAD:
				mDldMenuItem.requestFocus();
				break;
			case UPGRADE:
				mUpgradeMenuItem.requestFocus();
				break;
			case INSTALLED:
				mInstalledMenuItem.requestFocus();
				break;
			}
		} else {
			getMenu().getCurrentItem().requestFocus();
		}
		return true;
	}

	@Override
	public boolean onWaterKey() {
		if (mMultiListView.hasFocus()) {
			int selectedPos = mMultiListView.getSelectedItemPosition();
			AppEntity app = null;
			if (selectedPos >= 0) {
				switch (mManagerType) {
				case DOWNLOAD:
					if (selectedPos < mDownloadApps.size()) {
						app = mDownloadApps.get(selectedPos);
					}
					break;
				case UPGRADE:
					if (selectedPos < mUpgradeApps.size()) {
						app = mUpgradeApps.get(selectedPos);
					}
					break;
				case INSTALLED:
					if (selectedPos < mInstalledApps.size()) {
						app = mInstalledApps.get(selectedPos);
					}
					break;
				}
			}
			if (app != null) {
				deleteItem(app);
				return true;
			}
		}
		return false;
	}

	private void deleteItem(final AppEntity appEntity) {
		if (appEntity.getGameState() == GameState.LAUNCHABLE || appEntity.getGameState() == GameState.UPGRADEABLE) {
			PackageUtils.unInstallApp(getRootActivity(), appEntity.getPkgName());
		} else {
			final ConfirmDialog dialog = new ConfirmDialog(getRootActivity());
			dialog.setPrompt(R.string.store_manager_delete_task_prompt).setMsg(R.string.store_manager_delete_task_msg)
					.setConfirmClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							mGameManager.delete(appEntity);
							refreshDataByType(GameManagerType.DOWNLOAD);
							refreshDataByType(GameManagerType.UPGRADE);
						}
					});
			dialog.show();
		}
	}

	private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (UpdateManager.ACTION_UPDATABLE_NOTIFICATION.equals(intent.getAction())) {
				refreshDataByType(GameManagerType.UPGRADE);
			}
		}
	};

	private int updateDldAppEntity(AppEntity app) {
		for (int i = 0; i < mDownloadApps.size(); i++) {
			AppEntity oldApp = mDownloadApps.get(i);
			if (oldApp.getPkgName().equals(app.getPkgName())) {
				oldApp.copyFrom(app);
				return i;
			}
		}
		return -1;
	}
	
	private int updateUpgradeAppEntity(AppEntity app) {
		for (int i = 0; i < mUpgradeApps.size(); i++) {
			AppEntity oldApp = mUpgradeApps.get(i);
			if (oldApp.getPkgName().equals(app.getPkgName())) {
				oldApp.copyFrom(app);
				return i;
			}
		}
		return -1;
	}

	private DownloadListener mDownloadListener = new DownloadListener() {

		@Override
		public void onDownloadStateChange(AppEntity app) {
			switch (app.getGameState()) {
			case PAUSED:
			case TRANSFERING:
			case QUEUING:
				int pos = updateDldAppEntity(app);
				int pos2 = updateUpgradeAppEntity(app);
				if ((pos >= 0 || pos2 >= 0) && mManagerType == GameManagerType.DOWNLOAD || mManagerType == GameManagerType.UPGRADE) {
					mMultiListView.notifyDataSetChanged();
				}
				break;
			default:
				refreshDataByType(GameManagerType.DOWNLOAD);
				break;
			}
		}

		@Override
		public void onDownloadProgressChange(AppEntity app) {
			int pos = updateDldAppEntity(app);
			if (pos >= 0 && mManagerType == GameManagerType.DOWNLOAD || mManagerType == GameManagerType.UPGRADE) {
				View view = mMultiListView.getItemView(pos);
				if (view != null && view instanceof StoreManagerItem) {
					StoreManagerItem item = (StoreManagerItem) view;
					item.updateProgress(app.getDownloadSize(), app.getTotalSize(), app.getDownloadSpeed());
				}
			}
		}

		@Override
		public void onDownloadItemAdd(AppEntity app) {
			refreshAllData();
		}

		@Override
		public void onDownloadError(AppEntity app, GameManagerException de) {
			int pos = updateDldAppEntity(app);
			if (pos >= 0 && mManagerType == GameManagerType.DOWNLOAD || mManagerType == GameManagerType.UPGRADE) {
				mMultiListView.notifyDataSetChanged();
			}
		}
	};

	private InstallListener mInstallListener = new InstallListener() {

		@Override
		public void onInstallStateChange(AppEntity app) {
			refreshAllData();
		}

		@Override
		public void onInstallProgressChange(AppEntity app, int progress) {

		}

		@Override
		public void onInstallError(AppEntity app, GameManagerException ie) {

		}
	};

	private UninstallListener mUninstallListener = new UninstallListener() {

		@Override
		public void onUninstallError(String pkgName, GameManagerException ge) {

		}

		@Override
		public void onUninstallComplete(String pkgName) {
			refreshDataByType(GameManagerType.INSTALLED);
		}
	};

	@Override
	public void onClick(View v) {
		GameManagerType type = mManagerType;
		boolean isChangeType = false;
		switch (v.getId()) {
		case R.id.manager_download:
			if (type != GameManagerType.DOWNLOAD) {
				type = GameManagerType.DOWNLOAD;
				isChangeType = true;
			}
			break;
		case R.id.manager_upgrade:
			if (type != GameManagerType.UPGRADE) {
				type = GameManagerType.UPGRADE;
				isChangeType = true;
			}
			break;
		case R.id.manager_installed:
			if (type != GameManagerType.INSTALLED) {
				type = GameManagerType.INSTALLED;
				isChangeType = true;
			}
			break;
		}
		if (isChangeType) {
			mManagerType = type;
			updateLayoutByType(type);
			setAdapter(type);
		}
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (hasFocus) {
			onClick(view);
		} else {
			
		}
		switch (view.getId()) {
		case R.id.manager_download:
			if (mDownloadApps.isEmpty()) {
				view.setNextFocusRightId(R.id.manager_download);
			} else {
				view.setNextFocusRightId(R.id.upHList);
			}
			break;
		case R.id.manager_upgrade:
			if (mUpgradeApps.isEmpty()) {
				view.setNextFocusRightId(R.id.manager_upgrade);
			} else {
				view.setNextFocusRightId(R.id.upHList);
			}
			break;
		case R.id.manager_installed:
			if (mInstalledApps.isEmpty()) {
				view.setNextFocusRightId(R.id.manager_installed);
			} else {
				view.setNextFocusRightId(R.id.upHList);
			}
			break;
		}
	}

	private void refreshAllData() {
		refreshDataByType(GameManagerType.DOWNLOAD);
		refreshDataByType(GameManagerType.UPGRADE);
		refreshDataByType(GameManagerType.INSTALLED);
	}

	private void refreshDataByType(GameManagerType type) {
		new LoadDataThread(type).start();
	}

	private class LoadDataThread extends Thread {
		private static final int WHAT_LOAD_DATA_SUCCESS = 1;
		private GameManagerType mInternalType = GameManagerType.DOWNLOAD;

		private Handler mMainHandler = new Handler(getRootActivity().getMainLooper()) {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WHAT_LOAD_DATA_SUCCESS:
					updateLayoutByType(mManagerType);
					if (mMultiListView != null && mManagerType == mInternalType) {
						mMultiListView.notifyDataSetChanged();
					}
					break;
				default:
					break;
				}
			};
		};

		public LoadDataThread(GameManagerType type) {
			this.mInternalType = type;
		}

		@Override
		public void run() {
			List<AppEntity> apps = null;
			switch (mInternalType) {
			case DOWNLOAD:
				apps = GameData.getInstance(getRootActivity()).getDownloadGames();
				mDownloadApps.clear();
				if (apps != null) {
					mDownloadApps.addAll(apps);
				}
				break;
			case UPGRADE:
				apps = GameData.getInstance(getRootActivity()).getUpdateAbleGames(AppEntity.CAN_UPGRADE);
				mUpgradeApps.clear();
				if (apps != null) {
					mUpgradeApps.addAll(apps);
				}
				break;
			case INSTALLED:
				apps = GameData.getInstance(getRootActivity()).getLauncherAbleGames();
				mInstalledApps.clear();
				if (apps != null) {
					mInstalledApps.addAll(apps);
				}
				break;
			}
			mMainHandler.sendEmptyMessage(WHAT_LOAD_DATA_SUCCESS);
		}
	}
	
	public enum GameManagerType {
		DOWNLOAD, UPGRADE, INSTALLED
	}
}
