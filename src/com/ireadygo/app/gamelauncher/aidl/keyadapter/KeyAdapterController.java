package com.ireadygo.app.gamelauncher.aidl.keyadapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class KeyAdapterController {

	private static final String ACTION_REMOTE_SERVICE = "com.ireadygo.app.gamelauncher.aidl.keyadapter";
	private static KeyAdapterController sKeyAdapterController;
	private final Context mContext;
	private final InnerListener mInnerListener = new InnerListener();
	private final InnerServiceConnection mConnection = new InnerServiceConnection();
	private boolean isBind = false;
	private IKeyAdapterAidlService mService = null;
	private OnHandlerEventListener mListener;
	
	private KeyAdapterController(Context context) {
		mContext = context;
	}

	public void init() {
		if(!isBind) {
			Intent intent = new Intent(ACTION_REMOTE_SERVICE);
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

	public void setOnHandlerEventListener(OnHandlerEventListener listener) {
		if(listener != null) {
			unregisterCallback();
			mListener = listener;
			registerCallback();
		}
	}

	public String getLoginAccount() {
		try {
			return mService.getLoginAccount();
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getLoginNickname() {
		try {
			return mService.getLoginNickname();
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void queryNicknames(String accounts) {
		try {
			mService.queryNicknames(accounts);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void registerCallback() {
		try {
			mService.registerCallback(mInnerListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void unregisterCallback() {
		try {
			mService.unregisterCallback(mInnerListener);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static KeyAdapterController instance(Context context) {
		if(sKeyAdapterController == null) {
			synchronized (KeyAdapterController.class) {
				if (sKeyAdapterController == null) {
					sKeyAdapterController = new KeyAdapterController(context);
				}
			}
		}
		return sKeyAdapterController;
	}

	private class InnerListener extends IKeyAdapterAidlCallback.Stub {

		@Override
		public void handlerCommEvent(int msgId, String param) throws RemoteException {
			if(mListener != null) {
				mListener.handlerCommEvent(msgId, param);
			}
		}
		
	}

	private class InnerServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IKeyAdapterAidlService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

	}
}
