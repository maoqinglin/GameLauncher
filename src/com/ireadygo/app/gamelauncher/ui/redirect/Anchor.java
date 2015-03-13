package com.ireadygo.app.gamelauncher.ui.redirect;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.ui.activity.CustomWebviewActivity;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.StoreActivity;
import com.ireadygo.app.gamelauncher.ui.store.category.CategoryDetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.collection.CollectionDetailActivity;
import com.ireadygo.app.gamelauncher.ui.user.UserActivity;

public class Anchor implements Serializable {
	public static final String EXTRA_DESTINATION = "EXTRA_DESTINATION";
	private static final long serialVersionUID = 1L;

	private Destination mDestination;
	private String mArgs1;
	private Intent mIntent;

	public Anchor(Destination destination) {
		this.mDestination = destination;
		mIntent = newIntent();
	}

	public Intent getIntent() {
		return mIntent;
	}

	private Intent newIntent() {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DESTINATION, mDestination);
		intent.setClass(GameLauncherApplication.getApplication(), getActivityClassByDestination(mDestination));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	private Class<? extends Activity> getActivityClassByDestination(Destination destination) {
		Class<? extends Activity> clazz = null;
		switch (destination) {
		case CATEGORY_DETAIL:
			clazz = CategoryDetailActivity.class;
			break;
		case COLLECTION_DETAIL:
			clazz = CollectionDetailActivity.class;
			break;
		case STORE_RECOMMEND:
		case STORE_CATEGORY:
		case STORE_COLLECTION:
		case STORE_SEARCH:
		case STORE_GAME_MANAGE:
		case STORE_SETTINGS:
		case STORE_FAVORITE_APPS:
			clazz = StoreActivity.class;
			break;
		case GAME_DETAIL:
			clazz = DetailActivity.class;
			break;
		case ACCOUNT_WEALTH:
		case ACCOUNT_FREECARD:
		case ACCOUNT_PERSONAL:
			// case ACCOUNT_NOTICE:
		case ACCOUNT_RECHARGE:
			clazz = UserActivity.class;
			break;
		case WEBPAGE:
			clazz = CustomWebviewActivity.class;
			break;
		default:
			break;
		}
		return clazz;
	}

	public enum Destination {
		GAME_DETAIL, STORE_RECOMMEND, STORE_CATEGORY, STORE_COLLECTION, STORE_SEARCH, STORE_FAVORITE_APPS, STORE_GAME_MANAGE, //
		STORE_SETTINGS, COLLECTION_DETAIL, CATEGORY_DETAIL, ACCOUNT_WEALTH, ACCOUNT_PERSONAL, ACCOUNT_NOTICE, ACCOUNT_RECHARGE, ACCOUNT_FREECARD, WEBPAGE
	}

	public String getArgs1() {
		return mArgs1;
	}

	public void setArgs1(String args1) {
		this.mArgs1 = args1;
	}

	public Destination getDestination() {
		return mDestination;
	}

}
