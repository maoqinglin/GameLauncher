package com.ireadygo.app.gamelauncher.ui.widget.mutillistview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView.OnScrollListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemLongClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemSelectedListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView.SynSmoothScrollListener;

public class HMultiListViewTest extends RelativeLayout {

	private static final int NUM_LIST_VIEW = 2;

	private List<HListView> mHListViews = new ArrayList<HListView>();
	private List<BaseAdapter> mBaseAdapters = new ArrayList<BaseAdapter>();
	private int mHorizontalSpacing, mVerticalSpacing;
	private List<List<?>> mDataLists;
	private TouchScrollState mTouchScrollState = TouchScrollState.NONE;
	private OnSyncItemClickListener mSyncItemClickListener;
	private OnSyncItemLongClickListener mSyncItemLongClickListener;
	private OnSyncItemSelectedListener mSyncItemSelectedListener;
	private int mMainScrollListViewIndex = -1;

	enum TouchScrollState {
		NONE, UPTOUCH, DOWNTOUCH
	}

	public HMultiListViewTest(Context context,int num) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.hmutillistview_layout, this, true);
		HListView upHListView = (HListView) findViewById(R.id.upHList);
		HListView downHListView = (HListView) findViewById(R.id.downHList);
		mHListViews.add(upHListView);
		mHListViews.add(downHListView);
		setHorizontalSpacing(mHorizontalSpacing);
		setVerticalSpacing(mVerticalSpacing);
		setKeyListener();
		setOnScrollListener();
		setSyncScrollListener();
	}

	public HMultiListViewTest(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HMultiListView);
		if (ta != null) {
			mHorizontalSpacing = ta.getDimensionPixelSize(R.styleable.HMultiListView_hlv_horizontal_spacing, 0);
			mVerticalSpacing = ta.getDimensionPixelSize(R.styleable.HMultiListView_hlv_vertical_spacing, 0);
			ta.recycle();
		}
		initView(context);
	}

	public HMultiListViewTest(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setHorizontalSpacing(int horizontalSpacing) {
		if (horizontalSpacing < 0) {
			throw new IllegalArgumentException("horizontalSpacing must >= 0 ");
		}
		for (HListView hListView : mHListViews) {
			hListView.setDividerWidth(horizontalSpacing);
		}
	}

	public void setVerticalSpacing(int verticalSpacing) {
		if (verticalSpacing < 0) {
			throw new IllegalArgumentException("verticalSpacing must >= 0 ");
		}
		for (int i = 1; i < mHListViews.size(); i++) {
			LayoutParams params = (LayoutParams) (mHListViews.get(i).getLayoutParams());
			params.topMargin = verticalSpacing;
		}
	}


	public View getSelectedView() {
		for (HListView hListView : mHListViews) {
			if (hListView.hasFocus()) {
				return hListView.getSelectedView();
			}
		}
		return null;
	}

	/**
	 * 返回双行控件选中Item的位置（数据的绝对位置序号）
	 * @return
	 */
	public int getSelectedItemPosition() {
		for (int i = 0; i < mHListViews.size(); i++) {
			HListView hListView = mHListViews.get(i);
			if (hListView.hasFocus()) {
				return hListView.getSelectedItemPosition() * mHListViews.size() + i;
			}
		}
		return -1;
	}

	public List<HListView> getHListViews() {
		return mHListViews;
	}

	public boolean isCurrentFocus() {
		for (HListView hListView : mHListViews) {
			if (hListView.hasFocus()) {
				return true;
			}
		}
		return false;
	}

	private void setKeyListener() {
		for (int i = 0; i < mHListViews.size(); i++) {
			HListView curHListView = mHListViews.get(i);
			HListView nextHListView = null;
			HListView preHListView = null;
			if (i + 1 < mHListViews.size()) {
				nextHListView = mHListViews.get(i+1);
			}
			if (i - 1 > -1) {
				preHListView = mHListViews.get(i - 1);
			}
			setKeyListener(curHListView, preHListView, nextHListView);
		}
	}

	private void setKeyListener(final HListView curView,final HListView preView,final HListView nextView) {
		curView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				final int position = curView.getSelectedItemPosition();
				if (event.getAction() != KeyEvent.ACTION_DOWN) {
					return false;
				}
				if (position == HListView.INVALID_POSITION) {
					return false;
				}
				final int left = curView.getSelectedView().getLeft();
				if (keyCode == SnailKeyCode.DOWN_KEY) {
					if (nextView != null) {
						nextView.setSelectionFromLeft(position, left);
					}
				} else if (keyCode == SnailKeyCode.UP_KEY) {
					if (preView != null) {
						preView.setSelectionFromLeft(position, left);
					}
				}
				return false;
			}
		});
	}

	private void setSyncScrollListener() {
		for (final HListView hListView : mHListViews) {
			hListView.setSynSmoothScrollListener(new SynSmoothScrollListener() {

				@Override
				public void synSmoothScrollBy(int distance, int duration, boolean linear) {
					hListView.smoothScrollBy(distance, duration, linear);
				}
			});
		}
	}

	private void setOnScrollListener() {
		// 设置listview列表的scroll监听，用于滑动过程中左右不同步时校正
		for (HListView hListView : mHListViews) {
			hListView.setOnScrollListener(mOnScrollerListener);
		}
	}

	OnScrollListener mOnScrollerListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsHListView view, int scrollState) {
//			if (!view.isInTouchMode()) {
//				return;
//			}
			if (view.getId() == R.id.upHList) {
				mMainScrollListViewIndex = 0;
			} else if (view.getId() == R.id.downHList) {
				mMainScrollListViewIndex = 1;
			}
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					|| scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				if (mMainScrollListViewIndex == -1 && mMainScrollListViewIndex >= mHListViews.size()) {
					return;
				}
				for (int i = 0; i < mHListViews.size(); i++) {
					if (i != mMainScrollListViewIndex) {
						synScrollListview((HListView) view, mHListViews.get(i), view.getFirstVisiblePosition());
					}
				}
			}
		}

		@Override
		public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//			if (!view.isInTouchMode()) {
//				return;
//			}
			if (mMainScrollListViewIndex != -1 && mMainScrollListViewIndex < mHListViews.size()) {
				for (int i = 0; i < mHListViews.size(); i++) {
					if (i != mMainScrollListViewIndex) {
						synScrollListview((HListView) view, mHListViews.get(i), firstVisibleItem);
					}
				}
			}
		}

	};

	private void synScrollListview(HListView touchListView, HListView synListView, final int position) {
		View firstView = touchListView.getChildAt(0);
		if (firstView == null) {
			return;
		}
		int left = firstView.getLeft();
		View synView = synListView.getChildAt(0);
		if (synView != null) {
			int synLeft = synView.getLeft();
			if (left != synLeft) {
				synListView.setSelectionFromLeft(position, left);
			}
		}
	}

	public void setAdapter(HMultiListBaseAdapter hMultiListBaseAdapter) {
		if (hMultiListBaseAdapter != null) {
			mBaseAdapters = hMultiListBaseAdapter.getAdapters();
			if (mBaseAdapters.size() > 0 && mBaseAdapters.size() == mHListViews.size()) {
				mDataLists = hMultiListBaseAdapter.getDataList();
				for (int i = 0; i < mBaseAdapters.size(); i++) {
					BaseAdapter baseAdapter = mBaseAdapters.get(i);
					mHListViews.get(i).setAdapter(baseAdapter);
				}
			}
		}

	}

//	private HMultiBaseAdapter createHListViewAdapter(String className, List<?> data) throws InstantiationException,
//			IllegalAccessException, ClassNotFoundException {
//		HMultiBaseAdapter hAdapter = (HMultiBaseAdapter) Class.forName(className).newInstance();
//		if (hAdapter != null) {
//			hAdapter.setContext(getContext());
//			hAdapter.setData(data);
//		}
//		return hAdapter;
//	}

	public void notifyDataSetChanged() {
		for (BaseAdapter baseAdapter : mBaseAdapters) {
			baseAdapter.notifyDataSetChanged();
		}
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//		if (onItemClickListener != null) {
//			for (HListView hListView : mHListViews) {
//				hListView.setOnItemClickListener(onItemClickListener);
//			}
//		}
	}

	public void setSyncItemClickListener(OnSyncItemClickListener onSyncItemClickListener) {
		if (onSyncItemClickListener != null) {
			for (HListView hListView : mHListViews) {
//				hListView.setsy(onSyncItemClickListener);
			}
		}
	}

	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		if (onItemLongClickListener != null) {
			for (HListView hListView : mHListViews) {
				hListView.setOnItemLongClickListener(onItemLongClickListener);
			}
		}
	}

	public void setFocusListener(OnFocusChangeListener onFocusChangeListener) {
		if (onFocusChangeListener != null) {
			for (HListView hListView : mHListViews) {
				hListView.setOnFocusChangeListener(onFocusChangeListener);
			}
		}
	}

	public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectListener) {
		if (onItemSelectListener != null) {
			for (HListView hListView : mHListViews) {
				hListView.setOnItemSelectedListener(onItemSelectListener);
			}
		}
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (parent.getId() == R.id.upHList) {
//					mSyncItemClickListener.onSyncItemClick(mUpHListView, view, position * 2);
				mHListViews.get(0).performItemClick(view, position, id);
			} else if (parent.getId() == R.id.downHList) {
//					mSyncItemClickListener.onSyncItemClick(mUpHListView, view, (position + 1) * 2);
				mHListViews.get(1).performItemClick(view, position, id);
			}
		}
	};

}

interface OnSyncItemClickListener {
	void onSyncItemClick(AdapterView<?> parent, View view, int position);
}

interface OnSyncItemLongClickListener {
	void onSyncItemLongClick(AdapterView<?> parent, View view, int position);
}

interface OnSyncItemSelectedListener {
	void onSyncItemSelected(AdapterView<?> parent, View view, int position);
}
