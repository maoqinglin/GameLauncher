package com.ireadygo.app.gamelauncher.ui.menu;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.OnChildFocusChangeListener;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.base.BaseFragment;
import com.ireadygo.app.gamelauncher.ui.widget.CustomFrameLayout;

public abstract class BaseMenuFragment extends BaseFragment {
	private static final int WHAT_CONTENT_OBTAIN_FOCUS = 1;

	private MenuItem mPrevFocusItem;
	private MenuItem mCurrentFocusItem;
	private MenuItem mCurrentSelectedItem;
	private OnChildFocusChangeListener mOnChildFocusChangeListener;

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
					menuItem.getContentFragment().onObtainFocus(null);
				}
				break;

			default:
				break;
			}
		}

	};

	public BaseMenuFragment() {

	}

	public BaseMenuFragment(Activity activity) {
		super(activity);
	}

	protected void addMenuItem(MenuItem menuItem, BaseContentFragment fragment) {
		menuItem.setOnFocusChangeListener(mItemFocusChangeListener);
		menuItem.setContentFragment(fragment);
		menuItem.setIndex(mMenuItemList.size());
		mMenuItemList.add(menuItem);
	}

	protected void updateMenuItem(int menuIndex, BaseContentFragment newFragment) {
		if (menuIndex > -1 && menuIndex < mMenuItemList.size()) {
			MenuItem menuItem = mMenuItemList.get(menuIndex);
			if (newFragment != null) {
				if (mMenuItemList.indexOf(getCurrentItem()) == menuIndex) {
					switchFragmentWithNoAnimation(menuItem.getContentFragment(), newFragment);
				}
				menuItem.setContentFragment(newFragment);
			}
		}
	}

	public Status getState() {
		return mStatus;
	}

	protected OnFocusChangeListener mItemFocusChangeListener = new OnFocusChangeListener() {

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
					if (mCurrentFocusItem != menuItem) {
						switchFocusedItem(mCurrentFocusItem, menuItem);
					}
					toFocusedStatus(menuItem);
					mCurrentSelectedItem.getContentFragment().onLoseFocus(null);
				} else if (mStatus.isInit()) {// 菜单处于初始状态
					getRootActivity().addFragment(menuItem.getContentFragment());
					toFocusedStatus(menuItem);
				}
				getRootActivity().updateFocusViewNextFocusId(v.getId());
			} else {// 丢失焦点
				Message msg = Message.obtain(mHandler, WHAT_CONTENT_OBTAIN_FOCUS);
				msg.obj = menuItem;
				msg.sendToTarget();
			}
			if (mOnChildFocusChangeListener != null) {
				int index = mMenuItemList.indexOf(v);
				mOnChildFocusChangeListener.onChildFocusChange(index, v, hasFocus);
			}
		}
	};

	public void setOnChildFocusChangeListener(OnChildFocusChangeListener listener) {
		this.mOnChildFocusChangeListener = listener;
	}

	private void switchFragmentWithAnimation(BaseContentFragment currFragment, final BaseContentFragment targetFragment) {
		getRootActivity().replaceFragmentWithAnimation(currFragment, targetFragment);
	}

	private void switchFragmentWithNoAnimation(BaseContentFragment currFragment,
			final BaseContentFragment targetFragment) {
		getRootActivity().replaceFragmentWithNoAnimation(currFragment, targetFragment);
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

	// @Override
	// public boolean onBackKey() {
	// if (mStatus.isFocused()) {
	// getRootActivity().removeFragment(mCurrentFocusItem.getContentFragment());
	// mCurrentFocusItem.clearFocus();
	// if (mHandler.hasMessages(WHAT_CONTENT_OBTAIN_FOCUS)) {
	// mHandler.removeMessages(WHAT_CONTENT_OBTAIN_FOCUS);
	// }
	// toInitStatus();
	// }
	// return true;
	// }

	private void switchFocusedItem(MenuItem prevFocusedItem, MenuItem currFocusedItem) {
		mPrevFocusItem = prevFocusedItem;
		mCurrentFocusItem = currFocusedItem;
		animatorSwitchFocusView(prevFocusedItem, currFocusedItem);
		switchFragmentWithAnimation(prevFocusedItem.getContentFragment(), currFocusedItem.getContentFragment());
	}

	// 菜单回到初始状态
	private void toInitStatus() {
		mStatus = Status.INIT;
		mPrevFocusItem = mCurrentFocusItem;
		mCurrentFocusItem = null;
		mCurrentSelectedItem = null;
		animatorToInit();
	}

	// 菜单回到选中状态
	private void toSelectedStatus(MenuItem currSelectedItem) {
		mStatus = Status.SELECTED;
		mCurrentSelectedItem = currSelectedItem;
		animatorToSelected(null);

	}

	// 菜单回到焦点状态
	private void toFocusedStatus(MenuItem focusedItem) {
		mStatus = Status.FOCUSED;
		mPrevFocusItem = mCurrentFocusItem;
		mCurrentFocusItem = focusedItem;
		animatorToFocused(null);
	}

	private void animatorToInit() {
		doAnimator(null, Config.Menu.INIT_X, Config.Menu.INIT_Y);
		for (int i = 0; i < mMenuItemList.size(); i++) {
			MenuItem item = mMenuItemList.get(i);
			item.toInit(null);
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
				item.toFocused(listener);
			} else {
				item.toUnfocused(null);
				if(item instanceof TextMenu){
					item.setBackgroundResource(R.drawable.menu_nav_bg_normal_shape);
				}
			}
		}
	}

	public void animatorToSelected(AnimatorListener listener) {
		doAnimator(null, Config.Menu.SELECTED_X, Config.Menu.SELECTED_Y);
		for (int i = 0; i < mMenuItemList.size(); i++) {
			MenuItem item = mMenuItemList.get(i);
			if (item == mCurrentFocusItem) {
				item.toSelected(null);
			} else {
				item.toUnselected(null);
			}
			if(item instanceof TextMenu){
				item.setBackgroundResource(R.drawable.menu_nav_bg_normal_shape);
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
			mPrevFocus.toUnfocused(null);
		}
		if (mCurrFocus != null) {
			if (mCurrFocus.getIndex() == 0) {
				doAnimator(null, Config.Menu.FOCUSED_X1, Config.Menu.FOCUSED_Y);
			}
			mCurrFocus.toFocused(null);
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
	public boolean onUpKey() {
		requestFocusToUp();
		return true;
	}

	@Override
	public boolean onDownKey() {
		requestFocusToDown();
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

	public MenuItem getMenuItem(int index) {
		if (index < 0 || index >= mMenuItemList.size()) {
			return null;
		}
		return mMenuItemList.get(index);
	}

	public void requestFocusToDown() {
		int curPos = mMenuItemList.indexOf(getCurrentItem());
		if (curPos < mMenuItemList.size() - 1) {
			curPos++;
			mMenuItemList.get(curPos).requestFocus();
		} else if (curPos == mMenuItemList.size() - 1) {
			curPos = 0;
			mMenuItemList.get(curPos).requestFocus();
		}
	}

	public void requestFocusToUp() {
		int curPos = mMenuItemList.indexOf(getCurrentItem());
		if (curPos > 0) {
			curPos--;
			mMenuItemList.get(curPos).requestFocus();
		} else if (curPos == 0) {
			curPos = mMenuItemList.size() - 1;
			mMenuItemList.get(curPos).requestFocus();
		}
	}

	public void requestFocusByPosition(int position) {
		if (position < 0 || position >= mMenuItemList.size()) {
			return;
		}
		mMenuItemList.get(position).requestFocus();
	}
}
