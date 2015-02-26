package com.ireadygo.app.gamelauncher.ui.store;

import android.content.Context;
import android.content.Intent;

import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;

public class StoreActivity extends BaseMenuActivity {

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
