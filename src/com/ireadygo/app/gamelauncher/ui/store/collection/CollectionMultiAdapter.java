package com.ireadygo.app.gamelauncher.ui.store.collection;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryItem;
import com.ireadygo.app.gamelauncher.ui.item.ImageItem;
import com.ireadygo.app.gamelauncher.ui.item.ImageItem.ImageItemHolder;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class CollectionMultiAdapter implements HMultiBaseAdapter {
	private static final int LIST_NUM = 2;
	private Context mContext;
	private HMultiListView mMultiListView;
	private List<CategoryItem> mCollectionDatas = new ArrayList<CategoryItem>();

	public CollectionMultiAdapter(Context mContext, HMultiListView mMultiListView, List<CategoryItem> mCollectionDatas) {
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
			convertView = new ImageItem(mContext);
			ImageItemHolder holder1 = ((ImageItem) convertView).getHolder();
			holder1.title.setText(position + "");
		}
		ImageItemHolder holder = ((ImageItem) convertView).getHolder();
		Drawable drawable = mContext.getResources().getDrawable(R.drawable.store_poster_large);
		holder.icon.setImageDrawable(drawable);
		LayoutParams params = new LayoutParams(drawable.getMinimumWidth(), drawable.getMinimumHeight());
		holder.background.setLayoutParams(params);
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

}
