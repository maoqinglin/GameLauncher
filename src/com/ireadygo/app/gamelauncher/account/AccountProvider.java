package com.ireadygo.app.gamelauncher.account;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class AccountProvider extends ContentProvider {

	private static final String[] COLOMN_NAMES = new String[] { "Account", "NickName" };

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
			MatrixCursor cursor = new MatrixCursor(COLOMN_NAMES);
			String account = AccountManager.getInstance().getAccount(getContext());
			String nickName = AccountManager.getInstance().getNickName(getContext());
			cursor.addRow(new Object[] { account, nickName });
			return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
