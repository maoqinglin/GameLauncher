package com.ireadygo.app.gamelauncher.account.pushmsg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SnailPushMessageDBHelper extends SQLiteOpenHelper {

	private static final String TAG = "SnailPushMessageDBHelper";

	private static final String DB_NAME = "snail_message.db";

	private static final int DB_VERSION = 1;

	private static final String SQL_CREATE_TABLE_MESSAGE_INFO = 
			"CREATE TABLE " + SnailPushMessageColumns.TABLE_NAME + 
			"(" +
			SnailPushMessageColumns._ID + " INTEGER PRIMARY KEY, " + 
			SnailPushMessageColumns.COLUMN_MSG_TITLE + " TEXT, " + 
			SnailPushMessageColumns.COLUMN_MSG_CONTENT + " TEXT, " + 
			SnailPushMessageColumns.COLUMN_MSG_TYPE + " INTEGER, " + 
			SnailPushMessageColumns.COLUMN_MSG_URL + " TEXT, " + 
			SnailPushMessageColumns.COLUMN_MSG_PAGE_ID + " TEXT, " + 
			SnailPushMessageColumns.COLUMN_MSG_PAGE_TITLE + " TEXT, " +
			SnailPushMessageColumns.COLUMN_MSG_TEXT + " TEXT, " + 
			SnailPushMessageColumns.COLUMN_MSG_CREATE_DATE + " LONG, " + 
			SnailPushMessageColumns.COLUMN_MSG_EXTEND + " TEXT" + 
			")";

	private static final String SQL_DROP_TABLE_MESSAGE_INFO = "DROP TABLE IF EXISTS "
			+ SnailPushMessageColumns.TABLE_NAME;

	private void createDB(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_MESSAGE_INFO);
	}

	public SnailPushMessageDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "SQL_CREATE_TABLE_MESSAGE_INFO = " + SQL_CREATE_TABLE_MESSAGE_INFO);
		createDB(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//TODO
	}

}
