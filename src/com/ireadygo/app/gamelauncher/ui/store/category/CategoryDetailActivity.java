package com.ireadygo.app.gamelauncher.ui.store.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;

public class CategoryDetailActivity extends BaseMenuActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		int position = getIntent().getIntExtra(EXTRA_FOCUS_POSITION, 0);
		getMenuFragment().requestFocusByPosition(position);
	}

	@Override
	public BaseMenuFragment createMenuFragment() {
		return new CategoryDetailMenuFragment(this);
	}

	public static void startSelf(Context context, int focusPosition) {
		Intent intent = new Intent(context, CategoryDetailActivity.class);
		intent.putExtra(EXTRA_FOCUS_POSITION, focusPosition);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	@Override
	public boolean onBackKey() {
		finish();
		return true;
	}
}
