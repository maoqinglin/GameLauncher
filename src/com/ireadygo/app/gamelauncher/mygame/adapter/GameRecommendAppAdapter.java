package com.ireadygo.app.gamelauncher.mygame.adapter;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.manager.AppRestrictionManager;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.GameIconView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;

public class GameRecommendAppAdapter extends BaseAnimatorAdapter {

	private Context mContext;
	private GameData mGameData;
	private AppRestrictionManager mAppRestrictionManager;
	private List<EscrowApp> mNeckApps = new ArrayList<EscrowApp>();

	public GameRecommendAppAdapter(Context context, HListView hListView, GameData gameData) {
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
		RecommendAppHolder holder;
		if (convertView == null) {
			holder = new RecommendAppHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.game_item, null);
			holder.gameIcon = (GameIconView) convertView;
			convertView.setTag(holder);
		} else {
			holder = (RecommendAppHolder) convertView.getTag();
		}

		makeItem(holder, position, parent);
		return convertView;
	}

	private void makeItem(RecommendAppHolder holder, int position, ViewGroup parent) {
		EscrowApp gameApp = mNeckApps.get(position);
		holder.app = gameApp;

		Bitmap bitmap = PictureUtil.readBitmap(mContext, gameApp.iconUrl);
		if (bitmap == null) {
			return;
		}
		holder.gameIcon.getGameImg().setImageBitmap(bitmap);
		holder.gameIcon.getGameNameTxt().setText(gameApp.name);
	}

	public static class RecommendAppHolder {
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
