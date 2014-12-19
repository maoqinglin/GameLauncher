package com.ireadygo.app.gamelauncher.appstore.install;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.ireadygo.app.gamelauncher.utils.StorageUtils;

public class AbstractInstaller implements IInstaller{

	private Context mContext;
	private Handler mHandler;

	public AbstractInstaller(Context context) {
		mContext = context;
		mHandler = new Handler(context.getMainLooper());
	}

	protected Context getContext() {
		return mContext;
	}

	protected Handler getHandler() {
		return mHandler;
	}

	@Override
	public void install(InstallResponse response, String file, Object... params) {
	}


	//状态输出函数
	protected void reportSuccess(final InstallResponse response,final Object info) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (null != response) {
					response.onInstallSuccessfully(info);
				}
			}
		});
	}

	protected void reportFailed(final InstallResponse response,final InstallException ie) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (null != response) {
					response.onInstallFailed(ie);
				}
			}
		});
	}

	protected void reportStepStart(final InstallResponse response,final String step) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (null != response) {
					response.onInstallStepStart(step);
				}
			}
		});
	}

	protected void reportProgressChange(final InstallResponse response,final int progress) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				if (response != null) {
					//暂时只有解压这种状态才会调用
					response.onInstallProgressChange(IInstaller.STEP_UNZIP,progress);
				}
			}
		});
	}

	//文件状态检查函数
	protected boolean isFileReadable(InstallResponse response,File file,boolean checkExternalStorageMounted) {
		if (!isFileExist(response, file, checkExternalStorageMounted)) {
			return false;
		}
		if (!file.canRead()) {
			reportFailed(response, new InstallException(InstallMessage.PACKAGE_UNREADABLE));
			return false;
		}
		return true;
	}

	protected boolean isFileWritable(InstallResponse response, File file, boolean checkExternalStorageMounted) {
		if (!isFileExist(response, file, checkExternalStorageMounted)) {
			return false;
		}
		if (!file.canWrite()) {
			reportFailed(response, new InstallException(InstallMessage.STORAGE_SYSTEM_ERROR));
			return false;
		}
		return true;
	}

	@SuppressLint("NewApi")
	protected boolean hasEnoughSpace(InstallResponse response,File checkFile, String dstPath) {
		long writeLength = checkFile.length();
		File dstFileLocation = new File(dstPath);
		if (dstFileLocation.exists() && (dstFileLocation.getFreeSpace() >= writeLength)) {
			return true;
		}
		reportFailed(response, new InstallException(InstallMessage.INSUFFICIENT_STORAGE));
		return false;
	}

	private boolean isFileExist(InstallResponse response, File file, boolean checkExternalStorageMounted) {
		if ((checkExternalStorageMounted || isFileInExternalStorage(file))
				&& !StorageUtils.isMounted()) {
			reportFailed(response, new InstallException(InstallMessage.EXTERNAL_STORAGE_UNMOUNTED));
			return false;
		}
		if (!file.exists()) {
			reportFailed(response, new InstallException(InstallMessage.PACKAGE_NOT_FOUND));
			return false;
		}
		return true;
	}

	private boolean isFileInExternalStorage(File file) {
		return file.getAbsolutePath().matches("^"
				+ Environment.getExternalStorageDirectory().getAbsolutePath());
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void uninstall(InstallResponse response, String pkgName) {
		
	}


}
