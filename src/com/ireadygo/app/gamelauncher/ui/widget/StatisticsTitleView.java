package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class StatisticsTitleView extends FrameLayout {

	private TextView mTitleView;
	private TextView mCountView;
	private String mTitle;

	public StatisticsTitleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StatisticsTitleView(Context context) {
		super(context);
		initView();
	}

	public StatisticsTitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		initView();
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Statistics_Item);
		mTitle = ta.getString(R.styleable.Statistics_Item_stat_title);
		ta.recycle();
	}

	private void initView() {
		LayoutInflater.from(getContext()).inflate(R.layout.statistics_title_view, this, true);
		mTitleView = (TextView) findViewById(R.id.title);
		mCountView = (TextView) findViewById(R.id.count);

		mTitleView.setText(mTitle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

	}

	public TextView getTitleView() {
		return mTitleView;
	}

	public TextView getCountView() {
		return mCountView;
	}

	public void setCount(int count) {
		Log.d("lmq", "setCount---count = "+count);
		mCountView.setText(String.valueOf(count));
	}
}
