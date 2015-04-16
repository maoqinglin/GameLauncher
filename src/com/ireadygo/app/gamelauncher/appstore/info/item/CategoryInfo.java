package com.ireadygo.app.gamelauncher.appstore.info.item;

import java.io.Serializable;

public class CategoryInfo extends AppEntity implements Serializable{
	private static final long serialVersionUID = -5850512530441575140L;
	private int categoryId;
	private String catetoryName;
	private String categoryDes;
	private String iconUrl;
	private int appCounts;//游戏数量
	private String categoryType;
	private int platformId;

	public CategoryInfo(int categoryId,int platformId, int appCounts, String categoryName, String categoryDec, String categoryType, String categoryUrl, String posterIcon,
			String posterBg) {
		setCategoryId(categoryId);
		setPlatformId(platformId);
		setAppCounts(appCounts);
		setCatetoryName(categoryName);
		setCategoryDes(categoryDec);
		setCategoryType(categoryType);
		setIconUrl(categoryUrl);
		setPosterIconUrl(posterIcon);
		setPosterBgUrl(posterBg);
		
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getAppCounts() {
		return appCounts;
	}

	public void setAppCounts(int appCounts) {
		this.appCounts = appCounts;
	}

	public String getCatetoryName() {
		return catetoryName;
	}

	public void setCatetoryName(String catetoryName) {
		this.catetoryName = catetoryName;
	}

	public String getCategoryDes() {
		return categoryDes;
	}

	public void setCategoryDes(String categoryDes) {
		this.categoryDes = categoryDes;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
}
