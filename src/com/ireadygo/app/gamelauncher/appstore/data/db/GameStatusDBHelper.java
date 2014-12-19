package com.ireadygo.app.gamelauncher.appstore.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GameStatusDBHelper extends SQLiteOpenHelper {

	private static final String TAG = "DownloadDB";

	private static final String DB_NAME = "download_manager_db";

	private static final int DB_VERSION = 6;//合入免流量的数据库版本后，需要修改

	private static final String SQL_CREATE_TABLE_DOWNLOAD_INFO = "CREATE TABLE " + GameAppStatusColumns.TABLE_NAME
			+ "(" 
			+ GameAppStatusColumns._ID + " INTEGER PRIMARY KEY, " 
			+ GameAppStatusColumns.COLUMN_APP_ID + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_APP_NAME + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_PACKAGE_NAME + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_PKG_TYPE + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_FILE_NAME + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_DOWNLOAD_PATH + " TEXT, "
			+ GameAppStatusColumns.COLUMN_SAVED_PATH + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_FREE_FLOW_DOWNLOAD_PATH + " TEXT, "
			+ GameAppStatusColumns.COLUMN_TOTAL_SIZE + " TEXT, "
			+ GameAppStatusColumns.COLUMN_DOWNLOAD_SIZE + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_APP_GAME_STATUS + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_CUR_VERSION_CODE + " INTEGER, "
			+ GameAppStatusColumns.COLUMN_CUR_VERSION_NAME + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_NEW_VERSION_CODE + " INTEGER, " 
			+ GameAppStatusColumns.COLUMN_NEW_VERSION_NAME + " TEXT, "
			+ GameAppStatusColumns.COLUMN_APP_SIGN + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_REMOTE_ICON_URL + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_LOCAL_ICON_URL + " TEXT, "
			+ GameAppStatusColumns.COLUMN_DESCRIPTION + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_SCREENSHOT_URL + " TEXT, " 
			+ GameAppStatusColumns.COLUMN_CREATE_TIME + " LONG, "
			+ GameAppStatusColumns.COLUMN_IS_UPDATEABLE + " INTEGER, " 
			+ GameAppStatusColumns.COLUMN_FREEFLAG + " INTEGER, " 
			+ GameAppStatusColumns.COLUMN_DOWNLOAD_COUNT + " LONG, "
			+ GameAppStatusColumns.COLUMN_IN_FREESTORE + " INTEGER, " 
			+ GameAppStatusColumns.COLUMN_COME_FRM_FREESTORE + " INTEGER, " 
			+ GameAppStatusColumns.COLUMN_OCCUPY_SLOT + " INTEGER DEFAULT 0, " 
			+ GameAppStatusColumns.COLUMN_SCREENSHOT_DIRECTION + " TEXT, "
			+ GameAppStatusColumns.COLUMN_LAST_LAUNCH_TIME + " LONG, "
			+ GameAppStatusColumns.COLUMN_POSTER_ICON_URL + " TEXT, "
			+ GameAppStatusColumns.COLUMN_POSTER_BG_URL + " TEXT, "
			+ GameAppStatusColumns.COLUMN_POSTER_ICON + " BLOB, "
			+ GameAppStatusColumns.COLUMN_EXTEND + " TEXT"
			+ ")";

	private static final String SQL_DROP_TABLE_DOWNLOAD_INFO = "DROP TABLE IF EXISTS "
			+ GameAppStatusColumns.TABLE_NAME;

	// 新增是否占用卡槽列
	private static final String SQL_ADD_IS_OCCUPY_SLOT_COLUMN = "ALTER TABLE " + GameAppStatusColumns.TABLE_NAME
			+ " ADD COLUMN " + GameAppStatusColumns.COLUMN_OCCUPY_SLOT + " INTEGER DEFAULT 0";

	//新增游戏详情图片方向列
	private static final String SQL_ADD_SCREENSHOT_DIRECTION_COLUMN = "ALTER TABLE " + GameAppStatusColumns.TABLE_NAME
			+ " ADD COLUMN " + GameAppStatusColumns.COLUMN_SCREENSHOT_DIRECTION + " TEXT";

	//新增扩展字段列
	private static final String SQL_ADD_EXTEND_COLUMN = "ALTER TABLE " + GameAppStatusColumns.TABLE_NAME
			+ " ADD COLUMN " + GameAppStatusColumns.COLUMN_EXTEND + " TEXT";

	//新增上次启动时间列
	private static final String SQL_ADD_LAST_LAUNCH_TIME_COLUMN = "ALTER TABLE " + GameAppStatusColumns.TABLE_NAME
			+ " ADD COLUMN " + GameAppStatusColumns.COLUMN_LAST_LAUNCH_TIME + " LONG";
	//新增免流量下载地址列
	private static final String SQL_ADD_FREE_FLOW_DLD_URL_COLUMN = "ALTER TABLE " + GameAppStatusColumns.TABLE_NAME
			+ " ADD COLUMN " + GameAppStatusColumns.COLUMN_FREE_FLOW_DOWNLOAD_PATH + " TEXT";

	//新增海报图标下载地址
	private static final String SQL_ADD_POSTER_ICON_URL_COLUMN = "ALTER TABLE " + GameAppStatusColumns.TABLE_NAME
			+ " ADD COLUMN " + GameAppStatusColumns.COLUMN_POSTER_ICON_URL + " TEXT";

	//新增海报背景下载地址
	private static final String SQL_ADD_POSTER_BG_URL_COLUMN = "ALTER TABLE " + GameAppStatusColumns.TABLE_NAME
			+ " ADD COLUMN " + GameAppStatusColumns.COLUMN_POSTER_BG_URL + " TEXT";

	private static final String SQL_ADD_POSTER_ICON_COLUMN = "ALTER TABLE " + GameAppStatusColumns.TABLE_NAME
			+ " ADD COLUMN " + GameAppStatusColumns.COLUMN_POSTER_ICON + " BLOB";

	public GameStatusDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "SQL_CREATE_TABLE_DOWNLOAD_INFO = " + SQL_CREATE_TABLE_DOWNLOAD_INFO);
		createDB(db);
	}

	private void createDB(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_DOWNLOAD_INFO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		int upgradeVersion = oldVersion;
		if (upgradeVersion == 1) {
			db.beginTransaction();
			db.execSQL(SQL_ADD_IS_OCCUPY_SLOT_COLUMN);
			Cursor cursor = db.query(GameAppStatusColumns.TABLE_NAME, new String[] { GameAppStatusColumns._ID,
					GameAppStatusColumns.COLUMN_PACKAGE_NAME, GameAppStatusColumns.COLUMN_COME_FRM_FREESTORE }, null,
					null, null, null, null);
			if (cursor != null) {
				int isFromFreeStoreIndex = cursor.getColumnIndex(GameAppStatusColumns.COLUMN_COME_FRM_FREESTORE);
				int idIndex = cursor.getColumnIndex(GameAppStatusColumns._ID);
				int pkgNameIndex = cursor.getColumnIndex(GameAppStatusColumns.COLUMN_PACKAGE_NAME);
				while (cursor.moveToNext()) {
					int isFromFreeStore = cursor.getInt(isFromFreeStoreIndex);
					int id = cursor.getInt(idIndex);
//					String pkgName = cursor.getString(pkgNameIndex);
					ContentValues values = new ContentValues();
					values.put(GameAppStatusColumns.COLUMN_OCCUPY_SLOT, isFromFreeStore);
					db.update(GameAppStatusColumns.TABLE_NAME, values, GameAppStatusColumns._ID + "=?",
							new String[] { id + "" });
				}
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			upgradeVersion = 2;
		}
		if (upgradeVersion == 2) {
			db.beginTransaction();
			db.execSQL(SQL_ADD_SCREENSHOT_DIRECTION_COLUMN);
			db.execSQL(SQL_ADD_EXTEND_COLUMN);
			db.setTransactionSuccessful();
			db.endTransaction();
			upgradeVersion = 3;
		}
		if (upgradeVersion == 3) {
			db.beginTransaction();
			db.execSQL(SQL_ADD_FREE_FLOW_DLD_URL_COLUMN);
			db.setTransactionSuccessful();
			db.endTransaction();
			upgradeVersion = 4;
		}
		if (upgradeVersion == 4) {
			db.beginTransaction();
			db.execSQL(SQL_ADD_LAST_LAUNCH_TIME_COLUMN);
			db.setTransactionSuccessful();
			db.endTransaction();
			upgradeVersion = 5;
		}
		if (upgradeVersion == 5) {
			db.beginTransaction();
			db.execSQL(SQL_ADD_POSTER_ICON_URL_COLUMN);
			db.execSQL(SQL_ADD_POSTER_BG_URL_COLUMN);
			db.execSQL(SQL_ADD_POSTER_ICON_COLUMN);
			db.setTransactionSuccessful();
			db.endTransaction();
			upgradeVersion = 6;
		}
	}
}
