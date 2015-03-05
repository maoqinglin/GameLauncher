package com.ireadygo.app.gamelauncher.ui.store.storemanager;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.DownloadListener;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.GameManagerException;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.InstallListener;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.UninstallListener;
import com.ireadygo.app.gamelauncher.appstore.manager.UpdateManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.menu.ImageTextMenu;
import com.ireadygo.app.gamelauncher.ui.store.storemanager.StoreManagerContentAdapter.OnChildFocusChangeListener;
import com.ireadygo.app.gamelauncher.ui.store.storemanager.StoreManagerContentAdapter.OperatorListener;
import com.ireadygo.app.gamelauncher.ui.store.storemanager.StoreManagerItem.StoreManagerItemHolder;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;

public class StoreManagerContentFragment extends BaseContentFragment {

	private HMultiListView mHMultiListView;
	private StoreManagerContentAdapter mStoreManagerAdapter;

	private InnerOperatorListener mListener = new InnerOperatorListener();
	private InnerBtnOperatorListener mBtnListener = new InnerBtnOperatorListener();

	private ImageTextMenu mDldMenuItem;
	private ImageTextMenu mUpgradeMenuItem;
	private ImageTextMenu mInstalledMenuItem;

	private GameManager mGameManager;

	private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (UpdateManager.ACTION_UPDATABLE_NOTIFICATION.equals(intent.getAction())) {
				if(mStoreManagerAdapter != null) {
					updateUpgradeLayout();
				}
			}
		}
	};

	private OperatorListener mOperatorListener = new OperatorListener() {
		
		@Override
		public void operator(View view, AppEntity appEntity, GameManagerType type) {
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
		public void openDetail(View view, AppEntity appEntity, GameManagerType type) {
			DetailActivity.startSelf(getRootActivity(), appEntity);
		}
		
		@Override
		public void delete(View view, AppEntity appEntity, GameManagerType type) {
			deleteItem(appEntity);
		}
	};

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
						}
					});
			dialog.show();
		}
	}

	public StoreManagerContentFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
		mGameManager = GameLauncher.instance().getGameManager();
		mGameManager.addDownloadListener(mListener);
		mGameManager.addInstallListener(mListener);
		mGameManager.addUninstallListener(mListener);
		IntentFilter filter = new IntentFilter(UpdateManager.ACTION_UPDATABLE_NOTIFICATION);
		LocalBroadcastManager.getInstance(activity).registerReceiver(mUpdateReceiver, filter);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.store_manager_fragment, container, false);
		initView(view);
		initListener();
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		mHMultiListView = (HMultiListView) view.findViewById(R.id.manager_viewpager);
		mDldMenuItem = (ImageTextMenu) view.findViewById(R.id.manager_download);
		mUpgradeMenuItem = (ImageTextMenu) view.findViewById(R.id.manager_upgrade);
		mInstalledMenuItem = (ImageTextMenu) view.findViewById(R.id.manager_installed);

		mStoreManagerAdapter = new StoreManagerContentAdapter(getRootActivity(), mHMultiListView, GameManagerType.DOWNLOAD);
		mHMultiListView.setAdapter(mStoreManagerAdapter);
	}

	private void initListener() {
		mStoreManagerAdapter.setOperatorListener(mOperatorListener);
		mStoreManagerAdapter.setOnChildFocusChange(new OnChildFocusChangeListener() {
			
			@Override
			public void onChildFocusChange(StoreManagerItemHolder holder, boolean hasFocus) {
				if(hasFocus || mStoreManagerAdapter.getGameManagerType() == GameManagerType.DOWNLOAD) {
					holder.statusLayout.setVisibility(View.VISIBLE);
				} else {
					holder.statusLayout.setVisibility(View.GONE);
				}
			}
		});

		mDldMenuItem.setOnFocusChangeListener(mBtnListener);
		mUpgradeMenuItem.setOnFocusChangeListener(mBtnListener);
		mInstalledMenuItem.setOnFocusChangeListener(mBtnListener);

		mDldMenuItem.setOnClickListener(mBtnListener);
		mUpgradeMenuItem.setOnClickListener(mBtnListener);
		mInstalledMenuItem.setOnClickListener(mBtnListener);
	}

	@Override
	protected boolean isCurrentFocus() {
		return mHMultiListView.hasFocus()
				|| mDldMenuItem.isFocused() 
				|| mUpgradeMenuItem.isFocused()
				|| mInstalledMenuItem.isFocused();
	}

	private void updateCurrentLayout() {
		if(mStoreManagerAdapter == null || mHMultiListView == null) {
			return;
		}
		updateDldLayout();
		updateUpgradeLayout();
		updateInstalledLayout();
	}

	private void updateDldLayout() {
		if(mStoreManagerAdapter.getGameManagerType() == GameManagerType.DOWNLOAD) {
			mStoreManagerAdapter.refreshData(GameManagerType.DOWNLOAD);
		}
	}

	private void updateUpgradeLayout() {
		if(mStoreManagerAdapter.getGameManagerType() == GameManagerType.UPGRADE) {
			mStoreManagerAdapter.refreshData(GameManagerType.UPGRADE);
		}
	}

	private void updateInstalledLayout() {
		if(mStoreManagerAdapter.getGameManagerType() == GameManagerType.INSTALLED) {
			mStoreManagerAdapter.refreshData(GameManagerType.INSTALLED);
		}
	}

	private void updateDldStatus(AppEntity app) {
		if(mStoreManagerAdapter == null || mHMultiListView == null) {
			return;
		}
		if(mStoreManagerAdapter.getGameManagerType() == GameManagerType.DOWNLOAD) {
			List<HListView> hListViews = mHMultiListView.getHListViews();
			for (HListView hListView : hListViews) {
				int start = hListView.getFirstVisiblePosition();
				for (int i = start, j = hListView.getLastVisiblePosition(); i <= j; i++) {
					AppEntity otherApp = (AppEntity) hListView.getItemAtPosition(i);
					if (otherApp != null && !TextUtils.isEmpty(otherApp.getAppId())) {
						if (otherApp.getAppId().equals(app.getAppId())) {
							List<AppEntity> entities = mStoreManagerAdapter.getData();
							int posOther = entities.indexOf(otherApp);
							entities.set(posOther, otherApp);
							View view = hListView.findViewByPosition(i);
							if(view != null) {
								mStoreManagerAdapter.updateOnStateChange((StoreManagerItem) view, app);
							}
							return;
						}
					}
				}
			}
		}
	}

	private class InnerBtnOperatorListener implements OnClickListener, OnFocusChangeListener {

		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			if(hasFocus) {
				view.setSelected(true);
				onClick(view);
			} else {
				if(mHMultiListView.hasFocus()) {
					view.setSelected(true);
				} else {
					view.setSelected(false);
				}
			}
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.manager_download:
				if(mStoreManagerAdapter.getGameManagerType() != GameManagerType.DOWNLOAD) {
					mStoreManagerAdapter.refreshData(GameManagerType.DOWNLOAD);
				}
				break;
				
			case R.id.manager_upgrade:
				if(mStoreManagerAdapter.getGameManagerType() != GameManagerType.UPGRADE) {
					mStoreManagerAdapter.refreshData(GameManagerType.UPGRADE);
				}
				break;
				
			case R.id.manager_installed:
				if(mStoreManagerAdapter.getGameManagerType() != GameManagerType.INSTALLED) {
					mStoreManagerAdapter.refreshData(GameManagerType.INSTALLED);
				}
				break;
				
			default:
				break;
			}
		}
	}

	private class InnerOperatorListener implements DownloadListener, InstallListener, UninstallListener {

		@Override
		public void onInstallStateChange(AppEntity app) {
			updateCurrentLayout();
		}

		@Override
		public void onInstallProgressChange(AppEntity app, int progress) {
			
		}

		@Override
		public void onInstallError(AppEntity app, GameManagerException ie) {
			
		}

		@Override
		public void onDownloadItemAdd(AppEntity app) {
			updateCurrentLayout();
		}

		@Override
		public void onDownloadStateChange(AppEntity app) {
			updateCurrentLayout();
		}

		@Override
		public void onDownloadProgressChange(AppEntity app) {
			Log.i("chen.r", "onDownloadProgressChange");
			updateDldStatus(app);
		}

		@Override
		public void onDownloadError(AppEntity app, GameManagerException de) {
			updateDldStatus(app);
		}

		@Override
		public void onUninstallComplete(String pkgName) {
			updateCurrentLayout();
		}

		@Override
		public void onUninstallError(String pkgName, GameManagerException ge) {
			
		}
	}

	public enum GameManagerType {
		DOWNLOAD, UPGRADE, INSTALLED
	}

}
