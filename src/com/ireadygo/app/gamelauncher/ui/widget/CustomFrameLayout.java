package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class CustomFrameLayout extends FrameLayout {

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
		Log.d("liu.js", "CustomFrameLayout--addViewInLayout--view=" + child);
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
		Log.d("liu.js", "CustomFrameLayout--addView--view=" + child);
	}
	
	public void removeViewInLayout(View child) {
		Log.d("liu.js", "CustomFrameLayout--removeViewInLayout--view=" + child);
		if(child.hasFocus()){
			child.clearFocus();
		}
		super.removeViewInLayout(child);
	}
	
	@Override
	public void removeView(View view) {
		Log.d("liu.js", "CustomFrameLayout--removeView--view=" + view);
		super.removeView(view);
	}
}
