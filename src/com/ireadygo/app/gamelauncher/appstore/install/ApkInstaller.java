package com.ireadygo.app.gamelauncher.appstore.install;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.FileUtils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.utils.StorageUtils;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

public class ApkInstaller extends AbstractInstaller {

	//系统安装接口需要的安装标志
	private static final int FLAG_INSTALL_INTERNAL         = 0x00000010;
	private static final int FLAG_INSTALL_EXTERNAL         = 0x00000008;
	private static final int FLAG_INSTALL_REPLACE_EXISTING = 0x00000002;
	private static final int FLAG_INSTALL_SILENTLY_INTERNAL         = FLAG_INSTALL_INTERNAL | FLAG_INSTALL_REPLACE_EXISTING;
	private static final int FLAG_INSTALL_SILENTLY_EXTERNAL         = FLAG_INSTALL_EXTERNAL | FLAG_INSTALL_REPLACE_EXISTING;

	//系统安装接口返回的结果码
	private static final int INSTALL_SUCCEEDED                              = 1;
	private static final int INSTALL_FAILED_INVALID_APK                     = -2;
	private static final int INSTALL_FAILED_INSUFFICIENT_STORAGE            = -4;
	private static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES           = -103;
	private static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;

	private boolean mSelfIsSystemApp;
	private HashMap<String, InstallResponse> mInteractiveInstallMap = new HashMap<String, IInstaller.InstallResponse>();
	private HashMap<String, String> mPkgFileMap = new HashMap<String, String>();//pkgName -- fileName
	private ExecutorService mThreadPool = GameLauncherThreadPool.getFixedThreadPool();

	public ApkInstaller(Context context) {
		super(context);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		intentFilter.addDataScheme("package");
		getContext().registerReceiver(mPackageAddReceiver, intentFilter);

		mSelfIsSystemApp = PackageUtils.selfIsSystemApp(context);
		mPkgFileMap.clear();
	}

	@Override
	public void shutdown() {
		super.shutdown();
		getContext().unregisterReceiver(mPackageAddReceiver);
	}

	@Override
	public void install(InstallResponse response, String file, Object... params) {
		// 1.检测文件是否可读
		File pkgFile = new File(file);
		String pkgName = (String)params[0];
		if (!isFileReadable(response,pkgFile,false)) {
			return;
		}

		reportStepStart(response, IInstaller.STEP_INSTALL);
		mPkgFileMap.put(pkgName, file);
		if (mSelfIsSystemApp) {
			//当前为系统应用或手机已经root，静默安装
			installSilently(response,pkgFile,pkgName);
		} else {
			//当前不是系统应用且手机没有root，交互安装
			installInteractively(response,pkgFile,pkgName);
		}
//		//开放式系统，只进行交互安装
//		installInteractively(response,pkgFile,pkgName);

	}

	//静默安装
	private void installSilently(final InstallResponse response,final File pkgFile,final String pkgName) {
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				int flags =FLAG_INSTALL_SILENTLY_INTERNAL;
				int selectLocation = StorageUtils.getInstallPath(getContext());
				if(selectLocation == StorageUtils.APP_INSTALL_SDCARD){
					flags = FLAG_INSTALL_SILENTLY_EXTERNAL;
				}
				doInstallSilently(flags,response,pkgFile,pkgName);
			}
		});
	}

	private void doInstallSilently(final int flags,final InstallResponse response,final File packageFile, String packageName) {
		PackageManager pm = getContext().getPackageManager();
		Exception cause = null;
		try {
			Method method = pm.getClass().getDeclaredMethod("installPackage",
					Uri.class, IPackageInstallObserver.class, int.class, String.class);
			
			method.invoke(pm, Uri.fromFile(packageFile), new IPackageInstallObserver.Stub() {

				@Override
				public void packageInstalled(String packageName, int returnCode)
						throws RemoteException {
					String errorMessage = null;
					switch (returnCode) {
					case INSTALL_SUCCEEDED:
						reportSuccess(response, null);
						deleteSrcFile(packageName);
						return;
					case INSTALL_FAILED_INSUFFICIENT_STORAGE:
						if(flags == FLAG_INSTALL_SILENTLY_EXTERNAL){
							doInstallSilently(FLAG_INSTALL_SILENTLY_INTERNAL, response, packageFile, packageName);
							return;
						}
						errorMessage = InstallMessage.INSUFFICIENT_STORAGE;
						break;
					case INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES:
						errorMessage = InstallMessage.INCONSISTENT_CERTIFICATES;
						break;
					case INSTALL_FAILED_INVALID_APK:
						errorMessage = InstallMessage.INVALID_APK;
						break;
					case INSTALL_PARSE_FAILED_NO_CERTIFICATES:
						errorMessage = InstallMessage.INSTALL_PARSE_FAILED_NO_CERTIFICATES;
						break;
					default:
						errorMessage = InstallMessage.INSTALL_SILENTLY_NOT_COMPATIBLE;
					}
					reportFailed(response, new InstallException(errorMessage));
				}
			}, flags, packageName);
			return;
		} catch (IllegalArgumentException e) {
			cause = e;
		} catch (NoSuchMethodException e) {
			cause = e;
		} catch (IllegalAccessException e) {
			cause = e;
		} catch (InvocationTargetException e) {
			cause = e;
		}
		reportFailed(response, new InstallException(
			InstallMessage.INSTALL_SILENTLY_NOT_COMPATIBLE, cause));
	}


	//交互安装
	@SuppressLint("NewApi")
	private void installInteractively(final InstallResponse response,File installFile,final String pkgName) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(installFile), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);//设置为可信来源
		Bundle bundle = new Bundle();
		bundle.putString("PackageName", pkgName);
		GameLauncherApplication.getApplication().getCurrentActivity().startActivityForResult(intent, 555,bundle);//将调用者名称传入
		getContext().startActivity(intent);
		if (response != null) {
			synchronized (mInteractiveInstallMap) {
				mInteractiveInstallMap.put(pkgName,response);
			}
		}
	}

	private void reportForInstallInteractively(String pkgName,boolean success) {
		if (TextUtils.isEmpty(pkgName)) {
			throw new IllegalArgumentException("Install package name is empty!");
		}
		synchronized (mInteractiveInstallMap) {
			InstallResponse response = mInteractiveInstallMap.get(pkgName);
			if (null == response) {
				//直接返回，response为空也没法通知外面
				return;
			}
			if (success) {
				reportSuccess(response, null);
				//安装成功，删除源文件
				deleteSrcFile(pkgName);
			} else {
				reportFailed(response, new InstallException(InstallMessage.UNKNOW_APK_INSTALL_ERROR));
			}
			mInteractiveInstallMap.remove(pkgName);
		}
	}

	//广播接收器，检测安装包成功安装，只用与交互安装
	private BroadcastReceiver mPackageAddReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String packageName = intent.getDataString().split(":")[1];
			reportForInstallInteractively(packageName, true);
		}
	};

	private void deleteSrcFile(String pkgName) {
		//判断是否设置了安装删除源文件
		if (!PreferenceUtils.isInstalledDeleteApk()) {
			return;
		}
		String fileString = mPkgFileMap.get(pkgName);
		if (!TextUtils.isEmpty(fileString)) {
			FileUtils.deleteQuietly(new File(fileString));
			mPkgFileMap.remove(pkgName);
		}
	}

}
