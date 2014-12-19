package com.ireadygo.app.gamelauncher.ui.account;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class AccountItem extends RelativeLayout {
	private Drawable mUnselectedIconDrawable;
	private Drawable mSelectedIconDrawable;
	private Drawable mBgDrawable;
	private String mTitleString;
	private ImageView mIconView;
	private ImageView mBackgroundView;
	private TextView mTitleView;
	private Animator mSelectedAnimator;
	private Animator mUnselectedAnimator;

	public AccountItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public AccountItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public AccountItem(Context context) {
		super(context);
	}

	private void init(Context context, AttributeSet attrs) {
		setFocusable(true);
		setFocusableInTouchMode(true);
		setClipChildren(false);
		setClipToPadding(false);
		LayoutInflater.from(getContext()).inflate(R.layout.account_item, this, true);
		if (isInEditMode()) {
			return;
		}
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AccountItem);
		mBgDrawable = ta.getDrawable(R.styleable.AccountItem_account_background);
		mUnselectedIconDrawable = ta.getDrawable(R.styleable.AccountItem_account_unselected_icon);
		mSelectedIconDrawable = ta.getDrawable(R.styleable.AccountItem_account_selected_icon);
		mTitleString = ta.getString(R.styleable.AccountItem_account_title);
		ta.recycle();
		mBackgroundView = (ImageView) findViewById(R.id.background);
		if (mBgDrawable != null) {
			mBackgroundView.setImageDrawable(mBgDrawable);
		}
		mIconView = (ImageView) findViewById(R.id.icon);
		if (mUnselectedIconDrawable != null) {
			mIconView.setImageDrawable(mUnselectedIconDrawable);
		}
		mTitleView = (TextView) findViewById(R.id.title);
//		mTitleView.getPaint().setFakeBoldText(true);
		mTitleView.setText(mTitleString);
	}

	public Animator getSelectedAnimator() {
		if (mSelectedAnimator == null) {
			mSelectedAnimator = selectedAnimator();
		}
		return mSelectedAnimator;
	}

	public Animator getUnselectedAnimator() {
		if (mUnselectedAnimator == null) {
			mUnselectedAnimator = unselectedAnimator();
		}
		return mUnselectedAnimator;
	}

	public Animator unselectedAnimator() {
		float pivotY = 0.25f * mBackgroundView.getHeight();
		mBackgroundView.setPivotY(pivotY);
		mBackgroundView.setPivotX(mBackgroundView.getWidth() / 2);
		return doAnimator(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mBackgroundView.setImageResource(R.drawable.account_item_init_bg_shape);
				mIconView.setImageDrawable(mUnselectedIconDrawable);
			}
		}, 1, 1, 1, 1, 0.7f, 0);
	}

	public Animator selectedAnimator() {
		float pivotY = 0.25f * mBackgroundView.getHeight();
		mBackgroundView.setPivotY(pivotY);
		mBackgroundView.setPivotX(mBackgroundView.getWidth() / 2);
		return doAnimator(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				bringToFront();
				mBackgroundView.setImageResource(R.drawable.account_item_focused_bg_shape);
				mIconView.setImageDrawable(mSelectedIconDrawable);
			}
		}, 1.2f, 1.32f, 2, 2, 1.f, 36); 
	}

	private Animator doAnimator(AnimatorListener listener, final float bgScaleX, final float bgScaleY,
			final float iconScaleX, final float iconScaleY, final float titleScale, final int titleTranslateY) {
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator bgScaleXAnima = ObjectAnimator.ofFloat(mBackgroundView, View.SCALE_X, bgScaleX);
		ObjectAnimator bgScaleYAnima = ObjectAnimator.ofFloat(mBackgroundView, View.SCALE_Y, bgScaleY);
		ObjectAnimator iconScaleXAnima = ObjectAnimator.ofFloat(mIconView, View.SCALE_X, iconScaleX);
		ObjectAnimator iconScaleYAnima = ObjectAnimator.ofFloat(mIconView, View.SCALE_Y, iconScaleY);
		ObjectAnimator titleScaleXAnima = ObjectAnimator.ofFloat(mTitleView, View.SCALE_X, titleScale);
		ObjectAnimator titleScaleYAnima = ObjectAnimator.ofFloat(mTitleView, View.SCALE_Y, titleScale);
		ObjectAnimator titleTranslateYAnima = ObjectAnimator.ofFloat(mTitleView, View.TRANSLATION_Y, titleTranslateY);
		animatorSet.setDuration(200);
		if (listener != null) {
			animatorSet.addListener(listener);
		}
		animatorSet.setInterpolator(new AccelerateInterpolator());
		animatorSet.playTogether(bgScaleXAnima, bgScaleYAnima, iconScaleXAnima, iconScaleYAnima, titleTranslateYAnima,
				titleScaleXAnima, titleScaleYAnima);
		return animatorSet;
	}
	
}
