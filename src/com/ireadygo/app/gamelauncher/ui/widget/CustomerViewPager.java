package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class CustomerViewPager extends ViewPager {

	public CustomerViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomerViewPager(Context context) {
		super(context);
	}

	// 不响应按键滚动操作
	@Override
	public boolean executeKeyEvent(KeyEvent event) {
		return false;
	}
}
