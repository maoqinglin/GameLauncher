package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class DigitTextView extends TextView {
	private static final String DIGIT_DEFAULT = "0";
	private static final String UNIT_DEFAULT = "";
	private static final String DIGIT_UNIT_DIVIDE_STRING = "  ";
	private String mDigit = DIGIT_DEFAULT;
	private String mUnit = UNIT_DEFAULT;

	public DigitTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		setTextByStyle(mDigit, mUnit);
	}

	public DigitTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DigitTextView(Context context) {
		super(context);
		setTextByStyle(mDigit, mUnit);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
//		Log.d("liu.js", "initAttrs--");
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DigitText);
		mUnit = ta.getString(R.styleable.DigitText_unit);
		mDigit = ta.getString(R.styleable.DigitText_digit);
		ta.recycle();
		if (TextUtils.isEmpty(mDigit)) {
			mDigit = DIGIT_DEFAULT;
		}
		if(TextUtils.isEmpty(mUnit)){
			mUnit = UNIT_DEFAULT;
		}
	}

	public void setDigit(String digit) {
		if (TextUtils.isEmpty(digit)) {
			this.mDigit = DIGIT_DEFAULT;
		} else {
			this.mDigit = digit;
		}
		setTextByStyle(mDigit, mUnit);
	}

	public void setDigit(int digit) {
		setDigit(String.valueOf(digit));
	}

	public void setUnit(String unit) {
		if (unit == null) {
			this.mUnit = UNIT_DEFAULT;
		} else {
			this.mUnit = unit;
		}
		setTextByStyle(mDigit, mUnit);
	}

	private void setTextByStyle(String digit, String unit) {
//		Log.d("liu.js", "setText--digit=" + digit + "|unit=" + unit);
		SpannableStringBuilder ssb = new SpannableStringBuilder(digit + DIGIT_UNIT_DIVIDE_STRING + unit);
		ssb.setSpan(new TextAppearanceSpan(getContext(), R.style.TextLabelMiddleLight), 0, digit.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.setSpan(new TextAppearanceSpan(getContext(), R.style.TextLabelSmallDark), digit.length(), digit.length()
				+ DIGIT_UNIT_DIVIDE_STRING.length() + unit.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		setText(ssb);
	}
}
