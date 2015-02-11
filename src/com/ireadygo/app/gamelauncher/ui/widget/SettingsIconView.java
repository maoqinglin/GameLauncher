package com.ireadygo.app.gamelauncher.ui.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.settings.SettingsItemEntity;

public class SettingsIconView extends RelativeLayout {
	private ImageView mSettingsViewBg;
	private ImageView mSettingsImg;
	private TextView mSettingsNameTxt;
	private SettingsItemEntity mSettingsEntiry;

	public SettingsIconView(Context context) {
		super(context);
	}

	public SettingsIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SettingsIconView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mSettingsViewBg = (ImageView) findViewById(R.id.settings_view_bg);
		mSettingsImg = (ImageView) findViewById(R.id.settings_icon);
		mSettingsNameTxt = (TextView) findViewById(R.id.settings_name);
		mSettingsNameTxt.setScaleX(0.8f);
		mSettingsNameTxt.setScaleY(0.8f);
	}

	public void setSettingsItemEntity(SettingsItemEntity itemEntity) {
		this.mSettingsEntiry = itemEntity;
		updateViewByEntity(itemEntity);
	}

	private void updateViewByEntity(SettingsItemEntity itemEntity) {
		if (itemEntity == null) {
			return;
		}
		mSettingsNameTxt.setText(itemEntity.getItemName());
		mSettingsImg.setImageDrawable(itemEntity.getItemIcon());
	}

	public Animator selectedAnimation() {
		AnimatorListener listener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mSettingsViewBg.setImageResource(R.drawable.settings_item_bg_shape);
			}
		};
		return createAnimator(listener, 0.25f, 1.2f, 1.32f, 2f, 1.0f, Config.SettingsIcon.TITLE_SLEECTED_TRANSLATE_Y);
	}

	public Animator unselectedAnimation() {
		AnimatorListener listener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mSettingsViewBg.setImageResource(R.drawable.corner_settings_item_bg_shape);
			}
		};
		return createAnimator(listener, 0.25f, 1, 1, 1, 0.8f, Config.SettingsIcon.TITLE_UNSLEECTED_TRANSLATE_Y);
	}

	private Animator createAnimator(AnimatorListener listener, float bgPivotY, float bgScaleX, float bgScaleY,
			float icScale, float titleScale, float titleTranslateY) {
		AnimatorSet animSet = new AnimatorSet();
		mSettingsViewBg.setPivotX(mSettingsViewBg.getWidth() / 2);
		mSettingsViewBg.setPivotY(mSettingsViewBg.getHeight() * bgPivotY);
		// 背景动画
		PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, bgScaleX);
		PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, bgScaleY);
		ObjectAnimator gameBgAnim = ObjectAnimator.ofPropertyValuesHolder(mSettingsViewBg, scaleXHolder, scaleYHolder);

		// 游戏海报动画
		ObjectAnimator gameIconXAnim = ObjectAnimator.ofFloat(mSettingsImg, View.SCALE_X, icScale);
		ObjectAnimator gameIconYAnim = ObjectAnimator.ofFloat(mSettingsImg, View.SCALE_Y, icScale);

		// 游戏名称动画
		PropertyValuesHolder txtScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, titleScale);
		PropertyValuesHolder txtScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, titleScale);
		PropertyValuesHolder txtTranslateYHolder = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, titleTranslateY);
		ObjectAnimator gameNameAnim = ObjectAnimator.ofPropertyValuesHolder(mSettingsNameTxt, txtScaleXHolder,
				txtScaleYHolder, txtTranslateYHolder);
		animSet.playTogether(gameBgAnim, gameIconXAnim, gameIconYAnim, gameNameAnim);
		animSet.setInterpolator(new AccelerateInterpolator());
		if (listener != null) {
			animSet.addListener(listener);
		}
		return animSet;
	}
}
