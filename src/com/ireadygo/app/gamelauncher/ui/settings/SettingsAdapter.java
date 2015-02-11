package com.ireadygo.app.gamelauncher.ui.settings;

import java.util.List;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.SettingsIconView;

public class SettingsAdapter extends BaseAnimatorAdapter {

	private Context mContext;
	List<SettingsItemEntity> settingsList;

	public SettingsAdapter(Context context, HListView hListView, List<SettingsItemEntity> list) {
		super(hListView);
		mContext = context;
		this.settingsList = list;
	}

	public List<SettingsItemEntity> getList() {
		return settingsList;
	}

	@Override
	public int getCount() {
		return settingsList.size();
	}

	@Override
	public Object getItem(int position) {
		return settingsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = newView(position, convertView, parent);
		}
		bindView(position, convertView);
		return convertView;
	}

	public View newView(int position, View convertView, ViewGroup parent) {
		SettingsIconView gameIcon = (SettingsIconView) LayoutInflater.from(mContext).inflate(R.layout.settings_item,
				parent, false);
		ViewHolder holder = new ViewHolder();
		holder.settingsIcon = gameIcon;
		gameIcon.setTag(holder);
		return gameIcon;
	}

	public void bindView(final int position, final View convertView) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (null != holder) {
			holder.settingsItem = settingsList.get(position);
			holder.settingsIcon.setSettingsItemEntity(holder.settingsItem);
		}
	}

	public static class ViewHolder {
		public SettingsIconView settingsIcon;
		public SettingsItemEntity settingsItem;
	}

	@Override
	public Animator selectedAnimator(View view) {
		return ((SettingsIconView) view).selectedAnimation();
	}

	@Override
	public Animator unselectedAnimator(View view) {
		return ((SettingsIconView) view).unselectedAnimation();
	}
}
