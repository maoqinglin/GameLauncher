package com.ireadygo.app.gamelauncher.ui.store.gamemanager;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.DownloadListener;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.GameManagerException;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.InstallListener;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.UninstallListener;
import com.ireadygo.app.gamelauncher.appstore.manager.UpdateManager;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.store.StoreBaseContentLayout;
import com.ireadygo.app.gamelauncher.ui.store.StoreDetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.gamemanager.DownloadManageAdapter.DldManagerViewHolder;
import com.ireadygo.app.gamelauncher.ui.store.gamemanager.DownloadManageAdapter.OnDataRefreshListener;
import com.ireadygo.app.gamelauncher.ui.store.gamemanager.DownloadManageAdapter.UpdateType;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemSelectedListener;
import com.ireadygo.app.gamelauncher.ui.widget.ExpandableHListView;
import com.ireadygo.app.gamelauncher.ui.widget.ExpandableHListView.OnChildClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.ExpandableHListView.OnGroupClickListener;

public class StoreGamesLayout extends StoreBaseContentLayout {

	private Context mContext;
	private GameData mGameData;
	private GameManager mGameManager;

	private DownloadManageAdapter mAdapter;
	private ExpandableHListView mStoreGameManagerExpandList;

	public StoreGamesLayout(Context context) {
		super(context);
	}

	public StoreGamesLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StoreGamesLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StoreGamesLayout(Context context, int layoutTag, StoreDetailActivity storeFragment) {
		super(context, layoutTag, storeFragment);
		mContext = context;
		init();
	}

	@Override
	protected void init() {
		super.init();
		LayoutInflater.from(mContext).inflate(R.layout.store_gamemanager_layout, this, true);
		mGameData = GameData.getInstance(mContext);
		mGameManager = GameLauncher.instance().getGameManager();
		GameLauncherApplication.getApplication().setCurrentActivity(getActivity());
		initExpandList();

		mGameManager.addDownloadListener(mDownloadListener);
		mGameManager.addInstallListener(mInstallListener);
		mGameManager.addUninstallListener(mUninstallListener);
		IntentFilter filter = new IntentFilter();
		LocalBroadcastManager.getInstance(mContext).registerReceiver(mUpdateReceiver, filter);
	}

	private void initExpandList() {
		List<AppEntity> downloadingList = mGameData.getDownloadGames();
		List<AppEntity> updatableList = mGameData.getUpdateAbleGames(AppEntity.CAN_UPGRADE);
		List<AppEntity> launchableList = mGameData.getLauncherAbleGames();

		mStoreGameManagerExpandList = (ExpandableHListView) findViewById(R.id.store_gamemanager_expandlist);
		mAdapter = new DownloadManageAdapter(mContext, mStoreGameManagerExpandList, downloadingList, updatableList,
				launchableList);
		mAdapter.setDataRefreshListener(mDataRefreshListener);
		mStoreGameManagerExpandList.setAdapter(mAdapter);
		mAdapter.expandAllGroup();
		mStoreGameManagerExpandList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableHListView parent, View v, int groupPosition, int childPosition,
					long id) {
				AppEntity app = mAdapter.getChildList(groupPosition).get(childPosition);
				// skipGameDetail(app.getAppId());
				return true;
			}
		});
		mStoreGameManagerExpandList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableHListView parent, View v, int groupPosition, long id) {
				return false;
			}
		});

		mStoreGameManagerExpandList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(mAdapter != null){
					if (parent.hasFocus()) {
						mAdapter.doSelectedAnimator();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				unDisplayGameDeleteView();
				if(mAdapter != null){
					mAdapter.doUnselectedAnimator(null);
				}
			}
		});

		mStoreGameManagerExpandList.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					unDisplayGameDeleteView();
				}
				if(mAdapter != null){
					if (hasFocus) {
						int selectedPosition = mStoreGameManagerExpandList.getSelectedItemPosition();
						if (selectedPosition >= 0) {
							mAdapter.doSelectedAnimator();
						}
					} else {
						if (!v.isInTouchMode()) {
							mAdapter.doUnselectedAnimator(null);
						}
					}
				}
			}
		});

		updateOptionsViewByData(downloadingList, updatableList, launchableList);

	}

	private DownloadListener mDownloadListener = new DownloadListener() {

		@Override
		public void onDownloadStateChange(AppEntity app) {
//			Log.d("liu.js", "onDownloadStateChange--app=" + app.getName() + "|state=" + app.getGameState());
			switch (app.getGameState()) {
			case UPGRADEABLE:// 删除可更新应用
			case DEFAULT:// 删除普通下载任务
				mAdapter.refreshData();
				break;
			default:
				mAdapter.updateItemView(app, UpdateType.UPDATE_STATUS);
				break;
			}
		}

		@Override
		public void onDownloadProgressChange(AppEntity app) {
			mAdapter.updateItemView(app, UpdateType.UPDATE_PROGRESS);
		}

		@Override
		public void onDownloadItemAdd(AppEntity app) {
//			Log.d("liu.js", "onDownloadItemAdd--app=" + app.getName() + "|state=" + app.getGameState());
			if (app.getIsUpdateable() == AppEntity.CAN_UPGRADE) {
				mAdapter.refreshData();
			}
		}

		@Override
		public void onDownloadError(AppEntity app, GameManagerException de) {
			mAdapter.updateItemView(app, UpdateType.UPDATE_STATUS);
		}
	};

	private InstallListener mInstallListener = new InstallListener() {

		@Override
		public void onInstallStateChange(AppEntity app) {
//			Log.d("liu.js", "onInstallStateChange--app=" + app.getName() + "|state=" + app.getGameState());
			switch (app.getGameState()) {
			case LAUNCHABLE:// 安装成功
				mAdapter.refreshData();
				break;
			default:
				mAdapter.updateItemView(app, UpdateType.UPDATE_STATUS);
				break;
			}
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
			mAdapter.refreshData();
		}
	};

	private OnDataRefreshListener mDataRefreshListener = new OnDataRefreshListener() {

		@Override
		public void onDataRefresh(List<AppEntity> downloadingList, List<AppEntity> updateList,
				List<AppEntity> launchableList) {
			updateOptionsViewByData(downloadingList, updateList, launchableList);
		}
	};

	private void updateOptionsViewByData(List<AppEntity> downloadingList, List<AppEntity> updateList,
			List<AppEntity> launchableList) {
		if (downloadingList == null || updateList == null || launchableList == null) {
			return;
		}
		View optionsView = getDldOptionsView();
		if (optionsView != null) {
			if (downloadingList.isEmpty() && updateList.isEmpty() && launchableList.isEmpty()) {
				optionsView.requestFocus();
				optionsView.setNextFocusRightId(optionsView.getId());
			} else {
				optionsView.setNextFocusRightId(R.id.store_gamemanager_expandlist);
			}
		}
	}

	private View getDldOptionsView() {
		return findViewById(R.id.store_gamemanager_expandlist);
	}

	private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (UpdateManager.ACTION_UPDATABLE_NOTIFICATION.equals(intent.getAction())) {
				mAdapter.refreshData();
			}
		}
	};

	@Override
	protected boolean isCurrentFocus() {
		if (mStoreGameManagerExpandList.hasFocus()) {
			return true;
		}
		return false;
	}

	public void refreshDataBySwitchLayout() {
		if (mAdapter != null) {
			mAdapter.refreshData();
		}
	}

	private void processShortcutAction(int keyCode) {
		AppEntity appEntity;
		View currentSelectView = mStoreGameManagerExpandList.getSelectedView();
		if (currentSelectView != null) {
			DldManagerViewHolder dldHolder = (DldManagerViewHolder) currentSelectView.getTag();
			appEntity = dldHolder.entity;
			if (appEntity == null) {
				return;
			}
			switch (keyCode) {
			case SnailKeyCode.SUN_KEY:
				if (!mAdapter.isLongClickable()) {
					mAdapter.operator(appEntity);
				}
				break;

			case SnailKeyCode.MOON_KEY:
			case SnailKeyCode.BACK_KEY:
				unDisplayGameDeleteView();
				break;
			case SnailKeyCode.MOUNT_KEY:
				if (!mAdapter.isLongClickable()) {
					mAdapter.openGameDetail(appEntity);
				}
				break;
			case SnailKeyCode.WATER_KEY:
				mAdapter.processGameDelete(appEntity);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onSunKey() {
		processShortcutAction(SnailKeyCode.SUN_KEY);
		return super.onSunKey();
	}

	@Override
	public boolean onMoonKey() {
		processShortcutAction(SnailKeyCode.MOON_KEY);
		return super.onMoonKey();
	}

	@Override
	public boolean onMountKey() {
		processShortcutAction(SnailKeyCode.MOUNT_KEY);
		return super.onMountKey();
	}

	@Override
	public boolean onWaterKey() {
		processShortcutAction(SnailKeyCode.WATER_KEY);
		return super.onWaterKey();
	}

	@Override
	public boolean onBackKey() {
		processShortcutAction(SnailKeyCode.BACK_KEY);
		return super.onBackKey();
	}

	private void unDisplayGameDeleteView() {
		if (mAdapter.isLongClickable()) {
			mAdapter.setIsLongClickable(false);
			mAdapter.updateCurrentDeleteView();
		}
	}

}
