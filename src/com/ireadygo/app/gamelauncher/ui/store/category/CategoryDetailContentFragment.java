package com.ireadygo.app.gamelauncher.ui.store.category;

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
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryInfo;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppMultiAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.PagingIndicator;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView.OnScrollListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.snail.appstore.openapi.AppPlatFormConfig;

public class CategoryDetailContentFragment extends BaseContentFragment {
	private List<AppEntity> mAppEntities = new ArrayList<AppEntity>();
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mMultiAdapter;
	private long mCategoryId;
	private GameInfoHub mGameInfoHub;
	private long mCurrPageIndex = 1;
	private boolean mLoadingData = false;
	private static final int PAGE_NUMBER = 50;
	private StatisticsTitleView mTitleLayout;
	private CategoryInfo mCategoryInfo;
	private static final String TYPE_CATEGORY = "1";
	private static final String TYPE_TAG = "2";

	public CategoryDetailContentFragment(Activity activity, BaseMenuFragment menuFragment, CategoryInfo categoryInfo) {
		super(activity, menuFragment);
		this.mCategoryInfo = categoryInfo;
		mGameInfoHub = GameInfoHub.instance(activity);
		if(mCategoryInfo != null){
			loadCategoryDetail();
		}
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.category_detail_content_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(View.VISIBLE, TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mMultiListView = (HMultiListView) view.findViewById(R.id.category_detail_list);
		mMultiAdapter = new StoreAppMultiAdapter(getRootActivity(), mMultiListView, mAppEntities);
		mMultiListView.setAdapter(mMultiAdapter);
		setEmptyView(mMultiListView, R.string.store_empty_title, View.GONE, 0);
		mTitleLayout = (StatisticsTitleView) view.findViewById(R.id.title_layout);
		if(mCategoryInfo != null){
			updateNextFocusIdByCategoryId((int) mCategoryInfo.getCategoryId());
			mTitleLayout.setCount(mCategoryInfo.getAppCounts());
		}
		mMultiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DetailActivity.startSelf(getRootActivity(), mAppEntities.get(position));
			}
		});
		bindPagingIndicator(mMultiListView);
		mMultiListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsHListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsHListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (!mLoadingData
						&& firstVisibleItem >= totalItemCount
								- visibleItemCount - 1) {
					Log.i("chenrui", "The pageIndex : " + mCurrPageIndex);
					loadCategoryDetail();
				}
				PagingIndicator indicator = getMenuActivity().getPagingIndicator();
				if(indicator != null){
					indicator.onScroll((HListView) view);
				}
			}
		});
	}

	@Override
	protected boolean isCurrentFocus() {
		return mMultiListView.hasFocus();
	}

	private void loadCategoryDetail() {
		if (!mLoadingData && mCategoryInfo.getCategoryId() > 0) {
			new LoadCategoryDetailTask().execute(mCategoryInfo.getCategoryId() + "", mCurrPageIndex + "",mCategoryInfo.getCategoryType());
			mLoadingData = true;
		}
	}

	private class LoadCategoryDetailTask extends AsyncTask<String, Void, List<AppEntity>> {

		@Override
		protected List<AppEntity> doInBackground(String... params) {
			if (params == null || params.length < 3) {
				return null;
			}
			String id = params[0];
			int page = Integer.parseInt(params[1]);
			
			try {
				if(TYPE_CATEGORY.equals(params[2])){
					return mGameInfoHub.obtainCategotyChildren(id, page, String.valueOf(AppPlatFormConfig.IPLATFORMID), PAGE_NUMBER);
				}else if(TYPE_TAG.equals(params[2])){
					return mGameInfoHub.obtainCategotyTagChildren(id, page);
				}
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
			mAppEntities.addAll(result);
			if(mMultiListView != null){
				mMultiListView.notifyDataSetChanged();
			}
			mCurrPageIndex++;
			mLoadingData = false;
		}
	}

	private void updateNextFocusIdByCategoryId(int categoryId) {
		mMultiListView.setNextFocusLeftId(categoryId+1000);
	}
}
