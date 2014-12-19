package com.ireadygo.app.gamelauncher.ui.store;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.store.StoreFragment.StoreOptionsPoster;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;

public class StoreAdapter extends BaseAnimatorAdapter {
	private LayoutInflater mInflater;
	private List<StoreOptionsPoster> mOptionsPosters;

	public StoreAdapter(Context context, HListView hListView, List<StoreOptionsPoster> optionsPosters) {
		super(hListView);
		this.mOptionsPosters = optionsPosters;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if (mOptionsPosters == null) {
			return 0;
		}
		return mOptionsPosters.size();
	}

	@Override
	public Object getItem(int position) {
		return mOptionsPosters.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.store_item, parent, false);
			holder = new ViewHolder();
			holder.iconView = (ImageView) convertView.findViewById(R.id.icon);
			holder.titleView = (TextView) convertView.findViewById(R.id.title);
			holder.background = (ImageView) convertView.findViewById(R.id.background);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		StoreOptionsPoster optionsPoster = mOptionsPosters.get(position);
		holder.iconView.setImageResource(optionsPoster.drawableId);
		holder.titleView.setText(optionsPoster.titleId);
		convertView.setTag(holder);
		return convertView;
	}

	@Override
	public Animator unselectedAnimator(View view) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		AnimatorListener listener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				holder.background.setImageResource(R.drawable.corner_black_shape);
			}
		};
		return createAnimator(view, listener, 0.333f, 1.0f, 1.0f, 1.0f, 0.8f,
				Config.StoreItem.TITLE_UNSLEECTED_TRANSLATE_Y);
	}

	@Override
	public Animator selectedAnimator(View view) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		AnimatorListener listener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				holder.background.setImageResource(R.drawable.corner_red_shape);
			}
		};
		return createAnimator(view, listener, 0.333f, 1.12f, 1.13f, 1.06f, 1.0f,
				Config.StoreItem.TITLE_SLEECTED_TRANSLATE_Y);
	}

	private Animator createAnimator(View view, AnimatorListener listener, float bgPivotY, float bgScaleX,
			float bgScaleY, float icScale, float titleScale, float titleTranslateY) {
		AnimatorSet animatorSet = new AnimatorSet();
		ViewHolder holder = (ViewHolder) view.getTag();

		PropertyValuesHolder backgroundHolderX = PropertyValuesHolder.ofFloat(View.SCALE_X, bgScaleX);
		PropertyValuesHolder backgroundHolderY = PropertyValuesHolder.ofFloat(View.SCALE_Y, bgScaleY);
		ObjectAnimator backgroundAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.background, backgroundHolderX,
				backgroundHolderY);
		holder.background.setPivotY(bgPivotY * view.getHeight());
		holder.background.setPivotX(view.getWidth() / 2);

		PropertyValuesHolder titleHolderX = PropertyValuesHolder.ofFloat(View.SCALE_X, titleScale);
		PropertyValuesHolder titleHolderY = PropertyValuesHolder.ofFloat(View.SCALE_Y, titleScale);
		PropertyValuesHolder titleHolderTranslateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, titleTranslateY);
		ObjectAnimator titleAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.titleView, titleHolderX,
				titleHolderY, titleHolderTranslateY);

		PropertyValuesHolder iconHolderX = PropertyValuesHolder.ofFloat(View.SCALE_X, icScale);
		PropertyValuesHolder iconHolderY = PropertyValuesHolder.ofFloat(View.SCALE_Y, icScale);
		ObjectAnimator iconAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.iconView, iconHolderX, iconHolderY);

		if (listener != null) {
			animatorSet.addListener(listener);
		}
		animatorSet.playTogether(backgroundAnimator, titleAnimator, iconAnimator);
		return animatorSet;
	}

	private class ViewHolder {
		ImageView iconView;
		TextView titleView;
		ImageView background;
	}
}
