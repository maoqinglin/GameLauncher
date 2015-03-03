package com.ireadygo.app.gamelauncher.ui.store.category;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.helper.AnimatorHelper;
import com.ireadygo.app.gamelauncher.ui.item.BaseAdapterItem;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class CategoryItem extends BaseAdapterItem {
	private CategoryItemHoder mHolder;
	private Animator mSelectedAnimator;
	private Animator mUnselectedAnimator;

	public CategoryItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CategoryItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CategoryItem(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		View rootView = LayoutInflater.from(context).inflate(R.layout.category_item, this, true);
		mHolder = new CategoryItemHoder();
		mHolder.background = (ImageView) rootView.findViewById(R.id.category_item_background);
		mHolder.icon = (ImageView) rootView.findViewById(R.id.category_item_icon);
		mHolder.iconLayout = (ViewGroup) rootView.findViewById(R.id.category_item_icon_layout);
		mHolder.introLayout = (ViewGroup) rootView.findViewById(R.id.category_item_intro_layout);
		mHolder.titleLayout = (ViewGroup) rootView.findViewById(R.id.category_item_title_layout);
		mHolder.title = (TextView) rootView.findViewById(R.id.category_item_title);
		mHolder.countLayout = (ViewGroup) rootView.findViewById(R.id.category_item_count_layout);
		mHolder.count = (TextView) rootView.findViewById(R.id.category_item_count);
		Utils.setCustomTypeface(getContext(), "fonts/din_pro_regular.otf", mHolder.count);
		mHolder.count.getPaint().setFakeBoldText(true);
	}

	public CategoryItemHoder getHolder() {
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
		Log.d("liu.js", "toUnselected--" + this);
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

	static class CategoryItemHoder {
		ImageView background;
		ImageView icon;
		ViewGroup iconLayout;
		TextView title;
		ViewGroup titleLayout;
		ViewGroup introLayout;
		ViewGroup countLayout;
		TextView count;
	}
}
