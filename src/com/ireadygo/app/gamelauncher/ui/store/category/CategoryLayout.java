package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryInfo;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.store.StoreBaseContentLayout;
import com.ireadygo.app.gamelauncher.ui.store.StoreDetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.collection.CollectionDetailActivity;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;

public class CategoryLayout extends StoreBaseContentLayout {
	public static final String EXTRA_CATEGORY_ID = "CategoryID";
	public static final String EXTRA_POSTER_BG = "Poster_bg";
	private HListView mAppListView;
	private CategoryAdapter mAdapter;
	private List<CategoryInfo> mAppList = new ArrayList<CategoryInfo>();

	public CategoryLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CategoryLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CategoryLayout(Context context, int layoutTag, StoreDetailActivity storeFragment) {
		super(context, layoutTag, storeFragment);
		init();
	}

	@Override
	protected void init() {
		super.init();
//		LayoutInflater.from(getContext()).inflate(R.layout.store_category_layout, this, true);
		mAppListView = (HListView) findViewById(R.id.storeCategoryList);
		mAppListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CategoryInfo categoryItem = mAppList.get(position);
				startCategoryDetailActivity(categoryItem.getCategoryId());
			}
		});
		mAdapter = new CategoryAdapter(mAppList, mAppListView, getContext());
		mAppListView.setAdapter(mAdapter.toAnimationAdapter());
		loadData(0);
	}

	private void startCategoryDetailActivity(long categoryId) {
		Intent intent = new Intent(getContext(), CollectionDetailActivity.class);
		intent.putExtra(CategoryLayout.EXTRA_CATEGORY_ID, categoryId);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		SoundPoolManager.instance(getContext()).play(SoundPoolManager.SOUND_ENTER);
		getContext().startActivity(intent);
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
			mAppList.clear();
			mAppList.addAll(result);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected boolean isCurrentFocus() {
		return mAppListView.hasFocus();
	}

	@Override
	public boolean onWaterKey() {
//		Log.d("liu.js", "Category--onWaterKey");
		return super.onWaterKey();
	}

	@Override
	public boolean onSunKey() {
		int selectIndex = mAppListView.getSelectedItemPosition();
		if (selectIndex > -1 && selectIndex < mAdapter.getCount()) {
			CategoryInfo categoryItem = mAppList.get(selectIndex);
			startCategoryDetailActivity(categoryItem.getCategoryId());
			return true;
		}
		return super.onSunKey();
	}

}
