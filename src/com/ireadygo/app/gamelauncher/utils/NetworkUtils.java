package com.ireadygo.app.gamelauncher.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class NetworkUtils {

	/**
	 * 判断WIFI是否打开
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isOpenWifi(final Context ctx) {
		final WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		return wifi.isWifiEnabled();
	}

	public static boolean is3gDataActive(final Context ctx) {
		final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		int state = tm.getDataState();
		switch (state) {
		case TelephonyManager.DATA_CONNECTING:
		case TelephonyManager.DATA_CONNECTED:
			return true;
		case TelephonyManager.DATA_DISCONNECTED:
			return false;
		}
		return false;
	}

	/**
	 * 判断是否处于Wifi网络
	 * 
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = manager.getActiveNetworkInfo();
		return (wifi != null && wifi.getType() == ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * 判断是否处于2G/3G网络
	 * 
	 * @return
	 */
	public static boolean is3GNetConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager.getActiveNetworkInfo();
		return (mobile != null && mobile.getType() == ConnectivityManager.TYPE_MOBILE);
	}

	/**
	 * PING内网地址
	 * 
	 * @param ctx
	 * @param LAN
	 * @return
	 */
	public static boolean ping(final Context ctx, String ip) {
		// 使用Session连接一下服务端，如果能连上表示OK
		URL url = null;
		int code = 0;
		try {
			url = new URL(ip);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3 * 1000);
			conn.connect();
			code = conn.getResponseCode();
		} catch (Exception e1) {
			return false;
		}
		return code != 0;
	}

	/**
	 * 获取AndroidID
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getAndroidID(final Context ctx) {
		String androidID = "";
		if (ctx != null) {
			androidID = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
		}
		return androidID;
	}

	public static boolean detect(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}

		return true;
	}

	public static String doGetRequest(String urlStr, HashMap<String, String> params) throws ClientProtocolException,
			IOException {
		StringBuilder urlBuilder = new StringBuilder(urlStr);
		if (params != null && !params.isEmpty()) {
			urlBuilder.append("?").append(encodeUrl(params));
		}
		HttpGet httpGet = new HttpGet(urlBuilder.toString());
		HttpResponse response = new DefaultHttpClient().execute(httpGet);
		if (response.getStatusLine().getStatusCode() == 200) {
			String result = EntityUtils.toString(response.getEntity());
			return result;
		}
		return "";

	}

	public static String encodeUrl(Map<String, String> param) {
		if (param == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		Set<String> keys = param.keySet();
		boolean first = true;

		for (String key : keys) {
			String value = param.get(key);
			if (!TextUtils.isEmpty(value)) {
				if (first)
					first = false;
				else
					sb.append("&");
				try {
					sb.append(URLEncoder.encode(key, "UTF-8")).append("=")
							.append(URLEncoder.encode(param.get(key), "UTF-8"));
				} catch (UnsupportedEncodingException e) {

				}
			}

		}

		return sb.toString();
	}

	public static boolean isNetworkConnected(Context context) {
		NetworkInfo ni = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		return ni != null && ni.getState() == State.CONNECTED;
	}

	public static int getNetWorkType(Context context) {
		NetworkInfo ni = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (isNetworkConnected(context)) {
			return ni.getType();
		}
		return -1;
	}
}
