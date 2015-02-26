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
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public abstract class BaseMultiAnimatorAdapter extends BaseAdapter {
	private View mCurrentSelectedView;
	private int mCurrentSelectedPos = -1;
	private HMultiListView mHMultiHListView;
	private AnimationAdapter mUpAnimationAdapter;
	private AnimationAdapter mDownAnimationAdapter;
	// 进入退出动画持续时间
	private int mAnimationDurationMillis = Config.Animator.DURATION_SHORT;
	private int mAnimationDelayMillis = Config.Animator.DELAY_SHORT;

	public BaseMultiAnimatorAdapter(HMultiListView listView) {
		this.mHMultiHListView = listView;
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
//		checkListView();
//		if (mAnimationAdapter != null) {
//			mAnimationAdapter.reset();
//		}
//		List<Animator> animatorList = new ArrayList<Animator>();
//		int first = mHMultiHListView.getFirstVisiblePosition();
//		int last = mHMultiHListView.getLastVisiblePosition();
//		int count = last - first + 1;
//		for (int i = 0; i < count; i++) {
//			final View child = mHMultiHListView.getChildAt(i);
//			PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.33f);
//			PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.33f);
//			Animator animator = ObjectAnimator.ofPropertyValuesHolder(child, scaleXHolder, scaleYHolder);
//			animator.setDuration(mAnimationDurationMillis);
//			animatorList.add(animator);
//			animator.addListener(new AnimatorListenerAdapter() {
//				@Override
//				public void onAnimationEnd(Animator animation) {
//					child.setVisibility(View.GONE);
//				}
//			});
//		}
//		int delay = mAnimationDelayMillis;
//		AnimatorSet animatorSet = new AnimatorSet();
//		int size = animatorList.size();
//		for (int i = 0; i < size; i++) {
//			animatorSet.play(animatorList.get(size - i - 1)).after(i * delay);
//		}
//		if (listener != null) {
//			animatorSet.addListener(listener);
//		}
//		animatorSet.setInterpolator(new AccelerateInterpolator());
//		return animatorSet;
		return null;
	}

	public final void doSelectedAnimator() {
		checkListView();
		View selectedView = mHMultiHListView.getSelectedView();
		mCurrentSelectedPos = mHMultiHListView.getSelectedItemPosition();
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
		if (mUpAnimationAdapter != null) {
			mUpAnimationAdapter.getViewAnimator().setAnimationDurationMillis(duration);
		}
		if (mDownAnimationAdapter != null) {
			mDownAnimationAdapter.getViewAnimator().setAnimationDurationMillis(duration);
		}
	}

	public void setAnimatorDelay(int delay) {
		this.mAnimationDelayMillis = delay;
		if (mUpAnimationAdapter != null) {
			mUpAnimationAdapter.getViewAnimator().setAnimationDelayMillis(delay);
		}
		if (mDownAnimationAdapter != null) {
			mDownAnimationAdapter.getViewAnimator().setAnimationDelayMillis(delay);
		}
	}

	/** 转成AnimationAdapter，用于执行进入动画 **/
	public AnimationAdapter[] toAnimationAdapter() {
		checkListView();
		mUpAnimationAdapter = new AnimationAdapter(this) {
			@Override
			public Animator[] getAnimators(ViewGroup parent, View view) {
				return new Animator[] { inAnimator(view) };
			}
		};
		//modify by linmaoqing 2015-2-26
//		mUpAnimationAdapter.setAbsListView(mHMultiHListView.getUpHListView());
		mUpAnimationAdapter.getViewAnimator().setAnimationDurationMillis(mAnimationDurationMillis);
		mUpAnimationAdapter.getViewAnimator().setAnimationDelayMillis(mAnimationDelayMillis);
		mDownAnimationAdapter = new AnimationAdapter(this) {
			@Override
			public Animator[] getAnimators(ViewGroup parent, View view) {
				return new Animator[] { inAnimator(view) };
			}
		};
		//modify by linmaoqing 2015-2-26
//		mDownAnimationAdapter.setAbsListView(mHMultiHListView.getDownHListView());
		mDownAnimationAdapter.getViewAnimator().setAnimationDurationMillis(mAnimationDurationMillis);
		mDownAnimationAdapter.getViewAnimator().setAnimationDelayMillis(mAnimationDelayMillis);
		return new AnimationAdapter[]{mUpAnimationAdapter,mDownAnimationAdapter};
	}

	private void checkListView() {
		if (mHMultiHListView == null) {
			throw new NullPointerException("必须先调用setHListView()方法!");
		}
	}

	public int getOutAnimatorDuration() {
		return mAnimationDurationMillis + (mHMultiHListView.getChildCount()) * mAnimationDelayMillis;
	}

	public int getInAnimatorDuration() {
		return mAnimationDurationMillis + (mHMultiHListView.getChildCount()) * mAnimationDelayMillis;
	}

	protected HMultiListView getListView() {
		return mHMultiHListView;
	}
}
