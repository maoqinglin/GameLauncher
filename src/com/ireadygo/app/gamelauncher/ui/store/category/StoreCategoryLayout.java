package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.store.StoreBaseContentLayout;
import com.ireadygo.app.gamelauncher.ui.store.StoreDetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.collection.CollectionDetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.collection.StoreCollectionLayout;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class StoreCategoryLayout extends StoreBaseContentLayout {
	public static final String EXTRA_CATEGORY_ID = "CategoryID";
	private HListView mAppListView;
	private StoreCategoryAdapter mAdapter;
	private List<AppEntity> mAppList = new ArrayList<AppEntity>();

	public StoreCategoryLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StoreCategoryLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StoreCategoryLayout(Context context, int layoutTag, StoreDetailActivity storeFragment) {
		super(context, layoutTag, storeFragment);
		init();
	}

	@Override
	protected void init() {
		super.init();
		LayoutInflater.from(getContext()).inflate(R.layout.store_category_layout, this, true);
		mAppListView = (HListView) findViewById(R.id.storeCategoryList);
		mAppListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startCategoryDetailActivity(mAdapter.getCategoryId(position));
			}
		});
		initData();
		mAdapter = new StoreCategoryAdapter(mAppList, mAppListView, getContext());
		mAppListView.setAdapter(mAdapter.toAnimationAdapter());
		loadData(0);
	}

	private void startCategoryDetailActivity(long categoryId) {
		Intent intent = new Intent(getContext(), CollectionDetailActivity.class);
		intent.putExtra(StoreCollectionLayout.EXTRA_COLLECTION_ID, categoryId);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		SoundPoolManager.instance(getContext()).play(SoundPoolManager.SOUND_ENTER);
		getContext().startActivity(intent);
	}

	private void initData() {
		mAppList.addAll(Utils.createAppList(10));
	}

	private void loadData(int pageIndex) {
		// new Thread(){
		// public void run() {
		// try {
		// List<CategoryItem> categoryList = getGameInfoHub().obtainCategorys();
		// for(CategoryItem category:categoryList){
		// Log.d("liu.js", "Category--id=" + category.getId() + "|name=" +
		// category.getName());
		// }
		// } catch (InfoSourceException e) {
		// e.printStackTrace();
		// }
		// };
		// }.start();
	}

	@Override
	protected boolean isCurrentFocus() {
		return mAppListView.hasFocus();
	}

	@Override
	public boolean onWaterKey() {
		Log.d("liu.js", "Category--onWaterKey");
		return super.onWaterKey();
	}

	@Override
	public boolean onSunKey() {
		int selectIndex = mAppListView.getSelectedItemPosition();
		if (selectIndex > -1 && selectIndex < mAdapter.getCount()) {
			startCategoryDetailActivity(mAdapter.getCategoryId(selectIndex));
			return true;
		}
		return super.onSunKey();
	}

}
