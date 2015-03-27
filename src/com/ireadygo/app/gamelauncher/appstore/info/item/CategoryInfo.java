package com.ireadygo.app.gamelauncher.appstore.info.item;

public class CategoryInfo extends AppEntity{
	private static final long serialVersionUID = -5850512530441575140L;
	private int categoryId;
	private String catetoryName;
	private String categoryDes;
	private String iconUrl;
	private int appCounts;//游戏数量

	public CategoryInfo(int categoryId, int appCounts, String categoryName, String categoryDec, String categoryUrl, String posterIcon,
			String posterBg) {
		setCategoryId(categoryId);
		setAppCounts(appCounts);
		setCatetoryName(categoryName);
		setCategoryDes(categoryDec);
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

}
