package com.ireadygo.app.gamelauncher.ui.settings;

import java.util.List;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.game.info.ItemInfo;
import com.ireadygo.app.gamelauncher.ui.widget.SettingsIconView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class SettingsMultiAdapter implements HMultiBaseAdapter {

	private Context mContext;
	private List<SettingsItemEntity> mDataList;
	private HMultiListView mHListViews;
	private int mListViewNum;

	public SettingsMultiAdapter(Context context,List<SettingsItemEntity> list,int listViewNum,HMultiListView hListViews) {
		mContext = context;
		mDataList = list;
		mListViewNum = listViewNum;
		mHListViews = hListViews;
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = newView(position, convertView, parent);
		}
		bindView(position, convertView);
		return convertView;
	}

	public View newView(int position, View convertView, ViewGroup parent) {
		SettingsIconView gameIcon = (SettingsIconView) LayoutInflater.from(mContext).inflate(R.layout.settings_item,
				parent, false);
		ViewHolder holder = new ViewHolder();
		holder.settingsIcon = gameIcon;
		gameIcon.setTag(holder);
		return gameIcon;
	}

	public void bindView(final int position, final View convertView) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (null != holder) {
			holder.settingsItem = mDataList.get(position);
			holder.settingsIcon.setSettingsItemEntity(holder.settingsItem);
		}
	}

	public static class ViewHolder {
		public SettingsIconView settingsIcon;
		public SettingsItemEntity settingsItem;
	}

	public Animator selectedAnimator(View view) {
		return ((SettingsIconView) view).selectedAnimation();
	}

	public Animator unselectedAnimator(View view) {
		return ((SettingsIconView) view).unselectedAnimation();
	}

	@Override
	public BaseAdapter getAdapter() {
		return mHListViews.getAdapter();
	}

	@Override
	public List<?> getData() {
		return mDataList;
	}

	@Override
	public int getHListNum() {
		return mListViewNum;
	}

	@Override
	public View getEmptyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = newView(position, convertView, parent);
		}
		convertView.setVisibility(View.GONE);
		return convertView;
	}
}
