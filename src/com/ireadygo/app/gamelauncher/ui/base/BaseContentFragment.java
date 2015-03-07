package com.ireadygo.app.gamelauncher.ui.base;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity.ScrollListenerByIndicator;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.menu.HomeMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.PagingIndicator;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.ireadygo.app.gamelauncher.utils.Utils;

public abstract class BaseContentFragment extends BaseFragment {
	private OperationTipsLayout mOperationTipsLayout;
	private BaseMenuFragment mMenuFragment;
	private int mFocusedX = Config.Content.FOCUSED_X;
	private int mFocusedY = Config.Content.FOCUSED_Y;
	private Dialog mLoadingProgress;
	private GameInfoHub mGameInfoHub;

	// public BaseContentFragment(BaseMenuFragment menuFragment) {
	// this.mMenuFragment = menuFragment;
	// initCoordinateParams(Config.Content.INIT_X, Config.Content.INIT_Y);
	// }

	public BaseContentFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity);
		this.mMenuFragment = menuFragment;
		mGameInfoHub = GameInfoHub.instance(activity);
		initCoordinateParams(Config.Content.INIT_X, Config.Content.INIT_Y);
	}

	protected void initView(View view) {
		mOperationTipsLayout = (OperationTipsLayout) view.findViewById(R.id.tipsLayout);
	}

	protected OperationTipsLayout getOperationTipsLayout() {
		return mOperationTipsLayout;
	}

	@Override
	public Animator inAnimator(AnimatorListener listener) {
		// View rootView = getRootView();
		// if (rootView == null) {
		// return null;
		// }
		// AnimatorSet animatorSet = new AnimatorSet();
		// Animator animatorX = ObjectAnimator.ofFloat(rootView,
		// View.TRANSLATION_X, getInitX());
		// Animator animatorY = ObjectAnimator.ofFloat(getRootView(),
		// View.TRANSLATION_Y, Config.WINDOW_HEIGHT,
		// Config.WINDOW_HEIGHT, getInitY());
		// animatorSet.setDuration(Config.Animator.DURATION_SHORT);
		// if (listener != null) {
		// animatorSet.addListener(listener);
		// }
		// animatorSet.playTogether(animatorX, animatorY);
		// animatorSet.start();
		return null;
	}

	@Override
	public Animator outAnimator(AnimatorListener listener) {
		View rootView = getRootView();
		if (rootView == null) {
			return null;
		}
		Animator animatorY = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_Y, Config.WINDOW_HEIGHT);
		animatorY.setDuration(Config.Animator.DURATION_SHORT);
		if (listener != null) {
			animatorY.addListener(listener);
		}
		animatorY.start();
		return animatorY;
	}

	protected void setFocusedX(int focusedX) {
		this.mFocusedX = focusedX;
	}

	public int getFocusedX() {
		return mFocusedX;
	}

	protected void setFocusedY(int focusedY) {
		this.mFocusedY = focusedY;
	}

	public int getFocusedY() {
		return mFocusedY;
	}

	public void onObtainFocus(AnimatorListener listener) {
		getRootActivity().translateToLeft();
	}

	public void onLoseFocus(AnimatorListener listener) {
		getRootActivity().translateToRight();
	}

	private void obtainFocusAnimator(AnimatorListener listener) {
		createTranslateAnimator(listener, mFocusedX, mFocusedY).start();
	}

	private void loseFocusAnimator(AnimatorListener listener) {
		createTranslateAnimator(listener, getInitX(), getInitY()).start();
	}

	private Animator createTranslateAnimator(AnimatorListener listener, int destX, int destY) {
		View view = getRootView();
		AnimatorSet animatorSet = new AnimatorSet();
		Animator animTranslateY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, destY);
		Animator animTranslateX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, destX);
		animatorSet.setDuration(Config.Animator.DURATION_SHORT);
		if (listener != null) {
			animatorSet.addListener(listener);
		}
		animatorSet.play(animTranslateX).with(animTranslateY);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		return animatorSet;
	}

	protected BaseMenuFragment getMenu() {
		return mMenuFragment;
	}

	protected void initTranslationXY(int initX, int initY, int focusedX, int focusedY) {
		initCoordinateParams(initX, initY);
		this.mFocusedX = focusedX;
		this.mFocusedY = focusedY;
	}

	public void returnFocus() {
		if (isCurrentFocus()) {
			getMenu().getCurrentItem().requestFocus();
		}
	}

	public boolean onBackKey() {
		returnFocus();
		return true;
	};

	@Override
	public boolean onWaterKey() {
		return true;
	};

	@Override
	public boolean onMountKey() {
		return true;
	}

	@Override
	public int getOutAnimatorDuration() {
		return Config.Animator.DURATION_SHORT;
	}

	protected void showLoadingProgress() {
		if (mLoadingProgress == null) {
			mLoadingProgress = Utils.createLoadingDialog(getRootActivity());
			mLoadingProgress.setCancelable(false);
		}
		if (!mLoadingProgress.isShowing()) {
			mLoadingProgress.show();
		}
	}

	protected void dimissLoadingProgress() {
		if (mLoadingProgress != null && mLoadingProgress.isShowing()) {
			mLoadingProgress.dismiss();
		}
	}

	protected GameInfoHub getGameInfoHub() {
		return mGameInfoHub;
	}

	protected BaseMenuActivity getMenuActivity() {
		return (BaseMenuActivity) getRootActivity();
	}

	protected void bindPagingIndicator(HMultiListView multiListView) {
		PagingIndicator indicator = getMenuActivity().getPagingIndicator();
		multiListView.setOnScrollListener(new ScrollListenerByIndicator(indicator));
		HListView upListView = multiListView.getHListViews().get(0);
		indicator.bind(upListView);
	}

	protected void bindPagingIndicator(HListView hListView) {
		PagingIndicator indicator = getMenuActivity().getPagingIndicator();
		hListView.setOnScrollListener(new ScrollListenerByIndicator(indicator));
		indicator.bind(hListView);
	}
}
