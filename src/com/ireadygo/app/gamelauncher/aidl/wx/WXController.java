package com.ireadygo.app.gamelauncher.aidl.wx;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public final class WXController {

	private static final String ACTION_REMOTE_SERVICE = "com.ireadygo.app.gamelauncher.aidl.wx";
	private static WXController sWxController;
	private final Context mContext;
	private final InnerServiceConnection mConnection = new InnerServiceConnection();
	private boolean isBind = false;
	private IWXPublicManagerAidlService mService = null;

	private WXController(Context context) {
		mContext = context;
	}

	public static WXController instance(Context context) {
		if(sWxController == null) {
			synchronized (WXController.class) {
				if (sWxController == null) {
					sWxController = new WXController(context);
				}
			}
		}
		return sWxController;
	}

	public void init() {
		if(!isBind) {
			Intent intent = new Intent(ACTION_REMOTE_SERVICE);
			mContext.startService(intent);
			isBind = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	public void exit() {
		if(isBind) {
			mContext.unbindService(mConnection);
			isBind = false;
		}
	}

	public boolean isBind() {
		return isBind;
	}

	public void operator(AppInfo appInfo) {
		if(mService != null) {
			try {
				mService.operator(appInfo);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private class InnerServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IWXPublicManagerAidlService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

	}
}
