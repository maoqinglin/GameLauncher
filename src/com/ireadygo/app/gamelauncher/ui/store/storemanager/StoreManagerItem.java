package com.ireadygo.app.gamelauncher.ui.store.storemanager;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.item.BaseAdapterItem;

public class StoreManagerItem extends BaseAdapterItem {
	
	private StoreManagerItemHolder mHolder;
	private Drawable mIconDrawable;
	private Animator mSelectedAnimator;
	private Animator mUnselectedAnimator;
	private OnItemFocusChangeListener mListener;
	private boolean mIsItemFocus;

	public StoreManagerItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
		initView(context);
	}

	public StoreManagerItem(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StoreManagerItem(Context context) {
		super(context);
		initView(context);
	}

	@Override
	public void toSelected(AnimatorListener listener) {
		mIsItemFocus = true;
		if(mListener != null) {
			mListener.onFocusChange(true);
		}
		if (mUnselectedAnimator != null && mUnselectedAnimator.isRunning()) {
			mUnselectedAnimator.cancel();
		}
		mHolder.background.setImageResource(R.drawable.settings_item_bg_shape);
		mSelectedAnimator = createAnimator(listener, 1.1f, 1.2f);
		mSelectedAnimator.start();
	}

	@Override
	public void toUnselected(AnimatorListener listener) {
		mIsItemFocus = false;
		if(mListener != null) {
			mListener.onFocusChange(false);
		}
		if (mSelectedAnimator != null && mSelectedAnimator.isRunning()) {
			mSelectedAnimator.cancel();
		}
		mHolder.background.setImageResource(R.drawable.corner_app_item_bg_shape);
		mUnselectedAnimator = createAnimator(listener, 1, 1);
		mUnselectedAnimator.start();
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Item);
		mIconDrawable = a.getDrawable(R.styleable.Item_item_icon);
		a.recycle();
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.store_manager_item, this, true);
		mHolder = new StoreManagerItemHolder();
		mHolder.iconLayout = (ViewGroup)findViewById(R.id.icon_layout);
		mHolder.background = (ImageView) findViewById(R.id.background);
		mHolder.icon = (ImageView) findViewById(R.id.icon);
		mHolder.uninstallIcon = (ImageView) findViewById(R.id.delete_icon);
		if (mIconDrawable != null) {
			LayoutParams params = new LayoutParams(mIconDrawable.getMinimumWidth(), mIconDrawable.getMinimumHeight());
			mHolder.background.setLayoutParams(params);
			mHolder.icon.setImageDrawable(mIconDrawable);
		}
		mHolder.title = (TextView) findViewById(R.id.title);
		
		mHolder.statusLayout = (FrameLayout) findViewById(R.id.manager_item_status_layout);
		mHolder.status = (TextView) findViewById(R.id.manager_item_status);
		mHolder.downloadSpeedLayout = (LinearLayout) findViewById(R.id.manager_item_speed_layout);
		mHolder.downloadSpeed = (TextView) findViewById(R.id.manager_item_download_speed);
		mHolder.downloadSize = (TextView) findViewById(R.id.manager_item_download_size);
		mHolder.progressBar = (ProgressBar) findViewById(R.id.manager_item_download_progress);
	}

	public StoreManagerItemHolder getHolder() {
		return mHolder;
	}

	public boolean isItemFocus() {
		return mIsItemFocus;
	}

	private Animator createAnimator(AnimatorListener listener, float scaleIcon, float scaleBg) {
		AnimatorSet animatorSet = new AnimatorSet();

		PropertyValuesHolder iconScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, scaleIcon);
		PropertyValuesHolder iconScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleIcon);
		ObjectAnimator animatorIcon = ObjectAnimator.ofPropertyValuesHolder(mHolder.iconLayout, iconScaleXHolder,
				iconScaleYHolder);

		PropertyValuesHolder bgScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, scaleBg);
		PropertyValuesHolder bgScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleBg);
		ObjectAnimator animatorBg = ObjectAnimator.ofPropertyValuesHolder(mHolder.background, bgScaleXHolder,
				bgScaleYHolder);

		animatorSet.playTogether(animatorIcon, animatorBg);
		animatorSet.setDuration(200);
		if (listener != null) {
			animatorSet.addListener(listener);
		}
		return animatorSet;
	}

	public interface OnItemFocusChangeListener {
		void onFocusChange(boolean hasFocus);
	}

	public void setOnItemFocusChangeListener(OnItemFocusChangeListener listener) {
		mListener = listener;
	}

	public static class StoreManagerItemHolder {
		public ViewGroup iconLayout;
		public ImageView background;
		public ImageView icon;
		public ImageView uninstallIcon;
		public TextView title;
		
		public FrameLayout statusLayout;
		public TextView status;
		public LinearLayout downloadSpeedLayout;
		public TextView downloadSpeed;
		public TextView downloadSize;
		public ProgressBar progressBar;
	}
}
