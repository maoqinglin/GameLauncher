package com.ireadygo.app.gamelauncher.appstore.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.ireadygo.app.gamelauncher.R;

public class SoundPoolManager {

	private static final int MAX_STREAMS = 1;
	public static final int SOUND_SLIDE = 1;
	public static final int SOUND_MENU = 2;
	public static final int SOUND_SELECT = 3;
	public static final int SOUND_ENTER = 4;
	public static final int SOUND_EXIT = 5;

	private SoundPool mSoundPool;
	private SparseIntArray mSoundRes = new SparseIntArray(5);

	private static SoundPoolManager sSoundPoolManager;


	public static SoundPoolManager instance(Context context) {
		if(sSoundPoolManager == null) {
			synchronized (SoundPoolManager.class) {
				if(sSoundPoolManager == null) {
					sSoundPoolManager = new SoundPoolManager(context);
					return sSoundPoolManager;
				}
			}
		}

		return sSoundPoolManager;
	}

	private SoundPoolManager(Context context) {
		init(context);
	}

	private void init(Context context) {
		mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
		mSoundRes.put(SOUND_SLIDE, mSoundPool.load(context, R.raw.slide, 1));
		mSoundRes.put(SOUND_MENU, mSoundPool.load(context, R.raw.menu, 1));
		mSoundRes.put(SOUND_SELECT, mSoundPool.load(context, R.raw.select, 1));
		mSoundRes.put(SOUND_ENTER, mSoundPool.load(context, R.raw.enter, 1));
		mSoundRes.put(SOUND_EXIT, mSoundPool.load(context, R.raw.exit, 1));
	}

	public void play(int sound) {
		mSoundPool.play(mSoundRes.get(sound), 1, 1, 1, 0, 1.0f);
	}

	public void release() {
		mSoundPool.release();
	}
}
