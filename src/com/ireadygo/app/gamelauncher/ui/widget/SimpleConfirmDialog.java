package com.ireadygo.app.gamelauncher.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;

public class SimpleConfirmDialog extends AlertDialog {
	private TextView mPromptTextView;
	private TextView mMsgTextView;
	private TextView mConfirmBtn;
	private CharSequence mMsg;
	private String mPrompt;
	private String mConfirmText;
	private View.OnClickListener mConfirmListener;

	private Drawable mDrawableLeft;

	public SimpleConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public SimpleConfirmDialog(Context context, int theme) {
		super(context, theme);
	}

	public SimpleConfirmDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_confirm_dialog);
		initView();
	}

	private void initView() {
		mPromptTextView = (TextView) findViewById(R.id.dialogPrompt);
		mMsgTextView = (TextView) findViewById(R.id.dialogMsg);
		mConfirmBtn = (TextView) findViewById(R.id.configmBtn);

		if (mPrompt != null) {
			mPromptTextView.setText(mPrompt);
		}

		if (mMsg != null) {
			mMsgTextView.setText(mMsg);
		}
		if (mDrawableLeft != null) {
			mMsgTextView.setCompoundDrawables(mDrawableLeft, null, null, null);
			mMsgTextView.setCompoundDrawablePadding(20);
		}
		if (mConfirmText != null) {
			mConfirmBtn.setText(mConfirmText);
		}

		if (mConfirmListener != null) {
			mConfirmBtn.setOnClickListener(mConfirmListener);
		}
		setCanceledOnTouchOutside(true);
	}

	public SimpleConfirmDialog setMsg(int msgId) {
		this.mMsg = getContext().getResources().getString(msgId);
		return this;
	}

	public SimpleConfirmDialog setMsg(CharSequence msg) {
		this.mMsg = msg;
		return this;
	}

	public SimpleConfirmDialog setPrompt(int promptId) {
		this.mPrompt = getContext().getResources().getString(promptId);
		return this;
	}

	public SimpleConfirmDialog setPrompt(String prompt) {
		this.mPrompt = prompt;
		return this;
	}

	public SimpleConfirmDialog setConfirmClickListener(View.OnClickListener listener) {
		this.mConfirmListener = listener;
		return this;
	}

	public SimpleConfirmDialog setConfirmText(int confirmTextId) {
		this.mConfirmText = getContext().getResources().getString(confirmTextId);
		return this;
	}

	public SimpleConfirmDialog setConfirmText(String confirmText) {
		this.mConfirmText = confirmText;
		return this;
	}

	public SimpleConfirmDialog setMsgLeftDrawable(Drawable drawableLeft) {
		mDrawableLeft = drawableLeft;
		return this;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == SnailKeyCode.SUN_KEY) {
			if (mConfirmBtn.hasFocus() && mConfirmListener != null) {
				mConfirmListener.onClick(mConfirmBtn);
			}
			return true;
		}else if(keyCode == SnailKeyCode.MOON_KEY){
			dismiss();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
