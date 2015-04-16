package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryInfo;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.snail.appstore.openapi.AppPlatFormConfig;

public class CategoryFragment extends BaseContentFragment {
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mAdapter;
	private StatisticsTitleView mTitleLayout;
	private static final int MSG_UPDATE_ITEM_COUNT = 200;
	private static final long DELAY_UPDATE_ITEM = 100;
	private int mAllItemCount = 0;
	private List<CategoryInfo> mCategoryDatas = new ArrayList<CategoryInfo>();
	private static final String TYPE_TAG = "2";
	
	public CategoryFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.category_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(View.VISIBLE, TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mAllItemCount = 0;
		mTitleLayout = (StatisticsTitleView) view.findViewById(R.id.title_layout);
		setItemCount();
		mMultiListView = (HMultiListView) view.findViewById(R.id.category_list);
		mMultiListView.setIsDelayScroll(false);
		mAdapter = new CategoryMultiAdapter(getRootActivity(), mMultiListView,mCategoryDatas);
		mMultiListView.setAdapter(mAdapter);
		mMultiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position >= 0 && position < mCategoryDatas.size()) {
					CategoryDetailActivity.startSelf(getRootActivity(),position,mCategoryDatas);
				}
			}

		});
		bindPagingIndicator(mMultiListView);
		if (mCategoryDatas.size() == 0) {
			loadData(1);
		}
		setEmptyView(mMultiListView, R.string.store_empty_title, View.GONE, 0);
	}

	private void setItemCount() {
		for(CategoryInfo categoryInfo : mCategoryDatas){
			mAllItemCount = mAllItemCount + categoryInfo.getAppCounts();
		}
		mTitleLayout.setCount(mAllItemCount);

	}

	@Override
	protected boolean isCurrentFocus() {
		return mMultiListView.hasFocus();
	}

	private void loadData(int page) {
		new LoadCategoryTask().execute(page);
		showLoadingProgress();
	}

	private class LoadCategoryTask extends AsyncTask<Integer, Void, List<CategoryInfo>> {

		@Override
		protected List<CategoryInfo> doInBackground(Integer... params) {
			if (params == null || params.length == 0) {
				return null;
			}
			int page = params[0];
			if (page < 0) {
				return null;
			}
			try {
				return getGameInfoHub().obtainCategorys();
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<CategoryInfo> result) {
			dismissLoadingProgress();
			if (isCancelled() || result == null || result.isEmpty()) {
				return;
			}
			mCategoryDatas.clear();
			for(CategoryInfo categoryInfo : result){
				if (TYPE_TAG.equals(categoryInfo.getCategoryType())
						&& AppPlatFormConfig.IPLATFORMID == categoryInfo.getPlatformId()
						&& categoryInfo.getAppCounts() > 0) {
					mCategoryDatas.add(categoryInfo);
					postUpdateItemCount();
					mAllItemCount = mAllItemCount + categoryInfo.getAppCounts();
				}
			}
			mTitleLayout.setCount(mAllItemCount);
			mMultiListView.notifyDataSetChanged();
		}
	}

	private void postUpdateItemCount() {
		if (mHandler.hasMessages(MSG_UPDATE_ITEM_COUNT)) {
			mHandler.removeMessages(MSG_UPDATE_ITEM_COUNT);
		}
		Message msg = mHandler.obtainMessage(MSG_UPDATE_ITEM_COUNT);
		mHandler.sendMessageDelayed(msg, DELAY_UPDATE_ITEM);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_ITEM_COUNT:
				mMultiListView.notifyDataSetChanged();
				break;

			default:
				break;
			}
		};
	};
}
