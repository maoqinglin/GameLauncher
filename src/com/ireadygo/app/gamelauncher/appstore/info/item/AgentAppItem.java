package com.ireadygo.app.gamelauncher.appstore.info.item;


public class AgentAppItem {

	private Long NAppId;
	private String SGameName;
	private String CVersionName;
	private Integer IVersionCode;
	private String CPackage;
	private Integer iFlowFree;
	private String CDownloadUrl;
	private String CMD5;
	public Long getNAppId() {
		return NAppId;
	}
	public void setNAppId(Long nAppId) {
		NAppId = nAppId;
	}
	public String getSGameName() {
		return SGameName;
	}
	public void setSGameName(String sGameName) {
		SGameName = sGameName;
	}
	public String getCVersionName() {
		return CVersionName;
	}
	public void setCVersionName(String cVersionName) {
		CVersionName = cVersionName;
	}
	public Integer getIVersionCode() {
		return IVersionCode;
	}
	public void setIVersionCode(Integer iVersionCode) {
		IVersionCode = iVersionCode;
	}
	public String getCPackage() {
		return CPackage;
	}
	public void setCPackage(String cPackage) {
		CPackage = cPackage;
	}
	public Integer getiFlowFree() {
		return iFlowFree;
	}
	public void setiFlowFree(Integer iFlowFree) {
		this.iFlowFree = iFlowFree;
	}
	public String getCDownloadUrl() {
		return CDownloadUrl;
	}
	public void setCDownloadUrl(String cDownloadUrl) {
		CDownloadUrl = cDownloadUrl;
	}
	public String getCMD5() {
		return CMD5;
	}
	public void setCMD5(String cMD5) {
		CMD5 = cMD5;
	}


}
