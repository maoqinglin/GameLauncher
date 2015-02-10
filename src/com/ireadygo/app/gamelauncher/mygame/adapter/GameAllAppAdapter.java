package com.ireadygo.app.gamelauncher.mygame.adapter;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.manager.AppRestrictionManager;
import com.ireadygo.app.gamelauncher.mygame.adapter.MyAppAdapter.ViewHolder;
import com.ireadygo.app.gamelauncher.mygame.info.ExtendInfo;
import com.ireadygo.app.gamelauncher.mygame.info.ExtendInfo.Function;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.GameIconView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;

public class GameAllAppAdapter extends BaseAnimatorAdapter {

	private Context mContext;
	private GameData mGameData;
	private AppRestrictionManager mAppRestrictionManager;
	private List<EscrowApp> mNeckApps = new ArrayList<EscrowApp>();

	public GameAllAppAdapter(Context context, HListView hListView, GameData gameData) {
		super(hListView);
		mContext = context;
		mGameData = gameData;
		mAppRestrictionManager = AppRestrictionManager.getInstance(mContext);
		updateData();
	}

	private void updateData() {
		mNeckApps.clear();
		List<AppEntity> appEntities = mGameData.getGamesOccupySlot();
		for (AppEntity appEntity : appEntities) {
			EscrowApp escrowApp = new EscrowApp();
			escrowApp.isDisable = AppRestrictionManager.isAppDisable(mContext, appEntity.getPkgName());
			escrowApp.name = appEntity.getName();
			escrowApp.pkg = appEntity.getPkgName();
			escrowApp.appId = appEntity.getAppId();
			escrowApp.iconUrl = appEntity.getLocalIconUrl();
			escrowApp.isNearExpired = mAppRestrictionManager.isExpiringApp(appEntity.getPkgName());
			mNeckApps.add(escrowApp);
		}
		addBuySlot();
	}

	/**
	 * 第一个位置为添加卡槽
	 */
	private void addBuySlot() {
		EscrowApp escrowApp = new EscrowApp();
		escrowApp.name = mContext.getResources().getString(R.string.recharge_purpose_buy_slot);
		escrowApp.isDisable = true;
		mNeckApps.add(ExtendInfo.POS_SLOT_BUY, escrowApp);
	}

	@Override
	public void notifyDataSetChanged() {
		updateData();
		super.notifyDataSetChanged();
	}

	public List<EscrowApp> getDatas() {
		return mNeckApps;
	}

	@Override
	public int getCount() {
		return mNeckApps.size();
	}

	@Override
	public Object getItem(int position) {
		return mNeckApps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AllAppHolder holder;
		if (convertView == null) {
			holder = new AllAppHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.game_item, null);
			holder.gameIcon = (GameIconView) convertView;
			convertView.setTag(holder);
		} else {
			holder = (AllAppHolder) convertView.getTag();
		}

		makeItem(holder, position, parent);
		return convertView;
	}

	private void makeItem(AllAppHolder holder, int position, ViewGroup parent) {
		EscrowApp gameApp = mNeckApps.get(position);
		holder.app = gameApp;
		if(position == ExtendInfo.POS_SLOT_BUY){
			setFunctionItemState(holder,Function.SLOT_BUY,0.5f,R.color.app_item_bg_green);
			holder.gameIcon.getGameImg().setImageBitmap(
					BitmapFactory.decodeResource(mContext.getResources(), R.drawable.all_app_purpose_buy_slot_normal));
		}else{
			setFunctionItemState(holder,null,1f,R.color.white);
		}
		holder.gameIcon.getGameNameTxt().setText(gameApp.name);
		if(!TextUtils.isEmpty(gameApp.iconUrl)){
			Bitmap bitmap = PictureUtil.readBitmap(mContext, gameApp.iconUrl);
			if (bitmap == null) {
				return;
			}
			holder.gameIcon.getGameImg().setImageBitmap(bitmap);
			if (gameApp.isDisable) {
				holder.gameIcon.getGameSlotImg().setVisibility(View.INVISIBLE);
			} else {
				holder.gameIcon.getGameSlotImg().setVisibility(View.VISIBLE);
				// if (gameApp.isNearExpired) {
				// holder.icon.setBackgroundResource(R.drawable.bg_imaginary_line_expird);
				// } else {
				// holder.icon.setBackgroundResource(R.drawable.bg_imaginary_line);
				// }
			}
		}
	}

	private void setFunctionItemState(AllAppHolder holder,Function function,float alpha,int textColorResourceId) {
		holder.gameIcon.setFunction(function);
		holder.gameIcon.getGameViewBg().setAlpha(alpha);
		holder.gameIcon.getGameNameTxt().setTextColor(mContext.getResources().getColor(textColorResourceId));
	}

	public static class AllAppHolder {
		public GameIconView gameIcon;
		public EscrowApp app;
	}

	public static class EscrowApp {
		public String appId;
		public String iconUrl;
		public String name;
		public String pkg;
		public boolean isDisable;
		public boolean isNearExpired;
	}

	@Override
	protected Animator selectedAnimator(View view) {
		return ((GameIconView) view).selectedAnimator();
	}

	@Override
	protected Animator unselectedAnimator(View view) {
		return ((GameIconView) view).unselectedAnimator();
	}
}
