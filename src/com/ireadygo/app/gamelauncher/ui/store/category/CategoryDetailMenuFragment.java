package com.ireadygo.app.gamelauncher.ui.store.category;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryInfo;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.focus.FocusRelativeLayout;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.menu.TextMenu;

public class CategoryDetailMenuFragment extends BaseMenuFragment {

	private List<CategoryInfo> mCategoryList;
	public CategoryDetailMenuFragment(Activity activity, List<CategoryInfo> categoryList) {
		super(activity);
		mCategoryList = categoryList;
		initCoordinateParams(Config.Menu.INIT_X, Config.Menu.INIT_Y);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.category_detail_menu_fragment, container, false);
		FocusRelativeLayout parent = (FocusRelativeLayout)view.findViewById(R.id.store_category_focus_container);
		parent.setBorderViewBg(R.drawable.menu_nav_focused_bg);
		parent.setViewGroup(container); // 控制焦点顺序.
		parent.setChildFocusChangeListener(mItemFocusChangeListener);
		if(mCategoryList != null && !mCategoryList.isEmpty()){
			for (int i = 0; i < mCategoryList.size(); i++) {
				TextMenu categoryMenu = new TextMenu(getRootActivity());
				categoryMenu.setNextFocusRightId(R.id.upHList);
				categoryMenu.setId(mCategoryList.get(i).getCategoryId()+1000);
				categoryMenu.getTextView().setText(mCategoryList.get(i).getCatetoryName());
				FocusRelativeLayout.LayoutParams lp = new FocusRelativeLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
				if (i == 0) {
					lp.setMargins(0, 0, 0, 0);
				} else {
					lp.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.menu_item_margin_top), 0, 0);
					lp.addRule(RelativeLayout.BELOW, mCategoryList.get(i).getCategoryId()+999);
				}
				parent.addView(categoryMenu, i, lp);
				addMenuItem(categoryMenu, new CategoryDetailContentFragment(getRootActivity(), this,
						mCategoryList.get(i)));
			}
		}
		return view;
	}
}
