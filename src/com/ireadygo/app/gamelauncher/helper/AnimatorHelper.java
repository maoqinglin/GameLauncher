package com.ireadygo.app.gamelauncher.helper;

import android.animation.ObjectAnimator;
import android.view.View;

public class AnimatorHelper {
	public static ObjectAnimator createTranslateXAnimator(View target,float...values){
		ObjectAnimator animator = ObjectAnimator.ofFloat(target,View.TRANSLATION_X, values);
		return animator;
	}
	
	
}
