package com.ireadygo.app.gamelauncher.account.pushmsg;

import android.provider.BaseColumns;

public interface SnailPushMessageColumns extends BaseColumns{

	String COLUMN_MSG_TITLE = "title";
	String COLUMN_MSG_CONTENT = "content";
	String COLUMN_MSG_CREATE_DATE = "createDate";
	String COLUMN_MSG_TYPE = "type";
	String COLUMN_MSG_URL = "url";
	String COLUMN_MSG_PAGE_ID = "pageId";
	String COLUMN_MSG_PAGE_TITLE = "pageTitle";
	String COLUMN_MSG_TEXT = "text";

	String COLUMN_MSG_EXTEND = "extend";
	static final String TABLE_NAME = "snail_msg_info";
}
