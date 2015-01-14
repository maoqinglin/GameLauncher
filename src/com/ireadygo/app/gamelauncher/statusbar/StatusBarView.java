package com.ireadygo.app.gamelauncher.statusbar;

import java.util.HashMap;
import java.util.Map;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;

public class StatusBarView extends LinearLayout {
	private static final int LOW_BATTERY_LIMIT = 50;
	public static final int HANDLE_BLUE = 0;
	public static final int HANDLE_ORANGE = 1;
	public static final int HANDLE_PURPLE = 2;
	public static final int HANDLE_CYAN = 3;
	private final ImageView mBlueToothView;
	private final ImageView mNetWorkView;
	private final MyDigitalClock mTimeTextView;
	private Map<String, HandleHolder> mConnectedHandles = new HashMap<String, StatusBarView.HandleHolder>();
	private SparseArray<HandleHolder> mHandles = new SparseArray<HandleHolder>();

	public StatusBarView(Context context) {
		this(context, null, 0);
	}

	public StatusBarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StatusBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		LayoutInflater.from(context).inflate(R.layout.status_bar, this);

		mBlueToothView = (ImageView) findViewById(R.id.status_bar_bluetooth);
		mNetWorkView = (ImageView) findViewById(R.id.status_bar_network);
		mTimeTextView = (MyDigitalClock) findViewById(R.id.status_bar_clock_textview);
	}

	public void init() {
		setClockStart();
		initNetWorkState();
		initBluetoothState();
		initAllHandle();
	}

	private void initAllHandle() {
		HandleHolder blueHolder = new HandleHolder();
		blueHolder.handle = (ImageView) findViewById(R.id.status_bar_handle_blue);
		blueHolder.state = HandleState.IDLE;
		blueHolder.handleIndex = HANDLE_BLUE;
		blueHolder.connectedId = R.drawable.icon_handle_blue_connected;
		blueHolder.disconnectedId = R.drawable.icon_handle_blue_disconnected;
		blueHolder.lowBatteryId = R.drawable.icon_handle_blue_low_battery;
		mHandles.put(HANDLE_BLUE, blueHolder);

		HandleHolder orangeHolder = new HandleHolder();
		orangeHolder.handle = (ImageView) findViewById(R.id.status_bar_handle_orange);
		orangeHolder.state = HandleState.IDLE;
		orangeHolder.handleIndex = HANDLE_ORANGE;
		orangeHolder.connectedId = R.drawable.icon_handle_orange_connected;
		orangeHolder.disconnectedId = R.drawable.icon_handle_orange_disconnected;
		orangeHolder.lowBatteryId = R.drawable.icon_handle_orange_low_battery;
		mHandles.put(HANDLE_ORANGE, orangeHolder);

		HandleHolder purpleHolder = new HandleHolder();
		purpleHolder.handle = (ImageView) findViewById(R.id.status_bar_handle_purple);
		purpleHolder.state = HandleState.IDLE;
		purpleHolder.handleIndex = HANDLE_PURPLE;
		purpleHolder.connectedId = R.drawable.icon_handle_purple_connected;
		purpleHolder.disconnectedId = R.drawable.icon_handle_purple_disconnected;
		purpleHolder.lowBatteryId = R.drawable.icon_handle_purple_low_battery;
		mHandles.put(HANDLE_PURPLE, purpleHolder);

		HandleHolder cyanHolder = new HandleHolder();
		cyanHolder.handle = (ImageView) findViewById(R.id.status_bar_handle_cyan);
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
		switch (holder.state) {
		case IDLE:
			holder.handle.setVisibility(View.GONE);
			break;
		case CONNECTED:
			holder.handle.setImageResource(holder.connectedId);
			holder.handle.setVisibility(View.VISIBLE);
			break;
		case LOW_BATTERY:
			holder.handle.setImageResource(holder.lowBatteryId);
			holder.handle.setVisibility(View.VISIBLE);
			break;
		case DISCONNECTED:
			holder.handle.setVisibility(View.GONE);
			break;
		}
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
		mTimeTextView.resume();
	}

	public void setClockStop() {
		mTimeTextView.stop();
	}

	private void initNetWorkState() {
		if (NetworkUtils.isNetworkConnected(getContext())) {
			updateNetworkIcon(NetworkUtils.getNetWorkType(getContext()));
		} else {
			updateNetworkIcon(-1);
		}
	}

	private void updateNetworkIcon(int type) {
		mNetWorkView.setVisibility(View.VISIBLE);
		if (ConnectivityManager.TYPE_ETHERNET == type) {
			mNetWorkView.setImageResource(R.drawable.icon_statusbar_network_ethernet);
		} else if (ConnectivityManager.TYPE_WIFI == type) {
			mNetWorkView.setImageResource(R.drawable.icon_statusbar_network_wifi);
		} else {
			mNetWorkView.setImageResource(R.drawable.icon_statusbar_ethernet_disconnect);
		}
	}

	public void updateNetWorkState(int type) {
		updateNetworkIcon(type);
	}

	public void updateDisconnectState(int lastNeetworkType, int currentType) {
		if (currentType == -1) {
			mNetWorkView.setImageResource(R.drawable.icon_statusbar_ethernet_disconnect);
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
		if (state) {
			mBlueToothView.setVisibility(View.GONE);
		} else {
			mBlueToothView.setVisibility(View.VISIBLE);
		}
	}

	public void updateHandleState(int pos, HandleState state) {

	}

	public static class HandleHolder {
		ImageView handle;
		HandleState state;
		int handleIndex;
		int connectedId;
		int disconnectedId;
		int lowBatteryId;
	}

	public enum HandleState {
		CONNECTED, DISCONNECTED, LOW_BATTERY, IDLE
	}
}
