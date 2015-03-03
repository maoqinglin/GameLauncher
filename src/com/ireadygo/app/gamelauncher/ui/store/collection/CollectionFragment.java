package com.ireadygo.app.gamelauncher.ui.store.collection;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.CollectionInfo;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class CollectionFragment extends BaseContentFragment {
	public static final String EXTRA_COLLECTION_ID = "CollectionId";
	public static final String EXTRA_POSTER_BG = "Poster_bg";
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mAdapter;
	private StatisticsTitleView mTitleLayout;
	private View mSelectedView;
	private List<CollectionInfo> mAppList = new ArrayList<CollectionInfo>();

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
		mTitleLayout = (StatisticsTitleView)view.findViewById(R.id.title_layout);
		mTitleLayout.setCount(269);
		mTitleLayout.setTitle(R.string.collection_prompt);
		
		mMultiListView = (HMultiListView) view.findViewById(R.id.collection_list);
		for(int i = 0; i < 10 ; i ++){
			mAppList.add(null);
		}
		mAdapter = new CollectionMultiAdapter(getRootActivity(), mMultiListView, mAppList);
		mMultiListView.setAdapter(mAdapter);
		loadData(1);
	}

	private void loadData(int page) {
		new LoadCollectionTask().execute(page);
		showLoadingProgress();
	}

	private void startCollectionDetailActivity(long collectionId,String posterUrl) {
		Intent intent = new Intent(getRootActivity(), CollectionDetailActivity.class);
		intent.putExtra(EXTRA_COLLECTION_ID, collectionId);
		intent.putExtra(EXTRA_POSTER_BG, posterUrl);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
		getRootActivity().startActivity(intent);
	}
	
	@Override
	protected boolean isCurrentFocus() {
		return mMultiListView.hasFocus();
	}

	@Override
	public boolean onSunKey() {
		int selectedIndex = mMultiListView.getSelectedItemPosition();
		if (selectedIndex > -1 && selectedIndex < mAppList.size()) {
			CollectionInfo collection = mAppList.get(selectedIndex);
			startCollectionDetailActivity(collection.getCollectionId(),collection.getPosterBgUrl());
		}
		return super.onSunKey();
	}
	
	private class LoadCollectionTask extends AsyncTask<Integer, Void, List<CollectionInfo>> {

		@Override
		protected List<CollectionInfo> doInBackground(Integer... params) {
			if (params == null || params.length == 0) {
				return null;
			}
			int page = params[0];
			if (page < 0) {
				return null;
			}
			try {
				return getGameInfoHub().obtainCollection(page);
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<CollectionInfo> result) {
			dimissLoadingProgress();
			if (isCancelled() || result == null || result.isEmpty()) {
				return;
			}
			mAppList.clear();
			//TODO
			for(int i = 0; i < 20; i ++){
				mAppList.add(result.get(0));
			}
//			mAppList.addAll(result);
			mMultiListView.notifyDataSetChanged();
		}
	}
}
