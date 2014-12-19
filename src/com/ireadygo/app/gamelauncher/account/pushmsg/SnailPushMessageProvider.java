package com.ireadygo.app.gamelauncher.account.pushmsg;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

public class SnailPushMessageProvider extends ContentProvider {

	private static final String AUTHORITY = "com.ireadygo.app.gamelauncher.message.provider";
	public static final Uri URI = Uri.parse("content://" + AUTHORITY);
	public static final Uri URI_LIMIT = Uri.parse("content://" + AUTHORITY + "/" + "NEWEST");
	public static final Uri URI_ITEM = Uri.parse("content://" + AUTHORITY + "/" + "ITEM");
	public static final Uri URI_DELETE = Uri.parse("content://" + AUTHORITY + "/" + "DELETE");
	private static final int MATCH_CODE_ITEM = 0;
	private static final int MATCH_CODE_NEWEST = 1;
	private static final int MATCH_CODE_DELETE = 2;

	private SnailPushMessageDBHelper mDbHelper;
	private ContentResolver mResolver;

	private static UriMatcher uriMatcher;  

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "ITEM/#", MATCH_CODE_ITEM);
		uriMatcher.addURI(AUTHORITY, "NEWEST/#", MATCH_CODE_NEWEST);
		uriMatcher.addURI(AUTHORITY, "DELETE/#", MATCH_CODE_DELETE);
	}

	@Override
	public boolean onCreate() {
		mDbHelper = new SnailPushMessageDBHelper(getContext());
		mResolver = getContext().getContentResolver();
		return true;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String limit = null;
		switch (uriMatcher.match(uri)) {
		case MATCH_CODE_NEWEST:
		case MATCH_CODE_ITEM:
			limit = uri.getLastPathSegment();
			break;

		default:
			break;
		}

		return db.query(SnailPushMessageColumns.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, limit);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long id = db.insert(SnailPushMessageColumns.TABLE_NAME, null, values);
		if(id < 0) {
			throw new SQLiteException("Unable to insert " + values + " for " + uri); 
		}
		Uri newUri = ContentUris.withAppendedId(uri, id);
		mResolver.notifyChange(uri, null);
		return newUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int count = db.delete(SnailPushMessageColumns.TABLE_NAME, selection, selectionArgs);
		mResolver.notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
