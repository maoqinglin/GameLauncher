package com.ireadygo.app.gamelauncher.ui.item;

import com.ireadygo.app.gamelauncher.R;

import android.content.Context;
import android.util.AttributeSet;

public class ImageItemSmall extends ImageItem {

	public ImageItemSmall(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ImageItemSmall(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageItemSmall(Context context) {
		super(context);
	}

	@Override
	protected void initView(Context context) {
		super.initView(context);
		int width = getResources().getDimensionPixelOffset(R.dimen.common_image_item_small_width);
		int height = getResources().getDimensionPixelOffset(R.dimen.common_image_item_small_height);
		setBackgroundDimens(width, height);
		setIconLayoutDimens(width, height);
	}
}
