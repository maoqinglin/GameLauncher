package com.ireadygo.app.gamelauncher.ui.widget.mutillistview;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public interface HMultiBaseAdapter {

	Object getItem(int position);

	View getView(int position, View convertView, ViewGroup parent);

	BaseAdapter getAdapter();

	int getHListNum();

	List<?> getData();
}
