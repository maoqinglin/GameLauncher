package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

public class VerticalProgressBar extends ProgressBar {

	public VerticalProgressBar(Context context) {
		super(context);
	}

	public VerticalProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VerticalProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	protected void onDraw(Canvas canvas) {
		canvas.rotate(-90);
		canvas.translate(-getHeight(), 0);
		super.onDraw(canvas);
	}

	@Override
	public synchronized void setProgress(int progress) {
		int pos = 100 - progress;
		Log.i("MainActivity", "The progress : " + progress);
		if (pos >= 0)
			super.setProgress(pos);
		else
			super.setProgress(0);
	}
}
