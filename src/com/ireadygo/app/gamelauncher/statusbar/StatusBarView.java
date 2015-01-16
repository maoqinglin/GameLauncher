package com.ireadygo.app.gamelauncher.statusbar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;

public class StatusBarView extends LinearLayout {
	private static final int WHAT_INVALIDATE_CHILD_VISIBILITY = 1;
	private static final int LOW_BATTERY_LIMIT = 5;
	public static final int HANDLE_BLUE = 0;
	public static final int HANDLE_ORANGE = 1;
	public static final int HANDLE_PURPLE = 2;
	public static final int HANDLE_CYAN = 3;

	private static final String KEY_CLOCK = "CLOCK";
	private static final String KEY_BLUETOOTH = "BLUETOOTH";
	private static final String KEY_NETWORK = "NETWORK";
	private static final String KEY_HANDLE_BLUE = "HANDLE_BLUE";
	private static final String KEY_HANDLE_ORANGE = "HANDLE_ORANGE";
	private static final String KEY_HANDLE_PURPLE = "HANDLE_PURPLE";
	private static final String KEY_HANDLE_CYAN = "HANDLE_CYAN";

	private Map<String, HandleHolder> mConnectedHandles = new HashMap<String, StatusBarView.HandleHolder>();
	private SparseArray<HandleHolder> mHandles = new SparseArray<HandleHolder>();
	private Map<String, StatusBarItem> mItemMap = new HashMap<String, StatusBarView.StatusBarItem>();
	private boolean mIsDisplay = true;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_INVALIDATE_CHILD_VISIBILITY:
				doInvalidateChildVisibility();
				break;
			default:
				break;
			}
		};
	};

	public StatusBarView(Context context) {
		this(context, null, 0);
	}

	public StatusBarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StatusBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.status_bar, this);
		View view = findViewById(R.id.status_bar_bluetooth);
		mItemMap.put(KEY_BLUETOOTH, new StatusBarItem(view));
		view = findViewById(R.id.status_bar_network);
		mItemMap.put(KEY_NETWORK, new StatusBarItem(view));
		view = findViewById(R.id.status_bar_clock_textview);
		mItemMap.put(KEY_CLOCK, new StatusBarItem(view));
		init();
	}

	private void init() {
		setClockStart();
		initNetWorkState();
		initBluetoothState();
		initAllHandle();
	}

	private void initAllHandle() {
		HandleHolder blueHolder = new HandleHolder();
		View view = (ImageView) findViewById(R.id.status_bar_handle_blue);
		blueHolder.item = new StatusBarItem(view);
		mItemMap.put(KEY_HANDLE_BLUE, blueHolder.item);
		blueHolder.state = HandleState.IDLE;
		blueHolder.handleIndex = HANDLE_BLUE;
		blueHolder.connectedId = R.drawable.icon_handle_blue_connected;
		blueHolder.disconnectedId = R.drawable.icon_handle_blue_disconnected;
		blueHolder.lowBatteryId = R.drawable.icon_handle_blue_low_battery;
		mHandles.put(HANDLE_BLUE, blueHolder);

		HandleHolder orangeHolder = new HandleHolder();
		view = (ImageView) findViewById(R.id.status_bar_handle_orange);
		orangeHolder.item = new StatusBarItem(view);
		mItemMap.put(KEY_HANDLE_ORANGE, orangeHolder.item);
		orangeHolder.state = HandleState.IDLE;
		orangeHolder.handleIndex = HANDLE_ORANGE;
		orangeHolder.connectedId = R.drawable.icon_handle_orange_connected;
		orangeHolder.disconnectedId = R.drawable.icon_handle_orange_disconnected;
		orangeHolder.lowBatteryId = R.drawable.icon_handle_orange_low_battery;
		mHandles.put(HANDLE_ORANGE, orangeHolder);

		HandleHolder purpleHolder = new HandleHolder();
		view = (ImageView) findViewById(R.id.status_bar_handle_purple);
		purpleHolder.item = new StatusBarItem(view);
		mItemMap.put(KEY_HANDLE_PURPLE, purpleHolder.item);
		purpleHolder.state = HandleState.IDLE;
		purpleHolder.handleIndex = HANDLE_PURPLE;
		purpleHolder.connectedId = R.drawable.icon_handle_purple_connected;
		purpleHolder.disconnectedId = R.drawable.icon_handle_purple_disconnected;
		purpleHolder.lowBatteryId = R.drawable.icon_handle_purple_low_battery;
		mHandles.put(HANDLE_PURPLE, purpleHolder);

		HandleHolder cyanHolder = new HandleHolder();
		view = (ImageView) findViewById(R.id.status_bar_handle_cyan);
		cyanHolder.item = new StatusBarItem(view);
		mItemMap.put(KEY_HANDLE_CYAN, cyanHolder.item);
		cyanHolder.state = HandleState.IDLE;
		cyanHolder.handleIndex = HANDLE_CYAN;
		cyanHolder.connectedId = R.drawable.icon_handle_cyan_connected;
		cyanHolder.disconnectedId = R.drawable.icon_handle_cyan_disconnected;
		cyanHolder.lowBatteryId = R.drawable.icon_handle_cyan_low_battery;
		mHandles.put(HANDLE_CYAN, cyanHolder);
		invalidateAllHandle();
	}

	private void invalidateAllHandle() {
		for (int i = 0; i < mHandles.size(); i++) {
			HandleHolder holder = mHandles.valueAt(i);
			invalidateHandle(holder);
		}
	}

	private void invalidateHandle(HandleHolder holder) {
		StatusBarItem item = holder.item;
		switch (holder.state) {
		case IDLE:
			item.isShowInDisplay = false;
			item.isShowInUndisplay = false;
			break;
		case CONNECTED:
			((ImageView) item.view).setImageResource(holder.connectedId);
			item.isShowInDisplay = true;
			item.isShowInUndisplay = false;
			break;
		case LOW_BATTERY:
			((ImageView) item.view).setImageResource(holder.lowBatteryId);
			item.isShowInDisplay = true;
			item.isShowInUndisplay = true;
			break;
		case DISCONNECTED:
			item.isShowInDisplay = false;
			item.isShowInUndisplay = false;
			break;
		}
		invalidateChildVisibility();
	}

	public void handleConnected(BluetoothDevice device, int index) {
		HandleHolder holder = mHandles.get(index);
		if (holder != null) {
			holder.state = HandleState.CONNECTED;
			invalidateHandle(holder);
			mConnectedHandles.put(device.getAddress(), holder);
		}
	}

	public void handleDisconnected(BluetoothDevice device, int index) {
		HandleHolder holder = mHandles.get(index);
		if (holder != null) {
			holder.state = HandleState.DISCONNECTED;
			invalidateHandle(holder);
			mConnectedHandles.remove(device.getAddress());
		}
	}

	public void handleGetBattery(BluetoothDevice device, int battery) {
		HandleHolder holder = mConnectedHandles.get(device.getAddress());
		if (holder != null) {
			if (battery <= LOW_BATTERY_LIMIT) {
				holder.state = HandleState.LOW_BATTERY;
			} else {
				holder.state = HandleState.CONNECTED;
			}
			invalidateHandle(holder);
		}
	}

	private void setClockStart() {
		MyDigitalClock clock = (MyDigitalClock) getItemView(KEY_CLOCK);
		clock.resume();
	}

	public void setClockStop() {
		MyDigitalClock clock = (MyDigitalClock) getItemView(KEY_CLOCK);
		clock.stop();
	}

	private void initNetWorkState() {
		if (NetworkUtils.isNetworkConnected(getContext())) {
			updateNetworkIcon(NetworkUtils.getNetWorkType(getContext()));
		} else {
			updateNetworkIcon(-1);
		}
	}

	private void updateNetworkIcon(int type) {
		StatusBarItem item = getItem(KEY_NETWORK);
		ImageView view = (ImageView) item.view;
		item.isShowInDisplay = true;
		if (ConnectivityManager.TYPE_ETHERNET == type) {
			view.setImageResource(R.drawable.icon_statusbar_network_ethernet);
		} else if (ConnectivityManager.TYPE_WIFI == type) {
			view.setImageResource(R.drawable.icon_statusbar_network_wifi);
		} else {
			view.setImageResource(R.drawable.icon_statusbar_ethernet_disconnect);
		}
		invalidateChildVisibility();
	}

	public void updateNetWorkState(int type) {
		updateNetworkIcon(type);
	}

	public void updateDisconnectState(int lastNeetworkType, int currentType) {
		if (currentType == -1) {
			StatusBarItem item = getItem(KEY_NETWORK);
			ImageView view = (ImageView) item.view;
			item.isShowInDisplay = true;
			view.setImageResource(R.drawable.icon_statusbar_ethernet_disconnect);
			invalidateChildVisibility();
			// if(ConnectivityManager.TYPE_ETHERNET == lastNeetworkType){
			// mNetWorkView.setImageResource(R.drawable.icon_statusbar_ethernet_disconnect);
			// }else if(ConnectivityManager.TYPE_WIFI == lastNeetworkType){
			// mNetWorkView.setImageResource(R.drawable.icon_statusbar_wifi_disconnect);
			// mNetWorkView.postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// Animation anim = AnimationUtils.loadAnimation(getContext(),
			// R.animator.wif_disconnect);
			// mNetWorkView.startAnimation(anim);
			// mNetWorkView.setVisibility(View.GONE);
			// }
			// },50);
			// }
		}
	}

	private void initBluetoothState() {
		boolean stateOn = BluetoothController.getBlueToothState();
		updateBluetoothState(stateOn);
	}

	public void updateBluetoothState(boolean state) {
		StatusBarItem item = getItem(KEY_BLUETOOTH);
		if (state) {
			item.isShowInDisplay = false;
		} else {
			item.isShowInDisplay = true;
		}
		invalidateChildVisibility();
	}

	public void updateHandleState(int pos, HandleState state) {

	}

	public static class HandleHolder {
		StatusBarItem item;
		HandleState state;
		int handleIndex;
		int connectedId;
		int disconnectedId;
		int lowBatteryId;
	}

	public enum HandleState {
		CONNECTED, DISCONNECTED, LOW_BATTERY, IDLE
	}

	public void undisplay() {
		mIsDisplay = false;
		invalidateChildVisibility();
	}

	public void display() {
		mIsDisplay = true;
		invalidateChildVisibility();
	}

	private static class StatusBarItem {
		View view;
		boolean isShowInDisplay = true;
		boolean isShowInUndisplay;

		public StatusBarItem(View view) {
			this.view = view;
		}
	}

	private StatusBarItem getItem(String key) {
		return mItemMap.get(key);
	}

	private View getItemView(String key) {
		return getItem(key).view;
	}

	private void invalidateChildVisibility() {
		if (mHandler.hasMessages(WHAT_INVALIDATE_CHILD_VISIBILITY)) {
			mHandler.removeMessages(WHAT_INVALIDATE_CHILD_VISIBILITY);
		}
		mHandler.sendEmptyMessage(WHAT_INVALIDATE_CHILD_VISIBILITY);
	}

	private void doInvalidateChildVisibility() {
		boolean isDisplay = mIsDisplay;
		Iterator<StatusBarItem> it = mItemMap.values().iterator();
		while (it.hasNext()) {
			StatusBarItem item = it.next();
			if (isDisplay) {
				if (item.isShowInDisplay) {
					item.view.setVisibility(View.VISIBLE);
				} else {
					item.view.setVisibility(View.GONE);
				}
			} else {
				if (item.isShowInUndisplay) {
					item.view.setVisibility(View.VISIBLE);
				} else {
					item.view.setVisibility(View.GONE);
				}
			}
		}
	}
}
