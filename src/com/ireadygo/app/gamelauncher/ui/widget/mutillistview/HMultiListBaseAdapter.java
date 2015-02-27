package com.ireadygo.app.gamelauncher.ui.widget.mutillistview;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public interface HMultiListBaseAdapter {

	int getCount();
	Object getItem(int position);
	long getItemId(int position);
	View getView(int position, View convertView, ViewGroup parent);

	List<BaseAdapter> getAdapters();
	void setAdaters(List<BaseAdapter> adapters);
	int getAdatersNum();
	List<List<?>> getDataList();

}
