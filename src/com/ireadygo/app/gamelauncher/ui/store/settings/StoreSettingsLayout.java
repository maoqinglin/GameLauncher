package com.ireadygo.app.gamelauncher.ui.store.settings;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.store.StoreBaseContentLayout;
import com.ireadygo.app.gamelauncher.ui.store.StoreDetailActivity;

public class StoreSettingsLayout extends StoreBaseContentLayout {
	private static final int WIFI_INDEX = 0;// WIFI下载
	private static final int AUTO_DELETE_PKG_INDEX = 1;// 自动删除安装包
	private static final int UPDATE_NOTIFY_INDEX = 2;// 游戏更新提醒
	private static final int AIRDROP_AUTO_INSTALL_INDEX = 3;// 空投游戏自动下载安装
	private static final String CHECKED_TEXT = " ON ";
	private static final String UNCHECKED_TEXT = "OFF";
	private ListView mListView;
	private StoreSettingsAdapter mAdapter;
	private List<StoreSetting> mSettingList = new ArrayList<StoreSettingsLayout.StoreSetting>();
	private View mSelectedView;

	public StoreSettingsLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StoreSettingsLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StoreSettingsLayout(Context context, int layoutTag, StoreDetailActivity storeFragment) {
		super(context, layoutTag, storeFragment);
		init();
	}

	@Override
	protected void init() {
		super.init();
		LayoutInflater.from(getContext()).inflate(R.layout.store_settings_layout, this, true);
		initSettingsData();
		mListView = (ListView) findViewById(R.id.storeSettingsList);
		mAdapter = new StoreSettingsAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mAdapter.switchChecked(view);
			}
		});
		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
				mSelectedView = view;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				mSelectedView = null;
			}
		});
	}

	private void initSettingsData() {
		String[] settingTitles = getResources().getStringArray(R.array.store_settings_titles);
		for (int i = 0; i < settingTitles.length; i++) {
			StoreSetting setting = new StoreSetting();
			setting.title = settingTitles[i];
			if (i == WIFI_INDEX) {
				setting.isChecked = true;
			}
			mSettingList.add(setting);
		}
		//移除不需要的选项
		if (GameLauncherConfig.IGNORE_NETWORTYPE) {
			mSettingList.remove(WIFI_INDEX);
		}
	}

	private class StoreSettingsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mSettingList.size();
		}

		@Override
		public Object getItem(int position) {
			return mSettingList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.store_settings_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.checkBtn = (CheckedTextView) convertView.findViewById(R.id.toggleBtn);
			convertView.setTag(holder);
			StoreSetting setting = mSettingList.get(position);
			holder.title.setText(setting.title);
			setViewCheckedState(setting.isChecked, holder);
			return convertView;
		}

		class ViewHolder {
			TextView title;
			CheckedTextView checkBtn;
		}

		void switchChecked(View view) {
			ViewHolder holder = (ViewHolder) view.getTag();
			setViewCheckedState(!holder.checkBtn.isChecked(), holder);
		}

		private void setViewCheckedState(boolean isChecked, ViewHolder holder) {
			if (isChecked) {
				holder.checkBtn.setChecked(true);
				holder.checkBtn.setText(CHECKED_TEXT);
				Drawable drawable = getResources().getDrawable(R.drawable.store_settings_switch_selector);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				holder.checkBtn.setCompoundDrawables(null, null, drawable, null);
			} else {
				holder.checkBtn.setChecked(false);
				holder.checkBtn.setText(UNCHECKED_TEXT);
				Drawable drawable = getResources().getDrawable(R.drawable.store_settings_switch_selector);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				holder.checkBtn.setCompoundDrawables(drawable, null, null, null);
			}
		}
	}

	private class StoreSetting {
		String title;
		boolean isChecked;
	}

	@Override
	protected boolean isCurrentFocus() {
		return mListView.hasFocus();
	}

	@Override
	public boolean onSunKey() {
		mAdapter.switchChecked(mSelectedView);
		return true;
	}


}
