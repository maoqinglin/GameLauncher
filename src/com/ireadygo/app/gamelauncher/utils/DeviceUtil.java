package com.ireadygo.app.gamelauncher.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class DeviceUtil {

	private static final String PHONE_NUM_PRIFIX = "+86";

	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	public static String getMacAddr(Context context) {
		if(TextUtils.isEmpty(PreferenceUtils.getMacAddr())) {
			WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
			if(info != null) {
				String mac = info.getMacAddress();
				if(!TextUtils.isEmpty(mac)) {
					PreferenceUtils.saveMacAddr(mac);
					return mac;
				}
				return "";
			}
		}
		return PreferenceUtils.getMacAddr();
	}

	public static String getDevicePhoneNum(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceNum = tm.getLine1Number();
		if (!TextUtils.isEmpty(deviceNum) && deviceNum.contains(PHONE_NUM_PRIFIX)) {
			return deviceNum.replace(PHONE_NUM_PRIFIX, "");
		}
		return deviceNum;
	}

	public static int getNetworkIp(Context context) {
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info) {
			return info.getIpAddress();
		}
		return 0;
	}

	public static String getLocalIpAddress() {
		String networkIp = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				Enumeration<InetAddress> enIp = ni.getInetAddresses();
				while (enIp.hasMoreElements()) {
					InetAddress inet = enIp.nextElement();
					if (!inet.isLoopbackAddress() && (inet instanceof Inet4Address)) {
						networkIp = inet.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return networkIp;
	}
}
