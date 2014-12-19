package com.ireadygo.app.gamelauncher.ui.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;

public class ConfirmDialog extends AlertDialog {
	private TextView mPromptTextView;
	private TextView mMsgTextView;
	private TextView mCancelBtn;
	private TextView mConfirmBtn;
	private String mMsg;
	private String mPrompt;
	private String mCancelText;
	private String mConfirmText;
	private int mGravity = Gravity.CENTER;
	private int mMsgSize;
	private View.OnClickListener mConfirmListener;
	private View.OnClickListener mCancelListener;

	private Drawable mDrawableLeft;

	public ConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public ConfirmDialog(Context context, int theme) {
		super(context, theme);
	}

	public ConfirmDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_dialog);
		initView();
	}

	private void initView() {
		mPromptTextView = (TextView) findViewById(R.id.dialogPrompt);
		mMsgTextView = (TextView) findViewById(R.id.dialogMsg);
		mCancelBtn = (TextView) findViewById(R.id.cancelBtn);
		mConfirmBtn = (TextView) findViewById(R.id.configmBtn);

		if (mPrompt != null) {
			mPromptTextView.setText(mPrompt);
		}

		if (mMsg != null) {
			mMsgTextView.setText(mMsg);
		}

		if(mMsgSize > 0) {
			mMsgTextView.setTextSize(mMsgSize);
		}

		if(mGravity != Gravity.CENTER) {
			mMsgTextView.setGravity(mGravity);
		}

		if(mDrawableLeft != null){
			mMsgTextView.setCompoundDrawables(mDrawableLeft, null, null, null);
			mMsgTextView.setCompoundDrawablePadding(20);
		}

		if (mCancelText != null) {
			mCancelBtn.setText(mCancelText);
		}

		if (mConfirmText != null) {
			mConfirmBtn.setText(mConfirmText);
		}

		if(mCancelListener == null){
			mCancelListener = new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
				}
			};
		}
		mCancelBtn.setOnClickListener(mCancelListener);

		if (mConfirmListener != null) {
			mConfirmBtn.setOnClickListener(mConfirmListener);
		}
		setCanceledOnTouchOutside(true);
		mCancelBtn.requestFocus();
	}

	public ConfirmDialog setMsgTextSize(int size) {
		this.mMsgSize = size;
		return this;
	}

	public ConfirmDialog setMsgTextGravity(int gravity) {
		this.mGravity = gravity;
		return this;
	}

	public ConfirmDialog setMsg(int msgId) {
		this.mMsg = getContext().getResources().getString(msgId);
		return this;
	}

	public ConfirmDialog setMsg(String msg) {
		this.mMsg = msg;
		return this;
	}

	public ConfirmDialog setPrompt(int promptId) {
		this.mPrompt = getContext().getResources().getString(promptId);
		return this;
	}

	public ConfirmDialog setPrompt(String prompt) {
		this.mPrompt = prompt;
		return this;
	}

	public ConfirmDialog setConfirmClickListener(View.OnClickListener listener) {
		this.mConfirmListener = listener;
		return this;
	}

	public ConfirmDialog setCancelClickListener(View.OnClickListener listener) {
		this.mCancelListener = listener;
		return this;
	}

	public ConfirmDialog setCancelText(int cancelTextId) {
		this.mCancelText = getContext().getResources().getString(cancelTextId);
		return this;
	}

	public ConfirmDialog setConfirmText(int confirmTextId) {
		this.mConfirmText = getContext().getResources().getString(confirmTextId);
		return this;
	}

	public ConfirmDialog setCancelText(String cancelText) {
		this.mCancelText = cancelText;
		return this;
	}

	public ConfirmDialog setConfirmText(String confirmText) {
		this.mConfirmText = confirmText;
		return this;
	}

	public ConfirmDialog setMsgLeftDrawable(Drawable drawableLeft) {
		mDrawableLeft = drawableLeft;
		return this;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == SnailKeyCode.SUN_KEY){
			if(mCancelBtn.hasFocus() && mCancelListener != null){
				mCancelListener.onClick(mCancelBtn);
			}else if(mConfirmBtn.hasFocus() && mConfirmListener != null){
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
