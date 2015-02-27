package com.ireadygo.app.gamelauncher.ui.store.collection;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryItem;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.item.BaseAdapterItem;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemSelectedListener;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class CollectionFragment extends BaseContentFragment {
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mAdapter;
	private View mSelectedView;

	public CollectionFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.collection_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		mMultiListView = (HMultiListView) view.findViewById(R.id.collection_list);
		List<CategoryItem> infoList = new ArrayList<CategoryItem>();
		for (int i = 0; i < 20; i++) {
			infoList.add(new CategoryItem(0, "", "", "", "", ""));
		}
		mAdapter = new CollectionMultiAdapter(getRootActivity(), mMultiListView, infoList);
		mMultiListView.setAdapter(mAdapter);
	}

	@Override
	protected boolean isCurrentFocus() {
		return false;
	}

}
