package com.ireadygo.app.gamelauncher.ui.store.collection;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppMultiAdapter;
import com.ireadygo.app.gamelauncher.ui.store.StoreEmptyView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.snail.appstore.openapi.AppPlatFormConfig;

public class CollectionDetailActivity extends BaseActivity implements OnClickListener {
	public static final String EXTRA_COLLECTION_ID = "CollectionId";
	private StatisticsTitleView mTitleLayout;
	private HMultiListView mMultiListView;
	private OperationTipsLayout mTipsLayout;
	private List<AppEntity> mApps = new ArrayList<AppEntity>();
	private HMultiBaseAdapter mAdapter;
	private GameInfoHub mGameInfoHub;
	private long mPageIndex = 1;
	private boolean mLoadingData = false;
	private long mCollectionId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_collection_detail_activity);
		mGameInfoHub = GameInfoHub.instance(this);
		mTitleLayout = (StatisticsTitleView)findViewById(R.id.title_layout);
		mTitleLayout.setTitle(R.string.collection_detail_title_prompt);
		
		mMultiListView = (HMultiListView)findViewById(R.id.collection_detail_list);
		mAdapter = new StoreAppMultiAdapter(this, mMultiListView, mApps);
		mMultiListView.setAdapter(mAdapter);
		
		mTipsLayout = (OperationTipsLayout)findViewById(R.id.tips_layout);
		mTipsLayout.setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		
		
		mCollectionId = getIntent().getLongExtra(EXTRA_COLLECTION_ID, -1);
		if (mCollectionId > 0) {
			loadCollectionDetail();
		}
		// mMultiListView.setOnScrollListener(new OnScrollListener() {
		//
		// @Override
		// public void onScrollStateChanged(AbsHListView view, int scrollState)
		// {
		//
		// }
		//
		// @Override
		// public void onScroll(AbsHListView view, int firstVisibleItem, int
		// visibleItemCount, int totalItemCount) {
		// if (!mLoadingData && firstVisibleItem >= totalItemCount -
		// visibleItemCount - 1) {
		// loadCategoryDetail();
		// }
		// }
		// });
		mMultiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position >= 0 && position < mApps.size()) {
					DetailActivity.startSelf(CollectionDetailActivity.this, mApps.get(position));
				}
			}
		});
		StoreEmptyView emptyView = new StoreEmptyView(this);
		emptyView.getRefreshBtn().setVisibility(View.GONE);
		emptyView.getTitleView().setText(R.string.store_empty_title);
		mMultiListView.setEmptyView(emptyView);
	}

	private void loadCollectionDetail() {
		if (!mLoadingData && mCollectionId > 0) {
			new LoadCollectionDetailTask().execute(mCollectionId + "", mPageIndex + "");
			mLoadingData = true;
		}
	}

	@Override
	public boolean onBackKey() {
		finish();
		return true;
	}

	private class LoadCollectionDetailTask extends AsyncTask<String, Void, List<AppEntity>> {

		@Override
		protected List<AppEntity> doInBackground(String... params) {
			if (params == null || params.length < 2) {
				return null;
			}
			String id = params[0];
			int page = Integer.parseInt(params[1]);
			try {
				return mGameInfoHub.obtainChildren(AppPlatFormConfig.DATA_TYPE_COLLECTION, id, page);
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<AppEntity> result) {
			if (result == null || result.isEmpty()) {
				mLoadingData = false;
				return;
			}
			mApps.addAll(result);
			mMultiListView.notifyDataSetChanged();
			mTitleLayout.setCount(mApps.size());
			mPageIndex++;
			mLoadingData = false;
		}
	}

	@Override
	public void finish() {
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
		super.finish();
	}

	@Override
	public boolean onSunKey() {
		// if (mMultiListView.hasFocus()) {
		// int selectIndex = mMultiListView.getSelectedItemPosition();
		// if (selectIndex >= 0) {
		// mMultiListView.performItemClick(mMultiListView, selectIndex, 0);
		// }
		// }
		return true;
	}

	@Override
	public boolean onMoonKey() {
		finish();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.goback:
			onMoonKey();
			break;
		default:
			break;
		}
	}

	public static void startSelf(Context context, long collectionId) {
		Intent intent = new Intent(context, CollectionDetailActivity.class);
		intent.putExtra(EXTRA_COLLECTION_ID, collectionId);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
