package com.ireadygo.app.gamelauncher.ui;

import java.util.HashMap;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.mygame.adapter.GameModel;
import com.ireadygo.app.gamelauncher.mygame.adapter.GameModel.DataType;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherModel.Callbacks;
import com.ireadygo.app.gamelauncher.mygame.info.FolderInfo;
import com.ireadygo.app.gamelauncher.mygame.info.ItemInfo;
import com.ireadygo.app.gamelauncher.mygame.ui.view.GameAllAppLayout;
import com.ireadygo.app.gamelauncher.mygame.ui.view.GameAllAppLayout.AppWindowShowStateListener;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.MenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

@SuppressLint("ValidFragment")
public class MyAppFragment extends BaseContentFragment implements Callbacks{

	protected HListView mHListView;
	protected boolean mDataHasInit = false;
	protected boolean mIsAttach;
	private boolean mIsViewDestory = false;
	private GameAllAppLayout mGameAllAppLayout;
	private GameModel mGameModel;

	private HMultiListView mHMultiListView;
	private HAdapter mHAdapter ;
	public MyAppFragment(Activity activity, MenuFragment menuFragment) {
		super(activity, menuFragment);
//		mGameModel = new GameModel(getRootActivity(),this);
		GameLauncherAppState.getInstance(getRootActivity()).setCallback(this);
		GameLauncherAppState.getInstance(getRootActivity()).getModel().startLoader();
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.myapp_fragment, container, false);
		initView(view);
		return view;
	}

	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	// mIsAttach = true;
	// }

	// @Override
	// public void onDetach() {
	// super.onDetach();
	// mIsAttach = false;
	// }

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN,TipFlag.FLAG_TIPS_WATER,TipFlag.FLAG_TIPS_MOON);
//		mHListView = (HListView) view.findViewById(R.id.h_listview_myapp_fragment);
//		mHListView.setEmptyView(new StoreEmptyView(getRootActivity()));
//		mGameModel.setHListView(mHListView, DataType.TYPE_APP);
//		mGameModel.setAppWindowShowStateListener(mAllAppShowStateListener);
		
		//test
		mHMultiListView = (HMultiListView)view.findViewById(R.id.mutillist);
		if(mHMultiListView != null){
			mHMultiListView.setAdapter(mHAdapter);
			mHMultiListView.notifyDataSetChanged();
		}
	}

	public void displayAllAppLayout(){
		if (!AccountManager.getInstance().isLogined(getRootActivity())) {
			Toast.makeText(getRootActivity(),getRootActivity().getString(R.string.account_no_account_login_prompt), Toast.LENGTH_SHORT).show();
			return;
		}
		if (GameLauncherConfig.SLOT_ENABLE) {
			mGameAllAppLayout = null;
			mGameAllAppLayout = new GameAllAppLayout(getRootActivity());
			mGameAllAppLayout.openAllApp(getRootActivity(), mAllAppShowStateListener);
		}
	}

	AppWindowShowStateListener mAllAppShowStateListener = new AppWindowShowStateListener(){

		@Override
		public void openAppWindow() {
			mHListView.setVisibility(View.INVISIBLE);
		}

		@Override
		public void closeAppWindow() {
			mHListView.setVisibility(View.VISIBLE);
			mHListView.requestFocus();
		}
	};

//	@Override
//	public void onResume() {
//		super.onResume();
//		if (mIsViewDestory) {
//			mHListView.setSelectionFromLeft(0, mHListView.getPaddingLeft());
//			mIsViewDestory = false;
//		}
//
//	};

	// @Override
	// public void onResume() {
	// super.onResume();
	// if (mIsViewDestory) {
	// mHListView.setSelectionFromLeft(0, mHListView.getPaddingLeft());
	// mIsViewDestory = false;
	// }
	// }

	// @Override
	// public void onDestroyView() {
	// super.onDestroyView();
	// mGameModel.onDestoreView();
	// mIsViewDestory = true;
	// }

	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// mGameModel.onDestory();
	// mGameModel = null;
	// }

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
	public boolean onWaterKey() {
		if (mHListView.hasFocus()) {
			return mGameModel.onWaterKey();
		}
		return super.onWaterKey();
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}
	
	@Override
	public Animator outAnimator(AnimatorListener listener) {
		return mGameModel.outAnimator(DataType.TYPE_APP, listener);
	}
	
	@Override
	public int getOutAnimatorDuration() {
		return mGameModel.getOutAnimatorDuration(DataType.TYPE_APP);
	}

	@Override
	public void bindGames(List<ItemInfo> infos) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bindApps(List<ItemInfo> infos) {
		mHAdapter = new HAdapter(infos);
		if(mHMultiListView != null){
			mHMultiListView.setAdapter(mHAdapter);
			mHMultiListView.notifyDataSetChanged();
		}
	}

	@Override
	public void bindFolders(HashMap<Long, FolderInfo> folders) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameAddOrUpdate(ItemInfo info, boolean isAdd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameRemove(ItemInfo info) {
		// TODO Auto-generated method stub
		
	}
}