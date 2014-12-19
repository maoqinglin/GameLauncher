package com.ireadygo.app.gamelauncher.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.ireadygo.app.gamelauncher.service.GameLauncherService.GameLauncherBinder;

public class GameLauncherBindable extends BindableService {

	private static GameLauncherBindable sInstance;
	private GameLauncherBinder mGameLauncherBinder;

	private GameLauncherBindable(Context context) {
		super(context);
	}

	public static GameLauncherBindable instance(Context context) {
		if (null == sInstance) {
			synchronized (GameLauncherBindable.class) {
				if (null == sInstance) {
					sInstance = new GameLauncherBindable(context);
				}
			}
		}
		return sInstance;
	}

	public GameLauncherBinder getAppStoreBinder() {
		if (STATE_UNBIND != getBindState()) {
			return mGameLauncherBinder;
		}

		throw new IllegalStateException("Bind AppStoreService first!!!");
	}

	@Override
	public synchronized void bind(BindResponse response) {
		context().startService(serviceIntent());
		super.bind(response);
	}

	@Override
	public synchronized void unbind() {
		super.unbind();
		context().stopService(new Intent(context(), GameLauncherService.class));
	}

	@Override
	protected Intent serviceIntent() {
		return new Intent(context(), GameLauncherService.class);
	}

	@Override
	protected void onServiceConnected(ComponentName cn, IBinder service) {
		mGameLauncherBinder = (GameLauncherBinder) service;
	}

	@Override
	protected void onServiceDisconnected(ComponentName cn) {
		throw new IllegalStateException();
	}
}
