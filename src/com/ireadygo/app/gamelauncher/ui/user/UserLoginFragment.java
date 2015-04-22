package com.ireadygo.app.gamelauncher.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.account.AccountLoginActivity;
import com.ireadygo.app.gamelauncher.ui.account.AccountRegisterActivity;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.GuideTwoBtnLayout;
import com.ireadygo.app.gamelauncher.ui.widget.GuideTwoBtnLayout.OnLRBtnClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class UserLoginFragment extends BaseContentFragment {

	private GuideTwoBtnLayout mGuideTwoBtnLayout;
	
	public UserLoginFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_login_register_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected boolean isCurrentFocus() {
		return true;
	}
	
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(View.GONE, TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);

		mGuideTwoBtnLayout = (GuideTwoBtnLayout) view.findViewById(R.id.guideTwoBtnLayout);
		mGuideTwoBtnLayout.setLeftBtnText(R.string.starting_guide_login);
		mGuideTwoBtnLayout.setRightBtnText(R.string.starting_guide_register);
		mGuideTwoBtnLayout.setLeftBtnNextFocus();
		mGuideTwoBtnLayout.setRightBtnNextFocus();
		mGuideTwoBtnLayout.setOnLRBtnClickListener(new OnLRBtnClickListener() {

			@Override
			public void onRightBtnClickListener(View view) {
				Intent intent = new Intent(getRootActivity(), AccountRegisterActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				getRootActivity().startActivity(intent);
			}
			
			@Override
			public void onLeftBtnClickListener(View view) {
				Intent intent = new Intent(getRootActivity(), AccountLoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				getRootActivity().startActivity(intent);
			}
		});
	}
}
