package com.ireadygo.app.gamelauncher.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;

public class HandleDescriptionActivity extends BaseActivity implements OnClickListener {
	private static final String ACTION_LANGUAGE_SETTINGS = "com.ireadygo.app.wizard.language";
	private static final String HANDLE_NAME_DEFAULT = "OBox Controller";
	private TextView mContinueBtn; 
	private OperationTipsLayout mTipsLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.handle_connect_description);

		mContinueBtn = (TextView) findViewById(R.id.continue_btn);
		mContinueBtn.setOnClickListener(this);

		mTipsLayout = (OperationTipsLayout) findViewById(R.id.operationTipsLayout);
		mTipsLayout.setTipsVisible(View.INVISIBLE, TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction("com.ireadygo.inputdevice.handle");
		registerReceiver(mReceiver, intentFilter);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.continue_btn:
			next();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onBackKey() {
		return true;
	}

	private void next() {
		if (PreferenceUtils.isFirstLaunch()) {
			Intent intent = new Intent(ACTION_LANGUAGE_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			finish();
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device != null && isOBoxController(device.getName())) {
					next();
				}
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

			}
		}
	};

	private boolean isOBoxController(String name) {
		if (name == null) {
			return false;
		}
		if (name.contains(HANDLE_NAME_DEFAULT) || name.equalsIgnoreCase(HANDLE_NAME_DEFAULT)) {
			return true;
		}
		return false;
	}
}
