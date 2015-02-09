package com.ireadygo.app.gamelauncher.ui.store.collection;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.CollectionItem;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.store.StoreBaseContentLayout;
import com.ireadygo.app.gamelauncher.ui.store.StoreDetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.StoreEmptyView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class StoreCollectionLayout extends StoreBaseContentLayout {
	public static final String EXTRA_COLLECTION_ID = "CollectionId";
	public static final String EXTRA_POSTER_BG = "Poster_bg";
	private HListView mAppListView;
	private StoreCollectionAdapter mAdapter;
	private List<CollectionItem> mAppList = new ArrayList<CollectionItem>();

	public StoreCollectionLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StoreCollectionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StoreCollectionLayout(Context context, int layoutTag, StoreDetailActivity storeFragment) {
		super(context, layoutTag, storeFragment);
		init();
	}

	@Override
	protected void init() {
		super.init();
		LayoutInflater.from(getContext()).inflate(R.layout.store_collection_layout, this, true);
		mAppListView = (HListView) findViewById(R.id.storeCollectionList);
		mAppListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CollectionItem collection = mAppList.get(position);
				startCollectionDetailActivity(collection.getCollectionId(),collection.getPosterBgUrl());
			}

		});
		initData();
		mAdapter = new StoreCollectionAdapter(mAppList, mAppListView, getContext());
		mAppListView.setAdapter(mAdapter.toAnimationAdapter());
		loadData(1);
	}

	private void initData() {
		// mAppList.addAll(Utils.createAppList(10));
	}

	private void loadData(int page) {
		new LoadCollectionTask().execute(page);
		showLoadingProgress();
	}

	private void startCollectionDetailActivity(long collectionId,String posterUrl) {
		Intent intent = new Intent(getContext(), CollectionDetailActivity.class);
		intent.putExtra(EXTRA_COLLECTION_ID, collectionId);
		intent.putExtra(EXTRA_POSTER_BG, posterUrl);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		SoundPoolManager.instance(getContext()).play(SoundPoolManager.SOUND_ENTER);
		getContext().startActivity(intent);
	}

	@Override
	protected boolean isCurrentFocus() {
		return mAppListView.hasFocus();
	}

	@Override
	public boolean onSunKey() {
		int selectedIndex = mAppListView.getSelectedItemPosition();
		if (selectedIndex > -1 && selectedIndex < mAdapter.getCount()) {
			CollectionItem collection = mAppList.get(selectedIndex);
			startCollectionDetailActivity(collection.getCollectionId(),collection.getPosterBgUrl());
		}
		return super.onSunKey();
	}

	private class LoadCollectionTask extends AsyncTask<Integer, Void, List<CollectionItem>> {

		@Override
		protected List<CollectionItem> doInBackground(Integer... params) {
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
		protected void onPostExecute(List<CollectionItem> result) {
			dimissLoadingProgress();
			if (isCancelled() || result == null || result.isEmpty()) {
				return;
			}
			mAppList.clear();
			mAppList.addAll(result);
			mAdapter.notifyDataSetChanged();
		}
	}
}
