package com.ireadygo.app.gamelauncher.ui.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class ImageTextMenu extends MenuItem {

	private Drawable mIconDrawable;
	private ImageView mImageView;
	private String mText;
	private TextView mTextView;

	public ImageTextMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		init(context);
	}

	public ImageTextMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageTextMenu(Context context) {
		super(context);
		init(context);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MenuItem);
		mText = ta.getString(R.styleable.MenuItem_menu_title);
		mIconDrawable = ta.getDrawable(R.styleable.MenuItem_menu_icon);
		ta.recycle();
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.menu_image_text, this, true);
		mImageView = (ImageView) findViewById(R.id.menu_image);
		if (mIconDrawable != null) {
			mImageView.setImageDrawable(mIconDrawable);
		}
		mTextView = (TextView) findViewById(R.id.menu_text);
		if (mText != null) {
			mTextView.setText(mText);
		}
		mTextView.setScaleX(0.8f);
		mTextView.setScaleY(0.8f);
	}

	@Override
	public void setSelected(boolean selected) {
		mImageView.setSelected(selected);
		mTextView.setSelected(selected);
		mTextView.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
		super.setSelected(selected);
	}

}
