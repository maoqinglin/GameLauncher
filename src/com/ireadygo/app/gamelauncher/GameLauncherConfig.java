package com.ireadygo.app.gamelauncher;

import android.text.TextUtils;

import com.igexin.sdk.PushManager;

public class GameLauncherConfig {
	public static final String AUTHORITY = "com.ireadygo.app.gamelauncher";
	public static final String MY_APPID = "36";
	public static final String APP_ID = "APP_ID";
	public static final int PAGE_SIZE = 10;

	public static final boolean DEBUG = true;
	public static final boolean STANDARD_VERSION = false;//是否为标准版——标准版：应用，无卡槽;	联运版：桌面，有卡槽
	public static final boolean OBOX_VERSION = true;//是否为Obox版本
	public static final boolean ONLINE_DOWNLOAD_OPEN = true;//使能远程推送下载功能
	public static final boolean ENABLE_FREE_FLOW = true;//使能免流量功能
	public static final boolean IGNORE_NETWORTYPE = OBOX_VERSION;//是否忽略网络类型，主机上不需要关注网络类型，掌机需要关注
	public static final boolean SLOT_ENABLE = !OBOX_VERSION;//是否启用卡槽

	public static final long CATEGORY_CACHED_EXPIRED_TIME = 1 * 24 * 60 * 60 * 1000;//1 day
	public static final long COLLECTION_CACHED_EXPIRED_TIME = 4 * 60 * 60 * 1000;//4 hours
	public static final long BANNER_CACHED_EXPIRED_TIME = 2 * 60 * 60 *  1000;//2 hours
	public static final long KEYWORD_CACHED_EXPIRED_TIME = 1 * 24 * 60 * 60 * 1000;//1 day
	public static final long FEECONFIG_CACHED_EXPIRED_TIME = 1 * 24 * 60 * 60 * 1000;//1 day
	public static final long CATEGORY_ITEM_COUNT_CACHED_EXPIRED_TIME = 7 * 24 * 60 * 60 * 1000;//7 day

	public static final String PHONE_TYPE = "ANDROID";
	public static final String GETUI_CLIENTID = PushManager.getInstance().getClientid(GameLauncherApplication.getApplication());
	public static final String GETUI_APPID = "pBeBWRkaqE892HvalVEbj7";
	public static final int DEFAULT_SLOT_NUM = 20;
	public static final String DEFAULT_REMOTE_REQUEST_DOMAIN = "http://api.app.snail.com";

	public static final String OBOX_TYPE_A = "A";
	public static final String OBOX_TYPE_B = "B";
	public static final String OBOX_TYPE_C = "C";
	public static final String OBOX_DEFAULT_TYPE = OBOX_TYPE_C;
	public static  String sChannel = "1882";

	
	public static final String[] SLOT_WHITE_LIST = {
		"com.snailgame.cjg",
		"com.snail.im",
		"com.snail.snailkeytool",
		"com.woniu.mobile9yin",
		"com.woniu.mobilewoniu",
		"com.snail.oa",
		"com.alipay.android.app"};
	
	public static boolean isInSlotWhiteList(String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return false;
		}
		for (int i = 0; i < SLOT_WHITE_LIST.length; i++) {
			String whiteItem = SLOT_WHITE_LIST[i];
			if (pkgName.equals(whiteItem)) {
				return true;
			}
		}
		return false;
	}
}
