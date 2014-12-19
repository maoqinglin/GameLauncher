package com.ireadygo.app.gamelauncher.ui.detail;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ScreenPictureActivity extends BaseActivity {

	private ViewPager mViewPager;
	private ImageLoader mImageLoader;
	private List<ImageView> mViews = new ArrayList<ImageView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_picture_activity);
		final AppEntity appEntity = (AppEntity)getIntent().getParcelableExtra("APP");
		if(appEntity != null) {
			mImageLoader = GameLauncher.instance().getGameInfoHub().getImageLoader();
			for (int i = 0; i < appEntity.getSceenshotUrlList().size(); i++) {
				final ImageView imageView = new ImageView(this);
				mImageLoader.displayImage(appEntity.getSceenshotUrlList().get(i), imageView, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						if (loadedImage.getWidth() < loadedImage.getHeight()) {
							Matrix matrix = new Matrix();
							matrix.reset();
							matrix.setRotate(-90);
							imageView.setImageBitmap(Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(),
									loadedImage.getHeight(), matrix, true));
						} else {
							imageView.setImageBitmap(loadedImage);
						}
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						
					}
				});
				mViews.add(imageView);
			}

			mViewPager = (ViewPager) findViewById(R.id.viewpager);
			mViewPager.setAdapter(new ScreenPageAdapter(mViews));
			mViewPager.setCurrentItem(getIntent().getIntExtra("NO", 0));
		}
	}

	@Override
	public boolean onBackKey() {
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		return true;
	}

	private class ScreenPageAdapter extends PagerAdapter {

		private List<ImageView> mList = new ArrayList<ImageView>();

		public ScreenPageAdapter(List<ImageView> views) {
			super();
			mList = views;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewGroup)container).removeView(mList.get(position));
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewGroup)container).addView(mList.get(position), 0);
			return mList.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}
		
	}
	
	public static void startSelf(Context context,AppEntity appEntity,int pictureIndex){
		Intent intent = new Intent(context, ScreenPictureActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("APP", appEntity);
		intent.putExtra("NO", pictureIndex);
		intent.putExtras(bundle);
		SoundPoolManager.instance(context).play(SoundPoolManager.SOUND_ENTER);
		context.startActivity(intent);
	}
}
