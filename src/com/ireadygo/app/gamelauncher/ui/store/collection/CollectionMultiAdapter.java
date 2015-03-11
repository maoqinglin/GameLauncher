package com.ireadygo.app.gamelauncher.ui.store.collection;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView.ScaleType;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.CollectionInfo;
import com.ireadygo.app.gamelauncher.ui.item.ImageItem;
import com.ireadygo.app.gamelauncher.ui.item.ImageItem.ImageItemHolder;
import com.ireadygo.app.gamelauncher.ui.item.ImageItemLarge;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CollectionMultiAdapter implements HMultiBaseAdapter {
	private static final int LIST_NUM = 2;
	private Context mContext;
	private HMultiListView mMultiListView;
	private List<CollectionInfo> mCollectionDatas = new ArrayList<CollectionInfo>();

	public CollectionMultiAdapter(Context mContext, HMultiListView mMultiListView, List<CollectionInfo> mCollectionDatas) {
		this.mContext = mContext;
		this.mMultiListView = mMultiListView;
		this.mCollectionDatas = mCollectionDatas;
	}

	@Override
	public Object getItem(int position) {
		return mCollectionDatas.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new ImageItemLarge(mContext);
			ImageItemHolder holder1 = ((ImageItem) convertView).getHolder();
			holder1.title.setText(position + "");
		}
		convertView.setVisibility(View.VISIBLE);
		ImageItemHolder holder = ((ImageItem) convertView).getHolder();
//		Drawable drawable = mContext.getResources().getDrawable(R.drawable.store_poster_large);
//		holder.icon.setImageDrawable(drawable);
		
		CollectionInfo app = mCollectionDatas.get(position);
//		String iconUrl = app.getPosterIconUrl();
//		if (TextUtils.isEmpty(iconUrl)) {
//			iconUrl = app.getIconUrl();
//		}
		holder.icon.setScaleType(ScaleType.FIT_XY);
		String iconUrl = app.getIconUrl();
		ImageLoader.getInstance().displayImage(iconUrl, holder.icon);
		return convertView;
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
	public List<?> getData() {
		return mCollectionDatas;
	}

	@Override
	public View getEmptyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new ImageItemLarge(mContext);
		}
		convertView.setVisibility(View.GONE);
		return convertView;
	}

}
