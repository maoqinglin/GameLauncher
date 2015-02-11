package com.ireadygo.app.gamelauncher.aidl.rentfree;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RentFreeRemoteService extends Service {

	private RentFreeRemoteServiceImpl mRentFreeRemoteServiceImpl = new RentFreeRemoteServiceImpl(
			GameLauncherApplication.getApplication());

	@Override
	public IBinder onBind(Intent arg0) {
		Log.d("lmq", "RentFreeRemoteService---onBind----mRentFreeRemoteServiceImpl ="+mRentFreeRemoteServiceImpl);
		return mRentFreeRemoteServiceImpl;
	}

	@Override
	public void onDestroy() {
		mRentFreeRemoteServiceImpl.onDestroy();
		super.onDestroy();
	}

}
