package com.ireadygo.app.gamelauncher.ui;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.ui.listview.anim.AnimationAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;

public abstract class BaseAnimatorAdapter extends BaseAdapter {
	private View mCurrentSelectedView;
	private int mCurrentSelectedPos = -1;
	private HListView mListView;
	private AnimationAdapter mAnimationAdapter;
	// 进入退出动画持续时间
	private int mAnimationDurationMillis = Config.Animator.DURATION_SHORT;
	private int mAnimationDelayMillis = Config.Animator.DELAY_SHORT;

	public BaseAnimatorAdapter(HListView listView) {
		this.mListView = listView;
	}

	public Animator inAnimator(View view) {
		checkListView();
		PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.33f, 1f);
		PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.33f, 1f);
		Animator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleXHolder, scaleYHolder);
		animator.setInterpolator(new AccelerateInterpolator());
		return animator;
	}

	public Animator outAnimator(AnimatorListener listener) {
		checkListView();
		if (mAnimationAdapter != null) {
			mAnimationAdapter.reset();
		}
		List<Animator> animatorList = new ArrayList<Animator>();
		int first = mListView.getFirstVisiblePosition();
		int last = mListView.getLastVisiblePosition();
		int count = last - first + 1;
		for (int i = 0; i < count; i++) {
			final View child = mListView.getChildAt(i);
			PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.33f);
			PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.33f);
			Animator animator = ObjectAnimator.ofPropertyValuesHolder(child, scaleXHolder, scaleYHolder);
			animator.setDuration(mAnimationDurationMillis);
			animatorList.add(animator);
			animator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					child.setVisibility(View.GONE);
				}
			});
		}
		int delay = mAnimationDelayMillis;
		AnimatorSet animatorSet = new AnimatorSet();
		int size = animatorList.size();
		for (int i = 0; i < size; i++) {
			animatorSet.play(animatorList.get(size - i - 1)).after(i * delay);
		}
		if (listener != null) {
			animatorSet.addListener(listener);
		}
		animatorSet.setInterpolator(new AccelerateInterpolator());
		return animatorSet;
	}

	public final void doSelectedAnimator() {
		checkListView();
		View selectedView = mListView.getSelectedView();
		mCurrentSelectedPos = mListView.getSelectedItemPosition();
		if (selectedView == null) {
			return;
		}
		if (mCurrentSelectedView != null && mCurrentSelectedView != selectedView) {
			doUnselectedAnimator(null);
			mCurrentSelectedView = null;
		}
		selectedView.clearAnimation();
		Animator animator = selectedAnimator(selectedView);
		if (animator != null) {
//			ObjectAnimator.setFrameDelay(5);
			if (animator.getDuration() < 0) {
				animator.setDuration(Config.Animator.DURATION_SELECTED);
			}
			animator.start();
		}
		mCurrentSelectedView = selectedView;
	}

	public final void doUnselectedAnimator(AnimatorListener listener) {
		checkListView();
		if (mCurrentSelectedView != null) {
			Animator animator = unselectedAnimator(mCurrentSelectedView);
			if (animator != null) {
				if (listener != null) {
					animator.addListener(listener);
				}
//				ObjectAnimator.setFrameDelay(5);
				if (animator.getDuration() < 0) {
					animator.setDuration(Config.Animator.DURATION_UNSELECTED);
				}
				animator.start();
			}
		} else {
			if (listener != null) {
				listener.onAnimationEnd(null);
			}
		}
	}

	public View getSelectedView() {
		return mCurrentSelectedView;
	}

	public int getSelectedPos() {
		return mCurrentSelectedPos;
	}

	protected abstract Animator selectedAnimator(View view);

	protected abstract Animator unselectedAnimator(View view);

	public void setAnimatorDuration(int duration) {
		this.mAnimationDurationMillis = duration;
		if (mAnimationAdapter != null) {
			mAnimationAdapter.getViewAnimator().setAnimationDurationMillis(duration);
		}
	}

	public void setAnimatorDelay(int delay) {
		this.mAnimationDelayMillis = delay;
		if (mAnimationAdapter != null) {
			mAnimationAdapter.getViewAnimator().setAnimationDelayMillis(delay);
		}
	}

	/** 转成AnimationAdapter，用于执行进入动画 **/
	public AnimationAdapter toAnimationAdapter() {
		checkListView();
		mAnimationAdapter = new AnimationAdapter(this) {

			@Override
			public Animator[] getAnimators(ViewGroup parent, View view) {
				return new Animator[] { inAnimator(view) };
			}
		};
		mAnimationAdapter.setAbsListView(mListView);
		mAnimationAdapter.getViewAnimator().setAnimationDurationMillis(mAnimationDurationMillis);
		mAnimationAdapter.getViewAnimator().setAnimationDelayMillis(mAnimationDelayMillis);
		return mAnimationAdapter;
	}

	private void checkListView() {
		if (mListView == null) {
			throw new NullPointerException("必须先调用setHListView()方法!");
		}
	}

	public int getOutAnimatorDuration() {
		return mAnimationDurationMillis + (mListView.getChildCount()) * mAnimationDelayMillis;
	}

	public int getInAnimatorDuration() {
		return mAnimationDurationMillis + (mListView.getChildCount()) * mAnimationDelayMillis;
	}

	protected HListView getListView() {
		return mListView;
	}
}
