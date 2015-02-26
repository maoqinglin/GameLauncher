package com.ireadygo.app.gamelauncher.ui.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.ireadygo.app.gamelauncher.R;

public class ImageMenu extends MenuItem {
	private Drawable mIconDrawable;
	private ImageView mImageView;

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
		mImageView = (ImageView) findViewById(R.id.menu_image);
		if (mIconDrawable != null) {
			mImageView.setImageDrawable(mIconDrawable);
		}
	}

	public ImageView getImageView() {
		return mImageView;
	}

	// public void toInit() {
	// float destAlpha = Config.MenuItem.INIT_ALPHA;
	// float destScale = Config.MenuItem.INIT_SCALE;
	// boolean hasBackground = false;
	// int destTitleY = Config.MenuItem.INIT_TITLE_Y;
	// doAnimator(null, hasBackground, destAlpha, destScale, destTitleY);
	// }
	//
	// public void toFocusedOnMenuFocused(AnimatorListener listener) {
	// doAnimator(listener, true, Config.MenuItem.FOCUSED_ALPHA,
	// Config.MenuItem.FOCUSED_SCALE, Config.MenuItem.FOCUSED_TITLE_Y);
	// }
	//
	// public void toSelectedOnMenuSelected() {
	// doAnimator(null, false, Config.MenuItem.SELECTED_ALPHA,
	// Config.MenuItem.SELECTED_SCALE, Config.MenuItem.SELECTED_TITLE_Y);
	// }
	//
	// public void toNoFocusedOnMenuFocused() {
	// doAnimator(null, false, Config.MenuItem.NO_FOCUSED_ALPHA,
	// Config.MenuItem.NO_FOCUSED_SCALE, Config.MenuItem.NO_FOCUSED_TITLE_Y);
	// }
	//
	// public void toNoSelectedOnMenuSelected() {
	// doAnimator(null, false, Config.MenuItem.NO_SELECTED_ALPHA,
	// Config.MenuItem.NO_SELECTED_SCALE, Config.MenuItem.NO_SELECTED_TITLE_Y);
	// }
	//
	// private void doAnimator(AnimatorListener listener, boolean hasBackground,
	// float destAlpha, float destScale,
	// int destTitleY) {
	// ObjectAnimator animBg;
	// if (hasBackground) {
	// setBackground(mBgDrawable);
	// animBg = ObjectAnimator.ofFloat(mMenuBackground, View.ALPHA, 0, 1);
	// } else {
	// animBg = ObjectAnimator.ofFloat(mMenuBackground, View.ALPHA, 1, 0);
	// animBg.addListener(new AnimatorListenerAdapter() {
	// @Override
	// public void onAnimationEnd(Animator animation) {
	// setBackground(null);
	// }
	//
	// @Override
	// public void onAnimationCancel(Animator animation) {
	// setBackground(null);
	// }
	// });
	// }
	// ObjectAnimator animTitle = ObjectAnimator.ofFloat(mMenuTitle,
	// View.TRANSLATION_Y, destTitleY);
	// ObjectAnimator animAlpha = ObjectAnimator.ofFloat(this, View.ALPHA,
	// getAlpha(), destAlpha);
	// ObjectAnimator animScaleX = ObjectAnimator.ofFloat(this, View.SCALE_X,
	// getScaleX(), destScale);
	// ObjectAnimator animScaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y,
	// getScaleY(), destScale);
	// AnimatorSet animatorSet = new AnimatorSet();
	// animatorSet.playTogether(animBg, animTitle, animAlpha, animScaleX,
	// animScaleY);
	// animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
	// animatorSet.setDuration(200);
	// if (listener != null) {
	// animatorSet.addListener(listener);
	// }
	// animatorSet.start();
	// }
	//
}
