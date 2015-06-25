package com.ireadygo.app.gamelauncher.ui.settings;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.helper.AnimatorHelper;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.item.BaseAdapterItem;

public class SettingsItem extends BaseAdapterItem {
	private SettingsItemHoder mHolder;
	private Animator mSelectedAnimator;
	private Animator mUnselectedAnimator;

	public SettingsItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SettingsItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SettingsItem(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		View rootView = LayoutInflater.from(context).inflate(R.layout.settings_item, this, true);
		mHolder = new SettingsItemHoder();
		mHolder.iconLayout = (ViewGroup)rootView.findViewById(R.id.icon_layout);
		mHolder.background = (ImageView) rootView.findViewById(R.id.settings_view_bg);
		mHolder.icon = (ImageView) rootView.findViewById(R.id.settings_icon);
		mHolder.name = (TextView) rootView.findViewById(R.id.settings_name);
		mHolder.tip = (TextView) rootView.findViewById(R.id.settings_tip);
	}

	public SettingsItemHoder getHolder() {
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
	
	static class SettingsItemHoder {
		ViewGroup iconLayout;
		ImageView background;
		ImageView icon;
		TextView tip;
		TextView name;
	}
}
