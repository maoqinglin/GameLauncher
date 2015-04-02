package com.ireadygo.app.gamelauncher.ui.store.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;

public class CategoryDetailActivity extends BaseMenuActivity {
	public static final String EXTRA_CATEGORY_GAMES = "Games";
	public static SparseArray<Integer> mCategoryGamesArray;
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

	public static void startSelf(Context context, int focusPosition,SparseArray<Integer> games) {
		mCategoryGamesArray = games;
		Intent intent = new Intent(context, CategoryDetailActivity.class);
		intent.putExtra(EXTRA_FOCUS_POSITION, focusPosition);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static int getGamesByCategoryId(long categoryId){
		if(categoryId > 0 && mCategoryGamesArray != null){
			for(int i=0;i< mCategoryGamesArray.size();i++){
				if(mCategoryGamesArray.keyAt(i) == categoryId){
					return mCategoryGamesArray.get((int)categoryId);
				}
			}
		}
		return 0;
	}

	@Override
	public boolean onBackKey() {
		finish();
		return true;
	}
}
