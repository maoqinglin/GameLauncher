package com.ireadygo.app.gamelauncher.ui.widget;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class LoadingProgressDialog extends ProgressDialog {

	public LoadingProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public LoadingProgressDialog(Context context) {
		super(context);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void hide() {
		super.hide();
	}

	
}
