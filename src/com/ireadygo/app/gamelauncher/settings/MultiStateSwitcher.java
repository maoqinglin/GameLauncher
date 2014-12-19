package com.ireadygo.app.gamelauncher.settings;

import java.util.ArrayList;

import android.content.Context;
import android.widget.GridView;

import com.ireadygo.app.gamelauncher.settings.SwitcherAdapter.SwitcherItem;

public abstract class MultiStateSwitcher implements Switcher {

	protected Context mContext;
	private int mSwitcherId;
	protected ArrayList<Integer> mStateList = new ArrayList<Integer>();
	private int mCurState;
	private GridView mGridView;
	private SwitcherAdapter mSwitcherAdapter;
	protected JumperSwitcher mJumperSwitcher;

	public MultiStateSwitcher(Context context, int id) {
		mContext = context;
		mSwitcherId = id;
		initStateList();
	}

	@Override
	public void toggleState() {
		if (mStateList.size() == 0) {
			return;
		}
		mCurState = getActualState();
		int stateIndex = mStateList.indexOf(mCurState);
		if (stateIndex == mStateList.size() - 1) {
			updateStateChange(mStateList.get(0));
		} else {
			updateStateChange(mStateList.get(stateIndex + 1));
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

	public void init(GridView view) {
		mGridView = view;
		mSwitcherAdapter = (SwitcherAdapter)view.getAdapter();
		updateIconView();
	}

	public void updateIconView() {
		mCurState = getActualState();
		SwitcherItem switcher = (SwitcherItem)mSwitcherAdapter.getItem(getSwitcherId());
		int iconResId = getStateImage(mCurState);
		switcher.setSwitcherIconId(iconResId);
		int titleResId = getStateDescription(mCurState);
		switcher.setSwitcherTitle(mContext.getString(titleResId));
		switcher.setStartAnimation(false);
		mSwitcherAdapter.getView(getSwitcherId(), mGridView.getChildAt(getSwitcherId()), null);
	}

	protected abstract int getActualState();
	protected abstract void updateStateChange(int newState);
	protected abstract int getStateImage(int state);
	protected abstract int getStateDescription(int state);
	protected abstract void initStateList();

}
