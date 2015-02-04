package com.ireadygo.app.gamelauncher.ui.store;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.MenuFragment;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class StoreFragment extends BaseContentFragment {
	private HListView mListView;
	private StoreAdapter mAdapter;
	private static List<StoreOptionsPoster> sPosterItems = new ArrayList<StoreFragment.StoreOptionsPoster>();

	public StoreFragment(Activity activity, MenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	static {
		initPosterData();
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.store, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mListView = (HListView) view.findViewById(R.id.storeList);
		mAdapter = new StoreAdapter(getRootActivity(), mListView, sPosterItems);
		mListView.setAdapter(mAdapter.toAnimationAdapter());
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	private static void initPosterData() {
		StoreOptionsPoster posterItem = new StoreOptionsPoster();
		posterItem.drawableId = R.drawable.store_recommend_poster_icon_default;
		posterItem.titleId = R.string.store_options_recommend;
		sPosterItems.add(posterItem);

		posterItem = new StoreOptionsPoster();
		posterItem.drawableId = R.drawable.store_category_poster_icon_default;
		posterItem.titleId = R.string.store_options_category;
		sPosterItems.add(posterItem);

		posterItem = new StoreOptionsPoster();
		posterItem.drawableId = R.drawable.store_collection_poster_icon_default;
		posterItem.titleId = R.string.store_options_collection;
		sPosterItems.add(posterItem);

		posterItem = new StoreOptionsPoster();
		posterItem.drawableId = R.drawable.store_search_poster_icon_default;
		posterItem.titleId = R.string.store_options_search;
		sPosterItems.add(posterItem);

		posterItem = new StoreOptionsPoster();
		posterItem.drawableId = R.drawable.store_downloadmanage_poster_icon_default;
		posterItem.titleId = R.string.store_options_game_manager;
		sPosterItems.add(posterItem);

		//屏蔽商店设置 modify by linmaoqing 2015-2-4
//		posterItem = new StoreOptionsPoster();
//		posterItem.drawableId = R.drawable.store_settings_poster_icon_default;
//		posterItem.titleId = R.string.store_options_settings;
//		sPosterItems.add(posterItem);
	}

	@Override
	protected boolean isCurrentFocus() {
		return mListView.hasFocus();
	}

	@Override
	public boolean onSunKey() {
		View selectedView = mAdapter.getSelectedView();
		int pos = mAdapter.getSelectedPos();
		if (selectedView != null && pos != -1) {
			mListView.performItemClick(selectedView, pos, 0);
			return true;
		}
		return super.onSunKey();
	}

	@Override
	public boolean onMoonKey() {
		getMenu().getCurrentItem().requestFocus();
		return true;
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}

	static class StoreOptionsPoster {
		int drawableId;
		int titleId;
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Anchor anchor = null;
			switch (position) {
			case 0:
				anchor = new Anchor(Destination.STORE_RECOMMEND);
				break;
			case 1:
				anchor = new Anchor(Destination.STORE_CATEGORY);
				break;
			case 2:
				anchor = new Anchor(Destination.STORE_COLLECTION);
				break;
			case 3:
				anchor = new Anchor(Destination.STORE_SEARCH);
				break;
			case 4:
				anchor = new Anchor(Destination.STORE_GAME_MANAGE);
				break;
			case 5:
				anchor = new Anchor(Destination.STORE_SETTINGS);
				break;
			}
			if(anchor != null){
				Intent intent = anchor.getIntent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				getRootActivity().startActivity(intent);
				SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
			}
		}
	};

	protected View getCurrentFocusView() {
		if (isCurrentFocus()) {
			return mListView;
		}
		return null;
	}

	@Override
	public Animator outAnimator(AnimatorListener listener) {
		if (mAdapter != null) {
			return mAdapter.outAnimator(listener);
		}
		return null;
	}

	@Override
	public int getOutAnimatorDuration() {
		if (mAdapter != null) {
			return mAdapter.getOutAnimatorDuration();
		}
		return 0;
	}
}
