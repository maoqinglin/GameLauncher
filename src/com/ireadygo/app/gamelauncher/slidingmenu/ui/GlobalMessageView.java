package com.ireadygo.app.gamelauncher.slidingmenu.ui;

import com.ireadygo.app.gamelauncher.R;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.WindowManager;
import android.widget.TextView;

public class GlobalMessageView {

	private static final int DURATION_TIME = 800;
	private final Context mContext;
	private boolean isShow = false;
	private boolean isActive = false;
	private final TextView mMsgTextView;
	private final WindowManager mWindowManager;
	private final WindowManager.LayoutParams mLayoutParams;
	private static GlobalMessageView sGlobalMessageView;

	private GlobalMessageView(Context context) {
		mContext = context;
		mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		mLayoutParams = getWindowManagerParams();
		mMsgTextView = getTextMsgView();
		mWindowManager.addView(mMsgTextView, mLayoutParams);
	}

	public static GlobalMessageView getInstance(Context context) {
		if(sGlobalMessageView == null) {
			synchronized (GlobalMessageView.class) {
				if(sGlobalMessageView == null) {
					sGlobalMessageView = new GlobalMessageView(context);
				}
			}
		}
		return sGlobalMessageView;
	}
	
	public void show(Bitmap bm, String msg) {
		show(new BitmapDrawable(mContext.getResources(), bm), msg);
	}

	public void show(Drawable drawable, String msg) {
		mMsgTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		mMsgTextView.setText(msg);
		final int X = mLayoutParams.x;
		ValueAnimator moveAnimator = ValueAnimator.ofInt(mLayoutParams.width);
		moveAnimator.setDuration(DURATION_TIME);
		moveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		moveAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mLayoutParams.x = X - (Integer) animation.getAnimatedValue();
				updateView();
			}
		});
		moveAnimator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				isActive = true;
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				isShow = true;
				isActive = false;
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
		moveAnimator.start();
	}

	public void hide() {
		final int X = mLayoutParams.x;
		ValueAnimator moveAnimator = ValueAnimator.ofInt(mLayoutParams.width);
		moveAnimator.setDuration(DURATION_TIME);
		moveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		moveAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mLayoutParams.x = X + (Integer) animation.getAnimatedValue();
				updateView();
			}
		});
		moveAnimator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				isActive = true;
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				isShow = false;
				isActive = false;
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
		moveAnimator.start();
	}

	public boolean isShow() {
		return isShow;
	}

	public boolean isActive() {
		return isActive;
	}

	private TextView getTextMsgView() {
		TextView msgText = new TextView(mContext);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		msgText.setLayoutParams(lp);
		msgText.setEllipsize(TruncateAt.END);
		msgText.setSingleLine(true);
		msgText.setPadding(10, 10, 10, 10);
		msgText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		msgText.setCompoundDrawablePadding(10);
		msgText.setBackgroundResource(R.drawable.boxmessage_global_bg);
		msgText.setTextColor(Color.WHITE);
		msgText.setTextSize(mContext.getResources().getDimensionPixelOffset(R.dimen.boxmessage_global_message_text_size));
		return msgText;
	}

	private WindowManager.LayoutParams getWindowManagerParams() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.TRANSLUCENT); // must be translucent to support
											// KitKat gradient
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = obtainnScreenSize().x;
		params.y = obtainnScreenSize().y / 7;
		params.width = mContext.getResources().getDimensionPixelOffset(R.dimen.boxmessage_global_message_width);
		params.height = mContext.getResources().getDimensionPixelOffset(R.dimen.boxmessage_global_message_height);
		return params;
	}
	
	private void updateView() {
		mWindowManager.updateViewLayout(mMsgTextView, mLayoutParams);
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private Point obtainnScreenSize() {
		Point windowSize = new Point();
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			int w = mWindowManager.getDefaultDisplay().getWidth();
			int h = mWindowManager.getDefaultDisplay().getHeight();
			windowSize.set(w, h);
		} else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB_MR2) {
			mWindowManager.getDefaultDisplay().getSize(windowSize);
		} else {
			mWindowManager.getDefaultDisplay().getRealSize(windowSize);
		}
		return windowSize;
	}
}
