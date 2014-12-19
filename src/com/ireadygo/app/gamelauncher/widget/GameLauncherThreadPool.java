package com.ireadygo.app.gamelauncher.widget;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GameLauncherThreadPool {

	private static final ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(8);
	private static final ExecutorService mCachedThreadPool = Executors.newCachedThreadPool();


	public static ExecutorService getFixedThreadPool() {
		return mFixedThreadPool;
	}

	public static ExecutorService getCachedThreadPool() {
		return mCachedThreadPool;
	}

}
