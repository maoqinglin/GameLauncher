package com.ireadygo.app.gamelauncher.ui.detail;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
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
		if (mAppEntity == null) {
			return 0;
		}
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
		HListView listView = (HListView) parent;
		final ImageItemHolder holder;
		if (convertView == null) {
			convertView = new ImageItem(mContext);
		}
		View rootView = convertView.findViewById(R.id.root_view);
		final ImageItem item = ((ImageItem) convertView);
		holder = item.getHolder();
		final ImageView iconView = holder.icon;
		if (mAppEntity != null && mAppEntity.isScreenshotVertical()) {
			if (rootView.getWidth() != mScreenshotMaxHeight || rootView.getHeight() != mScreenshotMaxWidth) {
				LayoutParams params = new LayoutParams(mScreenshotMaxHeight, mScreenshotMaxWidth);
				rootView.setLayoutParams(params);
			}
			if (position == 0) {
				int dividerWidthV = mContext.getResources().getDimensionPixelOffset(
						R.dimen.detail_screenshot_divide_width_vertical);
				listView.setDividerWidth(dividerWidthV);
				int paddingTop = mContext.getResources().getDimensionPixelOffset(
						R.dimen.detail_list_padding_top_vertical);
				listView.setPadding(listView.getPaddingLeft(), paddingTop, listView.getPaddingRight(),
						listView.getPaddingBottom());
			}
		} else {
			if (rootView.getWidth() != mScreenshotMaxWidth || rootView.getHeight() != mScreenshotMaxHeight) {
				LayoutParams params = new LayoutParams(mScreenshotMaxWidth, mScreenshotMaxHeight);
				rootView.setLayoutParams(params);
			}
			if (position == 0) {
				int paddingTop = mContext.getResources().getDimensionPixelOffset(R.dimen.detail_list_padding_top_h);
				listView.setPadding(listView.getPaddingLeft(), paddingTop, listView.getPaddingRight(),
						listView.getPaddingBottom());
			}
		}
		if (!isDataEmpty()) {
			ImageLoader.getInstance().loadImage(mUrlList.get(position), new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String url, View view, Bitmap bitmap) {
					if (mAppEntity.isScreenshotVertical()) {
						iconView.setImageBitmap(bitmap);
					} else {
						Matrix matrix = new Matrix();
						matrix.setRotate(-90);
						Bitmap bmpTmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
								matrix, true);
						iconView.setImageBitmap(bmpTmp);
					}
				}
			});
		}
		return convertView;
	}

	private boolean isDataEmpty() {
		return (mUrlList == null || mUrlList.isEmpty());
	}
}
