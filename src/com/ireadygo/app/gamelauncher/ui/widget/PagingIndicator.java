package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ireadygo.app.gamelauncher.R;

public class PagingIndicator extends View {
	private static final int WHAT_REFRESH = 1;
	private static final int HEIGHT_DEFAULT = 4;
	private static final int INDICATOR_WIDTH_DEFAULT = 98;
	private static final int BACKGROUND_WIDTH_MAX = 1100;
	private Drawable mIndicatorDrawable;
	private Drawable mBackgroundDrawable;
	private int mHeight = HEIGHT_DEFAULT;

	private int mIndicatorWidth = INDICATOR_WIDTH_DEFAULT;
	private int mBackgroundWidth = INDICATOR_WIDTH_DEFAULT;
	private int mIndicatorLeft;

	private double mScale;
	private HListView mListView;
	private boolean mIsBound;

	private Interpolation mInterpolation = sDetaultInterpolation;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_REFRESH:
				HListView listView = (HListView) msg.obj;
				doRefresh(listView);
				break;

			default:
				break;
			}
		};
	};

	public PagingIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PagingIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagingIndicator(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mIndicatorDrawable = getResources().getDrawable(R.drawable.paging_indicator_shape);
		mBackgroundDrawable = getResources().getDrawable(R.drawable.paging_indicator_background_shape);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mBackgroundDrawable.setBounds(0, 0, mBackgroundWidth, mHeight);
		mBackgroundDrawable.draw(canvas);

		mIndicatorDrawable.setBounds(mIndicatorLeft, 0, mIndicatorLeft + mIndicatorWidth, mHeight);
		mIndicatorDrawable.draw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mBackgroundWidth, mHeight);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, left + mBackgroundWidth, top + mHeight);
	}

	private void scroll(int left) {
		if (left < 0) {
			left = 0;
		}
		mIndicatorLeft = calcIndicatorLeft(left, mScale);
		int maxIndicatorLeft = mBackgroundWidth - mIndicatorWidth;
		// 列表拖到最后时的处理
		if (mIndicatorLeft >= maxIndicatorLeft - 2) {
			mIndicatorLeft = maxIndicatorLeft;
		}
		invalidate();
	}

	private void refresh(HListView listView) {
		if (mHandler.hasMessages(WHAT_REFRESH)) {
			mHandler.removeMessages(WHAT_REFRESH);
		}
		Message msg = Message.obtain();
		msg.what = WHAT_REFRESH;
		msg.obj = listView;
		mHandler.sendMessageDelayed(msg, 200);
	}

	private void doRefresh(HListView listView) {
		if (listView.getChildCount() > 0) {
			HListViewIndicatorInfo listViewInfo = mInterpolation.calcHListIndicatorInfo(mListView);
			int scrollX = listViewInfo.scrollX;
			int listWidth = listViewInfo.listWidth;
			int totalWidth = listViewInfo.listTotalWidth;
			if (scrollX < 0 || listWidth < 0 || totalWidth < 0) {
				return;
			}
			if (totalWidth <= listWidth) {// 说明列表不能滑动
				reset();
			}
			calcWidth(scrollX, listWidth, totalWidth);
		} else {
			reset();
		}
		measure(0, 0);
		requestLayout();
		invalidate();
	}

	public void bind(HListView listView) {
		if (mIsBound) {
			mListView.removeOnLayoutChangeListener(mListViewLayoutChangeListener);
			mListView = null;
			mIsBound = false;
		}
		mListView = listView;
		if (listView != null) {
			mIsBound = true;
			listView.addOnLayoutChangeListener(mListViewLayoutChangeListener);
			refresh(listView);
		}
	}

	public void onScroll(HListView listView) {
		if (!mIsBound) {
			return;
		}
		if (listView == null || mListView != listView || listView.getChildCount() == 0) {
			return;
		}
		HListViewIndicatorInfo listViewInfo = mInterpolation.calcHListIndicatorInfo(mListView);
		scroll(listViewInfo.scrollX);
	}

	public void setInterpolation(Interpolation interpolation) {
		if (interpolation == null) {
			this.mInterpolation = sDetaultInterpolation;
			return;
		}
		this.mInterpolation = interpolation;
	}

	private OnLayoutChangeListener mListViewLayoutChangeListener = new OnLayoutChangeListener() {

		@Override
		public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
				int oldRight, int oldBottom) {
			if (mIsBound) {
				refresh(mListView);
			}
		}
	};

	private void calcWidth(int left, int listWidth, int totalWidth) {
		int indicatorW = INDICATOR_WIDTH_DEFAULT;
		int backgroundW = calcBackgroundWidth(indicatorW, listWidth, totalWidth);
		if (backgroundW > BACKGROUND_WIDTH_MAX) {
			backgroundW = BACKGROUND_WIDTH_MAX;
		}
		mScale = calcScale(backgroundW, indicatorW, totalWidth, listWidth);
		mIndicatorLeft = calcIndicatorLeft(left, mScale);
		mIndicatorWidth = indicatorW;
		mBackgroundWidth = backgroundW;
	}

	private int calcIndicatorLeft(int left, double scale) {
		int indicatorLeft = (int) (left * scale);
		return indicatorLeft;
	}

	private double calcScale(int backgroundW, int indicatorW, int totalW, int listW) {
		return ((double) (backgroundW - indicatorW)) / (totalW - listW);
	}

	private int calcBackgroundWidth(int indicatorW, int listW, int totalW) {
		return totalW * indicatorW / listW;
	}

	private void reset() {
		mIndicatorWidth = INDICATOR_WIDTH_DEFAULT;
		mBackgroundWidth = mIndicatorWidth;
		mScale = 0d;
	}

	private class VListViewIndicatorInfo {
		public int itemTop;
		public int listHeight;
		public int listTotalHeight;
	}

	public static class HListViewIndicatorInfo {
		public int scrollX;
		public int listWidth;
		public int listTotalWidth;
	}

	public interface Interpolation {
		HListViewIndicatorInfo calcHListIndicatorInfo(HListView listView);
	}

	private static Interpolation sDetaultInterpolation = new Interpolation() {

		@Override
		public HListViewIndicatorInfo calcHListIndicatorInfo(HListView listView) {
			int firstPos = listView.getFirstVisiblePosition();
			int paddingLeft = listView.getPaddingLeft();
			int paddingRight = listView.getPaddingRight();
			int listWidth = listView.getWidth();
			View firstItem = listView.getChildAt(0);
			int scrollX = firstItem.getWidth() * firstPos + listView.getDividerWidth() * firstPos + paddingLeft
					- firstItem.getLeft();
			int totalWidth = paddingLeft + paddingRight + listView.getDividerWidth() * (listView.getCount())
					+ firstItem.getWidth() * listView.getCount();
			HListViewIndicatorInfo info = new HListViewIndicatorInfo();
			info.scrollX = scrollX;
			info.listWidth = listWidth;
			info.listTotalWidth = totalWidth;
			return info;
		}
	};
}
