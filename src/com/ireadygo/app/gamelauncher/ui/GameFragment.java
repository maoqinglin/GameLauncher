package com.ireadygo.app.gamelauncher.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.game.adapter.GameModel;
import com.ireadygo.app.gamelauncher.game.adapter.GameModel.DataType;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.MenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

@SuppressLint("ValidFragment")
public class GameFragment extends BaseContentFragment {

	protected boolean mDataHasInit = false;

	protected HListView mHListView;

	protected boolean mIsAttach;
	private boolean mIsViewDestory = false;

	private GameModel mGameModel;

	public GameFragment(Activity activity, MenuFragment menuFragment) {
		super(activity, menuFragment);
		mGameModel = new GameModel(getRootActivity(),this);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mygame_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_ALL);
		mHListView = (HListView) view.findViewById(R.id.h_listview_mygame_fragment);
		View emptyView = view.findViewById(R.id.empty);  
		mHListView.setEmptyView(emptyView); 
		mGameModel.setHListView(mHListView, DataType.TYPE_GAME);
	}



	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(mHListView);
	}

	@Override
	public boolean onSunKey() {
		return mGameModel.onSunKey();
	}

	@Override
	public boolean onMoonKey() {
		return mGameModel.onMoonKey();
	}

	@Override
	public boolean onMountKey() {
		if (mHListView.hasFocus()) {
			return mGameModel.onMountainKey();
		}
		return super.onMountKey();
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}

	@Override
	public boolean onWaterKey() {
		if (mHListView.hasFocus()) {
			return mGameModel.onWaterKey();
		}
		return super.onWaterKey();
	}

	@Override
	public Animator outAnimator(AnimatorListener listener) {
		return mGameModel.outAnimator(DataType.TYPE_GAME, listener);
	}

	@Override
	public int getOutAnimatorDuration() {
		return mGameModel.getOutAnimatorDuration(DataType.TYPE_GAME);
	}
}