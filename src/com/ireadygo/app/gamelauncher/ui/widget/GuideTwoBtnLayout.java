package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class GuideTwoBtnLayout extends LinearLayout {

	private InnerOnClickListener mInnerOnClickListener = new InnerOnClickListener();
	private OnLRBtnClickListener mListener;
	private TextView mLeftBtn;
	private TextView mRightBtn;

	public GuideTwoBtnLayout(Context context) {
		super(context);
		init(context);
	}

	public GuideTwoBtnLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GuideTwoBtnLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.starting_guide_two_btn_layout, this, true);
		mLeftBtn = (TextView) findViewById(R.id.leftBtn);
		mLeftBtn.setOnClickListener(mInnerOnClickListener);

		mRightBtn = (TextView) findViewById(R.id.rightBtn);
		mRightBtn.setOnClickListener(mInnerOnClickListener);

		mLeftBtn.requestFocus();
	}

	public void setOnLRBtnClickListener(OnLRBtnClickListener listener) {
		if(listener != null) {
			mListener = listener;
		}
	}

	public void setLeftBtnText(int resId) {
		mLeftBtn.setText(resId);
	}

	public void setRightBtnText(int resId) {
		mRightBtn.setText(resId);
	}

	private class InnerOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.leftBtn:
				if(mListener != null) {
					mListener.onLeftBtnClickListener(v);
				}
				break;
				
			case R.id.rightBtn:
				if(mListener != null) {
					mListener.onRightBtnClickListener(v);
				}
				break;

			default:
				break;
			}
		}
	}

	public interface OnLRBtnClickListener {
		void onLeftBtnClickListener(View view);

		void onRightBtnClickListener(View view);
	}
}
