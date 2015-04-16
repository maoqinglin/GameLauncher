package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;

/**
 * 处理输入框按日键时，不弹出输入法问题
 * 
 * @author Administrator
 * 
 */
public class CustomerEditText extends EditText {

	public CustomerEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomerEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomerEditText(Context context) {
		super(context);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == SnailKeyCode.SUN_KEY) {
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (!imm.isActive()) {
				imm.showSoftInput(this, 0);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
