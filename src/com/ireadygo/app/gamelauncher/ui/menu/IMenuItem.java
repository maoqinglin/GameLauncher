package com.ireadygo.app.gamelauncher.ui.menu;

import android.animation.Animator.AnimatorListener;

import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.item.ISelectableItem;

public interface IMenuItem extends ISelectableItem{
	
	void setContentFragment(BaseContentFragment contentFragment);
	
	BaseContentFragment getContentFragment();
	
	void setIndex(int index);
	
	int getIndex();
	
	void toFocused(AnimatorListener listener);
	
	void toUnfocused(AnimatorListener listener);
	
	void toInit(AnimatorListener listener);
	
	static enum State {
		INIT, // 初始状态
		FOCUSED, // 菜单处于焦点状态，子项也处于焦点状态
		NOFOCUSED, // 菜单处于焦点状态，子项处于非焦点状态
		SELECTED, // 菜单处于选中状态，子项处于选中状态
		NOSELECTED// 菜单处于选中状态，子项处于非选中状态
	}
}
