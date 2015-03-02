package com.ireadygo.app.gamelauncher.ui.store.collection;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppNormalAdapter;
import com.ireadygo.app.gamelauncher.ui.store.StoreEmptyView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView.OnScrollListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.snail.appstore.openapi.AppPlatFormConfig;

public class CollectionDetailActivity extends BaseActivity implements OnClickListener {
	private HListView mListView;
	private List<AppEntity> mApps = new ArrayList<AppEntity>();
	private StoreAppNormalAdapter mAdapter;
	private OperationTipsLayout mOperationTipsLayout;
	private GameInfoHub mGameInfoHub;
	private long mPageIndex = 1;
	private boolean mLoadingData = false;
	private long mCollectionId = -1;
	private View mGobackView;
	private String mPosterBgUrl;
	private View mView;
	private Dialog mLoadingProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_collection_detail_activity);
		mGameInfoHub = GameInfoHub.instance(this);
		mGobackView = findViewById(R.id.goback);
		mGobackView.setOnClickListener(this);
		mOperationTipsLayout = (OperationTipsLayout) findViewById(R.id.tipsLayout);
		mOperationTipsLayout.setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mListView = (HListView) findViewById(R.id.storeCollectionDetailList);
		mAdapter = new StoreAppNormalAdapter(this, mListView, mApps);
		mListView.setAdapter(mAdapter.toAnimationAdapter());
		mCollectionId = getIntent().getLongExtra(CollectionFragment.EXTRA_COLLECTION_ID, -1);
		mPosterBgUrl = getIntent().getStringExtra(CollectionFragment.EXTRA_POSTER_BG);
		mView = getWindow().getDecorView();
		if (mCollectionId > 0) {
			loadCollectionDetail();
		}
		if (!TextUtils.isEmpty(mPosterBgUrl)) {
			ImageLoader.getInstance().loadImage(mPosterBgUrl, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String arg0, View arg1) {
				}

				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					mView.setBackground(new BitmapDrawable(arg2));
				}

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {

				}
			});
		}
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsHListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (!mLoadingData && firstVisibleItem >= totalItemCount - visibleItemCount - 1) {
					loadCollectionDetail();
				}
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position >= 0 && position < mApps.size()) {
					DetailActivity.startSelf(CollectionDetailActivity.this, mApps.get(position));
				}
			}
		});
	}

	private void loadCollectionDetail() {
		if (!mLoadingData && mCollectionId > 0) {
			new LoadCollectionDetailTask().execute(mCollectionId + "", mPageIndex + "");
			mLoadingData = true;
			if(mApps == null || mApps.isEmpty()){
				showLoadingProgress();
			}
		}
	}

	@Override
	public boolean onBackKey() {
		finish();
		return true;
	}

	private class LoadCollectionDetailTask extends AsyncTask<String, Void, List<AppEntity>> {
		
		@Override
		protected List<AppEntity> doInBackground(String... params) {
			if (params == null || params.length < 2) {
				return null;
			}
			String id = params[0];
			int page = Integer.parseInt(params[1]);
			try {
				return mGameInfoHub.obtainChildren(AppPlatFormConfig.DATA_TYPE_COLLECTION, id, page);
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<AppEntity> result) {
			dimissLoadingProgress();
			StoreEmptyView emptyView = new StoreEmptyView(CollectionDetailActivity.this);
			emptyView.getTitleView().setText(R.string.store_load_empty_title);
			Utils.setEmptyView(emptyView, mListView);
			if (result == null || result.isEmpty()) {
				mLoadingData = false;
				return;
			}
			mApps.addAll(result);
			mAdapter.notifyDataSetChanged();
			mPageIndex++;
			mLoadingData = false;
		}
	}

	@Override
	public void finish() {
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
		super.finish();
	}

	@Override
	public boolean onSunKey() {
		if (mListView.hasFocus()) {
			int selectIndex = mListView.getSelectedItemPosition();
			if (selectIndex >= 0) {
				mListView.performItemClick(mListView, selectIndex, 0);
			}
		}
		return true;
	}

	@Override
	public boolean onMoonKey() {
		finish();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.goback:
			onMoonKey();
			break;
		default:
			break;
		}
	}

	protected void showLoadingProgress() {
		if (mLoadingProgress == null) {
			mLoadingProgress = Utils.createLoadingDialog(this);
			mLoadingProgress.setCancelable(false);
		}
		if (!mLoadingProgress.isShowing()) {
			mLoadingProgress.show();
		}
	}

	protected void dimissLoadingProgress() {
		if (mLoadingProgress != null && mLoadingProgress.isShowing()) {
			mLoadingProgress.dismiss();
		}
	}
}
