package com.ireadygo.app.gamelauncher.appstore.download;

import java.io.File;

import com.ireadygo.app.gamelauncher.utils.StorageUtils;

public class DownloadConfig {

	private static final String DEF_SD_PATH = StorageUtils.getExternalStoragePath();//无外置SD卡时，返回内置SD路径;有外部SD卡时，返回外部SD卡路径
	private static final String SECONDARY_SD_PATH = StorageUtils.getSecondaryExternalStoragePath();//无外置SD卡时，返回路径不可用;有外部SD卡时，返回内置SD卡路径
	private static final String APP_DOWN_LOAD_PATH = "iReadyGo" + File.separator + "appstore" + File.separator + "Download";
	private static final String DEF_DLD_PATH = DEF_SD_PATH + File.separator + APP_DOWN_LOAD_PATH;
	private static final String SECONDARY_DLD_PATH = SECONDARY_SD_PATH + File.separator + APP_DOWN_LOAD_PATH;
	private static final byte DEF_MAX_DLD_TASK_COUNT = 2;
	private static final byte DEF_RETRY_TIMES = 4;
	private static final int DEF_RETRY_WAIT_TIME = 150;
	private static final int DEF_MIN_PROGRESS_STEP = 512;
	private static final int DEF_MIN_PROGRESS_TIME = 800;
	private static final String DEF_FILENAME = "filename";

	private String mDownloadPath = DEF_SD_PATH;
	private byte mMaxDownloadTaskCount = DEF_MAX_DLD_TASK_COUNT;
	private byte mRetryTimes = DEF_RETRY_TIMES;
	private int mRetryWaitTime = DEF_RETRY_WAIT_TIME;
	private int mMinProgressStep = DEF_MIN_PROGRESS_STEP;
	private int mMinProgressTime = DEF_MIN_PROGRESS_TIME;
	private String mDefaultFilename = DEF_FILENAME;

	private DownloadConfig() {
	}

	public static DownloadConfig defaultConfig() {
		return new DownloadConfig();
	}

	public String getDownloadPath() {
		return mDownloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		mDownloadPath = downloadPath;
	}

	public String getSecondaryDownloadPath() {
		return SECONDARY_DLD_PATH;
	}

	public byte getMaxDownloadTaskCount() {
		return mMaxDownloadTaskCount;
	}

	public void setMaxDownloadTaskCount(byte maxdownloadTaskCount) {
		mMaxDownloadTaskCount = maxdownloadTaskCount;
	}

	public byte getRetryTimes() {
		return mRetryTimes;
	}

	public void setRetryTimes(byte retryTimes) {
		mRetryTimes = retryTimes;
	}

	public int getRetryWaitTime() {
		return mRetryWaitTime;
	}

	public void setRetryWaitTime(int retryWaitTime) {
		mRetryWaitTime = retryWaitTime;
	}

	public int getMinProgressStep() {
		return mMinProgressStep;
	}

	public void setMinProgressStep(int mixProgressStep) {
		mMinProgressStep = mixProgressStep;
	}

	public int getMinProgressTime() {
		return mMinProgressTime;
	}

	public void setMinProgressTime(int minProgressTime) {
		mMinProgressTime = minProgressTime;
	}

	public String getDefaultFilename() {
		return mDefaultFilename;
	}

	public void setDefaultFilename(String defaultFilename) {
		mDefaultFilename = defaultFilename;
	}
}
