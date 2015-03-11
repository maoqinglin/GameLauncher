package com.ireadygo.app.gamelauncher.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.activity.BaseGuideActivity;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class GuideOBoxIntroduceActivity extends BaseGuideActivity {

	private static final int[] RES_IMAGE_ID = new int[]{R.drawable.helper_image_01, R.drawable.helper_image_02, R.drawable.helper_image_03};
	private OperationTipsLayout mTipsLayout;
	private TextView mKeepOnBtn;
	private ImageView mIntroImageView;
	private int mPos = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starting_guide_intro_activity);
		init();
	}

	private void init() {

		initHeaderView(R.string.starting_guide_handler_intro);
		mTipsLayout = (OperationTipsLayout) findViewById(R.id.operationTipsLayout);
		mTipsLayout.setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mTipsLayout.getPagingIndicator().setVisibility(View.GONE);

		mIntroImageView = (ImageView) findViewById(R.id.guideIntroImage);
		mIntroImageView.setImageResource(RES_IMAGE_ID[mPos]);
		
		mKeepOnBtn = (TextView) findViewById(R.id.guideKeepOn);
		mKeepOnBtn.requestFocus();
		mKeepOnBtn.setOnClickListener(this);
	}

	private void updateLTBtnText() {
		if(mPos == 0) {
			setLTBtnText(getString(R.string.starting_guide_handler_intro));
		} else {
			setLTBtnText(getString(R.string.starting_guide_obox_intro));
		}
	}

	@Override
	public void onClick(View v) {
		mKeepOnBtn.requestFocus();
		mPos++;
		if(mPos > RES_IMAGE_ID.length - 1) {
			mPos = RES_IMAGE_ID.length - 1;
			Intent intent = new Intent(this, GuideRegisterOrLoginActivity.class);
			startActivity(intent);
		}

		updateLTBtnText();
		mIntroImageView.setImageResource(RES_IMAGE_ID[mPos]);
		super.onClick(v);
	}

	@Override
	public boolean onSunKey() {
		onClick(mKeepOnBtn);
		return true;
	}

     @Override
    public boolean onMoonKey() {
    	mKeepOnBtn.requestFocus();
    	mPos--;
    	if(mPos < 0) {
    		mPos = 0;
    	}

    	updateLTBtnText();
    	mIntroImageView.setImageResource(RES_IMAGE_ID[mPos]);
    	return true;
    }
}
