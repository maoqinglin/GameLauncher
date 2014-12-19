package com.ireadygo.app.gamelauncher.ui.base;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.ui.GameLauncherActivity;
import com.ireadygo.app.gamelauncher.ui.widget.WidgetController;

public abstract class BaseFragment extends KeyEventFragment {
	private GameLauncherActivity mActivity;
	private LayoutInflater mInflater;
	private View mRootView;
	private String mTag;
	private int mInitX;
	private int mInitY;
	private static Animator mAnimatorIn;
	private static Animator mAnimatorOut;
	private boolean mIsResumed = false;

	public BaseFragment() {

	}

	public BaseFragment(Activity activity) {
		setActivity(activity);
	}

	public void setActivity(Activity activity) {
		this.mActivity = (GameLauncherActivity) activity;
		mInflater = LayoutInflater.from(activity);
	}

	public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		 Log.d("liu.js", "onCreateView--" + getClass());
		return createView(inflater, container, savedInstanceState);
	}

	public void onDestoryView() {
		mRootView = null;
		 Log.d("liu.js", "onDestoryView--" + getClass());
	}

	public void onResume(){
		this.mIsResumed = true;
		Log.d("liu.js", "onResume--" + this);
	}
	
	public void onPause(){
		this.mIsResumed = false;
		Log.d("liu.js", "onPause--" + this);
	}
	
	public abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	public View getRootView() {
		if (mRootView == null) {
			mRootView = onCreateView(mInflater, mActivity.getContainer(), null);
			setupCoordinate();
			if (mRootView.getId() == View.NO_ID) {
				mRootView.setId(getClass().hashCode());
			}
		}
		return mRootView;
	}

	public GameLauncherActivity getRootActivity() {
		return mActivity;
	}

	protected Animator inAnimator(AnimatorListener listener) {
		return null;
	}

	protected Animator outAnimator(AnimatorListener listener) {
		return null;
	}

	public final Animator createInAnimator(AnimatorListener listener){
		if(mAnimatorOut != null && mAnimatorOut.isStarted()){
			mAnimatorOut.cancel();
		}
		mAnimatorIn = inAnimator(listener);
		return mAnimatorIn;
	}
	
	public final Animator createOutAnimator(AnimatorListener listener){
		if(mAnimatorIn != null && mAnimatorIn.isStarted()){
			mAnimatorIn.cancel();
		}
		mAnimatorOut = outAnimator(listener);
		return mAnimatorOut;
	}
	
	protected Point getLocationInScreen() {
		int[] location = new int[2];
		getRootView().getLocationOnScreen(location);
		return new Point(location[0], location[1]);
	}

	protected int getStatusBarHeight() {
		return WidgetController.getStatusBarHeight(getRootActivity());
	}

	protected Resources getResources() {
		return mActivity.getResources();
	}

	public String getTag() {
		return mTag;
	}

	public void setTag(String tag) {
		this.mTag = tag;
	}

	public void initCoordinateParams(int initX, int initY) {
		this.mInitX = initX;
		this.mInitY = initY;
	}

	public void setupCoordinate() {
		getRootView().setTranslationX(mInitX);
		getRootView().setTranslationY(mInitY);
	}

	public int getInitX() {
		return mInitX;
	}

	public int getInitY() {
		return mInitY;
	}

	public int getOutAnimatorDuration() {
		return 0;
	}

	public int getInAnimatorDuration() {
		return 0;
	}
	
	public boolean isResumed(){
		return mIsResumed;
	}
}
