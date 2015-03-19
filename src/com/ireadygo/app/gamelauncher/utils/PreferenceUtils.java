package com.ireadygo.app.gamelauncher.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.appstore.info.item.FreeFlowType;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserHeaderImgItem;

public class PreferenceUtils {
	private static final String PREFERECNE_NAME = "APPSTORE_PRF";

	private static final long DEFAULT_LONG = 0;
	private static final int DEFAULT_INT = 0;
	private static final boolean DEFAULT_BOOLEAN = false;
	private static final String DEFAULT_STRING = "";

	public static final String KEY_LAST_CHECK_UPGRADE_TIME = "a";
	public static final String KEY_LAST_INFO_UPGRADE_TIME = "b";

	public static final String KEY_ENABLE_3G_DOWNLOAD = "c";
	public static final String KEY_INSTALLED_DELETE_APK = "d";
	public static final String KEY_AUTO_INSTALL = "e";
	public static final String KEY_IS_FIRST_LAUNCH = "f";
	public static final String KEY_AD_PIC_URL = "g";// 启动画面广告图片下载地址
	public static final String KEY_LAST_CHECK_AD_PIC_URL_TIME = "h";
	public static final String KEY_APP_UPDATE_NOTIFY = "i";
	public static final String KEY_MAP_GAME_COMPLETE = "j";

	public static final String KEY_CATEGORY_EXPIRED_TIME = "k";
	public static final String KEY_COLLECTION_EXPIRED_TIME = "l";
	public static final String KEY_BANNER_EXPIRED_TIME = "m";
	public static final String KEY_KEYWORD_EXPIRED_TIME = "n";
	public static final String KEY_FEECONFIG_EXPIRED_TIME = "o";

	public static final String KEY_SLOT_NUM = "p";
	public static final String KEY_USED_SLOT_NUM = "q";

	// 是否是第一次进入卡槽购买页面
	private static final String IS_SLOT_BUY_PAGE_FIRST = "r";

	// 是否是第一次下载
	private static final String IS_DOWNLOAD_FIRST = "s";

	// 是否有可更新的应用
	private static final String HAS_UPDATABLE = "t";

	// 头像Url列表缓存
	private static final String USER_HEADER_IMG_URL_LIST = "u";

	// 头像Url列表缓存过期时间
	private static final String HEADER_IMG_EXPIRED_MILLS = "v";

	
	public static final String KEY_LAST_SYNC_SLOT_TIME = "y";
	public static final String KEY_NOTIFICATION_SLOT_LAST_TIME = "x";

	public static final String KEY_FIRST_USED_SLOT = "z";
	// 记录MAC地址
	public static final String KEY_MAC_ADDRESS = "aa";
	// 首次使用快速充值绑定

	public static final String KEY_IS_BSS_ACCOUNT = "ab";

	public static final String KEY_RABBIT_COIN_BALANCE = "ac";

	public static final String KEY_BIND_PHONE_NUM = "ad";

	public static final String KEY_IS_FIRST_USED_BIND_PHONE = "ae";

	public static final String KEY_RABBIT_TICKET_BALANCE = "af";

	public static final String KEY_TICKET_PHONE_NUM = "ag";

	public static final String KEY_APP_ONLINE_DOWNLOAD = "ah";
	public static final String KEY_FREE_FLOW_DOMAIN = "ai";
	public static final String KEY_FREE_FLOW_NOT_SUPPORT_NUM = "aj";
	public static final String KEY_FREE_FLOW_BIND_PHONE_NUM = "ak";
	public static final String KEY_FREE_FLOW_MODE = "al";
	public static final String KEY_OBOX_TYPE = "am";
	public static final String KEY_DEVICE_ACTIVE = "an";
	public static final String KEY_DEVICE_BIND_ACCOUNT = "ao";
	public static final String KEY_CATEGORY_ITEM_COUNT_EXPIRED_TIME = "ap";
	public static final String KEY_WX_QR_URL_EXPIRETIME = "aq";
	public static final String KEY_WX_QR_URL = "ar";
	public static final String KEY_PLATFORM_CHANNEL_ID = "as";

	private static final int USER_PHOTO_EXPIRED_TIME = 7 * 24 * 60 * 60 * 1000;

	public static void saveWxQrUrl(String url) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_WX_QR_URL, url);
		editor.apply();
	}

	public static String getWxQrUrl() {
		return getSharePref().getString(KEY_WX_QR_URL, DEFAULT_STRING);
	}

	public static void saveWxQrUrlExpiretime(long time) {
		Editor editor = getSharePref().edit();
		editor.putLong(KEY_WX_QR_URL_EXPIRETIME, time);
		editor.apply();
	}

	public static long getWxQrUrlExpiretime() {
		return getSharePref().getLong(KEY_WX_QR_URL_EXPIRETIME, 0);
	}

	public static void saveBindPhoneNum(String phone) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_BIND_PHONE_NUM, phone);
		editor.apply();
	}

	public static String getBindPhoneNum() {
		return getSharePref().getString(KEY_BIND_PHONE_NUM, DEFAULT_STRING);
	}

	public static void savePhoneNum(String phone) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_TICKET_PHONE_NUM, phone);
		editor.apply();
	}

	public static String getPhoneNum() {
		return getSharePref().getString(KEY_TICKET_PHONE_NUM, DEFAULT_STRING);
	}

	public static void saveRabbitTicketBalance(int balance) {
		Editor editor = getSharePref().edit();
		editor.putInt(KEY_RABBIT_TICKET_BALANCE, balance);
		editor.apply();
	}

	public static int getRabbitTicketBalance() {
		return getSharePref().getInt(KEY_RABBIT_TICKET_BALANCE, DEFAULT_INT);
	}

	public static void saveRabbitCoinBalance(int balance) {
		Editor editor = getSharePref().edit();
		editor.putInt(KEY_RABBIT_COIN_BALANCE, balance);
		editor.apply();
	}

	public static int getRabbitCoinBalance() {
		return getSharePref().getInt(KEY_RABBIT_COIN_BALANCE, DEFAULT_INT);
	}

	public static void saveFirstBindPhoneLook(boolean isBindLook) {
		Editor editor = getSharePref().edit();
		editor.putBoolean(KEY_IS_FIRST_USED_BIND_PHONE, isBindLook);
		editor.apply();
	}

	public static boolean getFirstBindPhoneLook() {
		return getSharePref().getBoolean(KEY_IS_FIRST_USED_BIND_PHONE, true);
	}

	public static void saveLastCheckUpgradeTime(long time) {
		Editor editor = getSharePref().edit();
		editor.putLong(KEY_LAST_CHECK_UPGRADE_TIME, time);
		editor.apply();
	}

	public static long getLastCheckUpgradeTime() {
		return getSharePref().getLong(KEY_LAST_CHECK_UPGRADE_TIME, DEFAULT_LONG);
	}

	public static void saveLastInfoUpgradeTime(long time) {
		Editor editor = getSharePref().edit();
		editor.putLong(KEY_LAST_INFO_UPGRADE_TIME, time);
		editor.apply();
	}

	public static String getMacAddr() {
		return getSharePref().getString(KEY_MAC_ADDRESS, DEFAULT_STRING);
	}

	public static void saveMacAddr(String mac) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_MAC_ADDRESS, mac);
		editor.apply();
	}

	public static long getLastNotifySlotExpiredTime() {
		return getSharePref().getLong(KEY_NOTIFICATION_SLOT_LAST_TIME, DEFAULT_LONG);
	}

	public static void saveLastNotifySlotExpiredTime(long time) {
		Editor editor = getSharePref().edit();
		editor.putLong(KEY_NOTIFICATION_SLOT_LAST_TIME, time);
		editor.apply();
	}

	public static boolean isAppUpdateNotify() {
		return getSharePref().getBoolean(KEY_APP_UPDATE_NOTIFY, true);
	}

	public static boolean isAppOnlineDownload() {
		return getSharePref().getBoolean(KEY_APP_ONLINE_DOWNLOAD, true);
	}

	public static void saveAppOnlineDownload(boolean isOnlineDownload) {
		Editor editor = getSharePref().edit();
		editor.putBoolean(KEY_APP_ONLINE_DOWNLOAD, isOnlineDownload);
		editor.apply();
	}

	public static void saveAppUpdateNotify(boolean isNotify) {
		Editor editor = getSharePref().edit();
		editor.putBoolean(KEY_APP_UPDATE_NOTIFY, isNotify);
		editor.apply();
	}

	public static void saveFirstUesdSlot(boolean isNotify) {
		Editor editor = getSharePref().edit();
		editor.putBoolean(KEY_FIRST_USED_SLOT, isNotify);
		editor.apply();
	}

	public static boolean getFirstUesdSlot() {
		return getSharePref().getBoolean(KEY_FIRST_USED_SLOT, true);
	}

	public static long getLastInfoUpgradeTime() {
		return getSharePref().getLong(KEY_LAST_INFO_UPGRADE_TIME, DEFAULT_LONG);
	}

	public static void saveEnable3GDownload(boolean enable) {
		Editor editor = getSharePref().edit();
		editor.putBoolean(KEY_ENABLE_3G_DOWNLOAD, enable);
		editor.apply();
	}

	public static void saveLastCheckAdPicUrlTime(long time) {
		Editor editor = getSharePref().edit();
		editor.putLong(KEY_LAST_CHECK_AD_PIC_URL_TIME, time);
		editor.apply();
	}

	public static long getLastCheckAdPicUrlTime() {
		return getSharePref().getLong(KEY_LAST_CHECK_AD_PIC_URL_TIME, DEFAULT_LONG);
	}

	public static boolean getEnable3GDownload() {
		return getSharePref().getBoolean(KEY_ENABLE_3G_DOWNLOAD, DEFAULT_BOOLEAN);
	}

	public static void saveInstalledDeleteApk(boolean enable) {
		Editor editor = getSharePref().edit();
		editor.putBoolean(KEY_INSTALLED_DELETE_APK, enable);
		editor.apply();
	}

	public static boolean isInstalledDeleteApk() {
		return getSharePref().getBoolean(KEY_INSTALLED_DELETE_APK, true);
	}

	public static boolean isFirstLaunch() {
		return getSharePref().getBoolean(KEY_IS_FIRST_LAUNCH, true);
	}

	public static void setFirstLaunch(boolean isFirstLaunch) {
		Editor editor = getSharePref().edit();
		editor.putBoolean(KEY_IS_FIRST_LAUNCH, isFirstLaunch);
		editor.apply();
	}

	public static void setMapGameComplete(boolean isComplete) {
		Editor editor = getSharePref().edit();
		editor.putBoolean(KEY_MAP_GAME_COMPLETE, isComplete);
		editor.apply();
	}

	public static boolean isMapGameComplete() {
		return getSharePref().getBoolean(KEY_MAP_GAME_COMPLETE, DEFAULT_BOOLEAN);
	}

	public static int getSlotNum() {
		return getSharePref().getInt(KEY_SLOT_NUM, DEFAULT_INT);
	}

	public static void setSlotNum(int slotNum) {
		Editor editor = getSharePref().edit();
		editor.putInt(KEY_SLOT_NUM, slotNum);
		editor.apply();
	}

	public static int getUsedSlotNum() {
		return getSharePref().getInt(KEY_USED_SLOT_NUM, 0);
	}

	public static void setUsedSlotNum(int slotNum) {
		Editor editor = getSharePref().edit();
		editor.putInt(KEY_USED_SLOT_NUM, slotNum);
		editor.apply();
	}

	public static SharedPreferences getSharePref() {
		return GameLauncherApplication.getApplication().getSharedPreferences(PREFERECNE_NAME, Context.MODE_PRIVATE);
	}

	public static boolean isSlotBuyPageFirst() {
		return getSharePref().getBoolean(IS_SLOT_BUY_PAGE_FIRST, true);
	}

	public static void setSlotBuyPageFirst(boolean isFirst) {
		getSharePref().edit().putBoolean(IS_SLOT_BUY_PAGE_FIRST, isFirst).apply();
	}

	public static boolean isDownloadFirst() {
		return getSharePref().getBoolean(IS_DOWNLOAD_FIRST, true);
	}

	public static void setDownloadFirst(boolean isDownloadFirst) {
		getSharePref().edit().putBoolean(IS_DOWNLOAD_FIRST, isDownloadFirst).apply();
	}

	public static boolean isBSSAccount() {
		return getSharePref().getBoolean(KEY_IS_BSS_ACCOUNT, false);
	}

	public static void setBSSAccount(boolean isBSSAccount) {
		getSharePref().edit().putBoolean(KEY_IS_BSS_ACCOUNT, isBSSAccount).apply();
	}

	public static void setHasUpdatable(boolean hasUpdatable) {
		Editor editor = getSharePref().edit();
		editor.putBoolean(HAS_UPDATABLE, hasUpdatable);
		editor.apply();
	}

	public static boolean hasUpdatable() {
		return getSharePref().getBoolean(HAS_UPDATABLE, false);
	}

	public static void setCategoryItemCountExpiredTime(long expiredTime) {
		getSharePref().edit().putLong(KEY_CATEGORY_ITEM_COUNT_EXPIRED_TIME, expiredTime).apply();
	}

	public static long getCategoryItemCountExpiredTime() {
		return getSharePref().getLong(KEY_CATEGORY_ITEM_COUNT_EXPIRED_TIME, DEFAULT_LONG);
	}

	public static void setCategoryExpiredTime(long expiredTime) {
		getSharePref().edit().putLong(KEY_CATEGORY_EXPIRED_TIME, expiredTime).apply();
	}

	public static long getCategoryExpiredTime() {
		return getSharePref().getLong(KEY_CATEGORY_EXPIRED_TIME, DEFAULT_LONG);
	}

	public static void setCollectionExpiredTime(long expiredTime) {
		getSharePref().edit().putLong(KEY_COLLECTION_EXPIRED_TIME, expiredTime).apply();
	}

	public static long getCollectionExpiredTime() {
		return getSharePref().getLong(KEY_COLLECTION_EXPIRED_TIME, DEFAULT_LONG);
	}

	public static void setLastSyncSlotTime(long expiredTime) {
		getSharePref().edit().putLong(KEY_LAST_SYNC_SLOT_TIME, expiredTime).apply();
	}

	public static long getLastSyncSlotTime() {
		return getSharePref().getLong(KEY_LAST_SYNC_SLOT_TIME, DEFAULT_LONG);
	}

	public static void setBannerExpiredTime(long expiredTime) {
		getSharePref().edit().putLong(KEY_BANNER_EXPIRED_TIME, expiredTime).apply();
	}

	public static long getBannerExpiredTime() {
		return getSharePref().getLong(KEY_BANNER_EXPIRED_TIME, DEFAULT_LONG);
	}

	public static void setKeywordExpiredTime(long expiredTime) {
		getSharePref().edit().putLong(KEY_KEYWORD_EXPIRED_TIME, expiredTime).apply();
	}

	public static long getKeywordExpiredTime() {
		return getSharePref().getLong(KEY_KEYWORD_EXPIRED_TIME, DEFAULT_LONG);
	}

	public static void setFeeConfigExpiredTime(long expiredTime) {
		getSharePref().edit().putLong(KEY_FEECONFIG_EXPIRED_TIME, expiredTime).apply();
	}

	public static long getFeeConfigExpiredTime() {
		return getSharePref().getLong(KEY_FEECONFIG_EXPIRED_TIME, DEFAULT_LONG);
	}

	public static void saveHeaderPhotoUrlList(List<UserHeaderImgItem> headerImgList) {
		if (headerImgList == null) {
			return;
		}
		try {
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < headerImgList.size(); i++) {
				UserHeaderImgItem item = headerImgList.get(i);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("HeaderImg", item.getImgUrl());
				jsonArray.put(i, jsonObj);
			}
			getSharePref().edit().putString(USER_HEADER_IMG_URL_LIST, jsonArray.toString())
					.putLong(HEADER_IMG_EXPIRED_MILLS, System.currentTimeMillis()).apply();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static List<UserHeaderImgItem> getHeaderPhotoUrlList() {
		long timeMills = getSharePref().getLong(HEADER_IMG_EXPIRED_MILLS, 0);
		if (System.currentTimeMillis() - timeMills > USER_PHOTO_EXPIRED_TIME) {
			return null;
		}
		String jsonStr = getSharePref().getString(USER_HEADER_IMG_URL_LIST, "");
		if (TextUtils.isEmpty(jsonStr)) {
			return null;
		}
		try {
			JSONArray jsonArray = new JSONArray(jsonStr);
			List<UserHeaderImgItem> headerImgList = new ArrayList<UserHeaderImgItem>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				String url = jsonObj.getString("HeaderImg");
				if (!TextUtils.isEmpty(url)) {
					UserHeaderImgItem imgItem = new UserHeaderImgItem(url, "");
					headerImgList.add(imgItem);
				}
			}
			return headerImgList;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getFreeFlowMode() {
		return getSharePref().getString(KEY_FREE_FLOW_MODE, FreeFlowType.TYPE_DISABLE);
	}

	public static void saveFreeFlowMode(String mode) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_FREE_FLOW_MODE, mode);
		editor.apply();
	}

	public static String getFreeFlowDomain() {
		return getSharePref().getString(KEY_FREE_FLOW_DOMAIN, GameLauncherConfig.DEFAULT_REMOTE_REQUEST_DOMAIN);
	}

	public static void saveFreeFlowDomain(String domain) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_FREE_FLOW_DOMAIN, domain);
		editor.apply();
	}

	public static String getFreeFlowNotSupportNum() {
		return getSharePref().getString(KEY_FREE_FLOW_NOT_SUPPORT_NUM, DEFAULT_STRING);
	}

	public static void saveFreeFlowNotSupportNum(String phoneNum) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_FREE_FLOW_NOT_SUPPORT_NUM, phoneNum);
		editor.apply();
	}

	public static String getFreeFlowBindPhoneNum() {
		return getSharePref().getString(KEY_FREE_FLOW_BIND_PHONE_NUM, DEFAULT_STRING);
	}

	public static void saveFreeFlowBindPhoneNum(String phoneNum) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_FREE_FLOW_BIND_PHONE_NUM, phoneNum);
		editor.apply();
	}

	public static String getOBoxType() {
		return getSharePref().getString(KEY_OBOX_TYPE,GameLauncherConfig.OBOX_DEFAULT_TYPE);
	}

	public static void saveOBoxType(String type) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_OBOX_TYPE, type);
		editor.apply();
	}

	public static void setDeviceActive(boolean active) {
		Editor editor = getSharePref().edit();
		editor.putBoolean(KEY_DEVICE_ACTIVE, active);
		editor.apply();
	}

	public static boolean hasDeviceActive() {
		return getSharePref().getBoolean(KEY_DEVICE_ACTIVE, false);
	}

	public static String getDeviceBindAccount() {
		return getSharePref().getString(KEY_DEVICE_BIND_ACCOUNT,DEFAULT_STRING);
	}

	public static void setDeviceBindAccount(String account) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_DEVICE_BIND_ACCOUNT, account);
		editor.apply();
	}

	public static String getChennelID() {
		return getSharePref().getString(KEY_PLATFORM_CHANNEL_ID, DEFAULT_STRING);
	}

	public static void saveChennelID(String id) {
		Editor editor = getSharePref().edit();
		editor.putString(KEY_PLATFORM_CHANNEL_ID, id);
		editor.apply();
	}

}
