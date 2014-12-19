package com.ireadygo.app.gamelauncher.appstore.download;

import java.io.IOException;
import java.util.List;

import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;


public interface IDownloadData {

//	List<AppEntity> getDldItems();
	
	AppEntity getGameById(String appId);
	
	List<AppEntity> getQueueGames();
	
	List<AppEntity> getDownloadGames();
	
	List<AppEntity> getPausedGames();
	
	List<AppEntity> getCompletedGames();
	
	List<AppEntity> getErrorGames();
	
	void saveGame(AppEntity appEntity);
	
	
	void deleteGame(AppEntity appEntity);
	
	void updateGameStatus(AppEntity appEntity,String status);
	
	void updateGameStatus(AppEntity appEntity);
	
	void updateGameDldSize(AppEntity appEntity,long size);

	void resetGameDldStatus();
	
	List<AppEntity> getInstallAbleGames();
	
	List<AppEntity> getAllGames();
	
	void close() throws IOException;

}
