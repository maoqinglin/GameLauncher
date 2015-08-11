/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ireadygo.app.gamelauncher.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.hardware.input.InputManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.InputEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.download.DownloadException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.install.InstallMessage;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.GameManagerException;
import com.ireadygo.app.gamelauncher.ui.GameLauncherActivity;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.snail.appstore.openapi.json.JSONObject;

/**
 * Class containing some static utility methods.
 */
public class Utils {

	// @TargetApi(11)
	// public static void enableStrictMode() {
	// if (Utils.hasGingerbread()) {
	// StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new
	// StrictMode.ThreadPolicy.Builder()
	// .detectAll().penaltyLog();
	// StrictMode.VmPolicy.Builder vmPolicyBuilder = new
	// StrictMode.VmPolicy.Builder()
	// .detectAll().penaltyLog();
	//
	// if (Utils.hasHoneycomb()) {
	// threadPolicyBuilder.penaltyFlashScreen();
	// vmPolicyBuilder.setClassInstanceLimit(TvPlayRealActivity.class, 1);
	// }
	// StrictMode.setThreadPolicy(threadPolicyBuilder.build());
	// StrictMode.setVmPolicy(vmPolicyBuilder.build());
	// }
	// }

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				versionName = "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versionName;
	}

	public static void forceStopPackage(ActivityManager am, String pkgName) {
		try {
			Method method = am.getClass().getDeclaredMethod("forceStopPackage", String.class);
			method.invoke(am, pkgName);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void killBackgroundProcesses(ActivityManager am, String pkgName) {
		am.killBackgroundProcesses(pkgName);
	}

	public static String getChannleName(Context context) {
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			throw new IllegalStateException();
		}

		String channel = appInfo.metaData.getString("UMENG_CHANNEL");
		return channel;
	}

	/** 获取设备信息，用于友盟的测试设备添加 **/
	public static String getDeviceInfoForUmeng(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String handleException(Context context, GameManagerException e) {
		String msg = e.getMessage();
		if (TextUtils.isEmpty(msg)) {
			return null;
		}
		String result = null;
		if (msg.equals(GameManagerException.MSG_DOWNLOAD_ERROR)) {
			result = handleDownloadError(context, e.getCause());
		} else if (msg.equals(GameManagerException.MSG_INSTALL_FAILED)) {
			result = handleUnOrInstallError(context, e.getCause());
		} else if (msg.equals(GameManagerException.MSG_UNINSTALL_FAILED)) {
			result = handleUnOrInstallError(context, e.getCause());
		} else if (msg.equals(GameManagerException.MSG_MOVE_FAILED)) {

		}
		if (TextUtils.isEmpty(result)) {
			result = context.getString(R.string.error_unknown);
		}
		return result;
	}

	private static String handleDownloadError(Context context, Throwable t) {
		if (t != null) {
			String msg = t.getMessage();
			if (!TextUtils.isEmpty(msg)) {
				int resId = -1;
				if (DownloadException.NETWORK_UNAVAIBLE.equals(msg)) {
					resId = R.string.error_download_network_unavaible;
				} else if (DownloadException.INSUFFICIENT_STORAGE_SPACE.equals(msg)) {
					resId = R.string.error_download_insufficient_storage_space;
				} else if (DownloadException.CAN_NOT_CREATE_DOWNLOAD_PATH.equals(msg)) {
					resId = R.string.error_download_can_not_create_download_path;
				} else if (DownloadException.UNKNOW_ERROR.equals(msg)) {
					resId = R.string.error_download;
				} else if (DownloadException.DOWNLOAD_PATH_CAN_NOT_WRITE.equals(msg)) {
					resId = R.string.error_download_path_can_not_write;
				} else if (DownloadException.SERVER_ERROR.equals(msg)) {
					resId = R.string.error_download_server_error;
				} else if (DownloadException.URL_ERROR.equals(msg)) {
					resId = R.string.error_download_url_error;
				} else if (DownloadException.IO_ERROR.equals(msg)) {
					resId = R.string.error_download_io_error;
				}
				if (resId != -1) {
					return context.getString(resId);
				}
			}
		}
		return context.getString(R.string.error_download);
	}

	private static String handleUnOrInstallError(Context context, Throwable t) {
		if (t != null) {
			String msg = t.getMessage();
			if (!TextUtils.isEmpty(msg)) {
				int resId = -1;
				if (msg.equals(InstallMessage.PACKAGE_NOT_FOUND)) {
					resId = R.string.error_install_package_not_found;
				} else if (msg.equals(InstallMessage.PACKAGE_UNREADABLE)) {
					resId = R.string.error_install_package_unreadable;
				} else if (msg.equals(InstallMessage.INSTALL_SILENTLY_NOT_COMPATIBLE)) {
					resId = R.string.error_install_silently_not_compatible;
				} else if (msg.equals(InstallMessage.INVALID_APK)) {
					resId = R.string.error_install_invalid_apk;
				} else if (msg.equals(InstallMessage.INSUFFICIENT_STORAGE)) {
					resId = R.string.error_install_insufficient_storage;
				} else if (msg.equals(InstallMessage.INCONSISTENT_CERTIFICATES)) {
					resId = R.string.error_install_inconsistent_certificates;
				} else if (msg.equals(InstallMessage.INSTALL_PARSE_FAILED_NO_CERTIFICATES)) {
					resId = R.string.error_install_no_certificates;
				} else if (msg.equals(InstallMessage.UNKNOW_APK_INSTALL_ERROR)) {
					resId = R.string.error_install_unknow_apk;
				} else if (msg.equals(InstallMessage.ERROR_ZIP_PACKAGE)) {
					resId = R.string.error_install_zip_package;
				} else if (msg.equals(InstallMessage.STORAGE_SYSTEM_ERROR)) {
					resId = R.string.error_install_storage_system;
				} else if (msg.equals(InstallMessage.EXTERNAL_STORAGE_UNMOUNTED)) {
					resId = R.string.error_install_external_storage_unmounted;
				} else if (msg.equals(InstallMessage.ILLEGAL_APK_WITH_DATA)) {
					resId = R.string.error_install_illegal_apk_with_data;
				} else if (msg.equals(InstallMessage.UNINSTALL_FAILED_INVALID_PACKAGE)) {
					resId = R.string.error_uninstall_failed_invalid_package;
				} else if (msg.equals(InstallMessage.UNINSTALL_FAILED_INTERNAL_ERROR)) {
					resId = R.string.error_uninstall_failed_internal;
				} else if (msg.equals(InstallMessage.UNINSTALL_FAILED_PERMISSION_DENIED)) {
					resId = R.string.error_uninstall_failed_permission_denied;
				}
				if (resId != -1) {
					return context.getString(resId);
				}
			}
		}
		return context.getString(R.string.error_install);
	}

	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		return p.matcher(email).matches();
	}

	public static ArrayList<AppEntity> createAppList(int size) {
		ArrayList<AppEntity> appList = new ArrayList<AppEntity>();
		for (int i = 0; i < size; i++) {
			AppEntity app = new AppEntity();
			app.setAppId(10000 + i + "");
			app.setName("进击的妖精");
			app.setDescription("无穷想象力的萌趣江湖" + i);
			app.setLocalIconUrl(R.drawable.account_photo_default + "");
			app.setFreeFlag(6);
			appList.add(app);
		}
		return appList;

	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static String getCurrProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static Dialog createLoadingDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.loading_progress_dialog, null);// 得到加载view
		// main.xml中的ImageView
		Dialog loadingDialog = new Dialog(context, R.style.loading_progress_dialog);// 创建自定义样式dialog
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		return loadingDialog;
	}

	public static void setEmptyView(View emptyView, HListView listView) {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		emptyView.setLayoutParams(params);
		emptyView.setVisibility(View.GONE);
		if (emptyView.getParent() == null) {
			((ViewGroup) listView.getParent()).addView(emptyView);
		}
		listView.setEmptyView(emptyView);
	}

	/** 设置自定义字体 **/
	public static void setCustomTypeface(Context context, String path, TextView textView) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), path);
		textView.setTypeface(typeface);
	}

	public static boolean isSoftInputOpen(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.hideSoftInputFromWindow(editText.getWindowToken(), 0)) {
			imm.showSoftInput(editText, 0);
			return true;
		}
		return false;
	}

	/**
	 * 将应用版本号、渠道号、平台号保存到文件
	 */
	public static void saveFreeStoreData(Context context) {
		String data = StorageUtils.readFileSdcard(GameLauncherConfig.SDDATA_FILE_NAME);
		if (TextUtils.isEmpty(data)) {
			try {
				JSONObject obj = new JSONObject(data);
				String channelId = obj.getString(GameLauncherConfig.KEY_CHANNEL_ID);
				String versionName = obj.getString(GameLauncherConfig.KEY_PLATFORM_VERSION);
				if (channelId.equals(GameLauncherConfig.getChennelId())
						&& versionName.equals(getAppVersionName(context)))
					return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		JSONObject saveObj = new JSONObject();
		try {
			saveObj.put(GameLauncherConfig.KEY_CHANNEL_ID, GameLauncherConfig.getChennelId());
			saveObj.put(GameLauncherConfig.KEY_PLATFORM_VERSION, getAppVersionName(context));
			saveObj.put(GameLauncherConfig.KEY_PLATFORM_ID, String.valueOf(GameLauncherConfig.getPlatformId()));
			StorageUtils.writeFileSdcard(GameLauncherConfig.SDDATA_FILE_NAME, saveObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String formatSpeedText(long speed) {
		String speedStr = "";
		int kb = 1024;
		int mb = 1024 * kb;
		if (speed < kb) {
			speedStr = speed + "B/s";
		} else if (speed < mb) {
			speedStr = speed / kb + "KB/s";
		} else {
			speedStr = new DecimalFormat("#.00").format((float) speed / mb) + "MB/s";
		}
		return speedStr;
	}
}
