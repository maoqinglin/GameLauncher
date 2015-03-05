package com.ireadygo.app.gamelauncher.ui.store.storemanager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.ui.item.AppItem;
import com.ireadygo.app.gamelauncher.ui.store.storemanager.StoreManagerContentFragment.GameManagerType;
import com.ireadygo.app.gamelauncher.ui.store.storemanager.StoreManagerItem.OnItemFocusChangeListener;
import com.ireadygo.app.gamelauncher.ui.store.storemanager.StoreManagerItem.StoreManagerItemHolder;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class StoreManagerContentAdapter implements HMultiBaseAdapter{

	private static final int LIST_NUM = 2;
	private OperatorListener mListener;
	private OnChildFocusChangeListener mFocusChangeListener;
	private GameData mGameData;
	private Context mContext;
	private GameManagerType mType = GameManagerType.DOWNLOAD;
	private HMultiListView mMultiListView;
	private List<AppEntity> mGameManagerDatas = new ArrayList<AppEntity>();
	private Drawable mDefaultIcon;

	public StoreManagerContentAdapter(Context context, HMultiListView multiListView, GameManagerType type) {
		mContext = context;
		mMultiListView = multiListView;
		mType = type;
		mGameData = GameData.getInstance(mContext);
		mDefaultIcon = mContext.getResources().getDrawable(R.drawable.store_app_icon_normal);
		
		refreshData();
	}

	private void refreshData() {
		mGameManagerDatas.clear();
		switch (mType) {
		case INSTALLED:
			mGameManagerDatas.addAll(mGameData.getLauncherAbleGames());
			break;
		case UPGRADE:
			mGameManagerDatas.addAll(mGameData.getUpdateAbleGames(AppEntity.CAN_UPGRADE));
			break;
		default:
			mGameManagerDatas.addAll(mGameData.getDownloadGames());
			break;
		}
		mMultiListView.notifyDataSetChanged();
	}

	public void setGameManagerType(GameManagerType type) {
		mType = type;
	}

	public GameManagerType getGameManagerType() {
		return mType;
	}

	public void setOperatorListener(OperatorListener listener) {
		if(listener != null) {
			mListener = listener;
		}
	}

	public void setOnChildFocusChange(OnChildFocusChangeListener listener) {
		if(listener != null) {
			mFocusChangeListener = listener;
		}
	}

	public void refreshData(GameManagerType type) {
		mGameManagerDatas.clear();
		mType = type;
		switch (type) {
		case INSTALLED:
			mGameManagerDatas.addAll(mGameData.getLauncherAbleGames());
			break;
		case UPGRADE:
			mGameManagerDatas.addAll(mGameData.getUpdateAbleGames(AppEntity.CAN_UPGRADE));
			break;
		default:
			mGameManagerDatas.addAll(mGameData.getDownloadGames());
			break;
		}
		mMultiListView.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return mGameManagerDatas.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new StoreManagerItem(mContext);
		}
		if(!mGameManagerDatas.isEmpty()) {
			StoreManagerItem item = (StoreManagerItem) convertView;
			AppEntity app = mGameManagerDatas.get(position);
			makeItemView(item, app);
		}
		return convertView;
	}

	private void makeItemView(StoreManagerItem item, AppEntity app) {
		item.getHolder().title.setText(app.getName());
		displayIcon(item, app);
		updateOnStateChange(item, app);
		performClick(item, app);
	}

	private void displayIcon(StoreManagerItem item, AppEntity appEntity) {
		StoreManagerItemHolder holder = item.getHolder();
		String iconUrl = appEntity.getPosterIconUrl();
		if (TextUtils.isEmpty(iconUrl)) {
			iconUrl = appEntity.getRemoteIconUrl();
		}
		if (!TextUtils.isEmpty(iconUrl)) {
			holder.icon.setTag(iconUrl);
			ImageLoader.getInstance().displayImage(iconUrl, holder.icon);
		} else {
			iconUrl = appEntity.getLocalIconUrl();
			Bitmap bitmap = PictureUtil.readBitmap(mContext, iconUrl);
			if (bitmap == null) {
				holder.icon.setImageResource(R.drawable.store_app_icon_normal);
				return;
			}
			holder.icon.setImageBitmap(bitmap);
		}
	}

	private void performClick(final StoreManagerItem item, final AppEntity appEntity) {

		final StoreManagerItemHolder holder = item.getHolder();

		item.setOnItemFocusChangeListener(new OnItemFocusChangeListener() {
			
			@Override
			public void onFocusChange(boolean hasFocus) {
				if(mFocusChangeListener != null) {
					mFocusChangeListener.onChildFocusChange(holder, hasFocus);
				}
			}
		});

		holder.status.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mListener != null) {
					mListener.operator(v, appEntity, mType);
				}
			}
		});
		
		holder.status.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(mListener != null) {
					mListener.delete(v, appEntity, mType);
					return true;
				}
				return false;
			}
		});
		
		holder.downloadDisplayLayout.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(mListener != null) {
					mListener.delete(v, appEntity, mType);
					return true;
				}
				return false;
			}
		});
		
		holder.downloadDisplayLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mListener != null) {
					mListener.operator(v, appEntity, mType);
				}
			}
		});
		
		holder.downloadDisplayLayout.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(mListener != null) {
					mListener.delete(v, appEntity, mType);
					return true;
				}
				return false;
			}
		});
		
		holder.icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mListener != null) {
					mListener.operator(v, appEntity, mType);
				}
			}
		});

		holder.icon.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(mListener != null) {
					mListener.delete(v, appEntity, mType);
					return true;
				}
				return false;
			}
		});

	}

	private void updateStateView(StoreManagerItemHolder holder, GameState state, int drawableId) {
		if(mType == GameManagerType.DOWNLOAD && state != GameState.TRANSFERING) {
			holder.statusLayout.setVisibility(View.VISIBLE);
		} else {
			holder.statusLayout.setVisibility(View.GONE);
		}
		holder.status.setBackgroundResource(drawableId);
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

	private void updateProgressView(StoreManagerItemHolder holder, String size, String speed, int progress, int visible) {
		holder.downloadDisplayLayout.setVisibility(visible);
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

	public void updateOnStateChange(StoreManagerItem item, AppEntity app) {
		StoreManagerItemHolder holder = item.getHolder();

		switch (app.getGameState()) {
		case DEFAULT:
			updateStateView(holder, app.getGameState(), R.drawable.store_manager_status_queue);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case TRANSFERING:
			updateStateView(holder, app.getGameState(), 0);
			if (app.getTotalSize() != 0) {
				int progress = (int) (app.getDownloadSize() * 100 / app.getTotalSize());
				String sizeString = Formatter.formatFileSize(mContext, app.getDownloadSize()) + " / "
						+ Formatter.formatFileSize(mContext, app.getTotalSize());
				String speedString = formatSpeedText(app.getDownloadSpeed());

				updateProgressView(holder, sizeString, speedString, progress, View.VISIBLE);
			}
			break;
		case QUEUING:
			updateStateView(holder, app.getGameState(), R.drawable.store_manager_status_queue);
			if (app.getTotalSize() != 0) {
				int progress2 = (int) (app.getDownloadSize() * 100 / app.getTotalSize());
				updateProgressView(holder, null, null, progress2, View.INVISIBLE);
			}
			break;
		case UPGRADEABLE:
			updateStateView(holder, app.getGameState(), R.drawable.store_manager_status_upgrable);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case PAUSED:
			updateStateView(holder, app.getGameState(), R.drawable.store_manager_status_transfering);
			if (app.getTotalSize() != 0) {
				int progress3 = (int) (app.getDownloadSize() * 100 / app.getTotalSize());
				updateProgressView(holder, null, null, progress3, View.INVISIBLE);
			}
			break;
		case INSTALLABLE:
			updateStateView(holder, app.getGameState(), R.drawable.store_manager_status_install);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case INSTALLING:
			updateStateView(holder, app.getGameState(), R.drawable.store_manager_status_install);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case LAUNCHABLE:
			updateStateView(holder, app.getGameState(), R.drawable.store_manager_status_launch);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		case ERROR:
			updateStateView(holder, app.getGameState(), R.drawable.store_manager_status_error);
			updateProgressView(holder, null, null, 100, View.INVISIBLE);
			break;
		default:
			break;
		}
	}

	@Override
	public BaseAdapter getAdapter() {
		return mMultiListView.getAdapter();
	}

	@Override
	public int getHListNum() {
		return LIST_NUM;
	}

	@Override
	public List<AppEntity> getData() {
		return mGameManagerDatas;
	}

	@Override
	public View getEmptyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new AppItem(mContext);
		}
		convertView.setVisibility(View.GONE);
		return convertView;
	}

	public interface OperatorListener {
		public void operator(View view, AppEntity appEntity, GameManagerType type);
		public void delete(View view, AppEntity appEntity, GameManagerType type);
		public void openDetail(View view, AppEntity appEntity, GameManagerType type);
	}

	public interface OnChildFocusChangeListener {
		void onChildFocusChange(StoreManagerItemHolder holder, boolean hasFocus);
	}
}
