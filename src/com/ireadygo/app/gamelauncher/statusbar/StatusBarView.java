package com.ireadygo.app.gamelauncher.statusbar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;

public class StatusBarView extends LinearLayout {
	private static final int HANDLE_BLUE = 0;
	private static final int HANDLE_ORANGE = 1;
	private static final int HANDLE_PURPLE = 2;
	private static final int HANDLE_CYAN = 3;
	private final ImageView mBlueToothView;
	private final ImageView mNetWorkView;
	private ImageView mHandleBlue, mHandleOrange, mHandlePurple, mHandleCyan;
	private final MyDigitalClock mTimeTextView;
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
		mHandleBlue = (ImageView) findViewById(R.id.status_bar_handle_blue);
		mHandleOrange = (ImageView) findViewById(R.id.status_bar_handle_orange);
		mHandlePurple = (ImageView) findViewById(R.id.status_bar_handle_purple);
		mHandleCyan = (ImageView) findViewById(R.id.status_bar_handle_cyan);
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
		blueHolder.handle = mHandleBlue;
		blueHolder.handle.setImageResource(R.drawable.icon_handle_blue_connected);
		blueHolder.state = HandleState.CONNECTED;
		mHandles.put(HANDLE_BLUE, blueHolder);

		HandleHolder orangeHolder = new HandleHolder();
		orangeHolder.handle = mHandleOrange;
		orangeHolder.handle.setImageResource(R.drawable.icon_handle_orange_connected);
		orangeHolder.state = HandleState.CONNECTED;
		mHandles.put(HANDLE_ORANGE, orangeHolder);

		HandleHolder purpleHolder = new HandleHolder();
		purpleHolder.handle = mHandlePurple;
		purpleHolder.handle.setImageResource(R.drawable.icon_handle_purple_connected);
		purpleHolder.handle.setVisibility(View.GONE);
		purpleHolder.state = HandleState.CONNECTED;
		mHandles.put(HANDLE_PURPLE, purpleHolder);

		HandleHolder cyanHolder = new HandleHolder();
		cyanHolder.handle = mHandleCyan;
		cyanHolder.handle.setImageResource(R.drawable.icon_handle_cyan_connected);
		cyanHolder.state = HandleState.CONNECTED;
		mHandles.put(HANDLE_CYAN, cyanHolder);
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
		if (ConnectivityManager.TYPE_ETHERNET == type) {
			mNetWorkView.setImageResource(R.drawable.icon_statusbar_network_ethernet);
		} else if (ConnectivityManager.TYPE_WIFI == type) {
			mNetWorkView.setImageResource(R.drawable.icon_statusbar_network_wifi);
		} else {
			mNetWorkView.setImageResource(R.drawable.icon_statusbar_network_noconnect);
		}
	}

	public void updateNetWorkState(int type) {
		updateNetworkIcon(type);
	}

	private void initBluetoothState() {
		boolean stateOn = BluetoothController.getBlueToothState();
		updateBluetoothState(stateOn);
	}

	public void updateBluetoothState(boolean state) {
		if (state) {
			mBlueToothView.setVisibility(View.VISIBLE);
		} else {
			mBlueToothView.setVisibility(View.GONE);
		}
	}

	public void showHandle() {
		HandleHolder holder = null;
		for (int pos = 0; pos < mHandles.size(); pos++) {
			holder = mHandles.get(pos);
			if (holder.state != HandleState.NONE) {
				continue;
			}
		}
		if (holder != null) {
			holder.handle.setVisibility(View.VISIBLE);
		}
	}

	public void hideHandle(int pos) {
		HandleHolder holder = mHandles.valueAt(pos);
		if (holder != null) {
			holder.handle.setVisibility(View.GONE);
		}
	}

	public void updateHandleState(int pos, HandleState state) {

	}

	public static class HandleHolder {
		ImageView handle;
		HandleState state;
	}

	public enum HandleState {
		CONNECTED, DISCONNECTED, LOW_BATTERY, NONE
	}
}
