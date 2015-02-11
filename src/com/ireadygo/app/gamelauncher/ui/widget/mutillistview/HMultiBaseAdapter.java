package com.ireadygo.app.gamelauncher.ui.widget.mutillistview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class HMultiBaseAdapter extends BaseAdapter {

	List<?> mData;
	protected Context mContext;

	/**
	 * 子类必须重写空的构造方法
	 */
	public HMultiBaseAdapter(){
		
	}

	public HMultiBaseAdapter(List<?> data) {
		mData = data;
	}

	public void setContext(Context context){
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setData(List<?> data) {
		mData = data;
	}

	public List<?> getData() {
		return mData;
	}
}
