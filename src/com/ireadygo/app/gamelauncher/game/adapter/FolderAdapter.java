package com.ireadygo.app.gamelauncher.game.adapter;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.game.adapter.GameModel.DataType;
import com.ireadygo.app.gamelauncher.game.adapter.GameAdapter.ViewHolder;
import com.ireadygo.app.gamelauncher.game.info.ItemInfo;
import com.ireadygo.app.gamelauncher.game.info.ShortcutInfo;
import com.ireadygo.app.gamelauncher.game.utils.Utilities;
import com.ireadygo.app.gamelauncher.ui.widget.GameIconView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;

public class FolderAdapter extends AppListAdapter {

	private Context mContext;
	private ArrayList<ShortcutInfo> mFolderContentList;

	public FolderAdapter(Context context, HListView hListView, ArrayList<ShortcutInfo> infos) {
		super(hListView);
		mContext = context;
		this.mFolderContentList = infos;
	}

	@Override
	public List<ItemInfo> getList() {
		return null;
	}

	public List<ShortcutInfo> getFolderList() {
		return mFolderContentList;
	}

	@Override
	public int getCount() {
		return mFolderContentList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFolderContentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	@Override
	public View newView(int position, View convertView, ViewGroup parent) {
		GameIconView gameIcon = (GameIconView) LayoutInflater.from(mContext).inflate(R.layout.game_item, parent, false);
		FolderViewHolder folderHolder = new FolderViewHolder();
		folderHolder.gameIcon = gameIcon;
		folderHolder.gameIcon.setDataType(DataType.TYPE_APP);
		gameIcon.setTag(folderHolder);
		return gameIcon;
	}

	@Override
	public void bindView(int position, View convertView) {
		FolderViewHolder folderHolder = (FolderViewHolder) convertView.getTag();
		if (null != folderHolder) {
			if (mFolderContentList.size() > 0 && position < mFolderContentList.size()) {
				folderHolder.itemInfo = mFolderContentList.get(position);
				// holder.itemInfo.setAppIcon(BitmapFactory.decodeResource(mContext.getResources(),
				// R.drawable.poster));
				if (mFolderContentList.size() > 0 && position < mFolderContentList.size()) {
					folderHolder.gameIcon.getGameImg().setImageBitmap(mFolderContentList.get(position).getAppIcon());
					folderHolder.gameIcon.getGameNameTxt().setText(mFolderContentList.get(position).getTitle());
				}
			}
		}
	}

	public static class FolderViewHolder {
		public GameIconView gameIcon;
		public ItemInfo itemInfo;
	}
}
