package com.snail.appstore.openapi.vo;

import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;

/**
 * 应用列表Item对象
 * 
 * @author gewq
 * @version 1.0 2014-6-11
 */
public class AppListItemVO {

	// 游戏ID，游戏包名，游戏名称，游戏属性（如免属性等），游戏简要描述，游戏版本，游戏ICON下载链接，游戏大小。

	private Long NAppId; // 游戏ID
	private String SGameName; // 游戏名称
	private String CIcon; // 游戏图标
	private String CVersionName; // 当前版本名称
	private Integer IFlowFree = AppEntity.FLAG_OTHER; // 0 下载流量免费 1玩游戏流量免费 2 下载流量免费,玩游戏流量免费 10 其他
	private String SGameDesc; // 游戏描述
	private String CPackage; // 游戏包名
	private Integer ISize = 0; //游戏大小
	private String CPosterIcon;//海报图标地址
	private String CPosterPic;//海报背景地址

	public Integer getISize() {
		if (ISize == null) {
			return 0;
		}
		return ISize;
	}

	public void setISize(Integer iSize) {
		ISize = iSize;
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

	public Integer getIFlowFree() {
		if (IFlowFree == null) {
			return 0;
		}
		return IFlowFree;
	}

	public void setIFlowFree(Integer iFlowFree) {
		IFlowFree = iFlowFree;
	}

	public String getSGameDesc() {
		return SGameDesc;
	}

	public void setSGameDesc(String sGameDesc) {
		SGameDesc = sGameDesc;
	}

	public String getCPackage() {
		return CPackage;
	}

	public void setCPackage(String cPackage) {
		CPackage = cPackage;
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
