package com.ireadygo.app.gamelauncher.ui.menu;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
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

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.helper.AnimatorHelper;

public class ImageMenu extends MenuItem {
	private Drawable mIconDrawable;
	private ImageView mIconView;
	private ImageView mBackgroundView;

	public ImageMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		init(context);
	}

	public ImageMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageMenu(Context context) {
		super(context);
		init(context);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MenuItem);
		mIconDrawable = ta.getDrawable(R.styleable.MenuItem_menu_icon);
		ta.recycle();
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.menu_image, this, true);
		mIconView = (ImageView) findViewById(R.id.menu_image);
		mBackgroundView = (ImageView) findViewById(R.id.menu_background);
		if (mIconDrawable != null) {
			mIconView.setImageDrawable(mIconDrawable);
		}
	}

	public ImageView getImageView() {
		return mIconView;
	}

	@Override
	public void toFocused(AnimatorListener listener) {
		toSelected(listener);
		super.toFocused(listener);
	}

	@Override
	public void toUnfocused(AnimatorListener listener) {
		toUnselected(listener);
		super.toUnfocused(listener);
	}
	
	@Override
	public void toSelected(AnimatorListener listener) {
		if (mUnselectedAnimator != null && mUnselectedAnimator.isRunning()) {
			mUnselectedAnimator.cancel();
		}
		float iconScale = 1.371f;
		float bgScaleX = AnimatorHelper.calcBgScaleX(5, getWidth(), iconScale);
		float bgScaleY = AnimatorHelper.calcBgScaleY(0.5f, 5, getHeight(), getHeight(), iconScale);
		mSelectedAnimator = createAnimator(listener, iconScale, bgScaleX, bgScaleY);
		mSelectedAnimator.start();
	}

	@Override
	public void toUnselected(AnimatorListener listener) {
		if (mSelectedAnimator != null && mSelectedAnimator.isRunning()) {
			mSelectedAnimator.cancel();
		}
		mUnselectedAnimator = createAnimator(listener, 1, 1, 1);
		mUnselectedAnimator.start();
	}

	private Animator createAnimator(AnimatorListener listener, float iconScale, float bgScaleX, float bgScaleY) {
		AnimatorSet animatorSet = new AnimatorSet();

		PropertyValuesHolder iconScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, iconScale);
		PropertyValuesHolder iconScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, iconScale);
		ObjectAnimator animatorIcon = ObjectAnimator.ofPropertyValuesHolder(mIconView, iconScaleXHolder,
				iconScaleYHolder);

		PropertyValuesHolder bgScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, bgScaleX);
		PropertyValuesHolder bgScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, bgScaleY);
		ObjectAnimator animatorBg = ObjectAnimator.ofPropertyValuesHolder(mBackgroundView, bgScaleXHolder,
				bgScaleYHolder);

		animatorSet.playTogether(animatorIcon, animatorBg);
		animatorSet.setDuration(200);
		if (listener != null) {
			animatorSet.addListener(listener);
		}
		return animatorSet;
	}
}
