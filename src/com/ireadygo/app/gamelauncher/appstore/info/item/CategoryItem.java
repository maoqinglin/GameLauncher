package com.ireadygo.app.gamelauncher.appstore.info.item;

public class CategoryItem extends AppEntity{
	private static final long serialVersionUID = -5850512530441575140L;
	private long id;
	private String itemName;
	private String decription;
	private String iconUrl;

	public CategoryItem(long categoryId,String categoryName,String categoryDec,String categoryUrl,String posterIcon,String posterBg) {
		setId(categoryId);
		setItemName(categoryName);
		setDecription(categoryDec);
		setIconUrl(categoryUrl);
		setPosterIconUrl(posterIcon);
		setPosterBgUrl(posterBg);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDecription() {
		return decription;
	}

	public void setDecription(String decription) {
		this.decription = decription;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	

}
