package com.ireadygo.app.gamelauncher.ui.store.category;

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
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryInfo;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.store.category.CategoryMultiAdapter.InternalCategoryInfo;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class CategoryFragment extends BaseContentFragment {
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mAdapter;
	private StatisticsTitleView mTitleLayout;
	private static final int MSG_UPDATE_ITEM_COUNT = 200;
	private static final long DELAY_UPDATE_ITEM = 100;
	private int mAllItemCount = 0;

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
		mAllItemCount = 0;
		mTitleLayout = (StatisticsTitleView) view.findViewById(R.id.title_layout);
		mTitleLayout.setCount(mAllItemCount);
		mMultiListView = (HMultiListView) view.findViewById(R.id.category_list);
		mAdapter = new CategoryMultiAdapter(getRootActivity(), mMultiListView);
		mMultiListView.setAdapter(mAdapter);
		bindPagingIndicator(mMultiListView);
		loadData(1);
		loadCategoryItemsCount();
	}

	@Override
	protected boolean isCurrentFocus() {
		return mMultiListView.hasFocus();
	}

	private void loadData(int page) {
		new LoadCategoryTask().execute(page);
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
			if (isCancelled() || result == null || result.isEmpty()) {
				return;
			}
		}
	}

	private class LoadItemCountTask extends AsyncTask<Void, Void, Integer> {

		private InternalCategoryInfo info;

		public LoadItemCountTask(InternalCategoryInfo categoryInfo) {
			info = categoryInfo;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				return GameInfoHub.instance(getRootActivity()).obtainChildrenCount(1, info.categoryId);
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (isCancelled() || result == null) {
				return;
			}
			info.count = result;
			postUpdateItemCount();
			mAllItemCount = mAllItemCount + result;
			mTitleLayout.setCount(mAllItemCount);
		}
	}

	private void loadCategoryItemsCount() {
		List<InternalCategoryInfo> infoList = (List<InternalCategoryInfo>)mAdapter.getData();
		for (InternalCategoryInfo info : infoList) {
			LoadItemCountTask task = new LoadItemCountTask(info);
			task.execute();
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
