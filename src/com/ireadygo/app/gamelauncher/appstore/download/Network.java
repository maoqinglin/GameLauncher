package com.ireadygo.app.gamelauncher.appstore.download;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class Network {

	private final ConcurrentLinkedQueue<NetworkListener> mListeners
		= new ConcurrentLinkedQueue<Network.NetworkListener>();
	private final Context mContext;
	private boolean mIsConnected;
	private final NetworkReceiver mNetworkReceiver = new NetworkReceiver();

	public Network(Context context) {
		mContext = context;
		mContext.enforceCallingOrSelfPermission(
				android.Manifest.permission.ACCESS_NETWORK_STATE,
				"Need permission: "
						+ android.Manifest.permission.ACCESS_NETWORK_STATE);
		mIsConnected = isNetworkConnected();
	}

	public boolean isNetworkConnected() {
		NetworkInfo ni = ((ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (ni != null && ni.getState() == State.CONNECTED) {
			return true;
		}

		return false;
	}

	public void addNetworkListener(NetworkListener listener) {
		synchronized (mListeners) {
			if (listener == null) {
				throw new IllegalArgumentException();
			}

			if (mListeners.size() == 0) {
				IntentFilter filter = new IntentFilter();
				filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
				mContext.registerReceiver(mNetworkReceiver,
						filter);
			}

			if (!mListeners.contains(listener)) {
				mListeners.add(listener);
			}
		}
	}

	public void removeNetworkListener(NetworkListener listener) {
		synchronized (mListeners) {
			if (!mListeners.contains(listener)) {
				return;
			}
			mListeners.remove(listener);

			if (mListeners.size() == 0) {
				try {
					mContext.unregisterReceiver(mNetworkReceiver);
				} catch (Exception e) {
				} // Ignore
			}
		}
	}

	private class NetworkReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mIsConnected != isNetworkConnected()) {
				mIsConnected = isNetworkConnected();
				synchronized (mListeners) {
					if (mIsConnected) {
						for (NetworkListener listener : mListeners) {
							listener.onNetworkConnected();
						}
					} else {
						for (NetworkListener listener : mListeners) {
							listener.onNetworkDisconnected();
						}
					}
				}
			}
		}
	}

	public interface NetworkListener {

		void onNetworkConnected();

		void onNetworkDisconnected();
	}
}
