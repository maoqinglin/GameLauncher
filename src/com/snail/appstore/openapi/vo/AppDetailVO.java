package com.snail.appstore.openapi.vo;

import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;

/**
 * 应用详情VO
 * 
 * @author gewq
 * @version 1.0 2014-6-11
 */
public class AppDetailVO {
	// 游戏ID，游戏包名，游戏名称，游戏属性（如免属性等），游戏简介，游戏版本，游戏ICON，游戏截图

	private Long NAppId; // 游戏ID
	private String SGameName; // 游戏名称
	private String CIcon; // 游戏图标
	private String CVersionName; // 当前版本名称
	private Long IVersionCode; // 当前版本号
	private String CPackage; // 游戏包名
	private Integer ISize; // 文件大小
	private Integer IFlowFree = AppEntity.FLAG_OTHER; // 0 下载流量免费 1玩游戏流量免费 2 下载流量免费,玩游戏流量免费 10 其他
	private String CMd5; // 文件MD5
	private String CGameStatus; // 0 下架 1上架
	private String SGameDesc; // 游戏描述
	private String CPicUrl; // 截图地址
	private Long IDownload; // 下载量
	private String CAppScreen = "";//截图的方向 1--竖屏，2--横屏
	private String CPosterIcon;//海报图标地址
	private String CPosterPic;//海报背景地址

	public Long getNAppId() {
		if (NAppId == null) {
			return NAppId;
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

	public String getCVersionName() {
		return CVersionName;
	}

	public void setCVersionName(String cVersionName) {
		CVersionName = cVersionName;
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

	public Integer getISize() {
		if (ISize == null) {
			return 0;
		}
		return ISize;
	}

	public void setISize(Integer iSize) {
		ISize = iSize;
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

	public String getCMd5() {
		return CMd5;
	}

	public void setCMd5(String cMd5) {
		CMd5 = cMd5;
	}

	public String getCGameStatus() {
		return CGameStatus;
	}

	public void setCGameStatus(String cGameStatus) {
		CGameStatus = cGameStatus;
	}

	public String getSGameDesc() {
		return SGameDesc;
	}

	public void setSGameDesc(String sGameDesc) {
		SGameDesc = sGameDesc;
	}

	public String getCPicUrl() {
		return CPicUrl;
	}

	public void setCPicUrl(String cPicUrl) {
		CPicUrl = cPicUrl;
	}

	public Long getIDownload() {
		if (IDownload == null) {
			return 0L;
		}
		return IDownload;
	}

	public void setIDownload(Long iDownload) {
		IDownload = iDownload;
	}

	public String getCAppScreen() {
		return CAppScreen;
	}

	public void setCAppScreen(String cAppScreen) {
		this.CAppScreen = cAppScreen;
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
