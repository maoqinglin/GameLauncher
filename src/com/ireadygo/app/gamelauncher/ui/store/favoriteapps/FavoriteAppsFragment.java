package com.ireadygo.app.gamelauncher.ui.store.favoriteapps;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppMultiAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class FavoriteAppsFragment extends BaseContentFragment {
	private List<AppEntity> mAppEntities = new ArrayList<AppEntity>();
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mAdapter;
	private StatisticsTitleView mTitleLayout;

	public FavoriteAppsFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
		new LoadDataTask().execute();
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.favorite_apps_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mTitleLayout = (StatisticsTitleView) view.findViewById(R.id.title_layout);
		mTitleLayout.setTitle(R.string.favorite_apps_title_prompt);
		mTitleLayout.setCount(mAppEntities.size());

		mMultiListView = (HMultiListView) view.findViewById(R.id.favorite_apps_list);
		mAdapter = new StoreAppMultiAdapter(getRootActivity(), mMultiListView, mAppEntities);
		mMultiListView.setAdapter(mAdapter);
		setEmptyView(mMultiListView, R.string.store_empty_title, View.GONE, 0);
		mMultiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DetailActivity.startSelf(getRootActivity(), mAppEntities.get(position));
			}

		});
	}

	@Override
	protected boolean isCurrentFocus() {
		return mMultiListView.hasFocus();
	}

	private class LoadDataTask extends AsyncTask<Void, Void, List<AppEntity>> {

		@Override
		protected List<AppEntity> doInBackground(Void... params) {
			try {
				List<AppEntity> appEntities = getGameInfoHub().getCommonApp();
				return appEntities;
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<AppEntity> result) {
			super.onPostExecute(result);
			if (result == null || result.isEmpty()) {
				return;
			}
			mAppEntities.addAll(result);
			if (!isCancelled() && mMultiListView != null) {
				mMultiListView.notifyDataSetChanged();
			}
			if(mTitleLayout != null){
				mTitleLayout.setCount(mAppEntities.size());
			}
		}
	}
}
