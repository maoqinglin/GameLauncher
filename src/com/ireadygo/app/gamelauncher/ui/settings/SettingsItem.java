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
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
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
		mHolder.viewLayout = (ViewGroup)rootView.findViewById(R.id.settings_item);
		mHolder.background = (ImageView) rootView.findViewById(R.id.settings_view_bg);
		mHolder.icon = (ImageView) rootView.findViewById(R.id.settings_icon);
		mHolder.name = (TextView) rootView.findViewById(R.id.settings_name);
	}

	public SettingsItemHoder getHolder() {
		return mHolder;
	}

	@Override
	public void toSelected(AnimatorListener listener) {
		if (mUnselectedAnimator != null && mUnselectedAnimator.isRunning()) {
			mUnselectedAnimator.cancel();
		}
		mHolder.background.setImageResource(R.drawable.settings_item_bg_shape);
		mSelectedAnimator = createAnimator(listener, 0.25f, 1.15f, 1.15f, 1.23f, 1.0f, Config.SettingsIcon.TITLE_SLEECTED_TRANSLATE_Y);
		mSelectedAnimator.start();
	}

	@Override
	public void toUnselected(AnimatorListener listener) {
		if (mSelectedAnimator != null && mSelectedAnimator.isRunning()) {
			mSelectedAnimator.cancel();
		}
		mHolder.background.setImageResource(R.drawable.corner_settings_item_bg_shape);
		mUnselectedAnimator = createAnimator(listener, 0.25f, 1, 1, 1, 0.8f, Config.SettingsIcon.TITLE_UNSLEECTED_TRANSLATE_Y);
		mUnselectedAnimator.start();
	}

	private Animator createAnimator(AnimatorListener listener, float bgPivotY, float bgScaleX, float bgScaleY,
			float icScale, float titleScale, float titleTranslateY) {
		AnimatorSet animSet = new AnimatorSet();
		mHolder.background.setPivotX(mHolder.background.getWidth() / 2);
		mHolder.background.setPivotY(mHolder.background.getHeight() * bgPivotY);
		// 背景动画
		PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, bgScaleX);
		PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, bgScaleY);
		ObjectAnimator gameBgAnim = ObjectAnimator.ofPropertyValuesHolder(mHolder.background, scaleXHolder, scaleYHolder);

		// 游戏海报动画
		ObjectAnimator gameIconXAnim = ObjectAnimator.ofFloat(mHolder.icon, View.SCALE_X, icScale);
		ObjectAnimator gameIconYAnim = ObjectAnimator.ofFloat(mHolder.icon, View.SCALE_Y, icScale);

		// 游戏名称动画
		PropertyValuesHolder txtScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, titleScale);
		PropertyValuesHolder txtScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, titleScale);
		PropertyValuesHolder txtTranslateYHolder = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, titleTranslateY);
		ObjectAnimator gameNameAnim = ObjectAnimator.ofPropertyValuesHolder(mHolder.name, txtScaleXHolder,
				txtScaleYHolder, txtTranslateYHolder);
		animSet.playTogether(gameBgAnim, gameIconXAnim, gameIconYAnim, gameNameAnim);
		animSet.setInterpolator(new AccelerateInterpolator());
		if (listener != null) {
			animSet.addListener(listener);
		}
		return animSet;
	}

	static class SettingsItemHoder {
		ViewGroup viewLayout;
		ImageView background;
		ImageView icon;
		ImageView notificationIcon;
		TextView info;
		TextView name;
	}
}
