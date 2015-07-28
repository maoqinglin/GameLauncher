package com.ireadygo.app.gamelauncher.ui.store;

import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.item.AppItem;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class StoreAppNormalAdapter extends BaseAnimatorAdapter {
	private LayoutInflater mInflater;
	private List<AppEntity> mAppList;
	private Bitmap mDefaultBmp;
	private Context mContext;
	private ImageLoader mImageLoader;

	public StoreAppNormalAdapter(Context context, HListView hListView, List<AppEntity> appList) {
		super(hListView);
		mContext = context;
		this.mAppList = appList;
		mInflater = LayoutInflater.from(context);
		mDefaultBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.snail_icon_default);
		mImageLoader = GameInfoHub.instance(mContext).getImageLoader();
	}

	@Override
	public int getCount() {
		if (mAppList == null) {
			return 0;
		}
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
			convertView = new AppItem(mContext);
			holder = new ViewHolder();
			holder.background = (ImageView) convertView.findViewById(R.id.background);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AppEntity app = mAppList.get(position);
		String iconUrl = app.getPosterIconUrl();
		holder.icon.setImageBitmap(mDefaultBmp);
		if (TextUtils.isEmpty(iconUrl)) {
			iconUrl = app.getRemoteIconUrl();
		}
		mImageLoader.displayImage(iconUrl, holder.icon);
		holder.title.setText(app.getName());
		return convertView;
	}

	private class ViewHolder {
		ImageView background;
		ImageView icon;
		TextView title;
	}

	@Override
	protected Animator selectedAnimator(View view) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.background.setImageResource(R.drawable.corner_red_shape);
		return createAnimator(holder, null, 0.25f, 1.2f, 1.3226f, 1.12f, 1,
				Config.StoreDetail.APP_NORMAL_TITLE_SELECTED_TRANSLATE_Y);
	}

	@Override
	protected Animator unselectedAnimator(View view) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.background.setImageResource(R.drawable.corner_black_shape);
		return createAnimator(holder, null, 0.25f, 1, 1, 1, 0.8f,
				Config.StoreDetail.APP_NORMAL_TITLE_UNSELECTED_TRANSLATE_Y);
	}

	private Animator createAnimator(ViewHolder holder, AnimatorListener listener, float bgPivotY, float bgScaleX,
			float bgScaleY, float icScale, float titleScale, float titleTranslateY) {
		AnimatorSet animSet = new AnimatorSet();
		holder.background.setPivotX(holder.background.getWidth() / 2);
		holder.background.setPivotY(holder.background.getHeight() * bgPivotY);
		// 背景动画
		PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, bgScaleX);
		PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, bgScaleY);
		ObjectAnimator gameBgAnim = ObjectAnimator
				.ofPropertyValuesHolder(holder.background, scaleXHolder, scaleYHolder);

		// 游戏海报动画
		ObjectAnimator gameIconXAnim = ObjectAnimator.ofFloat(holder.icon, View.SCALE_X, icScale);
		ObjectAnimator gameIconYAnim = ObjectAnimator.ofFloat(holder.icon, View.SCALE_Y, icScale);

		// 游戏名称动画
		PropertyValuesHolder txtScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, titleScale);
		PropertyValuesHolder txtScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, titleScale);
		PropertyValuesHolder txtTranslateYHolder = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, titleTranslateY);
		ObjectAnimator gameNameAnim = ObjectAnimator.ofPropertyValuesHolder(holder.title, txtScaleXHolder,
				txtScaleYHolder, txtTranslateYHolder);
		animSet.playTogether(gameBgAnim, gameIconXAnim, gameIconYAnim, gameNameAnim);
		animSet.setInterpolator(new AccelerateInterpolator());
		if (listener != null) {
			animSet.addListener(listener);
		}
		return animSet;
	}
}
