package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.menu.ImageTextMenu;
import com.ireadygo.app.gamelauncher.ui.menu.MenuItem;
import com.ireadygo.app.gamelauncher.ui.menu.TextMenu;

public class CustomFrameLayout extends FrameLayout implements OnGlobalFocusChangeListener{

	private ImageView mFocusWindow;
	private static final int DEFAULT_STROKE_WIDTH = 0;// dip

	private static final int DEFAULT_DURATION = 250;// ms
	public CustomFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomFrameLayout(Context context) {
		super(context);
	}

	public void addViewInLayout(View child) {
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = generateDefaultLayoutParams();
			if (params == null) {
				throw new IllegalArgumentException(
						"generateDefaultLayoutParams() cannot return null");
			}
		}
		super.addViewInLayout(child, -1, params);
	}
	
	@Override
	public void addView(View child) {
		super.addView(child);
	}
	
	public void removeViewInLayout(View child) {
		if(child.hasFocus()){
			child.clearFocus();
		}
		super.removeViewInLayout(child);
	}
	
	@Override
	public void removeView(View view) {
		super.removeView(view);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		addFocusWindow();
		this.getViewTreeObserver().addOnGlobalFocusChangeListener(this);
	}
	
	public void addFocusWindow() {
		if (mFocusWindow == null) {
			mFocusWindow = new ImageView(getContext());
			addView(mFocusWindow);
			// 初始化时隐藏焦点框
			hideFocusWindow();
		}
	}

	private Drawable getDefaultBackground() {
		GradientDrawable drawable = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, new int[] {
						Color.TRANSPARENT, Color.TRANSPARENT });
		drawable.setStroke(dipToPixels(DEFAULT_STROKE_WIDTH), Color.YELLOW);

		return drawable;
	}
	
	private int dipToPixels(int dip) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
				r.getDisplayMetrics());
		return (int) px;
	}

	private void hideFocusWindow() {
		setFocusWindowRect(0, 0);
	}
	
	/**
	 * 设置焦点框的宽高
	 * 
	 * @param width
	 *            焦点框宽度
	 * @param height
	 *            焦点框高度
	 */
	private void setFocusWindowRect(int width, int height) {
		ViewGroup.LayoutParams p = mFocusWindow.getLayoutParams();
		p.width = width;
		p.height = height;
		mFocusWindow.setLayoutParams(p);
	}

	@Override
	public void onGlobalFocusChanged(View oldFocus, View newFocus) {
		if (oldFocus == null) {
			return;
		} else if (oldFocus != null && oldFocus.getId() == R.id.focusView) {
//			if(newFocus instanceof TextMenu){
//				newFocus.setBackgroundResource(R.drawable.menu_nav_focused_bg);
//			}
			return;
		}
		boolean isFocusTranslate = false;
		if (newFocus != null) {
			if ((oldFocus instanceof MenuItem || oldFocus instanceof HListView)
					&& (newFocus instanceof MenuItem || newFocus instanceof HListView)) {// 临时过滤方案
				isFocusTranslate = true;
			}
			if(oldFocus instanceof ImageTextMenu && newFocus instanceof ImageTextMenu){
				return;
			}
			if (oldFocus instanceof HListView && newFocus instanceof HListView) {
				return;
			}
			if (oldFocus instanceof HListView) {
				oldFocus = ((HListView) oldFocus).getChildAt(0);
			}
			if (newFocus instanceof HListView) {
				newFocus = ((HListView) newFocus).getChildAt(0);
			}
			focusTranslate(oldFocus, newFocus, isFocusTranslate);
		}
	}

	private void focusTranslate(final View from, final View to, final boolean isFocusTranslate) {
		if (to == null) {
			return;
		}
		final int gap = dipToPixels(DEFAULT_STROKE_WIDTH);

		int[] location = new int[2];
		from.getLocationOnScreen(location);

		final int fromX = location[0] - gap;
		final int fromY = location[1] - gap; // y轴计算不准确，补差值
		
		to.getLocationOnScreen(location);
		final int toX = location[0] - gap;
		final int toY = location[1] - gap;
		final int currWidth = from.getWidth() + (gap << 1);
		final int currHeight = from.getHeight() + (gap << 1);

		// 首次显示矩形焦点框(默认为原始图矩形框)，
		if(isFocusTranslate){
			setFocusWindowRect(currWidth, currHeight);
			setFocusWindowBg(from, to,true,-1);
		}

		final int toWidth = to.getWidth() + (gap << 1);
		final int toHeight = to.getHeight() + (gap << 1);

		// 平移过程中焦点框宽高变化值
		final int deltaWidth = toWidth - currWidth;
		final int deltaHeight = toHeight - currHeight;

		Animation translate = new TranslateAnimation(fromX, toX, fromY, toY) {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				super.applyTransformation(interpolatedTime, t);

				// 相同宽高控件不做焦点焦点框大小改变，直接返回
				if (deltaWidth == 0 && deltaHeight == 0) {
					return;
				}

				if (interpolatedTime == 1.0) {
					// 隐藏边框
					hideFocusWindow();
					return;
				}
				// 根据差值，设置矩形焦点框的大小，显示平移效果
				setFocusWindowBg(from, to, false, interpolatedTime);
				setFocusWindowRect(
						currWidth + Math.round(interpolatedTime * deltaWidth),
						currHeight + Math.round(interpolatedTime * deltaHeight));
			}
		};

		translate.setDuration(isFocusTranslate ? DEFAULT_DURATION : 0);
		translate.setFillAfter(false);
		translate.setInterpolator(getContext(),
				android.R.anim.accelerate_interpolator);
		translate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				if(from instanceof MenuItem){
					from.setBackgroundDrawable(new BitmapDrawable());
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束，隐藏焦点框
				if(to instanceof TextMenu){
					to.setBackgroundResource(R.drawable.menu_nav_focused_bg);
				}
				hideFocusWindow();
			}
		});

		mFocusWindow.startAnimation(translate);
	}

	private void setFocusWindowBg(final View from, final View to, boolean isInit, float interpolatedTime) {
		if (from instanceof MenuItem && to instanceof MenuItem){
			mFocusWindow.setBackgroundResource(R.drawable.menu_nav_focused_bg);
		}else if ((from instanceof MenuItem) && !(to instanceof MenuItem)) {
			if (isInit) {
				mFocusWindow.setBackgroundResource(R.drawable.menu_nav_focused_bg);
			} else if (interpolatedTime > 0.15) {
				mFocusWindow.setBackgroundResource(R.drawable.settings_item_bg_selected_shape);
			}
		} else {
			if (!(from instanceof MenuItem) && to instanceof MenuItem) {
				if (isInit) {
					mFocusWindow.setBackgroundResource(R.drawable.settings_item_bg_selected_shape);
				} else if (interpolatedTime > 0.85) {
					mFocusWindow.setBackgroundResource(R.drawable.menu_nav_focused_bg);
				}
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		hideFocusWindow();

		return super.dispatchTouchEvent(ev);
	}
}
