package com.ireadygo.app.gamelauncher.appstore.info.item;

import android.graphics.Bitmap;

public class UserHeaderImgItem {

	private String mImgUrl;
	private String mImgDesc;
	private Bitmap mBitmap;

	public UserHeaderImgItem(String url, String desc) {
		mImgUrl = url;
		mImgDesc = desc;
	}

	public String getImgUrl() {
		return mImgUrl;
	}

	public void setImgUrl(String imgUrl) {
		mImgUrl = imgUrl;
	}

	public String getImgDesc() {
		return mImgDesc;
	}

	public void setImgDesc(String imgDesc) {
		mImgDesc = imgDesc;
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.mBitmap = bitmap;
	}

}
