package com.snail.appstore.openapi.vo;

import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;

/**
 * 广告栏VO
 * 
 * @author gewq
 * @version 1.0 2014-6-11
 */
public class AppBannerVO {

	private Long NAppId;
	private Integer IBannerId;
	private String CDownloadUrl;
	private Integer ICategoryId;
	private String SPayDesc;
	private String SInfo; // 简述
	private String CType; // 类型: 1,游戏; 2,合集; 3,小编
	private String CPostion; // 广告栏位置: 1, 上;2, 中;3, 下
	private Long NParamId; // 参数ID: 当type为1时,此为游戏ID; 当type为2时,此为集合ID; 为3时,为游戏ID
	private String CPicUrl; // 宣传图片地址
	private String CHtmlUrl; // 静态页面url
	private Integer IRefId; // 关联广告栏ID

	private String SAppName; // 游戏名称
	private String CIcon; // 游戏图标
	private String CVersionName; // 当前版本名称
	private Long IVersionCode; // 当前版本号
	private String CPackage; // 游戏包名
	private Integer IFlowFree = AppEntity.FLAG_OTHER; // 0 下载流量免费 1玩游戏流量免费 2
														// 下载流量免费,玩游戏流量免费 10 其他
	private String CMd5; // 文件MD5
	private String CMark;// 专题标识: 0:不显示 1:首发 2:合集 3:热门 4:推荐 5:独家 6:精品
	private Integer ISize;// 文件大小

	public Long getNAppId() {
		if(NAppId == null){
			return 0L;
		}
		return NAppId;
	}

	public void setNAppId(Long nAppId) {
		NAppId = nAppId;
	}

	public Integer getIBannerId() {
		if(IBannerId == null){
			return 0;
		}
		return IBannerId;
	}

	public void setIBannerId(Integer iBannerId) {
		IBannerId = iBannerId;
	}

	public String getCDownloadUrl() {
		return CDownloadUrl;
	}

	public void setCDownloadUrl(String cDownloadUrl) {
		CDownloadUrl = cDownloadUrl;
	}

	public Integer getICategoryId() {
		if(ICategoryId == null){
			return 0;
		}
		return ICategoryId;
	}

	public void setICategoryId(Integer iCategoryId) {
		ICategoryId = iCategoryId;
	}

	public String getSPayDesc() {
		return SPayDesc;
	}

	public void setSPayDesc(String sPayDesc) {
		SPayDesc = sPayDesc;
	}

	public String getSInfo() {
		return SInfo;
	}

	public void setSInfo(String sInfo) {
		SInfo = sInfo;
	}

	public String getCType() {
		return CType;
	}

	public void setCType(String cType) {
		CType = cType;
	}

	public String getCPostion() {
		return CPostion;
	}

	public void setCPostion(String cPostion) {
		CPostion = cPostion;
	}

	public Long getNParamId() {
		if(NParamId == null){
			return 0L;
		}
		return NParamId;
	}

	public void setNParamId(Long nParamId) {
		NParamId = nParamId;
	}

	public String getCPicUrl() {
		return CPicUrl;
	}

	public void setCPicUrl(String cPicUrl) {
		CPicUrl = cPicUrl;
	}

	public String getCHtmlUrl() {
		return CHtmlUrl;
	}

	public void setCHtmlUrl(String cHtmlUrl) {
		CHtmlUrl = cHtmlUrl;
	}

	public Integer getIRefId() {
		if(IRefId == null){
			return 0;
		}
		return IRefId;
	}

	public void setIRefId(Integer iRefId) {
		IRefId = iRefId;
	}

	public String getSAppName() {
		return SAppName;
	}

	public void setSAppName(String sAppName) {
		SAppName = sAppName;
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
		if(IVersionCode == null){
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
		if(IFlowFree == null){
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

	public String getCMark() {
		return CMark;
	}

	public void setCMark(String cMark) {
		CMark = cMark;
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
}
