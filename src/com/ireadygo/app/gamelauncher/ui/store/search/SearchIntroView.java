package com.ireadygo.app.gamelauncher.ui.store.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class SearchIntroView extends FrameLayout {

	private ImageView mIntroImg;
	private TextView mTips;

	public SearchIntroView(Context context) {
		super(context);
		initView(context);
	}

	public SearchIntroView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public SearchIntroView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private void initView(Context context) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.search_intro_layout, this, true);
		mIntroImg = (ImageView) view.findViewById(R.id.search_intro_img);
		mTips = (TextView) view.findViewById(R.id.search_intro_tip);
	}

	public void setIntro(SEARCH_STATUS status) {
		if (status == SEARCH_STATUS.NETWORK_DISCONNECT) {
			mTips.setText(getResources().getString(R.string.store_empty_title));
			mTips.setVisibility(VISIBLE);
			mIntroImg.setVisibility(INVISIBLE);
		} else if (status == SEARCH_STATUS.SEARCH_RESULT_EMPTY) {
			mTips.setText(getResources().getString(R.string.search_result_empty));
			mTips.setVisibility(VISIBLE);
			mIntroImg.setVisibility(INVISIBLE);
		}else{
			mTips.setVisibility(INVISIBLE);
			mIntroImg.setVisibility(VISIBLE);
		}
	}

	public enum SEARCH_STATUS {
		NETWORK_DISCONNECT, SEARCH_RESULT_EMPTY,SEARCH_RESULT_SUCCESS
	};
}
