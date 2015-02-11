package com.ireadygo.app.gamelauncher.ui.widget.mutillistview;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.mygame.adapter.GameModel.DataType;
import com.ireadygo.app.gamelauncher.mygame.info.ItemInfo;
import com.ireadygo.app.gamelauncher.ui.widget.GameIconView;

public class HAdapter extends HMultiBaseAdapter {

	List<?> mData;
	LayoutInflater mInflater;

	public HAdapter() {

	}

	public HAdapter(List<?> data) {
		super(data);
		mData = data;
	}

	@Override
	public int getCount() {
		if (mData != null && !mData.isEmpty()) {
			return mData.size();
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GameIconView gameIcon = (GameIconView) LayoutInflater.from(mContext).inflate(R.layout.game_item, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.gameIcon = gameIcon;
		holder.gameIcon.setDataType(DataType.TYPE_APP);
		gameIcon.setTag(holder);
		holder = (ViewHolder) gameIcon.getTag();
		if (null != holder && mData != null && !mData.isEmpty()) {
			holder.itemInfo = (ItemInfo) mData.get(position);
			if (holder.itemInfo.getAppIcon() != null) {
				holder.gameIcon.getGameImg().setImageBitmap(holder.itemInfo.getAppIcon());
				holder.gameIcon.getGameNameTxt().setText(holder.itemInfo.getTitle());
			}
		}
		return gameIcon;
	}

	public static class ViewHolder {
		public GameIconView gameIcon;
		public ItemInfo itemInfo;
	}

	@Override
	public void setData(List<?> data) {
		mData = data;
	}
}