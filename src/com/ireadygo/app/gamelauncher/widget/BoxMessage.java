package com.ireadygo.app.gamelauncher.widget;

import java.io.Serializable;

import android.graphics.Bitmap;

public class BoxMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int msgId = 10000;
	private String appId;
	private String title;
	private String pkgName;
	private String content;
	private int skipType;

	public BoxMessage() {
		generateNewId();
	}

	public int getMsgId() {
		return msgId;
	}

	public static int generateNewId() {
		return msgId++;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public int getSkipType() {
		return skipType;
	}

	public void setSkipType(int skipType) {
		this.skipType = skipType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
