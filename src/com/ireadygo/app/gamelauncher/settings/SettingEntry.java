package com.ireadygo.app.gamelauncher.settings;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.GridView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.settings.SwitcherAdapter.SwitcherItem;

public class SettingEntry {
	private static final String ACTION_DATA_STATE_CHANGE = "android.intent.action.ANY_DATA_STATE";

	private TwoStateSwitcher mWiFiSwitcher;
	private TwoStateSwitcher mBtSwitcher;
	private TwoStateSwitcher mGpsSwitcher;
	private TwoStateSwitcher mDataConnectionSwitcher;
	private TwoStateSwitcher mAirplaneSwitcher;
	private JumperSwitcher mBatterySwitcher;
	private JumperSwitcher mDataUsageSwitcher;
	private JumperSwitcher mSettingsSwitcher;
	private MultiStateSwitcher mTimeoutSwitcher;
	private MultiStateSwitcher mOrientationSwitcher;
	private MultiStateSwitcher mSceneSwitcher;
	private MultiStateSwitcher mBrightnessSwitcher;
	private Context mContext;

	public SettingEntry(GridView view) {
		mContext = GameLauncherApplication.getApplication();
		mWiFiSwitcher = new WiFiSwitcher(mContext, Switcher.ID_WIFI);
		mAirplaneSwitcher = new AirPlaneModeSwitcher(mContext, Switcher.ID_AIRPLANE);
		mBtSwitcher = new BTSwitcher(mContext, Switcher.ID_BT);
		mBatterySwitcher = new BatterySwitcher(mContext, Switcher.ID_BATTERY);
		mDataUsageSwitcher = new DataUsageSwitcher(mContext, Switcher.ID_DATA_USAGE);
		mTimeoutSwitcher = new TimeoutSwitcher(mContext, Switcher.ID_TIME_OUT);
		mOrientationSwitcher = new OrientationSwitcher(mContext, Switcher.ID_ORIENTATION);
		mGpsSwitcher = new GPSSwitcher(mContext, Switcher.ID_GPS);
		mDataConnectionSwitcher = new DataConnectionSwitcher(mContext, Switcher.ID_DATA);
		mSceneSwitcher = new SceneSwitcher(mContext, Switcher.ID_SCENE);
		mBrightnessSwitcher = new BrightnessSwitcher(mContext, Switcher.ID_BRIGTHNESS);
		mSettingsSwitcher = new SettingsSwitcher(mContext, Switcher.ID_SETTINGS);
		initSwitchState(view);

		mContext.registerReceiver(mReceiver, getIntentFilter());
	}

	private IntentFilter getIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		intentFilter.addAction(ACTION_DATA_STATE_CHANGE);
		return intentFilter;
	}


	public void initSwitchState(GridView view) {
		mWiFiSwitcher.init(view);
		mAirplaneSwitcher.init(view);
		mBtSwitcher.init(view);
		mTimeoutSwitcher.init(view);
		mOrientationSwitcher.init(view);
		mGpsSwitcher.init(view);
		mDataConnectionSwitcher.init(view);
		mSceneSwitcher.init(view);
		mBrightnessSwitcher.init(view);
		mBatterySwitcher.init(view);
	}

	public void onClick(int switchId) {
		switch (switchId) {
		case Switcher.ID_WIFI:
			mWiFiSwitcher.toggleState();
			break;
		case Switcher.ID_AIRPLANE:
			mAirplaneSwitcher.toggleState();
			break;
		case Switcher.ID_BT:
			mBtSwitcher.toggleState();
			break;
		case Switcher.ID_BATTERY:
			mBatterySwitcher.toggleState();
			break;
		case Switcher.ID_DATA_USAGE:
			mDataUsageSwitcher.toggleState();
			break;
		case Switcher.ID_TIME_OUT:
			mTimeoutSwitcher.toggleState();
			break;
		case Switcher.ID_ORIENTATION:
			mOrientationSwitcher.toggleState();
			break;
		case Switcher.ID_GPS:
			mGpsSwitcher.toggleState();
			break;
		case Switcher.ID_DATA:
			mDataConnectionSwitcher.toggleState();
			break;
		case Switcher.ID_SCENE:
			mSceneSwitcher.toggleState();
			break;
		case Switcher.ID_BRIGTHNESS:
			mBrightnessSwitcher.toggleState();
			break;
		case Switcher.ID_SETTINGS:
			mSettingsSwitcher.toggleState();
			break;
		default:
			break;
		}
	}

	public void onLongClick(int switchId) {
		switch (switchId) {
		case Switcher.ID_WIFI:
			mWiFiSwitcher.jumpToSettings();
			break;
		case Switcher.ID_AIRPLANE:
			mAirplaneSwitcher.jumpToSettings();
			break;
		case Switcher.ID_BT:
			mBtSwitcher.jumpToSettings();
			break;
		case Switcher.ID_BRIGTHNESS:
			mBrightnessSwitcher.jumpToSettings();
			break;
		case Switcher.ID_SCENE:
			mSceneSwitcher.jumpToSettings();
			break;
		case Switcher.ID_TIME_OUT:
			mTimeoutSwitcher.jumpToSettings();
			break;
		case Switcher.ID_DATA:
			mDataConnectionSwitcher.jumpToSettings();
			break;
		case Switcher.ID_GPS:
			mGpsSwitcher.jumpToSettings();
			break;
		default:
			break;
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				mWiFiSwitcher.setActualState(intent);
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				mBtSwitcher.setActualState(intent);
			} else if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
			} else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				int status = intent.getIntExtra("status", 0); // 电池状态
				int level = intent.getIntExtra("level", 0); // 电池的电量，数字
				mBatterySwitcher.updateIconView(status, level);
			} else if (ACTION_DATA_STATE_CHANGE.equals(action)) {
				mDataConnectionSwitcher.setActualState(intent);
			}
		}
	};



	public class WiFiSwitcher extends TwoStateSwitcher {

		private WifiManager mWifiManager;
		public WiFiSwitcher(Context context,int id) {
			super(context,id);
			mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			initJumpSwitcher();
		}

		private void initJumpSwitcher() {
			Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mJumperSwitcher = new JumperSwitcher(mContext,Switcher.ID_WIFI,intent);
		}

		@Override
		protected void updateStateChange() {
			switch (mCurState) {
			case STATE_DISABLE:
				mWifiManager.setWifiEnabled(true);
				break;
			case STATE_ENABLE:
				mWifiManager.setWifiEnabled(false);
			case STATE_INTERMEDIATE:
				break;
			default:
				break;
			}
		}

		@Override
		protected int getEnableImage() {
			return R.drawable.settings_wifi_enabled;
		}

		@Override
		protected int getDisableImage() {
			return R.drawable.settings_wifi_diabled;
		}

		@Override
		protected void setActualState(Intent intent) {
			if (!WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
				return;
			}
			if (null == mSwitcherAdapter) {
				return;
			}
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
			mCurState = wifiStateToThreeState(wifiState);
			updateIconView();
		}

		private int wifiStateToThreeState(int wifiState) {
			switch (wifiState) {
			case WifiManager.WIFI_STATE_DISABLED:
				return STATE_DISABLE;
			case WifiManager.WIFI_STATE_ENABLED:
				return STATE_ENABLE;
			case WifiManager.WIFI_STATE_DISABLING:
			case WifiManager.WIFI_STATE_ENABLING:
				return STATE_INTERMEDIATE;
			default:
				return STATE_DISABLE;
			}
		}

		@Override
		protected int getInterMedateImage() {
			return R.drawable.settings_wifi_animator_list;
		}

		@Override
		protected int getActualState() {
			if (mWifiManager != null) {
				return wifiStateToThreeState(mWifiManager.getWifiState());
			}
			return STATE_DISABLE;
		}
	}




	public class BTSwitcher extends TwoStateSwitcher {

		public BTSwitcher(Context context,int id) {
			super(context,id);
			initJumpSwitcher();
		}


		@Override
		protected void updateStateChange() {
			final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (bluetoothAdapter == null) {
				return;
			}
			// / M: Actually request the Bluetooth change and persistent
			// settings write off the UI thread, as it can take a
			// / user-noticeable amount of time, especially if there's disk
			// contention.
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... args) {
					switch (mCurState) {
					case STATE_DISABLE:
						bluetoothAdapter.enable();
						break;
					case STATE_ENABLE:
						bluetoothAdapter.disable();
					case STATE_INTERMEDIATE:
						break;
					default:
						break;
					}
					return null;
				}
			}.execute();

		}

		private void initJumpSwitcher() {
			Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mJumperSwitcher = new JumperSwitcher(mContext, Switcher.ID_BT, intent);
		}

		@Override
		protected int getEnableImage() {
			return R.drawable.settings_bt_enabled;
		}

		@Override
		protected int getDisableImage() {
			return R.drawable.settings_bt_disabled;
		}

		@Override
		protected void setActualState(Intent intent) {
			if (!BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
				return;
			}
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
			mCurState = btStateToThreeState(state);
			updateIconView();
		}

		private int btStateToThreeState(int state) {
			switch (state) {
			case BluetoothAdapter.STATE_OFF:
				return STATE_DISABLE;
			case BluetoothAdapter.STATE_ON:
				return STATE_ENABLE;
			case BluetoothAdapter.STATE_TURNING_ON:
			case BluetoothAdapter.STATE_TURNING_OFF:
				return STATE_INTERMEDIATE;
			default:
				return STATE_DISABLE;
			}
		}

		@Override
		protected int getInterMedateImage() {
			return R.drawable.settings_bluetooth_animator_list;
		}

		@Override
		protected int getActualState() {
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (bluetoothAdapter == null) {
				return STATE_DISABLE;
			}
			return btStateToThreeState(bluetoothAdapter.getState());
		}
	}



	public class DataConnectionSwitcher extends TwoStateSwitcher {

		private static final String DATA_CONNECTION_SIM_ID = "gprs_connection_sim_setting";
		private static final int MSG_ANIMATOR_TIMEOUT = 100;
		private static final long ANIMATOR_TIMEOUT_MS = 5000;
		private ConnectivityManager mConnectivityManager;
		private TelephonyManager mTelephonyManager;
		private int[] mMobileIconResIds = new int[4];
		private ContentResolver mContentResolver;

		public DataConnectionSwitcher(Context context,int id) {
			super(context,id);
			mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			mTelephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			mContentResolver = mContext.getContentResolver();
			initDataConnectionIcon();
			initJumpSwitcher();
		}

		private void initJumpSwitcher() {
			Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mJumperSwitcher = new JumperSwitcher(mContext, Switcher.ID_DATA, intent);
		}

		private void initDataConnectionIcon() {
			mMobileIconResIds[0] = R.drawable.settings_data_connection_blue;
			mMobileIconResIds[1] = R.drawable.settings_data_connection_orange;
			mMobileIconResIds[2] = R.drawable.settings_data_connection_green;
			mMobileIconResIds[3] = R.drawable.settings_data_connection_purple;
		}


		@Override
		protected void updateStateChange() {
			if (!isSimCardValid() || isAirPlaneMode()) {
				return;
			}
			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... args) {
					boolean enabled = isDataConnectionEnable();
					setDataConnectionEnable(!enabled);
					return null;
				}
				protected void onPostExecute(Boolean result) {
					updateIconView(STATE_INTERMEDIATE);
					if (!mHandler.hasMessages(MSG_ANIMATOR_TIMEOUT)) {
						Message msg = mHandler.obtainMessage(MSG_ANIMATOR_TIMEOUT);
						mHandler.sendMessageDelayed(msg, ANIMATOR_TIMEOUT_MS);
					}
				};
			}.execute();
		}

		private Handler mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case MSG_ANIMATOR_TIMEOUT:
					updateIconView(STATE_DISABLE);
					break;

				default:
					break;
				}
			};
		};

		@Override
		protected int getEnableImage() {
//			return mMobileIconResIds[(int)getSimId()-1];
			return R.drawable.settings_data_connection_white;
		}

		@Override
		protected int getDisableImage() {
			return R.drawable.settings_data_connection_disabled;
		}

		@Override
		protected void setActualState(Intent intent) {
			if (mHandler.hasMessages(MSG_ANIMATOR_TIMEOUT)) {
				mHandler.removeMessages(MSG_ANIMATOR_TIMEOUT);
			}
			updateIconView();
		}


		@Override
		protected int getInterMedateImage() {
			return R.drawable.settings_data_connection_animator_list;
		}

		@Override
		protected int getActualState() {
			int state = mTelephonyManager.getDataState();

			switch (state) {
			case TelephonyManager.DATA_CONNECTING:
				return STATE_INTERMEDIATE;
			case TelephonyManager.DATA_CONNECTED:
				return STATE_ENABLE;
			case TelephonyManager.DATA_DISCONNECTED:
				return STATE_DISABLE;
			default:
				return STATE_DISABLE;
			}
		}

		private boolean isDataConnectionEnable() {
			try {
				Method getMobileDataEnabled = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
				return (Boolean)getMobileDataEnabled.invoke(mConnectivityManager);
			} catch (NoSuchMethodException e) {
				return false;
			} catch (IllegalAccessException e) {
				return false;
			} catch (IllegalArgumentException e) {
				return false;
			} catch (InvocationTargetException e) {
				return false;
			}
		}

		private void setDataConnectionEnable(boolean enable) {
			try {
				Method setMobileDataEnabled = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled",boolean.class);
				setMobileDataEnabled.invoke(mConnectivityManager,enable);
			} catch (NoSuchMethodException e) {
				return;
			} catch (IllegalAccessException e) {
				return;
			} catch (IllegalArgumentException e) {
				return;
			} catch (InvocationTargetException e) {
				return;
			}
		}


		/*
		 * 现在无法读取实时的sim卡对应的颜色，这能读取sim卡默认对应的颜色
		 */
		private long getSimId() {
				try {
					long simId = Settings.System.getLong(mContentResolver, DATA_CONNECTION_SIM_ID);
					return simId;
				} catch (SettingNotFoundException e) {
					e.printStackTrace();
				}
//				Class<?> simInfoManagerCls = Class.forName("com.mediatek.telephony.SimInfoManager");
//				Class<?> simInfoRecordCls = Class.forName("com.mediatek.telephony.SimInfoManager$SimInfoRecord");
//				Method getSimInfoById = simInfoManagerCls.getMethod("getSimInfoById", String.class);
//				Object simInfoRecordObject = simInfoRecordCls.newInstance();
//				simInfoRecordObject = getSimInfoById.invoke(simInfoManagerCls, simId);
//				Field color = simInfoRecordCls.getField("mColor");
//				return (Integer)color.get(simInfoRecordObject);
//				Class<?> simHelper = Class.forName("com.android.systemui.statusbar.util.SIMHelper");
//				Class<?> simInfoRecordCls = Class.forName("com.mediatek.telephony.SimInfoManager$SimInfoRecord");
//				Method getSIMInfo = simHelper.getMethod("getSIMInfo", long.class);
//				Object simInfoRecordObject = simInfoRecordCls.newInstance();
//				simInfoRecordObject = getSIMInfo.invoke(simHelper, simId);
//				Field color = simInfoRecordCls.getField("mColor");
//				return (Integer)color.get(simInfoRecordObject);
//			} catch (SettingNotFoundException e) {
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				Log.e("lilu", ""+e);
//				e.printStackTrace();
//			} catch (NoSuchMethodException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				e.printStackTrace();
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (NoSuchFieldException e) {
//				e.printStackTrace();
//			}
			return -1;
		}

		private boolean isSimCardValid() {
			int state = mTelephonyManager.getSimState();
			if (TelephonyManager.SIM_STATE_ABSENT == state
					|| TelephonyManager.SIM_STATE_UNKNOWN == state) {
				return false;
			}
			return true;
		}

		@SuppressLint("NewApi")
		private boolean isAirPlaneMode() {
			return Settings.Global.getInt(mContext.getContentResolver(),
					Settings.Global.AIRPLANE_MODE_ON, 0) == 1 ? true : false;
		}
	}


	public class AirPlaneModeSwitcher extends TwoStateSwitcher {

		public AirPlaneModeSwitcher(Context context,int id) {
			super(context,id);
			initJumpSwitcher();
		}

		private void initJumpSwitcher() {
			Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mJumperSwitcher = new JumperSwitcher(mContext, Switcher.ID_AIRPLANE,intent);
		}

		@SuppressLint("NewApi")
		@Override
		protected void updateStateChange() {
			boolean setAirPlane = (mCurState == STATE_ENABLE) ? false : true;
			Settings.Global
					.putInt(mContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, setAirPlane ? 1 : 0);
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
			intent.putExtra("state", !setAirPlane);
			mContext.sendBroadcast(intent);
		}

		@Override
		protected int getEnableImage() {
			return R.drawable.settings_air_mode_enabled;
		}

		@Override
		protected int getDisableImage() {
			return R.drawable.settings_air_mode_disabled;
		}

		@Override
		protected void setActualState(Intent intent) {
			if (intent == null) {
				return;
			}
			boolean enabled = intent.getBooleanExtra("state", false);
			mCurState = enabled ? STATE_ENABLE : STATE_DISABLE;
			updateIconView();
		}

		@Override
		protected int getInterMedateImage() {
			return 0;
		}

		@SuppressLint("NewApi")
		@Override
		protected int getActualState() {
			boolean AirPlaneModeOn = Settings.Global.getInt(mContext.getContentResolver(),
					Settings.Global.AIRPLANE_MODE_ON, 0) == 1 ? true : false;
			return AirPlaneModeOn ? STATE_ENABLE : STATE_DISABLE;
		}
	}

	public class GPSSwitcher extends TwoStateSwitcher {

		private ContentResolver mContentResolver;

		public GPSSwitcher(Context context,int id) {
			super(context,id);
			mContentResolver = context.getContentResolver();
			initJumpSwitcher();
		}

		private void initJumpSwitcher() {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mJumperSwitcher = new JumperSwitcher(mContext, Switcher.ID_GPS, intent);
		}


		@SuppressLint("NewApi")
		@Override
		protected void updateStateChange() {
			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... args) {
					final boolean enable = (getActualState() == STATE_ENABLE) ? false : true;
					Settings.Secure.setLocationProviderEnabled(mContentResolver, LocationManager.GPS_PROVIDER, enable);
					return null;
				}

				@Override
				protected void onPostExecute(Boolean result) {
					updateIconView();
				}
			}.execute();
		}

		@Override
		protected int getEnableImage() {
			return R.drawable.settings_gps_enabled;
		}

		@Override
		protected int getDisableImage() {
			return R.drawable.settings_gps_disabled;
		}

		@Override
		protected void setActualState(Intent intent) {
		}

		@Override
		protected int getInterMedateImage() {
			return 0;
		}

		@Override
		protected int getActualState() {
			boolean on = Settings.Secure.isLocationProviderEnabled(mContentResolver, LocationManager.GPS_PROVIDER);
			return on ? STATE_ENABLE : STATE_DISABLE;
		}
	}


	/*
	 * Jumper switcher
	 */

	public class BatterySwitcher extends JumperSwitcher {
		private int mLastBatteryState = -1;
		private int mLastBatteryLevel = -1;

		public BatterySwitcher(Context context,int switchId) {
			super(context,switchId);
			mJumpIntent = new Intent();
			mJumpIntent.setClassName("com.android.settings",
					"com.android.settings.Settings$PowerUsageSummaryActivity");
			mJumpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		@Override
		protected void updateIconView(int state, int level) {
			int iconId = getImageResId(state, level);
			String title = getTitleString(state, level);
			SwitcherItem switcher = (SwitcherItem)mSwitcherAdapter.getItem(getSwitcherId());
			if (0 != iconId) {
				switcher.setSwitcherIconId(iconId);
			}
			if (!TextUtils.isEmpty(title)) {
				switcher.setSwitcherTitle(title);
			}
			mSwitcherAdapter.getView(getSwitcherId(), mGridView.getChildAt(getSwitcherId()), null);
		}

		protected int getImageResId(int state, int level) {
			if ((mLastBatteryState == state) 
					&& (mLastBatteryLevel == level)) {
				return 0;
			}
			if (BatteryManager.BATTERY_STATUS_CHARGING == state) {
				mLastBatteryState = state;
				if (level <= 15) {
					return R.drawable.settings_battery_charge_10;
				} else if (level <= 35) {
					return R.drawable.settings_battery_charge_20;
				} else if (level <= 55) {
					return R.drawable.settings_battery_charge_40;
				} else if (level <= 85) {
					return R.drawable.settings_battery_charge_60;
				} else if (level < 100) {
					return R.drawable.settings_battery_charge_90;
				} else {
					return R.drawable.settings_battery_charge_100;
				}
			} else if (BatteryManager.BATTERY_STATUS_NOT_CHARGING == state
					|| BatteryManager.BATTERY_STATUS_FULL == state) {
				mLastBatteryState = state;
				if (level <= 15) {
					return R.drawable.settings_battery_10;
				} else if (level <= 35) {
					return R.drawable.settings_battery_20;
				} else if (level <= 55) {
					return R.drawable.settings_battery_40;
				} else if (level <= 85) {
					return R.drawable.settings_battery_60;
				} else if (level < 100) {
					return R.drawable.settings_battery_90;
				} else {
					return R.drawable.settings_battery_100;
				}
			}
			return 0;
		}

		protected String getTitleString(int state, int level) {
			if ((mLastBatteryState == state)
					&& (mLastBatteryLevel == level)) {
				return null;
			}
			if (BatteryManager.BATTERY_STATUS_CHARGING == state) {
				String result = String.format(mContext.getString(R.string.setting_battery_charge_level), level);
				return result + "%";
			} else if (BatteryManager.BATTERY_STATUS_NOT_CHARGING == state
					|| BatteryManager.BATTERY_STATUS_FULL == state) {
				String result = String.format(mContext.getString(R.string.setting_battery_level), level);
				return result + "%";
			}
			return null;
		}
	}

	public class DataUsageSwitcher extends JumperSwitcher {

		public DataUsageSwitcher(Context context, int id) {
			super(context, id);
			mJumpIntent = new Intent();
			mJumpIntent.setClassName("com.android.settings",
					"com.android.settings.Settings$DataUsageSummaryActivity");
			mJumpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
	}

	public class SettingsSwitcher extends JumperSwitcher {

		public SettingsSwitcher(Context context, int id) {
			super(context, id);
			mJumpIntent = new Intent();
			mJumpIntent.setClassName("com.android.settings",
					"com.android.settings.Settings");
			mJumpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
	}


	/*
	 * multi-state switcher
	 * */
	public class TimeoutSwitcher extends MultiStateSwitcher {
		private static final int STATE_MINIMUM_TIMEOUT = 15000;
		private static final int STATE_MEDIUM_TIMEOUT = 30000;
		private static final int STATE_MAXIMUM_TIMEOUT = 60000;

		private ContentResolver mContentResolver;

		public TimeoutSwitcher(Context context, int id) {
			super(context, id);
			mContentResolver = mContext.getContentResolver();
			initJumpSwitcher();
		}

		@Override
		protected int getActualState() {
			try {
				int timeout = Settings.System.getInt(mContentResolver, SCREEN_OFF_TIMEOUT);
				if (timeout <= STATE_MINIMUM_TIMEOUT) {
					return STATE_MINIMUM_TIMEOUT;
				} else if (timeout <= STATE_MEDIUM_TIMEOUT) {
					return STATE_MEDIUM_TIMEOUT;
				} else {
					return STATE_MAXIMUM_TIMEOUT;
				}
			} catch (SettingNotFoundException e) {
				return STATE_MAXIMUM_TIMEOUT;
			}
		}

		private void initJumpSwitcher() {
			Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mJumperSwitcher = new JumperSwitcher(mContext, Switcher.ID_TIME_OUT, intent);
		}

		@Override
		protected void updateStateChange(int newState) {
			Settings.System.putInt(mContentResolver, SCREEN_OFF_TIMEOUT, newState);
			updateIconView();
			Toast.makeText(mContext, String.format(mContext.getString(R.string.setting_lcd_timeout),newState/1000),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		protected int getStateImage(int state) {
			switch (state) {
			case STATE_MINIMUM_TIMEOUT:
				return R.drawable.settings_timeout_min;
			case STATE_MEDIUM_TIMEOUT:
				return R.drawable.settings_timeout_med;
			case STATE_MAXIMUM_TIMEOUT:
				return R.drawable.settings_timeout_max;
			default:
				return R.drawable.settings_timeout_min;
			}
		}

		@Override
		protected void initStateList() {
			mStateList.add(STATE_MINIMUM_TIMEOUT);
			mStateList.add(STATE_MEDIUM_TIMEOUT);
			mStateList.add(STATE_MAXIMUM_TIMEOUT);
		}

		@Override
		protected int getStateDescription(int state) {
			return R.string.setting_timeout_title;
		}
	}

	public class OrientationSwitcher extends MultiStateSwitcher {
		private static final int STATE_AUTO_ROTATION = 0;
		private static final int STATE_VERTICAL = 1;
		private static final int STATE_HORIZONTAL = 2;

		private ContentResolver mContentResolver;

		public OrientationSwitcher(Context context, int id) {
			super(context, id);
			mContentResolver = mContext.getContentResolver();
		}

		@Override
		protected int getActualState() {
			int state = Settings.System.getInt(mContentResolver, Settings.System.ACCELEROMETER_ROTATION, -1);
			if (state == 1) {
				return STATE_AUTO_ROTATION;
			} else if (state == 0) {
				state = Settings.System.getInt(mContentResolver, Settings.System.USER_ROTATION, 0);
				if (state == 0) {
					return STATE_VERTICAL;
				} else {
					return STATE_HORIZONTAL;
				}
			} else {
				return STATE_AUTO_ROTATION;
			}
		}

		@Override
		protected void updateStateChange(final int newState) {
			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... args) {
					switch (newState) {
					case STATE_AUTO_ROTATION:
						Settings.System.putInt(mContentResolver, Settings.System.ACCELEROMETER_ROTATION, 1);
						break;
					case STATE_HORIZONTAL:
						Settings.System.putInt(mContentResolver, Settings.System.ACCELEROMETER_ROTATION, 0);
						Settings.System.putInt(mContentResolver, Settings.System.USER_ROTATION, 1);
						break;
					case STATE_VERTICAL:
						Settings.System.putInt(mContentResolver, Settings.System.ACCELEROMETER_ROTATION, 0);
						Settings.System.putInt(mContentResolver, Settings.System.USER_ROTATION, 0);
						break;
					default:
						break;
					}
					return null;
				}
				@Override
				protected void onPostExecute(Boolean result) {
					updateIconView();
				}
			}.execute();
		}

		@Override
		protected int getStateImage(int state) {
			switch (state) {
			case STATE_AUTO_ROTATION:
				return R.drawable.settings_auto_rotation_enabled;
			case STATE_HORIZONTAL:
				return R.drawable.settings_auto_rotation_horizontal;
			case STATE_VERTICAL:
				return R.drawable.settings_auto_rotation_vertical;
			default:
				return R.drawable.settings_auto_rotation_enabled;
			}
		}

		@Override
		protected void initStateList() {
			mStateList.add(STATE_AUTO_ROTATION);
			mStateList.add(STATE_VERTICAL);
			mStateList.add(STATE_HORIZONTAL);
		}

		@Override
		protected int getStateDescription(int state) {
			switch (state) {
			case STATE_AUTO_ROTATION:
				return R.string.setting_auto_rotation_title;
			case STATE_HORIZONTAL:
				return R.string.setting_auto_rotation_horizontal_title;
			case STATE_VERTICAL:
				return R.string.setting_auto_rotation_vetical_title;
			default:
				return R.string.setting_auto_rotation_title;
			}
		}
	}

	public static class SceneSwitcher extends MultiStateSwitcher {
		private static final int STATE_NORMAL = 0;
		private static final int STATE_OUTDOOR = 1;
		private static final int STATE_SILENT = 2;
		private static final int STATE_MEETING = 3;
		private static String sKeyNormal;
		private static String sKeyOutdoor;
		private static String sKeySilent;
		private static String sKeyMeeting;
		static {
			try {
				Class<?> clsManager = Class.forName("com.mediatek.audioprofile.AudioProfileManager");
				Class<?> clsScenario = Class.forName("com.mediatek.audioprofile.AudioProfileManager$Scenario");
				Field meeting = clsScenario.getField("MEETING");
				Field silent = clsScenario.getField("SILENT");
				Field outdoor = clsScenario.getField("OUTDOOR");
				Field normal = clsScenario.getField("GENERAL");
				Method getProfileKey = clsManager.getDeclaredMethod("getProfileKey", clsScenario);
				sKeyMeeting = (String)getProfileKey.invoke(clsManager, meeting.get(clsScenario));
				sKeyNormal = (String)getProfileKey.invoke(clsManager, normal.get(clsScenario));
				sKeyOutdoor = (String)getProfileKey.invoke(clsManager, outdoor.get(clsScenario));
				sKeySilent = (String)getProfileKey.invoke(clsManager, silent.get(clsScenario));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}


		public SceneSwitcher(Context context, int id) {
			super(context, id);
			initJumpSwitcher();
		}

		private void initJumpSwitcher() {
			Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mJumperSwitcher = new JumperSwitcher(mContext, Switcher.ID_SCENE, intent);
		}

		@Override
		protected int getActualState() {
			String active = getActiveScenario();
			if(TextUtils.isEmpty(active)) {
				return STATE_NORMAL;
			}
			if (sKeyMeeting.equals(active)) {
				return STATE_MEETING;
			} else if (sKeyNormal.equals(active)) {
				return STATE_NORMAL;
			} else if (sKeyOutdoor.equals(active)) {
				return STATE_OUTDOOR;
			} else if (sKeySilent.equals(active)) {
				return STATE_SILENT;
			}
			return STATE_NORMAL;
		}

		@Override
		protected void updateStateChange(final int newState) {
			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... args) {
					switch (newState) {
					case STATE_MEETING:
						setScenario(sKeyMeeting);
						break;
					case STATE_NORMAL:
						setScenario(sKeyNormal);
						break;
					case STATE_OUTDOOR:
						setScenario(sKeyOutdoor);
						break;
					case STATE_SILENT:
						setScenario(sKeySilent);
						break;
					default:
						break;
					}
					return null;
				}
				@Override
				protected void onPostExecute(Boolean result) {
					updateIconView();
				}
			}.execute();
		}

		private void setScenario(String key) {
			try {
				Class clsManager = Class.forName("com.mediatek.audioprofile.AudioProfileManager");
				Method setActiveProfile = clsManager.getDeclaredMethod("setActiveProfile", String.class);
				Class[] paramTypes = {Context.class };
				Object[] params = {mContext};
				Constructor con = clsManager.getConstructor(paramTypes);
				setActiveProfile.invoke(con.newInstance(params), key);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}

		private String getActiveScenario() {
			try {
				Class clsManager = Class.forName("com.mediatek.audioprofile.AudioProfileManager");
				Method getActiveProfileKey = clsManager.getDeclaredMethod("getActiveProfileKey");
				Class[] paramTypes = {Context.class };
				Object[] params = {mContext};
				Constructor con = clsManager.getConstructor(paramTypes);
				return (String)getActiveProfileKey.invoke(con.newInstance(params));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected int getStateImage(int state) {
			switch (state) {
			case STATE_MEETING:
				return R.drawable.settings_scene_meeting;
			case STATE_NORMAL:
				return R.drawable.settings_scene_normal;
			case STATE_OUTDOOR:
				return R.drawable.settings_scene_outdoor;
			case STATE_SILENT:
				return R.drawable.settings_scene_silent;
			default:
				return R.drawable.settings_scene_normal;
			}
		}

		@Override
		protected void initStateList() {
			mStateList.add(STATE_NORMAL);
			mStateList.add(STATE_SILENT);
			mStateList.add(STATE_MEETING);
			mStateList.add(STATE_OUTDOOR);
		}

		@Override
		protected int getStateDescription(int state) {
			switch (state) {
			case STATE_MEETING:
				return R.string.setting_scene_meeting;
			case STATE_NORMAL:
				return R.string.setting_scene_normal;
			case STATE_OUTDOOR:
				return R.string.setting_scene_outdoor;
			case STATE_SILENT:
				return R.string.setting_scene_silent;
			default:
				return R.string.setting_scene_normal;
			}
		}
	}

	public class BrightnessSwitcher extends MultiStateSwitcher {
		private static final int STATE_AUTO_BRIGHTNESS = 0;
		private static final int STATE_MAX_BRIGHTNESS = 1;
		private static final int STATE_MED_BRIGHTNESS = 2;
		private static final int STATE_MIN_BRIGHTNESS = 3;

		private static final int BRIGHTNESS_IN_ERROR = 30;//出现异常情况时设置的亮度值，避免设置0亮度导致LCD灭屏

		private ContentResolver mContentResolver;
		private PowerManager mPowerManager;
		private int mMaxBrightness;
		private int mMinBrightness;
		private int mMedBrightness;

		public BrightnessSwitcher(Context context, int id) {
			super(context, id);
			mContentResolver = mContext.getContentResolver();
			mPowerManager = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
			mMaxBrightness = getMaxBrightness();
			mMinBrightness = getMinBrightness();
			mMedBrightness = (mMaxBrightness - mMinBrightness) / 2;
			initJumpSwitcher();
		}

		@Override
		protected int getActualState() {
			int automatic;
			try {
				automatic = Settings.System.getInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
			} catch (SettingNotFoundException snfe) {
				automatic = 0;
			}
			if (1 == automatic) {//自动背光调节开启
				return STATE_AUTO_BRIGHTNESS;
			} else {
				try {
					int value = Settings.System.getInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS);
					if (value == mMaxBrightness) {
						return STATE_MAX_BRIGHTNESS;
					} else if (value == mMinBrightness) {
						return STATE_MIN_BRIGHTNESS;
					}
					return STATE_MED_BRIGHTNESS;
				} catch (SettingNotFoundException e) {
					//do nothing
				}
			}
			return STATE_AUTO_BRIGHTNESS;
		}

		private void initJumpSwitcher() {
			Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mJumperSwitcher = new JumperSwitcher(mContext, Switcher.ID_BRIGTHNESS, intent);
		}

		@Override
		protected void updateStateChange(final int newState) {
			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... args) {
					switch (newState) {
					case STATE_AUTO_BRIGHTNESS:
						Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,1);
						break;
					case STATE_MAX_BRIGHTNESS:
						Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,0);
						Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS,mMaxBrightness);
						break;
					case STATE_MED_BRIGHTNESS:
						Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,0);
						Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS,mMedBrightness);
						break;
					case STATE_MIN_BRIGHTNESS:
						Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,0);
						Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS,mMinBrightness);
						break;
					default:
						break;
					}
					return null;
				}
				@Override
				protected void onPostExecute(Boolean result) {
					updateIconView();
				}
			}.execute();
		}

		@Override
		protected int getStateImage(int state) {
			switch (state) {
			case STATE_AUTO_BRIGHTNESS:
				return R.drawable.settings_brightness_auto;
			case STATE_MAX_BRIGHTNESS:
				return R.drawable.settings_brightness_max;
			case STATE_MED_BRIGHTNESS:
				return R.drawable.settings_brightness_med;
			case STATE_MIN_BRIGHTNESS:
				return R.drawable.settings_brightness_min;
			default:
				return R.drawable.settings_brightness_auto;
			}
		}

		@Override
		protected void initStateList() {
			mStateList.add(STATE_AUTO_BRIGHTNESS);
			mStateList.add(STATE_MAX_BRIGHTNESS);
			mStateList.add(STATE_MED_BRIGHTNESS);
			mStateList.add(STATE_MIN_BRIGHTNESS);
		}

		private int getMaxBrightness() {
			try {
				Method getMaximumScreenBrightnessSetting = PowerManager.class.getDeclaredMethod("getMaximumScreenBrightnessSetting");
				return (Integer)getMaximumScreenBrightnessSetting.invoke(mPowerManager);
			} catch (NoSuchMethodException e) {
				//do nothing
			} catch (IllegalAccessException e) {
				//do nothing
			} catch (IllegalArgumentException e) {
				//do nothing
			} catch (InvocationTargetException e) {
				//do nothing
			}
			return BRIGHTNESS_IN_ERROR;
		}

		private int getMinBrightness() {
			try {
				Method getMinimumScreenBrightnessSetting = PowerManager.class.getDeclaredMethod("getMinimumScreenBrightnessSetting");
				return (Integer)getMinimumScreenBrightnessSetting.invoke(mPowerManager);
			} catch (NoSuchMethodException e) {
				//do nothing
			} catch (IllegalAccessException e) {
				//do nothing
			} catch (IllegalArgumentException e) {
				//do nothing
			} catch (InvocationTargetException e) {
				//do nothing
			}
			return BRIGHTNESS_IN_ERROR;
		}

		@Override
		protected int getStateDescription(int state) {
			switch (state) {
			case STATE_AUTO_BRIGHTNESS:
				return R.string.setting_brightness_auto;
			case STATE_MAX_BRIGHTNESS:
				return R.string.setting_brightness_max;
			case STATE_MED_BRIGHTNESS:
				return R.string.setting_brightness_med;
			case STATE_MIN_BRIGHTNESS:
				return R.string.setting_brightness_min;
			default:
				return R.string.setting_brightness_auto;
			}
		}
	}
}
