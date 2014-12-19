package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.Spinner;

public class CustomerSpinner extends Spinner {

	public CustomerSpinner(Context context, AttributeSet attrs, int defStyle, int mode) {
		super(context, attrs, defStyle, mode);
	}

	public CustomerSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomerSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomerSpinner(Context context, int mode) {
		super(context, mode);
	}

	public CustomerSpinner(Context context) {
		super(context);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
}
