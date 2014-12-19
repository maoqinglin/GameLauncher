package com.snail.appstore.openapi;


import static com.snail.appstore.openapi.AppPlatFormConfig.API_KEY;
import static com.snail.appstore.openapi.AppPlatFormConfig.APP_SECRET;

import android.content.Context;

import com.snail.appstore.openapi.service.IAppPlatFormService;
import com.snail.appstore.openapi.service.impl.AppPlatFormService;

/**
 * 免商店OPEN-API客户端
 * 
 * @author gewq
 * @version 1.0 2014-6-12
 */
public class AppPlatFormClient {
	// 免商店平台OPEN-API接口
	private IAppPlatFormService appPlatFormService;

	private static AppPlatFormClient instant = null;

	private AppPlatFormClient(Context context) {
		init(context);
	}

	/**
	 * 获取免商店OPEN-API客户端 事例
	 * 
	 * @return
	 */
	public static AppPlatFormClient getInstant(Context context) {
		if (instant == null) {
			instant = new AppPlatFormClient(context);
		}
		return instant;
	}

	private void init(Context context) {
		if (API_KEY == null || APP_SECRET == null) {
			throw new RuntimeException("APP_KEY and APP_SECRET should not be null, please set it in com.snail.mapc.AppPlatFormConfig");
		}
		if (appPlatFormService == null) {
			appPlatFormService = new AppPlatFormService(context);
		}
	}

	public IAppPlatFormService getAppPlatFormService() {
		return appPlatFormService;
	}
}
