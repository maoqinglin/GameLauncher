/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ireadygo.app.gamelauncher.game.data;

import java.lang.ref.WeakReference;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Handler;

import com.ireadygo.app.gamelauncher.game.data.GameLauncherModel.Callbacks;
import com.ireadygo.app.gamelauncher.game.utils.IconCache;
import com.ireadygo.app.gamelauncher.game.utils.IconDecorater;

public class GameLauncherAppState {
	private static final String TAG = "LauncherAppState";
	private static final String SHARED_PREFERENCES_KEY = "com.android.launcher3.prefs";
	private IconCache mIconCache;
	private boolean mIsScreenLarge;
	private float mScreenDensity;
	private int mLongPressTimeout = 300;
	private static WeakReference<GameLauncherProvider> sLauncherProvider;
	private static Context sContext;

	private static GameLauncherAppState INSTANCE;

	private GameLauncherModel mModle;

	private IconDecorater mIconDecorater;

	public static GameLauncherAppState getInstance(Context context) {
		if (INSTANCE == null) {
			synchronized (GameLauncherAppState.class) {
				if (INSTANCE == null) {
					INSTANCE = new GameLauncherAppState(context);
				}
			}
		}
		return INSTANCE;
	}

	public static GameLauncherAppState getInstanceNoCreate() {
		return INSTANCE;
	}

	public Context getContext() {
		return sContext;
	}

	/*
	 * public static void setApplicationContext(Context context) { if (sContext
	 * != null) { Log.w(TAG, "setApplicationContext called twice! old=" +
	 * sContext + " new=" + context); } sContext =
	 * context.getApplicationContext(); }
	 */

	private GameLauncherAppState(Context context) {
		sContext = context.getApplicationContext();
		if (sContext == null) {
			throw new IllegalStateException("LauncherAppState inited before app context set");
		}

		// set sIsScreenXLarge and mScreenDensity *before* creating icon cache
		mScreenDensity = sContext.getResources().getDisplayMetrics().density;
		mIconCache = new IconCache(sContext);

		mModle = new GameLauncherModel(sContext, mIconCache);

		mIconDecorater = new IconDecorater(sContext);
		// Register for changes to the favorites
		ContentResolver resolver = sContext.getContentResolver();
		resolver.registerContentObserver(GameLauncherSettings.Favorites.CONTENT_URI, true, mFavoritesObserver);
	}

	/**
	 * Call from Application.onTerminate(), which is not guaranteed to ever be
	 * called.
	 */
	public void onTerminate() {

		ContentResolver resolver = sContext.getContentResolver();
		resolver.unregisterContentObserver(mFavoritesObserver);
	}

	/**
	 * Receives notifications whenever the user favorites have changed.
	 */
	private final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			// If the database has ever changed, then we really need to force a
			// reload of the
			// workspace on the next load
			// mModel.resetLoadedState(false, true);
			// mModel.startLoaderFromBackground();
		}
	};

	public GameLauncherModel setCallback(Callbacks callback) {
		if (mModle == null) {
			throw new IllegalStateException("setLauncher() called before init()");
		}
		mModle.addCallback(callback);
		return mModle;
	}

	public IconCache getIconCache() {
		return mIconCache;
	}

	public GameLauncherModel getModel() {
		return mModle;
	}

	public static void setLauncherProvider(GameLauncherProvider provider) {
		sLauncherProvider = new WeakReference<GameLauncherProvider>(provider);
	}

	public static GameLauncherProvider getLauncherProvider() {
		return sLauncherProvider.get();
	}

	public static String getSharedPreferencesKey() {
		return SHARED_PREFERENCES_KEY;
	}

	public boolean isScreenLarge() {
		return mIsScreenLarge;
	}

	public static boolean isScreenLandscape(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public float getScreenDensity() {
		return mScreenDensity;
	}

	public int getLongPressTimeout() {
		return mLongPressTimeout;
	}

	public IconDecorater getIconDecorater() {
		return mIconDecorater;
	}
}
