package com.ireadygo.app.gamelauncher.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.widget.GridView;

import com.ireadygo.app.gamelauncher.settings.SwitcherAdapter.SwitcherItem;


public abstract class TwoStateSwitcher implements Switcher {

	protected static final int STATE_ENABLE = 0;
	protected static final int STATE_DISABLE = 1;
	protected static final int STATE_INTERMEDIATE = 2;

	protected int mCurState = STATE_DISABLE;

	protected Context mContext;
	protected int mSwitcherId;
	protected boolean isProcessSwitch;
	protected AnimationDrawable mFrameAnimation;
	protected GridView mGridView;
	protected SwitcherAdapter mSwitcherAdapter;
	protected JumperSwitcher mJumperSwitcher;

	public TwoStateSwitcher(Context context,int id) {
		mContext = context;
		mSwitcherId = id;
	}

	public void init(GridView view) {
		mGridView = view;
		mSwitcherAdapter = (SwitcherAdapter)view.getAdapter();
		updateIconView();
	}

	//点击ICON会触发该函数
	@Override
	public void toggleState() {
		mCurState = getActualState();
		switch (mCurState) {
		case STATE_ENABLE:
		case STATE_DISABLE:
			updateStateChange();
			break;
		case STATE_INTERMEDIATE:
			break;
		default:
			break;
		}
	}

	public void jumpToSettings() {
		if (null != mJumperSwitcher) {
			mJumperSwitcher.toggleState();
		}
	}

	@Override
	public int getSwitcherId() {
		return mSwitcherId;
	}

	protected void setCurState(int state) {
		mCurState = state;
	}

	protected void updateIconView() {
		mCurState = getActualState();
		SwitcherItem switcher = (SwitcherItem)mSwitcherAdapter.getItem(getSwitcherId());
		switch (mCurState) {
		case STATE_DISABLE:
			switcher.setSwitcherIconId(getDisableImage());
			switcher.setStartAnimation(false);
			break;
		case STATE_ENABLE:
			switcher.setSwitcherIconId(getEnableImage());
			switcher.setStartAnimation(false);
			break;
		case STATE_INTERMEDIATE:
			switcher.setAnimatorId(getInterMedateImage());
			switcher.setStartAnimation(true);
			break;
		default:
			break;
		}
		mSwitcherAdapter.getView(getSwitcherId(), mGridView.getChildAt(getSwitcherId()), null);
	}

	protected void updateIconView(int state) {
		SwitcherItem switcher = (SwitcherItem)mSwitcherAdapter.getItem(getSwitcherId());
		switch (state) {
		case STATE_DISABLE:
			switcher.setSwitcherIconId(getDisableImage());
			switcher.setStartAnimation(false);
			break;
		case STATE_ENABLE:
			switcher.setSwitcherIconId(getEnableImage());
			switcher.setStartAnimation(false);
			break;
		case STATE_INTERMEDIATE:
			switcher.setAnimatorId(getInterMedateImage());
			switcher.setStartAnimation(true);
			break;
		default:
			break;
		}
		mSwitcherAdapter.getView(getSwitcherId(), mGridView.getChildAt(getSwitcherId()), null);
	}


	protected abstract void updateStateChange();
	protected abstract void setActualState(Intent intent);
	protected abstract int getEnableImage();
	protected abstract int getDisableImage();
	protected abstract int getInterMedateImage();
	protected abstract int getActualState();


}
