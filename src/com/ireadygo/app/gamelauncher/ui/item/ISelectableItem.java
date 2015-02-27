package com.ireadygo.app.gamelauncher.ui.item;

import android.animation.Animator.AnimatorListener;

public interface ISelectableItem {
	void toSelected(AnimatorListener listener);

	void toUnselected(AnimatorListener listener);
}
