package com.ireadygo.app.gamelauncher.ui;

import java.util.HashMap;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.ireadygo.app.gamelauncher.ui.base.BaseFragment;
import com.ireadygo.app.gamelauncher.ui.widget.CustomFrameLayout;

public class CustomFragmentManager {
	private static final int WHAT_REMOVE_FRAGMENT = 1;
	private static final int WHAT_ADD_FRAGMENT = 2;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private Map<String, BaseFragment> mCacheFragmentList = new HashMap<String, BaseFragment>();
	private Runnable mAddFragmentRunnable;
	private Runnable mAddFragmentWithAnimatorRunnable;

	public SparseArray<BaseFragment> mAddedFragmentList = new SparseArray<BaseFragment>();

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_ADD_FRAGMENT:
				break;

			default:
				break;
			}
		};
	};

	public CustomFragmentManager(Activity activity) {
		this.mActivity = activity;
		mInflater = LayoutInflater.from(activity);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		for (int i = 0; i < mAddedFragmentList.size(); i++) {
			BaseFragment fragment = mAddedFragmentList.valueAt(i);
			if (fragment.onKeyDown(keyCode, event)) {
				return true;
			}
		}
		return false;
	}

	public void addFragment(CustomFrameLayout container, BaseFragment fragment) {
		addFragmentInLayout(container, fragment);
		container.requestLayout();
		container.invalidate();
	}

	public void addFragmentInLayout(CustomFrameLayout container, BaseFragment fragment) {
		View view = fragment.getRootView();
		container.addViewInLayout(view);
		if (!fragment.isResumed()) {
			fragment.onResume();
		}
		if (!hasFragment(fragment)) {
			mAddedFragmentList.put(fragment.getRootView().getId(), fragment);
		}
	}

	public void addFragmentWithAnimation(CustomFrameLayout container, BaseFragment fragment, Animator animatorIn) {
		addFragmentInLayout(container, fragment);
		if (animatorIn != null) {
			animatorIn.start();
		}
		container.requestLayout();
		container.invalidate();
	}

	public void removeFragment(CustomFrameLayout container, BaseFragment fragment) {
		removeAddRunnable();
		externalRemoveFragment(container, fragment);
	}

	public void removeFragmentInLayout(CustomFrameLayout container, BaseFragment fragment) {
		if (fragment.isResumed()) {
			fragment.onPause();
		}
		View view = fragment.getRootView();
		container.removeViewInLayout(view);
		fragment.onDestoryView();
		int index = mAddedFragmentList.indexOfValue(fragment);
		if (index >= 0) {
			mAddedFragmentList.removeAt(index);
		}
	}

	public void removeFragmentWithAnimation(final CustomFrameLayout container, final BaseFragment fragment,
			Animator animatorOut) {
		if (animatorOut == null) {
			externalRemoveFragment(container, fragment);
		} else {
			animatorOut.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					externalRemoveFragment(container, fragment);
				}
			});
			animatorOut.start();
		}
	}

	public void replaceFragment(CustomFrameLayout parent, BaseFragment prevFragment, BaseFragment destFragment) {
		if (prevFragment != null) {
			externalRemoveFragment(parent, prevFragment);
		}
		if (destFragment != null) {
			addFragment(parent, destFragment);
		}
	}

	public void replaceFragmentWithAnimation(final CustomFrameLayout container, final BaseFragment prevFragment,
			final BaseFragment destFragment) {
		removeAddRunnable();
		final Animator animatorIn = destFragment.createInAnimator(null);
		if (prevFragment == null) {
			addFragmentWithAnimation(container, destFragment, animatorIn);
			return;
		}
		final Animator animatorOut = prevFragment.createOutAnimator(null);
		if (animatorIn != null && animatorOut != null) {
			mAddFragmentWithAnimatorRunnable = new AddFragmentWithAnimatorRunnable(container, destFragment, animatorIn);
			mHandler.postDelayed(mAddFragmentWithAnimatorRunnable, prevFragment.getOutAnimatorDuration());
			removeFragmentWithAnimation(container, prevFragment, animatorOut);
		} else if (animatorIn != null) {
			externalRemoveFragment(container, prevFragment);
			addFragmentWithAnimation(container, destFragment, animatorIn);
		} else if (animatorOut != null) {
			mAddFragmentRunnable = new AddFragmentRunnable(container, destFragment);
			mHandler.postDelayed(mAddFragmentRunnable, prevFragment.getOutAnimatorDuration());
			removeFragmentWithAnimation(container, prevFragment, animatorOut);
		} else {
			replaceFragment(container, prevFragment, destFragment);
		}
	}

	public void replaceFragmentWithNoAnimation(final CustomFrameLayout container, final BaseFragment prevFragment,
			final BaseFragment destFragment) {
		removeAddRunnable();
		if (prevFragment == null) {
			addFragmentWithAnimation(container, destFragment, null);
			return;
		}
		replaceFragment(container, prevFragment, destFragment);
	}

	private class AddFragmentRunnable implements Runnable {
		private CustomFrameLayout mContainer;
		private BaseFragment mFragment;

		AddFragmentRunnable(CustomFrameLayout container, BaseFragment fragment) {
			this.mContainer = container;
			this.mFragment = fragment;
		}

		@Override
		public void run() {
			addFragment(mContainer, mFragment);
		}
	}

	public void externalRemoveFragment(CustomFrameLayout container, BaseFragment fragment) {
		removeFragmentInLayout(container, fragment);
		container.requestLayout();
		container.invalidate();
	}

	private class AddFragmentWithAnimatorRunnable implements Runnable {
		private CustomFrameLayout mContainer;
		private BaseFragment mFragment;
		private Animator mAnimator;

		AddFragmentWithAnimatorRunnable(CustomFrameLayout container, BaseFragment fragment, Animator animator) {
			this.mContainer = container;
			this.mFragment = fragment;
			this.mAnimator = animator;
		}

		@Override
		public void run() {
			addFragmentWithAnimation(mContainer, mFragment, mAnimator);
		}
	}

	public boolean hasFragment(BaseFragment fragment) {
		if (mAddedFragmentList.size() == 0) {
			return false;
		}
		return (mAddedFragmentList.indexOfValue(fragment) != -1);
	}

	private void removeAddRunnable() {
		mHandler.removeCallbacks(mAddFragmentRunnable);
		mHandler.removeCallbacks(mAddFragmentWithAnimatorRunnable);
	}

	public void onResume() {
		if (mAddedFragmentList.size() == 0) {
			return;
		}
		for (int i = 0; i < mAddedFragmentList.size(); i++) {
			BaseFragment fragment = mAddedFragmentList.valueAt(i);
			if (!fragment.isResumed()) {
				fragment.onResume();
			}
		}
	}

	public void onPause() {
		if (mAddedFragmentList.size() == 0) {
			return;
		}
		for (int i = 0; i < mAddedFragmentList.size(); i++) {
			BaseFragment fragment = mAddedFragmentList.valueAt(i);
			if (fragment.isResumed()) {
				fragment.onPause();
			}
		}
	}
}
