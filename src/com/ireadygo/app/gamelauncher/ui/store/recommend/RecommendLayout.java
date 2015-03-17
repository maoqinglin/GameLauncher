package com.ireadygo.app.gamelauncher.ui.store.recommend;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.BannerItem;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppNormalAdapter;
import com.ireadygo.app.gamelauncher.ui.store.StoreBaseContentLayout;
import com.ireadygo.app.gamelauncher.ui.store.StoreDetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.StoreEmptyView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView.OnScrollListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class RecommendLayout extends StoreBaseContentLayout {
	private static final int TYPE_GAME = 1;
	private static final int TYPE_COMP = 2;
	private static final String TYPE_REDACT = "3";
	private HListView mListView;
	private StoreAppNormalAdapter mAdapter;
	private List<AppEntity> mAppList = new ArrayList<AppEntity>();
	private int mPageIndex = 1;
	private boolean mLoadingData = false;
	private Context mContext;

	public RecommendLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public RecommendLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public RecommendLayout(Context context, int layoutTag, StoreDetailActivity storeFragment) {
		super(context, layoutTag, storeFragment);
		mContext = context;
		init();
	}

	@Override
	protected void init() {
		super.init();
		LayoutInflater.from(getContext()).inflate(R.layout.store_recommend_layout, this, true);
		mListView = (HListView) findViewById(R.id.storeRecommendList);
		initListView();
	}

	private void initListView(){
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsHListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (!mLoadingData && firstVisibleItem >= totalItemCount - visibleItemCount - 1) {
					loadData();
				}
			}
		});
		mListView.setOnItemClickListener(mOnItemClickListener);
		initData();
		mAdapter = new StoreAppNormalAdapter(mContext, mListView, mAppList);
		mListView.setAdapter(mAdapter.toAnimationAdapter());
		loadData();
	}
	
	OnItemClickListener mOnItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AppEntity entity = (AppEntity)mAdapter.getItem(position);
			Intent intent = new Intent(mContext, DetailActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundle = new Bundle();
			bundle.putParcelable(DetailActivity.EXTRAS_APP_ENTITY, entity);
			intent.putExtras(bundle);
			SoundPoolManager.instance(mContext).play(SoundPoolManager.SOUND_ENTER);
			mContext.startActivity(intent);
		}
	};

	private void initData() {
		// mAppList.addAll(Utils.createAppList(10));
	}

	private void loadData() {
		if (!mLoadingData) {
			if(mAppList.isEmpty()){
				showLoadingProgress();
			}
			new LoadRecommendTask().execute(mPageIndex);
			mLoadingData = true;
		}
	}


	@Override
	protected boolean isCurrentFocus() {
		return mListView.hasFocus();
	}

	@Override
	public boolean onSunKey() {
		View selectedView = mListView.getSelectedView();
		int pos = mListView.getSelectedItemPosition();
		if (selectedView != null && pos != -1) {
			mListView.performItemClick(selectedView, pos, 0);
			return true;
		}
		return super.onSunKey();
	}

	private class LoadRecommendTask extends AsyncTask<Integer, Void, List<BannerItem>> {

		@Override
		protected List<BannerItem> doInBackground(Integer... params) {
			if (params == null || params.length == 0) {
				return null;
			}
			int page = params[0];
			if (page < 0) {
				return null;
			}
			try {
				return getGameInfoHub().obtainBannerList(page);
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<BannerItem> result) {
			dimissLoadingProgress();
			StoreEmptyView emptyView = new StoreEmptyView(mContext);
//			emptyView.getTitleView().setText(R.string.store_load_empty_title);
			Utils.setEmptyView(emptyView, mListView);
			if (isCancelled() || result == null || result.isEmpty()) {
				return;
			}
			List<AppEntity> appList = new ArrayList<AppEntity>();
			for (BannerItem banner : result) {
				if (TYPE_REDACT.equals(banner.getCPostion())) {
					appList.add(bannerItemToAppEntity(banner));
				}
			}
			mAppList.addAll(appList);
			mAdapter.notifyDataSetChanged();
			mPageIndex++;
			mLoadingData = false;
		}

		private AppEntity bannerItemToAppEntity(BannerItem bannerItem) {
			AppEntity appEntity = new AppEntity();
			appEntity.setAppId(String.valueOf(bannerItem.getNParamId()));
			appEntity.setDescription(bannerItem.getSInfo());
			appEntity.setName(bannerItem.getSAppName());
			appEntity.setRemoteIconUrl(bannerItem.getCIcon());
			appEntity.setVersionName(bannerItem.getCVersionName());
			appEntity.setVersionCode(bannerItem.getIVersionCode());
			appEntity.setPkgName(bannerItem.getCPackage());
			appEntity.setFreeFlag(bannerItem.getIFlowFree());
			appEntity.setSign(bannerItem.getCMd5());
			appEntity.setTotalSize(bannerItem.getISize());
			return appEntity;
		}
	}
}
