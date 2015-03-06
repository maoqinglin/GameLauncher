package com.ireadygo.app.gamelauncher.ui.base;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.CustomFragmentManager;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView.OnScrollListener;
import com.ireadygo.app.gamelauncher.ui.widget.CustomFrameLayout;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.PagingIndicator;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;

public abstract class BaseMenuActivity extends BaseActivity {
	public static final String EXTRA_FOCUS_POSITION = "EXTRA_FOCUS_POSITION";
	private boolean mShouldTranslate = false;
	private int mLastKeyCode = -1;
	private long mLastKeyTime;

	private CustomFragmentManager mFragmentManager;

	private CustomFrameLayout mMainLayout;
	private View mFocusView;
	private BaseMenuFragment mMenuFragment;

	private OperationTipsLayout mTipsLayout;

	private ObjectAnimator mLeftTranslateAnimator;
	private ObjectAnimator mRightTranslateAnimator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFragmentManager = new CustomFragmentManager(this);
		setContentView(R.layout.main);
		mMainLayout = (CustomFrameLayout) findViewById(R.id.main_layout);
		mFocusView = findViewById(R.id.focusView);
		mTipsLayout = (OperationTipsLayout) findViewById(R.id.tips_layout);
		mTipsLayout.setAllVisible(View.VISIBLE);
		mMenuFragment = createMenuFragment();
		addFragment(mMenuFragment);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 上报应用置前台的时间
		getCustomFragmentManager().onResume();
		StaticsUtils.onResume();
	}

	@Override
	protected void onPause() {
		getCustomFragmentManager().onPause();
		super.onPause();
	}

	public OperationTipsLayout getTipsLayout() {
		return mTipsLayout;
	}

	public PagingIndicator getPagingIndicator() {
		return mTipsLayout.getPagingIndicator();
	}

	public void removeFragment(BaseFragment fragment) {
		getCustomFragmentManager().removeFragment(mMainLayout, fragment);
	}

	public void addFragment(BaseFragment fragment) {
		getCustomFragmentManager().addFragment(mMainLayout, fragment);
	}

	public void addFragmentWithAnimation(BaseFragment fragment, Animator animatorIn) {
		getCustomFragmentManager().addFragmentWithAnimation(mMainLayout, fragment, animatorIn);
	}

	public void removeFragmentWithAnimation(BaseFragment fragment, Animator animatorOut) {
		getCustomFragmentManager().removeFragmentWithAnimation(mMainLayout, fragment, animatorOut);
	}

	public void replaceFragmentWithAnimation(final BaseFragment prevFragment, final BaseFragment destFragment) {
		getCustomFragmentManager().replaceFragmentWithAnimation(mMainLayout, prevFragment, destFragment);
	}

	public CustomFragmentManager getCustomFragmentManager() {
		return mFragmentManager;
	}

	public ViewGroup getContainer() {
		return mMainLayout;
	}

	public abstract BaseMenuFragment createMenuFragment();

	public void translateToLeft() {
		if (!mShouldTranslate) {
			return;
		}
		if (mRightTranslateAnimator != null && mRightTranslateAnimator.isRunning()) {
			mRightTranslateAnimator.cancel();
		}
		if (mLeftTranslateAnimator == null) {
			mLeftTranslateAnimator = ObjectAnimator.ofFloat(mMainLayout, View.TRANSLATION_X, -220);
			mLeftTranslateAnimator.setDuration(200);
		}
		mLeftTranslateAnimator.start();
	}

	public void translateToRight() {
		if (!mShouldTranslate) {
			return;
		}
		if (mLeftTranslateAnimator != null && mLeftTranslateAnimator.isRunning()) {
			mLeftTranslateAnimator.cancel();
		}
		if (mRightTranslateAnimator == null) {
			mRightTranslateAnimator = ObjectAnimator.ofFloat(mMainLayout, View.TRANSLATION_X, 0);
			mRightTranslateAnimator.setDuration(200);
		}
		mRightTranslateAnimator.start();
	}

	public View getFocusView() {
		return mFocusView;
	}

	public BaseMenuFragment getMenuFragment() {
		return mMenuFragment;
	}

	public void setShouldTranslate(boolean shouldTranslate) {
		this.mShouldTranslate = shouldTranslate;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEventFragment.ALLOW_KEY_DELAY && mLastKeyCode != -1
				&& (keyCode == SnailKeyCode.LEFT_KEY || keyCode == SnailKeyCode.RIGHT_KEY)) {
			if (System.currentTimeMillis() - mLastKeyTime <= KeyEventFragment.KEY_DELAY) {
				return true;
			}
		}
		mLastKeyCode = keyCode;
		mLastKeyTime = System.currentTimeMillis();
		switch (keyCode) {
		case SnailKeyCode.MOUNT_KEY:
		case SnailKeyCode.WATER_KEY:
		case SnailKeyCode.SUN_KEY:
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case SnailKeyCode.MOON_KEY:
		case SnailKeyCode.BACK_KEY:
		case SnailKeyCode.L2_KEY:
		case SnailKeyCode.R2_KEY:
		case SnailKeyCode.LEFT_KEY:
		case SnailKeyCode.RIGHT_KEY:
		case SnailKeyCode.UP_KEY:
		case SnailKeyCode.DOWN_KEY:
			if (mFragmentManager.onKeyDown(keyCode, event)) {
				return true;
			}
			break;
		case SnailKeyCode.L1_KEY:
			mMenuFragment.requestFocusToUp();
			return true;
		case SnailKeyCode.R1_KEY:
			mMenuFragment.requestFocusToDown();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static class ScrollListenerByIndicator implements OnScrollListener {
		private PagingIndicator mPagingIndicator;

		public ScrollListenerByIndicator(PagingIndicator mPagingIndicator) {
			this.mPagingIndicator = mPagingIndicator;
		}

		@Override
		public void onScrollStateChanged(AbsHListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (mPagingIndicator != null) {
				mPagingIndicator.onScroll((HListView) view);
			}
		}

	}
}
