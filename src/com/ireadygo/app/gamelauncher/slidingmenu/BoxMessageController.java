package com.ireadygo.app.gamelauncher.slidingmenu;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.ireadygo.app.gamelauncher.slidingmenu.BoxMessageService.BoxMessageLocalBinder;
import com.ireadygo.app.gamelauncher.slidingmenu.data.BoxMessage;

public class BoxMessageController {

	private static final String CHANGE_ACTION_BOXMESSAGE = "com.ireadygo.app.boxmessage.change";
	private static final String BIND_ACTION_BOXMESSAGE = "com.ireadygo.app.gamelauncher.slidingmenu.BoxMessageService";
	private final Context mContext;
	private final LocalBroadcastManager mLocalBroadcastManager;
	private static BoxMessageController sBoxMessageController;
	private boolean mIsInit = false;
	private BoxMessageLocalBinder mService;
	private ConcurrentLinkedQueue<OnBoxMessageUpdateListener> mListeners = new ConcurrentLinkedQueue<OnBoxMessageUpdateListener>();
	private BoxMessageServiceConnection mServiceConnection = new BoxMessageServiceConnection();
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int type = intent.getIntExtra("type", -1);
			for (OnBoxMessageUpdateListener listener : mListeners) {
				listener.onChange(type);
			}
		}
	};
	
	private BoxMessageController(Context context) {
		mContext = context;
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
	}

	public static BoxMessageController getInstance(Context context) {
		if(sBoxMessageController == null) {
			synchronized (BoxMessageController.class) {
				if(sBoxMessageController == null) {
					sBoxMessageController = new BoxMessageController(context);
				}
			}
		}
		return sBoxMessageController;
	}

	public void init() {
		if(!mIsInit) {
			Intent intent = new Intent(BIND_ACTION_BOXMESSAGE);
			mContext.startService(intent);
			mIsInit = mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
			mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(CHANGE_ACTION_BOXMESSAGE));
		}
	}
	
	public List<BoxMessage> getBoxMessages() {
		return mService.getAllBoxMessage();
	}

	public int getMsgCount() {
		return mService.getMsgsCount();
	}

	public void setMsgReadStatus(String pkgName, int id, boolean isRead) {
		mService.setMsgReadStatus(pkgName, id, isRead);
	}

	public void removeBoxMessage(String pkgName, int id) {
		mService.removeBoxMessage(pkgName, id);
	}

	public void addBoxMessageUpdateListener(OnBoxMessageUpdateListener listener) {
		if(listener != null) {
			mListeners.add(listener);
		}
	}

	public void removeBoxMessageChangeListener(OnBoxMessageUpdateListener listener) {
		if(listener != null) {
			mListeners.remove(listener);
		}
	}

	public boolean isInit() {
		return mIsInit;
	}

	public void shutdown() {
		if(mIsInit) {
			mContext.unbindService(mServiceConnection);
			mIsInit = false;
			mLocalBroadcastManager.unregisterReceiver(mReceiver);
			mListeners.clear();
		}
	}

	private class BoxMessageServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = (BoxMessageLocalBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
	}
	
	public interface OnBoxMessageUpdateListener {
		void onChange(int type);
	}
}
