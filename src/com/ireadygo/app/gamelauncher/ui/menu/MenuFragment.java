package com.ireadygo.app.gamelauncher.ui.menu;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.MyAppFragment;
import com.ireadygo.app.gamelauncher.ui.MyGameFragment;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.base.BaseFragment;
import com.ireadygo.app.gamelauncher.ui.settings.SettingsFragment;
import com.ireadygo.app.gamelauncher.ui.store.StoreFragment;

public class MenuFragment extends BaseFragment {
	private static final int WHAT_CONTENT_OBTAIN_FOCUS = 1;

	private MenuItem mStoreMenu;
	private MenuItem mGameMenu;
	private MenuItem mAppMenu;
	private MenuItem mSettingsMenu;

	private MenuItem mPrevFocusItem;
	private MenuItem mCurrentFocusItem;
	private MenuItem mCurrentSelectedItem;

	private List<MenuItem> mMenuItemList = new ArrayList<MenuItem>();

	private Status mStatus = Status.INIT;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_CONTENT_OBTAIN_FOCUS:// 内容页面得到焦点
				if (mStatus.isFocused()) {// 如果之前是焦点状态
					MenuItem menuItem = (MenuItem) msg.obj;
					toSelectedStatus(menuItem);
					menuItem.getFragment().onObtainFocus(null);
				}
				break;

			default:
				break;
			}
		}

	};

	public MenuFragment() {

	}

	public MenuFragment(Activity activity) {
		super(activity);
		initCoordinateParams(Config.Menu.INIT_X, Config.Menu.INIT_Y);
	}

	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	// Drawable highlightD =
	// activity.getResources().getDrawable(R.drawable.menu_my_highlight);
	// mHighlightWidth = highlightD.getIntrinsicWidth();
	// mHighlightHeight = highlightD.getIntrinsicHeight();
	// }

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int index = 0;
		View view = inflater.inflate(R.layout.menu, container, false);
		mStoreMenu = (MenuItem) view.findViewById(R.id.menu_store);
		mGameMenu = (MenuItem) view.findViewById(R.id.menu_game);
		mAppMenu = (MenuItem) view.findViewById(R.id.menu_app);
		mSettingsMenu = (MenuItem) view.findViewById(R.id.menu_settings);

		initItemView(mStoreMenu, new StoreFragment(getRootActivity(), this), index++);
		initItemView(mGameMenu, new MyGameFragment(getRootActivity(), this), index++);
		initItemView(mAppMenu, new MyAppFragment(getRootActivity(), this), index++);
		initItemView(mSettingsMenu, new SettingsFragment(getRootActivity(), this), index++);

		return view;
	}

	private void initItemView(MenuItem menuItem, BaseContentFragment fragment, int index) {
		menuItem.setOnFocusChangeListener(mItemFocusChangeListener);
		menuItem.setFragment(fragment);
		menuItem.setIndex(index);
		mMenuItemList.add(menuItem);
	}

	public Status getState() {
		return mStatus;
	}

	private OnFocusChangeListener mItemFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			final MenuItem menuItem = (MenuItem) v;
			if (hasFocus) {// 得到焦点
				if (mHandler.hasMessages(WHAT_CONTENT_OBTAIN_FOCUS)) {
					mHandler.removeMessages(WHAT_CONTENT_OBTAIN_FOCUS);
				}
				if (mStatus.isFocused()) {// 当前焦点位于菜单上
					switchFocusedItem(mCurrentFocusItem, menuItem);
				} else if (mStatus.isSelected()) {// 焦点不在菜单上，但菜单有选中项
					if(mCurrentFocusItem != menuItem){
						switchFocusedItem(mCurrentFocusItem, menuItem);
					}
					toFocusedStatus(menuItem);
					mCurrentSelectedItem.getFragment().onLoseFocus(null);
				} else if (mStatus.isInit()) {// 菜单处于初始状态
					getRootActivity().addFragment(menuItem.getFragment());
					toFocusedStatus(menuItem);
				}
			} else {// 丢失焦点
				Message msg = Message.obtain(mHandler, WHAT_CONTENT_OBTAIN_FOCUS);
				msg.obj = menuItem;
				msg.sendToTarget();
			}
		}
	};

	private void switchFragment(BaseContentFragment currFragment, final BaseContentFragment targetFragment) {
		getRootActivity().replaceFragmentWithAnimation(currFragment, targetFragment);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode != BACK_KEY) {
			if (mStatus.isInit()) {
				if (mCurrentFocusItem != null) {
					mCurrentFocusItem.requestFocus();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onBackKey() {
		if (mStatus.isFocused()) {
			getRootActivity().removeFragment(mCurrentFocusItem.getFragment());
			mCurrentFocusItem.clearFocus();
			if (mHandler.hasMessages(WHAT_CONTENT_OBTAIN_FOCUS)) {
				mHandler.removeMessages(WHAT_CONTENT_OBTAIN_FOCUS);
			}
			toInitStatus();
		}
		return true;
	}

	private void switchFocusedItem(MenuItem prevFocusedItem, MenuItem currFocusedItem) {
		mPrevFocusItem = prevFocusedItem;
		mCurrentFocusItem = currFocusedItem;
		animatorSwitchFocusView(prevFocusedItem, currFocusedItem);
		switchFragment(prevFocusedItem.getFragment(), currFocusedItem.getFragment());
	}

	// 菜单回到初始状态
	private void toInitStatus() {
		mStatus = Status.INIT;
		mPrevFocusItem = mCurrentFocusItem;
		mCurrentFocusItem = null;
		mCurrentSelectedItem = null;
		getRootActivity().hideHighlightView();
		animatorToInit();
	}

	// 菜单回到选中状态
	private void toSelectedStatus(MenuItem currSelectedItem) {
		mStatus = Status.SELECTED;
		mCurrentSelectedItem = currSelectedItem;
		getRootActivity().hideHighlightView();
		animatorToSelected(null);

	}

	// 菜单回到焦点状态
	private void toFocusedStatus(MenuItem focusedItem) {
		mStatus = Status.FOCUSED;
		mPrevFocusItem = mCurrentFocusItem;
		mCurrentFocusItem = focusedItem;
		animatorToFocused(new ToFocusedAnimatorListener(focusedItem));
	}

	private void animatorToInit() {
		doAnimator(null, Config.Menu.INIT_X, Config.Menu.INIT_Y);
		for (int i = 0; i < mMenuItemList.size(); i++) {
			MenuItem item = mMenuItemList.get(i);
			item.toInit();
		}
	}

	public void animatorToFocused(AnimatorListener listener) {
		int x = Config.Menu.FOCUSED_X1;
		if (mCurrentFocusItem != null && mCurrentFocusItem.getIndex() != 0) {
			x = Config.Menu.FOCUSED_X2;
		}
		doAnimator(listener, x, Config.Menu.FOCUSED_Y);
		for (int i = 0; i < mMenuItemList.size(); i++) {
			MenuItem item = mMenuItemList.get(i);
			if (item == mCurrentFocusItem) {
				item.toFocusedOnMenuFocused(listener);
			} else {
				item.toNoFocusedOnMenuFocused();
			}
		}
	}

	public void animatorToSelected(AnimatorListener listener) {
		doAnimator(null, Config.Menu.SELECTED_X, Config.Menu.SELECTED_Y);
		for (int i = 0; i < mMenuItemList.size(); i++) {
			MenuItem item = mMenuItemList.get(i);
			if (item == mCurrentFocusItem) {
				item.toSelectedOnMenuSelected();
			} else {
				item.toNoSelectedOnMenuSelected();
			}
		}
	}

	private void doAnimator(AnimatorListener listener, int destX, int destY) {
		AnimatorSet animatorSet = new AnimatorSet();
		Animator animTranslateY = ObjectAnimator.ofFloat(getRootView(), View.TRANSLATION_Y, destY);
		Animator animTranslateX = ObjectAnimator.ofFloat(getRootView(), View.TRANSLATION_X, destX);
		animatorSet.setDuration(200);
		if (listener != null) {
			animatorSet.addListener(listener);
		}
		animatorSet.play(animTranslateX).with(animTranslateY);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.start();
	}

	private void animatorSwitchFocusView(MenuItem mPrevFocus, MenuItem mCurrFocus) {
		if (mPrevFocus != null) {
			if (mPrevFocus.getIndex() == 0) {
				doAnimator(null, Config.Menu.FOCUSED_X2, Config.Menu.FOCUSED_Y);
			}
			mPrevFocus.toNoFocusedOnMenuFocused();
		}
		if (mCurrFocus != null) {
			if (mCurrFocus.getIndex() == 0) {
				doAnimator(null, Config.Menu.FOCUSED_X1, Config.Menu.FOCUSED_Y);
			}
			mCurrFocus.toFocusedOnMenuFocused(new ToFocusedAnimatorListener(mCurrFocus));
		}
	}

	private class ToFocusedAnimatorListener extends AnimatorListenerAdapter {
		private MenuItem mFocusItem;

		public ToFocusedAnimatorListener(MenuItem focusItem) {
			this.mFocusItem = focusItem;
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			int[] location = new int[2];
			mFocusItem.getLocationInWindow(location);
			int x = (int) location[0] - Config.Highlight.DISTANCE_X;
			int y = (int) location[1] - Config.Highlight.DISTANCE_Y - getStatusBarHeight();
			getRootActivity().updateHighlightView(mFocusItem.getHighlightDrawable(), x, y);
		}

		@Override
		public void onAnimationStart(Animator animation) {
			getRootActivity().hideHighlightView();
			// int[] location = new int[2];
			// mFocusItem.getLocationInWindow(location);
			// int x = 0;
			// int y = 0;
			// if (mFocusItem.getIndex() == 0) {
			// x = -100;
			// y = -125;
			// } else {
			// x = mFocusItem.getWidth() * mFocusItem.getIndex() - 40 - 100;
			// y = -125;
			// }
			// getBaseActivity().updateHighlightView(mFocusItem.getHighlightDrawable(),
			// x, y);
		}
	}

	public static enum Status {
		INIT, // 初始状态
		FOCUSED, // 焦点状态
		SELECTED;// 选中状态

		public boolean isFocused() {
			return this == FOCUSED;
		}

		public boolean isSelected() {
			return this == SELECTED;
		}

		public boolean isInit() {
			return this == INIT;
		}
	}

	@Override
	public boolean onSunKey() {
		return true;
	}

	@Override
	public boolean onWaterKey() {
		return true;
	}

	@Override
	public boolean onLeftKey() {
		requestFocusToLeft();
		return true;
	}

	@Override
	public boolean onRightKey() {
		requestFocusToRight();
		return true;
	}


	@Override
	protected boolean isCurrentFocus() {
		return mStatus.isFocused();
	}

	public MenuItem getCurrentItem() {
		if (mStatus.isFocused()) {
			return mCurrentFocusItem;
		} else if (mStatus.isSelected()) {
			return mCurrentSelectedItem;
		}
		return null;
	}

	public void requestFocusToRight() {
		int curPos = mMenuItemList.indexOf(getCurrentItem());
		if (curPos < mMenuItemList.size() - 1) {
			curPos++;
			mMenuItemList.get(curPos).requestFocus();
		} else if (curPos == mMenuItemList.size() - 1) {
			curPos = 0;
			mMenuItemList.get(curPos).requestFocus();
		}
	}

	public void requestFocusToLeft() {
		int curPos = mMenuItemList.indexOf(getCurrentItem());
		if (curPos > 0) {
			curPos--;
			mMenuItemList.get(curPos).requestFocus();
		} else if (curPos == 0) {
			curPos = mMenuItemList.size() - 1;
			mMenuItemList.get(curPos).requestFocus();
		}
	}
	
}
