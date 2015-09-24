package com.ireadygo.app.gamelauncher.aidl.rentfree;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

public class RentFreeController {

	private static final String BIND_SERVICE_NAME = "com.ireadygo.app.gamelauncher.rentfree.RentFreeRemoteService";
	private static final int MSG_DATA_UPLOAD = 1;
	private static final int MSG_GET_RENT_LIST = 2;
	private boolean mIsBind;
	private IRentFreeAidlService mService;
	private ServiceCallBack mCallBack = new ServiceCallBack();
	private ServiceTimeCallBack mTimeCallBack = new ServiceTimeCallBack();
	private RentFreeConnection mConnection = new RentFreeConnection();
	private IGameStatisticListChangeListener mGameStatisticListChangeListener;
	private IGameTimeInfoResultListener mGameTimeInfoResultListener;
	private final Context mContext;
	private final Handler mHandler;
	private static RentFreeController sInstance;

	private RentFreeController(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
	}

	public static RentFreeController instance(Context context, Handler handler) {
		if(null == sInstance) {
			synchronized (RentFreeController.class) {
				if(null == sInstance) {
					sInstance = new RentFreeController(context, handler);
				}
			}
		}
		return sInstance;
	}
	
	public void open() {
		if (!mIsBind) {
			Intent intent = new Intent(BIND_SERVICE_NAME);
			mContext.startService(intent);
			mIsBind = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	public void shutdown() {
		if (mIsBind) {
			mContext.unbindService(mConnection);
			mIsBind = false;
		}
	}

	public void getRentFreeGameList() throws RemoteException {
		if(mService != null) {
			mService.getRentFreeGameList();
		}
	}
	
	public void uploadStatisticTimeList(List<AppTimeUploadItem> uploadList) throws RemoteException {
		if(mService != null) {
			mService.uploadStatisticTimeList(uploadList);
		}
	}
	
	public boolean isBindService() {
		return mIsBind;
	}

	public void unregistGameStatisticListListener() {
		if(mService != null) {
			try {
				mService.unregisterCallback(mCallBack);
				mGameStatisticListChangeListener = null;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void registGameStatisticListListener(IGameStatisticListChangeListener listener) {
		if(mService != null) {
			try {
				mService.registerCallback(mCallBack);
				mGameStatisticListChangeListener = listener;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void unregistGameTimeInfoResultListener() {
		if(mService != null) {
			try {
				mService.unregisterTimeCallback(mTimeCallBack);
				mGameTimeInfoResultListener = null;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void registGameTimeInfoResultListener(IGameTimeInfoResultListener listener) {
		if(mService != null) {
			try {
				mService.registerTimeCallback(mTimeCallBack);
				mGameTimeInfoResultListener = listener;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private class ServiceCallBack extends IRentFreeAidlCallback.Stub {

		@Override
		public void receiverRentFreeList(List<String> rentList) throws RemoteException {
			if (mGameStatisticListChangeListener != null) {
				mGameStatisticListChangeListener.onStatisticListChange(rentList);
			}
		}
	}

	private class ServiceTimeCallBack extends IRentFreeTimeAidlCallback.Stub {

		@Override
		public void handlerResult(AppTimeUploadResultItem uploadResult) throws RemoteException {
			if(mGameTimeInfoResultListener != null) {
				mGameTimeInfoResultListener.onReuslt(uploadResult);
			}
		}
	}

	private final class RentFreeConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IRentFreeAidlService.Stub.asInterface(service);
			mHandler.sendEmptyMessage(MSG_GET_RENT_LIST);
			mHandler.sendEmptyMessage(MSG_DATA_UPLOAD);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
	}
}
