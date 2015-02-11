package com.ireadygo.app.gamelauncher.statusbar;

import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义DigitalClock输出格式
 * 
 * @author
 * 
 */
public class DigitalClock extends TextView {

	// public final static String m12 = "h:mm:ss aa";//h:mm:ss aa
	// public final static String m24 = "k:mm:ss";//k:mm:ss

	public final static String m12 = "h:mm aa";// h:mm:ss aa
	public final static String m24 = "k:mm";// k:mm:ss

	private FormatChangeObserver mFormatChangeObserver;
	Calendar mCalendar;

	private Handler mHandler;

	private boolean mTickerStopped = false;

	String mFormat;
	private boolean mForceByUser = false;

	public DigitalClock(Context context) {
		super(context);
		initClock(context);
	}

	public DigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		initClock(context);
	}

	private void initClock(Context context) {
		Resources r = context.getResources();

		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}

		mFormatChangeObserver = new FormatChangeObserver();

		mHandler = new Handler();

		setForceFormat(m24);
	}

	@Override
	protected void onAttachedToWindow() {
		mTickerStopped = false;
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mTickerStopped = true;
	}

	/**
	 * Pulls 12/24 mode from system settings
	 */
	private boolean get24HourMode() {
		return android.text.format.DateFormat.is24HourFormat(getContext());
	}

	private void setFormat() {
		if (get24HourMode()) {
			mFormat = m24;
		} else {
			mFormat = m12;
		}
	}

	public void setForceFormat(String format) {
		if (TextUtils.isEmpty(format)) {
			mForceByUser = false;
			setFormat();
			return;
		}

		mForceByUser = true;
		mFormat = format;
	}

	private class FormatChangeObserver extends ContentObserver {
		public FormatChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			if (mForceByUser) {
				return;
			}
			setFormat();
		}
	}

	public void resume() {
		mTickerStopped = false;

		mHandler.removeCallbacks(mTicker);
		mHandler.post(mTicker);

		getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true,
				mFormatChangeObserver);
	}

	public void stop() {
		mTickerStopped = true;

		mHandler.removeCallbacks(mTicker);
		getContext().getContentResolver().unregisterContentObserver(mFormatChangeObserver);
	}

	/**
	 * requests a tick on the next hard-second boundary
	 * 
	 */
	Runnable mTicker = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mTickerStopped) {
				return;
			}

			mCalendar.setTimeInMillis(System.currentTimeMillis());
			setText(DateFormat.format(mFormat, mCalendar));
			invalidate();

			long now = SystemClock.uptimeMillis();
			long next = now + (1000 - now % 1000);
			mHandler.postAtTime(mTicker, next);
		}
	};
}