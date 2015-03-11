package com.ireadygo.app.gamelauncher.ui.store;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.OnChildFocusChangeListener;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.base.KeyEventFragment;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;
import com.ireadygo.app.gamelauncher.ui.store.recommend.RecommendLayout;
import com.ireadygo.app.gamelauncher.ui.widget.CustomFrameLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class StoreDetailActivity extends BaseActivity {
	// public static final String EXTRA_MENU_TYPE = "MENU_TYPE";
	private StoreOptionsLayout mOptionsLayout;
	private CustomFrameLayout mContentLayout;
	private SparseArray<StoreBaseContentLayout> mContentChildArray = new SparseArray<StoreBaseContentLayout>();
	private StoreBaseContentLayout mCurrentContentChild;
	private OperationTipsLayout mTipsLayout;
	private int mLastKeyCode = -1;
	private long mLastKeyTime;

	// private Animator mLayoutEnterAnimator, mLayoutExitAnimator;

	// public StoreDetailActivity(Activity activity) {
	// super(activity);
	// }

	// @Override
	// public View createView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// View view = inflater.inflate(R.layout.store_detail, container, false);
	// initView(view);
	// return view;
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_detail);
		initView();
		menuRequestFocusByIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		menuRequestFocusByIntent(getIntent());
	}

	private void menuRequestFocusByIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		Destination destination = (Destination) intent.getSerializableExtra(Anchor.EXTRA_DESTINATION);
		if (destination != null) {
			mOptionsLayout.requestOptionsFocusByTag(destination);
		}
	}

	protected void initView() {
		mOptionsLayout = (StoreOptionsLayout) findViewById(R.id.storeOptionsLayout);
		// mOptionsLayout.setFragment(this);
		mContentLayout = (CustomFrameLayout) findViewById(R.id.storeContentLayout);

		// replaceContentLayout(LayoutTag.RECOMMEND);
		mOptionsLayout.setOnChildFocusChangeListener(new OnChildFocusChangeListener() {

			@Override
			public void onChildFocusChange(int index, View v, boolean hasFocus) {
				if (hasFocus) {
					switch (v.getId()) {
					case R.id.storeOptionsSearch:
						replaceContentLayout(LayoutTag.SEARCH);
						break;
					case R.id.storeOptionsRecommend:
						replaceContentLayout(LayoutTag.RECOMMEND);
						break;
					case R.id.storeOptionsCollection:
						replaceContentLayout(LayoutTag.COLLECTION);
						break;
					case R.id.storeOptionsCategory:
						replaceContentLayout(LayoutTag.CATEGORY);
						break;
					case R.id.storeOptionsGameManage:
						replaceContentLayout(LayoutTag.GAME_MANAGE);
						break;
					case R.id.storeOptionsSettings:
						replaceContentLayout(LayoutTag.SETTINGS);
						break;
					default:
						break;
					}
				}
			}
		});
		mTipsLayout = (OperationTipsLayout) findViewById(R.id.tipsLayout);
		// mTipsLayout.setTipsGone(OperationTipsLayout.FLAG_TIPS_SUN,OperationTipsLayout.FLAG_TIPs_WATER);
	}

	public StoreOptionsLayout getOptionsLayout() {
		return mOptionsLayout;
	}

	private void replaceContentLayout(int layoutTag) {
		setTipsByLayoutTag(layoutTag);
		if (mCurrentContentChild != null) {
			if (mCurrentContentChild.getLayoutTag() == layoutTag) {
				return;
			}
			// if (mLayoutEnterAnimator != null &&
			// mLayoutEnterAnimator.isStarted()) {
			// mLayoutEnterAnimator.cancel();
			// }
			// if (mLayoutExitAnimator != null &&
			// mLayoutExitAnimator.isStarted()) {
			// mLayoutExitAnimator.cancel();
			// }
			mContentLayout.removeViewInLayout(mCurrentContentChild);
		}
		StoreBaseContentLayout targetLayout = mContentChildArray.get(layoutTag);
		if (targetLayout == null) {
			targetLayout = createContentLayoutByTag(layoutTag);
		}
		mContentLayout.addViewInLayout(targetLayout);
		// TODO
		// mContentChildArray.put(layoutTag, targetLayout);
		mCurrentContentChild = targetLayout;
		// if (targetLayout instanceof StoreGamesLayout) {
		// ((StoreGamesLayout) targetLayout).refreshDataBySwitchLayout();
		// }
		mContentLayout.requestLayout();
		mContentLayout.invalidate();
	}

	private AnimatorListener mExitAnimatorListener = new AnimatorListenerAdapter() {
		public void onAnimationEnd(Animator animation) {
			mContentLayout.removeView((View) ((ObjectAnimator) animation).getTarget());
		};
	};

	private StoreBaseContentLayout createContentLayoutByTag(int layoutTag) {
		StoreBaseContentLayout contentLayout = null;
		switch (layoutTag) {
		case LayoutTag.SEARCH:
			// contentLayout = new SearchLayout(this, layoutTag, this);
			break;
		case LayoutTag.RECOMMEND:
			contentLayout = new RecommendLayout(this, layoutTag, this);
			break;
		case LayoutTag.COLLECTION:
			// contentLayout = new CollectionLayout(this, layoutTag, this);
			break;
		case LayoutTag.CATEGORY:
			// contentLayout = new CategoryLayout(this, layoutTag, this);
			break;
		case LayoutTag.GAME_MANAGE:
			// contentLayout = new StoreGamesLayout(this, layoutTag, this);
			break;
		}
		return contentLayout;
	}

	private class LayoutTag {
		static final int SEARCH = 0;
		static final int RECOMMEND = 1;
		static final int COLLECTION = 2;
		static final int CATEGORY = 3;
		static final int GAME_MANAGE = 4;
		static final int SETTINGS = 5;
	}

	// @Override
	// public void onDestroyView() {
	// mCurrentContentChild = null;
	// mContentChildArray.clear();
	// super.onDestroyView();
	// }

	@Override
	public void finish() {
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
		super.finish();
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
		if (mOptionsLayout != null && mOptionsLayout.hasFocus()) {
			if (SnailKeyCode.MOON_KEY == keyCode || SnailKeyCode.BACK_KEY == keyCode || SnailKeyCode.UP_KEY == keyCode) {
				finish();
				return true;
			}
			return mOptionsLayout.onKeyDown(keyCode, event);
		} else if (mCurrentContentChild != null && mCurrentContentChild.hasFocus()
				&& mCurrentContentChild.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setTipsByLayoutTag(int layoutTag) {
		if (LayoutTag.GAME_MANAGE == layoutTag) {
			mTipsLayout.setTipsVisible(TipFlag.FLAG_ALL);
		} else {
			mTipsLayout.setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		}
	}

	// @Override
	// protected boolean isCurrentFocus() {
	// if (mOptionsLayout.hasFocus()) {
	// return true;
	// }
	// if (mCurrentContentChild == null) {
	// return false;
	// }
	// return mCurrentContentChild.hasFocus();
	// }
	//
	// @Override
	// public void onDestoryView() {
	// super.onDestoryView();
	// mContentChildArray.clear();
	// }
}
