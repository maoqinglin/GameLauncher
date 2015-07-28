package com.ireadygo.app.gamelauncher.ui.menu;

import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class TextMenu extends MenuItem {
	private static final float TEXT_SCALE_DEFAULT = 0.75f;
	private String mText;
	private TextView mTextView;
	private ImageView mMenuCircle;

	public TextMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		init(context);
	}

	public TextMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextMenu(Context context) {
		super(context);
		init(context);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MenuItem);
		mText = ta.getString(R.styleable.MenuItem_menu_title);
		ta.recycle();
	}

	private void init(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.menu_text, this, true);
		mTextView = (TextView) view.findViewById(R.id.menu_text);
		mMenuCircle = (ImageView) view.findViewById(R.id.menu_circle);
		if (mText != null) {
			mTextView.setText(mText);
		}
		mTextView.setScaleX(TEXT_SCALE_DEFAULT);
		mTextView.setScaleY(TEXT_SCALE_DEFAULT);
	}

	public TextView getTextView() {
		return mTextView;
	}

	@Override
	public void toSelected(AnimatorListener listener) {
		super.toSelected(listener);
		mTextView.setTextColor(Color.WHITE);
		setBackgroundResource(R.drawable.menu_nav_bg_normal_shape);
		mMenuCircle.setVisibility(View.VISIBLE);
	}

	@Override
	public void toUnfocused(AnimatorListener listener) {
		super.toUnfocused(listener);
		mTextView.setTextColor(Color.WHITE);
		mMenuCircle.setVisibility(View.INVISIBLE);
		setBackgroundResource(R.drawable.menu_nav_bg_normal_shape);
		mUnfocusedAnimator = createAnimator(listener, 150, 0.3f, TEXT_SCALE_DEFAULT);
		mUnfocusedAnimator.start();
	}

	@Override
	public void toFocused(AnimatorListener listener) {
		super.toFocused(listener);
		mTextView.setTextColor(Color.WHITE);
		mMenuCircle.setVisibility(View.INVISIBLE);
		mFocusedAnimator = createAnimator(listener, 300, 1, 1);
		mFocusedAnimator.start();
	}

	private ObjectAnimator createAnimator(AnimatorListener listener, int duration, float alpha, float scale) {
		PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat(View.ALPHA, alpha);
		PropertyValuesHolder holderScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, scale);
		PropertyValuesHolder holderScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, scale);

		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mTextView, holderAlpha, holderScaleX,
				holderScaleY);
		if (listener != null) {
			animator.addListener(listener);
		}
		animator.setDuration(duration);
		return animator;
	}
}
