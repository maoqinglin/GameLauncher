package com.ireadygo.app.gamelauncher.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.activity.BaseGuideActivity;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class GuideOBoxIntroduceActivity extends BaseGuideActivity {

	private static final String ACTION_WIFI = "com.ireadygo.app.wizard.wifisettings.WifiSettings";
	private static final String ACTION_LANGUAGE = "com.ireadygo.app.wizard.language";
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
		mTipsLayout.setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_L1, TipFlag.FLAG_TIPS_R1);
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

	private void nextPage() {
		mKeepOnBtn.requestFocus();
		mPos++;
		if(mPos > RES_IMAGE_ID.length - 1) {
			mPos = RES_IMAGE_ID.length - 1;
			Intent intent = new Intent(this, GuideRegisterOrLoginActivity.class);
			startActivity(intent);
		}

		updateLTBtnText();
		mIntroImageView.setImageResource(RES_IMAGE_ID[mPos]);
	}

	private void previousPage() {
		mKeepOnBtn.requestFocus();
    	mPos--;
    	if(mPos < 0) {
    		mPos = 0;
    		previousActivity();
    		return;
    	}

    	updateLTBtnText();
    	mIntroImageView.setImageResource(RES_IMAGE_ID[mPos]);
	}

	private void previousActivity() {
		String entry = getIntent().getStringExtra("Entry");
		if(!TextUtils.isEmpty(entry)) {
			if(entry.equals("WifiSettings")) {
				startActivity(new Intent(ACTION_WIFI));
				finish();
				return;
			}

			if(entry.equals("LanguageSettings")) {
				startActivity(new Intent(ACTION_LANGUAGE));
				finish();
				return;
			}
		}
	}

	@Override
	public void onClick(View v) {
		nextPage();
		super.onClick(v);
	}

	@Override
	public boolean onSunKey() {
		nextPage();
		return true;
	}

	@Override
	public boolean onL1Key() {
		previousPage();
		return true;
	}

	@Override
	public boolean onR1Key() {
		nextPage();
		return true;
	}

	@Override
    public boolean onMoonKey() {
    	previousPage();
    	return true;
    }
}
