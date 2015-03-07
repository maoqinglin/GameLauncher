package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class CustomLinearLayout extends LinearLayout{

	public CustomLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	protected void onDraw(Canvas canvas) {
//		if (getDividerDrawable() == null) {
//            return;
//        }
//
//        if (getOrientation() == VERTICAL) {
//            drawDividersVertical(canvas);
//        } else {
//            drawDividersHorizontal(canvas);
//        }
//	}
//	
//	void drawDividersVertical(Canvas canvas) {
//        final int count = getChildCount();
//        for (int i = 0; i < count; i++) {
//            final View child = getChildAt(i);
//
//            if (child != null && child.getVisibility() != GONE) {
//                if (hasDividerBeforeChildAt(i)) {
//                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
//                    final int top = child.getTop() - lp.topMargin - mDividerHeight;
//                    drawHorizontalDivider(canvas, top);
//                }
//            }
//            
//        }
//
//        if (hasDividerBeforeChildAt(count)) {
//            final View child = getVirtualChildAt(count - 1);
//            int bottom = 0;
//            if (child == null) {
//                bottom = getHeight() - getPaddingBottom() - mDividerHeight;
//            } else {
//                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
//                bottom = child.getBottom() + lp.bottomMargin;
//            }
//            drawHorizontalDivider(canvas, bottom);
//        }
//    }
}
