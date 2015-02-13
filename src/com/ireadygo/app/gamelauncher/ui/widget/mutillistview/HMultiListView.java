package com.ireadygo.app.gamelauncher.ui.widget.mutillistview;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView.OnScrollListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemLongClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemSelectedListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView.SynSmoothScrollListener;

public class HMultiListView extends RelativeLayout {

	private static final int NUM_LIST_VIEW = 2;

	private HListView mUpHListView, mDownHListView;
	private int mHorizontalSpacing, mVerticalSpacing;
	private HMultiBaseAdapter mUpAdapter, mDownAdapter;
	private List<?> mData;
	private TouchScrollState mTouchScrollState = TouchScrollState.NONE;
	private OnSyncItemClickListener mSyncItemClickListener;
	private OnSyncItemLongClickListener mSyncItemLongClickListener;
	private OnSyncItemSelectedListener mSyncItemSelectedListener;

	enum TouchScrollState {
		NONE, UPTOUCH, DOWNTOUCH
	}

	public HMultiListView(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.hmutillistview_layout, this, true);
		mUpHListView = (HListView) findViewById(R.id.upHList);
		mDownHListView = (HListView) findViewById(R.id.downHList);
		setHorizontalSpacing(mHorizontalSpacing);
		setVerticalSpacing(mVerticalSpacing);
		setKeyListener();
		setOnScrollListener();
		setSyncScrollListener();
	}

	public HMultiListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HMultiListView);
		if (ta != null) {
			mHorizontalSpacing = ta.getDimensionPixelSize(R.styleable.HMultiListView_hlv_horizontal_spacing, 0);
			mVerticalSpacing = ta.getDimensionPixelSize(R.styleable.HMultiListView_hlv_vertical_spacing, 0);
			ta.recycle();
		}
		initView(context);
	}

	public HMultiListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void setHorizontalSpacing(int horizontalSpacing) {
		if (horizontalSpacing < 0) {
			throw new IllegalArgumentException("horizontalSpacing must >= 0 ");
		}
		mUpHListView.setDividerWidth(horizontalSpacing);
		mDownHListView.setDividerWidth(horizontalSpacing);
	}

	public void setVerticalSpacing(int verticalSpacing) {
		if (verticalSpacing < 0) {
			throw new IllegalArgumentException("verticalSpacing must >= 0 ");
		}
		LayoutParams params = (LayoutParams) mDownHListView.getLayoutParams();
		params.topMargin = verticalSpacing;
	}

	public HListView getUpHListView() {
		return mUpHListView;
	}

	public HListView getDownHListView() {
		return mDownHListView;
	}

	public View getSelectedView() {
		if (mUpHListView.hasFocus()) {
			return mUpHListView.getSelectedView();
		}
		if (mDownHListView.hasFocus()) {
			return mDownHListView.getSelectedView();
		}
		return null;
	}

	/**
	 * 返回双行控件选中Item的位置（数据的绝对位置序号）
	 * @return
	 */
	public int getSelectedItemPosition() {
		if (mUpHListView.hasFocus()) {
			return (mUpHListView.getSelectedItemPosition() * 2);
		}
		if (mDownHListView.hasFocus()) {
			return (mDownHListView.getSelectedItemPosition() * 2 + 1);
		}
		return -1;
	}

	private void setKeyListener() {
		mUpHListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					final int position = mUpHListView.getSelectedItemPosition();
					if (position == HListView.INVALID_POSITION) {
						return false;
					}
					final int left = mUpHListView.getSelectedView().getLeft();
					// 如果两个首个显示的子view高度不等
					mDownHListView.setSelectionFromLeft(position, left);
				}
				return false;
			}
		});

		mDownHListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					final int position = mDownHListView.getSelectedItemPosition();
					if (position == HListView.INVALID_POSITION) {
						return false;
					}
					final int left = mDownHListView.getSelectedView().getLeft();
					mUpHListView.setSelectionFromLeft(position, left);
				}
				return false;
			}
		});
	}

	private void setSyncScrollListener() {
		mUpHListView.setSynSmoothScrollListener(new SynSmoothScrollListener() {

			@Override
			public void synSmoothScrollBy(int distance, int duration, boolean linear) {
				mDownHListView.smoothScrollBy(distance, duration, linear);
			}
		});
		mDownHListView.setSynSmoothScrollListener(new SynSmoothScrollListener() {

			@Override
			public void synSmoothScrollBy(int distance, int duration, boolean linear) {
				mUpHListView.smoothScrollBy(distance, duration, linear);

			}
		});
	}

	private void setOnScrollListener() {
		// 设置listview列表的scroll监听，用于滑动过程中左右不同步时校正
		mUpHListView.setOnScrollListener(mOnScrollerListener);

		mDownHListView.setOnScrollListener(mOnScrollerListener);
	}

	OnScrollListener mOnScrollerListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsHListView view, int scrollState) {
			if (!view.isInTouchMode()) {
				return;
			}
			if (view.getId() == R.id.upHList) {
				mTouchScrollState = TouchScrollState.UPTOUCH;
			} else if (view.getId() == R.id.downHList) {
				mTouchScrollState = TouchScrollState.DOWNTOUCH;
			}
			if (mTouchScrollState == TouchScrollState.DOWNTOUCH) {
				// 如果停止滑动
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						|| scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				}
				synScrollListview((HListView) view, mUpHListView, view.getFirstVisiblePosition());
			} else if (mTouchScrollState == TouchScrollState.UPTOUCH) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						|| scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				}
				synScrollListview((HListView) view, mDownHListView, view.getFirstVisiblePosition());
			}
		}

		@Override
		public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (!view.isInTouchMode()) {
				return;
			}
			if (mTouchScrollState == TouchScrollState.DOWNTOUCH) {
				synScrollListview((HListView) view, mUpHListView, firstVisibleItem);
			} else if (mTouchScrollState == TouchScrollState.UPTOUCH) {
				synScrollListview((HListView) view, mDownHListView, firstVisibleItem);
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

	public void setAdapter(HMultiBaseAdapter hSyncAdapter) {
		if (hSyncAdapter != null) {
			Class subClass = hSyncAdapter.getClass();
			try {
				mData = hSyncAdapter.getData();
				if (mData == null || mData.isEmpty()) {
					return;
				}
				String className = subClass.getName();

				mUpAdapter = createHListViewAdapter(className, mData);
				if (mUpAdapter != null) {
					mUpHListView.setAdapter(mUpAdapter);
				}

				mDownAdapter = createHListViewAdapter(className, mData);
				if (mDownAdapter != null) {
					mDownHListView.setAdapter(mDownAdapter);
				}
				mDownHListView.setAdapter(mDownAdapter);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private HMultiBaseAdapter createHListViewAdapter(String className, List<?> data) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		HMultiBaseAdapter hAdapter = (HMultiBaseAdapter) Class.forName(className).newInstance();
		if (hAdapter != null) {
			hAdapter.setContext(getContext());
			hAdapter.setData(data);
		}
		return hAdapter;
	}

	public void notifyDataSetChanged() {
		if (mUpAdapter != null) {
			mUpAdapter.notifyDataSetChanged();
		}
		if (mDownAdapter != null) {
			mDownAdapter.notifyDataSetChanged();
		}
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		if (onItemClickListener != null) {
			mUpHListView.setOnItemClickListener(onItemClickListener);
			mDownHListView.setOnItemClickListener(onItemClickListener);
		}
	}

	public void setSyncItemClickListener(OnSyncItemClickListener onSyncItemClickListener) {
		if (onSyncItemClickListener != null) {
			mUpHListView.setOnItemClickListener(mOnItemClickListener);
			mDownHListView.setOnItemClickListener(mOnItemClickListener);
		}
	}

	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		if (onItemLongClickListener != null) {
			mUpHListView.setOnItemLongClickListener(onItemLongClickListener);
			mDownHListView.setOnItemLongClickListener(onItemLongClickListener);
		}
	}

	public void setFocusListener(OnFocusChangeListener onFocusChangeListener) {
		if (onFocusChangeListener != null) {
			mUpHListView.setOnFocusChangeListener(onFocusChangeListener);
			mDownHListView.setOnFocusChangeListener(onFocusChangeListener);
		}
	}

	public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectListener) {
		if (onItemSelectListener != null) {
			mUpHListView.setOnItemSelectedListener(onItemSelectListener);
			mDownHListView.setOnItemSelectedListener(onItemSelectListener);
		}
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mSyncItemClickListener != null) {
			if (parent.getId() == R.id.upHList) {
//					mSyncItemClickListener.onSyncItemClick(mUpHListView, view, position * 2);
				} else if (parent.getId() == R.id.downHList) {
					mSyncItemClickListener.onSyncItemClick(mUpHListView, view, (position + 1) * 2);
				}
			}
		}

	};

}

//interface OnSyncItemClickListener {
//	void onSyncItemClick(AdapterView<?> parent, View view, int position);
//}
//
//interface OnSyncItemLongClickListener {
//	void onSyncItemLongClick(AdapterView<?> parent, View view, int position);
//}
//
//interface OnSyncItemSelectedListener {
//	void onSyncItemSelected(AdapterView<?> parent, View view, int position);
//}
