package com.ireadygo.app.gamelauncher.statusbar;

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
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.download.Network;
import com.ireadygo.app.gamelauncher.appstore.download.Network.NetworkListener;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;

public class StatusBarService extends Service {
	public static final String ACTION_GET_BATTERY = "com.ireadygo.app.devicemanager.ACTION_GET_BATTERY";
	public static final String ACTION_HANDLE_CONNECTED = "com.ireadygo.app.devicemanager.ACTION_HANDLE_CONNECTED";
	public static final String ACTION_HANDLE_DISCONNECTED = "com.ireadygo.app.devicemanager.ACTION_HANDLE_DISCONNECTED";
	public static final String EXTRA_LED_COLOR_INDEX = "com.ireadygo.app.devicemanager.EXTRA_LED_COLOR";
	public static final String EXTRA_BATTERY = "com.ireadygo.app.devicemanager.EXTRA_BATTERY";

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
	private int mLastNetworkType = -1;

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

		sIsRunning = true;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_DISPLAY);
		intentFilter.addAction(ACTION_UNDISPLAY);
		intentFilter.addAction(ACTION_HANDLE_CONNECTED);
		intentFilter.addAction(ACTION_HANDLE_DISCONNECTED);
		intentFilter.addAction(ACTION_GET_BATTERY);
		registerReceiver(mReceiver, intentFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (mStatusBarView == null) {
			mStatusBarView = new StatusBarView(this);
			mWindowManager.addView(mStatusBarView, getWindowManagerParams());
		}
		mLastNetworkType = NetworkUtils.getNetWorkType(this);
		return super.onStartCommand(intent, flags, startId);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_GET_BATTERY.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int battery = intent.getIntExtra(EXTRA_BATTERY, 100);
				mStatusBarView.handleGetBattery(device, battery);
			} else if (ACTION_UNDISPLAY.equals(action)) {
				if (!mHandler.hasMessages(MSG_DISPLAY_STATUS_BAR)) {
					postMsg(MSG_UNDISPLAY_STATUS_BAR, 300);
				}
			} else if (ACTION_DISPLAY.equals(action)) {
				postMsg(MSG_DISPLAY_STATUS_BAR, 100);
			} else if (ACTION_HANDLE_CONNECTED.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int index = intent.getIntExtra(EXTRA_LED_COLOR_INDEX, -1);
				if (index >= 0) {
					mStatusBarView.handleConnected(device,index);
				}
			} else if (ACTION_HANDLE_DISCONNECTED.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int index = intent.getIntExtra(EXTRA_LED_COLOR_INDEX, -1);
				if (index >= 0) {
					mStatusBarView.handleDisconnected(device,index);
				}
			}
		};
	};

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
				if(mStatusBarView != null){
					mStatusBarView.display();
				}
				break;
			case MSG_UNDISPLAY_STATUS_BAR:
				if (mStatusBarView != null) {
					mStatusBarView.undisplay();
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
