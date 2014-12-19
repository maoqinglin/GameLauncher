package com.ireadygo.app.gamelauncher.utils;

import android.content.Context;

import com.ireadygo.app.gamelauncher.appstore.download.IDldOperator.DldException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;

public class DisplayDownloadMsgUtil {
	public static void showMsg(Context context, AppEntity appEntity, DldException e) {

//		if (e != null) {
//			if (DownloadException.NETWORK_UNAVAIBLE.equals(e.getMessage())) {
//				Toast.makeText(
//						context,
//						context.getString(R.string.network_disconnect,
//								appEntity.getName()), Toast.LENGTH_SHORT).show();
//			} else if (DownloadException.INSUFFICIENT_STORAGE_SPACE
//					.equals(e.getMessage())) {
//				Toast.makeText(
//						context,
//						context.getString(R.string.no_space_error,
//								appEntity.getName()), Toast.LENGTH_SHORT).show();
//			} else if (DownloadException.CAN_NOT_CREATE_DOWNLOAD_PATH
//					.equals(e.getMessage())) {
//				Toast.makeText(
//						context,
//						context.getString(
//								R.string.msg_can_not_create_download_path,
//								appEntity.getName()), Toast.LENGTH_SHORT).show();
//			} else if (DownloadException.UNKNOW_ERROR
//					.equals(e.getMessage())) {
//				Toast.makeText(
//						context,
//						context.getString(R.string.msg_unknow_error,
//								appEntity.getName()), Toast.LENGTH_SHORT).show();
//			} else if (DownloadException.DOWNLOAD_PATH_CAN_NOT_WRITE
//					.equals(e.getMessage())) {
//				Toast.makeText(
//						context,
//						context.getString(
//								R.string.msg_download_path_can_not_write,
//								appEntity.getName()), Toast.LENGTH_SHORT).show();
//			} else if (DownloadException.SERVER_ERROR
//					.equals(e.getMessage())) {
//				Toast.makeText(
//						context,
//						context.getString(R.string.msg_server_error,
//								appEntity.getName()), Toast.LENGTH_SHORT).show();
//			} else if (DownloadException.URL_ERROR.equals(e.getMessage())) {
//				Toast.makeText(
//						context,
//						context.getString(R.string.msg_url_error,
//								appEntity.getName()), Toast.LENGTH_SHORT).show();
//			} else if (DownloadException.IO_ERROR.equals(e.getMessage())) {
//				Toast.makeText(
//						context,
//						context.getString(R.string.msg_io_error,
//								appEntity.getName()), Toast.LENGTH_SHORT).show();
//			}
//		}
	}
}
