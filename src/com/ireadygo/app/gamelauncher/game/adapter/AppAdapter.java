package com.ireadygo.app.gamelauncher.game.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.game.info.ItemInfo;
import com.ireadygo.app.gamelauncher.game.info.ShortcutInfo;
import com.ireadygo.app.gamelauncher.ui.item.AppItem;
import com.ireadygo.app.gamelauncher.ui.item.AppItem.AppItemHolder;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemLongClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;

public class AppAdapter implements HMultiBaseAdapter {

	private Context mContext;
	private List<ItemInfo> appList = new LinkedList<ItemInfo>();
	private boolean mIsLongClickable;
	private HMultiListView mHMultiListView;
	private int mListViewNum;

	public AppAdapter(Context context, List<ItemInfo> list, int listViewNum, HMultiListView hListView) {
		mHMultiListView = hListView;
		mListViewNum = listViewNum;
		if(mHMultiListView != null){
			mHMultiListView.setOnItemLongClickListener(mOnItemLongClickListener);
		}
		mContext = context;
		appList.clear();
		this.appList = list;
	}

	@Override
	public Object getItem(int position) {
		if (appList == null || position > appList.size() - 1) {
			return null;
		}
		return appList.get(position);
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
		convertView = new AppItem(mContext);
		return convertView;
	}

	public void bindView(final int position, View convertView) {
		convertView.setVisibility(View.VISIBLE);
		final AppItemHolder holder = ((AppItem)convertView).getHolder();
		if (null != holder) {
			if (appList.size() > 0 && position < appList.size()) {
				if(appList.get(position).getAppIcon() != null){
					holder.icon.setImageBitmap(appList.get(position).getAppIcon());
					holder.title.setText(appList.get(position).getTitle());
					if (appList.get(position).getTitle().toString().contains("太极熊猫")) {
						AppEntity app = GameData.getInstance(mContext).getGameById("38");
						if (app == null) {
							app = GameData.getInstance(mContext).getGameByPkgName("com.snailgames.taiji");
						}
						if (app != null && !TextUtils.isEmpty(app.getLocalIconUrl())) {
							holder.icon.setImageBitmap(PictureUtil.readBitmap(mContext, app.getLocalIconUrl()));
						}
					}

					updateDeleteView(holder,appList.get(position));
					holder.uninstallIcon.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							PackageUtils.unInstallApp(mContext, appList.get(position).packageName);
						}
					});
				}
			}
		}
	}

	public void updateDeleteView(AppItemHolder holder, ItemInfo info) {
		if (isLongClickable() && (info instanceof ShortcutInfo && !info.isSystemApp)) {
			holder.uninstallIcon.setVisibility(View.VISIBLE);
		} else {
			holder.uninstallIcon.setVisibility(View.INVISIBLE);
		}
	}

	OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			mIsLongClickable = true;
			mHMultiListView.notifyDataSetChanged();
			return true;
		}
	};

	public void unDisplayGameDeleteView() {
		if (isLongClickable()) {
			setIsLongClickable(false);
			mHMultiListView.notifyDataSetChanged();
		}
	}

	public boolean isLongClickable() {
		return mIsLongClickable;
	}

	public void setIsLongClickable(boolean isLongClickable) {
		this.mIsLongClickable = isLongClickable;
	}

	@Override
	public BaseAdapter getAdapter() {
		return mHMultiListView.getAdapter();
	}

	@Override
	public int getHListNum() {
		return mListViewNum;
	}

	@Override
	public List<ItemInfo> getData() {
		return appList;
	}

	@Override
	public View getEmptyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = newView(position, convertView, parent);
		}
		convertView.setVisibility(View.GONE);
		return convertView;
	}

}
