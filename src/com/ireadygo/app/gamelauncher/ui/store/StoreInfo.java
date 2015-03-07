package com.ireadygo.app.gamelauncher.ui.store;

import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;

public class StoreInfo {
	private Anchor mAnchor;
	private int mDrawableId;
	private int mTitleId;

	public StoreInfo(int drawableId, Anchor anchor) {
		this.mDrawableId = drawableId;
		this.mAnchor = anchor;
	}

	public StoreInfo(int drawableId, int titleId, Anchor anchor) {
		this.mDrawableId = drawableId;
		this.mTitleId = titleId;
		this.mAnchor = anchor;
	}

	public Anchor getAnchor() {
		return mAnchor;
	}

	public void setmAnchor(Anchor anchor) {
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

}
