package com.ireadygo.app.gamelauncher.ui.store;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alipay.android.mini.uielement.an;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.BannerItem;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;
import com.ireadygo.app.gamelauncher.ui.store.collection.CollectionDetailActivity;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.PagingIndicator.HListViewIndicatorInfo;
import com.ireadygo.app.gamelauncher.ui.widget.PagingIndicator.Interpolation;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class StoreFragment extends BaseContentFragment {
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mMultiBaseAdapter;
	private List<StoreInfo> mStoreDatas = new ArrayList<StoreInfo>();// 搜索、分类等数据
	private List<StoreInfo> mRecommendDatas = new ArrayList<StoreInfo>();// 位置0-7的数据，从服务器获取

	public StoreFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
		initData();
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.store_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mMultiListView = (HMultiListView) view.findViewById(R.id.store_list);
		mMultiBaseAdapter = new StoreMultiAdapter(getRootActivity(), mRecommendDatas, mStoreDatas, mMultiListView);
		mMultiListView.setAdapter(mMultiBaseAdapter);
		mMultiListView.setOnItemClickListener(mOnItemClickListener);
		bindPagingIndicator(mMultiListView, mInterpolation);
		loadRecommendData(1);
	}

	@Override
	protected boolean isCurrentFocus() {
		return mMultiListView.hasFocus();
	}

	private void initData() {
		mStoreDatas.clear();
		// 0
		Anchor anchor = new Anchor(Destination.GAME_DETAIL);
		StoreInfo info = new StoreInfo(R.drawable.store_poster_large, anchor);
		mRecommendDatas.add(info);

		anchor = new Anchor(Destination.GAME_DETAIL);
		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mRecommendDatas.add(info);

		anchor = new Anchor(Destination.GAME_DETAIL);
		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mRecommendDatas.add(info);

		anchor = new Anchor(Destination.GAME_DETAIL);
		info = new StoreInfo(R.drawable.store_poster_large, anchor);
		mRecommendDatas.add(info);

		anchor = new Anchor(Destination.GAME_DETAIL);
		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mRecommendDatas.add(info);

		anchor = new Anchor(Destination.GAME_DETAIL);
		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mRecommendDatas.add(info);

		anchor = new Anchor(Destination.GAME_DETAIL);
		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mRecommendDatas.add(info);
		// 7
		anchor = new Anchor(Destination.GAME_DETAIL);
		info = new StoreInfo(R.drawable.store_icon_category, anchor);
		mRecommendDatas.add(info);
		// 搜索
		anchor = new Anchor(Destination.STORE_SEARCH);
		info = new StoreInfo(R.drawable.store_icon_search, R.string.store_menu_search, anchor);
		mStoreDatas.add(info);

		anchor = new Anchor(Destination.STORE_CATEGORY);
		info = new StoreInfo(R.drawable.store_icon_category, R.string.store_menu_category, anchor);
		mStoreDatas.add(info);

		anchor = new Anchor(Destination.STORE_COLLECTION);
		info = new StoreInfo(R.drawable.store_icon_collection, R.string.store_menu_collection, anchor);
		mStoreDatas.add(info);

		anchor = new Anchor(Destination.STORE_FAVORITE_APPS);
		info = new StoreInfo(R.drawable.store_icon_favorite_apps, R.string.store_menu_app, anchor);
		mStoreDatas.add(info);

		anchor = new Anchor(Destination.STORE_GAME_MANAGE);
		info = new StoreInfo(R.drawable.store_icon_manager, R.string.store_menu_manager, anchor);
		mStoreDatas.add(info);
	}

	// @Override
	// public boolean onSunKey() {
	// View selectedView = mAdapter.getSelectedView();
	// int pos = mAdapter.getSelectedPos();
	// if (selectedView != null && pos != -1) {
	// mListView.performItemClick(selectedView, pos, 0);
	// return true;
	// }
	// return super.onSunKey();
	// }

	@Override
	public boolean onMoonKey() {
		return onBackKey();
	}

	@Override
	public boolean onBackKey() {
		return getMenu().getCurrentItem().requestFocus();
	}

	private void loadRecommendData(int page) {
		new LoadDataTask().execute(page);
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			StoreInfo info;
			if (position < mRecommendDatas.size()) {
				info = mRecommendDatas.get(position);
			} else {
				info = mStoreDatas.get(position - mRecommendDatas.size());
			}
			Anchor anchor = info.getAnchor();
			if (anchor != null) {
				Intent intent = anchor.getIntent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				getRootActivity().startActivity(intent);
				SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);

			}
		}
	};

	private class LoadDataTask extends AsyncTask<Integer, Void, List<StoreInfo>> {

		@Override
		protected List<StoreInfo> doInBackground(Integer... params) {
			if (params == null || params.length == 0) {
				return null;
			}
			int page = params[0];
			if (page < 1) {
				return null;
			}
			try {
				List<BannerItem> bannerItems = getGameInfoHub().obtainBannerList(page);
				if (bannerItems == null) {
					return null;
				}
				List<StoreInfo> storeInfoList = new ArrayList<StoreInfo>();
				for (BannerItem item : bannerItems) {
					StoreInfo info = new StoreInfo();
					Drawable drawable = new BitmapDrawable(getResources(), getGameInfoHub().getImageLoader()
							.loadImageSync(item.getCPicUrl()));
					info.setDrawable(drawable);
					int type = Integer.parseInt(item.getCType());
					long paramsId = item.getNParamId();
					Log.d("liu.js", "type=" + type + "|paramsId=" + paramsId + "|appId=" + item.getNAppId());
					Anchor anchor = null;
					switch (type) {
					case BannerItem.TYPE_GAME:
						anchor = new Anchor(Destination.GAME_DETAIL);
						anchor.getIntent().putExtra(DetailActivity.EXTRAS_APP_ID, paramsId);
						break;
					case BannerItem.TYPE_COLLECTION:
						anchor = new Anchor(Destination.COLLECTION_DETAIL);
						anchor.getIntent().putExtra(CollectionDetailActivity.EXTRA_CATEGORY_ID, paramsId);
						break;
					case BannerItem.TYPE_WEBPAGE:
						anchor = new Anchor(Destination.WEBPAGE);
						if (!TextUtils.isEmpty(item.getCHtmlUrl())) {
							Uri uri = Uri.parse(item.getCHtmlUrl());
							anchor.getIntent().setData(uri);
						}
						break;
					}
					info.setAnchor(anchor);
					storeInfoList.add(info);
				}
				return storeInfoList;
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<StoreInfo> result) {
			super.onPostExecute(result);
			if (result == null || result.isEmpty()) {
				return;
			}
			for (int i = 0; i < result.size(); i++) {
				if (i >= mRecommendDatas.size()) {
					break;
				}
				StoreInfo newInfo = result.get(i);
				StoreInfo oldInfo = mRecommendDatas.get(i);
				oldInfo.copyFrom(newInfo);
			}
			if (!isCancelled() && mMultiListView != null) {
				mMultiListView.notifyDataSetChanged();
			}
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
