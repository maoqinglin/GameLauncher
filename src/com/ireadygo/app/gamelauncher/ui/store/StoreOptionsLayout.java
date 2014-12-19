package com.ireadygo.app.gamelauncher.ui.store;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.base.BaseOptionsLayout;
import com.ireadygo.app.gamelauncher.ui.base.OptionsItem;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;

public class StoreOptionsLayout extends BaseOptionsLayout {
	private OptionsItem mRecommendBtn;
	private OptionsItem mCategoryBtn;
	private OptionsItem mCollectionBtn;
	private OptionsItem mSearchBtn;
	private OptionsItem mGameManageBtn;
	private OptionsItem mSettingsBtn;

	// private StoreDetailActivity mFragment;
	// private View mFocusView;

	public StoreOptionsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StoreOptionsLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StoreOptionsLayout(Context context) {
		super(context);
	}

	public void requestOptionsFocusByTag(Destination destination) {
		OptionsItem optionsItem = getStoreOptionsItemByTag(destination);
		optionsItem.requestFocus();
	}

	private OptionsItem getStoreOptionsItemByTag(Destination destination) {
		switch (destination) {
		case STORE_RECOMMEND:
			return mRecommendBtn;
		case STORE_CATEGORY:
			return mCategoryBtn;
		case STORE_COLLECTION:
			return mCollectionBtn;
		case STORE_GAME_MANAGE:
			return mGameManageBtn;
		case STORE_SEARCH:
			return mSearchBtn;
		case STORE_SETTINGS:
			return mSettingsBtn;
		default:
			return mRecommendBtn;
		}
	}

	protected void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.store_options_layout, this, true);

		mRecommendBtn = (OptionsItem) findViewById(R.id.storeOptionsRecommend);
		mCategoryBtn = (OptionsItem) findViewById(R.id.storeOptionsCategory);
		mCollectionBtn = (OptionsItem) findViewById(R.id.storeOptionsCollection);
		mSearchBtn = (OptionsItem) findViewById(R.id.storeOptionsSearch);
		mGameManageBtn = (OptionsItem) findViewById(R.id.storeOptionsGameManage);
		mSettingsBtn = (OptionsItem) findViewById(R.id.storeOptionsSettings);

		initOptionButton(mRecommendBtn);
		initOptionButton(mCategoryBtn);
		initOptionButton(mCollectionBtn);
		initOptionButton(mSearchBtn);
		initOptionButton(mGameManageBtn);
		initOptionButton(mSettingsBtn);
	}
}
