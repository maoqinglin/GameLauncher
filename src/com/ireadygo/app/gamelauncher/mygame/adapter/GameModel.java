package com.ireadygo.app.gamelauncher.mygame.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherModel.Callbacks;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherSettings.Favorites;
import com.ireadygo.app.gamelauncher.mygame.info.ExtendInfo;
import com.ireadygo.app.gamelauncher.mygame.info.FolderInfo;
import com.ireadygo.app.gamelauncher.mygame.info.ItemInfo;
import com.ireadygo.app.gamelauncher.mygame.info.ShortcutInfo;
import com.ireadygo.app.gamelauncher.mygame.ui.view.Folder;
import com.ireadygo.app.gamelauncher.mygame.ui.view.GameAllAppLayout.AppWindowShowStateListener;
import com.ireadygo.app.gamelauncher.mygame.utils.ImageHelper;
import com.ireadygo.app.gamelauncher.mygame.utils.ScreenCapture;
import com.ireadygo.app.gamelauncher.mygame.utils.Utilities;
import com.ireadygo.app.gamelauncher.ui.MyAppFragment;
import com.ireadygo.app.gamelauncher.ui.MyGameFragment;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.detail.GameDetailActivity;
import com.ireadygo.app.gamelauncher.ui.listview.anim.AnimationAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;

public class GameModel implements Callbacks {

	private HListView mHListView;

	private Activity mActivity;
	private MyAppAdapter mMyAppAdapter;
	private MyGameAdapter mMyGameAdapter;

	protected List<ItemInfo> mAppItems = new LinkedList<ItemInfo>();
	protected List<ItemInfo> mGameItems = new LinkedList<ItemInfo>();
	private HashMap<Long, FolderInfo> mFoldItems = new HashMap<Long, FolderInfo>();
	private Folder mFolder;
	private BaseContentFragment mCurrentContentFragment;
	private AppWindowShowStateListener mAppWindowShowStateListener;

	public GameModel(Activity activity,BaseContentFragment contentFragment) {
		mActivity = activity;
		mCurrentContentFragment = contentFragment;
		initData();
	}

	private void initData() {
		GameLauncherAppState.getInstance(mActivity).setCallback(this);
		GameLauncherAppState.getInstance(mActivity).getModel().startLoader();
	}

	public void setHListView(HListView hListView, DataType dataType) {
		mHListView = hListView;
		mHListView.setOnItemClickListener(mOnItemClickListener);
		setAdapter(dataType);
	}

	public void setAppWindowShowStateListener(AppWindowShowStateListener listener){
		mAppWindowShowStateListener = listener;
	}

	public void setAdapter(DataType dataType) {
		if (mHListView != null) {
			if (DataType.TYPE_APP == dataType) {
				mMyAppAdapter = new MyAppAdapter(mActivity, mHListView, mAppItems);
				mHListView.setAdapter(mMyAppAdapter.toAnimationAdapter());
			} else if (DataType.TYPE_GAME == dataType) {
				mMyGameAdapter = new MyGameAdapter(mActivity, mHListView, mGameItems);
				mHListView.setAdapter(mMyGameAdapter.toAnimationAdapter());
			}
		}
	}

	public void notifyDataSet() {
		if (null != mHListView) {
			AnimationAdapter animAdapter = (AnimationAdapter) mHListView.getAdapter();
			if (null != animAdapter) {
				BaseAdapter appAdapter = animAdapter.getDecoratedBaseAdapter();
				appAdapter.notifyDataSetChanged();
				setAppNextFocusDownId(appAdapter);
			}
		}
	}

	private void setAppNextFocusDownId(BaseAdapter adapter) {
		if (adapter instanceof MyAppAdapter) {

		} else if (adapter instanceof MyGameAdapter) {

		}
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			performClickAction(parent, view, position);
		}

	};

	private void performClickAction(AdapterView<?> parent, View view, int position) {
		if (view.getTag() instanceof MyGameAdapter.ViewHolder) {
			MyGameAdapter.ViewHolder viewHolder = (MyGameAdapter.ViewHolder) view.getTag();
			if (null != viewHolder) {
				doAction(parent, view, position, viewHolder.itemInfo);
			}
		} else if (view.getTag() instanceof MyAppAdapter.ViewHolder) {
			MyAppAdapter.ViewHolder viewHolder = (MyAppAdapter.ViewHolder) view.getTag();
			if (null != viewHolder) {
				doAction(parent, view, position, viewHolder.itemInfo);
			}
		}
	}

	private void doAction(AdapterView<?> parent, View view, int position,ItemInfo gameInfo) {
		if(gameInfo == null){
			return;
		}
		if (gameInfo instanceof ShortcutInfo) {
			if ((mMyAppAdapter != null && mMyAppAdapter.isLongClickable())
					|| (mMyGameAdapter != null && mMyGameAdapter.isLongClickable())) {
				return;
			}
			Utilities.startActivitySafely(view, gameInfo.getIntent(), null);
			GameData.getInstance(mActivity).updateLastLaunchTime(gameInfo.packageName, System.currentTimeMillis());
			GameLauncherAppState.getInstance(mActivity).getModel()
					.updateModifiedTime(gameInfo.packageName, System.currentTimeMillis());
			// 上报外部启动免商店游戏
			AppEntity app = GameData.getInstance(mActivity).getGameByPkgName(gameInfo.packageName);
			if (app != null && !TextUtils.isEmpty(app.getAppId())) {
				StaticsUtils.openGame(app.getAppId());
			}
			
		} else if (gameInfo instanceof FolderInfo) {
			FolderInfo folderInfo = (FolderInfo) gameInfo;
			ArrayList<ShortcutInfo> infos = folderInfo.contents;
			mFolder = Folder.fromXml(mActivity);
			parent.setVisibility(View.INVISIBLE);
//			applyBlur(mFolder);
			mFolder.bind(folderInfo);
			mFolder.openFolder(mActivity, infos, view, position,mAppWindowShowStateListener);
		}else if(gameInfo instanceof ExtendInfo){
			ExtendInfo extendInfo = (ExtendInfo) gameInfo;
			switch (extendInfo.function) {
			case GAME_ALL:
				if(mCurrentContentFragment !=null && mCurrentContentFragment instanceof MyAppFragment){
					((MyAppFragment)mCurrentContentFragment).displayAllAppLayout();
				}
				break;
			case GAME_RECOMMEND_DOWNLOAD:
				if(mCurrentContentFragment !=null && mCurrentContentFragment instanceof MyGameFragment){
					((MyGameFragment)mCurrentContentFragment).displayRecommandAppLayout();
				}
				break;
			case SLOT_BUY:

				break;
			case SLOT_USE:
				break;
			default:
				throw new IllegalStateException("extendInfo is illegalState");
			}
		}
	}

	private void applyBlur(final Folder folder) {
		folder.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				folder.getViewTreeObserver().removeOnPreDrawListener(this);
				Context context = mActivity.getApplicationContext();
//				ScreenCapture capture = new ScreenCapture(context);
//				Bitmap screenBmp = capture.captureScreen();
				// Bitmap screenBmp =
				// PictureUtil.drawableToBitmap(mFolder.getBackground());
				 Bitmap screenBmp =
				 BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.game_folder_bg);
				ImageHelper.blur(mActivity, mFolder, screenBmp);
				// if(folderBg != null){
				// mFolder.setBackground(folderBg);
				//
				// }
				return true;
			}
		});
	}

	@Override
	public void bindGames(List<ItemInfo> infos) {
		mGameItems = infos;
		if (mHListView != null) {
			AnimationAdapter adapter = (AnimationAdapter) mHListView.getAdapter();
			if (adapter != null && adapter.getDecoratedBaseAdapter() instanceof MyGameAdapter) {
				setAdapter(DataType.TYPE_GAME);
			}
		}
	}

	@Override
	public void bindApps(List<ItemInfo> infos) {
		mAppItems = infos;
		if (mHListView != null) {
			AnimationAdapter adapter = (AnimationAdapter) mHListView.getAdapter();
			if (adapter != null && adapter.getDecoratedBaseAdapter() instanceof MyAppAdapter) {
				setAdapter(DataType.TYPE_APP);
			}
		}
	}

	@Override
	public void bindFolders(HashMap<Long, FolderInfo> folders) {
		mFoldItems = folders;

		for (Map.Entry<Long, FolderInfo> entry : folders.entrySet()) {
			FolderInfo folderInfo = entry.getValue();
			updateFolderIcon(folderInfo);
		}
		notifyDataSet();
	}

	@Override
	public void gameAddOrUpdate(ItemInfo info, boolean isAdd) {
		if (info == null) {
			Log.e("lmq", "gameAddOrUpdate---info is null");
			return;
		}
		if (info.appType == Favorites.APP_TYPE_APPLICATION) {
			updateGridData(mAppItems, info);
		} else if (info.appType == Favorites.APP_TYPE_GAME) {
			updateGridData(mGameItems, info);
		}
	}

	@Override
	public void gameRemove(ItemInfo info) {
		if (info == null) {
			Log.e("lmq", "gameAddOrUpdate---info is null");
			return;
		}
		updateDataByGameRemove(mAppItems, info);
		updateDataByGameRemove(mGameItems, info);
//		if (info.appType == Favorites.APP_TYPE_APPLICATION) {
//		} else if (info.appType == Favorites.APP_TYPE_GAME) {
//		}
	}

	private synchronized void updateDataByGameRemove(final List<ItemInfo> appInfos, final ItemInfo info) {
		if (null != info && null != appInfos) {
			int size = appInfos.size();
			for (int i = 0; i < size; i++) {
				ItemInfo item = appInfos.get(i);
				if (null != item) {
					if (item instanceof ShortcutInfo) {
						ShortcutInfo appShortcutInfo = (ShortcutInfo) item;
						if (appShortcutInfo.getCellSortId() == info.getCellSortId() && appShortcutInfo.equals(info)) {
							appInfos.remove(item);
							break;
						}
					}
				}
			}
			notifyDataSet();
		}
	}

	private void updateGridData(final List<ItemInfo> appList, final ItemInfo info) {
		if (null != info) {
			if (null == appList || dataFilter(appList, info)) {
				return;
			}
			appList.add(info);
			notifyDataSet();
		}
	}

	private boolean dataFilter(final List<ItemInfo> appList, final ItemInfo info) {
		boolean isExist = false;
		for (ItemInfo appItem : appList) {
			if (appItem instanceof ShortcutInfo && appItem.equals(info)) {
				isExist = true;
				updateInfo((ShortcutInfo) appItem, (ShortcutInfo) info);
			}
		}
		return isExist;
	}

	private void updateInfo(ShortcutInfo oriInfo, ShortcutInfo newInfo) {
		oriInfo.intent = newInfo.intent;
		oriInfo.appIcon = newInfo.appIcon;
		notifyDataSet();
	}

	public boolean onSunKey() {

		View v = mHListView.getSelectedView();
		if (v == null) {
			return false;
		}
		performClickAction(mHListView, v, mHListView.getSelectedItemPosition());
		return true;
	}

	public boolean onMoonKey() {
		// 取消
		AnimationAdapter animAdapter = (AnimationAdapter) mHListView.getAdapter();
		AppListAdapter adapter = (AppListAdapter) animAdapter.getDecoratedBaseAdapter();
		if (adapter instanceof MyGameAdapter) {
			mActivity.findViewById(R.id.menu_game).requestFocus();
			((MyGameAdapter)adapter).unDisplayGameDeleteView();
		} else if (adapter instanceof MyAppAdapter) {
			mActivity.findViewById(R.id.menu_app).requestFocus();
			((MyAppAdapter)adapter).unDisplayGameDeleteView();
		}

		return true;
	}

	public boolean onMountainKey() {
		View v = mHListView.getSelectedView();
		if (v == null) {
			return false;
		}

		if (v.getTag() instanceof MyGameAdapter.ViewHolder) {
			skipToGameDetail(v);
		}
		return true;
	}

	private void skipToGameDetail(View v) {
		MyGameAdapter.ViewHolder viewHolder = (MyGameAdapter.ViewHolder) v.getTag();
		if (null != viewHolder) {
			ItemInfo appInfo = viewHolder.itemInfo;
			if (null != appInfo) {
				AppEntity app = GameData.getInstance(mActivity).getGameByPkgName(appInfo.packageName);
				if (app != null) {
					Intent intent = new Intent(mActivity, GameDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Bundle bundle = new Bundle();
					bundle.putParcelable(GameDetailActivity.EXTRAS_APP_ENTITY, app);
					intent.putExtras(bundle);
					mActivity.startActivity(intent);
				}
			}
		}
	}

	public boolean onWaterKey() {
		// 卸载游戏
		ItemInfo item = getCurrentSelectedItem();
		if (item != null) {
			PackageUtils.unInstallApp(mActivity, item.packageName);
			return true;
		}
		return false;
	}

	private ItemInfo getCurrentSelectedItem() {
		ItemInfo info = null;
		if (null != mHListView) {
			Object selectedItem = mHListView.getSelectedItem();
			if (null != selectedItem && selectedItem instanceof ItemInfo) {
				info = (ItemInfo) selectedItem;
			}
		}
		return info;
	}

	public void updateFolderIcon(final FolderInfo folderInfo) {
		folderInfo.appIcon = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.game_system_folder_icon);
	}

	public void onDestoreView() {
		mHListView.setAdapter(null);
		mHListView = null;
	}

	public void onDestory() {
		GameLauncherAppState app = GameLauncherAppState.getInstance(mActivity);
		app.getModel().stopLoader();
		app.getModel().removeCallback(this);
	}

	public MyGameAdapter getMyGameAdapter() {
		return mMyGameAdapter;
	}

	public enum DataType {
		TYPE_GAME, TYPE_APP
	}

	public Animator outAnimator(DataType dataType, AnimatorListener listener) {
		if (dataType == DataType.TYPE_APP) {
			if (mMyAppAdapter == null) {
				return null;
			}
			return mMyAppAdapter.outAnimator(listener);
		} else if (dataType == DataType.TYPE_GAME) {
			if (mMyGameAdapter == null) {
				return null;
			}
			return mMyGameAdapter.outAnimator(listener);
		}
		return null;
	}

	public int getOutAnimatorDuration(DataType dataType) {
		if (dataType == DataType.TYPE_APP) {
			if (mMyAppAdapter == null) {
				return 0;
			}
			return mMyAppAdapter.getOutAnimatorDuration();
		} else if (dataType == DataType.TYPE_GAME) {
			if (mMyGameAdapter == null) {
				return 0;
			}
			return mMyGameAdapter.getOutAnimatorDuration();
		}
		return 0;
	}
}
