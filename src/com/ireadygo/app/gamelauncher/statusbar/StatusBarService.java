package com.ireadygo.app.gamelauncher.statusbar;

import java.util.ArrayList;

import android.R.color;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.download.Network;
import com.ireadygo.app.gamelauncher.appstore.download.Network.NetworkListener;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.lthj.unipay.plugin.ac;

public class StatusBarService extends Service {
	public static final String ACTION_UNDISPLAY = "com.ireadygo.app.gamelauncher.ACTION_UNDISPLAY";
	public static final String ACTION_DISPLAY = "com.ireadygo.app.gamelauncher.ACTION_DISPLAY";
	private static final String HANDLE_NAME = "OBox Controller 1";
	private static final int MSG_DISPLAY_STATUS_BAR = 100;
	private static final int MSG_UNDISPLAY_STATUS_BAR = 101;
	private static boolean sIsRunning = false;

	private WindowManager mWindowManager;
	private static StatusBarView mStatusBarView;
	private Network mNetWork;
	private BluetoothController mBluetoothController;
	private boolean mIsShow = false;
	private int mLastNetworkType = -1;
	private ArrayList<HandleColorItem> mColorItems = new ArrayList<StatusBarService.HandleColorItem>();

	public StatusBarService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mNetWork = new Network(this);
		mNetWork.addNetworkListener(mNetWorkListener);

		mBluetoothController = new BluetoothController(this);
		mBluetoothController.addStateChangedCallback(new BoxBluetoothStateChangeCallback());

		initColorItem();
		sIsRunning = true;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_DISPLAY);
		intentFilter.addAction(ACTION_UNDISPLAY);
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(mReceiver, intentFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (mStatusBarView == null) {
			mStatusBarView = new StatusBarView(this);
			mWindowManager.addView(mStatusBarView, getWindowManagerParams());
			mIsShow = true;
		}
		mStatusBarView.init();
		mLastNetworkType = NetworkUtils.getNetWorkType(this);
		return super.onStartCommand(intent, flags, startId);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_UNDISPLAY.equals(action)) {
				if (!mHandler.hasMessages(MSG_DISPLAY_STATUS_BAR)) {
					postMsg(MSG_UNDISPLAY_STATUS_BAR, 300);
				}
			} else if (ACTION_DISPLAY.equals(action)) {
					postMsg(MSG_DISPLAY_STATUS_BAR, 100);
			} else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device != null && HANDLE_NAME.equals(device.getName())) {
					HandleColorItem item = getFreeColorItem();
					if (item != null) {
						mStatusBarView.handleConnected(item.index);
						item.handleAddr = device.getAddress();
						item.isOccupy = true;
					}
				}
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device != null && HANDLE_NAME.equals(device.getName())) {
					HandleColorItem item = getHandleColorItemByAddr(device.getAddress());
					if (item != null) {
						mStatusBarView.handleDisconnected(item.index);
						item.isOccupy = false;
						item.handleAddr = null;
					}
				}
			}
		};
	};

	private void initColorItem() {
		HandleColorItem blueColorItem = new HandleColorItem(StatusBarView.HANDLE_BLUE, false);
		mColorItems.add(blueColorItem);
		HandleColorItem orangeColorItem = new HandleColorItem(StatusBarView.HANDLE_ORANGE, false);
		mColorItems.add(orangeColorItem);
		HandleColorItem purpleColorItem = new HandleColorItem(StatusBarView.HANDLE_PURPLE, false);
		mColorItems.add(purpleColorItem);
		HandleColorItem cyanColorItem = new HandleColorItem(StatusBarView.HANDLE_CYAN, false);
		mColorItems.add(cyanColorItem);
	}

	private HandleColorItem getFreeColorItem() {
		for (HandleColorItem item : mColorItems) {
			if (!item.isOccupy) {
				return item;
			}
		}
		return null;
	}

	private HandleColorItem getHandleColorItemByAddr(String addr) {
		for (HandleColorItem item : mColorItems) {
			if (!TextUtils.isEmpty(addr) && addr.equals(item.handleAddr)) {
				return item;
			}
		}
		return null;
	}

	private class HandleColorItem {
		int index;
		boolean isOccupy;
		String handleAddr;

		public HandleColorItem(int index,boolean occupy) {
			this.index = index;
			this.isOccupy = occupy;
		}
	}

	private void postMsg(int msgTag, long delay) {
		if (mHandler.hasMessages(MSG_DISPLAY_STATUS_BAR)) {
			mHandler.removeMessages(MSG_DISPLAY_STATUS_BAR);
		}
		if (mHandler.hasMessages(MSG_UNDISPLAY_STATUS_BAR)) {
			mHandler.removeMessages(MSG_UNDISPLAY_STATUS_BAR);
		}
		Message msg = mHandler.obtainMessage(msgTag);
		mHandler.sendMessageDelayed(msg, delay);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_DISPLAY_STATUS_BAR:
				if (mStatusBarView == null) {
					return;
				}
				if (!mIsShow) {
					mStatusBarView.setVisibility(View.VISIBLE);
					mIsShow = true;
				}
				break;
			case MSG_UNDISPLAY_STATUS_BAR:
				if (mStatusBarView == null) {
					return;
				}
				if (mIsShow) {
					mStatusBarView.setVisibility(View.INVISIBLE);
					mIsShow = false;
				}
				break;
			default:
				break;
			}
		};
	};

	private WindowManager.LayoutParams getWindowManagerParams() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				PixelFormat.TRANSLUCENT); // must be translucent to support
											// KitKat gradient
		params.gravity = Gravity.TOP;
		params.height = getResources().getDimensionPixelSize(R.dimen.statusbar_height);
		return params;
	}

	private NetworkListener mNetWorkListener = new NetworkListener() {

		@Override
		public void onNetworkDisconnected() {
			if (mStatusBarView != null) {
				mStatusBarView.updateDisconnectState(mLastNetworkType, -1);
			}
		}

		@Override
		public void onNetworkConnected() {
			int type = NetworkUtils.getNetWorkType(StatusBarService.this);
			if (mStatusBarView != null) {
				mStatusBarView.updateNetWorkState(type);
			}
			mLastNetworkType = type;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static boolean isRunning() {
		return sIsRunning;
	}

	@Override
	public void onDestroy() {
		sIsRunning = false;

		if (mStatusBarView != null) {
			mWindowManager.removeView(mStatusBarView);
			mStatusBarView = null;
		}
		mNetWork.removeNetworkListener(mNetWorkListener);

		unregisterReceiver(mBluetoothController);
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	class BoxBluetoothStateChangeCallback implements BluetoothStateChangeCallback {

		@Override
		public void onBluetoothStateChange(boolean state) {
			if (mStatusBarView != null) {
				mStatusBarView.updateBluetoothState(state);
			}
		}
	}

	public interface BluetoothStateChangeCallback {
		void onBluetoothStateChange(boolean state);
	}
}
