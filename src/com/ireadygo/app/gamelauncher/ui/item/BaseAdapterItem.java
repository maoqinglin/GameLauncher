package com.ireadygo.app.gamelauncher.ui.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class BaseAdapterItem extends FrameLayout implements ISelectableItem {

	public BaseAdapterItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public BaseAdapterItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public BaseAdapterItem(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context){
		setClipChildren(false);
		setClipToPadding(false);
	}
}
