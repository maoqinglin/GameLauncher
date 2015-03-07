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
	private HMultiListView mMultiListView;

	public StoreMultiAdapter(Context mContext, List<StoreInfo> mStoreDatas, HMultiListView listView) {
		this.mContext = mContext;
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
		StoreInfo info = mStoreDatas.get(position);
		if (position < 8) {
			holder.title.setVisibility(View.GONE);
		} else {
			holder.title.setVisibility(View.VISIBLE);
			holder.title.setText(info.getTitleId());
		}
		holder.icon.setImageResource(info.getDrawableId());
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
		return mStoreDatas;
	}

	@Override
	public View getEmptyView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = new ImageItemSmall(mContext);
		}
		convertView.setVisibility(View.GONE);
		return convertView;
	}

}
