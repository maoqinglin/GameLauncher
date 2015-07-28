package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alipay.android.app.c;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryInfo;
import com.ireadygo.app.gamelauncher.ui.store.category.CategoryItem.CategoryItemHoder;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.nostra13.universalimageloader.core.ImageLoader;

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
	public static final int CATEGORY_ID_OLG = 35;
	/** 经营策略 **/
	public static final int CATEGORY_ID_SIM = 7;
	/** 竞技飞行 **/
	public static final int CATEGORY_ID_RSG = 8;

	private static final int LIST_NUM = 2;
	private Context mContext;
	private HMultiListView mMultiListView;
	private List<CategoryInfo> mCategoryDatas = new ArrayList<CategoryInfo>();
	private ImageLoader mImageLoader;

	public CategoryMultiAdapter(Context context, HMultiListView multiListView, List<CategoryInfo> categoryDatas) {
		this.mContext = context;
		this.mMultiListView = multiListView;
		mCategoryDatas = categoryDatas;
		mImageLoader = GameInfoHub.instance(mContext).getImageLoader();
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
		convertView.setVisibility(View.VISIBLE);
		CategoryItemHoder holder = ((CategoryItem) convertView).getHolder();
		CategoryInfo info = mCategoryDatas.get(position);
		if(!TextUtils.isEmpty(info.getPosterIconUrl())){
			mImageLoader.displayImage(info.getPosterIconUrl(), holder.icon);
		}
		holder.title.setText(info.getCatetoryName().trim());

		int introSize = 0;
		String[] intros = info.getCategoryDes().split("/");
		if (intros != null) {
			introSize = intros.length;
		}
		introSize = Math.min(introSize, holder.introLayout.getChildCount());
		for (int i = 0; i < introSize; i++) {
			String intro = intros[i];
			TextView introView = (TextView) holder.introLayout.getChildAt(i);
			introView.setText(intro);
		}

		int count = info.getAppCounts();
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

	@Override
	public View getEmptyView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new CategoryItem(mContext);
		}
		convertView.setVisibility(View.GONE);
		return convertView;
	}

}
