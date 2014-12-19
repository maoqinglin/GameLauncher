package com.ireadygo.app.gamelauncher.ui.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.MenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.DigitTextView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

@SuppressLint("ValidFragment")
public class UserFragment extends BaseContentFragment {

	private DigitTextView mDayNum;
	private DigitTextView mGamesNum;
	private DigitTextView mRubbitNum;
	private DigitTextView mRubbitQuanNum;
	private DigitTextView mIntegralNum;
	private View mListView;

	public UserFragment(Activity activity, MenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user, null);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN,TipFlag.FLAG_TIPS_MOON);
		mDayNum = (DigitTextView) view.findViewById(R.id.dayNum);
		mDayNum.setUnit("Day");

		mGamesNum = (DigitTextView) view.findViewById(R.id.gamesNum);
		mGamesNum.setUnit("Games");

		mRubbitNum = (DigitTextView) view.findViewById(R.id.rabbitNum);
		mRubbitNum.setUnit("兔兔币");

		mRubbitQuanNum = (DigitTextView) view.findViewById(R.id.rabbitQuanNum);
		mRubbitQuanNum.setUnit("兔兔礼券");

		mIntegralNum = (DigitTextView) view.findViewById(R.id.integralNum);
		mIntegralNum.setUnit("积分");

		mListView = view.findViewById(R.id.userAppList);
	}

	@Override
	protected boolean isCurrentFocus() {
		// TODO Auto-generated method stub
		return false;
	}
}
