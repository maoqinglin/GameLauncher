package com.ireadygo.app.gamelauncher.appstore.download;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.PowerManager;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;
import com.ireadygo.app.gamelauncher.utils.ToastUtils;

public class DownloadTask {

	private static final String TAG = DownloadTask.class.getSimpleName();

	private final DownloadConfig mConfig;
	private final AppEntity mAppEntity;
	private volatile DownloadState mCurState = DownloadState.DEFAULT;

	private final ConcurrentLinkedQueue<DownloadTaskListener> mDownloadTaskListener = new ConcurrentLinkedQueue<DownloadTaskListener>();
	private final Context mContext;

	private final ExecutorService mTransferPool = Executors.newSingleThreadExecutor();
	private final ExecutorService mReportPool = Executors.newSingleThreadExecutor();
	private final ProgressReporter mProgressReporter = new ProgressReporter();
	private static final String DOWNLOAD_CONTENT_TYPE_APK = "application/vnd.android.package-archive";
	private static final String DOWNLOAD_CONTENT_TYPE_ZIP = "application/zip";

	private static final int SIZE_BUFFER = 4096;

	public DownloadTask(Context context, DownloadConfig config, AppEntity entity) {
		mContext = context;
		mConfig = config;
		mAppEntity = entity;

		try {
			setupDownloadPath();
		} catch (DownloadException e) {
			mCurState = DownloadState.ERROR;
		}
		setupDldedSizeAndSpeed(downloadFileLength(), 0);

		// if (isTaskFinish()) {
		// mCurState = DownloadState.COMPLETE;
		// return;
		// }
		if (isDldFileExcp()) {
			mCurState = DownloadState.ERROR;
		}
	}

	public void updateDownloadPath(String originPath, String freeflowPath) {
		if (AppEntity.TYPE_ZIP.equals(mAppEntity.getResType())) {
			mAppEntity.setResUrl(originPath);
		} else {
			mAppEntity.setDownloadPath(originPath);
		}
		mAppEntity.setFreeflowDldPath(freeflowPath);
	}

	private long downloadFileLength() {
		if (TextUtils.isEmpty(mAppEntity.getFileName())) {
			return 0;
		}
		File downloadFile = new File(mAppEntity.getSavedPath(), mAppEntity.getFileName());
		return downloadFile.length();
	}

	private void setupDldedSizeAndSpeed(long dldedSize, int speed) {
		mAppEntity.setDownloadSize(dldedSize);
		mAppEntity.setDownloadSpeed(speed);
	}

	private boolean isTaskFinish() {
		return (mAppEntity.getTotalSize() > 0) && (mAppEntity.getTotalSize() == downloadFileLength());
	}

	private boolean isDldFileExcp() {
		return (mAppEntity.getTotalSize() > 0) && (mAppEntity.getTotalSize() < mAppEntity.getDownloadSize());
	}

	private void networkWell(Context context) throws DownloadException {
		if (!NetworkUtils.detect(context)) {
			throw new DownloadException(DownloadException.NETWORK_UNAVAIBLE);
		}
	}

	private void setupDownloadPath() throws DownloadException {

		if (TextUtils.isEmpty(mAppEntity.getSavedPath())) {
			mAppEntity.setSavedPath(mConfig.getDownloadPath());
		}
		mAppEntity.setSavedPath(new File(mAppEntity.getSavedPath()).getAbsolutePath());

		File savePath = new File(mAppEntity.getSavedPath());
		if (!savePath.exists() && !savePath.mkdirs()) {
			throw new DownloadException(DownloadException.CAN_NOT_CREATE_DOWNLOAD_PATH);
		}

		if (!savePath.canWrite()) {
			throw new DownloadException(DownloadException.DOWNLOAD_PATH_CAN_NOT_WRITE);
		}
	}

	private void setupDownloadFile(HttpURLConnection conn) throws DownloadException {
		mAppEntity.setFileName(getConnectFileName(conn));
		mAppEntity.setTotalSize(conn.getContentLength());
	}

	private String getConnectFileName(HttpURLConnection conn) throws DownloadException {
		String filename = conn.getURL().getFile();
		filename = filename.substring(filename.lastIndexOf("/") + 1);
		if (filename.indexOf("?") != -1) {
			filename = filename.substring(0,filename.indexOf("?"));
		}
		String contentType = HttpURLConnection.guessContentTypeFromName(filename);
		if (!DOWNLOAD_CONTENT_TYPE_APK.equals(contentType)
				&& !DOWNLOAD_CONTENT_TYPE_ZIP.equals(contentType)) {
			throw new DownloadException(DownloadException.MSG_UNMATCH_CONTENT_TYPE);
		}
		return filename;
	}


	private boolean checkNeedTransfer(HttpURLConnection conn) throws DownloadException {
		setupDownloadFile(conn);
		setupDldedSizeAndSpeed(downloadFileLength(), 0);

		if (isTaskFinish()) {
			reportState(DownloadState.COMPLETE, null);
			return false;
		}
		if (isDldFileExcp()) {
			throw new DownloadException("Downloaded size > Total size.");
		}
		checkStorageSpace();
		return true;
	}

	private HttpURLConnection getTransferConn(String downloadUrl) {
		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty(
					"Accept",
					"image/gif,image/jpeg,image/pjpeg,application/x-shockwaveflash,application/x-ms-xbap,application/xaml+xml,application/vnd.ms-xpsdocument,application/x-ms-application,application/vnd.ms-excel,application/vnd.ms-powerpoint,application/msword,*/*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0(compatible;MSIE7.0;Windows NT 5.2;Trident/4.0;.NET CLR 1.1.4322;.NET CLR 2.0.50727;.NET CLR 3.0.04506.30;.NET CLR 3.0.4506.2152;.NET CLR 3.5.30729)");
			conn.setRequestProperty("Connection", "Keep-Alive");
			String sProperty = "bytes=" + downloadFileLength() + "-";
			// 告诉服务器book.rar这个文件从nStartPos字节开始传
			conn.setRequestProperty("RANGE", sProperty);
			return conn;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	@SuppressLint("NewApi")
	private void checkStorageSpace() throws DownloadException {
		long appLeftSize = mAppEntity.getTotalSize() - mAppEntity.getDownloadSize();
		if (appLeftSize > new File(mAppEntity.getSavedPath()).getFreeSpace()) {
			//默认挂载SD卡空间不足
			String newSavePath = mConfig.getSecondaryDownloadPath();
			if (!TextUtils.isEmpty(newSavePath) && !newSavePath.equals(mAppEntity.getSavedPath())) {
				File file = new File(newSavePath);
				if (file.exists() && appLeftSize <= file.getFreeSpace()) {
					//存在第二SD卡，且空间足够，则将存储路径更换
					mAppEntity.setSavedPath(newSavePath);
					return;
				}
			}
			throw new DownloadException(DownloadException.INSUFFICIENT_STORAGE_SPACE);
		}
	}



	public void addDownloadTaskListener(DownloadTaskListener listener) {
		mDownloadTaskListener.add(listener);
	}

	public void removeDownloadTaskListener(DownloadTaskListener listener) {
		mDownloadTaskListener.remove(listener);
	}

	public AppEntity getDownloadEntity() {
		return mAppEntity;
	}

	public DownloadState getDownloadStatus() {
		return mCurState;
	}

	public void setDownloadStatus(DownloadState state) {
		mCurState = state;
	}

	public void queue() {
		if (!GameLauncherConfig.IGNORE_NETWORTYPE
				&& !PreferenceUtils.getEnable3GDownload()
				&& NetworkUtils.is3GNetConnected(mContext)) {
			reportState(DownloadState.PAUSED, new DownloadException(DownloadException.MSG_UNDOWNLOADABLE_NETWORK_TYPE));
			return;
		}
		if (DownloadState.TRANSFERING == mCurState || DownloadState.PAUSED == mCurState
				|| DownloadState.ERROR == mCurState) {
			reportState(DownloadState.QUEUING, null);
		}
	}

	public void pause() {
		if (DownloadState.TRANSFERING == mCurState || DownloadState.QUEUING == mCurState
				|| DownloadState.ERROR == mCurState) {
			reportState(DownloadState.PAUSED, null);
		}
	}

	public void download() {
		if (!GameLauncherConfig.IGNORE_NETWORTYPE 
				&& !PreferenceUtils.getEnable3GDownload() 
				&& NetworkUtils.is3GNetConnected(mContext)) {
			reportState(DownloadState.PAUSED, new DownloadException(DownloadException.MSG_UNDOWNLOADABLE_NETWORK_TYPE));
			ToastUtils.ToastMsg(mContext.getString(R.string.toast_disable_3G_prompt), true);
			return;
		}
		if (DownloadState.DEFAULT == mCurState || DownloadState.QUEUING == mCurState
				|| DownloadState.PAUSED == mCurState || DownloadState.ERROR == mCurState) {
			reportState(DownloadState.TRANSFERING, null);
			mTransferPool.execute(new TransferDataRunnable());
			if (NetworkUtils.is3GNetConnected(mContext)) {
				ToastUtils.ToastMsg(mContext.getString(R.string.toast_using_3G_download), true);
			}
			//上报下载开始事件
			StaticsUtils.beginDownload(mAppEntity.getAppId());
		}
	}

	public void delete() {
		deleteDownloadFile();
		setupDldedSizeAndSpeed(0, 0);
		reportState(DownloadState.DELETE, null);
		mDownloadTaskListener.clear();
	}

	private void deleteDownloadFile() {
		if (!TextUtils.isEmpty(mAppEntity.getFileName())) {
			new Thread() {
				@Override
				public void run() {
					new File(mAppEntity.getSavedPath(), mAppEntity.getFileName()).delete();
				};
			}.start();
		}
	}

	public interface DownloadTaskListener {

		void onDownloadStateChange(DownloadTask task, DownloadState state, DownloadException e);

		void onDownloadEntityChange(DownloadTask task, AppEntity appEntity);
	}

	public enum DownloadState {

		DEFAULT, QUEUING, TRANSFERING, PAUSED, ERROR, COMPLETE, DELETE
	}

	private final class TransferDataRunnable implements Runnable {

		@Override
		public void run() {
			if (isTransferCancel()) {
				return;
			}
			if (isTaskFinish()) {
				reportState(DownloadState.COMPLETE, null);
				return;
			}

			Thread.currentThread().setName("DOWNLOAD: " + mAppEntity.getAppId());
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

			PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getSimpleName());
			wakeLock.acquire();
			transferData();
			wakeLock.release();
		}
	}

	private void transferData() {
		int retryTimes = mConfig.getRetryTimes();
		for (;;) {
			HttpURLConnection conn = null;
			try {
				networkWell(mContext);
				setupDownloadPath();
				conn = getHttpURLConnection(mAppEntity.getActualDldPath(mContext));
				try {
					if (!checkNeedTransfer(conn)) {
						break;
					}
				} catch (DownloadException e) {
					if (DownloadException.MSG_UNMATCH_CONTENT_TYPE.equals(e.getMessage())) {
						//免流量下载链接重定位导致下载文件类型不正确
						mAppEntity.setFreeflowDldPath("");//清空免流量下载地址
//						Toast.makeText(mContext, "免流量地址下载异常，使用默认默认地址下载", Toast.LENGTH_SHORT).show();
						if (!TextUtils.isEmpty(mAppEntity.getDownloadPath())) {
							conn = getHttpURLConnection(mAppEntity.getDownloadPath());
							if (!checkNeedTransfer(conn)) {
								break;
							}
						}
					} else {
						reportState(DownloadState.ERROR, e);
					}
					break;
				}
				if (isTransferCancel()) {
					break;
				}
				conn.disconnect();

				HttpURLConnection transferConn = getTransferConn(mAppEntity.getActualDldPath(mContext));
				if (transferConn == null) {
					break;
				}
				if (transferingData(getISFromResponse(transferConn))) {
					reportState(DownloadState.COMPLETE, null);
					break;
				}
			} catch (DownloadException e) {
				if (--retryTimes <= 0) {
					reportState(DownloadState.ERROR, e);
					break;
				}

				try {
					Thread.sleep(mConfig.getRetryWaitTime());
				} catch (InterruptedException ie) {
				} // Ignore
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
				
			}

			if (isTransferCancel()) {
				break;
			}
		}

		if (DownloadState.DELETE == mCurState) {
			deleteDownloadFile();
		}
	}

	private HttpURLConnection getHttpURLConnection(String downloadUrl) {
		try {
			URL url = new URL(downloadUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty(
					"Accept",
					"image/gif,image/jpeg,image/pjpeg,application/x-shockwaveflash,application/x-ms-xbap,application/xaml+xml,application/vnd.ms-xpsdocument,application/x-ms-application,application/vnd.ms-excel,application/vnd.ms-powerpoint,application/msword,*/*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0(compatible;MSIE7.0;Windows NT 5.2;Trident/4.0;.NET CLR 1.1.4322;.NET CLR 2.0.50727;.NET CLR 3.0.04506.30;.NET CLR 3.0.4506.2152;.NET CLR 3.5.30729)");

			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.getContentLength();//必须先调用这个函数，否则后面调用getUrl不能得到真实下载地址
			return conn;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	private boolean isTransferCancel() {
		return (DownloadState.QUEUING == mCurState || DownloadState.PAUSED == mCurState
				|| DownloadState.COMPLETE == mCurState || DownloadState.DELETE == mCurState);
	}


	private InputStream getISFromResponse(HttpURLConnection connection) throws DownloadException {
		try {
			return connection.getInputStream();
		} catch (IllegalStateException e) {
			throw new DownloadException(DownloadException.UNKNOW_ERROR, e);
		} catch (IOException e) {
			throw new DownloadException(DownloadException.IO_ERROR, e);
		}
	}

	private boolean transferingData(InputStream is) throws DownloadException {
		int readed = 0;
		byte[] buffer = new byte[SIZE_BUFFER];
		long hasDldLength = downloadFileLength();
		mProgressReporter.init(hasDldLength);
		FileWriter fileWriter = new FileWriter(new File(mAppEntity.getSavedPath(), mAppEntity.getFileName()));
		DownloadException de = null;

		try {
			while (!isTransferCancel() && (readed = is.read(buffer)) >= 0) {
				fileWriter.write(buffer, readed);
				hasDldLength += readed;
				mProgressReporter.report(hasDldLength);
			}
		} catch (IOException e) {
			de = new DownloadException(DownloadException.IO_ERROR, e);
		}
		try {
			fileWriter.close();
		} catch (IOException e) {
			de = new DownloadException(DownloadException.IO_ERROR, e);
		}
		mProgressReporter.shutdown();

		if (de != null) {
			throw de;
		}
		return isTaskFinish();
	}

	private void reportProgress() {
		mReportPool.execute(new Runnable() {

			@Override
			public void run() {
				for (DownloadTaskListener listener : mDownloadTaskListener) {
					listener.onDownloadEntityChange(DownloadTask.this, mAppEntity);
				}
			}
		});
	}

	private void reportState(final DownloadState status, final DownloadException e) {
		if (mCurState == status) {
			return;
		}
		mCurState = status;
		mReportPool.execute(new Runnable() {

			@Override
			public void run() {
				for (DownloadTaskListener listener : mDownloadTaskListener) {
					listener.onDownloadStateChange(DownloadTask.this, mCurState, e);
				}
			}
		});
	}

	private class ProgressReporter {
		private long mCurrentBytes;
		private long mBytesNotified;
		private long mTimeLastNotification;

		public void init(long currentBytes) {
			mCurrentBytes = currentBytes;
			mBytesNotified = mCurrentBytes;
			mTimeLastNotification = System.currentTimeMillis();
		}

		public void report(long currentBytes) {
			long now = System.currentTimeMillis();
			mCurrentBytes = currentBytes;
			if (mCurrentBytes - mBytesNotified > mConfig.getMinProgressStep()
					&& now - mTimeLastNotification > mConfig.getMinProgressTime()) {
				final int speed = (int) ((mCurrentBytes - mBytesNotified) / (now - mTimeLastNotification)) * 1000;
				mBytesNotified = mCurrentBytes;
				mTimeLastNotification = now;
				setupDldedSizeAndSpeed(currentBytes, speed);
				reportProgress();
			}
		}

		public void shutdown() {
			setupDldedSizeAndSpeed(downloadFileLength(), 0);
			reportProgress();
		}
	}

	private class FileWriter {

		private final boolean mIsExternal;
		private OutputStream mOS;
		private File mFileWrite;

		public FileWriter(File fileWrite) {
			mIsExternal = isInExternalStorage(mAppEntity.getSavedPath());
			mFileWrite = fileWrite;
		}

		private boolean isInExternalStorage(String path) {
			try {
				return new File(path).getCanonicalPath().startsWith(
						Environment.getExternalStorageDirectory().getCanonicalPath());
			} catch (IOException e) {
				return true;
			}
		}

		public void write(byte[] buffer, int count) throws IOException {
			if (mOS == null) {
				mOS = new BufferedOutputStream(new FileOutputStream(mFileWrite, true));
			}
			mOS.write(buffer, 0, count);
			if (mIsExternal) { // 避免出现SD卸载时使用此类到进程被杀
				try {
					mOS.close();
				} finally {
					mOS = null;
				}
			}
		}

		public void close() throws IOException {
			if (mOS != null) {
				mOS.close();
				mOS = null;
			}
		}
	}
}