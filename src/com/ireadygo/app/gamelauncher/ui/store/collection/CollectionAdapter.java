package com.ireadygo.app.gamelauncher.ui.store.collection;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.CollectionItem;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CollectionAdapter extends BaseAnimatorAdapter {
	private List<CollectionItem> mAppList = new ArrayList<CollectionItem>();
	private Context mContext;

	public CollectionAdapter(List<CollectionItem> appList, HListView hListView, Context context) {
		super(hListView);
		if (appList != null) {
			this.mAppList = appList;
		}
		this.mContext = context;
		setAnimatorDuration(Config.Animator.DURATION_SHORT * 3 / 2);
		setAnimatorDelay(Config.Animator.DELAY_SHORT * 3 / 2);
	}

	@Override
	public int getCount() {
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.store_collection_item, parent, false);
			holder.background = (ImageView) convertView.findViewById(R.id.background);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.iconLayout = convertView.findViewById(R.id.iconLayout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CollectionItem app = mAppList.get(position);
		String iconUrl = app.getPosterIconUrl();
		if (TextUtils.isEmpty(iconUrl)) {
			iconUrl = app.getIconUrl();
		}
		ImageLoader.getInstance().displayImage(iconUrl, holder.icon);
		return convertView;
	}

	private Animator createAnimator(AnimatorListener listener, ViewHolder holder, float pivotY, float bgScaleX,
			float bgScaleY, float iconScale) {
		holder.background.setPivotX(holder.background.getWidth() / 2);
		holder.background.setPivotY(holder.background.getHeight() * pivotY);
		AnimatorSet animatorSet = new AnimatorSet();

		PropertyValuesHolder bgScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, bgScaleX);
		PropertyValuesHolder bgScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, bgScaleY);
		ObjectAnimator bgScaleAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.background, bgScaleXHolder,
				bgScaleYHolder);

		PropertyValuesHolder iconScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, iconScale);
		PropertyValuesHolder iconScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, iconScale);
		ObjectAnimator iconScaleAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.iconLayout, iconScaleXHolder,
				iconScaleYHolder);
		if (listener != null) {
			animatorSet.addListener(listener);
		}
		animatorSet.playTogether(bgScaleAnimator, iconScaleAnimator);
		return animatorSet;
	}

	class ViewHolder {
		View iconLayout;
		ImageView icon;
		ImageView background;
	}

	@Override
	protected Animator selectedAnimator(View view) {
		if (view == null) {
			return null;
		}
		final ViewHolder holder = (ViewHolder) view.getTag();
		AnimatorListener listener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				holder.background.setImageResource(R.drawable.corner_red_shape);
			}
		};
		return createAnimator(listener, holder, 0.333f, 1.08f, 1.135f, 1.05f);
	}

	@Override
	protected Animator unselectedAnimator(View view) {
		if (view == null) {
			return null;
		}
		final ViewHolder holder = (ViewHolder) view.getTag();
		AnimatorListener listener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				holder.background.setImageDrawable(new ColorDrawable(color.transparent));
			}
		};
		return createAnimator(listener, holder, 0.333f, 1, 1, 1);
	}
}
