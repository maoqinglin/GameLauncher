package com.ireadygo.app.gamelauncher.ui.widget;

import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

public class SnailAutoCompleteTextView extends AutoCompleteTextView{

	public SnailAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SnailAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SnailAutoCompleteTextView(Context context) {
		super(context);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == SnailKeyCode.SUN_KEY){
			keyCode = KeyEvent.KEYCODE_DPAD_CENTER;
		}
		return super.onKeyDown(keyCode, event);
	}
}
