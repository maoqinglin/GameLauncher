package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class OperationTipsLayout extends RelativeLayout {

	private TextView tipsSunTxt, tipsMoonTxt, tipsMoontainTxt, tipsWaterTxt, tipsL1Txt, tipsR1Txt;
	private PagingIndicator mPagingIndicator;

	public enum TipFlag {
		FLAG_ALL, FLAG_TIPS_SUN, FLAG_TIPS_MOON, FLAG_TIPS_MOONTAIN, FLAG_TIPS_WATER, FLAG_TIPS_L1, FLAG_TIPS_R1
	}

	public OperationTipsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public OperationTipsLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public OperationTipsLayout(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.operation_tips, this, true);
		mPagingIndicator = (PagingIndicator) findViewById(R.id.paging_indicator);
		tipsSunTxt = (TextView) findViewById(R.id.tips_sun);
		tipsMoonTxt = (TextView) findViewById(R.id.tips_moon);
		tipsMoontainTxt = (TextView) findViewById(R.id.tips_mountain);
		tipsWaterTxt = (TextView) findViewById(R.id.tips_water);
		tipsL1Txt = (TextView) findViewById(R.id.tips_l1);
		tipsR1Txt = (TextView) findViewById(R.id.tips_r1);
		setClipChildren(false);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

	}

	public void setTipsVisible(TipFlag... flagList) {
		if (flagList == null) {
			return;
		}
		setAllVisible(View.GONE);
		for (TipFlag flag : flagList) {
			setVisibleByFlag(flag, View.VISIBLE);
		}
	}

	public void setTipsVisible(int indicatorVisible, TipFlag... flagList) {
		setTipsVisible(flagList);
		mPagingIndicator.setVisibility(indicatorVisible);
	}

	private void setVisibleByFlag(TipFlag flag, int isVisible) {
		switch (flag) {
		case FLAG_ALL:
			setAllVisible(View.VISIBLE);
			break;
		case FLAG_TIPS_SUN:
			tipsSunTxt.setVisibility(isVisible);
			break;
		case FLAG_TIPS_MOON:
			tipsMoonTxt.setVisibility(isVisible);
			break;
		case FLAG_TIPS_MOONTAIN:
			tipsMoontainTxt.setVisibility(isVisible);
			break;
		case FLAG_TIPS_WATER:
			tipsWaterTxt.setVisibility(isVisible);
			break;
		case FLAG_TIPS_L1:
			tipsL1Txt.setVisibility(isVisible);
			break;
		case FLAG_TIPS_R1:
			tipsR1Txt.setVisibility(isVisible);
			break;
		default:
			throw new NumberFormatException("params is valid");
		}
	}

	public void setAllVisible(int isVisible) {
		tipsSunTxt.setVisibility(isVisible);
		tipsMoonTxt.setVisibility(isVisible);
		tipsMoontainTxt.setVisibility(isVisible);
		tipsWaterTxt.setVisibility(isVisible);
	}

	public PagingIndicator getPagingIndicator() {
		return mPagingIndicator;
	}
}
