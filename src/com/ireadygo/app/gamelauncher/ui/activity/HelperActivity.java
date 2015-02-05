package com.ireadygo.app.gamelauncher.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.activity.SwitchView.OnViewChangeListener;
import com.umeng.analytics.MobclickAgent;

public class HelperActivity extends Activity implements OnClickListener,
		OnViewChangeListener {

	private SwitchView mScrollLayout;

	private ImageView[] mDotViews;

	private int mViewCount;

	private int mCurSel;

	// private TextView tv_help;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.helper);
		initUI();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	private void initUI() {

		mScrollLayout = (SwitchView) findViewById(R.id.image_layout);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.dot_layout);

		mViewCount = getResources().getStringArray(R.array.image_name).length;
		mDotViews = new ImageView[mViewCount];

		for (int i = 0; i < mViewCount; i++) {

			ImageView imageView = new ImageView(this);
			imageView.setClickable(true);
			imageView.setEnabled(true);
			imageView.setOnClickListener(this);
			imageView.setImageResource(R.drawable.helper_dot_selected);
			imageView.setTag(i);
			mDotViews[i] = imageView;
			mDotViews[i].setVisibility(View.INVISIBLE);

			LinearLayout.LayoutParams lpDot = new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			lpDot.gravity = Gravity.CENTER_VERTICAL;

			FrameLayout layout = new FrameLayout(this);
			layout.setBackgroundResource(getResources().getIdentifier(
					getResources().getStringArray(R.array.image_name)[i],
					"drawable", getPackageName()));
			ViewGroup.LayoutParams lpImage = new ViewGroup.LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT);


			mScrollLayout.addView(layout, lpImage);
			linearLayout.addView(imageView, lpDot);
		}

		mCurSel = 0;
//		mDotViews[mCurSel].setEnabled(false);
		mDotViews[mCurSel].setVisibility(View.VISIBLE);

		mScrollLayout.SetOnViewChangeListener(this);
	}

	/**
	 * 
	 * @param index
	 */
	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}

//		mDotViews[mCurSel].setEnabled(true);
//		mDotViews[index].setEnabled(false);
		mDotViews[mCurSel].setVisibility(View.INVISIBLE);
		mDotViews[index].setVisibility(View.VISIBLE);

		mCurSel = index;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		int curScreen = mScrollLayout.getCurScreen();

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return false;
		}

		int desScreen = curScreen;

		if (keyCode == KeyEvent.KEYCODE_BUTTON_L1 || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			desScreen = curScreen - 1;
		}
		if (keyCode == KeyEvent.KEYCODE_BUTTON_R1 || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (curScreen == mViewCount - 1) {
				finish();
				return false;
			}
			desScreen = curScreen + 1;
		}
		setCurPoint(desScreen);
		mScrollLayout.snapToScreen(desScreen);

		return false;
	}

	@Override
	public void OnViewChange(int indexView) {
		setCurPoint(indexView);
	}

	@Override
	public void onClick(View v) {
		int pos = (Integer) (v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
	}

}