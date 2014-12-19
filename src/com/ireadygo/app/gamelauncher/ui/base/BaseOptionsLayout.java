package com.ireadygo.app.gamelauncher.ui.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import com.ireadygo.app.gamelauncher.ui.OnChildFocusChangeListener;

public abstract class BaseOptionsLayout extends KeyEventLayout implements OnFocusChangeListener {
	private static final int WHAT_LOSE_FOCUS = 1;

	private List<OptionsItem> mOptionList = new ArrayList<OptionsItem>();
	private OptionsItem mCurrentFocusedBtn;
	private OptionsItem mCurrentSelectedBtn;
	private OnChildFocusChangeListener mChildFocusChangeListener;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_LOSE_FOCUS:
				OptionsItem optionsBtn = (OptionsItem) msg.obj;
				optionsBtn.toSelectedAnimator().start();
				mCurrentFocusedBtn = null;
				mCurrentSelectedBtn = optionsBtn;
				break;

			default:
				break;
			}
		};
	};

	public BaseOptionsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public BaseOptionsLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		initView(context);
	}

	public BaseOptionsLayout(Context context) {
		super(context);
	}

	protected abstract void initView(Context context);

	protected void initOptionButton(OptionsItem optionBtn) {
		optionBtn.setOnFocusChangeListener(this);
		mOptionList.add(optionBtn);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		final OptionsItem optionsBtn = (OptionsItem) v;
		if (hasFocus) {
			if (mHandler.hasMessages(WHAT_LOSE_FOCUS)) {
				mHandler.removeMessages(WHAT_LOSE_FOCUS);
			}
			if (mCurrentFocusedBtn != null) {
				mCurrentFocusedBtn.toNoselectedAnimator().start();
			} else if (mCurrentSelectedBtn != null) {
				mCurrentSelectedBtn.toNoselectedAnimator().start();
			}
			optionsBtn.toFocusAnimator().start();
			mCurrentSelectedBtn = optionsBtn;
			mCurrentFocusedBtn = optionsBtn;
		} else {
			Message msg = Message.obtain(mHandler, WHAT_LOSE_FOCUS);
			msg.obj = optionsBtn;
			msg.sendToTarget();
		}
		if (mChildFocusChangeListener != null) {
			mChildFocusChangeListener.onChildFocusChange(mOptionList.indexOf(optionsBtn), v, hasFocus);
		}
	}

	public void setOnChildFocusChangeListener(OnChildFocusChangeListener listener) {
		this.mChildFocusChangeListener = listener;
	}

	public View getCurrentSelectedView() {
		return mCurrentSelectedBtn;
	}

	@Override
	protected boolean isCurrentFocus() {
		if (mCurrentFocusedBtn == null) {
			return false;
		}
		return mCurrentFocusedBtn.hasFocus();
	}

	@Override
	public boolean onMoonKey() {
		// if (mFragment != null) {
		// mFragment.getActivity().startMainPage();
		// return true;
		// }
		return super.onMoonKey();
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}

	@Override
	public boolean onL1Key() {
		requestFocusToLeft();
		return true;
	}

	@Override
	public boolean onR1Key() {
		requestFocusToRight();
		return true;
	}

	@Override
	public boolean onLeftKey() {
		return onL1Key();
	}

	@Override
	public boolean onRightKey() {
		return onR1Key();
	}

	public void requestFocusToLeft() {
		int curMenuIdx = mOptionList.indexOf(mCurrentFocusedBtn);
		if (curMenuIdx > 0) {
			curMenuIdx--;
			mOptionList.get(curMenuIdx).requestFocus();
		} else if (curMenuIdx == 0) {
			curMenuIdx = mOptionList.size() - 1;
			mOptionList.get(curMenuIdx).requestFocus();
		}
	}

	public void requestFocusToRight() {
		int curMenuIdx = mOptionList.indexOf(mCurrentFocusedBtn);
		if (curMenuIdx < mOptionList.size() - 1) {
			curMenuIdx++;
			mOptionList.get(curMenuIdx).requestFocus();
		} else if (curMenuIdx == mOptionList.size() - 1) {
			curMenuIdx = 0;
			mOptionList.get(curMenuIdx).requestFocus();
		}
	}

	public OptionsItem getCurrentItem() {
		if (mCurrentFocusedBtn != null) {
			return mCurrentFocusedBtn;
		} else if (mCurrentSelectedBtn != null) {
			return mCurrentSelectedBtn;
		}
		return null;
	}
}
