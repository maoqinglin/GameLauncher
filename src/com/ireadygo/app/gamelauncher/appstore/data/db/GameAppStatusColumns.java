package com.ireadygo.app.gamelauncher.appstore.data.db;

import android.provider.BaseColumns;

public interface GameAppStatusColumns extends BaseColumns {
	String COLUMN_APP_ID = "app_id";
	String COLUMN_APP_NAME = "app_name";
	String COLUMN_PACKAGE_NAME = "package_name";
	String COLUMN_PKG_TYPE = "pkg_type";
	String COLUMN_FILE_NAME = "file_name";
	String COLUMN_DOWNLOAD_PATH = "download_path";
	String COLUMN_FREE_FLOW_DOWNLOAD_PATH = "free_flow_download_path";
	String COLUMN_SAVED_PATH = "saved_path";
	String COLUMN_TOTAL_SIZE = "total_size";
	String COLUMN_DOWNLOAD_SIZE = "download_size";
	String COLUMN_APP_GAME_STATUS = "app_game_status";
	String COLUMN_CUR_VERSION_CODE = "cur_version_code";
	String COLUMN_CUR_VERSION_NAME = "cur_version_name";
	String COLUMN_NEW_VERSION_CODE = "new_version_code";
	String COLUMN_NEW_VERSION_NAME = "new_version_name";
	String COLUMN_APP_SIGN = "sign";
	String COLUMN_REMOTE_ICON_URL = "remote_icon_url";
	String COLUMN_LOCAL_ICON_URL = "local_icon_url";
	String COLUMN_CREATE_TIME = "create_time";
	String COLUMN_IS_UPDATEABLE = "is_updateable";

	String COLUMN_IN_FREESTORE = "in_freestore";
	String COLUMN_DESCRIPTION = "description";
	String COLUMN_FREEFLAG = "freeflag";
	String COLUMN_SCREENSHOT_URL = "screenshot_url";
	String COLUMN_DOWNLOAD_COUNT = "download_count";
	String COLUMN_COME_FRM_FREESTORE = "come_frm_freestore";
	String COLUMN_OCCUPY_SLOT = "occupy_slot";
	String COLUMN_SCREENSHOT_DIRECTION = "screenshot_direction";
	String COLUMN_LAST_LAUNCH_TIME = "last_launch_time";
	String COLUMN_POSTER_ICON_URL = "poster_icon_url";
	String COLUMN_POSTER_BG_URL = "poster_bg_url";
	String COLUMN_POSTER_ICON = "poster_icon";
	String COLUMN_RES_TYPE = "res_type";
	String COLUMN_RES_URL = "res_url";
	String COLUMN_RES_MD5 = "res_md5";
	String COLUMN_RES_SIZE = "res_size";

	String COLUMN_EXTEND = "extend";

	static final String TABLE_NAME = "download_info";

}
