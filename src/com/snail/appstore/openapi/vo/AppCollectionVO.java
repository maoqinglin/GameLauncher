package com.snail.appstore.openapi.vo;

import java.util.Date;

/**
 * 应用集合缓存VO
 * @author gewq
 * @version 1.0 2014-6-11
 */
public class AppCollectionVO {
	// 信息包括分类ID，分类名称，分类描述，分类ICON下载链接

	private Date DCreate;
	private Integer IPlatformId;
	private Date DUpdate;
	private String CDelFlag;
	private Integer ICollectionId;
	private String SCollectionName;// 集合名称
	private String SCollectionDec;// 集合描述
	private Integer ISortValue;
	private String CAppType;
	private String CPicUrl; // 图片URL
	private String CPosterIcon;// 海报图标地址
	private String CPosterPic;// 海报背景地址

	public Date getDCreate() {
		if(DCreate == null){
			return new Date();
		}
		return DCreate;
	}

	public void setDCreate(Date dCreate) {
		DCreate = dCreate;
	}

	public Integer getIPlatformId() {
		if(IPlatformId == null){
			return 0;
		}
		return IPlatformId;
	}

	public void setIPlatformId(Integer iPlatformId) {
		IPlatformId = iPlatformId;
	}

	public Date getDUpdate() {
		if(DUpdate == null){
			return new Date();
		}
		return DUpdate;
	}

	public void setDUpdate(Date dUpdate) {
		DUpdate = dUpdate;
	}

	public String getCDelFlag() {
		return CDelFlag;
	}

	public void setCDelFlag(String cDelFlag) {
		CDelFlag = cDelFlag;
	}

	public Integer getICollectionId() {
		if(ICollectionId == null){
			return ICollectionId;
		}
		return ICollectionId;
	}

	public void setICollectionId(Integer iCollectionId) {
		ICollectionId = iCollectionId;
	}

	public String getSCollectionName() {
		return SCollectionName;
	}

	public void setSCollectionName(String sCollectionName) {
		SCollectionName = sCollectionName;
	}

	public String getSCollectionDec() {
		return SCollectionDec;
	}

	public void setSCollectionDec(String sCollectionDec) {
		SCollectionDec = sCollectionDec;
	}

	public Integer getISortValue() {
		if(ISortValue == null){
			return 0;
		}
		return ISortValue;
	}

	public void setISortValue(Integer iSortValue) {
		ISortValue = iSortValue;
	}

	public String getCAppType() {
		return CAppType;
	}

	public void setCAppType(String cAppType) {
		CAppType = cAppType;
	}

	public String getCPicUrl() {
		return CPicUrl;
	}

	public void setCPicUrl(String cPicUrl) {
		CPicUrl = cPicUrl;
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
