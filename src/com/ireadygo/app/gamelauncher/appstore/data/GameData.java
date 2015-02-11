package com.ireadygo.app.gamelauncher.appstore.data;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.db.GameAppStatusColumns;
import com.ireadygo.app.gamelauncher.appstore.data.db.GameStatusDBHelper;
import com.ireadygo.app.gamelauncher.appstore.download.IDownloadData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity.PkgType;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.install.IInstallData;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherSettings;
import com.ireadygo.app.gamelauncher.game.utils.Utilities;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

public class GameData implements IDownloadData, IInstallData, Closeable {
	// TODO 完成apk信息的数据库存储

	private static final String TAG = "GameData";

	private static GameData mGameData;

	private final GameStatusDBHelper mGameStatusDBHelper;

	private final SQLiteDatabase mDB;

	private ArrayList<LocalDataLoadCallback> mLocalDataLoadCallbacks = new ArrayList<GameData.LocalDataLoadCallback>();

	private ExecutorService mThreadPool = GameLauncherThreadPool.getFixedThreadPool();

	private Context mContext;

	private Handler mHandler;

	private GameData(final Context context) {
		mContext = context;
		mGameStatusDBHelper = new GameStatusDBHelper(mContext);
		mDB = mGameStatusDBHelper.getWritableDatabase();
		mHandler = new Handler(mContext.getMainLooper());
	}

	public void initGameData(final Context context) {
		final LocalGameData localGameData = new LocalGameData();
		// 每次启动都要初始化，避免游戏仓被forcestop后接收不到包安装和卸载的广播，无法更新数据库
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				localGameData.initLocalGameData(mGameStatusDBHelper, context);
			}
		});
	}

	public static GameData getInstance(Context context) {
		if (mGameData == null) {
			synchronized (GameData.class) {
				if (mGameData == null) {
					mGameData = new GameData(context.getApplicationContext());
				}
			}
		}
		return mGameData;
	}

	public GameStatusDBHelper getGameStatusDBHelper() {
		return mGameStatusDBHelper;
	}

	@Override
	public synchronized void saveGame(AppEntity appEntity) {
		ContentValues values = transferDldItemToContentValues(appEntity);
		try {
			mDB.beginTransaction();
			AppEntity entityFromDb = getGameById(appEntity.getAppId());
			if (entityFromDb != null) {
				updateGameStatus(appEntity);
			} else {
				long rowId = mDB.insert(GameAppStatusColumns.TABLE_NAME, null, values);
				Log.d(TAG, "saveDldItem---rowId = " + rowId);
			}
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDB.endTransaction();
		}
	}

	@Override
	public synchronized void updateGameStatus(AppEntity appEntity) {
		ContentValues values = transferDldItemToContentValues(appEntity);
		try {
			mDB.beginTransaction();
			String appId = appEntity.getAppId();
			if (!TextUtils.isEmpty(appId)) {
				mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_APP_ID + " =? ",
						new String[] { appEntity.getAppId() });
			} else {
				String pkgName = appEntity.getPkgName();
				if (!TextUtils.isEmpty(pkgName)) {
					mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME
							+ " =? ", new String[] { appEntity.getPkgName() });
				}
			}
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mDB.endTransaction();
		}
	}

	public synchronized void updateUpgradeAppData(AppEntity appEntity) {
		ContentValues values = transferUpgradeItemToContentValues(appEntity);
		try {
			mDB.beginTransaction();
			String pkgName = appEntity.getPkgName();
			if (!TextUtils.isEmpty(pkgName)) {
				mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ",
						new String[] { appEntity.getPkgName() });
			}
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDB.endTransaction();
		}
	}


	public synchronized void updateMappedAppData(AppEntity appEntity) {
		ContentValues values = transferMappedItemToContentValues(appEntity);
		String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
				+ GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ";
		Cursor cursor = mDB.rawQuery(sql, new String[] { appEntity.getPkgName() });
		try {
			mDB.beginTransaction();
			String pkgName = appEntity.getPkgName();
			if (!TextUtils.isEmpty(pkgName)) {
				if (cursor.getCount() == 0) {
					mDB.insert(GameAppStatusColumns.TABLE_NAME, null, values);
				} else {
					mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ",
							new String[] { appEntity.getPkgName() });
				}
			}
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDB.endTransaction();
			closeCursor(cursor);
		}
	}


	@Override
	public synchronized void updateGameStatus(AppEntity appEntity, String status) {
		try {
			mDB.beginTransaction();
			String appId = appEntity.getAppId();
			int rowId = 0;
			if (!TextUtils.isEmpty(appId)) {
				ContentValues values = new ContentValues();
				values.put(GameAppStatusColumns.COLUMN_APP_GAME_STATUS, status);
				rowId = mDB.update(GameAppStatusColumns.TABLE_NAME, values,
						GameAppStatusColumns.COLUMN_APP_ID + " =? ", new String[] { appEntity.getAppId() });
			} else {
				String pkgName = appEntity.getPkgName();
				if (!TextUtils.isEmpty(pkgName)) {
					ContentValues values = new ContentValues();
					values.put(GameAppStatusColumns.COLUMN_APP_GAME_STATUS, status);
					rowId = mDB.update(GameAppStatusColumns.TABLE_NAME, values,
							GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ", new String[] { appEntity.getPkgName() });
				}
			}
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDB.endTransaction();
		}
	}

	public synchronized void updateGameStatus(String pkgName, String status) {
		if (TextUtils.isEmpty(pkgName)) {
			Log.e(TAG, "updateGameStatus with empty pkgName!");
			return;
		}
		try {
			mDB.beginTransaction();
			if (!TextUtils.isEmpty(pkgName)) {
				ContentValues values = new ContentValues();
				values.put(GameAppStatusColumns.COLUMN_APP_GAME_STATUS, status);
				mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ",
						new String[] { pkgName });
			}
			mDB.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDB.endTransaction();
		}
	}

	@Override
	public void updateGameDldSize(AppEntity appEntity, long size) {
		try {
			mDB.beginTransaction();
			ContentValues values = new ContentValues();
			values.put(GameAppStatusColumns.COLUMN_DOWNLOAD_SIZE, size);
			int rowId = mDB.update(GameAppStatusColumns.TABLE_NAME, values,
					GameAppStatusColumns.COLUMN_APP_ID + " =? ", new String[] { appEntity.getAppId() });
			mDB.setTransactionSuccessful();
			Log.d(TAG, "updateDownloadSize--rowId = " + rowId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDB.endTransaction();
		}
	}

	@Override
	public synchronized void deleteGame(AppEntity appEntity) {
		String appId = appEntity.getAppId();
		try {
			mDB.beginTransaction();
			if (!TextUtils.isEmpty(appId)) {
				int row = mDB.delete(GameAppStatusColumns.TABLE_NAME, GameAppStatusColumns.COLUMN_APP_ID + " = ? ",
						new String[] { appId });
				Log.d(TAG, "deleteDldItem---row = " + row);
			} else {
				String pkgName = appEntity.getPkgName();
				if (!TextUtils.isEmpty(pkgName)) {
					mDB.delete(GameAppStatusColumns.TABLE_NAME, GameAppStatusColumns.COLUMN_PACKAGE_NAME + " = ? ",
							new String[] { pkgName });
				}
			}
			mDB.setTransactionSuccessful();
			deleteLocalIcon(appEntity.getRemoteIconUrl());
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			mDB.endTransaction();
		}
	}

	@SuppressLint("NewApi")
	private void deleteLocalIcon(String iconUrl) {
		if (!TextUtils.isEmpty(iconUrl)) {
			File file = new File(iconUrl);
			if (file.exists() && file.canExecute()) {
				file.delete();
			}
		}
	}

	@Override
	public AppEntity getGameById(String appId) {
		AppEntity item = null;
		Cursor cursor = null;
		try {
			String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
					+ GameAppStatusColumns.COLUMN_APP_ID + " =? ";
			cursor = mDB.rawQuery(sql, new String[] { appId });
			if (cursor != null && cursor.moveToFirst()) {
				item = transferCursorToDldItem(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		return item;
	}

	@Override
	public List<AppEntity> getQueueGames() {
		return getGameListByStatus(GameState.QUEUING);
	}

	@Override
	public List<AppEntity> getDownloadGames() {
		List<AppEntity> items = new ArrayList<AppEntity>();
		String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
				+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? or " + GameAppStatusColumns.COLUMN_APP_GAME_STATUS
				+ " =? or " + GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? or "
				+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? or " + GameAppStatusColumns.COLUMN_APP_GAME_STATUS
				+ " =? " + " order by " + GameAppStatusColumns.COLUMN_CREATE_TIME + " desc ";
		Cursor cursor = mDB.rawQuery(sql, new String[] { GameState.QUEUING.toString(),
				GameState.TRANSFERING.toString(), GameState.PAUSED.toString(), GameState.ERROR.toString(),
				GameState.INSTALLABLE.toString() });
		if (cursor != null && cursor.moveToFirst()) {
			items = new ArrayList<AppEntity>(cursor.getCount());
			do {
				items.add(transferCursorToDldItem(cursor));
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return items;
	}

	@Override
	public List<AppEntity> getPausedGames() {
		return getGameListByStatus(GameState.PAUSED);
	}

	@Override
	public List<AppEntity> getCompletedGames() {
		return getGameListByStatus(GameState.INSTALLABLE);
	}

	@Override
	public List<AppEntity> getErrorGames() {
		return getGameListByStatus(GameState.ERROR);
	}

	private List<AppEntity> getGameListByStatus(GameState gameStatus) {
		List<AppEntity> items = new ArrayList<AppEntity>();
		if (null != gameStatus) {
			String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
					+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? ";
			Cursor cursor = mDB.rawQuery(sql, new String[] { gameStatus.toString() });
			if (cursor != null && cursor.moveToFirst()) {
				items = new ArrayList<AppEntity>(cursor.getCount());
				do {
					items.add(transferCursorToDldItem(cursor));
				} while (cursor.moveToNext());
			}
			closeCursor(cursor);
		}
		return items;
	}

	@Override
	public void close() throws IOException {
		if (null != mGameData) {
			synchronized (mGameData) {
				if (null != mGameData) {
					mGameStatusDBHelper.close();
				}
			}
		}
	}

	private void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

	private AppEntity transferCursorToDldItem(Cursor cursor) {
		AppEntity item = new AppEntity();
		if (cursor == null) {
			return item;
		}
		item.setAppId(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_APP_ID)));
		item.setName(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_APP_NAME)));
		item.setPkgName(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_PACKAGE_NAME)));
		item.setFileName(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_FILE_NAME)));
		item.setDownloadPath(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_DOWNLOAD_PATH)));
		item.setFreeflowDldPath(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_FREE_FLOW_DOWNLOAD_PATH)));
		item.setSavedPath(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_SAVED_PATH)));
		item.setGameState(GameState.valueOf(cursor.getString(cursor
				.getColumnIndex(GameAppStatusColumns.COLUMN_APP_GAME_STATUS))));
		item.setTotalSize(cursor.getLong(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_TOTAL_SIZE)));
		item.setDownloadSize(cursor.getLong(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_DOWNLOAD_SIZE)));
		item.setVersionCode(cursor.getInt(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_CUR_VERSION_CODE)));
		item.setVersionName(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_CUR_VERSION_NAME)));
		item.setNewVersionCode(cursor.getInt(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_NEW_VERSION_CODE)));
		item.setNewVersionName(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_NEW_VERSION_NAME)));
		item.setCreateTime(cursor.getLong(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_CREATE_TIME)));
		item.setSign(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_APP_SIGN)));
		item.setRemoteIconUrl(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_REMOTE_ICON_URL)));
		item.setLocalIconUrl(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_LOCAL_ICON_URL)));
		item.setIsUpdateable(cursor.getInt(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_IS_UPDATEABLE)));
		item.setIsInFreeStore(cursor.getInt(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_IN_FREESTORE)));
		item.setFreeFlag(cursor.getInt(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_FREEFLAG)));
		item.setDownloadCounts(cursor.getLong(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_DOWNLOAD_COUNT)));
		item.setScreenshotUrl(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_SCREENSHOT_URL)));
		item.setDescription(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_DESCRIPTION)));
		item.setIsComeFrmFreeStore(cursor.getInt(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_COME_FRM_FREESTORE)));
		item.setIsOccupySlot(cursor.getInt(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_OCCUPY_SLOT)));
		item.setScreenshotDirection(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_SCREENSHOT_DIRECTION)));
		item.setPosterIconUrl(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_POSTER_ICON_URL)));
		item.setPosterBgUrl(cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_POSTER_BG_URL)));
		return item;
	}

	private ContentValues transferDldItemToContentValues(AppEntity appEntity) {
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_APP_ID, appEntity.getAppId());
		values.put(GameAppStatusColumns.COLUMN_APP_NAME, appEntity.getName());
		values.put(GameAppStatusColumns.COLUMN_PACKAGE_NAME, appEntity.getPkgName());
		values.put(GameAppStatusColumns.COLUMN_FILE_NAME, appEntity.getFileName());
		values.put(GameAppStatusColumns.COLUMN_DOWNLOAD_PATH, appEntity.getDownloadPath());
		values.put(GameAppStatusColumns.COLUMN_FREE_FLOW_DOWNLOAD_PATH, appEntity.getFreeflowDldPath());
		values.put(GameAppStatusColumns.COLUMN_SAVED_PATH, appEntity.getSavedPath());
		values.put(GameAppStatusColumns.COLUMN_APP_GAME_STATUS, appEntity.getGameState().toString());
		values.put(GameAppStatusColumns.COLUMN_TOTAL_SIZE, appEntity.getTotalSize());
		values.put(GameAppStatusColumns.COLUMN_DOWNLOAD_SIZE, appEntity.getDownloadSize());
		values.put(GameAppStatusColumns.COLUMN_CUR_VERSION_CODE, appEntity.getVersionCode());
		values.put(GameAppStatusColumns.COLUMN_CUR_VERSION_NAME, appEntity.getVersionName());
		values.put(GameAppStatusColumns.COLUMN_NEW_VERSION_CODE, appEntity.getNewVersionCode());
		values.put(GameAppStatusColumns.COLUMN_NEW_VERSION_NAME, appEntity.getNewVersionName());
		values.put(GameAppStatusColumns.COLUMN_CREATE_TIME, appEntity.getCreateTime());
		values.put(GameAppStatusColumns.COLUMN_APP_SIGN, appEntity.getSign());
		values.put(GameAppStatusColumns.COLUMN_REMOTE_ICON_URL, appEntity.getRemoteIconUrl());
		values.put(GameAppStatusColumns.COLUMN_LOCAL_ICON_URL, appEntity.getLocalIconUrl());
		values.put(GameAppStatusColumns.COLUMN_IS_UPDATEABLE, appEntity.getIsUpdateable());
		values.put(GameAppStatusColumns.COLUMN_IN_FREESTORE, appEntity.getIsInFreeStore());
		values.put(GameAppStatusColumns.COLUMN_DESCRIPTION, appEntity.getDescription());
		values.put(GameAppStatusColumns.COLUMN_DOWNLOAD_COUNT, appEntity.getDownloadCounts());
		values.put(GameAppStatusColumns.COLUMN_SCREENSHOT_URL, appEntity.getScreenshotUrl());
		values.put(GameAppStatusColumns.COLUMN_FREEFLAG, appEntity.getFreeFlag());
		values.put(GameAppStatusColumns.COLUMN_COME_FRM_FREESTORE, appEntity.getIsComeFrmFreeStore());
		values.put(GameAppStatusColumns.COLUMN_OCCUPY_SLOT, appEntity.getIsOccupySlot());
		values.put(GameAppStatusColumns.COLUMN_SCREENSHOT_DIRECTION, appEntity.getScreenshotDirection());
		values.put(GameAppStatusColumns.COLUMN_POSTER_ICON_URL, appEntity.getPosterIconUrl());
		values.put(GameAppStatusColumns.COLUMN_POSTER_BG_URL, appEntity.getPosterBgUrl());
		return values;
	}

	private ContentValues transferUpgradeItemToContentValues(AppEntity appEntity) {
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_APP_ID, appEntity.getAppId());
		values.put(GameAppStatusColumns.COLUMN_APP_NAME, appEntity.getName());
		values.put(GameAppStatusColumns.COLUMN_PACKAGE_NAME, appEntity.getPkgName());
		values.put(GameAppStatusColumns.COLUMN_DOWNLOAD_PATH, appEntity.getDownloadPath());
		values.put(GameAppStatusColumns.COLUMN_APP_GAME_STATUS, appEntity.getGameState().toString());
		values.put(GameAppStatusColumns.COLUMN_TOTAL_SIZE, appEntity.getTotalSize());
		values.put(GameAppStatusColumns.COLUMN_NEW_VERSION_CODE, appEntity.getNewVersionCode());
		values.put(GameAppStatusColumns.COLUMN_NEW_VERSION_NAME, appEntity.getNewVersionName());
		values.put(GameAppStatusColumns.COLUMN_APP_SIGN, appEntity.getSign());
		values.put(GameAppStatusColumns.COLUMN_REMOTE_ICON_URL, appEntity.getRemoteIconUrl());
		values.put(GameAppStatusColumns.COLUMN_IS_UPDATEABLE, appEntity.getIsUpdateable());
		values.put(GameAppStatusColumns.COLUMN_IN_FREESTORE, appEntity.getIsInFreeStore());
		return values;
	}

	private ContentValues transferMappedItemToContentValues(AppEntity appEntity) {
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_APP_ID, appEntity.getAppId());
		values.put(GameAppStatusColumns.COLUMN_APP_NAME, appEntity.getName());
		values.put(GameAppStatusColumns.COLUMN_PACKAGE_NAME, appEntity.getPkgName());
		values.put(GameAppStatusColumns.COLUMN_DOWNLOAD_PATH, appEntity.getDownloadPath());
		values.put(GameAppStatusColumns.COLUMN_TOTAL_SIZE, appEntity.getTotalSize());
		values.put(GameAppStatusColumns.COLUMN_APP_SIGN, appEntity.getSign());
		values.put(GameAppStatusColumns.COLUMN_REMOTE_ICON_URL, appEntity.getRemoteIconUrl());
		values.put(GameAppStatusColumns.COLUMN_DESCRIPTION, appEntity.getDescription());
		values.put(GameAppStatusColumns.COLUMN_IN_FREESTORE, appEntity.getIsInFreeStore());
		values.put(GameAppStatusColumns.COLUMN_FREEFLAG, appEntity.getFreeFlag());
		values.put(GameAppStatusColumns.COLUMN_DOWNLOAD_COUNT, appEntity.getDownloadCounts());
		values.put(GameAppStatusColumns.COLUMN_SCREENSHOT_URL, appEntity.getScreenshotUrl());
		values.put(GameAppStatusColumns.COLUMN_COME_FRM_FREESTORE, appEntity.getIsComeFrmFreeStore());
		values.put(GameAppStatusColumns.COLUMN_APP_GAME_STATUS, appEntity.getGameState().toString());
		values.put(GameAppStatusColumns.COLUMN_POSTER_ICON_URL, appEntity.getPosterIconUrl());
		values.put(GameAppStatusColumns.COLUMN_POSTER_BG_URL, appEntity.getPosterBgUrl());
		if (TextUtils.isEmpty(appEntity.getLocalIconUrl())) {
			values.put(GameAppStatusColumns.COLUMN_LOCAL_ICON_URL, 
					PackageUtils.getIconUrl(PackageUtils.getPkgInfo(mContext, appEntity.getPkgName()), mContext));
		}
		return values;
	}

	@Override
	public synchronized void resetGameDldStatus() { // TODO 状态改变，逻辑需要修改
		Cursor cursor = null;
		try {
			String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
					+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " !=? and "
					+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " !=? and "
					+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " !=? ";
			Log.i(TAG, "sql = " + sql);
			cursor = mDB.rawQuery(sql, new String[] { GameState.INSTALLABLE.toString(),
					GameState.LAUNCHABLE.toString(), GameState.UPGRADEABLE.toString() });
			if (cursor != null && cursor.moveToFirst()) {
				do {
					String status = cursor
							.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_APP_GAME_STATUS));
					String appId = cursor.getString(cursor.getColumnIndex(GameAppStatusColumns.COLUMN_APP_ID));
					Log.i(TAG, "status = " + status);
					ContentValues values = new ContentValues();
					values.put(GameAppStatusColumns.COLUMN_APP_GAME_STATUS, GameState.PAUSED.toString());
					if (!TextUtils.isEmpty(appId)) {
						mDB.update(GameAppStatusColumns.TABLE_NAME, values,
								GameAppStatusColumns.COLUMN_APP_ID + " =? ", new String[] { appId });
					} else {
						String pkgName = cursor.getString(cursor
								.getColumnIndex(GameAppStatusColumns.COLUMN_PACKAGE_NAME));
						if (!TextUtils.isEmpty(pkgName)) {
							mDB.update(GameAppStatusColumns.TABLE_NAME, values,
									GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ", new String[] { pkgName });
						}
					}
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			closeCursor(cursor);
		}
	}

	@Override
	public List<AppEntity> getInstallAbleGames() {
		return getGameListByStatus(GameState.INSTALLABLE);
	}

	@Override
	public List<AppEntity> getLauncherAbleGames() {
		return getGameListByStatus(GameState.LAUNCHABLE);
	}

	@Override
	public List<AppEntity> getUpdateAbleGames(int updateFlag) {
		List<AppEntity> items = new ArrayList<AppEntity>();
		String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
				+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? and " + GameAppStatusColumns.COLUMN_IS_UPDATEABLE
				+ " =? ";
		Cursor cursor = mDB
				.rawQuery(sql, new String[] { GameState.UPGRADEABLE.toString(), String.valueOf(updateFlag) });
		if (cursor != null && cursor.moveToFirst()) {
			items = new ArrayList<AppEntity>(cursor.getCount());
			do {
				items.add(transferCursorToDldItem(cursor));
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return items;
	}

	@Override
	public List<AppEntity> getAllGames() {
		List<AppEntity> appEntitys = new ArrayList<AppEntity>();
		String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " order by "
				+ GameAppStatusColumns.COLUMN_CREATE_TIME + " desc ";
		Cursor cursor = mDB.rawQuery(sql, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				appEntitys = new ArrayList<AppEntity>();
				do {
					AppEntity item = transferCursorToDldItem(cursor);
					appEntitys.add(item);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		return appEntitys;
	}
	
	/**默认占卡槽**/
	@Override
	public synchronized void addGame(final Context context, final String pkgName) {
		addGame(context, pkgName, true);
	}

	public synchronized void addGame(final Context context,final String pkgName,final boolean isOccupySlot){
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		final PackageInfo info = PackageUtils.getPkgInfo(context, pkgName);
		if (null != info) {
			mThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					addGameByPkgInfo(context, pkgName, info,isOccupySlot);
				}
			});
		}
	}
	
	private void addGameByPkgInfo(Context context, String pkgName, PackageInfo info,final boolean occupySlot) {
		String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
				+ GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ";
		Cursor cursor = mDB.rawQuery(sql, new String[] { pkgName });
		ContentValues values = transferPkgInfoToContentValues(info, context,occupySlot);
		try {
			if (cursor.getCount() == 0) {
				mDB.insert(GameAppStatusColumns.TABLE_NAME, null, values);
			} else {
				mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ",
						new String[] { pkgName });
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
	}

	@Override
	public synchronized void removeGame(String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
				+ GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ";
		Cursor cursor = mDB.rawQuery(sql, new String[] { pkgName });
		try {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				deleteGame(transferCursorToDldItem(cursor));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
	}

	private final class LocalGameData {

		public synchronized void initLocalGameData(GameStatusDBHelper dbHelper, Context context) {
			if (null != dbHelper) {
				List<PackageInfo> apps = PackageUtils.getNonSystemApps(context);
				if (null == apps || 0 == apps.size()) {
					return;
				}
				ArrayList<String> recordAppsPkgNames = getAllRecordAppPkgNames(getAllGames());
				for (PackageInfo info : apps) {
					if (recordAppsPkgNames.contains(info.packageName)) {
						continue;
					}
					addGame(mContext, info.packageName);
				}
				reportLoadDataSuccess();
			}
		}
	}

	private ContentValues transferPkgInfoToContentValues(PackageInfo pkgInfo, Context context,final boolean isOccupySlot) {
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_APP_NAME, PackageUtils.getGameName(pkgInfo, context));
		values.put(GameAppStatusColumns.COLUMN_PACKAGE_NAME, pkgInfo.applicationInfo.packageName);
		values.put(GameAppStatusColumns.COLUMN_PKG_TYPE, PkgType.APK.toString());
		values.put(GameAppStatusColumns.COLUMN_APP_GAME_STATUS, GameState.LAUNCHABLE.toString());
		values.put(GameAppStatusColumns.COLUMN_CUR_VERSION_CODE, pkgInfo.versionCode);
		values.put(GameAppStatusColumns.COLUMN_CUR_VERSION_NAME, pkgInfo.versionName);
		values.put(GameAppStatusColumns.COLUMN_CREATE_TIME, System.currentTimeMillis());
//		values.put(GameAppStatusColumns.COLUMN_APP_SIGN,
//				PackageUtils.getSingInfo(context, pkgInfo.applicationInfo.packageName));
		values.put(GameAppStatusColumns.COLUMN_LOCAL_ICON_URL, PackageUtils.getIconUrl(pkgInfo, context));
		values.put(GameAppStatusColumns.COLUMN_IS_UPDATEABLE, 0);
		File file = new File(pkgInfo.applicationInfo.publicSourceDir);
		if (file.exists()) {
			values.put(GameAppStatusColumns.COLUMN_TOTAL_SIZE, file.length());
		}
		values.put(GameAppStatusColumns.COLUMN_IN_FREESTORE, 0);
		values.put(GameAppStatusColumns.COLUMN_FREEFLAG, 0);
		if(isOccupySlot){
			values.put(GameAppStatusColumns.COLUMN_OCCUPY_SLOT, AppEntity.OCCUPY_SLOT);
		}else{
			values.put(GameAppStatusColumns.COLUMN_OCCUPY_SLOT, AppEntity.NOT_OCCUPY_SLOT);
		}
		values.put(GameAppStatusColumns.COLUMN_COME_FRM_FREESTORE, 0);
		return values;
	}

	public interface LocalDataLoadCallback {
		public void loadSuccess();

		public void loadFail();
	}

	public void addDataLoadCallback(LocalDataLoadCallback callback) {
		if (null == callback) {
			return;
		}
		synchronized (mLocalDataLoadCallbacks) {
			mLocalDataLoadCallbacks.add(callback);
		}
	}

	public void removeDataLoadCallback(LocalDataLoadCallback callback) {
		synchronized (mLocalDataLoadCallbacks) {
			mLocalDataLoadCallbacks.remove(callback);
		}
	}

	private void reportLoadDataSuccess() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				for (LocalDataLoadCallback callback : mLocalDataLoadCallbacks) {
					callback.loadSuccess();
				}
			}
		});
	}

	@Override
	public AppEntity getGameByPkgName(String pkgName) {
		AppEntity item = null;
		Cursor cursor = null;
		try {
			String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
					+ GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ";
			cursor = mDB.rawQuery(sql, new String[] { pkgName });
			if (cursor != null && cursor.moveToFirst()) {
				item = transferCursorToDldItem(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		return item;
	}

	@Override
	public AppEntity getExistApp(String pkgName, String appName) {
		AppEntity item = null;
		Cursor cursor = null;
		try {
			// select * from tableName + where (colName1 =? or colName2 =?) and
			// (state = )
			String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where " + "("
					+ GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? " + " or " + GameAppStatusColumns.COLUMN_APP_NAME
					+ " =?" + ")" + " and " + "(" + GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =?" + " or "
					+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =?" + ")";
			cursor = mDB.rawQuery(sql, new String[] { pkgName, appName, GameState.LAUNCHABLE.toString(),
					GameState.UPGRADEABLE.toString() });
			if (cursor != null && cursor.moveToFirst()) {
				item = transferCursorToDldItem(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		return item;
	}

	/*
	 * true--包含忽略更新的应用 false--不包含忽略更新的应用
	 */
	@Override
	public List<AppEntity> getAllInstalledApp(boolean includeIgnore) {
		List<AppEntity> items = new ArrayList<AppEntity>();
		String sql = null;
		String [] values = null;
		if (includeIgnore) {
			sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
					+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? " + " or "
					+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =?";
			values = new String[] {GameState.LAUNCHABLE.toString(), GameState.UPGRADEABLE.toString()};
		} else {
			sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where " 
					+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? " + " or " + "("
					+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =?" + " and "
					+ GameAppStatusColumns.COLUMN_IS_UPDATEABLE + " =? " + ")";
			values = new String[] {GameState.LAUNCHABLE.toString(), GameState.UPGRADEABLE.toString(),String.valueOf(1)};
		}
		Cursor cursor = mDB.rawQuery(sql,values);
		if (cursor != null && cursor.moveToFirst()) {
			items = new ArrayList<AppEntity>(cursor.getCount());
			do {
				items.add(transferCursorToDldItem(cursor));
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return items;
	}

	private ArrayList<String> getAllRecordAppPkgNames(List<AppEntity> allRecords) {
		ArrayList<String> pkgNameList = new ArrayList<String>();
		for (AppEntity app : allRecords) {
			pkgNameList.add(app.getPkgName());
		}
		return pkgNameList;
	}

	//从数据库中获取在免商店中存在的游戏数据
	@Override
	public List<AppEntity> getFreeStoreGames() {
		return getGameByFreeStoreFlag(true);
	}

	@Override
	public List<AppEntity> getNotFreeStoreGames() {
		return getGameByFreeStoreFlag(false);
	}

	private List<AppEntity> getGameByFreeStoreFlag(boolean isInFreeStore) {
		int freeStoreFlag = 1;//选取免商店中的应用
		if (!isInFreeStore) {
			freeStoreFlag = 0;//选取非免商店中的应用
		}
		List<AppEntity> items = new ArrayList<AppEntity>();
		String sql = null;
		String [] values = null;
		sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
				+ GameAppStatusColumns.COLUMN_IN_FREESTORE + " =? and (" + GameAppStatusColumns.COLUMN_APP_GAME_STATUS +
				" =? or " + GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? )";
		values = new String[] {String.valueOf(freeStoreFlag),GameState.LAUNCHABLE.toString(),GameState.UPGRADEABLE.toString()};
		Cursor cursor = mDB.rawQuery(sql, values);
		if (cursor != null && cursor.moveToFirst()) {
			items = new ArrayList<AppEntity>(cursor.getCount());
			do {
				items.add(transferCursorToDldItem(cursor));
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return items;
	}

	//从数据库中获取来自免商店的游戏数据
	public List<AppEntity> getGamesComeFrmFreeStore() {
		return getGameByFrmFreeStoreFlag(true);
	}

	public List<AppEntity> getGamesComeNotFrmFreeStore() {
		return getGameByFrmFreeStoreFlag(false);
	}

	private List<AppEntity> getGameByFrmFreeStoreFlag(boolean isFrmFreeStore) {
		int frmFreeStoreFlag = 1;//选取免商店中的应用
		if (!isFrmFreeStore) {
			frmFreeStoreFlag = 0;//选取非免商店中的应用
		}
		List<AppEntity> items = new ArrayList<AppEntity>();
		String sql = null;
		String [] values = null;
		sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
				+ GameAppStatusColumns.COLUMN_COME_FRM_FREESTORE + " =? and (" + GameAppStatusColumns.COLUMN_APP_GAME_STATUS +
				" =? or " + GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? )";
		values = new String[] {String.valueOf(frmFreeStoreFlag),GameState.LAUNCHABLE.toString(),GameState.UPGRADEABLE.toString()};
		Cursor cursor = mDB.rawQuery(sql, values);
		if (cursor != null && cursor.moveToFirst()) {
			items = new ArrayList<AppEntity>(cursor.getCount());
			do {
				items.add(transferCursorToDldItem(cursor));
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return items;
	}

	/** 获取占卡槽的应用 **/
	public List<AppEntity> getGamesOccupySlot() {
		List<AppEntity> items = new ArrayList<AppEntity>();
		String sql = null;
		String[] values = null;
		sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where " + GameAppStatusColumns.COLUMN_OCCUPY_SLOT
				+ " =? and (" + GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? or "
				+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? )";
		values = new String[] { String.valueOf(AppEntity.OCCUPY_SLOT), GameState.LAUNCHABLE.toString(),
				GameState.UPGRADEABLE.toString() };
		Cursor cursor = mDB.rawQuery(sql, values);
		if (cursor != null && cursor.moveToFirst()) {
			items = new ArrayList<AppEntity>(cursor.getCount());
			do {
				items.add(transferCursorToDldItem(cursor));
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return items;
	}

	/** 获取不占卡槽的应用 **/
	public List<AppEntity> getGamesNotOccupySlot() {
		List<AppEntity> items = new ArrayList<AppEntity>();
		String sql = null;
		String[] values = null;
		sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where " + GameAppStatusColumns.COLUMN_OCCUPY_SLOT
				+ " =? and (" + GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? or "
				+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " =? )";
		values = new String[] { String.valueOf(AppEntity.NOT_OCCUPY_SLOT), GameState.LAUNCHABLE.toString(),
				GameState.UPGRADEABLE.toString() };
		Cursor cursor = mDB.rawQuery(sql, values);
		if (cursor != null && cursor.moveToFirst()) {
			items = new ArrayList<AppEntity>(cursor.getCount());
			do {
				items.add(transferCursorToDldItem(cursor));
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return items;
	}

	public synchronized void updateOccupySlot(String pkgName, int occupySlot) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_OCCUPY_SLOT, occupySlot);
		mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + "=? ",
				new String[] { pkgName });
	}

	public synchronized void updateScreenshotDirection(String pkgName, String direction) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_SCREENSHOT_DIRECTION, direction);
		mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + "=? ",
				new String[] { pkgName });
	}

	public synchronized void updateTotalSize(String pkgName, long totalSize) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_TOTAL_SIZE, totalSize);
		mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + "=? ",
				new String[] { pkgName });
	}

	public synchronized void updateInFreeStoreFlag(String pkgName, int flag) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_IN_FREESTORE, flag);
		mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + "=? ",
				new String[] { pkgName });
	}
	public synchronized void updateLastLaunchTime(String pkgName, long launchTime) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_LAST_LAUNCH_TIME, launchTime);
		mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + "=? ",
				new String[] { pkgName });
	}

	public synchronized void updateDownloadPath(String pkgName, String url) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_DOWNLOAD_PATH, url);
		mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + "=? ",
				new String[] { pkgName });
	}

	public synchronized void updateFreeflowDownloadPath(String pkgName, String url) {
		if (TextUtils.isEmpty(pkgName)) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(GameAppStatusColumns.COLUMN_FREE_FLOW_DOWNLOAD_PATH, url);
		mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + "=? ",
				new String[] { pkgName });
	}

	public synchronized void updatePosterIcon(String pkgName,Bitmap posterIcon) {
		if (posterIcon == null || TextUtils.isEmpty(pkgName)) {
			return;
		}
		int iconWidth = GameLauncherApplication.getApplication().getResources()
				.getDimensionPixelSize(R.dimen.mygame_game_width);
		int iconHeigth = GameLauncherApplication.getApplication().getResources()
				.getDimensionPixelSize(R.dimen.mygame_game_height);
		if (posterIcon.getHeight() > iconHeigth || posterIcon.getWidth() > iconWidth) {
			posterIcon = PictureUtil.zoomImage(posterIcon, iconWidth, iconHeigth);
		}
		ContentValues values = new ContentValues();
		byte[] data = PictureUtil.flattenBitmap(posterIcon);
		values.put(GameAppStatusColumns.COLUMN_POSTER_ICON, data);
		mDB.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns.COLUMN_PACKAGE_NAME + "=? ",
				new String[] { pkgName });
	}

	public synchronized Bitmap getPosterIconByPkgName(String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return null;
		}
		Cursor cursor = null;
		try {
			String sql = "select * from " + GameAppStatusColumns.TABLE_NAME + " where "
					+ GameAppStatusColumns.COLUMN_PACKAGE_NAME + " =? ";
			cursor = mDB.rawQuery(sql, new String[] { pkgName });
			if (cursor != null && cursor.moveToFirst()) {
				byte[] data = cursor.getBlob(cursor.getColumnIndexOrThrow(GameAppStatusColumns.COLUMN_POSTER_ICON));
				if (data != null && data.length > 0) {
					return Utilities.createIconBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), mContext);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
		return null;
	}

}
