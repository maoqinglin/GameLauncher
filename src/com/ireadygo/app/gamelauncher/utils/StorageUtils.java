package com.ireadygo.app.gamelauncher.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import org.apache.http.util.EncodingUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;

public class StorageUtils {

	private static final String DWONLOAD_TEST_PATH = "/iReadyGo/TvPlay/Download/Test";
	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取sdcard目录，如果sdcard不可用则返回 Null
	 * 
	 * @param context
	 * @param uniqueName
	 * @returnf
	 */
	public static File getSdcardCacheDir(Context context, String uniqueName) {
		if (getExternalCacheDir(context) == null) {
			return null;
		}
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? new File(getExternalCacheDir(
				context).getPath()
				+ File.separator + uniqueName) : null;
	}

	/**
	 * Check if external storage is built-in or removable.
	 * 
	 * @return True if external storage is removable (like an SD card), false
	 *         otherwise.
	 */
	@TargetApi(9)
	public static boolean isExternalStorageRemovable() {
		if (Utils.hasGingerbread()) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 * 是否已有存储卡挂载
	 * 
	 * @return
	 */
	public static boolean isMounted() {  
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	public static boolean canWrite(Context context) {
		if (isMounted()) {
			File test = new File(Environment.getExternalStorageDirectory()
					.getPath() + DWONLOAD_TEST_PATH);
			if (test.exists()) {
				test.delete();
			}
			if (test.mkdirs() && test.canWrite()) {
				return true;
			}
		}
		return false;
	}

	public static boolean canRead(Context context) {
		if (isMounted()) {
			File test = new File(Environment.getExternalStorageDirectory()
					.getPath() + DWONLOAD_TEST_PATH);
			if (test.exists()) {
				test.delete();
			}
			if (test.mkdirs() && test.canRead()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@TargetApi(8)
	public static File getExternalCacheDir(Context context) {
		if (Utils.hasFroyo()) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	/**
	 * 内存的存储空间
	 * 
	 * @return 内存的存储空间大小 unit G
	 */
	public static String getInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long availableBlocks = stat.getAvailableBlocks();
		double freeStorageD = availableBlocks * blockSize
				/ (double) (1024 * 1024 * 1024);
		double totalStorageD = totalBlocks * blockSize
				/ (double) (1024 * 1024 * 1024);
		String freeStorageString = (new DecimalFormat("#0.00"))
				.format(freeStorageD) + "/";
		String totalStorageString = (new DecimalFormat("#0.00"))
				.format(totalStorageD) + "G";
		return freeStorageString + totalStorageString;
	}

	/**
	 * 外部的存储空间
	 * 
	 * @return 外部的存储空间大小 unit G
	 */
	public static String getExternalMemorySize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long availableBlocks = stat.getAvailableBlocks();
		double freeStorageD = availableBlocks * blockSize
				/ (double) (1024 * 1024 * 1024);
		double totalStorageD = totalBlocks * blockSize
				/ (double) (1024 * 1024 * 1024);
		String freeStorageString = (new DecimalFormat("#0.00"))
				.format(freeStorageD) + "/";
		String totalStorageString = (new DecimalFormat("#0.00"))
				.format(totalStorageD) + "G";
		return freeStorageString + totalStorageString;
	}

	public static boolean externalMemoryAvailable() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}
	
	private static final int APP_INSTALL_AUTO = 0;
	public static final int APP_INSTALL_DEVICE = 1;
	public static final int APP_INSTALL_SDCARD = 2;
	public static final String DEFAULT_INSTALL_LOCATION = "default_install_location";
	
	public static int getInstallPath(Context context){
		int installPathId = Settings.Global.getInt(context.getContentResolver(),
				DEFAULT_INSTALL_LOCATION, APP_INSTALL_AUTO);
		if(installPathId == APP_INSTALL_AUTO){
			if(isMounted()){
				return APP_INSTALL_SDCARD;
			}else{
				return APP_INSTALL_DEVICE;
			}
		}
		return installPathId;
	}

	public static String getExternalStoragePath() {
		return System.getenv("EXTERNAL_STORAGE");
	}

	public static String getSecondaryExternalStoragePath() {
		return System.getenv("SECONDARY_STORAGE");
	}

	/**
	 * 写入SD卡文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param message
	 *            内容
	 */
	public static void writeFileSdcard(String fileName, String message) {
		try {
			mkDir(fileName);
			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取SD卡文件
	 * 
	 * @param fileName
	 *            文件名
	 * @return 内容
	 */
	public static String readFileSdcard(String fileName) {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param fileName
	 *            文件名
	 */
	public static void mkDir(String fileName) {
		try {
			File file = new File(fileName);
			if (file.exists())
				return;

			if (file.isDirectory()) {
				file.mkdirs();
			} else {
				File parent = file.getParentFile();
				if (!parent.exists())
					parent.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
