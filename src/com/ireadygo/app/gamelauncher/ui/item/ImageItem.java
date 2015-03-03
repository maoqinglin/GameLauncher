package com.ireadygo.app.gamelauncher.ui.item;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.helper.AnimatorHelper;

public class ImageItem extends BaseAdapterItem {
	private ImageItemHolder mHolder;
	private Drawable mIconDrawable;
	private Animator mSelectedAnimator;
	private Animator mUnselectedAnimator;

	public ImageItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		initView(context);
	}

	public ImageItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageItem(Context context) {
		super(context);
		initView(context);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Item);
		mIconDrawable = a.getDrawable(R.styleable.Item_item_icon);
		a.recycle();
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.image_item, this, true);
		mHolder = new ImageItemHolder();
		mHolder.background = (ImageView) findViewById(R.id.background);
		mHolder.icon = (ImageView) findViewById(R.id.icon);
		if (mIconDrawable != null) {
			LayoutParams params = new LayoutParams(mIconDrawable.getMinimumWidth(), mIconDrawable.getMinimumHeight());
			mHolder.background.setLayoutParams(params);
			mHolder.icon.setImageDrawable(mIconDrawable);
		}
		mHolder.iconLayout = (ViewGroup) findViewById(R.id.icon_layout);
		mHolder.title = (TextView) findViewById(R.id.title);
	}

	public ImageItemHolder getHolder() {
		return mHolder;
	}

	@Override
	public void toSelected(AnimatorListener listener) {
		if (mUnselectedAnimator != null && mUnselectedAnimator.isRunning()) {
			mUnselectedAnimator.cancel();
		}
		float iconScale = 1.088f;
		float bgScaleX = AnimatorHelper.calcBgScaleX(10, getWidth(), iconScale);
		float bgScaleY = AnimatorHelper.calcBgScaleY(0.5f, 10, getHeight(), getHeight(), iconScale);
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
		ObjectAnimator animatorIcon = ObjectAnimator.ofPropertyValuesHolder(mHolder.iconLayout, iconScaleXHolder,
				iconScaleYHolder);

		PropertyValuesHolder bgScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, bgScaleX);
		PropertyValuesHolder bgScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, bgScaleY);
		ObjectAnimator animatorBg = ObjectAnimator.ofPropertyValuesHolder(mHolder.background, bgScaleXHolder,
				bgScaleYHolder);

		animatorSet.playTogether(animatorIcon, animatorBg);
		animatorSet.setDuration(200);
		if (listener != null) {
			animatorSet.addListener(listener);
		}
		return animatorSet;
	}

	public void setBackgroundDimens(int width, int height) {
		LayoutParams params = new LayoutParams(width, height);
		mHolder.background.setLayoutParams(params);
	}

	public class ImageItemHolder {
		public ImageView background;
		public ImageView icon;
		public TextView title;
		public ViewGroup iconLayout;
	}
}
