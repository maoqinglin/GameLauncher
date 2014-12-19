package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;

public class WebviewProgressBar extends View {
	private static final int MAX_PROGRESS = 100;
	private int mProgress;
	private int mWidth;
	private int mHeight;
	private Bitmap mProgressDownBmp;
	private Bitmap mProgressUpBmp;
	private String mText = "";
	private Rect mUpRect = new Rect();// UpBitmap显示的区域
	private Paint mTextPaint = new Paint();
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mProgress += 3;
			if (mProgress >= MAX_PROGRESS) {
				mProgress = 0;
			}
			invalidate();
		}
	};

	public WebviewProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public WebviewProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WebviewProgressBar(Context context) {
		super(context);
		init();
	}

	private void init() {
		mProgressDownBmp = BitmapFactory.decodeResource(getResources(), R.drawable.progress_m_down);
		mProgressUpBmp = BitmapFactory.decodeResource(getResources(), R.drawable.progress_m_up);
		// 获取图片宽度、高度
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.drawable.progress_m_down, opts);
		mWidth = opts.outWidth;
		mHeight = opts.outHeight;
		mText = "努力加载中";
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setTextSize(28);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int left = (dm.widthPixels - mWidth) / 2;
		int top = (dm.heightPixels / 2 - mHeight) * 2 / 3;
		// super.onDraw(canvas);
		int upHeight = mHeight - mHeight * mProgress / 100;
		canvas.drawBitmap(mProgressDownBmp, left, top, null);
		if (upHeight < mHeight) {
			mUpRect.set(0, upHeight, mProgressUpBmp.getWidth(), mProgressUpBmp.getHeight());
			Bitmap bmpUp = PictureUtil.cutBitmap(mProgressUpBmp, mUpRect, Config.ARGB_8888);
			canvas.drawBitmap(bmpUp, left, top + upHeight, null);
		}
		canvas.drawText(mText, left + mWidth / 2 + 10, top + mHeight + 50, mTextPaint);
		if (getVisibility() == View.VISIBLE) {
			mHandler.sendEmptyMessageDelayed(1, 100);
		}
	}

	public void setProgress(int progress) {
		this.mProgress = progress;
	}

	public int getProgress() {
		return mProgress;
	}

}
