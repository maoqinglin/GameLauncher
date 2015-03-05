package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppMultiAdapter;
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

	public CategoryDetailContentFragment(Activity activity, BaseMenuFragment menuFragment, String categoryId) {
		super(activity, menuFragment);
		this.mCategoryId = Long.parseLong(categoryId);
		mGameInfoHub = GameInfoHub.instance(activity);
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
		mMultiListView = (HMultiListView) view.findViewById(R.id.category_detail_list);
		mMultiAdapter = new StoreAppMultiAdapter(getRootActivity(), mMultiListView, mAppEntities);
		mMultiListView.setAdapter(mMultiAdapter);
		loadCategoryDetail();
	}

	@Override
	protected boolean isCurrentFocus() {
		return mMultiListView.hasFocus();
	}

	private void loadCategoryDetail() {
		if (!mLoadingData && mCategoryId > 0) {
			new LoadCategoryDetailTask().execute(mCategoryId + "", mCurrPageIndex + "");
			mLoadingData = true;
		}
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
			mAppEntities.addAll(result);
			mMultiListView.notifyDataSetChanged();
			mCurrPageIndex++;
			mLoadingData = false;
		}
	}
}
