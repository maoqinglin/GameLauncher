package com.ireadygo.app.gamelauncher.ui.store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;

public class StoreActivity extends BaseMenuActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFocusView().setNextFocusLeftId(R.id.store_menu_search);
		getFocusView().setNextFocusRightId(R.id.store_menu_search);
		getFocusView().setNextFocusUpId(R.id.store_menu_search);
		getFocusView().setNextFocusDownId(R.id.store_menu_search);
		requestMenuFocusByIntent(getIntent());
	}

	@Override
	public boolean onBackKey() {
		finish();
		return true;
	}

	private void requestMenuFocusByIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		Destination destination = (Destination) intent.getSerializableExtra(Anchor.EXTRA_DESTINATION);
		int position = 0;
		if (destination != null) {
			switch (destination) {
			case STORE_SEARCH:
				position = 0;
				break;
			case STORE_CATEGORY:
				position = 1;
				break;
			case STORE_COLLECTION:
				position = 2;
				break;
			case STORE_FAVORITE_APPS:
				position = 3;
				break;
			case STORE_GAME_MANAGE:
				position = 4;
				break;
			}
		}
		getMenuFragment().requestFocusByPosition(position);
	}

	@Override
	public BaseMenuFragment createMenuFragment() {
		return new StoreMenuFragment(this);
	}

	public static void startSelf(Context context) {
		Intent intent = new Intent(context, StoreActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		SoundPoolManager.instance(context).play(SoundPoolManager.SOUND_ENTER);
		context.startActivity(intent);
	}
}
