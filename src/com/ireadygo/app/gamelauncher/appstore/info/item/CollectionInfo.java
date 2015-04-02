package com.ireadygo.app.gamelauncher.appstore.info.item;

public class CollectionInfo extends AppEntity {
	private static final long serialVersionUID = -5850512530441575140L;
	private int collectionId;
	private String collectionName;
	private String collectionDes;
	private String iconUrl;
	private int appCounts;//游戏数量

	public CollectionInfo(){
		
	}

	public CollectionInfo(int collectionId, int appCounts, String collectionName, String collectionDes, String iconUrl,
			String posterIcon, String posterBg) {
		setCollectionId(collectionId);
		setAppCounts(appCounts);
		setCollectionName(collectionName);
		setCollectionDes(collectionDes);
		setIconUrl(iconUrl);
		setPosterIconUrl(posterIcon);
		setPosterBgUrl(posterBg);
	}

	public int getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}

	public int getAppCounts() {
		return appCounts;
	}

	public void setAppCounts(int appCounts) {
		this.appCounts = appCounts;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getCollectionDes() {
		return collectionDes;
	}

	public void setCollectionDes(String collectionDes) {
		this.collectionDes = collectionDes;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

}
