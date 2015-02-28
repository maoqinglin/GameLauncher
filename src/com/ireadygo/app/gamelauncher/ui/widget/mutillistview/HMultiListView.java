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

	private HMultiBaseAdapter mHMultiBaseAdapter;
	private List<HListView> mHListViews = new ArrayList<HListView>();
	private List<BaseAdapter> mBaseAdapters = new ArrayList<BaseAdapter>();
	private int mHorizontalSpacing, mVerticalSpacing;
	private List<List<?>> mDataLists = new ArrayList<List<?>>();
	private TouchScrollState mTouchScrollState = TouchScrollState.NONE;
	private int mMainScrollListViewIndex = -1;

	private int mPaddingLeft;
	private int mPaddingTop;
	private int mPaddingRight;
	private int mPaddingBottom;

	private BaseAdapterItem mSelectedItem;
	private OnItemSelectedListener mOnItemSelectedListener;
	private OnFocusChangeListener mOnFocusChangeListener;
	private OnItemClickListener mOnItemClickListener;

	enum TouchScrollState {
		NONE, UPTOUCH, DOWNTOUCH
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_SELECTED_ANIMATOR:
				BaseAdapterItem item = (BaseAdapterItem) msg.obj;
				item.toSelected(null);
				break;
			case WHAT_UNSELECTED_ANIMATOR:
				BaseAdapterItem itemUn = (BaseAdapterItem) msg.obj;
				itemUn.toUnselected(null);
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
		upHListView.setNextFocusLeftId(getNextFocusLeftId());
		upHListView.setNextFocusRightId(getNextFocusRightId());
		upHListView.setNextFocusUpId(getNextFocusUpId());
		
		HListView downHListView = (HListView) findViewById(R.id.downHList);
		downHListView.setPadding(mPaddingLeft, mVerticalSpacing / 2, mPaddingRight, mPaddingBottom);
		downHListView.setNextFocusLeftId(getNextFocusLeftId());
		downHListView.setNextFocusRightId(getNextFocusRightId());
		downHListView.setNextFocusDownId(getNextFocusDownId());
		mHListViews.add(upHListView);
		mHListViews.add(downHListView);
		setHorizontalSpacing(mHorizontalSpacing);
		// setVerticalSpacing(mVerticalSpacing);
		setKeyListener();
		setOnScrollListener();
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
	 *  返回选中的Item
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
				nextHListView = mHListViews.get(i + 1);
			}
			if (i - 1 > -1) {
				preHListView = mHListViews.get(i - 1);
			}
			setKeyListener(curHListView, preHListView, nextHListView);
		}
	}

	private void setKeyListener(final HListView curView, final HListView preView, final HListView nextView) {
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
						nextView.setSelectionFromLeft(position, left - mPaddingLeft);
					}
				} else if (keyCode == SnailKeyCode.UP_KEY) {
					if (preView != null) {
						preView.setSelectionFromLeft(position, left - mPaddingLeft);
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
					for (final HListView otherListView : mHListViews) {
						if (hListView.getId() != otherListView.getId()) {
							otherListView.smoothScrollBy(distance, duration, linear);
						}
					}
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
			if (!view.isInTouchMode()) {
				return;
			}
			if (view.getId() == R.id.upHList) {
				mMainScrollListViewIndex = 0;
			} else if (view.getId() == R.id.downHList) {
				mMainScrollListViewIndex = 1;
			}
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					|| scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			}
			if (mMainScrollListViewIndex == -1 && mMainScrollListViewIndex >= mHListViews.size()) {
				return;
			}
			for (int i = 0; i < mHListViews.size(); i++) {
				if (i != mMainScrollListViewIndex) {
					synScrollListview((HListView) view, mHListViews.get(i), view.getFirstVisiblePosition());
				}
			}
		}

		@Override
		public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (!view.isInTouchMode()) {
				return;
			}
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
				synListView.setSelectionFromLeft(position, left - mPaddingLeft);
			}
		}
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
	private <T> void initDataLists(int listNum, List<?> list) {
		if (!mDataLists.isEmpty()) {
			for (List<?> listData : mDataLists) {
				listData.clear();// 清除单个列表数据
			}

			for (int j = 0; j < list.size(); j++) {
				int dataListIndex = j % listNum;
				List<T> dataList = (List<T>) mDataLists.get(dataListIndex);
				dataList.add((T) list.get(j));
			}
		}
	}

	public BaseAdapter getAdapter() {
		for (HListView hListView : mHListViews) {
			if (hListView.hasFocus()) {
				return (BaseAdapter) hListView.getAdapter();
			}
		}
		return null;
	}

	private View getView(int arg0, View arg1, ViewGroup arg2) {
		if (mHMultiBaseAdapter != null) {
			return mHMultiBaseAdapter.getView(arg0, arg1, arg2);
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
			listNum = num;
			dataIndex = index;
		}

		public int getDataIndex() {
			return dataIndex;
		}

		@Override
		public int getCount() {
			if (data != null && !data.isEmpty()) {
				return data.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if(position > data.size()-1){
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
			return HMultiListView.this.getView(realPos, convertView, parent);
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

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	private void setInternalItemClickListener(OnItemClickListener onItemClickListener){
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

	private OnItemSelectedListener mInternalItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Log.d("liu.js", "onItemSelected--parent=" + parent + "|position=" + position);
			if (mSelectedItem != null) {
				animatorToUnselected(mSelectedItem);
				mSelectedItem = null;
			}
			if (hasFocus()) {
				mSelectedItem = (BaseAdapterItem) view;
				animatorToSelected(mSelectedItem);
			}
			if (mOnItemSelectedListener != null) {
				mOnItemSelectedListener.onItemSelected(parent, view, position, id);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			if (mSelectedItem != null) {
				animatorToUnselected(mSelectedItem);
				mSelectedItem = null;
			}
			if (mOnItemSelectedListener != null) {
				mOnItemSelectedListener.onNothingSelected(parent);
			}
		}

	};

	private OnFocusChangeListener mInternalFocusChangeL = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			if (hasFocus) {
				if (mSelectedItem != null) {
					animatorToUnselected(mSelectedItem);
					mSelectedItem = null;
				}
				HListView listView = (HListView) view;
				BaseAdapterItem selectedItem = (BaseAdapterItem) listView.getSelectedView();
				if (selectedItem == null) {
					if (listView.getChildCount() > 0) {
						mSelectedItem = ((BaseAdapterItem) listView.getChildAt(0));
					}
				} else {
					mSelectedItem = selectedItem;
				}
				animatorToSelected(mSelectedItem);
			} else {
				if (mSelectedItem != null) {
					animatorToUnselected(mSelectedItem);
					mSelectedItem = null;
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

	private void animatorToSelected(BaseAdapterItem item) {
		if (mHandler.hasMessages(WHAT_SELECTED_ANIMATOR)) {
			mHandler.removeMessages(WHAT_SELECTED_ANIMATOR);
		}
		Message msg = Message.obtain();
		msg.what = WHAT_SELECTED_ANIMATOR;
		msg.obj = item;
		mHandler.sendMessageDelayed(msg, 20);
	}

	private void animatorToUnselected(BaseAdapterItem item) {
		item.toUnselected(null);
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
