package com.ireadygo.app.gamelauncher.game.adapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.game.adapter.GameModel.DataType;
import com.ireadygo.app.gamelauncher.game.info.ExtendInfo;
import com.ireadygo.app.gamelauncher.game.info.ItemInfo;
import com.ireadygo.app.gamelauncher.game.info.ShortcutInfo;
import com.ireadygo.app.gamelauncher.game.info.ExtendInfo.Function;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemLongClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.GameIconView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;

public class GameAdapter extends AppListAdapter {

	private Context mContext;
	private List<ItemInfo> gameList = new LinkedList<ItemInfo>();
	private boolean mIsLongClickable;
	private HListView mHListView;

	public GameAdapter(Context context, HListView hListView, List<ItemInfo> list) {
		super(hListView);
		mHListView = hListView;
		mHListView.setOnItemLongClickListener(mOnItemLongClickListener);
		mContext = context;
		gameList.clear();
		this.gameList = list;
		if (GameLauncherConfig.SLOT_ENABLE) {
			addExtendItem();
		}
	}

	private void addExtendItem(){
		if(!gameList.isEmpty() && gameList.get(ExtendInfo.POS_GAME_RECOMMEND) instanceof ExtendInfo){
			return;
		}
		gameList.add(ExtendInfo.POS_GAME_RECOMMEND, getRecommandAppInfo());
	}
	public ExtendInfo getRecommandAppInfo(){
		ExtendInfo allAppInfo = new ExtendInfo();
		allAppInfo.function = Function.GAME_RECOMMEND_DOWNLOAD;
		allAppInfo.appIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mygame_recommand_app_normal);
		allAppInfo.title = mContext.getString(R.string.mygame_recommend_app_download);
		return allAppInfo;
	}

	public List<ItemInfo> getList() {
		return gameList;
	}

	@Override
	public int getCount() {
		return gameList.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0) {
			position = 0;
		} else if (position >= getCount()) {
			position = getCount() - 1;
		}
		return gameList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	public View newView(int position, View convertView, ViewGroup parent) {
		GameIconView gameIcon = (GameIconView) LayoutInflater.from(mContext).inflate(R.layout.game_item, parent,
				false);
		ViewHolder holder = new ViewHolder();
		holder.gameIcon = gameIcon;
		holder.gameIcon.setDataType(DataType.TYPE_GAME);
		gameIcon.setTag(holder);
		return gameIcon;
	}

	public void bindView(final int position, final View convertView) {
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		if (null != holder) {
			if (gameList.size() > 0 && position < gameList.size()) {
				if (gameList.get(position).getAppIcon() != null) {
					holder.itemInfo = gameList.get(position);
					if(position == ExtendInfo.POS_GAME_RECOMMEND && GameLauncherConfig.SLOT_ENABLE){
						setFunctionItemState(holder,Function.GAME_RECOMMEND_DOWNLOAD,0.5f,R.color.orange);
					}else{
						setFunctionItemState(holder,null,1f,R.color.white);
					}
					updateDeleteView(holder);
					holder.gameIcon.getGameUninstallImg().setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							PackageUtils.unInstallApp(mContext, holder.itemInfo.packageName);
						}
					});
					
					holder.gameIcon.getGameNameTxt().setText(gameList.get(position).getTitle());
					holder.gameIcon.getGameImg().setImageBitmap(gameList.get(position).getAppIcon());
					if (gameList.get(position).getTitle().toString().contains("太极熊猫")) {
						AppEntity app = GameData.getInstance(mContext).getGameById("38");
						if (app == null) {
							app = GameData.getInstance(mContext).getGameByPkgName("com.snailgames.taiji");
						}
						if (app != null && !TextUtils.isEmpty(app.getLocalIconUrl())) {
							holder.gameIcon.getGameImg().setImageBitmap(PictureUtil.readBitmap(mContext, app.getLocalIconUrl()));
						}
					}
				}
			}
		}
	}
	
	private void setFunctionItemState(ViewHolder holder,Function function,float alpha,int textColorResourceId) {
		holder.gameIcon.setFunction(function);
		holder.gameIcon.getGameViewBg().setAlpha(alpha);
		holder.gameIcon.getGameNameTxt().setTextColor(mContext.getResources().getColor(textColorResourceId));
	}

	public static class ViewHolder {
		public GameIconView gameIcon;
		public ItemInfo itemInfo;
	}

	OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			mIsLongClickable = true;
			updateCurrentDeleteView();
			return true;
		}
	};

	public void updateDeleteView(ViewHolder holder) {
		if (isLongClickable() && (holder.itemInfo instanceof ShortcutInfo && !holder.itemInfo.isSystemApp)) {
			holder.gameIcon.getGameUninstallImg().setVisibility(View.VISIBLE);
//			holder.gameDeleteBg.setVisibility(View.VISIBLE);
		} else {
			holder.gameIcon.getGameUninstallImg().setVisibility(View.INVISIBLE);
//			holder.gameDeleteBg.setVisibility(View.INVISIBLE);
		}
	}

	public void updateCurrentDeleteView() {
		for (int pos = 0; pos < mHListView.getChildCount(); pos++) {
			View view = mHListView.getChildAt(pos);
			if (view != null) {
				ViewHolder holder = (ViewHolder) view.getTag();
				updateDeleteView(holder);
			}
		}
	}
	
	public void unDisplayGameDeleteView() {
		if (isLongClickable()) {
			setIsLongClickable(false);
			updateCurrentDeleteView();
		}
	}

	public boolean isLongClickable() {
		return mIsLongClickable;
	}

	public void setIsLongClickable(boolean isLongClickable) {
		this.mIsLongClickable = isLongClickable;
	}
}
