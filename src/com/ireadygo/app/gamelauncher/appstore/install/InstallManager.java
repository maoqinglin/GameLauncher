package com.ireadygo.app.gamelauncher.appstore.install;

import android.content.Context;

public class InstallManager implements IInstaller {

	private ApkInstaller mApkInstaller;
	private ApkWithDataInstaller mApkWithDataInstaller;
	private ZipInstaller mZipInstaller;
	private ApkUninstaller mApkUninstaller;
	private Context mContext;

	public InstallManager (Context context) {
		mContext = context;
		mApkInstaller = new ApkInstaller(mContext);
		mZipInstaller = new ZipInstaller(mContext);
		mApkWithDataInstaller = new ApkWithDataInstaller(mContext,mApkInstaller,mZipInstaller);
		mApkUninstaller = new ApkUninstaller(context);
	}

	//参数params
	//InstallManager-- String installtype
	//Other Installer -- String pkgName 
	@Override
	public void install(InstallResponse response, String file, Object... params) {
		String installType = (String)params[0];
		if (InstallType.INSTALL_TYPE_APK.equals(installType)) {
			mApkInstaller.install(response, file, shiftParams(params));
		} else if (InstallType.INSTALL_TYPE_APK_WITH_DATA.equals(installType)) {
			mApkWithDataInstaller.install(response, file, shiftParams(params));
		} else if (InstallType.INSTALL_TYPE_APK_PATCH.equals(installType)) {
			//暂时没有安装增量升级包的需求
		} else {
			throw new IllegalArgumentException("Unsupported install type!");
		}
//		免商店只有APK类型，因此目前只有apk安装
//		mApkInstaller.install(response, file, (String)params[0]);
	}

	public void shutdown() {
		mApkInstaller.shutdown();
		mApkUninstaller.shutdown();
	}

	private Object[] shiftParams(Object[] params) {
		Object [] shifted = new Object[params.length - 1];
		for (int i = 1; i < params.length; i++) {
			shifted[i - 1] = params[i];
		}
		return shifted;
	}

	@Override
	public void uninstall(InstallResponse response, String pkgName) {
		mApkUninstaller.uninstall(response, pkgName);
	}

}
