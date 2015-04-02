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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.helper.AnimatorHelper;
import com.ireadygo.app.gamelauncher.ui.Config;

public class AppItem extends BaseAdapterItem {
	private AppItemHolder mHolder;
	private Drawable mIconDrawable;
	private Animator mSelectedAnimator;
	private Animator mUnselectedAnimator;

	public AppItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		initView(context);
	}

	public AppItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AppItem(Context context) {
		super(context);
		initView(context);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Item);
		mIconDrawable = a.getDrawable(R.styleable.Item_item_icon);
		a.recycle();
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.app_item, this, true);
		mHolder = new AppItemHolder();
		mHolder.iconLayout = (ViewGroup)findViewById(R.id.icon_layout);
		mHolder.background = (ImageView) findViewById(R.id.background);
		mHolder.icon = (ImageView) findViewById(R.id.icon);
		mHolder.uninstallIcon = (ImageView) findViewById(R.id.delete_icon);
		if (mIconDrawable != null) {
			LayoutParams params = new LayoutParams(mIconDrawable.getMinimumWidth(), mIconDrawable.getMinimumHeight());
			mHolder.background.setLayoutParams(params);
			mHolder.icon.setImageDrawable(mIconDrawable);
		}
		mHolder.title = (TextView) findViewById(R.id.title);
	}

	public AppItemHolder getHolder() {
		return mHolder;
	}

	@Override
	public void toSelected(AnimatorListener listener) {
		if (mUnselectedAnimator != null && mUnselectedAnimator.isRunning()) {
			mUnselectedAnimator.cancel();
		}
		mSelectedAnimator = AnimatorHelper.createSelectAnimator(listener, mHolder.background, mHolder.iconLayout, mHolder.title);
		mSelectedAnimator.start();
	}

	@Override
	public void toUnselected(AnimatorListener listener) {
		if (mSelectedAnimator != null && mSelectedAnimator.isRunning()) {
			mSelectedAnimator.cancel();
		}
		mUnselectedAnimator = AnimatorHelper.createUnselectAnimator(listener, mHolder.background, mHolder.iconLayout, mHolder.title);
		mUnselectedAnimator.start();
	}

	public class AppItemHolder {
		public ViewGroup iconLayout;
		public ImageView background;
		public ImageView icon;
		public ImageView uninstallIcon;
		public TextView title;
	}
}
