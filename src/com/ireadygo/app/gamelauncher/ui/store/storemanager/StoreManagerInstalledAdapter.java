package com.ireadygo.app.gamelauncher.ui.store.storemanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.ui.item.AppItem;
import com.ireadygo.app.gamelauncher.ui.item.AppItem.AppItemHolder;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemLongClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class StoreManagerInstalledAdapter implements HMultiBaseAdapter {

	private Context mContext;
	private List<AppEntity> mAppList = new ArrayList<AppEntity>();
	private HMultiListView mHMultiListView;
	private int mListViewNum;
	private PackageManager mPkgManager;

	public StoreManagerInstalledAdapter(Context context, List<AppEntity> list, int listViewNum, HMultiListView hListView) {
		mHMultiListView = hListView;
		mListViewNum = listViewNum;
		mContext = context;
		mAppList.clear();
		this.mAppList = list;
		mPkgManager = mContext.getPackageManager();
	}

	@Override
	public Object getItem(int position) {
		if (mAppList == null || position > mAppList.size() - 1) {
			return null;
		}
		return mAppList.get(position);
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
		final AppItemHolder holder = ((AppItem) convertView).getHolder();
		if (position < mAppList.size()) {
			AppEntity app = mAppList.get(position);
			holder.title.setText(app.getName());
			Log.d("liu.js", "displayIcon--start time=" + System.currentTimeMillis());
			displayIcon(holder, app);
			Log.d("liu.js", "displayIcon--end time=" + System.currentTimeMillis());
		}
	}

	private void displayIcon(AppItemHolder holder, AppEntity app) {
		String pkgName = app.getPkgName();
		Bitmap icon = GameData.getInstance(mContext).getPosterIconByPkgName(pkgName);
		if (icon == null) {
			// PackageInfo info = PackageUtils.getPkgInfo(mContext,
			// app.getPkgName());
			// if (info != null) {
			// icon = new IconDecorater(mContext).decorateIcon(info);
			// }
			Intent intent = mPkgManager.getLaunchIntentForPackage(pkgName);
			if (intent != null) {
				icon = GameLauncherAppState.getInstance(mContext).getIconCache().getIcon(intent);
			}
		}
		if (icon == null) {
			holder.icon.setImageResource(R.drawable.snail_icon_default);
		} else {
			holder.icon.setImageBitmap(icon);
		}
	}

	OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			mHMultiListView.notifyDataSetChanged();
			return true;
		}
	};

	@Override
	public BaseAdapter getAdapter() {
		return mHMultiListView.getAdapter();
	}

	@Override
	public int getHListNum() {
		return mListViewNum;
	}

	@Override
	public List<AppEntity> getData() {
		return mAppList;
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
