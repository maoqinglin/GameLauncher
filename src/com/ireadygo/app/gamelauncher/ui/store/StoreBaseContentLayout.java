package com.ireadygo.app.gamelauncher.ui.store;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.ui.base.KeyEventLayout;

public abstract class StoreBaseContentLayout extends KeyEventLayout {
	private int mLayoutTag;
	private GameInfoHub mGameInfoHub;
	// private StoreOptionsLayout mOptionsLayout;
	private StoreDetailActivity mStoreFragment;

	public StoreBaseContentLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StoreBaseContentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StoreBaseContentLayout(Context context) {
		super(context);
	}

	public StoreBaseContentLayout(Context context, int layoutTag, StoreDetailActivity storeFragment) {
		super(context);
		setClipChildren(false);
		setClipToPadding(false);
		this.mLayoutTag = layoutTag;
		this.mStoreFragment = storeFragment;
	}

	public StoreDetailActivity getActivity() {
		return mStoreFragment;
	}

	public int getLayoutTag() {
		return mLayoutTag;
	}

	protected void init() {
		mGameInfoHub = GameInfoHub.instance(getContext());
	}

	protected GameInfoHub getGameInfoHub() {
		return mGameInfoHub;
	}

	protected Animator animatorTopEnter(AnimatorListener listener) {
		return getTranslateYAnimator(listener, -getHeight(), 0);
	}

	protected Animator animatorTopExit(AnimatorListener listener) {
		return getTranslateYAnimator(listener, 0, -getHeight());
	}

	protected Animator animatorBottomExit(AnimatorListener listener) {
		return getTranslateYAnimator(listener, 0, getHeight());
	}

	protected Animator animatorBottomEnter(AnimatorListener listener) {
		return getTranslateYAnimator(listener, getHeight(), 0);
	}

	protected ObjectAnimator getTranslateYAnimator(AnimatorListener listener, int startY, int endY) {
		ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, startY, endY);
		animatorTranslateY.setDuration(200);
		animatorTranslateY.setInterpolator(new AccelerateDecelerateInterpolator());
		if (listener != null) {
			animatorTranslateY.addListener(listener);
		}
		return animatorTranslateY;
	}

	@Override
	public boolean onMoonKey() {
		getActivity().getOptionsLayout().getCurrentSelectedView().requestFocus();
		return super.onMoonKey();
	}

	@Override
	public boolean onBackKey() {
		return false;
	}

}
