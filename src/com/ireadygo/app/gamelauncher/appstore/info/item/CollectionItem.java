package com.ireadygo.app.gamelauncher.appstore.info.item;

public class CollectionItem extends AppEntity {
	private static final long serialVersionUID = -5850512530441575140L;
	private int collectionId;
	private String collectionName;
	private String collectionDes;
	private String iconUrl;

	public CollectionItem(int collectionId, String collectionName, String collectionDes, String iconUrl,
			String posterIcon, String posterBg) {
		setCollectionId(collectionId);
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
