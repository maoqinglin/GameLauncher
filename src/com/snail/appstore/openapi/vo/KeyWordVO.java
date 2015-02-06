package com.snail.appstore.openapi.vo;

public class KeyWordVO {
	
	private String SAppName;// 关键字

	public KeyWordVO() {

	}

	public KeyWordVO(String sAppName) {
		super();
		this.SAppName = sAppName;
	}

	public String getSAppName() {
		return SAppName;
	}

	public void setSAppName(String sAppName) {
		SAppName = sAppName;
	}
	
}
