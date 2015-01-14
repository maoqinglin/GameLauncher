package com.ireadygo.app.gamelauncher.ui.store.gamemanager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.mygame.adapter.HExpandableListAdapter;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.detail.GameDetailActivity;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.ui.widget.ExpandableHListView;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class DownloadManageAdapter extends HExpandableListAdapter {
	private Context mContext;
	private GameManager mGameManager;
	private ImageLoader mImageLoader;
	private GameData mGameData;
	private OnDataRefreshListener mDataRefreshL;

	private List<AppEntity> mDownloadingList = new ArrayList<AppEntity>();
	private List<AppEntity> mUpdatableList = new ArrayList<AppEntity>();
	private List<AppEntity> mLaunchableList = new ArrayList<AppEntity>();

	private ExpandableHListView mListView;

	private boolean mIsLongClickable;
	private View mCurrentSelectedView;
	private int mCurrentSelectedPos = -1;

    public DownloadManageAdapter(Context context, ExpandableHListView listview, List<AppEntity> downloadingList,
			List<AppEntity> updatableList, List<AppEntity> launchableList) {
		this.mContext = context;
		this.mListView = listview;
		this.mDownloadingList = downloadingList;
		this.mUpdatableList = updatableList;
		this.mLaunchableList = launchableList;
		mImageLoader = ImageLoader.getInstance();
		mGameManager = GameLauncher.instance().getGameManager();
		mGameData = GameData.getInstance(mContext);
	}

	@Override
	public synchronized int getGroupCount() {
		int groupCount = 0;
		if (!mDownloadingList.isEmpty()) {
			groupCount++;
		}
		if (!mUpdatableList.isEmpty()) {
			groupCount++;
		}
		if (!mLaunchableList.isEmpty()) {
			groupCount++;
		}
		return groupCount;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<AppEntity> appList = getChildList(groupPosition);
		if (appList != null) {
			return appList.size() -1;
		}
		return 0;
	}

	@Override
	public AppEntity getGroup(int groupPosition) {
		
		if (groupPosition == 0) {
			if (!mDownloadingList.isEmpty()) {
				return mDownloadingList.get(0);
			} else if (!mUpdatableList.isEmpty()) {
				return mUpdatableList.get(0);
			} else if (!mLaunchableList.isEmpty()) {
				return mLaunchableList.get(0);
			}
		} else if (groupPosition == 1) {

			if (!mDownloadingList.isEmpty() && !mUpdatableList.isEmpty()) {
				return mUpdatableList.get(0);
			} else if (!mLaunchableList.isEmpty()) {
				return mLaunchableList.get(0);
			}

		} else if (groupPosition == 2) {
			if (!mLaunchableList.isEmpty()) {
				return mLaunchableList.get(0);
			}
		}
		
		return null;
	}
	
	@Override
	public AppEntity getChild(int groupPosition, int childPosition) {
		List<AppEntity> appList = getChildList(groupPosition);
		if (appList != null) {
			if(childPosition < appList.size()-1){
				return appList.get(childPosition+1);
			}else{
				return appList.get(childPosition);
			}
		}
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		DldManagerViewHolder dldManagerViewHolder;
		if (convertView == null) {
			dldManagerViewHolder = new DldManagerViewHolder();
			convertView = newView(dldManagerViewHolder, convertView);
		} else {
			dldManagerViewHolder = (DldManagerViewHolder) convertView.getTag();
		}
		makeItem(dldManagerViewHolder, groupPosition);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		DldManagerViewHolder dldManagerViewHolder;
		if (convertView == null) {
			dldManagerViewHolder = new DldManagerViewHolder();
			convertView = newView(dldManagerViewHolder, convertView);
		} else {
			dldManagerViewHolder = (DldManagerViewHolder) convertView.getTag();
		}
		makeItem(dldManagerViewHolder, groupPosition, childPosition);
		return convertView;
	}
	
	private View newView(DldManagerViewHolder dldManagerViewHolder,View convertView){
		convertView = LayoutInflater.from(mContext).inflate(R.layout.store_game_list_item, null);
		dldManagerViewHolder.background = (ImageView)convertView.findViewById(R.id.background);
		dldManagerViewHolder.icon = (ImageView)convertView.findViewById(R.id.game_icon);
		dldManagerViewHolder.title = (TextView) convertView.findViewById(R.id.game_name);
		dldManagerViewHolder.status = (TextView) convertView.findViewById(R.id.game_download_state);
		dldManagerViewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.game_download_progress);
		dldManagerViewHolder.downloadDisplayLayout = (LinearLayout) convertView.findViewById(R.id.ll_game_download_speed_size_display);
		dldManagerViewHolder.downloadSpeed = (TextView) convertView.findViewById(R.id.tv_game_download_speed);
		dldManagerViewHolder.downloadSize = (TextView) convertView.findViewById(R.id.tv_game_download_size);
		dldManagerViewHolder.gameNameLayout = (LinearLayout) convertView.findViewById(R.id.game_name_ll);
		dldManagerViewHolder.gameStatusLayout = (FrameLayout)convertView.findViewById(R.id.game_state_layout);
		dldManagerViewHolder.gameDelete = (ImageView)convertView.findViewById(R.id.iv_gametask_delete);
		dldManagerViewHolder.gameDeleteBg = (ImageView)convertView.findViewById(R.id.iv_game_delete_bg);
		convertView.setTag(dldManagerViewHolder);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public List<AppEntity> getChildList(int groupPosition) {
		if (groupPosition == 0) {
			if (!mDownloadingList.isEmpty()) {
				return mDownloadingList;
			} else if (!mUpdatableList.isEmpty()) {
				return mUpdatableList;
			} else {
				return mLaunchableList;
			}
		} else if (groupPosition == 1) {
			if (!mDownloadingList.isEmpty() && !mUpdatableList.isEmpty()) {
				return mUpdatableList;
			}
			if (!mLaunchableList.isEmpty()) {
				return mLaunchableList;
			}
		} else if (groupPosition == 2) {
			return mLaunchableList;
		}
		return null;
	}

	private void makeItem(final DldManagerViewHolder holder, int groupPosition, int childPosition) {
		final AppEntity appEntity = getChild(groupPosition, childPosition);
		if (appEntity == null) {
			return;
		}
		holder.title.setText(appEntity.getName());
//		if (appEntity.getTotalSize() == 0) {
//			holder.downloadDisplayLayout.setVisibility(View.INVISIBLE);
//		} else {
//			holder.fileSize.setVisibility(View.VISIBLE);
//			holder.fileSize.setText(mContext.getString(R.string.detail_game_size)
//					+ Formatter.formatFileSize(mContext, appEntity.getTotalSize()));
//		}
		//判断是否为显示已安装列表
		if (GameState.LAUNCHABLE.equals(appEntity.getGameState())) {
			PackageInfo info = PackageUtils.getPkgInfo(mContext, appEntity.getPkgName());
			if (appEntity.getTotalSize() == 0) {
				File file = new File(info.applicationInfo.publicSourceDir);
				if (file.exists()) {
					mGameData.updateTotalSize(appEntity.getPkgName(), file.length());
				}
			}
		}

		displayIcon(holder, appEntity);
		updateOnStateChange(holder, appEntity);
		holder.entity = appEntity;
		performClick(holder, appEntity);
	}

    private void performClick(final DldManagerViewHolder holder, final AppEntity appEntity) {
        holder.gameNameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLongClickable()) {
                    if (appEntity.getGameState() == GameState.LAUNCHABLE) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("AppName", appEntity.getName());
                        map.put("PkgName", appEntity.getPkgName());
                        MobclickAgent.onEvent(mContext, "app_launch", map);
                    }
                    operator(appEntity);
                }
            }
        });

        holder.icon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isLongClickable()) {
                    openGameDetail(appEntity);
                }
            }
        });

        holder.icon.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mIsLongClickable = true;
                updateCurrentDeleteView();
                return true;
            }
        });

        holder.gameDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                processGameDelete(appEntity);
            }

        });
    }

    public void openGameDetail(final AppEntity appEntity) {
        Intent intent = new Intent(mContext, GameDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putParcelable(GameDetailActivity.EXTRAS_APP_ENTITY, appEntity);
        intent.putExtras(bundle);
        SoundPoolManager.instance(mContext).play(SoundPoolManager.SOUND_ENTER);
        mContext.startActivity(intent);
    }

    public void processGameDelete(final AppEntity appEntity) {
        if (appEntity.getGameState() == GameState.LAUNCHABLE
                || appEntity.getGameState() == GameState.UPGRADEABLE) {
            PackageUtils.unInstallApp(mContext, appEntity.getPkgName());
        } else {
            deleteItem(appEntity);
        }
    }

    public boolean isLongClickable() {
        return mIsLongClickable;
    }

    public void setIsLongClickable(boolean isLongClickable) {
        this.mIsLongClickable = isLongClickable;
    }

	private void makeItem(final DldManagerViewHolder holder, int groupPosition) {
		final AppEntity appEntity = getGroup(groupPosition);
		if (appEntity == null) {
			return;
		}
		holder.title.setText(appEntity.getName());
		if (appEntity.getTotalSize() == 0) {
//			holder.fileSize.setVisibility(View.INVISIBLE);
		} else {
//			holder.fileSize.setVisibility(View.VISIBLE);
//			holder.fileSize.setText(mContext.getString(R.string.detail_game_size)
//					+ Formatter.formatFileSize(mContext, appEntity.getTotalSize()));
		}
		//判断是否为显示已安装列表
		if (GameState.LAUNCHABLE.equals(appEntity.getGameState())) {
			PackageInfo info = PackageUtils.getPkgInfo(mContext, appEntity.getPkgName());
			String curVersion = String.format(mContext.getResources().getString(R.string.store_dld_manager_cur_version),
					appEntity.getVersionName());
			if (appEntity.getTotalSize() == 0) {
				File file = new File(info.applicationInfo.publicSourceDir);
				if (file.exists()) {
					mGameData.updateTotalSize(appEntity.getPkgName(), file.length());
				}
			}
		}
		displayIcon(holder, appEntity);
		updateOnStateChange(holder, appEntity);
		holder.entity = appEntity;
		
		performClick(holder, appEntity);
	}

	private String formatSpeedText(long speed) {
		String speedStr = "";
		int kb = 1024;
		int mb = 1024 * kb;
		if (speed < kb) {
			speedStr = speed + "B/s";
		} else if (speed < mb) {
			speedStr = speed / kb + "KB/s";
		} else {
			speedStr = new DecimalFormat("#.00").format((float) speed / mb) + "MB/s";
		}
		return speedStr;
	}

	private void displayIcon(DldManagerViewHolder holder, AppEntity appEntity) {
		String iconUrl = appEntity.getPosterIconUrl();
		if (TextUtils.isEmpty(iconUrl)) {
			iconUrl = appEntity.getRemoteIconUrl();
		}
		if (!TextUtils.isEmpty(iconUrl)) {
			holder.icon.setTag(iconUrl);
			mImageLoader.displayImage(iconUrl, holder.icon);
		} else {
			iconUrl = appEntity.getLocalIconUrl();
			Bitmap bitmap = PictureUtil.readBitmap(mContext,iconUrl);
			if(bitmap == null) {
				holder.icon.setImageResource(R.drawable.store_app_icon_normal);
				return;
			}
			holder.icon.setImageBitmap(bitmap);
		}
	}

	public void operator(AppEntity appEntity) {
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

	public void deleteItem(final AppEntity appEntity) {
		final ConfirmDialog dialog = new ConfirmDialog(mContext);
		dialog.setPrompt(R.string.delete_task_prompt).setMsg(R.string.delete_task_msg)
				.setConfirmClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						GameLauncher.instance().getGameManager().delete(appEntity);
					}
				});
		dialog.show();
	}

	public void refreshData() {
		mDownloadingList.clear();
		List<AppEntity> downloadingList = mGameData.getDownloadGames();
		mDownloadingList.addAll(downloadingList);

		mUpdatableList.clear();
		List<AppEntity> updatableList = mGameData.getUpdateAbleGames(AppEntity.CAN_UPGRADE);
		mUpdatableList.addAll(updatableList);

		mLaunchableList.clear();
		List<AppEntity> launchableList = mGameData.getLauncherAbleGames();
		mLaunchableList.addAll(launchableList);

		notifyDataSetChanged();

		expandAllGroup();

		if (mDataRefreshL != null) {
			mDataRefreshL.onDataRefresh(mDownloadingList, mUpdatableList, mLaunchableList);
		}
	}

    private void updateProgressView(DldManagerViewHolder holder, String size, String speed, int progress, int visible) {
        holder.progressBar.setVisibility(visible);
        holder.progressBar.setProgress(progress);
        holder.downloadSpeed.setVisibility(visible);
        holder.downloadSpeed.setText(speed);
        holder.downloadSize.setVisibility(visible);
        holder.downloadSize.setText(size);
        if (!TextUtils.isEmpty(speed)) {
            holder.downloadDisplayLayout.setBackgroundColor(mContext.getResources().getColor(
                    R.color.semitransparent_game_download_bg));
        } else {
            holder.downloadDisplayLayout.setBackgroundColor(mContext.getResources().getColor(
                    android.R.color.transparent));
        }
    }

	private void updateOnProgressChange(DldManagerViewHolder holder, AppEntity app) {
		switch (app.getGameState()) {
		case DEFAULT:
		case UPGRADEABLE:
		case INSTALLABLE:
		case INSTALLING:
		case LAUNCHABLE:
		case ERROR:
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case TRANSFERING:
			int progress = (int) (app.getDownloadSize() * 100 / app.getTotalSize());
			String sizeString = Formatter.formatFileSize(mContext, app.getDownloadSize()) + " / "
					+ Formatter.formatFileSize(mContext, app.getTotalSize());
			String speedString = formatSpeedText(app.getDownloadSpeed());

			updateProgressView(holder, sizeString, speedString, progress, View.VISIBLE);
			break;
		case QUEUING:
			int progress2 = (int) (app.getDownloadSize() * 100 / app.getTotalSize());
			updateProgressView(holder, null, null, progress2, View.INVISIBLE);
			break;
		case PAUSED:
			int progress3 = (int) (app.getDownloadSize() * 100 / app.getTotalSize());
			updateProgressView(holder, null, null, progress3, View.INVISIBLE);
			break;
		default:
			break;
		}
	}

	private void updateStateView(DldManagerViewHolder holder, int textId, int drawableId) {
		holder.status.setText(textId);
		holder.status.setBackgroundResource(drawableId);
	}

	void updateOnStateChange(DldManagerViewHolder holder, AppEntity app) {
		switch (app.getGameState()) {
		case DEFAULT:
			updateStateView(holder, R.string.status_download, R.drawable.store_item_status_download_shape);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case TRANSFERING:
			updateStateView(holder, R.string.status_pause, R.drawable.group_title_transparent);
			if (app.getTotalSize() != 0) {
				int progress = (int) (app.getDownloadSize() * 100 / app.getTotalSize());
				String sizeString = Formatter.formatFileSize(mContext, app.getDownloadSize()) + " / "
						+ Formatter.formatFileSize(mContext, app.getTotalSize());
				String speedString = formatSpeedText(app.getDownloadSpeed());

				updateProgressView(holder, sizeString, speedString, progress, View.VISIBLE);
			}
			break;
		case QUEUING:
			updateStateView(holder, R.string.status_queue, R.drawable.store_item_status_download_shape);
			if (app.getTotalSize() != 0) {
				int progress2 = (int) (app.getDownloadSize() * 100 / app.getTotalSize());
				updateProgressView(holder, null, null, progress2, View.INVISIBLE);
			}
			break;
		case UPGRADEABLE:
			updateStateView(holder, R.string.status_update, R.drawable.store_item_status_update_shape);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case PAUSED:
			updateStateView(holder, R.string.status_transfering, R.drawable.store_item_status_download_shape);
			if (app.getTotalSize() != 0) {
				int progress3 = (int) (app.getDownloadSize() * 100 / app.getTotalSize());
				updateProgressView(holder, null, null, progress3, View.INVISIBLE);
			}
			break;
		case INSTALLABLE:
			updateStateView(holder, R.string.status_installing, R.drawable.store_item_status_install_shape);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case INSTALLING:
			updateStateView(holder, R.string.status_installing, R.drawable.store_item_status_install_shape);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case LAUNCHABLE:
			updateStateView(holder, R.string.status_launch, R.drawable.store_item_status_download_shape);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case ERROR:
			updateStateView(holder, R.string.status_error, R.drawable.store_item_status_error);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		default:
			break;
		}
		updateDeleteView(holder);
	}

    public void updateDeleteView(DldManagerViewHolder holder) {
        if (isLongClickable()) {
            holder.gameDelete.setVisibility(View.VISIBLE);
            holder.gameDeleteBg.setVisibility(View.VISIBLE);
        } else {
            holder.gameDelete.setVisibility(View.INVISIBLE);
            holder.gameDeleteBg.setVisibility(View.INVISIBLE);
        }
    }

    public void updateCurrentDeleteView() {
        for (int pos = 0; pos < mListView.getChildCount(); pos++) {
            View view = mListView.getChildAt(pos);
            if (view != null) {
                DldManagerViewHolder dldHolder = (DldManagerViewHolder) view.getTag();
                updateDeleteView(dldHolder);
            }
        }
    }

    // 不包括可更新的Item
	public void updateItemView(AppEntity appEntity, UpdateType updateType) {
		if (appEntity == null || mDownloadingList.isEmpty()) {
			return;
		}
		int childPosition = 0;
		// 更新列表数据
		for (int i = 0; i < mDownloadingList.size(); i++) {
			AppEntity app = mDownloadingList.get(i);
			if (appEntity == null || TextUtils.isEmpty(appEntity.getAppId())) {
				continue;
			}
			if (appEntity.getAppId().equals(app.getAppId())) {
				switch (updateType) {
				case ITEM_ALL:
				case UPDATE_STATUS:
					app.copyFrom(appEntity);
					break;
				case UPDATE_PROGRESS:
					app.setDownloadSize(appEntity.getDownloadSize());
					app.setTotalSize(appEntity.getTotalSize());
					app.setDownloadSpeed(appEntity.getDownloadSpeed());
					break;
				default:
					break;
				}
				childPosition = i;
				break;
			}
		}
		// 更新页面
		int start = mListView.getFirstVisiblePosition();
		for (int i = start, j = mListView.getLastVisiblePosition(); i <= j; i++) {
			if (i >= mDownloadingList.size()) {
				return;
			}
			AppEntity otherApp = mDownloadingList.get(i);
			if (otherApp != null) {
				if (appEntity.getAppId().equals(otherApp.getAppId())) {
					View view = mListView.getChildAt(i - start);
					DldManagerViewHolder holder = (DldManagerViewHolder) view.getTag();
					switch (updateType) {
					case ITEM_ALL:
						if (i != 0) {
							makeItem(holder, 0, childPosition);
						} else {
							makeItem(holder, 0);
						}
						break;
					case UPDATE_STATUS:
						updateOnStateChange(holder, appEntity);
						break;
					case UPDATE_PROGRESS:
						updateOnProgressChange(holder, appEntity);
						break;
					default:
						break;
					}
					break;
				}
			}
		}
	}

	public enum UpdateType {
		ITEM_ALL, UPDATE_STATUS, UPDATE_PROGRESS
	}

	public static class DldManagerViewHolder {
		ImageView background;
		ImageView icon;
		TextView title;
		TextView status;
		LinearLayout gameNameLayout;
		FrameLayout gameStatusLayout;
		ProgressBar progressBar;
		LinearLayout downloadDisplayLayout;
		TextView downloadBg;
		TextView downloadSpeed;
		TextView downloadSize;
		ImageView gameDelete;
		ImageView gameDeleteBg;
		AppEntity entity;
		
	}

	public void expandAllGroup() {
		int groupCount = getGroupCount();
//		Log.d("liu.js", "expandAllGroup-groupCount=" + groupCount);
		for (int i = 0; i < groupCount; i++) {
			mListView.expandGroup(i);
		}
	}

	private String getGroupTitleByPosition(int groupPosition){
		String title = "";
		if(groupPosition < 0 || groupPosition > getGroupCount()-1){
			return null;
		}
		AppEntity entity = getGroup(groupPosition);
		if(entity != null){
			GameState state = entity.getGameState();
			if(GameState.LAUNCHABLE == state){
				title =  mContext.getString(R.string.store_dld_manager_launchable);
			}else if(GameState.UPGRADEABLE == state){
				title = mContext.getString(R.string.store_dld_manager_updatable);
			}else{
				title = mContext.getString(R.string.store_dld_manager_downloading);
			}
		}
		return title;
	}

	@Override
	public Drawable getGroupTitleBg(Drawable titleBg,int groupPosition){
		String title = getGroupTitleByPosition(groupPosition);
		if(!TextUtils.isEmpty(title) && titleBg != null){
			Bitmap indicatorBmp = PictureUtil.drawableToBitmap(titleBg);
			if (indicatorBmp != null) {
				Bitmap textBmp = PictureUtil.drawTextAtBitmap(indicatorBmp, title);
				if (textBmp != null) {
					return new BitmapDrawable(textBmp);
				}
			}
		}
		return null;
	}
	
	public final void doSelectedAnimator() {
		checkListView();
		View selectedView = mListView.getSelectedView();
		mCurrentSelectedPos = mListView.getSelectedItemPosition();
		if (selectedView == null) {
			return;
		}
		if (mCurrentSelectedView != null && mCurrentSelectedView != selectedView) {
			doUnselectedAnimator(null);
			mCurrentSelectedView = null;
		}
		Animator animator = selectedAnimator(selectedView);
		if(animator != null){
			animator.setDuration(Config.Animator.DURATION_SELECTED);
			animator.start();
		}
		mCurrentSelectedView = selectedView;
	}

	public final void doUnselectedAnimator(AnimatorListener listener) {
		checkListView();
		if (mCurrentSelectedView != null) {
			Animator animator = unselectedAnimator(mCurrentSelectedView);
			if(animator != null){
				if (listener != null) {
					animator.addListener(listener);
				}
				animator.setDuration(Config.Animator.DURATION_UNSELECTED);
				animator.start();
			}
		} else {
			if (listener != null) {
				listener.onAnimationEnd(null);
			}
		}
	}
	
	private void checkListView() {
		if (mListView == null) {
			throw new NullPointerException("必须先调用setHListView()方法!");
		}
	}

	public View getSelectedView() {
		return mCurrentSelectedView;
	}

	public int getSelectedPos() {
		return mCurrentSelectedPos;
	}
	
	public Animator selectedAnimator(View view) {
		final DldManagerViewHolder viewHolder = (DldManagerViewHolder)view.getTag();
		AnimatorSet animSet = new AnimatorSet();
//		viewHolder.background.setPivotX(viewHolder.background.getWidth() / 2);
//		viewHolder.background.setPivotY(viewHolder.background.getHeight() / 5);
		viewHolder.background.setPivotX(viewHolder.background.getWidth() / 2);
		viewHolder.background.setPivotY(viewHolder.background.getHeight() / 3);
		PropertyValuesHolder bgScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.24f);
		PropertyValuesHolder bgScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.24f);
		ObjectAnimator bgScaleAnimator = ObjectAnimator.ofPropertyValuesHolder(viewHolder.background, bgScaleXHolder, bgScaleYHolder);

		// 游戏海报动画
		PropertyValuesHolder gameIconXAnim = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.13f);
		PropertyValuesHolder gameIconYAnim = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.13f);
		ObjectAnimator gameIconAnim = ObjectAnimator.ofPropertyValuesHolder(viewHolder.icon, gameIconXAnim,
				gameIconYAnim);

		PropertyValuesHolder downloadBgXAnim = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.13f);
        PropertyValuesHolder downloadBgYAnim = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.13f);
        ObjectAnimator downloadBgAnim = ObjectAnimator.ofPropertyValuesHolder(viewHolder.downloadDisplayLayout, downloadBgXAnim,
                downloadBgYAnim);

        PropertyValuesHolder txtXAnim = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.13f);
        PropertyValuesHolder txtYAnim = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.13f);
		PropertyValuesHolder txtTranslateYHolder = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,
				Config.StoreDetail.DOWNLOAD_MANAGE_TITLE_SELECTED_TRANSLATE_Y);
		ObjectAnimator gameNameAnim = ObjectAnimator.ofPropertyValuesHolder(viewHolder.gameNameLayout,
				txtTranslateYHolder,txtXAnim,txtYAnim);

		animSet.playTogether(bgScaleAnimator,gameNameAnim,gameIconAnim,downloadBgAnim);
		animSet.setInterpolator(new AccelerateInterpolator());
		animSet.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				viewHolder.background.setImageResource(R.drawable.corner_red_shape);
				viewHolder.status.setBackground(null);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				viewHolder.status.setBackgroundResource(R.drawable.store_item_status_select_bg);
			}
		});
		return animSet;
	}

	public Animator unselectedAnimator(View view) {
		final DldManagerViewHolder viewHolder = (DldManagerViewHolder)view.getTag();
		AnimatorSet animSet = new AnimatorSet();

		PropertyValuesHolder bgScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f);
		PropertyValuesHolder bgScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f);
		ObjectAnimator bgScaleAnimator = ObjectAnimator.ofPropertyValuesHolder(viewHolder.background, bgScaleXHolder, bgScaleYHolder);
		
		// 游戏海报动画
		PropertyValuesHolder gameIconXAnim = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f);
		PropertyValuesHolder gameIconYAnim = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f);
		ObjectAnimator gameIconAnim = ObjectAnimator.ofPropertyValuesHolder(viewHolder.icon, gameIconXAnim,
				gameIconYAnim);
		
		PropertyValuesHolder downloadBgXAnim = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f);
        PropertyValuesHolder downloadBgYAnim = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f);
        ObjectAnimator downloadBgAnim = ObjectAnimator.ofPropertyValuesHolder(viewHolder.downloadDisplayLayout, downloadBgXAnim,
                downloadBgYAnim);

        PropertyValuesHolder txtXAnim = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f);
        PropertyValuesHolder txtYAnim = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f);
		PropertyValuesHolder txtTranslateYHolder = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0);
		ObjectAnimator gameNameAnim = ObjectAnimator.ofPropertyValuesHolder(viewHolder.gameNameLayout,
				txtTranslateYHolder,txtXAnim,txtYAnim);

		animSet.playTogether(gameNameAnim,bgScaleAnimator,gameIconAnim,downloadBgAnim);
		animSet.setInterpolator(new AccelerateInterpolator());
		animSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				updateOnStateChange(viewHolder, viewHolder.entity);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				updateOnStateChange(viewHolder, viewHolder.entity);
				viewHolder.background.setImageResource(R.drawable.corner_black_shape);
			}
		});
		return animSet;
	}

	public void setDataRefreshListener(OnDataRefreshListener dataRefreshListener) {
		this.mDataRefreshL = dataRefreshListener;
	}

	public static interface OnDataRefreshListener {
		public void onDataRefresh(List<AppEntity> downloadingList, List<AppEntity> updateList, List<AppEntity> launchableList);
	}

	public List<AppEntity> getDownloadingList() {
		return mDownloadingList;
	}

	public List<AppEntity> getUpdatableList() {
		return mUpdatableList;
	}

	public List<AppEntity> getLaunchableList() {
		return mLaunchableList;
	}
}
