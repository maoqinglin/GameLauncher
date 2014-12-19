package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;

public class StoreCategoryAdapter extends BaseAnimatorAdapter {
	private List<AppEntity> mAppList = new ArrayList<AppEntity>();
	private Context mContext;
	private View mCurrentItemView;

	private static int[][] sIconAndBgIds = {
			{ R.drawable.category_icon2, R.drawable.category_icon_background_365b0b, 1 },
			{ R.drawable.category_icon3, R.drawable.category_icon_background_1b4971, 6 },
			{ R.drawable.category_icon4, R.drawable.category_icon_background_365b0b, 2 },
			{ R.drawable.category_icon5, R.drawable.category_icon_background_2b2b55, 4 },
			{ R.drawable.category_icon6, R.drawable.category_icon_background_483028, 7 },
			{ R.drawable.category_icon7, R.drawable.category_icon_background_483028, 8 },
			{ R.drawable.category_icon8, R.drawable.category_icon_background_365b0b, 5 },
			{ R.drawable.category_icon9, R.drawable.category_icon_background_483028, 3 },
			{ R.drawable.category_icon1, R.drawable.category_icon_background_2b2b55, 10010 } };

	private static List<Category> sCategories = new ArrayList<StoreCategoryAdapter.Category>();

	static {
		Resources resources = GameLauncherApplication.getApplication().getResources();
		String[] titles = resources.getStringArray(R.array.category_titles);
		String[] intros = resources.getStringArray(R.array.category_intros);
		for (int i = 0; i < sIconAndBgIds.length; i++) {
			Category category = new Category();
			int[] iconAndBgId = sIconAndBgIds[i];
			category.iconId = iconAndBgId[0];
			category.bgId = iconAndBgId[1];
			category.id = iconAndBgId[2];
			category.title = titles[i];
			category.intro = intros[i];
			sCategories.add(category);
		}
	}

	public StoreCategoryAdapter(List<AppEntity> appList, HListView mHListView, Context context) {
		super(mHListView);
		if (appList != null) {
			this.mAppList = appList;
		}
		this.mContext = context;
		setAnimatorDuration(Config.Animator.DURATION_SHORT * 3 / 2);
		setAnimatorDelay(Config.Animator.DELAY_SHORT * 3 / 2);
	}

	@Override
	public int getCount() {
		return sIconAndBgIds.length;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.store_category_item, parent, false);
			holder.background = (ImageView) convertView.findViewById(R.id.background);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.intro = (TextView) convertView.findViewById(R.id.intro);
			holder.titleLayout = convertView.findViewById(R.id.titleLayout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Category category = sCategories.get(position);
		holder.icon.setImageResource(category.iconId);
		holder.title.setText(category.title);
		holder.intro.setText(category.intro);
		holder.background.setImageResource(category.bgId);
		return convertView;
	}

	public int getCategoryId(int position) {
		return sCategories.get(position).id;
	}

	class ViewHolder {
		ImageView background;
		ImageView icon;
		TextView title;
		TextView intro;
		View titleLayout;
	}

	private static class Category {
		int iconId;
		int bgId;
		String title;
		String intro;
		int id;
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
		return createAnimator(holder, listener, 0.333f, 1.08f, 1.12f, 1.05f, 1,
				Config.StoreDetail.CATEGORY_TITLE_SELECTED_TRANSLATE_Y);
	}

	@Override
	protected Animator unselectedAnimator(View view) {
		if (view == null) {
			return null;
		}
		final int position = getListView().getPositionForView(view);
		if(position < 0 || position > getCount()){
			return null;
		}
		final ViewHolder holder = (ViewHolder) view.getTag();
		AnimatorListener listener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				holder.background.setImageResource(sCategories.get(position).bgId);
			}
		};
		return createAnimator(holder, listener, 0.333f, 1, 1, 1, 0.9f,
				Config.StoreDetail.CATEGORY_TITLE_UNSELECTED_TRANSLATE_Y);
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
		ObjectAnimator gameNameAnim = ObjectAnimator.ofPropertyValuesHolder(holder.titleLayout, txtScaleXHolder,
				txtScaleYHolder, txtTranslateYHolder);
		animSet.playTogether(gameBgAnim, gameIconXAnim, gameIconYAnim, gameNameAnim);
		animSet.setInterpolator(new AccelerateInterpolator());
		if (listener != null) {
			animSet.addListener(listener);
		}
		return animSet;
	}
}
