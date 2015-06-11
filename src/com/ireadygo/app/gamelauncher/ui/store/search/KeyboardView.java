package com.ireadygo.app.gamelauncher.ui.store.search;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class KeyboardView extends FrameLayout implements OnClickListener {

	private TextView mKeyText;
	private ImageView mBackground;
	private KeyCallback mKeyCallback;
	private Drawable mBgDrawable;
	private Drawable mIconDrawable;
	private String mTitleString;
	private int mTitleTextSize;

	public KeyboardView(Context context) {
		super(context);
		initView(context, null);
	}

	public KeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	public KeyboardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private void initView(Context context, AttributeSet attrs) {
		View view = LayoutInflater.from(context).inflate(R.layout.key_layout,
				this, true);
		mKeyText = (TextView) view.findViewById(R.id.key);
		mBackground = (ImageView) view.findViewById(R.id.background);
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs,
					R.styleable.Keyboard_Item);
			mBgDrawable = ta
					.getDrawable(R.styleable.Keyboard_Item_key_background);
			mIconDrawable = ta.getDrawable(R.styleable.Keyboard_Item_key_icon);
			mTitleString = ta.getString(R.styleable.Keyboard_Item_key_title);
			mTitleTextSize = ta.getDimensionPixelOffset(
					R.styleable.Keyboard_Item_title_textsize,
					R.dimen.common_title_prompt_size);
			ta.recycle();
		}

		if (mBgDrawable != null) {
			mBackground.setBackground(mBgDrawable);
		}
		if (mIconDrawable != null) {
			mBackground.setImageDrawable(mIconDrawable);
		}
		if (!TextUtils.isEmpty(mTitleString)) {
			mKeyText.setText(mTitleString);
		}
		if (mTitleTextSize > 0) {
			mKeyText.setTextSize(mTitleTextSize);
		}
		setOnClickListener(this);
		setFocusable(true);
		setClickable(true);
	}

	public void setKeyText(String value) {
		if (!TextUtils.isEmpty(value)) {
			mKeyText.setText(value);
		}
	}

	public String getKeyText(){
		return mKeyText.getText().toString();
	}

	public void setKeyImage(Drawable drawable) {
		mBackground.setImageDrawable(drawable);
	}

	public void setKeyBackground(int resId) {
		mBackground.setBackgroundResource(resId);
	}

	public void setKeyCallback(KeyCallback callback) {
		mKeyCallback = callback;
	}

	public interface KeyCallback {
		void setKeyValue(String value);
	}

	@Override
	public void onClick(View v) {
		String value = mKeyText.getText().toString();
		if (mKeyCallback != null) {
			mKeyCallback.setKeyValue(value);
		}
	}

}
