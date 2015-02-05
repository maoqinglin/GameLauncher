package com.ireadygo.app.gamelauncher.ui.store;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class StoreEmptyView extends FrameLayout {
	private TextView mTitleView;
	
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
		mTitleView = (TextView)findViewById(R.id.empty_title);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

	}
	
	public TextView getTitleView(){
		return mTitleView;
	}
}
