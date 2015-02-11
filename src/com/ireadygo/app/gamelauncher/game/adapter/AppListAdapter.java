package com.ireadygo.app.gamelauncher.game.adapter;

import java.util.List;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.game.info.ItemInfo;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.GameIconView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;

public abstract class AppListAdapter extends BaseAnimatorAdapter {
	public abstract List<ItemInfo> getList();

	public AppListAdapter(HListView listView) {
		super(listView);
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = newView(position, convertView, parent);
		}

		bindView(position, convertView);
		return convertView;
	};

	public abstract View newView(int position, View convertView, ViewGroup parent);

	public abstract void bindView(int position, View convertView);
	

	@Override
	protected Animator selectedAnimator(View view) {
		return ((GameIconView) view).selectedAnimator();
	}

	@Override
	protected Animator unselectedAnimator(View view) {
		return ((GameIconView) view).unselectedAnimator();
	}
}
