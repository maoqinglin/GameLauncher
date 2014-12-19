package com.snail.appstore.openapi.vo;

/**
 * 应用集合缓存VO
 * @author gewq
 * @version 1.0 2014-6-11
 */
public class AppCollectionVO {
	// 信息包括分类ID，分类名称，分类描述，分类ICON下载链接

	private Long NCollectionId; // 集合ID
	private String SCollectionName; // 集合名称
	private String SCollectionDec; // 集合描述
	private String CPicUrl; // 图片URL
	private String CPosterIcon;//海报图标地址
	private String CPosterPic;//海报背景地址

	public Long getNCollectionId() {
		if (NCollectionId == null) {
			return 0L;
		}
		return NCollectionId;
	}

	public void setNCollectionId(Long nCollectionId) {
		NCollectionId = nCollectionId;
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
