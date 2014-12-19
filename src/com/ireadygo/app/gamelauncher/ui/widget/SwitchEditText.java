package com.ireadygo.app.gamelauncher.ui.widget;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class SwitchEditText extends FrameLayout {
	private Drawable mNormalBackground;
	private Drawable mFocusBackground;
	private boolean mEditable;
	private EditText mEditText;
	private TextView mTextView;
	private String mText = "";
	private int mTextColor = Color.BLACK;
	private int mTextSize = 12;
	private OnFocusChangeListener mCustomFocusListener;
	private boolean mPaddingHasDefine = false;
	private int mPadding;
	private int mPaddingLeft, mPaddingRight, mPaddingTop, mPaddingBottom;

	public SwitchEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		init(context);
	}

	public SwitchEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwitchEditText(Context context) {
		super(context);
		init(context);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwitchEditText);
		mText = ta.getString(R.styleable.SwitchEditText_text);
		mTextColor = ta.getColor(R.styleable.SwitchEditText_textColor, Color.BLACK);
		mTextSize = ta.getDimensionPixelSize(R.styleable.SwitchEditText_textSize, 12);
		mEditable = ta.getBoolean(R.styleable.SwitchEditText_editable, false);
		mNormalBackground = ta.getDrawable(R.styleable.SwitchEditText_normalBackground);
		mFocusBackground = ta.getDrawable(R.styleable.SwitchEditText_focusBackground);
		mPadding = ta.getDimensionPixelSize(R.styleable.SwitchEditText_padding, 0);
		if (mPadding > 0) {
			mPaddingHasDefine = true;
		} else {
			mPaddingLeft = ta.getDimensionPixelSize(R.styleable.SwitchEditText_paddingLeft, 0);
			mPaddingRight = ta.getDimensionPixelSize(R.styleable.SwitchEditText_paddingRight, 0);
			mPaddingTop = ta.getDimensionPixelSize(R.styleable.SwitchEditText_paddingTop, 0);
			mPaddingBottom = ta.getDimensionPixelSize(R.styleable.SwitchEditText_paddingBottom, 0);
		}
		ta.recycle();
	}

	private void init(Context context) {
		setFocusable(true);
		setOnFocusChangeListener(mFocusChangeListener);

		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		mEditText = new EditText(context);
		mEditText.setTextColor(mTextColor);
		mEditText.setTextSize(mTextSize);
		mEditText.setText(mText);
		mEditText.setSingleLine();
		mEditText.setGravity(Gravity.CENTER_VERTICAL);
		mEditText.setBackgroundColor(color.background_dark);
		mEditText.setBackground(mFocusBackground);
		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					setEditable(false);
					if (mCustomFocusListener != null) {
						mCustomFocusListener.onFocusChange(SwitchEditText.this, false);
					}
				}
			}
		});
		addView(mEditText, lp);

		mTextView = new TextView(context);
		mTextView.setTextColor(mTextColor);
		mTextView.setClickable(true);
		mTextView.setTextSize(mTextSize);
		mTextView.setText(mText);
		mTextView.setGravity(Gravity.CENTER_VERTICAL);
		mTextView.setSingleLine();
		mTextView.setBackground(mNormalBackground);
		mTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setEditable(true);
				if (mCustomFocusListener != null) {
					mCustomFocusListener.onFocusChange(SwitchEditText.this, true);
				}
			}
		});
		addView(mTextView, lp);
		if (mPaddingHasDefine) {
			setPadding(mPadding, mPadding, mPadding, mPadding);
		} else {
			setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
		}

		setMininumDimens();
		setEditable(mEditable);
	}

	public void setEditable(boolean editable) {
		mEditable = editable;
		if (editable) {
			mEditText.setVisibility(View.VISIBLE);
			mTextView.setVisibility(View.GONE);
			mEditText.requestFocus();
		} else {
			mTextView.setVisibility(View.VISIBLE);
			mEditText.setVisibility(View.GONE);
			// mTextView.requestFocus();
		}
	}

	private void setMininumDimens() {
		int mininumWidth = 0;
		int mininumHeight = 0;
		if (mNormalBackground == null) {
			if (mFocusBackground != null) {
				mininumWidth = mFocusBackground.getIntrinsicWidth();
				mininumHeight = mFocusBackground.getIntrinsicHeight();
			}
		} else {
			if (mFocusBackground != null) {
				mininumHeight = Math.max(mNormalBackground.getIntrinsicHeight(), mFocusBackground.getIntrinsicHeight());
				mininumWidth = Math.max(mNormalBackground.getIntrinsicWidth(), mFocusBackground.getIntrinsicWidth());
			} else {
				mininumWidth = mNormalBackground.getIntrinsicWidth();
				mininumHeight = mNormalBackground.getIntrinsicHeight();
			}
		}
		if (mininumWidth < 0) {
			mininumWidth = 0;
		}
		if (mininumHeight < 0) {
			mininumHeight = 0;
		}
		setMinimumWidth(mininumWidth);
		setMinimumHeight(mininumHeight);
	}

	public void setText(String text) {
		mText = text;
		mEditText.setText(text);
		mTextView.setText(text);
	}

	public void setText(int resId) {
		mText = getResources().getString(resId);
		mEditText.setText(mText);
		mTextView.setText(mText);
	}

	public void setTextSize(int textSize) {
		mTextSize = textSize;
		mTextView.setTextSize(textSize);
		mEditText.setTextSize(textSize);
	}

	public void setTextColor(int textColor) {
		mTextColor = textColor;
		mTextView.setTextColor(textColor);
		mEditText.setTextColor(textColor);
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		mTextView.setPadding(left, top, right, bottom);
		mEditText.setPadding(left, top, right, bottom);
	}

	public void setOnCustomFocusChangeListener(OnFocusChangeListener listener) {
		mCustomFocusListener = listener;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				setEditable(true);
				if (mCustomFocusListener != null) {
					mCustomFocusListener.onFocusChange(SwitchEditText.this, true);
				}
			}
		}
	};
}
