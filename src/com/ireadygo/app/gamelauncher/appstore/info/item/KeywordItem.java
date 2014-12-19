package com.ireadygo.app.gamelauncher.appstore.info.item;

public class KeywordItem {
	private String SKeyWord;// 热词
	private Integer INum;// 次数
	private String CPosterIcon;//海报图标地址
	private Integer NAppId;//热词对应应用的ID

	public String getSKeyWord() {
		return SKeyWord;
	}
	public void setSKeyWord(String sKeyWord) {
		SKeyWord = sKeyWord;
	}
	public Integer getINum() {
		return INum;
	}
	public void setINum(Integer iNum) {
		INum = iNum;
	}
	public String getCPosterIcon() {
		return CPosterIcon;
	}
	public void setCPosterIcon(String cPosterIcon) {
		CPosterIcon = cPosterIcon;
	}
	public Integer getNAppId() {
		return NAppId;
	}
	public void setNAppId(Integer nAppId) {
		NAppId = nAppId;
	}

}
