package com.ireadygo.app.gamelauncher.ui.base;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.Config;

public class OptionsItem extends LinearLayout {
	private Drawable mIconDrawable;
	private String mTitleString = "";
	private ImageView mIconView;
	private TextView mTitleView;

	// private OnFocusChangeListener mFocusChangeListener;

	public OptionsItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context, attrs);
	}

	public OptionsItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public OptionsItem(Context context) {
		super(context);
	}

	private void initView(Context context, AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.options_item, this, true);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OptionsItem);
		mIconDrawable = ta.getDrawable(R.styleable.OptionsItem_store_icon);
		mTitleString = ta.getString(R.styleable.OptionsItem_store_title);
		ta.recycle();

		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);

		mIconView = (ImageView) findViewById(R.id.icon);
		if (mIconDrawable != null) {
			mIconView.setImageDrawable(mIconDrawable);
		}
		mTitleView = (TextView) findViewById(R.id.title);
		if (mTitleString != null) {
			mTitleView.setText(mTitleString);
		}
		// super.setOnFocusChangeListener(mExternalFocusChangeListener);
		setScaleX(0.8f);
		setScaleY(0.8f);
		setAlpha(0.3f);
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener l) {
		// this.mFocusChangeListener = l;
		super.setOnFocusChangeListener(l);
	}

	public Animator toFocusAnimator() {
		return focusChangeAnimator(1.0f, 1.0f);
	}

	public Animator toSelectedAnimator() {
		return focusChangeAnimator(1.0f, 0.8f);
	}

	public Animator toNoselectedAnimator() {
		return focusChangeAnimator(0.3f, 0.8f);
	}

	private Animator focusChangeAnimator(float textAlpha, float scale) {
		PropertyValuesHolder scaleHolderX = PropertyValuesHolder.ofFloat(View.SCALE_X, scale);
		PropertyValuesHolder scaleHolderY = PropertyValuesHolder.ofFloat(View.SCALE_Y, scale);
		PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat(View.ALPHA, textAlpha);
		Animator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(this, scaleHolderX, scaleHolderY);
		Animator alphaAnimator = ObjectAnimator.ofPropertyValuesHolder(this, alphaHolder);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(Config.Animator.DURATION_SHORT);
		animatorSet.playTogether(scaleAnimator, alphaAnimator);
		return animatorSet;
	}

	// private OnFocusChangeListener mExternalFocusChangeListener = new
	// OnFocusChangeListener() {
	//
	// @Override
	// public void onFocusChange(View v, boolean hasFocus) {
	// if (hasFocus) {
	// if(mHandler.hasMessages(WHAT_LOSE_FOCUS)){
	// mHandler.removeMessages(WHAT_LOSE_FOCUS);
	// }
	// toFocusAnimator().start();
	// } else {
	// mHandler.sendEmptyMessage(WHAT_LOSE_FOCUS);
	// }
	// if (mFocusChangeListener != null) {
	// mFocusChangeListener.onFocusChange(v, hasFocus);
	// }
	// }
	// };
}
