package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.item.ImageItem;
import com.ireadygo.app.gamelauncher.ui.item.ImageItem.ImageItemHolder;
import com.ireadygo.app.gamelauncher.ui.store.category.CategoryItem.CategoryItemHoder;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;

public class CategoryMultiAdapter implements HMultiBaseAdapter {
	/** 角色扮演 **/
	private static final String CATEGORY_ID_RPG = "1";
	/** 创意休闲 **/
	private static final String CATEGORY_ID_SLG = "6";
	/** 单机游戏 **/
	private static final String CATEGORY_ID_OLG = "2";
	/** 动作射击 **/
	private static final String CATEGORY_ID_STG = "4";
	/** 经营策略 **/
	private static final String CATEGORY_ID_SIM = "1";
	/** 益智棋牌 **/
	private static final String CATEGORY_ID_PZL = "1";
	/** 竞技飞行 **/
	private static final String CATEGORY_ID_RSG = "1";
	/** 体育竞技 **/
	private static final String CATEGORY_ID_SPT = "1";

	private static final int LIST_NUM = 2;
	private Context mContext;
	private HMultiListView mMultiListView;
	private List<InternalCategoryInfo> mCategoryDatas = new ArrayList<InternalCategoryInfo>();

	public CategoryMultiAdapter(Context mContext, HMultiListView mMultiListView) {
		this.mContext = mContext;
		this.mMultiListView = mMultiListView;
		initCategoryDatas();
	}

	private void initCategoryDatas() {
		// 角色扮演RPG
		Resources res = mContext.getResources();
		String intro = res.getString(R.string.category_intro_rpg);
		InternalCategoryInfo categoryInfo = new InternalCategoryInfo(CATEGORY_ID_RPG, R.string.category_title_rpg,
				R.drawable.category_item_bg_rpg, R.drawable.category_item_title_bg_blue, intro);
		mCategoryDatas.add(categoryInfo);

		// 创意休闲SLG
		intro = res.getString(R.string.category_intro_slg);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_SLG, R.string.category_title_slg,
				R.drawable.category_item_bg_slg, R.drawable.category_item_title_bg_green, intro);
		mCategoryDatas.add(categoryInfo);

		// 益智棋牌PZL
		intro = res.getString(R.string.category_intro_pzl);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_PZL, R.string.category_title_pzl,
				R.drawable.category_item_bg_pzl, R.drawable.category_item_title_bg_brown, intro);
		mCategoryDatas.add(categoryInfo);

		// 单机精选OLG
		intro = res.getString(R.string.category_intro_olg);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_OLG, R.string.category_title_olg,
				R.drawable.category_item_bg_olg, R.drawable.category_item_title_bg_blue, intro);
		mCategoryDatas.add(categoryInfo);

		// 动作射击STG
		intro = res.getString(R.string.category_intro_stg);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_STG, R.string.category_title_stg,
				R.drawable.category_item_bg_stg, R.drawable.category_item_title_bg_green, intro);
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

		// 体育竞技SPT
		intro = res.getString(R.string.category_intro_spt);
		categoryInfo = new InternalCategoryInfo(CATEGORY_ID_SPT, R.string.category_title_spt,
				R.drawable.category_item_bg_spt, R.drawable.category_item_title_bg_green, intro);
		mCategoryDatas.add(categoryInfo);
	}

	@Override
	public Object getItem(int position) {
		return mCategoryDatas.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new CategoryItem(mContext);
		}
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
			holder.count.setText(249 + "");
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
		String categoryId;
		int titleId;
		int iconId;
		int titleBgId;
		String[] intros;
		int count;

		public InternalCategoryInfo(String categoryId, int titleId, int iconId, int bgId, String intro) {
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
}
