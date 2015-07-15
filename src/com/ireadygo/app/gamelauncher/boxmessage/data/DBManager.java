package com.ireadygo.app.gamelauncher.boxmessage.data;

import java.util.ArrayList;
import java.util.List;

import com.ireadygo.app.gamelauncher.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;

public class DBManager {

	public static final String TABLE_NAME = "message";
	
	public static final String DB_NAME = "box_message.db";

	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME 
												+ "(" 
												+ MessageColumn._ID + " integer primary key autoincrement, " 
												+ MessageColumn.PKG_NAME + " text, "
												+ MessageColumn.TITLE + " text, "
												+ MessageColumn.SKIP_FLAG + " text, "
												+ MessageColumn.SKIP_TYPE + " text, "
												+ MessageColumn.IS_READ + " text, "
												+ MessageColumn.TIME + " text, "
												+ MessageColumn.EXTEND + " text"
												+ ")";
	
	private static final String RESET_TABLE = "UPDATE sqlite_sequence SET seq = 0 where name = " + "'" + TABLE_NAME + "'";
	
	private static final String DELETE_TABLE = "DELETE from " + TABLE_NAME;
	
	private static final int DB_VER = 1;

	private static DBManager sDBManager;
	
	private final Context mContext;
	
	private DBHelper mHelper;
	
	private DBManager(Context context) {
		mContext = context;
		mHelper = new DBHelper(context);
	}

	public static DBManager getInstance(Context context) {
		if(sDBManager == null) {
			synchronized (DBManager.class) {
				if(sDBManager == null) {
					sDBManager = new DBManager(context);
				}
			}
		}
		return sDBManager;
	}
	
	public void resetDB() {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE);
		db.execSQL(RESET_TABLE);
	}
	
	public List<BroadcastMsg> getAllBroadcastMsg() {
		List<BroadcastMsg> broadcastMsgs = new ArrayList<BroadcastMsg>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		if(cursor != null && cursor.moveToFirst()) {
			while (cursor.moveToNext()) {
				BroadcastMsg msg = new BroadcastMsg();
				msg.id = cursor.getInt(cursor.getColumnIndex(MessageColumn._ID));
				msg.title = cursor.getString(cursor.getColumnIndex(MessageColumn.TITLE));
				msg.pkgName = cursor.getString(cursor.getColumnIndex(MessageColumn.PKG_NAME));
				msg.skipFlag = cursor.getString(cursor.getColumnIndex(MessageColumn.TITLE));
				msg.time = cursor.getLong(cursor.getColumnIndex(MessageColumn.TIME));
				msg.isRead = cursor.getInt(cursor.getColumnIndex(MessageColumn.IS_READ));
				msg.skipType = cursor.getInt(cursor.getColumnIndex(MessageColumn.SKIP_TYPE));
				msg.icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
				broadcastMsgs.add(msg);
			}
		}
		return broadcastMsgs;
	}
	
	public int insertBroadcastMsg(BroadcastMsg msg) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(MessageColumn.TITLE, msg.title);
		cv.put(MessageColumn.PKG_NAME, msg.pkgName);
		cv.put(MessageColumn.SKIP_FLAG, msg.skipFlag);
		cv.put(MessageColumn.SKIP_TYPE, msg.skipType);
		cv.put(MessageColumn.IS_READ, msg.isRead);
		cv.put(MessageColumn.TIME, msg.time);
		return (int)db.insert(TABLE_NAME, null, cv);
	}

	public int getDBDataCount() {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		return cursor.getCount();
	}

	public void removeBroadcastMsg(int id) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.delete(TABLE_NAME, MessageColumn._ID + " = " + "'" + id + "'", null);
	}

	public void updateBroadcastMsg(BroadcastMsg msg) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(MessageColumn.TITLE, msg.title);
		cv.put(MessageColumn.PKG_NAME, msg.pkgName);
		cv.put(MessageColumn.SKIP_FLAG, msg.skipFlag);
		cv.put(MessageColumn.SKIP_TYPE, msg.skipType);
		cv.put(MessageColumn.IS_READ, msg.isRead);
		cv.put(MessageColumn.TIME, msg.time);
		db.update(TABLE_NAME, cv, MessageColumn._ID + " = " + "'" + msg.id + "'", null);
	}

	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VER);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//TODO
		}

	}

	public static interface MessageColumn extends BaseColumns {

		String SKIP_FLAG = "skip_flag";
		
		String PKG_NAME = "pkgName";
		
		String TITLE = "title";

		String TIME = "time";
		
		String SKIP_TYPE = "skip_type";
		
		String IS_READ = "isRead";
		
		String EXTEND = "extend";
	}
}
