package com.ireadygo.app.gamelauncher.boxmessage;

import java.util.ArrayList;
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
import android.util.Log;

import com.ireadygo.app.gamelauncher.boxmessage.BoxMessageService.BoxMessageLocalBinder;
import com.ireadygo.app.gamelauncher.boxmessage.data.BoxMessage;

public class BoxMessageController {

	public static final int TYPE_CHANGE_ADD = 0;
	public static final int TYPE_CHANGE_DEL = 1;
	public static final int TYPE_CHANGE_UPDATE = 2;
	public static final int TYPE_CHANGE_BTN_DISMISS = 3;
	public static final int TYPE_CHANGE_BTN_SHOW = 4;
	private static final String CHANGE_ACTION_BOXMESSAGE = "com.ireadygo.app.boxmessage.change";
	private static final String BIND_ACTION_BOXMESSAGE = "com.ireadygo.app.gamelauncher.boxmessage.BoxMessageService";
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
		Intent intent = new Intent(mContext, BoxMessageService.class);
		mContext.startService(intent);
		mIsInit = mContext.bindService(new Intent(BIND_ACTION_BOXMESSAGE), mServiceConnection, Context.BIND_AUTO_CREATE);
		mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(CHANGE_ACTION_BOXMESSAGE));
	}
	
	public List<BoxMessage> getBoxMessages() {
		if(mService != null) {
			return mService.getAllBoxMessage();
		} else {
			Log.i("chenrui", "Service is Null!!!");
		}
		return new ArrayList<BoxMessage>();
	}

	public int getUnReadMsgCount() {
		if(mService != null) {
			return mService.getUnReadMsgCount();
		}
		return 0;
	}

	public void setMsgReadStatus(String pkgName, int id, boolean isRead) {
		if(mService != null) {
			mService.setMsgReadStatus(pkgName, id, isRead);
		}
	}

	public void removeBoxMessage(String pkgName, int id) {
		if(mService != null) {
			mService.removeBoxMessage(pkgName, id);
		}
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
		mContext.unbindService(mServiceConnection);
		mIsInit = false;
		mLocalBroadcastManager.unregisterReceiver(mReceiver);
		mListeners.clear();
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
