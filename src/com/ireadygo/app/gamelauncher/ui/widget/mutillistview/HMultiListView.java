package com.ireadygo.app.gamelauncher.ui.widget.mutillistview;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.item.BaseAdapterItem;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView.OnScrollListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemLongClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemSelectedListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView.SynSmoothScrollListener;

public class HMultiListView extends LinearLayout {

	private static final int NUM_LIST_VIEW = 2;
	private static final int WHAT_SELECTED_ANIMATOR = 1;
	private static final int WHAT_UNSELECTED_ANIMATOR = 2;
	private static final int WHAT_SHOW_EMPTYVIEW = 3;
	private static final int DELAY_SHOW_EMPTYVIEW = 3000;

	private HMultiBaseAdapter mHMultiBaseAdapter;
	private List<HListView> mHListViews = new ArrayList<HListView>();
	private List<BaseAdapter> mBaseAdapters = new ArrayList<BaseAdapter>();
	private int mHorizontalSpacing, mVerticalSpacing;
	private List<List<?>> mDataLists = new ArrayList<List<?>>();
	private HListView mActiveListView;
	private int mMaxCount;
	private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;

	private int mPaddingLeft;
	private int mPaddingTop;
	private int mPaddingRight;
	private int mPaddingBottom;

	// private BaseAdapterItem mSelectedItem;
	private OnItemSelectedListener mOnItemSelectedListener;
	private OnFocusChangeListener mOnFocusChangeListener;
	private OnItemClickListener mOnItemClickListener;
	private OnScrollListener mOnScrollListener;

	private View mEmptyView;
	private boolean mIsDelayScroll = true;

	enum TouchScrollState {
		NONE, UPTOUCH, DOWNTOUCH
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_SELECTED_ANIMATOR:
				BaseAdapterItem item = (BaseAdapterItem) msg.obj;
				if (item != null) {
					item.toSelected(null);
				}
				break;
			case WHAT_UNSELECTED_ANIMATOR:
				BaseAdapterItem itemUn = (BaseAdapterItem) msg.obj;
				if (itemUn != null) {
					itemUn.toUnselected(null);
				}
				break;
			case WHAT_SHOW_EMPTYVIEW:
				showEmptyView();
				break;
			default:
				break;
			}
		};
	};

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.hmutillistview_layout, this, true);
		HListView upHListView = (HListView) findViewById(R.id.upHList);
		upHListView.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mVerticalSpacing / 2);
		upHListView.setOnKeyListener(mUpListKeyListener);

		HListView downHListView = (HListView) findViewById(R.id.downHList);
		downHListView.setPadding(mPaddingLeft, mVerticalSpacing / 2, mPaddingRight, mPaddingBottom);
		downHListView.setOnKeyListener(mDownKeyListener);

		initFocusIds(upHListView, downHListView);

		mHListViews.add(upHListView);
		mHListViews.add(downHListView);
		setHorizontalSpacing(mHorizontalSpacing);
		// setVerticalSpacing(mVerticalSpacing);
		setOnInternalScrollListener(mOnInternalScrollerListener);
		setSyncScrollListener();
		setInternalFocusListener(mInternalFocusChangeL);
		setInternalItemSelectedListener(mInternalItemSelectedListener);
		setInternalItemClickListener(mInternalItemClickListener);
	}

	public HMultiListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HMultiListView);
		if (ta != null) {
			mHorizontalSpacing = ta.getDimensionPixelSize(R.styleable.HMultiListView_hlv_horizontal_spacing, 0);
			mVerticalSpacing = ta.getDimensionPixelSize(R.styleable.HMultiListView_hlv_vertical_spacing, 0);
			mPaddingLeft = ta.getDimensionPixelSize(R.styleable.HMultiListView_padding_left, 0);
			mPaddingTop = ta.getDimensionPixelSize(R.styleable.HMultiListView_padding_top, 0);
			mPaddingRight = ta.getDimensionPixelSize(R.styleable.HMultiListView_padding_right, 0);
			mPaddingBottom = ta.getDimensionPixelSize(R.styleable.HMultiListView_padding_bottom, 0);
			ta.recycle();
		}
		initView(context);
	}

	public HMultiListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void initFocusIds(HListView upHListView, HListView downHListView) {
		int nextFocusLeftId = getNextFocusLeftId();
		int nextFocusRightId = getNextFocusRightId();
		int nextFocusUpId = getNextFocusUpId();
		int nextFocusDownId = getNextFocusDownId();

		int upListViewId = upHListView.getId();
		int downListViewId = downHListView.getId();

		if (nextFocusLeftId == View.NO_ID) {
			upHListView.setNextFocusLeftId(upListViewId);
			downHListView.setNextFocusLeftId(downListViewId);
		} else {
			upHListView.setNextFocusLeftId(nextFocusLeftId);
			downHListView.setNextFocusLeftId(nextFocusLeftId);
		}

		if (nextFocusRightId == View.NO_ID) {
			upHListView.setNextFocusRightId(upListViewId);
			downHListView.setNextFocusRightId(downListViewId);
		} else {
			upHListView.setNextFocusRightId(nextFocusRightId);
			downHListView.setNextFocusRightId(nextFocusRightId);
		}
		if (nextFocusUpId == View.NO_ID) {
			upHListView.setNextFocusUpId(upListViewId);
		} else {
			upHListView.setNextFocusUpId(nextFocusUpId);
		}

		if (nextFocusDownId == View.NO_ID) {
			downHListView.setNextFocusDownId(downListViewId);
		} else {
			downHListView.setNextFocusDownId(nextFocusDownId);
		}
		upHListView.setNextFocusDownId(downListViewId);
		downHListView.setNextFocusUpId(upListViewId);
	}

	@Override
	public void setNextFocusDownId(int nextFocusDownId) {
		super.setNextFocusDownId(nextFocusDownId);
		initFocusIds(mHListViews.get(0), mHListViews.get(1));
	}

	@Override
	public void setNextFocusLeftId(int nextFocusLeftId) {
		super.setNextFocusLeftId(nextFocusLeftId);
		initFocusIds(mHListViews.get(0), mHListViews.get(1));
	}

	@Override
	public void setNextFocusRightId(int nextFocusRightId) {
		super.setNextFocusRightId(nextFocusRightId);
		initFocusIds(mHListViews.get(0), mHListViews.get(1));
	}

	@Override
	public void setNextFocusUpId(int nextFocusUpId) {
		super.setNextFocusUpId(nextFocusUpId);
		initFocusIds(mHListViews.get(0), mHListViews.get(1));
	}

	public void setHorizontalSpacing(int horizontalSpacing) {
		if (horizontalSpacing < 0) {
			throw new IllegalArgumentException("horizontalSpacing must >= 0 ");
		}
		for (HListView hListView : mHListViews) {
			hListView.setDividerWidth(horizontalSpacing);
		}
	}

	// public void setVerticalSpacing(int verticalSpacing) {
	// if (verticalSpacing < 0) {
	// throw new IllegalArgumentException("verticalSpacing must >= 0 ");
	// }
	// for (int i = 1; i < mHListViews.size(); i++) {
	// LayoutParams params = (LayoutParams)
	// (mHListViews.get(i).getLayoutParams());
	// params.topMargin = verticalSpacing;
	// }
	// }

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
	 * 
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

	/**
	 * 返回选中的Item
	 * 
	 * @return
	 */
	public Object getSelectedItem() {
		for (HListView hListView : mHListViews) {
			if (hListView.hasFocus()) {
				return hListView.getSelectedItem();
			}
		}
		return null;
	}

	public List<HListView> getHListViews() {
		return mHListViews;
	}

	public void setAdapter(HMultiBaseAdapter hMultiListBaseAdapter) {
		mHMultiBaseAdapter = hMultiListBaseAdapter;
		if (hMultiListBaseAdapter != null) {
			int listNum = hMultiListBaseAdapter.getHListNum();
			if (listNum < 0) {
				listNum = NUM_LIST_VIEW;
			}
			if (listNum > 0 && listNum == mHListViews.size()) {
				mBaseAdapters.clear();
				// 设置数据
				initLists(listNum);
				initDataLists(listNum, hMultiListBaseAdapter.getData());
				for (int i = 0; i < listNum; i++) {
					ProxyAdapter proxyAdapter = new ProxyAdapter(mHListViews.get(i));
					proxyAdapter.setData(mDataLists.get(i), listNum, i);
					mBaseAdapters.add(proxyAdapter);
					mHListViews.get(i).setAdapter(proxyAdapter);
				}
			}
		}
	}

	/**
	 * 初始化多级水平列表
	 * 
	 * @param listNum
	 */
	private <T> void initLists(int listNum) {
		mDataLists.clear();
		for (int i = 0; i < listNum; i++) {
			List<T> dataList = new ArrayList<T>();
			mDataLists.add(dataList);
		}
	}

	/**
	 * 装载每个列表数据
	 * 
	 * @param listNum
	 * @param list
	 */
	private <T> void initDataLists(int listNum, List<T> list) {
		if (listNum != 0 && list != null && !mDataLists.isEmpty()) {
			for (List<?> listData : mDataLists) {
				listData.clear();// 清除单个列表数据
			}
			int size = list.size();
			for (int j = 0; j < list.size(); j++) {
				int dataListIndex = j % listNum;
				List<T> dataList = (List<T>) mDataLists.get(dataListIndex);
				dataList.add((T) list.get(j));
			}
			if (isEmpty(mDataLists)) {
				mHandler.removeMessages(WHAT_SHOW_EMPTYVIEW);
				mHandler.sendEmptyMessageDelayed(WHAT_SHOW_EMPTYVIEW, DELAY_SHOW_EMPTYVIEW);
			} else {
				hideEmptyView();
			}
			mMaxCount = size % listNum == 0 ? size / listNum : size / listNum + 1;
		}
	}

	private boolean isEmpty(List<List<?>> dataLists) {
		boolean isEmpty = true;
		for (int i = 0; i < dataLists.size(); i++) {
			if (!dataLists.get(i).isEmpty()) {
				isEmpty = false;
				break;
			}
		}
		return isEmpty;
	}

	public BaseAdapter getAdapter() {
		for (HListView hListView : mHListViews) {
			if (hListView.hasFocus()) {
				return (BaseAdapter) hListView.getAdapter();
			}
		}
		return null;
	}

	private View getEmptyItem(int arg0, View arg1, ViewGroup arg2) {
		if (mHMultiBaseAdapter != null) {
			return mHMultiBaseAdapter.getEmptyView(arg0, arg1, arg2);
		}
		return null;
	}

	private void showEmptyView() {
		if (mEmptyView == null) {
			return;
		}
		if (getParent() == null) {
			return;
		}
		if (mEmptyView.getParent() == null) {
			((ViewGroup) getParent()).addView(mEmptyView);
		}
		mEmptyView.setVisibility(View.VISIBLE);
	}

	private void hideEmptyView() {
		if (mEmptyView == null) {
			return;
		}
		mHandler.removeMessages(WHAT_SHOW_EMPTYVIEW);
		mEmptyView.setVisibility(View.GONE);
	}

	private View getView(int position, View convertView, ViewGroup parent) {
		if (mHMultiBaseAdapter != null) {
			View view = mHMultiBaseAdapter.getView(position, convertView, parent);
			return view;
		}
		return null;
	}

	public void notifyDataSetChanged() {
		if (mHMultiBaseAdapter != null) {
			initDataLists(mHMultiBaseAdapter.getHListNum(), mHMultiBaseAdapter.getData());
		}
		for (BaseAdapter baseAdapter : mBaseAdapters) {
			baseAdapter.notifyDataSetChanged();
		}
	}

	private int getRealPos(int oriPos, int listNum, int rowIndex) {
		return listNum * oriPos + rowIndex;
	}

	public class ProxyAdapter extends BaseAnimatorAdapter {

		private List<?> data;
		private int listNum;
		private int dataIndex;

		public ProxyAdapter(HListView listView) {
			super(listView);
		}

		public void setData(List<?> data, int num, int index) {
			this.data = data;
			this.listNum = num;
			this.dataIndex = index;
		}

		public int getDataIndex() {
			return dataIndex;
		}

		@Override
		public int getCount() {
			if (data != null && !data.isEmpty()) {
				return mMaxCount;
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (position > data.size() - 1) {
				return null;
			}
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int realPos = getRealPos(position, listNum, dataIndex);
			if (position > data.size() - 1) {
				View view = HMultiListView.this.getEmptyItem(realPos, convertView, parent);
				view.setId(position);
				return view;
			}
			View view = HMultiListView.this.getView(realPos, convertView, parent);
			view.setId(position);
			return view;
		}

		@Override
		public Animator selectedAnimator(View view) {
			if (view != null && view instanceof ISelectedAnim) {
				return ((ISelectedAnim) view).selectedAnimation();
			}
			return null;
		}

		@Override
		protected Animator unselectedAnimator(View view) {
			if (view != null && view instanceof ISelectedAnim) {
				return ((ISelectedAnim) view).unselectedAnimation();
			}
			return null;
		}
	}

	private OnKeyListener mUpListKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() != KeyEvent.ACTION_DOWN) {
				return false;
			}
			if (keyCode == SnailKeyCode.DOWN_KEY) {
				HListView upListView = mHListViews.get(0);
				HListView downListView = mHListViews.get(1);
				int selectedPosition = upListView.getSelectedItemPosition();
				setSelectionByPostion(downListView, selectedPosition);
			}
			return false;
		}
	};

	private OnKeyListener mDownKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() != KeyEvent.ACTION_DOWN) {
				return false;
			}
			if (keyCode == SnailKeyCode.UP_KEY) {
				HListView upListView = mHListViews.get(0);
				HListView downListView = mHListViews.get(1);
				int selectedPosition = downListView.getSelectedItemPosition();
				View itemView = upListView.findViewById(selectedPosition);
				if (itemView != null) {
					upListView.setSelectionFromLeft(selectedPosition, itemView.getLeft() - mPaddingLeft);
				}
			}
			return false;
		}
	};

	private void setSyncScrollListener() {
		for (final HListView hListView : mHListViews) {
			hListView.setSynSmoothScrollListener(new SynSmoothScrollListener() {

				@Override
				public void synSmoothScrollBy(final int distance, final int duration, final boolean linear) {
					for (final HListView otherListView : mHListViews) {
						if (hListView.getId() != otherListView.getId()) {
							// for(HListView listView:mHListViews){
							// if(listView != parent){
							// setSelectionByPostion(listView, position);
							// }
							// }
							if (mIsDelayScroll) {
								setSelectionByPostion(otherListView, hListView.getSelectedItemPosition());
								mHandler.post(new Runnable() {

									@Override
									public void run() {
										otherListView.smoothScrollBy(distance, duration, linear);
									}
								});
							} else {
								otherListView.smoothScrollBy(distance, duration, linear);
							}
						}
					}
				}
			});
		}

	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.mOnScrollListener = onScrollListener;
	}

	private void setOnInternalScrollListener(OnScrollListener onScrollListener) {
		// 设置listview列表的scroll监听，用于滑动过程中左右不同步时校正
		for (HListView hListView : mHListViews) {
			hListView.setOnScrollListener(onScrollListener);
		}
	}

	private boolean isScroll() {
		return mScrollState != OnScrollListener.SCROLL_STATE_IDLE;
	}

	OnScrollListener mOnInternalScrollerListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsHListView view, int scrollState) {
			if (mOnScrollListener != null) {
				mOnScrollListener.onScrollStateChanged(view, scrollState);
			}
			if (!isInTouchMode()) {
				return;
			}
			switch (scrollState) {
			case SCROLL_STATE_IDLE:

				break;
			case SCROLL_STATE_FLING:
			case SCROLL_STATE_TOUCH_SCROLL:
				if (!isScroll()) {
					mActiveListView = (HListView) view;
				}
				break;
			}
			mScrollState = scrollState;
		}

		@Override
		public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (mOnScrollListener != null) {
				mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
			if (!isScroll()) {// 系统导致的滚动
				return;
			}
			if (view != mActiveListView) {
				return;
			}
			if (!isInTouchMode()) {
				return;
			}
			int activeIndex = mHListViews.indexOf(view);
			for (int i = 0; i < mHListViews.size(); i++) {
				if (activeIndex != i) {
					synScrollListview((HListView) view, mHListViews.get(i), firstVisibleItem);
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
				synListView.setSelectionFromLeft(position, left - mPaddingLeft);
			}
		}
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	private void setInternalItemClickListener(OnItemClickListener onItemClickListener) {
		if (onItemClickListener != null) {
			for (HListView hListView : mHListViews) {
				hListView.setOnItemClickListener(onItemClickListener);
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

	public void setOnFocusChangeListener(OnFocusChangeListener listener) {
		this.mOnFocusChangeListener = listener;
	}

	private void setInternalFocusListener(OnFocusChangeListener onFocusChangeListener) {
		for (HListView hListView : mHListViews) {
			hListView.setOnFocusChangeListener(onFocusChangeListener);
		}
	}

	public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectListener) {
		this.mOnItemSelectedListener = onItemSelectListener;
	}

	private void setInternalItemSelectedListener(OnItemSelectedListener onItemSelectListener) {
		for (HListView hListView : mHListViews) {
			hListView.setOnItemSelectedListener(onItemSelectListener);
		}
	}

	@Override
	public boolean hasFocus() {
		for (HListView hListView : mHListViews) {
			if (hListView.hasFocus()) {
				return true;
			}
		}
		return false;
	}

	public void setEmptyView(View emptyView) {
		if (mEmptyView != null && mEmptyView.getParent() != null) {
			((ViewGroup) mEmptyView.getParent()).removeView(mEmptyView);
		}
		mEmptyView = emptyView;
		if (emptyView != null && getParent() != null) {
			emptyView.setVisibility(View.GONE);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);
			((ViewGroup) getParent()).addView(emptyView, params);
			if (isEmpty(mDataLists)) {
				mHandler.removeMessages(WHAT_SHOW_EMPTYVIEW);
				mHandler.sendEmptyMessageDelayed(WHAT_SHOW_EMPTYVIEW, DELAY_SHOW_EMPTYVIEW);
			} else {
				hideEmptyView();
			}
		}
	}

	private OnItemSelectedListener mInternalItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (mOnItemSelectedListener != null) {
				ProxyAdapter adapter = (ProxyAdapter) parent.getAdapter();
				if (adapter != null) {
					int realPos = getRealPos(position, mHMultiBaseAdapter.getHListNum(), adapter.getDataIndex());
					mOnItemSelectedListener.onItemSelected(parent, view, realPos, id);
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			if (mOnItemSelectedListener != null) {
				mOnItemSelectedListener.onNothingSelected(parent);
			}
		}

	};

	private OnFocusChangeListener mInternalFocusChangeL = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			if (!hasFocus) {
				if (!hasFocus()) {
					HListView focusList = ((HListView) view);
					int selectedPosition = focusList.getSelectedItemPosition();
					for (HListView listView : mHListViews) {
						setSelectionByPostion(listView, selectedPosition);
					}
				}
			}
			if (mOnFocusChangeListener != null) {
				mOnFocusChangeListener.onFocusChange(view, hasFocus);
			}
		}
	};

	private OnItemClickListener mInternalItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ProxyAdapter adapter = (ProxyAdapter) parent.getAdapter();
			if (adapter != null) {
				int realPosition = getRealPos(position, mHMultiBaseAdapter.getHListNum(), adapter.getDataIndex());
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(parent, view, realPosition, id);
				}
			}
		}

	};

	public int getScrollX(AbsHListView listView) {// 获取滚动距离
		View firstItem = listView.getChildAt(0);
		if (firstItem == null) {
			return 0;
		}
		int firstPos = listView.getFirstVisiblePosition();
		int paddingLeft = listView.getPaddingLeft();
		int scrollX = firstItem.getWidth() * firstPos + paddingLeft - firstItem.getLeft();
		return scrollX;
	}

	private void setSelectionByPostion(HListView listView, int postion) {
		View itemView = listView.findViewById(postion);
		if (itemView != null) {
			if (itemView.getVisibility() != View.VISIBLE) {// EmptyView
				if (postion > 0) {
					postion = postion - 1;
					itemView = listView.findViewById(postion);
				}
			}
			if (itemView != null) {
				listView.setSelectionFromLeft(postion, itemView.getLeft() - mPaddingLeft);
			}
		}
	}

	public void setIsDelayScroll(boolean isDelayScroll) {
		this.mIsDelayScroll = isDelayScroll;
	}

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
