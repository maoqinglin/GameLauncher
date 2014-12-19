package com.ireadygo.app.gamelauncher.account.pushmsg;

import com.snail.appstore.openapi.json.JSONException;
import com.snail.appstore.openapi.json.JSONObject;

import android.text.TextUtils;

public class SnailPushMessage {
	private int id;
	private String title;
	private String content;
	private String expandMessage;
	private long createDate;

	// 扩展字段
	private int type;
	private String url;
	private String pageId;
	private String pageTitle;
	private String text;

	public SnailPushMessage() {

	}

	public SnailPushMessage(long createDate, int type, String url, String pageId, String pageTitle, String text) {
		this.createDate = createDate;
		this.type = type;
		this.url = url;
		this.pageId = pageId;
		this.pageTitle = pageTitle;
		this.text = text;
	}

	public SnailPushMessage(int id, String title, String content, String expandMessage, long createDate) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.expandMessage = expandMessage;
		if (!TextUtils.isEmpty(expandMessage)) {
			try {
				JSONObject jsonObj = new JSONObject(expandMessage);
				this.type = jsonObj.getInt("type");
				this.url = jsonObj.getString("url");
				this.pageId = jsonObj.getString("pageId");
				this.pageTitle = jsonObj.getString("pageTitle");
				this.text = jsonObj.getString("text");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		this.createDate = createDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExpandMessage() {
		return expandMessage;
	}

	public void setExpandMessage(String expandMessage) {
		this.expandMessage = expandMessage;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public int getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public String getPageId() {
		return pageId;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public String getText() {
		return text;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static class Type {
		/** 打开指定应用 **/
		public static final int OPEN_APP = 1;
		/** 跳转流量分享页面 **/
		public static final int GOTO_FLOW_SHARE = 2;
		/** 登录首页 **/
		public static final int LOGIN_HOME_PAGE = 4;
		/** 跳转合集 **/
		public static final int GOTO_COMP_PAGE = 5;
		/** 跳转指定的游戏详情 **/
		public static final int GOTO_GAME_DETAIL = 6;
		/** 相关活动页面 **/
		public static final int RELATED_ACTIVITIES_PAGE = 7;
		/** 应用下载 **/
		public static final int APP_DOWNLOAD = 8;
		/** 推送余额 **/
		public static final int PUSH_BALANCE = 9;
		/** 系统公告 **/
		public static final int SYSTEM_NOTICE = 10;
	}
}
