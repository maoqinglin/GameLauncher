package com.ireadygo.app.gamelauncher.ui;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class SimpleImageLoadingListener implements ImageLoadingListener{

	@Override
	public void onLoadingCancelled(String url, View view) {
		
	}

	@Override
	public void onLoadingComplete(String url, View view, Bitmap bitmap) {
		
	}

	@Override
	public void onLoadingFailed(String url, View view, FailReason failReason) {
		
	}

	@Override
	public void onLoadingStarted(String url, View view) {
		
	}

}
