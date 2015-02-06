package com.snail.appstore.openapi.vo;

import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;

/**
 * 应用列表Item对象
 * 
 * @author gewq
 * @version 1.0 2014-6-11
 */
public class AppListItemVO {

	private Long NAppId; // 游戏ID
	private String SAppName; // 游戏名称
	private String CAppType; // 主类型: 1,游戏; 2, 应用
	private String CIcon; // 游戏图标
	private String CVersionName; // 当前版本名称
	private Long IVersionCode; // 当前版本号
	private String CPackage; // 游戏包名
	private String CDownloadUrl; // 下载地址
	private Integer ISize; // 文件大小
	private Integer IFlowFree; // 0 下载流量免费 1玩游戏流量免费 2 下载流量免费,玩游戏流量免费 10 其他
	private String CMd5; // 文件MD5
	private String CStatus; // 0 下架 1上架
	private String SAppDesc; // 游戏描述
	private Long NScore; // 累计评分
	private Integer IDownloadTimes; // 下载量

	public Long getNAppId() {
		if(NAppId == null){
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

	public String getCAppType() {
		return CAppType;
	}

	public void setCAppType(String cAppType) {
		CAppType = cAppType;
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

	public String getCDownloadUrl() {
		return CDownloadUrl;
	}

	public void setCDownloadUrl(String cDownloadUrl) {
		CDownloadUrl = cDownloadUrl;
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

	public String getCStatus() {
		return CStatus;
	}

	public void setCStatus(String cStatus) {
		CStatus = cStatus;
	}

	public String getSAppDesc() {
		return SAppDesc;
	}

	public void setSAppDesc(String sAppDesc) {
		SAppDesc = sAppDesc;
	}

	public Long getNScore() {
		if (NScore == null) {
			return 0L;
		}
		return NScore;
	}

	public void setNScore(Long nScore) {
		NScore = nScore;
	}

	public Integer getIDownloadTimes() {
		if (IDownloadTimes == null) {
			return 0;
		}
		return IDownloadTimes;
	}

	public void setIDownloadTimes(Integer iDownloadTimes) {
		IDownloadTimes = iDownloadTimes;
	}

}
