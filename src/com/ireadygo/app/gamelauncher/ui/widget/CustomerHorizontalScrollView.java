package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
/**
 * 加入滚动监听
 * @author Administrator
 *
 */
public class CustomerHorizontalScrollView extends HorizontalScrollView {
	private OnScrollChangedListener mScrollChangedListener;

	public CustomerHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomerHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomerHorizontalScrollView(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldX, int oldY) {
		super.onScrollChanged(x, y, oldX, oldY);
		if (mScrollChangedListener != null) {
			mScrollChangedListener.onScrollChanged(x, y, oldX, oldY);
		}
	}

	public boolean isAtTop() {
		return getScrollY() <= 0;
	}

	public boolean isAtBottom() {
		return getScrollY() == getChildAt(getChildCount() - 1).getBottom() + getPaddingBottom() - getHeight();
	}

	public boolean isAtLeft() {
		return getScrollX() <= 0;
	}

	public boolean isAtRight() {
		return getScrollX() == getChildAt(getChildCount() - 1).getRight() + getPaddingRight();
	}

	public interface OnScrollChangedListener {
		public void onScrollChanged(int x, int y, int oldxX, int oldY);
	}

	public void setOnScrollListener(OnScrollChangedListener onScrollChangedListener) {
		this.mScrollChangedListener = onScrollChangedListener;
	}
}
