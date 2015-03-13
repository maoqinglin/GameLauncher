package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryInfo;
import com.ireadygo.app.gamelauncher.ui.store.category.CategoryItem.CategoryItemHoder;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class CategoryMultiAdapter implements HMultiBaseAdapter {
	/** 创意休闲 **/
	public static final int CATEGORY_ID_SLG = 1;
	/** 动作射击 **/
	public static final int CATEGORY_ID_STG = 2;
	/** 益智棋牌 **/
	public static final int CATEGORY_ID_PZL = 3;
	/** 角色扮演 **/
	public static final int CATEGORY_ID_RPG = 4;
	/** 体育竞技 **/
	public static final int CATEGORY_ID_SPT = 5;
	/** 单机游戏 **/
	public static final int CATEGORY_ID_OLG = 6;
	/** 经营策略 **/
	public static final int CATEGORY_ID_SIM = 7;
	/** 竞技飞行 **/
	public static final int CATEGORY_ID_RSG = 8;

	private static final int LIST_NUM = 2;
	private Context mContext;
	private HMultiListView mMultiListView;
	private List<InternalCategoryInfo> mCategoryDatas = new ArrayList<InternalCategoryInfo>();

	public CategoryMultiAdapter(Context context, HMultiListView multiListView) {
		this.mContext = context;
		this.mMultiListView = multiListView;
		initCategoryDatas();
		multiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CategoryDetailActivity.startSelf(mContext, position);
			}

		});
	}

	private void initCategoryDatas() {
		Resources res = mContext.getResources();
		// 创意休闲SLG
		String intro = res.getString(R.string.category_intro_slg);
		InternalCategoryInfo categoryInfo = new InternalCategoryInfo(CATEGORY_ID_SLG, R.string.category_title_slg,
				R.drawable.category_item_bg_slg, R.drawable.category_item_title_bg_green, intro);
		mCategoryDatas.add(categoryInfo);

		// 动作射击STG
		intro = res.getString(R.string.category_intro_stg);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_STG, R.string.category_title_stg,
				R.drawable.category_item_bg_stg, R.drawable.category_item_title_bg_green, intro);
		mCategoryDatas.add(categoryInfo);

		// 益智棋牌PZL
		intro = res.getString(R.string.category_intro_pzl);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_PZL, R.string.category_title_pzl,
				R.drawable.category_item_bg_pzl, R.drawable.category_item_title_bg_brown, intro);
		mCategoryDatas.add(categoryInfo);

		// 角色扮演RPG
		intro = res.getString(R.string.category_intro_rpg);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_RPG, R.string.category_title_rpg,
				R.drawable.category_item_bg_rpg, R.drawable.category_item_title_bg_blue, intro);
		mCategoryDatas.add(categoryInfo);

		// 体育竞技SPT
		intro = res.getString(R.string.category_intro_spt);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_SPT, R.string.category_title_spt,
				R.drawable.category_item_bg_spt, R.drawable.category_item_title_bg_green, intro);
		mCategoryDatas.add(categoryInfo);

		// 单机精选OLG
		intro = res.getString(R.string.category_intro_olg);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_OLG, R.string.category_title_olg,
				R.drawable.category_item_bg_olg, R.drawable.category_item_title_bg_blue, intro);
		mCategoryDatas.add(categoryInfo);

		// 经营策略SIM
		intro = res.getString(R.string.category_intro_sim);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_SIM, R.string.category_title_sim,
				R.drawable.category_item_bg_sim, R.drawable.category_item_title_bg_brown, intro);
		mCategoryDatas.add(categoryInfo);

		// 竞技飞行RSG
		intro = res.getString(R.string.category_intro_rsg);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_RSG, R.string.category_title_rsg,
				R.drawable.category_item_bg_rsg, R.drawable.category_item_title_bg_brown, intro);
		mCategoryDatas.add(categoryInfo);

	}

	@Override
	public Object getItem(int position) {
		return mCategoryDatas.get(position);
	}

	public int getAllItemCount() {
		int sum = 0;
		for (InternalCategoryInfo info : mCategoryDatas) {
			sum = sum + info.count;
		}
		return sum;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new CategoryItem(mContext);
		}
		convertView.setVisibility(View.VISIBLE);
		CategoryItemHoder holder = ((CategoryItem) convertView).getHolder();
		InternalCategoryInfo info = mCategoryDatas.get(position);
		holder.icon.setImageResource(info.iconId);
		holder.titleLayout.setBackgroundResource(info.titleBgId);
		holder.title.setText(info.titleId);

		int introSize = 0;
		String[] intros = info.intros;
		if (intros != null) {
			introSize = intros.length;
		}
		introSize = Math.min(introSize, holder.introLayout.getChildCount());
		for (int i = 0; i < introSize; i++) {
			String intro = intros[i];
			TextView introView = (TextView) holder.introLayout.getChildAt(i);
			introView.setText(intro);
		}

		int count = info.count;
		if (count < 0) {
			holder.countLayout.setVisibility(View.INVISIBLE);
		} else {
			holder.countLayout.setVisibility(View.VISIBLE);
			holder.count.setText("" + count);
		}

		return convertView;
	}

	@Override
	public BaseAdapter getAdapter() {
		return mMultiListView.getAdapter();
	}

	@Override
	public int getHListNum() {
		return LIST_NUM;
	}

	@Override
	public List<?> getData() {
		return mCategoryDatas;
	}

	static class InternalCategoryInfo {
		int categoryId;
		int titleId;
		int iconId;
		int titleBgId;
		String[] intros;
		int count;

		public InternalCategoryInfo() {

		}

		public InternalCategoryInfo(int categoryId, int titleId, int iconId, int bgId, String intro) {
			this.categoryId = categoryId;
			this.titleId = titleId;
			this.iconId = iconId;
			this.titleBgId = bgId;
			if (!TextUtils.isEmpty(intro)) {
				this.intros = intro.split("/");
			}
		}

		public void setCount(int count) {
			this.count = count;
		}
	}

	@Override
	public View getEmptyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new CategoryItem(mContext);
		}
		convertView.setVisibility(View.GONE);
		return convertView;
	}

}
