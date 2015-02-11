package com.ireadygo.app.gamelauncher.game.ui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.game.adapter.AppListAdapter;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.base.KeyEventLayout;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppNormalAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;

public class GameRecommendAppLayout extends KeyEventLayout implements View.OnFocusChangeListener {

	private static final int MSG_UPDATE_DATA = 100;
	private Context mContext;
	private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);

	private HListView mRecommendAppHListView;
	private PopupWindow mPopWindow;

	private StoreAppNormalAdapter mAdapter;
	private boolean mDestroyed = false;
	private Activity mActivity;
	private RecommendAppDisplayListener mRecommendAppShowStateListener;
	private boolean mIsPopupWindowShow = true;
	private long mPageIndex = 1;
	private boolean mLoadingData = false;
	private long mCollectionId = 10751;
	private List<AppEntity> mApps = new ArrayList<AppEntity>();

	public GameRecommendAppLayout(Context context) {
		super(context);
		mContext = context;
		initUi();
		initListener();
	}

	public GameRecommendAppLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

	}

	public GameRecommendAppLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_DATA:
				break;
			default:
				break;
			}
		};
	};

	private void initUi() {
		LayoutInflater.from(mContext).inflate(R.layout.mygame_recommend_app_layout, this, true);
		mRecommendAppHListView = (HListView) findViewById(R.id.hlist_mygame_recommend_app);
		mRecommendAppHListView.setOnKeyListener(mRecommendAppOnKeyListener);
		mAdapter = new StoreAppNormalAdapter(mContext,mRecommendAppHListView,mApps);
		mRecommendAppHListView.setAdapter(mAdapter.toAnimationAdapter());
		loadCollectionDetail();
	}

	private void initListener() {

		mRecommendAppHListView.setOnItemClickListener(mOnItemClickListener);

	}

	private void postUpdateData() {
		Message msg = mHandler.obtainMessage(MSG_UPDATE_DATA);
		mHandler.sendMessageDelayed(msg, 0);
	}

	private OnKeyListener mRecommendAppOnKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View focusView, int keyCode, KeyEvent event) {

			if (KeyEvent.ACTION_DOWN != event.getAction()) {
				return false;
			}
			switch (keyCode) {
			case SnailKeyCode.SUN_KEY:
				return onSunKey();
			case SnailKeyCode.MOON_KEY:
			case SnailKeyCode.BACK_KEY:
				return onMoonKey();
			case SnailKeyCode.WATER_KEY:
				return onWaterKey();
			}
			return false;
		}

	};

	private void loadCollectionDetail() {
		if (!mLoadingData && mCollectionId > 0) {
			new LoadCollectionDetailTask().execute(mCollectionId + "", mPageIndex + "");
			mLoadingData = true;
		}
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
				return GameInfoHub.instance(mContext).getPreLoadList();
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
			mApps.addAll(result);
			mAdapter.notifyDataSetChanged();
			mPageIndex++;
			mLoadingData = false;
		}
	}

	public static RelativeLayout fromXml(Context context) {
		return (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.mygame_recommend_app_layout, null);
	}

	public boolean isRecommendAppShow() {
		return mIsPopupWindowShow;
	}

	public void openRecommendApp(final Activity activity, RecommendAppDisplayListener listener) {
		mActivity = activity;
		mRecommendAppShowStateListener = listener;
		mRecommendAppShowStateListener.recommendAppOpened();
		initPopWindow();
		mIsPopupWindowShow = true;
	}

	private void initPopWindow() {
		int width = getResources().getDimensionPixelOffset(R.dimen.popwindow_width);
		int height = getResources().getDimensionPixelOffset(R.dimen.popwindow_height);
		int popX = getResources().getDimensionPixelOffset(R.dimen.popwindow_x);
		int popY = getResources().getDimensionPixelOffset(R.dimen.popwindow_y);
		mPopWindow = new PopupWindow(this, width, height,true);//解决popwindow无法响应back键问题
		mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopWindow.setFocusable(true);
		mPopWindow.update();
		mPopWindow.showAtLocation(findViewById(R.id.hlist_mygame_recommend_app), Gravity.NO_GRAVITY, popX, popY);
		mPopWindow.setOnDismissListener(new PopWindowCloseListener());
	}

	class PopWindowCloseListener implements OnDismissListener {

		@Override
		public void onDismiss() {
			if (mRecommendAppShowStateListener != null) {
				mRecommendAppShowStateListener.recommendAppClosed();
			}
		}
	}

	public void closeRecommendAppLayout() {
		closePopWindow();
	}

	private void closePopWindow() {
		if (mActivity != null) {
			if (null != mPopWindow && mPopWindow.isShowing() && mActivity != null && !mActivity.isFinishing()) {
				mPopWindow.dismiss();
				mIsPopupWindowShow = false;
			}
		}
	}

	boolean isDestroyed() {
		return mDestroyed;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		if (!isPointInFolderFrame((int) touchX, (int) touchY)) {
			if (mRecommendAppShowStateListener != null) {
				closeRecommendAppLayout();
				mRecommendAppShowStateListener.recommendAppClosed();
			}
		}
		return super.onTouchEvent(event);
	}

	private boolean isPointInFolderFrame(int x, int y) {
		if (x < getLeft() || x > getLeft() + getWidth() || y < getTop() || y > getTop() + getHeight()) {
			return false;
		}
		return true;
	}

	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			return;
		}
	}

	public void notifyDataSet() {
		if (null != mRecommendAppHListView) {
			AppListAdapter adapter = (AppListAdapter) mRecommendAppHListView.getAdapter();
			if (null != adapter) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		closePopWindow();
	}

	public interface RecommendAppDisplayListener {
		public void recommendAppOpened();

		public void recommendAppClosed();
	}

	@Override
	protected boolean isCurrentFocus() {
		return mRecommendAppHListView.hasFocus();
	}

	public boolean hasFocus() {
		return isCurrentFocus();
	}

	@Override
	public boolean onSunKey() {
		View selectedView = mRecommendAppHListView.getSelectedView();
		if (selectedView != null) {
			int position = mRecommendAppHListView.getSelectedItemPosition();
			if (position > 0) {
				mOnItemClickListener.onItemClick(mRecommendAppHListView, selectedView, position, 0);
			}
			return true;
		}
		return super.onSunKey();
	}

	@Override
	public boolean onMoonKey() {
		closeRecommendAppLayout();
		return true;
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}

	@Override
	public boolean onWaterKey() {
		return super.onWaterKey();
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AppEntity entity = (AppEntity)mAdapter.getItem(position);
			Intent intent = new Intent(mContext, DetailActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundle = new Bundle();
			bundle.putParcelable(DetailActivity.EXTRAS_APP_ENTITY, entity);
			intent.putExtras(bundle);
			SoundPoolManager.instance(mContext).play(SoundPoolManager.SOUND_ENTER);
			mContext.startActivity(intent);
		}
	};
}
