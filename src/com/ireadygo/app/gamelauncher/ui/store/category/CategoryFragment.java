package com.ireadygo.app.gamelauncher.ui.store.category;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class CategoryFragment extends BaseContentFragment {
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mAdapter;

	public CategoryFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.category_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		mMultiListView = (HMultiListView) view.findViewById(R.id.category_list);
		mAdapter = new CategoryMultiAdapter(getRootActivity(), mMultiListView);
		mMultiListView.setAdapter(mAdapter);
	}

	@Override
	protected boolean isCurrentFocus() {
		return mMultiListView.hasFocus();
	}

}
