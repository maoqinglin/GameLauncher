package com.ireadygo.app.gamelauncher.ui.store.collection;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.category.CategoryDetailMultiAdapter;
import com.ireadygo.app.gamelauncher.ui.store.category.CategoryLayout;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.snail.appstore.openapi.AppPlatFormConfig;

public class CollectionDetailActivity extends BaseActivity implements OnClickListener {
	private StatisticsTitleView mTitleLayout;
	private HMultiListView mMultiListView;
	private OperationTipsLayout mTipsLayout;
	private List<AppEntity> mApps = new ArrayList<AppEntity>();
	private HMultiBaseAdapter mAdapter;
	private GameInfoHub mGameInfoHub;
	private long mPageIndex = 1;
	private boolean mLoadingData = false;
	private long mCategoryId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_collection_detail_activity);
		mGameInfoHub = GameInfoHub.instance(this);
		mTitleLayout = (StatisticsTitleView)findViewById(R.id.title_layout);
		mTitleLayout.setTitle(R.string.collection_detail_prompt);
		mTitleLayout.setCount(255);
		
		mMultiListView = (HMultiListView)findViewById(R.id.collection_detail_list);
		mAdapter = new CategoryDetailMultiAdapter(this, mMultiListView, mApps);
		mMultiListView.setAdapter(mAdapter);
		
		mTipsLayout = (OperationTipsLayout)findViewById(R.id.tips_layout);
		mTipsLayout.setAllVisible(View.VISIBLE);
		
		mCategoryId = getIntent().getLongExtra(CategoryLayout.EXTRA_CATEGORY_ID, -1);
		if (mCategoryId > 0) {
			loadCategoryDetail();
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
	}

	private void loadCategoryDetail() {
		if (!mLoadingData && mCategoryId > 0) {
			new LoadCategoryDetailTask().execute(mCategoryId + "", mPageIndex + "");
			mLoadingData = true;
		}
	}

	@Override
	public boolean onBackKey() {
		finish();
		return true;
	}

	private class LoadCategoryDetailTask extends AsyncTask<String, Void, List<AppEntity>> {

		@Override
		protected List<AppEntity> doInBackground(String... params) {
			if (params == null || params.length < 2) {
				return null;
			}
			String id = params[0];
			int page = Integer.parseInt(params[1]);
			try {
				return mGameInfoHub.obtainChildren(AppPlatFormConfig.DATA_TYPE_CATEGORY, id, page);
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

	public static void startSelf(Context context, long categoryId) {
		Intent intent = new Intent(context, CollectionDetailActivity.class);
		intent.putExtra(CategoryLayout.EXTRA_CATEGORY_ID, categoryId);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
