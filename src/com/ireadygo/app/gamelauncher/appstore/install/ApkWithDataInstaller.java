package com.ireadygo.app.gamelauncher.appstore.install;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executor;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

public class ApkWithDataInstaller extends AbstractInstaller {
	
	private static final String UNZIP_TO = new StringBuffer()
			.append(Environment.getExternalStorageDirectory().getAbsolutePath())
			.append(File.separator)
			.append("iReadyGo")
			.append(File.separator)
			.append("AppStore")
			.append(File.separator)
			.append("Unzip").toString();
	//直接将解压后的数据移动到sd卡根目录，与运营压包方式有关
	private static final String DATA_LOCATION = Environment.getExternalStorageDirectory().getAbsolutePath();
	private ApkInstaller mApkInstaller;
	private ZipInstaller mZipInstaller;
	private Executor mThreadPool = GameLauncherThreadPool.getFixedThreadPool();
	private HashMap<String, String> mPkgFileList = new HashMap<String, String>();//pkgName--src fileName

	public ApkWithDataInstaller(Context context,ApkInstaller apkInstaller,ZipInstaller zipInstaller) {
		super(context);
		mApkInstaller = apkInstaller;
		mZipInstaller = zipInstaller;
		mPkgFileList.clear();
	}

	@Override
	public void install(InstallResponse response, String file, Object... params) {
		File installFile = new File(file);
		if (!isFileReadable(response, installFile, true)) {
			return;
		}
		String pkgName = (String)params[0];
		File unzipFile = new File(new StringBuffer().append(UNZIP_TO).append(File.separator).append(pkgName).toString());
		mPkgFileList.put(pkgName, file);
		//先删除现有的同名文件夹，再解压安装
		deleteExistedThenInstall(response,installFile,pkgName,unzipFile);
	}

	private void deleteExistedThenInstall(final InstallResponse response,
			final File packageFile, final String packageName, final File unzipDir) {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				doDeleteExistedThenInstall(response, packageFile, packageName,
						unzipDir);
			}
		});
	}

	private void doDeleteExistedThenInstall(final InstallResponse response,
			final File packageFile, final String packageName, final File unzipDir) {
			try {
				FileUtils.deleteDirectory(unzipDir);
			} catch (IOException e) {
				if (!isFileWritable(response, unzipDir.getParentFile(), true)) {
					return;
				}
				reportFailed(response, new InstallException(
						InstallMessage.STORAGE_SYSTEM_ERROR));
				return;
			}

			//先解压
			mZipInstaller.install(new InstallResponse() {

				@Override
				public void onInstallSuccessfully(Object info) {
					installApkAndMoveData(response, unzipDir, packageName);
				}

				@Override
				public void onInstallFailed(InstallException ie) {
					reportFailed(response, ie);
					
				}

				@Override
				public void onInstallStepStart(String step) {
					reportStepStart(response, IInstaller.STEP_UNZIP);
				}

				@Override
				public void onInstallProgressChange(String step,int progress) {
					reportProgressChange(response, progress);
				}


			}, packageFile.getAbsolutePath(), unzipDir.getAbsolutePath());
		}

	private void installApkAndMoveData(final InstallResponse response, final File unzipDir,
			final String packageName) {
		File apk = getApkFrmDir(unzipDir);
		if (apk == null) {
			reportFailed(response, new InstallException(InstallMessage.ILLEGAL_APK_WITH_DATA));
			return;
		}
		mApkInstaller.install(new InstallResponse() {

			@Override
			public void onInstallSuccessfully(Object info) {
				moveData(response, unzipDir,packageName);
			}

			@Override
			public void onInstallFailed(InstallException ie) {
				reportFailed(response, ie);
			}

			@Override
			public void onInstallStepStart(String step) {
				reportStepStart(response, IInstaller.STEP_INSTALL);
			}

			@Override
			public void onInstallProgressChange(String step, int progress) {
				
			}


		}, apk.getAbsolutePath(), packageName);
	}

	//从文件夹中获取apk文件
	private File getApkFrmDir(File apkDir) {
		File[] files = apkDir.listFiles();
		if (files == null) {
			return null;
		}
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".apk")) {
				return file;
			}
		}
		return null;
	}

	private void moveData(final InstallResponse response, final File unzipDir,final String pkgName) {
		mThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				if (!hasEnoughSpace(response, unzipDir, DATA_LOCATION)) {
					return;
				}
				if (FileHelper.moveDirectory(unzipDir.getAbsolutePath(),
						DATA_LOCATION)) {
					reportSuccess(response, null);
					//安装成功，删除源文件
					deleteSrcFile(pkgName);
					return;
				}
				if (!isFileWritable(response,new File(DATA_LOCATION), true)
					|| !isFileWritable(response, unzipDir, true)) {
					return;
				}
				reportFailed(response, new InstallException(
						InstallMessage.STORAGE_SYSTEM_ERROR));
			}
		});
	}

	private void deleteSrcFile(String pkgName) {
		if (!PreferenceUtils.isInstalledDeleteApk()) {
			return;
		}
		String fileString = mPkgFileList.get(pkgName);
		if (!TextUtils.isEmpty(fileString)) {
			File srcFile = new File(fileString);
			FileUtils.deleteQuietly(srcFile);
			mPkgFileList.remove(pkgName);
		}
	}
}
