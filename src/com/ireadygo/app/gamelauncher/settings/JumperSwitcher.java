package com.ireadygo.app.gamelauncher.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.GridView;

public class JumperSwitcher implements Switcher {

	Intent mJumpIntent;
	Context mContext;
	int mSwitchId;
	protected GridView mGridView;
	protected SwitcherAdapter mSwitcherAdapter;

	public JumperSwitcher(Context context,int id) {
		mContext = context;
		mSwitchId = id;
	}

	public JumperSwitcher(Context context,int id,Intent intent) {
		mContext = context;
		mSwitchId = id;
		mJumpIntent = intent;
	}

	@Override
	public void toggleState() {
		if (null != mJumpIntent) {
			try {
				mContext.startActivity(mJumpIntent);
			} catch (ActivityNotFoundException e) {
				// do nothing
			}
		}
	}

	public void setJumpIntent(Intent intent) {
		mJumpIntent = intent;
	}

	public void init(GridView view) {
		mGridView = view;
		mSwitcherAdapter = (SwitcherAdapter)mGridView.getAdapter();
	}

	@Override
	public int getSwitcherId() {
		return mSwitchId;
	}

	protected void updateIconView(int state, int level) {
		
	};
}
