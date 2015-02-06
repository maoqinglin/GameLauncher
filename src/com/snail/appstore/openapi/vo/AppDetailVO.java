package com.snail.appstore.openapi.vo;

import java.util.Date;

import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;

/**
 * 应用详情VO
 * 
 * @author gewq
 * @version 1.0 2014-6-11
 */
public class AppDetailVO {
	private String sNotice;//应用通知，富文本编辑
	private String cAuditSource;//审核源，1：开发者审核，2：爬虫审核，3：CMS上传审核
	private String cSource;//应用来源: 0,自研;1,联运;2,其他;3,爬取,4.CPS
	private String cScreen;//屏幕类型:1,竖屏; 2, 横屏；9：可适配
	private String cPicScreen;// 截图的方向 1--竖屏，2--横屏
	private String SAppExtend;//APP扩展，JSON格式，例：{spreeId:20}
	private String CTags;//应用标签，{标签ID:标签名称}
	private String CAppType;//主类型: 1,游戏; 2, 应用
	private String COs;//系统: 1,安卓; 2,IOS；3,WP
	private Integer ICommentTimes;//评论次数
	private Long NFid;//论坛版本ID
	private Integer IShareTimes;//分享次数
	private String CPlatforms;
	private Date DUpdate;
	private String CServicePhone;
	private Long NScore;
	private String COfficialUrl;
	private String SUpdateDesc;
	private String CDownloadUrl;
	private String CBbsUrl;
	private Integer IDownloadTimes;
	private String CApkPermission;
	private Date DCreate;
	private Integer ICategoryId;
	// 游戏ID，游戏包名，游戏名称，游戏属性（如免属性等），游戏简介，游戏版本，游戏ICON，游戏截图

	private Long NAppId; // 游戏ID
	private String SAppName; // 游戏名称
	private String CIcon; // 游戏图标
	private String CVersionName; // 当前版本名称
	private Long IVersionCode; // 当前版本号
	private String CPackage; // 游戏包名
	private Integer ISize; // 文件大小
	private Integer IFlowFree = AppEntity.FLAG_OTHER; // 0 下载流量免费 1玩游戏流量免费 2
														// 下载流量免费,玩游戏流量免费 10 其他
	private String CMd5; // 文件MD5
	private String CStatus; // 0 下架 1上架
	private String SAppDesc; // 游戏描述
	private String CPicUrl; // 截图地址
	private Long IDownload; // 下载量
	private String CPosterIcon;// 海报图标地址
	private String CPosterPic;// 海报背景地址

	public String getsNotice() {
		return sNotice;
	}

	public void setsNotice(String sNotice) {
		this.sNotice = sNotice;
	}

	public String getcAuditSource() {
		return cAuditSource;
	}

	public void setcAuditSource(String cAuditSource) {
		this.cAuditSource = cAuditSource;
	}

	public String getcSource() {
		return cSource;
	}

	public void setcSource(String cSource) {
		this.cSource = cSource;
	}

	public String getcScreen() {
		return cScreen;
	}

	public void setcScreen(String cScreen) {
		this.cScreen = cScreen;
	}

	public String getcPicScreen() {
		return cPicScreen;
	}

	public void setcPicScreen(String cPicScreen) {
		this.cPicScreen = cPicScreen;
	}

	public String getSAppExtend() {
		return SAppExtend;
	}

	public void setSAppExtend(String sAppExtend) {
		SAppExtend = sAppExtend;
	}

	public String getCTags() {
		return CTags;
	}

	public void setCTags(String cTags) {
		CTags = cTags;
	}

	public String getCAppType() {
		return CAppType;
	}

	public void setCAppType(String cAppType) {
		CAppType = cAppType;
	}

	public String getCOs() {
		return COs;
	}

	public void setCOs(String cOs) {
		COs = cOs;
	}

	public Integer getICommentTimes() {
		return ICommentTimes;
	}

	public void setICommentTimes(Integer iCommentTimes) {
		ICommentTimes = iCommentTimes;
	}

	public Long getNFid() {
		return NFid;
	}

	public void setNFid(Long nFid) {
		NFid = nFid;
	}

	public Integer getIShareTimes() {
		return IShareTimes;
	}

	public void setIShareTimes(Integer iShareTimes) {
		IShareTimes = iShareTimes;
	}

	public String getCPlatforms() {
		return CPlatforms;
	}

	public void setCPlatforms(String cPlatforms) {
		CPlatforms = cPlatforms;
	}

	public Date getDUpdate() {
		return DUpdate;
	}

	public void setDUpdate(Date dUpdate) {
		DUpdate = dUpdate;
	}

	public String getCServicePhone() {
		return CServicePhone;
	}

	public void setCServicePhone(String cServicePhone) {
		CServicePhone = cServicePhone;
	}

	public Long getNScore() {
		return NScore;
	}

	public void setNScore(Long nScore) {
		NScore = nScore;
	}

	public String getCOfficialUrl() {
		return COfficialUrl;
	}

	public void setCOfficialUrl(String cOfficialUrl) {
		COfficialUrl = cOfficialUrl;
	}

	public String getSUpdateDesc() {
		return SUpdateDesc;
	}

	public void setSUpdateDesc(String sUpdateDesc) {
		SUpdateDesc = sUpdateDesc;
	}

	public String getCDownloadUrl() {
		return CDownloadUrl;
	}

	public void setCDownloadUrl(String cDownloadUrl) {
		CDownloadUrl = cDownloadUrl;
	}

	public String getCBbsUrl() {
		return CBbsUrl;
	}

	public void setCBbsUrl(String cBbsUrl) {
		CBbsUrl = cBbsUrl;
	}

	public Integer getIDownloadTimes() {
		return IDownloadTimes;
	}

	public void setIDownloadTimes(Integer iDownloadTimes) {
		IDownloadTimes = iDownloadTimes;
	}

	public String getCApkPermission() {
		return CApkPermission;
	}

	public void setCApkPermission(String cApkPermission) {
		CApkPermission = cApkPermission;
	}

	public Date getDCreate() {
		return DCreate;
	}

	public void setDCreate(Date dCreate) {
		DCreate = dCreate;
	}

	public Integer getICategoryId() {
		return ICategoryId;
	}

	public void setICategoryId(Integer iCategoryId) {
		ICategoryId = iCategoryId;
	}

	public String getSAppName() {
		return SAppName;
	}

	public void setSAppName(String sAppName) {
		SAppName = sAppName;
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

	public Long getNAppId() {
		if (NAppId == null) {
			return NAppId;
		}
		return NAppId;
	}

	public void setNAppId(Long nAppId) {
		NAppId = nAppId;
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
