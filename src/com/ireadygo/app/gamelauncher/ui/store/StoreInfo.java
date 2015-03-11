package com.ireadygo.app.gamelauncher.ui.store;

import android.graphics.drawable.Drawable;

import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;

public class StoreInfo {
	private Anchor mAnchor;
	private int mDrawableId;
	private int mTitleId;
	private Drawable mDrawable;

	public StoreInfo() {

	}

	public StoreInfo(int drawableId, Anchor anchor) {
		this.mDrawableId = drawableId;
		this.mAnchor = anchor;
	}

	public StoreInfo(int drawableId, int titleId, Anchor anchor) {
		this.mDrawableId = drawableId;
		this.mTitleId = titleId;
		this.mAnchor = anchor;
	}

	public Drawable getDrawable() {
		return mDrawable;
	}

	public void setDrawable(Drawable drawable) {
		this.mDrawable = drawable;
	}

	public Anchor getAnchor() {
		return mAnchor;
	}

	public void setAnchor(Anchor anchor) {
		this.mAnchor = anchor;
	}

	public int getDrawableId() {
		return mDrawableId;
	}

	public void setDrawableId(int drawableId) {
		this.mDrawableId = drawableId;
	}

	public int getTitleId() {
		return mTitleId;
	}

	public void setTitleId(int titleId) {
		this.mTitleId = titleId;
	}

	public void copyFrom(StoreInfo otherInfo) {
		this.mAnchor = otherInfo.getAnchor();
		this.mDrawable = otherInfo.getDrawable();
		this.mDrawableId = otherInfo.getDrawableId();
		this.mTitleId = otherInfo.getTitleId();
	}
}
