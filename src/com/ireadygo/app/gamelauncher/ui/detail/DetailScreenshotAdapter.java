package com.ireadygo.app.gamelauncher.ui.detail;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.SimpleImageLoadingListener;
import com.ireadygo.app.gamelauncher.ui.item.ImageItem;
import com.ireadygo.app.gamelauncher.ui.item.ImageItem.ImageItemHolder;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailScreenshotAdapter extends BaseAdapter {
	private List<String> mUrlList;
	private Context mContext;
	private AppEntity mAppEntity;
	private int mScreenshotMaxWidth;
	private int mScreenshotMaxHeight;

	public DetailScreenshotAdapter(HListView listView, List<String> urlList, Context context) {
		this.mUrlList = urlList;
		this.mContext = context;
		mScreenshotMaxWidth = context.getResources().getDimensionPixelOffset(R.dimen.detail_screenshot_width);
		mScreenshotMaxHeight = context.getResources().getDimensionPixelOffset(R.dimen.detail_screenshot_height);
	}

	public void initParams(List<String> urlList, AppEntity appEntity) {
		this.mUrlList = urlList;
		this.mAppEntity = appEntity;
	}

	@Override
	public int getCount() {
		if (isDataEmpty()) {
			return 5;
		}
		return mUrlList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ImageItemHolder holder;
		if (convertView == null) {
			convertView = new ImageItem(mContext);
		}
		final ImageItem item = ((ImageItem) convertView);
		holder = item.getHolder();
		if (isDataEmpty()) {
			if(mAppEntity != null && !mAppEntity.isScreenshotVertical()){
				holder.icon.setImageResource(R.drawable.detail_screenshot_default_h);
			}else{
				holder.icon.setImageResource(R.drawable.detail_screenshot_default_v);
			}
		} else {
			ImageLoader.getInstance().loadImage(mUrlList.get(position), new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String url, View view, Bitmap bitmap) {
					int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.detail_screenshot_padding);
					// 计算图片的宽高
					int widthMax = mScreenshotMaxHeight - padding * 2;// 由于服务器传过来的图片都是宽450，高800，宽<高
					int heightMax = mScreenshotMaxWidth - padding * 2;
					int[] widthAndHeight = new int[2];
					float normalRatio = ((float) widthMax) / heightMax;// 正常宽高比
					float currentRatio = ((float) bitmap.getWidth()) / bitmap.getHeight();// 当前宽高比
					float fraction = 1;
					if (currentRatio >= normalRatio) {// 当前宽高比大于等于标准宽高比，以宽为基准进行缩放
						fraction = ((float) widthMax) / bitmap.getWidth();
						widthAndHeight[0] = widthMax + padding * 2;
						widthAndHeight[1] = (int) (bitmap.getHeight() * fraction) + padding * 2;
					} else {// 以高为基准进行缩放
						fraction = ((float) heightMax) / bitmap.getHeight();
						widthAndHeight[0] = (int) (bitmap.getWidth() * fraction) + padding * 2;
						widthAndHeight[1] = heightMax + padding * 2;
					}
					if (mAppEntity.isScreenshotVertical()) {
						holder.icon.setImageBitmap(bitmap);
						AbsHListView.LayoutParams params = new AbsHListView.LayoutParams(widthAndHeight[0],
								widthAndHeight[1]);
						item.setLayoutParams(params);
						if (position == 0) {
							HListView listView = (HListView) parent;
							int paddingTop = mContext.getResources().getDimensionPixelOffset(
									R.dimen.detail_list_padding_top_vertical);
							((HListView) parent).setPadding(listView.getPaddingLeft(), paddingTop,
									listView.getPaddingRight(), listView.getPaddingBottom());
							int dividerWidthV = mContext.getResources().getDimensionPixelOffset(
									R.dimen.detail_screenshot_divide_width_vertical);
							((HListView) parent).setDividerWidth(dividerWidthV);
						}
					} else {
						Matrix matrix = new Matrix();
						matrix.setRotate(-90);
						Bitmap bmpTmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
								matrix, true);
						holder.icon.setImageBitmap(bmpTmp);
						AbsHListView.LayoutParams params = new AbsHListView.LayoutParams(widthAndHeight[1],
								widthAndHeight[0]);
						item.setLayoutParams(params);
					}
				}
			});
		}
		return convertView;
	}

	private boolean isDataEmpty() {
		return (mUrlList == null || mUrlList.isEmpty());
	}

//	@Override
//	protected Animator selectedAnimator(View view) {
//		if (view == null) {
//			return null;
//		}
//		final ViewHolder holder = (ViewHolder) view.getTag();
//		int scalePadding = mContext.getResources().getDimensionPixelOffset(R.dimen.detail_screenshot_scale_padding);
//		int iconWidth = holder.icon.getWidth();
//		int iconHeight = holder.icon.getHeight();
//		float iconScale = 1.12f;
//		float bgScaleX = iconScale + ((float) (scalePadding * 2) / iconWidth);
//		float bgScaleY = iconScale + ((float) (scalePadding * 2) / iconHeight);
//		AnimatorListener listener = new AnimatorListenerAdapter() {
//			@Override
//			public void onAnimationStart(Animator animation) {
//				holder.background.setImageResource(R.drawable.corner_red_shape);
//			}
//		};
//		return createScaleAnimator(holder, listener, bgScaleX, bgScaleY, iconScale);
//	}
//
//	@Override
//	protected Animator unselectedAnimator(View view) {
//		if (view == null) {
//			return null;
//		}
//		final ViewHolder holder = (ViewHolder) view.getTag();
//		AnimatorListener listener = new AnimatorListenerAdapter() {
//			@Override
//			public void onAnimationEnd(Animator animation) {
//				holder.background.setImageResource(android.R.color.transparent);
//			}
//		};
//		return createScaleAnimator(holder, listener, 1, 1, 1);
//	}
//
//	private Animator createScaleAnimator(ViewHolder holder, AnimatorListener listener, float bgScaleX, float bgScaleY,
//			float iconScale) {
//		PropertyValuesHolder bgScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, bgScaleX);
//		PropertyValuesHolder bgScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, bgScaleY);
//		ObjectAnimator bgScaleAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.background, bgScaleXHolder,
//				bgScaleYHolder);
//
//		PropertyValuesHolder iconScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, iconScale);
//		PropertyValuesHolder iconScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, iconScale);
//		ObjectAnimator iconScaleAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.icon, iconScaleXHolder,
//				iconScaleYHolder);
//
//		AnimatorSet animator = new AnimatorSet();
//		animator.playTogether(bgScaleAnimator, iconScaleAnimator);
//		if (listener != null) {
//			animator.addListener(listener);
//		}
//		animator.setInterpolator(new AccelerateInterpolator());
//		return animator;
//	}
}
