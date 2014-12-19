package com.ireadygo.app.gamelauncher.settings;

import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SwitcherAdapter extends BaseAdapter {

	private static HashMap<Integer,SwitcherItem> mSwitcherItems = new HashMap<Integer, SwitcherAdapter.SwitcherItem>();
	private Context mContext;
	private LayoutInflater mInflater;

	public SwitcherAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		initSwitcherList();
	}

	private void initSwitcherList() {
//		mSwitcherItems.clear();
//		mSwitcherItems.put(Switcher.ID_WIFI,new SwitcherItem(Switcher.ID_WIFI,
//				R.drawable.settings_wifi_diabled,
//				mContext.getString(R.string.setting_wifi_title)));
//		mSwitcherItems.put(Switcher.ID_AIRPLANE,new SwitcherItem(Switcher.ID_AIRPLANE,
//				R.drawable.settings_air_mode_disabled,
//				mContext.getString(R.string.setting_air_plane_mode_title)));
//		mSwitcherItems.put(Switcher.ID_DATA,new SwitcherItem(Switcher.ID_DATA,
//				R.drawable.settings_data_connection_disabled,
//				mContext.getString(R.string.setting_data_connection_title)));
//		mSwitcherItems.put(Switcher.ID_BT,new SwitcherItem(Switcher.ID_BT,
//				R.drawable.settings_bt_disabled,
//				mContext.getString(R.string.setting_bt_title)));
//		mSwitcherItems.put(Switcher.ID_BRIGTHNESS,new SwitcherItem(Switcher.ID_BRIGTHNESS,
//				R.drawable.settings_brightness_auto,
//				mContext.getString(R.string.setting_wifi_title)));
//		mSwitcherItems.put(Switcher.ID_SCENE,new SwitcherItem(Switcher.ID_SCENE,
//				R.drawable.settings_scene_normal,
//				mContext.getString(R.string.setting_scene_normal)));
//		mSwitcherItems.put(Switcher.ID_DATA_USAGE,new SwitcherItem(Switcher.ID_DATA_USAGE,
//				R.drawable.settings_data_usage,
//				mContext.getString(R.string.setting_data_usage_title)));
//		mSwitcherItems.put(Switcher.ID_BATTERY,new SwitcherItem(Switcher.ID_BATTERY,
//				R.drawable.settings_battery_100,
//				mContext.getString(R.string.setting_battery_level)));
//
//		mSwitcherItems.put(Switcher.ID_GPS,new SwitcherItem(Switcher.ID_GPS,
//				R.drawable.settings_gps_disabled,
//				mContext.getString(R.string.setting_gps_title)));
//		mSwitcherItems.put(Switcher.ID_TIME_OUT,new SwitcherItem(Switcher.ID_TIME_OUT,
//				R.drawable.settings_timeout_min,
//				mContext.getString(R.string.setting_timeout_title)));
//		mSwitcherItems.put(Switcher.ID_ORIENTATION,new SwitcherItem(Switcher.ID_ORIENTATION,
//				R.drawable.settings_auto_rotation_enabled,
//				mContext.getString(R.string.setting_auto_rotation_title)));
//		mSwitcherItems.put(Switcher.ID_SETTINGS,new SwitcherItem(Switcher.ID_SETTINGS,
//				R.drawable.settings,
//				mContext.getString(R.string.setting_more_title)));

	}



	@Override
	public int getCount() {
		if (null == mSwitcherItems) {
			return 0;
		}
		return mSwitcherItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mSwitcherItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mSwitcherItems.get(position).getSwitcherId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		ViewHolder viewHolder;
//		if (convertView == null) {
//			convertView = mInflater.inflate(R.layout.settings_switcher_item, null);
//			viewHolder = new ViewHolder();
//			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
//			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
//			viewHolder.animation = (ImageView)convertView.findViewById(R.id.animation);
//			convertView.setTag(viewHolder);
//			AbsListView.LayoutParams param = new AbsListView.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
//					mContext.getResources().getInteger(R.integer.settings_gridview_height));
//			convertView.setLayoutParams(param);
//		} else {
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
//		SwitcherItem switcher = mSwitcherItems.get(position);
//		viewHolder.title.setText(switcher.getSwitcherTitle());
//		AnimationDrawable animationDrawable;
//		if (switcher.isStartAnimation()) {
//			viewHolder.animation.setImageResource(switcher.getAnimatorId());
//			viewHolder.animation.setVisibility(View.VISIBLE);
//			viewHolder.icon.setVisibility(View.GONE);
//			animationDrawable = (AnimationDrawable)viewHolder.animation.getDrawable();
//			if (null != animationDrawable && !animationDrawable.isRunning()) {
//				animationDrawable.start();
//			}
//		} else {
//			viewHolder.icon.setImageResource(switcher.getSwitcherIconId());
//			viewHolder.icon.setVisibility(View.VISIBLE);
//			viewHolder.animation.setVisibility(View.GONE);
//			Drawable drawable = viewHolder.animation.getDrawable();
//			if (null != drawable) {
//				animationDrawable = (AnimationDrawable)drawable;
//				if (animationDrawable.isRunning()) {
//					animationDrawable.stop();
//				}
//			}
//		}
		return convertView;
	}


	public class SwitcherItem {
		private int mSwitcherIconId;
		private int mAnimatorId;
		private String mSwitcherTitle;
		private int mSwitcherId;
		private AnimationDrawable mAnimationDrawable;
		private boolean startAnimation;

		public SwitcherItem(int id) {
			mSwitcherId = id;
		}

		public SwitcherItem(int id, int icon, String title) {
			mSwitcherIconId = icon;
			mSwitcherTitle = title;
			mSwitcherId = id;
		}

		public int getSwitcherId() {
			return mSwitcherId;
		}

		public int getSwitcherIconId() {
			return mSwitcherIconId;
		}

		public void setSwitcherIconId(int switcherIconId) {
			mSwitcherIconId = switcherIconId;
		}

		public String getSwitcherTitle() {
			return mSwitcherTitle;
		}

		public void setSwitcherTitle(String switcherTitle) {
			mSwitcherTitle = switcherTitle;
		}

		public AnimationDrawable getAnimationDrawable() {
			return mAnimationDrawable;
		}

		public void setAnimationDrawable(AnimationDrawable animationDrawable) {
			mAnimationDrawable = animationDrawable;
		}

		public boolean isStartAnimation() {
			return startAnimation;
		}

		public void setStartAnimation(boolean startAnimation) {
			this.startAnimation = startAnimation;
		}

		public int getAnimatorId() {
			return mAnimatorId;
		}

		public void setAnimatorId(int mAnimatorId) {
			this.mAnimatorId = mAnimatorId;
		}
	}

	public class ViewHolder {
		public ImageView icon;
		public TextView title;
		public ImageView animation;
		public View verticalLine;
		public View horizontalLine;
	}

}
