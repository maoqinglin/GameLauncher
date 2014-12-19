package com.ireadygo.app.gamelauncher.appstore.install;

import java.util.List;

import android.content.Context;

import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;

public interface IInstallData {

	//根据包名获取数据库游戏数据
	AppEntity getGameByPkgName(String pkgName);

	AppEntity getExistApp(String pkgName,String appName);

	List<AppEntity> getAllInstalledApp(boolean includeIgnore);

	List<AppEntity> getLauncherAbleGames();

	List<AppEntity> getUpdateAbleGames(int updateFlag);

	//获取免商店中的游戏列表
	List<AppEntity> getFreeStoreGames();

	//获取不在免商店中的游戏列表
	List<AppEntity> getNotFreeStoreGames();

	void addGame(Context context,String pkgName);
	
	void removeGame(String pkgName);
}
