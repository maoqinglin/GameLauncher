package com.ireadygo.app.gamelauncher.ui.widget.mutillistview;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public interface HMultiBaseAdapter {

	Object getItem(int position);

	View getView(int arg0, View arg1, ViewGroup arg2);

	BaseAdapter getAdapter();

	int getHListNum();

	List<?> getData();
}
