/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ireadygo.app.gamelauncher.mygame.utils;

import java.util.HashSet;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;

public class LauncherAnimUtils {
	static HashSet<Animator> sAnimators = new HashSet<Animator>();

	public static final int SCALE_ANIMATION_DURATION = 200; // in ms
	static Animator.AnimatorListener sEndAnimListener = new Animator.AnimatorListener() {
		public void onAnimationStart(Animator animation) {
		}

		public void onAnimationRepeat(Animator animation) {
		}

		public void onAnimationEnd(Animator animation) {
			sAnimators.remove(animation);
		}

		public void onAnimationCancel(Animator animation) {
			sAnimators.remove(animation);
		}
	};

	public static void cancelOnDestroyActivity(Animator a) {
		sAnimators.add(a);
		a.addListener(sEndAnimListener);
	}

	public static void onDestroyActivity() {
		HashSet<Animator> animators = new HashSet<Animator>(sAnimators);
		for (Animator a : animators) {
			if (a.isRunning()) {
				a.cancel();
			} else {
				sAnimators.remove(a);
			}
		}
	}

	public static AnimatorSet createAnimatorSet() {
		AnimatorSet anim = new AnimatorSet();
		cancelOnDestroyActivity(anim);
		return anim;
	}

	public static ValueAnimator ofFloat(float... values) {
		ValueAnimator anim = new ValueAnimator();
		anim.setFloatValues(values);
		cancelOnDestroyActivity(anim);
		return anim;
	}

	public static ObjectAnimator ofFloat(Object target, String propertyName, float... values) {
		ObjectAnimator anim = new ObjectAnimator();
		anim.setTarget(target);
		anim.setPropertyName(propertyName);
		anim.setFloatValues(values);
		cancelOnDestroyActivity(anim);
		return anim;
	}

	public static ObjectAnimator ofPropertyValuesHolder(Object target, PropertyValuesHolder... values) {
		ObjectAnimator anim = new ObjectAnimator();
		anim.setTarget(target);
		anim.setValues(values);
		cancelOnDestroyActivity(anim);
		return anim;
	}

	public static void startScaleAnimation(View animView, float... values) {
		if (animView != null && values != null) {
			PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", values[0]);
			PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", values[1]);
			ObjectAnimator childAnim = ObjectAnimator.ofPropertyValuesHolder(animView, scaleXHolder, scaleYHolder);
			// if(values[0] ==1.0f){
			// childAnim.setDuration(100).start();
			// }else{
			childAnim.setDuration(SCALE_ANIMATION_DURATION).start();
			// }
		}
	}
}
