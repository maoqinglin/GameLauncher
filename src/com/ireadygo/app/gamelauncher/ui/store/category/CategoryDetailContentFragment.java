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
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppMultiAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
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

	public CategoryDetailContentFragment(Activity activity, BaseMenuFragment menuFragment, int categoryId) {
		super(activity, menuFragment);
		this.mCategoryId = categoryId;
		mGameInfoHub = GameInfoHub.instance(activity);
		loadCategoryDetail();
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
		mMultiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DetailActivity.startSelf(getRootActivity(), mAppEntities.get(position));
			}
		});
		updateNextFocusIdByCategoryId((int) mCategoryId);
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
				return mGameInfoHub.obtainCategotyChildren(id, page, String.valueOf(AppPlatFormConfig.IPLATFORMID), PAGE_NUMBER);
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
		switch (categoryId) {
		case CategoryMultiAdapter.CATEGORY_ID_SLG:
			mMultiListView.setNextFocusLeftId(R.id.category_detail_menu_slg);
			break;
		case CategoryMultiAdapter.CATEGORY_ID_STG:
			mMultiListView.setNextFocusLeftId(R.id.category_detail_menu_stg);
			break;
		case CategoryMultiAdapter.CATEGORY_ID_PZL:
			mMultiListView.setNextFocusLeftId(R.id.category_detail_menu_pzl);
			break;
		case CategoryMultiAdapter.CATEGORY_ID_RPG:
			mMultiListView.setNextFocusLeftId(R.id.category_detail_menu_rpg);
			break;
		case CategoryMultiAdapter.CATEGORY_ID_SPT:
			mMultiListView.setNextFocusLeftId(R.id.category_detail_menu_spt);
			break;
		case CategoryMultiAdapter.CATEGORY_ID_OLG:
			mMultiListView.setNextFocusLeftId(R.id.category_detail_menu_olg);
			break;
		case CategoryMultiAdapter.CATEGORY_ID_SIM:
			mMultiListView.setNextFocusLeftId(R.id.category_detail_menu_sim);
			break;
		case CategoryMultiAdapter.CATEGORY_ID_RSG:
			mMultiListView.setNextFocusLeftId(R.id.category_detail_menu_rsg);
			break;
		}
	}
}
