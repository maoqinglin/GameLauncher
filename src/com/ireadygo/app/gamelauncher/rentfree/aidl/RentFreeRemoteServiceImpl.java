package com.ireadygo.app.gamelauncher.rentfree.aidl;

import java.util.List;

import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.rentfree.info.AppTimeUploadResultItem;
import com.ireadygo.app.gamelauncher.rentfree.info.AppTimeUploadItem;
import com.ireadygo.app.gamelauncher.utils.Md5Util;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

public class RentFreeRemoteServiceImpl extends IRentFreeAidlService.Stub {

	private final RemoteCallbackList<IRentFreeAidlCallback> mCallbackList = new RemoteCallbackList<IRentFreeAidlCallback>();
	private final RemoteCallbackList<IRentFreeTimeAidlCallback> mTimeCallbackList = new RemoteCallbackList<IRentFreeTimeAidlCallback>();
	private Context mContext;
	private static final String SEED = "T6b749cX";
	private volatile int mRetryCount = 0;

	public RentFreeRemoteServiceImpl(Context context) {
		mContext = context;
	}

	@Override
	public void getRentFreeGameList() throws RemoteException {
		GameLauncherThreadPool.getCachedThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				try {
					List<String> rentList = GameInfoHub.instance(mContext).getRentReliefAppList();
					if (rentList != null && !rentList.isEmpty()) {
						callBack(rentList);
						mRetryCount = 0;
					}
				} catch (InfoSourceException e) {
					e.printStackTrace();
					if (mRetryCount < 3) {
						try {
							getRentFreeGameList();
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
					}
					mRetryCount++;
				}
			}
		});
	}

	@Override
	public void uploadStatisticTimeList(final List<AppTimeUploadItem> uploadList) throws RemoteException {
		if (uploadList != null && !uploadList.isEmpty()) {
			GameLauncherThreadPool.getCachedThreadPool().execute(new Runnable() {

				@Override
				public void run() {
					int size = uploadList.size();
					for (int i = 0; i < size; i++) {
						AppTimeUploadResultItem result = null;
						AppTimeUploadItem upload = uploadList.get(i);
						if (upload != null) {
							String pkgName = uploadList.get(i).getPackageName();
							long playingTime = uploadList.get(i).getPlayingTime();
							try {
								long reqId = System.currentTimeMillis();
								String sign = getGameSign(pkgName, playingTime, reqId);
								result = GameInfoHub.instance(mContext).saveAppTime(pkgName, playingTime,
										String.valueOf(reqId), sign);
							} catch (InfoSourceException e) {
								e.printStackTrace();
								result = null;
							}
							if (result == null) {
								result = new AppTimeUploadResultItem();
								result.setPackageName(pkgName);
								result.setResult(AppTimeUploadResultItem.FAIL);
							}
							callBack(result);
						}
					}
				}
			});
		}

	}

	@Override
	public void registerCallback(IRentFreeAidlCallback callback) throws RemoteException {
		if (callback != null) {
			synchronized (mCallbackList) {
				mCallbackList.register(callback);
			}
		}
	}

	@Override
	public void unregisterCallback(IRentFreeAidlCallback callback) throws RemoteException {
		if (callback != null) {
			synchronized (mCallbackList) {
				mCallbackList.unregister(callback);
			}
		}
	}

	@Override
	public void registerTimeCallback(IRentFreeTimeAidlCallback callback) throws RemoteException {
		if (callback != null) {
			synchronized (mTimeCallbackList) {
				mTimeCallbackList.register(callback);
			}
		}
	}

	@Override
	public void unregisterTimeCallback(IRentFreeTimeAidlCallback callback) throws RemoteException {
		if (callback != null) {
			synchronized (mTimeCallbackList) {
				mTimeCallbackList.unregister(callback);
			}
		}
	}

	private void callBack(List<String> rentList) {
		try {
			int N = mCallbackList.beginBroadcast();
			Log.d("lmq", "callBack---rentList--N = " + N);
			for (int i = 0; i < N; i++) {
				mCallbackList.getBroadcastItem(i).receiverRentFreeList(rentList);
			}
			mCallbackList.finishBroadcast();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IllegalStateException e1) {
			Log.e("lmq", "callBack----rentList = " + rentList);
			e1.printStackTrace();
		}
	}

	private void callBack(AppTimeUploadResultItem uploadResult) {
		try {
			int N = mTimeCallbackList.beginBroadcast();
			Log.d("lmq", "callBack---AppTimeUploadResultVO--N = " + N);
			for (int i = 0; i < N; i++) {
				mTimeCallbackList.getBroadcastItem(i).handlerResult(uploadResult);
			}
			mTimeCallbackList.finishBroadcast();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IllegalStateException e1) {
			Log.e("lmq", "callBack----uploadResult = " + uploadResult);
			e1.printStackTrace();
		}
	}

	private String getGameSign(String packageName, long playTime, long reqId) {
		return Md5Util.getMD5(new StringBuffer().append(packageName).append(playTime).append(reqId).append(SEED)
				.toString());
	}

	public void onDestroy() {
		mCallbackList.kill();
		mTimeCallbackList.kill();
	}

}
