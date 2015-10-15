package com.ireadygo.app.gamelauncher.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;

public class UserActivity extends BaseMenuActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				requestMenuFocusByIntent(getIntent());
			}
		}, 300);
	}

	private void requestMenuFocusByIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		Destination destination = (Destination) intent
				.getSerializableExtra(Anchor.EXTRA_DESTINATION);
		int position = 0;
		if (destination != null) {
			switch (destination) {
			case ACCOUNT_PERSONAL:
				position = 0;
				break;
			// case ACCOUNT_NOTICE:
			// position = 1;
			// break;
			case ACCOUNT_RECHARGE:
				position = 1;
				break;
			default:
				break;
			}
		}
		getMenuFragment().requestFocusByPosition(position);
	}

	@Override
	public BaseMenuFragment createMenuFragment() {
		return new UserMenuFragment(this);
	}

	public static void startSelf(Context context, int focusPosition) {
		Intent intent = new Intent(context, UserActivity.class);
		intent.putExtra(EXTRA_FOCUS_POSITION, focusPosition);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	@Override
	public boolean isShowBoxMessageLayout() {
		return false;
	}
}
