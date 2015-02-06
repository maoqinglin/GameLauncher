package com.snail.appstore.openapi.vo;

public class AppUpdateVO {

	private Long NAppId; // 游戏ID
	private String SGameName; // 游戏名称,兼容老版本
	private String CIcon; // 游戏图标
	private Long IVersionCode; // 当前版本号
	private String sVersionName;// 版本名称
	private String CPackage; // 游戏包名
	private Integer IFlowFree; // 免标识
	private String CUpdate;// 是否升级 0 不需要升级 1需要升级
	private Integer iSize;// Apk大小
	private String CMd5;// 文件MD5
	private String SAppName;//与"sGameName   数据一样
	private String CAppType;
	private String CDownloadUrl;

	public Long getNAppId() {
		if (NAppId == null) {
			return 0L;
		}
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

	public String getCIcon() {
		return CIcon;
	}

	public void setCIcon(String cIcon) {
		CIcon = cIcon;
	}

	public Long getIVersionCode() {
		if (IVersionCode == null) {
			return 0L;
		}
		return IVersionCode;
	}

	public void setIVersionCode(Long iVersionCode) {
		IVersionCode = iVersionCode;
	}

	public String getCPackage() {
		return CPackage;
	}

	public void setCPackage(String cPackage) {
		CPackage = cPackage;
	}

	public Integer getIFlowFree() {
		if (IFlowFree == null) {
			return 0;
		}
		return IFlowFree;
	}

	public void setIFlowFree(Integer iFlowFree) {
		IFlowFree = iFlowFree;
	}

	public String getCUpdate() {
		return CUpdate;
	}

	public void setCUpdate(String cUpdate) {
		CUpdate = cUpdate;
	}

	public String getsVersionName() {
		return sVersionName;
	}

	public void setsVersionName(String sVersionName) {
		this.sVersionName = sVersionName;
	}

	public Integer getiSize() {
		if (iSize == null) {
			return 0;
		}
		return iSize;
	}

	public void setiSize(Integer iSize) {
		this.iSize = iSize;
	}

	public String getCMd5() {
		return CMd5;
	}

	public void setCMd5(String cMd5) {
		CMd5 = cMd5;
	}

	public String getSAppName() {
		return SAppName;
	}

	public void setSAppName(String sAppName) {
		SAppName = sAppName;
	}

	public String getCAppType() {
		return CAppType;
	}

	public void setCAppType(String cAppType) {
		CAppType = cAppType;
	}

	public String getCDownloadUrl() {
		return CDownloadUrl;
	}

	public void setCDownloadUrl(String cDownloadUrl) {
		CDownloadUrl = cDownloadUrl;
	}
}
