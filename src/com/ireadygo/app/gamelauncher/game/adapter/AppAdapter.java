package com.ireadygo.app.gamelauncher.game.adapter;

import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HandshakeCompletedListener;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
		if (GameLauncherConfig.SLOT_ENABLE) {
			addExtendItem();
		}
	}

	public List<ItemInfo> getList() {
		return appList;
	}
	
	private void addExtendItem(){
		if(!appList.isEmpty() && appList.get(ExtendInfo.POS_GAME_ALL) instanceof ExtendInfo){
			return;
		}
	}

	@Override
	public Object getItem(int position) {
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
		GameIconView gameIcon = (GameIconView) LayoutInflater.from(mContext).inflate(R.layout.game_item, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.gameIcon = gameIcon;
		holder.gameIcon.setDataType(DataType.TYPE_APP);
		gameIcon.setTag(holder);
		return gameIcon;
	}

	public void bindView(final int position, View convertView) {
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		if (null != holder) {
			holder.itemInfo = appList.get(position);
			if (appList.size() > 0 && position < appList.size()) {
				if(appList.get(position).getAppIcon() != null){
					if(position == ExtendInfo.POS_GAME_ALL && GameLauncherConfig.SLOT_ENABLE){
						setFunctionItemState(holder,Function.GAME_ALL,0.5f,R.color.app_item_bg_green);
					}else{
						setFunctionItemState(holder,null,1f,R.color.white);
					}
					holder.gameIcon.getGameImg().setImageBitmap(appList.get(position).getAppIcon());
					holder.gameIcon.getGameNameTxt().setText(appList.get(position).getTitle());
					if (appList.get(position).getTitle().toString().contains("太极熊猫")) {
						AppEntity app = GameData.getInstance(mContext).getGameById("38");
						if (app == null) {
							app = GameData.getInstance(mContext).getGameByPkgName("com.snailgames.taiji");
						}
						if (app != null && !TextUtils.isEmpty(app.getLocalIconUrl())) {
							holder.gameIcon.getGameImg().setImageBitmap(PictureUtil.readBitmap(mContext, app.getLocalIconUrl()));
						}
					}

					updateDeleteView(holder);
					holder.gameIcon.getGameUninstallImg().setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							PackageUtils.unInstallApp(mContext, holder.itemInfo.packageName);
						}
					});
				}
			}
		}
	}

	public void updateDeleteView(ViewHolder holder) {
		if (isLongClickable() && (holder.itemInfo instanceof ShortcutInfo && !holder.itemInfo.isSystemApp)) {
			holder.gameIcon.getGameUninstallImg().setVisibility(View.VISIBLE);
//			holder.gameIcon.getGameViewBg().setAlpha(0.5f);
		} else {
			holder.gameIcon.getGameUninstallImg().setVisibility(View.INVISIBLE);
//			holder.gameIcon.getGameViewBg().setAlpha(1f);
//			holder.gameDeleteBg.setVisibility(View.INVISIBLE);
		}
	}

	public void updateCurrentDeleteView() {
		if(mHMultiListView == null){
			return;
		}
		List<HListView> hListViews = mHMultiListView.getHListViews();
		if (hListViews != null && !hListViews.isEmpty()) {
			for (HListView hListView : hListViews) {
				for (int pos = 0; pos < hListView.getChildCount(); pos++) {
					View view = hListView.getChildAt(pos);
					if (view != null) {
						ViewHolder holder = (ViewHolder) view.getTag();
						updateDeleteView(holder);
					}
				}
			}
		}
	}
	
	public void unDisplayGameDeleteView() {
		if (isLongClickable()) {
			setIsLongClickable(false);
			updateCurrentDeleteView();
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

	public boolean isLongClickable() {
		return mIsLongClickable;
	}

	public void setIsLongClickable(boolean isLongClickable) {
		this.mIsLongClickable = isLongClickable;
	}

	@Override
	public BaseAdapter getAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHListNum() {
		return mListViewNum;
	}

	@Override
	public List<?> getData() {
		return appList;
	}
}
