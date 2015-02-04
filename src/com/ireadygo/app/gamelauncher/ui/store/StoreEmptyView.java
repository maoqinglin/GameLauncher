package com.ireadygo.app.gamelauncher.ui.store;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.ireadygo.app.gamelauncher.R;

public class StoreEmptyView extends FrameLayout {

	public StoreEmptyView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StoreEmptyView(Context context) {
		super(context);
		initView();
	}

	public StoreEmptyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.store_empty_view, this, true);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

	}
}
