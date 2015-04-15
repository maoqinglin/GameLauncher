package com.ireadygo.app.gamelauncher.ui.store.storemanager;

import java.text.DecimalFormat;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.helper.AnimatorHelper;
import com.ireadygo.app.gamelauncher.ui.item.BaseAdapterItem;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class StoreManagerItem extends BaseAdapterItem {

	private StoreManagerItemHolder mHolder;
	private Animator mSelectedAnimator;
	private Animator mUnselectedAnimator;

	public StoreManagerItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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
		if (mUnselectedAnimator != null && mUnselectedAnimator.isRunning()) {
			mUnselectedAnimator.cancel();
		}
		mSelectedAnimator = AnimatorHelper.createSelectAnimator(listener, mHolder.background, mHolder.iconLayout, mHolder.title);
		mSelectedAnimator.start();
	}

	@Override
	public void toUnselected(AnimatorListener listener) {
		if (mSelectedAnimator != null && mSelectedAnimator.isRunning()) {
			mSelectedAnimator.cancel();
		}
		mUnselectedAnimator = AnimatorHelper.createUnselectAnimator(listener, mHolder.background, mHolder.iconLayout, mHolder.title);
		mUnselectedAnimator.start();
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.download_item, this, true);
		mHolder = new StoreManagerItemHolder();
		mHolder.iconLayout = (ViewGroup) findViewById(R.id.icon_layout);
		mHolder.background = (ImageView) findViewById(R.id.background);
		mHolder.icon = (ImageView) findViewById(R.id.icon);
		mHolder.title = (TextView) findViewById(R.id.title);

		mHolder.status = (ImageView) findViewById(R.id.manager_item_status);
		mHolder.speedSizeLayout = (LinearLayout) findViewById(R.id.manager_item_speed_layout);
		mHolder.speed = (TextView) findViewById(R.id.manager_item_download_speed);
		mHolder.size = (TextView) findViewById(R.id.manager_item_download_size);
		mHolder.progressBar = (ProgressBar) findViewById(R.id.manager_item_download_progress);
	}

	public StoreManagerItemHolder getHolder() {
		return mHolder;
	}

	public void updateByStateChange(GameState state) {
		switch (state) {
		case TRANSFERING:
			mHolder.status.setVisibility(View.INVISIBLE);
			mHolder.speedSizeLayout.setVisibility(View.VISIBLE);
			break;
		case QUEUING:
			mHolder.status.setImageResource(R.drawable.store_manager_status_queue);
			mHolder.status.setVisibility(View.VISIBLE);
			mHolder.speedSizeLayout.setVisibility(View.INVISIBLE);
			break;
		case PAUSED:
			mHolder.status.setImageResource(R.drawable.store_manager_status_transfering);
			mHolder.status.setVisibility(View.VISIBLE);
			mHolder.speedSizeLayout.setVisibility(View.INVISIBLE);
			break;
		case INSTALLABLE:
		case INSTALLING:
			mHolder.status.setImageResource(R.drawable.store_manager_status_install);
			mHolder.status.setVisibility(View.VISIBLE);
			mHolder.speedSizeLayout.setVisibility(View.INVISIBLE);
			break;
		case UPGRADEABLE:
			mHolder.status.setImageResource(R.drawable.store_manager_status_upgrable);
			mHolder.status.setVisibility(View.VISIBLE);
			mHolder.speedSizeLayout.setVisibility(View.INVISIBLE);
			break;
		case ERROR:
			mHolder.status.setImageResource(R.drawable.store_manager_status_error);
			mHolder.status.setVisibility(View.VISIBLE);
			mHolder.speedSizeLayout.setVisibility(View.INVISIBLE);
			break;
		case DEFAULT:
		default:
			mHolder.status.setVisibility(View.INVISIBLE);
			mHolder.speedSizeLayout.setVisibility(View.INVISIBLE);
			break;
		}
	}

	public void updateProgress(long downloadSize, long totalSize, long speed) {
		if (totalSize <= 0 || downloadSize < 0) {
			return;
		}

		int progress = (int) (downloadSize * 100 / totalSize);
		mHolder.progressBar.setProgress(progress);

		String sizeString = Formatter.formatFileSize(getContext(), downloadSize) + " / "
				+ Formatter.formatFileSize(getContext(), totalSize);
		mHolder.size.setText(sizeString);

		String speedString = Utils.formatSpeedText(speed);
		mHolder.speed.setText(speedString);
	}

	public static class StoreManagerItemHolder {
		public ViewGroup iconLayout;
		public ImageView background;
		public ImageView icon;
		public TextView title;

		public ImageView status;
		public ViewGroup speedSizeLayout;
		public TextView speed;
		public TextView size;
		public ProgressBar progressBar;
	}
}
