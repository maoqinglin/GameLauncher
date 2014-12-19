package com.ireadygo.app.gamelauncher.ui.store.recommend;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class StoreRecommendAdapter extends BaseAnimatorAdapter {
	private List<AppEntity> mAppList = new ArrayList<AppEntity>();
	private Context mContext;
	private View mCurrentItemView;

	public StoreRecommendAdapter(List<AppEntity> appList, HListView hListView, Context context) {
		super(hListView);
		if (appList != null) {
			this.mAppList = appList;
		}
		this.mContext = context;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.store_recomend_item, parent, false);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.playNumbers = (TextView) convertView.findViewById(R.id.playNumbers);
			holder.nameLayout = (ViewGroup) convertView.findViewById(R.id.nameLayout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AppEntity app = mAppList.get(position);
		ImageLoader.getInstance().displayImage(app.getRemoteIconUrl(), holder.icon);
		holder.title.setText(app.getName());
		holder.playNumbers.setText("6584545");
		holder.nameLayout.setVisibility(View.INVISIBLE);
		return convertView;
	}

	void onItemSelected(final View view) {
		if (mCurrentItemView != null) {
			doUnselectedAnimator(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					((ViewHolder) mCurrentItemView.getTag()).nameLayout.setVisibility(View.INVISIBLE);
					((ViewHolder) view.getTag()).nameLayout.setVisibility(View.VISIBLE);
					doSelectedAnimator(null, view);
					mCurrentItemView = view;
				}
			}, mCurrentItemView);
		} else {
			((ViewHolder) view.getTag()).nameLayout.setVisibility(View.VISIBLE);
			doSelectedAnimator(null, view);
			mCurrentItemView = view;
		}
	}

	void onNothingSelected(final AnimatorListener listener) {
		if (mCurrentItemView != null) {
			doUnselectedAnimator(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					((ViewHolder) mCurrentItemView.getTag()).nameLayout.setVisibility(View.INVISIBLE);
					mCurrentItemView = null;
					if (listener != null) {
						listener.onAnimationEnd(animation);
					}
				}
			}, mCurrentItemView);
		}
	}

	private void doUnselectedAnimator(AnimatorListener listener, View view) {
		doAnimator(listener, view, 1.0f, 1.0f, 0);
	}

	private void doSelectedAnimator(AnimatorListener listener, View view) {
		doAnimator(listener, view, 1.1f, 1.1f, 10);
	}

	private void doAnimator(AnimatorListener listener, View view, float scaleX, float scaleY, int translateY) {
		ViewHolder holder = (ViewHolder) view.getTag();
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, scaleX);
		ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, scaleY);
		ObjectAnimator animatorNameLayoutTranslateY = ObjectAnimator.ofFloat(holder.nameLayout, View.TRANSLATION_Y,
				translateY);
		animatorSet.setDuration(Config.Animator.DURATION_SHORT / 2);
		if (listener != null) {
			animatorSet.addListener(listener);
		}
		animatorSet.playTogether(animatorScaleX, animatorScaleY, animatorNameLayoutTranslateY);
		animatorSet.start();
	}

	class ViewHolder {
		ImageView icon;
		TextView title;
		TextView playNumbers;
		ViewGroup nameLayout;
	}

	@Override
	protected Animator selectedAnimator(View view) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Animator unselectedAnimator(View view) {
		// TODO Auto-generated method stub
		return null;
	}
}
