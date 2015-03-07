package com.ireadygo.app.gamelauncher.ui.store;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class StoreFragment extends BaseContentFragment {
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mMultiBaseAdapter;
	private List<StoreInfo> mStoreDatas = new ArrayList<StoreInfo>();

	public StoreFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.store_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		initData();
		mMultiListView = (HMultiListView) view.findViewById(R.id.store_list);
		mMultiBaseAdapter = new StoreMultiAdapter(getRootActivity(), mStoreDatas, mMultiListView);
		mMultiListView.setAdapter(mMultiBaseAdapter);
		mMultiListView.setOnItemClickListener(mOnItemClickListener);
		bindPagingIndicator(mMultiListView);
	}

	@Override
	protected boolean isCurrentFocus() {
		return mMultiListView.hasFocus();
	}

	private void initData() {
		mStoreDatas.clear();
		// 0
		Anchor anchor = new Anchor(Destination.GAME_DETAIL);
		// TODO 需要传入AppEntity
		StoreInfo info = new StoreInfo(R.drawable.store_poster_large, anchor);
		mStoreDatas.add(info);

		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mStoreDatas.add(info);

		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mStoreDatas.add(info);

		info = new StoreInfo(R.drawable.store_poster_large, anchor);
		mStoreDatas.add(info);

		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mStoreDatas.add(info);

		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mStoreDatas.add(info);

		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mStoreDatas.add(info);
		// 7
		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mStoreDatas.add(info);
		// 搜索
		anchor = new Anchor(Destination.STORE_SEARCH);
		info = new StoreInfo(R.drawable.store_icon_search, R.string.store_menu_search, anchor);
		mStoreDatas.add(info);

		anchor = new Anchor(Destination.STORE_CATEGORY);
		info = new StoreInfo(R.drawable.store_icon_category, R.string.store_menu_category, anchor);
		mStoreDatas.add(info);

		anchor = new Anchor(Destination.STORE_COLLECTION);
		info = new StoreInfo(R.drawable.store_icon_collection, R.string.store_menu_collection, anchor);
		mStoreDatas.add(info);

		anchor = new Anchor(Destination.STORE_FAVORITE_APPS);
		info = new StoreInfo(R.drawable.store_icon_favorite_apps, R.string.store_menu_app, anchor);
		mStoreDatas.add(info);

		info = new StoreInfo(R.drawable.store_icon_manager, R.string.store_menu_manager, anchor);
		mStoreDatas.add(info);
	}

	// @Override
	// public boolean onSunKey() {
	// View selectedView = mAdapter.getSelectedView();
	// int pos = mAdapter.getSelectedPos();
	// if (selectedView != null && pos != -1) {
	// mListView.performItemClick(selectedView, pos, 0);
	// return true;
	// }
	// return super.onSunKey();
	// }

	@Override
	public boolean onMoonKey() {
		return onBackKey();
	}

	@Override
	public boolean onBackKey() {
		return getMenu().getCurrentItem().requestFocus();
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			StoreInfo info = mStoreDatas.get(position);
			Anchor anchor = info.getAnchor();
			if (anchor != null) {
				Intent intent = anchor.getIntent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				getRootActivity().startActivity(intent);
				SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
			}
		}
	};
}
