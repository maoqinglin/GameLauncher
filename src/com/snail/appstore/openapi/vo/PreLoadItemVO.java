package com.snail.appstore.openapi.vo;

public class PreLoadItemVO {

	private Long NAppId;//游戏ID
	private String SAppName;//游戏名称
	private String CPosterIcon;//海报图标地址
	private String CPosterPic;//海报背景地址

	public PreLoadItemVO() {
	}

	public Long getNAppId() {
		if (NAppId == null) {
			return 0L;
		}
		return NAppId;
	}

	public void setNAppId(Long nAppId) {
		NAppId = nAppId;
	}

	public String getSAppName() {
		return SAppName;
	}

	public void setSAppName(String sAppName) {
		SAppName = sAppName;
	}

	public String getCPosterIcon() {
		return CPosterIcon;
	}

	public void setCPosterIcon(String cPosterIcon) {
		CPosterIcon = cPosterIcon;
	}

	public String getCPosterPic() {
		return CPosterPic;
	}

	public void setCPosterPic(String cPosterPic) {
		CPosterPic = cPosterPic;
	}



}

