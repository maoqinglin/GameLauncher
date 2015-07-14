package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryInfo;
import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;

public class CategoryDetailActivity extends BaseMenuActivity {
	public static final String EXTRA_CATEGORY_GAMES = "Games";
	public static final String EXTRA_CATEGORYS = "Categorys";
	public static List<CategoryInfo> sCategoryList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		initView();
		final int position = getIntent().getIntExtra(EXTRA_FOCUS_POSITION, 0);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getMenuFragment().requestFocusByPosition(position);
			}
		}, 300);
	}

	@Override
	public BaseMenuFragment createMenuFragment() {
		return new CategoryDetailMenuFragment(this,sCategoryList);
	}

	public static void startSelf(Context context, int focusPosition,List<CategoryInfo> categoryList) {
		sCategoryList = categoryList;
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
