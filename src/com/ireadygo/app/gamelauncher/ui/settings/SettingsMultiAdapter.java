package com.ireadygo.app.gamelauncher.ui.settings;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.SettingsIconView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListBaseAdapter;

public class SettingsMultiAdapter implements HMultiListBaseAdapter {

	private Context mContext;
	private List<SettingsItemEntity> mDataList;
	private List<BaseAdapter> mSettingsAdapters = new ArrayList<BaseAdapter>();
	private List<HListView> mHListViews;
	private int mListViewNum;
	private List<List<?>> mDataLists = new ArrayList<List<?>>();

	public SettingsMultiAdapter(Context context,List<SettingsItemEntity> list,int listViewNum,List<HListView> hListViews) {
		mContext = context;
		mDataList = list;
		mListViewNum = listViewNum;
		mHListViews = hListViews;
		init();
	}

	private void init() {
		initDataLists();
		initAdapters();
	}

	private void initAdapters() {
		mSettingsAdapters.clear();
		for (int i = 0; i < mDataLists.size(); i++) {
			SettingsAdapter settingsAdapter = new SettingsAdapter(mContext, mHListViews.get(i), (List<SettingsItemEntity>)mDataLists.get(i));
			mSettingsAdapters.add(settingsAdapter);
		}
	}

	private void initDataLists() {
		mDataLists.clear();
		for (int i = 0; i < mListViewNum; i++) {
			List<SettingsItemEntity> dataList = new ArrayList<SettingsItemEntity>();
			mDataLists.add(dataList);
		}
		for (int j = 0; j < mDataList.size(); j++) {
			int dataListIndex = j % mListViewNum;
			List<SettingsItemEntity> dataList = (List<SettingsItemEntity>)mDataLists.get(dataListIndex);
			dataList.add(mDataList.get(j));
		}
	}

	public List<SettingsItemEntity> getList() {
		return mDataList;
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
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
	public List<BaseAdapter> getAdapters() {
		return mSettingsAdapters;
	}

	@Override
	public void setAdaters(List<BaseAdapter> adapters) {
		mSettingsAdapters = adapters;
	}

	@Override
	public int getAdatersNum() {
		return mSettingsAdapters.size();
	}

	@Override
	public List<List<?>> getDataList() {
		return mDataLists;
	}
}
