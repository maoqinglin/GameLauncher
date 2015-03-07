package com.ireadygo.app.gamelauncher.ui.item;

import com.ireadygo.app.gamelauncher.R;

import android.content.Context;
import android.util.AttributeSet;

public class ImageItemLarge extends ImageItem {

	public ImageItemLarge(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageItemLarge(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageItemLarge(Context context) {
		super(context);
	}

	@Override
	protected void initView(Context context) {
		super.initView(context);
		int width = getResources().getDimensionPixelOffset(R.dimen.common_image_item_large_width);
		int height = getResources().getDimensionPixelOffset(R.dimen.common_image_item_large_height);
		setBackgroundDimens(width, height);
		setIconLayoutDimens(width, height);
	}

}
