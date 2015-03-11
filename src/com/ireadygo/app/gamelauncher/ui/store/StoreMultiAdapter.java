package com.ireadygo.app.gamelauncher.ui.store;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.ui.item.ImageItem;
import com.ireadygo.app.gamelauncher.ui.item.ImageItem.ImageItemHolder;
import com.ireadygo.app.gamelauncher.ui.item.ImageItemLarge;
import com.ireadygo.app.gamelauncher.ui.item.ImageItemSmall;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class StoreMultiAdapter implements HMultiBaseAdapter {
	private Context mContext;
	private List<StoreInfo> mStoreDatas = new ArrayList<StoreInfo>();
	private List<StoreInfo> mRecommendDatas = new ArrayList<StoreInfo>();// 位置0-7的数据，从服务器获取
	private HMultiListView mMultiListView;

	public StoreMultiAdapter(Context mContext, List<StoreInfo> recommendDatas, List<StoreInfo> mStoreDatas,
			HMultiListView listView) {
		this.mContext = mContext;
		this.mRecommendDatas = recommendDatas;
		this.mStoreDatas = mStoreDatas;
		this.mMultiListView = listView;
	}

	@Override
	public Object getItem(int position) {
		return mStoreDatas.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageItem item;
		if (position == 0 || position == 3) {
			item = new ImageItemLarge(mContext);
		} else {
			item = new ImageItemSmall(mContext);
		}
		ImageItemHolder holder = item.getHolder();
		StoreInfo info;
		if (position < mRecommendDatas.size()) {
			info = mRecommendDatas.get(position);
			holder.title.setVisibility(View.GONE);
		} else {
			info = mStoreDatas.get(position - mRecommendDatas.size());
			holder.title.setVisibility(View.VISIBLE);
			holder.title.setText(info.getTitleId());
		}
		if (info.getDrawable() != null) {
			holder.icon.setImageDrawable(info.getDrawable());
		} else {
			holder.icon.setImageResource(info.getDrawableId());
		}
		return item;
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
		ArrayList<StoreInfo> infoList = new ArrayList<StoreInfo>();
		infoList.addAll(mRecommendDatas);
		infoList.addAll(mStoreDatas);
		return infoList;
	}

	@Override
	public View getEmptyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new ImageItemSmall(mContext);
		}
		convertView.setVisibility(View.GONE);
		return convertView;
	}

}
