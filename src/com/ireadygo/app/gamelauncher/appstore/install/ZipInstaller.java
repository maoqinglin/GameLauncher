package com.ireadygo.app.gamelauncher.appstore.install;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.concurrent.Executor;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

public class ZipInstaller extends AbstractInstaller {
	private static final int BUFFER_SIZE = 4096;
	private static long sTimeLastNotification;
	private Executor mThreadPool = GameLauncherThreadPool.getCachedThreadPool();
	private Context mContext;

	public ZipInstaller(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public void install(final InstallResponse response, String file, Object... params) {
		String unzipTo = (String) params[0];
		final File fileToUnzip = new File(file);
		if (!isFileReadable(response, fileToUnzip, false)
				||(!hasEnoughSpace(response, fileToUnzip,
						Environment.getExternalStorageDirectory().getAbsolutePath()))) {
			return;
		}

		final File dirUnzipTo = new File(unzipTo);
		dirUnzipTo.mkdirs();
		if (!isFileWritable(response, dirUnzipTo.getParentFile(), false)
			|| !isFileWritable(response, dirUnzipTo, false)) {
			return;
		}

		reportStepStart(response, IInstaller.STEP_UNZIP);
		mThreadPool.execute(new Runnable() {

			@SuppressLint("NewApi")
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				WakeLock wakeLock = ((PowerManager) mContext.getSystemService(
					Context.POWER_SERVICE)).newWakeLock(PowerManager
					.PARTIAL_WAKE_LOCK, ZipInstaller.class.getSimpleName());
				wakeLock.acquire();

				long sizeAfterUnzip = 0;
				long sizeAlreadyUnzip = 0;
				boolean sucess = false;
				try {
					sizeAfterUnzip = getZipFileSize(fileToUnzip);
					unzip(response,fileToUnzip, dirUnzipTo, sizeAfterUnzip,sizeAlreadyUnzip);
					reportProgressChange(response, 100);
					sucess = true;
					reportSuccess(response, null);
				} catch (ZipException e) {
					reportFailed(response, new InstallException(InstallMessage.ERROR_ZIP_PACKAGE, e));
				} catch (IOException e) {
					if (sizeAfterUnzip > 0 && sizeAlreadyUnzip > 0
						&& (sizeAfterUnzip - sizeAlreadyUnzip
						> dirUnzipTo.getFreeSpace())) {
						reportFailed(response, new InstallException(InstallMessage.INSUFFICIENT_STORAGE,e));
						return;
					}

					if (!isFileWritable(response, dirUnzipTo, false)) {
						return;
					}

					reportFailed(response, new InstallException(
						InstallMessage.STORAGE_SYSTEM_ERROR, e));
				} finally {
					if (!sucess) {
						deleteDirectory(dirUnzipTo.getAbsolutePath());
					}
					wakeLock.release();
				}
			}
		});
	}

	private long getZipFileSize(File file) throws ZipException, IOException {
		ZipFile zipFile = new ZipFile(file);
		Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
		long size = 0;
		while (zipEntries.hasMoreElements()) {
			size += ((ZipEntry) zipEntries.nextElement()).getSize();
		}
		return size;
	}

	//zip包解压函数
	private void unzip(final InstallResponse response,File fileToUnzip, File pathUnzipTo, long sizeAfterUnzip,long outSizeAlreadyUnzip)
			throws ZipException, IOException {
		long now = System.currentTimeMillis();
		ZipFile zipFile = new ZipFile(fileToUnzip);
		Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
		ZipEntry zipEntry = null;
		File entryFile = null;
		byte[] buffer = new byte[BUFFER_SIZE];
		int readed = -1;
		InputStream input = null;
		BufferedOutputStream bos = null;

		try {
			while (zipEntries.hasMoreElements()) {
				zipEntry = (ZipEntry) zipEntries.nextElement();
				if (zipEntry.isDirectory()) {
					continue;
				}

				input = zipFile.getInputStream(zipEntry);
				entryFile = new File(pathUnzipTo.getAbsolutePath(),zipEntry.getName());
				if (!entryFile.getParentFile().exists()) {
					entryFile.getParentFile().mkdirs();
				}
				bos = new BufferedOutputStream(new FileOutputStream(entryFile));
				while (true) {
					readed = input.read(buffer);
					if (readed == -1) {
						break;
					}
					outSizeAlreadyUnzip += readed;
					bos.write(buffer, 0, readed);
					now = System.currentTimeMillis();
					if(now - sTimeLastNotification > 800) {
						sTimeLastNotification = now;
						int progress = (int)((1.0f * outSizeAlreadyUnzip / sizeAfterUnzip) * 100);
						reportProgressChange(response, progress);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				bos.close();
				input.close();
			}
		} finally {
			zipFile.close();
		}
	}

	private void deleteDirectory(String file) {
		try {
			FileUtils.deleteDirectory(new File(file));
		} catch (IOException e) {
			// Ignore
		}
	}
}
