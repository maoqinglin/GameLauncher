package com.ireadygo.app.gamelauncher.ui.menu;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;

public class MenuItem extends LinearLayout {

	private ImageView mMenuBackground;
	private ImageView mMenuView;
	private TextView mMenuTitle;
	private View mItemLayout;
	private Drawable mBgDrawable;
	private Drawable mIconDrawable;
	private Drawable mHighlightDrawable;
	private String mTitleString;
	private int mMenuIndex = -1;
	private BaseContentFragment mContentFragment;

	public MenuItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public MenuItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MenuItem(Context context) {
		super(context);
	}

	private void init(Context context, AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.menu_item, this, true);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MenuItem);
		mBgDrawable = ta.getDrawable(R.styleable.MenuItem_menu_background);
		mHighlightDrawable = ta.getDrawable(R.styleable.MenuItem_menu_highlight);
		mIconDrawable = ta.getDrawable(R.styleable.MenuItem_menu_icon);
		mTitleString = ta.getString(R.styleable.MenuItem_menu_title);
		ta.recycle();
		
		if(isInEditMode()){
			return;
		}
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		mMenuBackground = (ImageView) findViewById(R.id.menu_item_bg);
		mItemLayout = findViewById(R.id.menu_item_layout);
		mMenuView = (ImageView) findViewById(R.id.menu_item_icon);
		if (mIconDrawable != null) {
			mMenuView.setImageDrawable(mIconDrawable);
		}
		mMenuTitle = (TextView) findViewById(R.id.menu_item_title);
		if (!TextUtils.isEmpty(mTitleString)) {
			mMenuTitle.setText(mTitleString);
		}
		setScaleX(Config.MenuItem.INIT_SCALE);
		setScaleY(Config.MenuItem.INIT_SCALE);
		setAlpha(Config.MenuItem.INIT_ALPHA);
		mMenuTitle.setTranslationY(Config.MenuItem.INIT_TITLE_Y);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
	}

	@Override
	public void setBackgroundResource(int resid) {
		if(mMenuBackground != null){
			mMenuBackground.setImageResource(resid);
		}
	}

	@Override
	public void setBackground(Drawable background) {
		if(mMenuBackground != null){
			mMenuBackground.setImageDrawable(background);
		}
	}

	public Drawable getHighlightDrawable() {
		return mHighlightDrawable;
	}

	public int getIndex() {
		return mMenuIndex;
	}

	public void setIndex(int index) {
		this.mMenuIndex = index;
	}

	public void toInit() {
		float destAlpha = Config.MenuItem.INIT_ALPHA;
		float destScale = Config.MenuItem.INIT_SCALE;
		boolean hasBackground = false;
		int destTitleY = Config.MenuItem.INIT_TITLE_Y;
		doAnimator(null, hasBackground, destAlpha, destScale, destTitleY);
	}

	public void toFocusedOnMenuFocused(AnimatorListener listener) {
		doAnimator(listener, true, Config.MenuItem.FOCUSED_ALPHA, Config.MenuItem.FOCUSED_SCALE, Config.MenuItem.FOCUSED_TITLE_Y);
	}

	public void toSelectedOnMenuSelected() {
		doAnimator(null, false, Config.MenuItem.SELECTED_ALPHA, Config.MenuItem.SELECTED_SCALE, Config.MenuItem.SELECTED_TITLE_Y);
	}

	public void toNoFocusedOnMenuFocused() {
		doAnimator(null, false, Config.MenuItem.NO_FOCUSED_ALPHA, Config.MenuItem.NO_FOCUSED_SCALE, Config.MenuItem.NO_FOCUSED_TITLE_Y);
	}

	public void toNoSelectedOnMenuSelected() {
		doAnimator(null, false, Config.MenuItem.NO_SELECTED_ALPHA, Config.MenuItem.NO_SELECTED_SCALE, Config.MenuItem.NO_SELECTED_TITLE_Y);
	}

	private void doAnimator(AnimatorListener listener, boolean hasBackground, float destAlpha, float destScale,
			int destTitleY) {
		ObjectAnimator animBg;
		if (hasBackground) {
			setBackground(mBgDrawable);
			animBg = ObjectAnimator.ofFloat(mMenuBackground, View.ALPHA, 0, 1);
		} else {
			animBg = ObjectAnimator.ofFloat(mMenuBackground, View.ALPHA, 1, 0);
			animBg.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					setBackground(null);
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					setBackground(null);
				}
			});
		}
		ObjectAnimator animTitle = ObjectAnimator.ofFloat(mMenuTitle, View.TRANSLATION_Y, destTitleY);
		ObjectAnimator animAlpha = ObjectAnimator.ofFloat(this, View.ALPHA, getAlpha(), destAlpha);
		ObjectAnimator animScaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, getScaleX(), destScale);
		ObjectAnimator animScaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, getScaleY(), destScale);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(animBg, animTitle, animAlpha, animScaleX, animScaleY);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.setDuration(200);
		if (listener != null) {
			animatorSet.addListener(listener);
		}
		animatorSet.start();
	}

	public BaseContentFragment getFragment() {
		return mContentFragment;
	}

	public void setFragment(BaseContentFragment contentFragment) {
		this.mContentFragment = contentFragment;
	}

	private static enum State {
		INIT, // 初始状态
		FOCUSED_FOCUSED, // 菜单处于焦点状态，子项也处于焦点状态
		FOCUSED_NO_FOCUSED, // 菜单处于焦点状态，子项处于非焦点状态
		SELECTED_SELECTED, // 菜单处于选中状态，子项处于选中状态
		SELECTED_NO_SELECTED// 菜单处于选中状态，子项处于非选中状态
	}
}
