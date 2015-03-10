package com.ireadygo.app.gamelauncher.ui.menu;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;

public abstract class MenuItem extends FrameLayout implements IMenuItem {

	private int mMenuIndex = -1;
	private BaseContentFragment mContentFragment;
	private State mState = State.INIT;
	protected Animator mInitAnimator;
	protected Animator mFocusedAnimator;
	protected Animator mUnfocusedAnimator;
	protected Animator mSelectedAnimator;
	protected Animator mUnselectedAnimator;

	public MenuItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MenuItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		init();
	}

	public MenuItem(Context context) {
		super(context);
		init();
	}

	@Override
	public void setContentFragment(BaseContentFragment contentFragment) {
		this.mContentFragment = contentFragment;
	}

	@Override
	public BaseContentFragment getContentFragment() {
		return mContentFragment;
	}

	@Override
	public void setIndex(int index) {
		this.mMenuIndex = index;
	}

	@Override
	public int getIndex() {
		return mMenuIndex;
	}

	private void init() {
		setClipChildren(false);
		setClipToPadding(false);
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	@Override
	public void toFocused(AnimatorListener listener) {
		mState = State.FOCUSED;
		if (mUnfocusedAnimator != null && mUnfocusedAnimator.isRunning()) {
			mUnfocusedAnimator.cancel();
		}
	}

	@Override
	public void toUnfocused(AnimatorListener listener) {
		mState = State.NOFOCUSED;
		if (mFocusedAnimator != null && mFocusedAnimator.isRunning()) {
			mFocusedAnimator.cancel();
		}
	}

	@Override
	public void toSelected(AnimatorListener listener) {
		mState = State.SELECTED;
	}

	@Override
	public void toUnselected(AnimatorListener listener) {
		mState = State.NOSELECTED;
	}

	@Override
	public void toInit(AnimatorListener listener) {
		mState = State.INIT;
	}

}
