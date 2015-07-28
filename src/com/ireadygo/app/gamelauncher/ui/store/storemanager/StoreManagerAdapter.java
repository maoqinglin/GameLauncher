package com.ireadygo.app.gamelauncher.ui.store.storemanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.ui.store.storemanager.StoreManagerContentFragment.GameManagerType;
import com.ireadygo.app.gamelauncher.ui.store.storemanager.StoreManagerItem.StoreManagerItemHolder;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class StoreManagerAdapter implements HMultiBaseAdapter {

	private Context mContext;
	private GameManagerType mType = GameManagerType.DOWNLOAD;
	private HMultiListView mMultiListView;
	private List<AppEntity> mAppList = new ArrayList<AppEntity>();
	private ImageLoader mImageLoader;

	public StoreManagerAdapter(Context context, HMultiListView multiListView, List<AppEntity> apps, GameManagerType type) {
		this.mContext = context;
		this.mMultiListView = multiListView;
		this.mAppList = apps;
		this.mType = type;
		mImageLoader = GameInfoHub.instance(mContext).getImageLoader();
	}

	@Override
	public Object getItem(int position) {
		return mAppList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new StoreManagerItem(mContext);
		}
		convertView.setVisibility(View.VISIBLE);
		StoreManagerItem item = (StoreManagerItem) convertView;
		if(position < mAppList.size()){
			AppEntity app = mAppList.get(position);
			makeItem(item, app);
		}
		item.setId(position);
		return convertView;
	}

	private void makeItem(StoreManagerItem item, AppEntity app) {
		StoreManagerItemHolder holder = item.getHolder();
		holder.title.setText(app.getName());
		displayIcon(holder, app);
		item.updateProgress(app.getDownloadSize(), app.getTotalSize(), app.getDownloadSpeed());
		item.updateByStateChange(app.getGameState());
	}

	private void displayIcon(StoreManagerItemHolder holder, AppEntity app) {
		Bitmap icon = GameData.getInstance(mContext).getPosterIconByPkgName(app.getPkgName());
		if(icon == null){
			String iconUrl = app.getPosterIconUrl();
			if(TextUtils.isEmpty(iconUrl)){
				iconUrl = app.getRemoteIconUrl();
			}
			if (!TextUtils.isEmpty(iconUrl)) {
				mImageLoader.displayImage(iconUrl, holder.icon);
				return;
			}
			iconUrl = app.getLocalIconUrl();
			icon = PictureUtil.readBitmap(mContext, iconUrl);
		}
		if (icon == null) {
			holder.icon.setImageResource(R.drawable.snail_icon_default);
		} else {
			holder.icon.setImageBitmap(icon);
		}
	}

	@Override
	public BaseAdapter getAdapter() {
		return mMultiListView.getAdapter();
	}

	@Override
	public int getHListNum() {
		return 2;
	}

	@Override
	public List<?> getData() {
		return mAppList;
	}

	@Override
	public View getEmptyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new StoreManagerItem(mContext);
		}
		convertView.setVisibility(View.GONE);
		return convertView;
	}

}
