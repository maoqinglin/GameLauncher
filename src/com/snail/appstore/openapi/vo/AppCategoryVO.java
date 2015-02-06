package com.snail.appstore.openapi.vo;

import java.util.Date;

/**
 * 应用分类缓存VO
 * 
 * @author gewq
 * @version 1.0 2014-6-11
 */
public class AppCategoryVO {
	// 信息包括分类ID，分类名称，分类描述，分类ICON下载链接

	private Date DCreate;
	private Integer IPlatformId;
	private Date DUpdate;
	private String CDelFlag;
	private Integer ICategoryId;
	private String CPicUrl;
	private String CAppType;
	private String SCategoryName;
	private Integer ISortValue;
	private String SCategoryDesc;
	private String CCategoryType;
	private String CPosterIcon;
	private String CPosterPic;

	public Date getDCreate() {
		if (DCreate == null) {
			return new Date();
		}
		return DCreate;
	}

	public void setDCreate(Date dCreate) {
		DCreate = dCreate;
	}

	public Integer getIPlatformId() {
		return IPlatformId;
	}

	public void setIPlatformId(Integer iPlatformId) {
		IPlatformId = iPlatformId;
	}

	public Date getDUpdate() {
		if (DUpdate == null) {
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

	public Integer getICategoryId() {
		if (ICategoryId == null) {
			return 0;
		}
		return ICategoryId;
	}

	public void setICategoryId(Integer iCategoryId) {
		ICategoryId = iCategoryId;
	}

	public String getCPicUrl() {
		return CPicUrl;
	}

	public void setCPicUrl(String cPicUrl) {
		CPicUrl = cPicUrl;
	}

	public String getCAppType() {
		return CAppType;
	}

	public void setCAppType(String cAppType) {
		CAppType = cAppType;
	}

	public String getSCategoryName() {
		return SCategoryName;
	}

	public void setSCategoryName(String sCategoryName) {
		SCategoryName = sCategoryName;
	}

	public Integer getISortValue() {
		if (ISortValue == null) {
			return 0;
		}
		return ISortValue;
	}

	public void setISortValue(Integer iSortValue) {
		ISortValue = iSortValue;
	}

	public String getSCategoryDesc() {
		return SCategoryDesc;
	}

	public void setSCategoryDesc(String sCategoryDesc) {
		SCategoryDesc = sCategoryDesc;
	}

	public String getCCategoryType() {
		return CCategoryType;
	}

	public void setCCategoryType(String cCategoryType) {
		CCategoryType = cCategoryType;
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
