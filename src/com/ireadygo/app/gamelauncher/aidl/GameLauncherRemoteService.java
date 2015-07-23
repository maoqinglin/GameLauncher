package com.ireadygo.app.gamelauncher.aidl;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.aidl.keyadapter.KeyAdapterRemoteServiceImpl;
import com.ireadygo.app.gamelauncher.aidl.rentfree.RentFreeRemoteServiceImpl;
import com.ireadygo.app.gamelauncher.aidl.wx.WXPublicManagerRemoteServiceImpl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GameLauncherRemoteService extends Service {

	private static final String ACTION_KEYADAPTER_SERVICE = "com.ireadygo.app.gamelauncher.aidl.keyadapter";
	private static final String ACTION_WX_SERVICE = "com.ireadygo.app.gamelauncher.aidl.wx";
	private static final String ACTION_RENTFREE_SERVICE = "com.ireadygo.app.gamelauncher.rentfree.RentFreeRemoteService";
	private KeyAdapterRemoteServiceImpl mKeyAdapterRemoteServiceImpl = 
			new KeyAdapterRemoteServiceImpl(GameLauncherApplication.getApplication());
	
	private WXPublicManagerRemoteServiceImpl mWxPublicManagerRemoteServiceImpl = 
			new WXPublicManagerRemoteServiceImpl(GameLauncherApplication.getApplication());

	private RentFreeRemoteServiceImpl mRentFreeRemoteServiceImpl = new RentFreeRemoteServiceImpl(
			GameLauncherApplication.getApplication());

	@Override
	public void onCreate() {
		super.onCreate();
		mWxPublicManagerRemoteServiceImpl.init();
	}

	@Override
	public IBinder onBind(Intent intent) {
		if(intent.getAction().equals(ACTION_KEYADAPTER_SERVICE)) {
			return mKeyAdapterRemoteServiceImpl;
		}

		if(intent.getAction().equals(ACTION_WX_SERVICE)) {
			return mWxPublicManagerRemoteServiceImpl;
		}

		if(intent.getAction().equals(ACTION_RENTFREE_SERVICE)) {
			return mRentFreeRemoteServiceImpl;
		}

		return null;
	}

	@Override
	public void onDestroy() {
		mKeyAdapterRemoteServiceImpl.onDestroy();
		mWxPublicManagerRemoteServiceImpl.onDestory();
		mRentFreeRemoteServiceImpl.onDestroy();
		super.onDestroy();
	}

}
