package com.ireadygo.app.gamelauncher.helper;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.util.Log;
import android.view.View;

public class AnimatorHelper {

	public static Animator createSelectAnimator(AnimatorListener listener, View background, View icon, View title) {
		float bgPivotY = 0.333f;
		float bgPivotX = 0.5f;
		int width = background.getWidth();
		int bgHeight = background.getHeight();
		float icScale = 1.08f;
		int icHeight = icon.getHeight();
		float titleScale = 1;
		float bgScaleX = calcBgScaleX(10, width, icScale);
		float bgScaleY = calcBgScaleY(bgPivotY, 10, bgHeight, icHeight, icScale);
		int titleTranslateY = getTitleTranslateY(bgHeight, bgScaleY, bgPivotY);
		Log.d("liu.js", "titleTranslateY--" + titleTranslateY);
		AnimatorInfo info = new AnimatorInfo(background, icon, title, width, bgHeight, icHeight, bgPivotX, bgPivotY,
				bgScaleX, bgScaleY, icScale, titleScale, titleTranslateY);
		return doCreateAnimator(listener, info);
	}

	public static Animator createUnselectAnimator(AnimatorListener listener, View background, View icon, View title) {
		float bgPivotY = 0.333f;
		float bgPivotX = 0.5f;
		int width = background.getWidth();
		int bgHeight = background.getHeight();
		float icScale = 1;
		int icHeight = icon.getHeight();
		float titleScale = 0.8f;
		float bgScaleX = 1;
		float bgScaleY = 1;
		int titleTranslateY = 0;
		AnimatorInfo info = new AnimatorInfo(background, icon, title, width, bgHeight, icHeight, bgPivotX, bgPivotY,
				bgScaleX, bgScaleY, icScale, titleScale, titleTranslateY);
		return doCreateAnimator(listener, info);
	}

	public static Animator doCreateAnimator(AnimatorListener listener, AnimatorInfo info) {
		AnimatorSet animSet = new AnimatorSet();
		info.background.setPivotX(info.width * info.bgPivotX);
		info.background.setPivotY(info.bgHeight * info.bgPivotY);
		// 背景动画
		PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, info.bgScaleX);
		PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, info.bgScaleY);
		ObjectAnimator gameBgAnim = ObjectAnimator.ofPropertyValuesHolder(info.background, scaleXHolder, scaleYHolder);

		// 游戏海报动画
		ObjectAnimator gameIconXAnim = ObjectAnimator.ofFloat(info.icon, View.SCALE_X, info.icScale);
		ObjectAnimator gameIconYAnim = ObjectAnimator.ofFloat(info.icon, View.SCALE_Y, info.icScale);

		// 游戏名称动画
		PropertyValuesHolder titleScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, info.titleScale);
		PropertyValuesHolder titleScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, info.titleScale);
		PropertyValuesHolder titleTranslateYHolder = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, info.titleTranslateY);
		ObjectAnimator titleAnim = ObjectAnimator.ofPropertyValuesHolder(info.title, titleScaleXHolder, titleScaleYHolder, titleTranslateYHolder);
		
		if(info.title != null){
			animSet.playTogether(gameBgAnim, gameIconXAnim, gameIconYAnim, titleAnim);
		}else{
			animSet.playTogether(gameBgAnim, gameIconXAnim, gameIconYAnim);
		}
		// animSet.setInterpolator(new AccelerateInterpolator());
		if (listener != null) {
			animSet.addListener(listener);
		}
		animSet.setDuration(200);
		return animSet;
	}

	public static float calcBgScaleY(float pivotY, int space, int bgHeight, int icHeight, float iconScale) {
		return (space + icHeight * (iconScale - 1) / 2) / (pivotY * bgHeight) + 1;
		// return (space / pivotY + icHeight * (iconScale - 1)) / bgHeight + 1;
	}

	public static float calcBgScaleX(int space, int width, float icScale) {
		return (space * 2 + width * icScale) / width;
	}

	private static int getTitleTranslateY(int bgHeight, float bgScaleY, float bgPivotY) {
		return (int) (bgHeight * (bgScaleY - 1) * (1 - bgPivotY) * (1 - bgPivotY));
	}

	public static class AnimatorInfo {
		View background;
		View icon;
		View title;
		int width;
		int bgHeight;
		int icHeight;
		float bgPivotX;
		float bgPivotY;
		float bgScaleX;
		float bgScaleY;
		float icScale;
		float titleScale;
		int titleTranslateY;

		public AnimatorInfo(View background, View icon, View title, int width, int bgHeight, int icHeight,
				float bgPivotX, float bgPivotY, float bgScaleX, float bgScaleY, float icScale, float titleScale,
				int titleTranslateY) {
			this.background = background;
			this.icon = icon;
			this.title = title;
			this.width = width;
			this.bgHeight = bgHeight;
			this.icHeight = icHeight;
			this.bgPivotX = bgPivotX;
			this.bgPivotY = bgPivotY;
			this.bgScaleX = bgScaleX;
			this.bgScaleY = bgScaleY;
			this.icScale = icScale;
			this.titleScale = titleScale;
			this.titleTranslateY = titleTranslateY;
		}

	}
}
