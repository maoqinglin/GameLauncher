package com.ireadygo.app.gamelauncher.mygame.info;

public class ExtendInfo extends ItemInfo {

	public boolean isFixed;
	public Function function = Function.SLOT_BUY;

	public static final int POS_GAME_ALL = 0;
	public static final int POS_GAME_RECOMMEND = 0;
	public static final int POS_SLOT_BUY = 0;

	public enum Function {
		SLOT_BUY, SLOT_USE,GAME_ALL,GAME_RECOMMEND_DOWNLOAD
	}

	@Override
	public String toString() {
		return "ExtendInfo [isFixed=" + isFixed + ", function=" + function + "]";
	}

}
