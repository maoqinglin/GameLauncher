package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.ui.item.AppItem;
import com.ireadygo.app.gamelauncher.ui.item.AppItem.AppItemHolder;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CategoryDetailMultiAdapter implements HMultiBaseAdapter {

	private static final int LIST_NUM = 2;
	private Context mContext;
	private HMultiListView mMultiListView;
	private List<AppEntity> mAppEntities = new ArrayList<AppEntity>();
	private Drawable mDefaultIcon;

	public CategoryDetailMultiAdapter(Context context, HMultiListView multiListView, List<AppEntity> appEntities) {
		this.mContext = context;
		this.mMultiListView = multiListView;
		this.mAppEntities = appEntities;
		mDefaultIcon = context.getResources().getDrawable(R.drawable.store_app_icon_normal);
	}

	@Override
	public Object getItem(int position) {
		return mAppEntities.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new AppItem(mContext);
		}
		AppItemHolder holder = ((AppItem) convertView).getHolder();
		AppEntity app = mAppEntities.get(position);
		String iconUrl = app.getPosterIconUrl();
		holder.icon.setImageDrawable(mDefaultIcon);
		if (TextUtils.isEmpty(iconUrl)) {
			iconUrl = app.getRemoteIconUrl();
		}
		ImageLoader.getInstance().displayImage(iconUrl, holder.icon);
		holder.title.setText(app.getName());
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
		return mAppEntities;
	}

	@Override
	public void addEmptyData() {
		
	}

}
