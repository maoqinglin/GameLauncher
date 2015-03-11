package com.ireadygo.app.gamelauncher.ui.settings;

import java.util.List;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.ui.settings.SettingsItem.SettingsItemHoder;
import com.ireadygo.app.gamelauncher.ui.widget.SettingsIconView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class SettingsMultiAdapter implements HMultiBaseAdapter {

	private Context mContext;
	private List<SettingsInfo> mDataList;
	private HMultiListView mHListViews;
	private int mListViewNum;

	public SettingsMultiAdapter(Context context,List<SettingsInfo> list,int listViewNum,HMultiListView hListViews) {
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
			convertView = new SettingsItem(mContext);
		}
		convertView.setVisibility(View.VISIBLE);
		SettingsItemHoder holder = ((SettingsItem)convertView).getHolder();
		SettingsInfo info = mDataList.get(position);
		holder.icon.setImageDrawable(info.getItemIcon());
		holder.name.setText(info.getItemName());
		convertView.setTag(info);
		return convertView;
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
			convertView = new SettingsItem(mContext);
		}
		convertView.setVisibility(View.GONE);
		return convertView;
	}
}
