package com.ireadygo.app.gamelauncher.ui.store.recommend;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.BannerItem;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppMultiAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView.OnScrollListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.PagingIndicator.HListViewIndicatorInfo;
import com.ireadygo.app.gamelauncher.ui.widget.PagingIndicator.Interpolation;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class RecommendFragment extends BaseContentFragment {
	private List<AppEntity> mAppEntities = new ArrayList<AppEntity>();
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mAdapter;
	private StatisticsTitleView mTitleLayout;
	private static final String BANNER_POSITION = "3";
	private int mCurrPageIndex = 1;
	private boolean mLoadingData = false;

	public RecommendFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.recommand_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mTitleLayout = (StatisticsTitleView) view.findViewById(R.id.title_layout);
		mTitleLayout.setTitle(R.string.recommend_title_prompt);
		mTitleLayout.setCount(mAppEntities.size());

		mMultiListView = (HMultiListView) view.findViewById(R.id.recommand_list);
		mAdapter = new StoreAppMultiAdapter(getRootActivity(), mMultiListView, mAppEntities);
		mMultiListView.setAdapter(mAdapter);
		bindPagingIndicator(mMultiListView);
		setEmptyView(mMultiListView, R.string.store_empty_title, View.GONE, 0);
		if(mAppEntities.size() == 0){
			loadData(1);
		}
		mMultiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DetailActivity.startSelf(getRootActivity(), mAppEntities.get(position));
			}

		});
		mMultiListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsHListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsHListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (!mLoadingData
						&& firstVisibleItem >= totalItemCount
								- visibleItemCount - 1) {
					Log.i("chenrui", "The pageIndex : " + mCurrPageIndex);
					loadData(mCurrPageIndex);
				}
			}
		});
	}

	private void loadData(int page) {
		if(!mLoadingData) {
			new LoadDataTask().execute(page);
//			showLoadingProgress();
			mLoadingData = true;
		}
	}

	@Override
	protected boolean isCurrentFocus() {
		return mMultiListView.hasFocus();
	}

	private class LoadDataTask extends AsyncTask<Integer, Void, List<BannerItem>> {

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
			super.onPostExecute(result);
//			dimissLoadingProgress();
			if (isCancelled() || result == null || result.isEmpty()) {
				mLoadingData = false;
				return;
			}
			List<AppEntity> appList = new ArrayList<AppEntity>();
			for (BannerItem banner : result) {
				if (BANNER_POSITION.equals(banner.getCPostion())) {
					appList.add(bannerItemToAppEntity(banner));
				}
			}
			mAppEntities.addAll(appList);
			if (mMultiListView != null) {
				mMultiListView.notifyDataSetChanged();
			}
			if(mTitleLayout != null){
				mTitleLayout.setCount(mAppEntities.size());
			}
			mCurrPageIndex++;
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

	private Interpolation mInterpolation = new Interpolation() {

		@Override
		public HListViewIndicatorInfo calcHListIndicatorInfo(HListView listView) {
			int firstPos = listView.getFirstVisiblePosition();
			int paddingLeft = listView.getPaddingLeft();
			int paddingRight = listView.getPaddingRight();
			int listWidth = listView.getWidth();
			// View firstItem = listView.getChildAt(0);
			View largeItem = listView.getChildAt(0);
			View smallItem = listView.getChildAt(1);
			int scrollX = 0;
			if (firstPos == 0) {
				scrollX = paddingLeft - largeItem.getLeft();
			} else {
				scrollX = largeItem.getWidth() + smallItem.getWidth() * (firstPos - 1) + listView.getDividerWidth()
						* firstPos + paddingLeft - largeItem.getLeft();
			}
			int totalWidth = paddingLeft + paddingRight + listView.getDividerWidth() * (listView.getCount() - 1)
					+ largeItem.getWidth() + smallItem.getWidth() * (listView.getCount() - 1);
			HListViewIndicatorInfo info = new HListViewIndicatorInfo();
			info.scrollX = scrollX;
			info.listWidth = listWidth;
			info.listTotalWidth = totalWidth;
			Log.d("liu.js", "StoreFragment--scrollX=" + scrollX + "|totalWidth=" + totalWidth);
			return info;
		}
	};
}
