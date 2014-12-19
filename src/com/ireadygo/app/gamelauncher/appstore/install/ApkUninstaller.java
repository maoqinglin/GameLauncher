package com.ireadygo.app.gamelauncher.appstore.install;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.ireadygo.app.gamelauncher.utils.PackageUtils;
import com.ireadygo.app.gamelauncher.utils.ShellUtils;
import com.ireadygo.app.gamelauncher.utils.ShellUtils.CommandResult;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;

public class ApkUninstaller extends AbstractInstaller {

	private ExecutorService mThreadPool = GameLauncherThreadPool.getFixedThreadPool();
	private boolean mSelfIsSystemApp = false;
	private HashMap<String, InstallResponse> mUninstallInstallMap = new HashMap<String, IInstaller.InstallResponse>();
	private Context mContext;

	public ApkUninstaller(Context context) {
		super(context);
		mContext = context;
		mSelfIsSystemApp = PackageUtils.selfIsSystemApp(context);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addDataScheme("package");
		mContext.registerReceiver(mReceiver, intentFilter);
	}

	/**
	 * uninstall according conditions
	 * <ul>
	 * <li>if system application or rooted, see
	 * {@link #uninstallSilent(Context, String)}</li>
	 * <li>else see {@link #uninstallNormal(Context, String)}</li>
	 * </ul>
	 * 
	 * @param context
	 * @param packageName
	 *            package name of app
	 * @return whether package name is empty
	 * @return
	 */
	public void uninstall(final InstallResponse response, String packageName) {
		if (mSelfIsSystemApp || ShellUtils.checkRootPermission()) {
			uninstallSilent(response,packageName);
			return;
		}
		if (uninstallNormal(response, packageName)) {
			mUninstallInstallMap.put(packageName, response);
			return;
		}
		reportFailed(response, new InstallException(InstallMessage.UNINSTALL_FAILED_INVALID_PACKAGE));
	}

	@Override
	public void shutdown() {
		super.shutdown();
		mContext.unregisterReceiver(mReceiver);
	}

	/**
	 * uninstall package normal by system intent
	 * 
	 * @param context
	 * @param packageName
	 *				package name of app
	 * @return whether package name is empty
	 */
	private boolean uninstallNormal(InstallResponse response, String packageName) {
		if (packageName == null || packageName.length() == 0) {
			return false;
		}

		Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse(new StringBuilder(32).append("package:")
				.append(packageName).toString()));
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getContext().startActivity(i);
		return true;
	}

	/**
	 * uninstall package and clear data of app silent by root
	 * 
	 * @param context
	 * @param packageName
	 *            package name of app
	 * @return
	 * @see #uninstallSilent(Context, String, boolean)
	 */
	private void uninstallSilent(final InstallResponse response, final String packageName) {
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				uninstallSilent(response,packageName, true);
			}
		});
	}

	/**
	 * uninstall package silent by root
	 * <ul>
	 * <strong>Attentions:</strong>
	 * <li>Don't call this on the ui thread, it may costs some times.</li>
	 * <li>You should add <strong>android.permission.DELETE_PACKAGES</strong> in
	 * manifest, so no need to request root permission, if you are system app.</li>
	 * </ul>
	 * 
	 * @param context
	 *            file path of package
	 * @param packageName
	 *            package name of app
	 * @param isKeepData
	 *            whether keep the data and cache directories around after
	 *            package removal
	 * @return <ul>
	 *         <li>{@link #DELETE_SUCCEEDED} means uninstall success</li>
	 *         <li>{@link #DELETE_FAILED_INTERNAL_ERROR} means internal error</li>
	 *         <li>{@link #DELETE_FAILED_INVALID_PACKAGE} means package name
	 *         error</li>
	 *         <li>{@link #DELETE_FAILED_PERMISSION_DENIED} means permission
	 *         denied</li>
	 */
	private void uninstallSilent(InstallResponse response,String packageName, boolean isKeepData) {
		if (packageName == null || packageName.length() == 0) {
			reportFailed(response, new InstallException(InstallMessage.UNINSTALL_FAILED_INVALID_PACKAGE));
			return;
		}

		/**
		 * if context is system app, don't need root permission, but should add
		 * <uses-permission android:name="android.permission.DELETE_PACKAGES" />
		 * in mainfest
		 **/
		StringBuilder command = new StringBuilder().append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall")
				.append(isKeepData ? " -k " : " ").append(packageName.replace(" ", "\\ "));
		CommandResult commandResult = ShellUtils.execCommand(command.toString(), !mSelfIsSystemApp, true);
		if (commandResult.successMsg != null
				&& (commandResult.successMsg.contains("Success") || commandResult.successMsg.contains("success"))) {
			reportSuccess(response, null);
			return;
		}
		Log.e("newApp",
				new StringBuilder().append("uninstallSilent successMsg:").append(commandResult.successMsg)
						.append(", ErrorMsg:").append(commandResult.errorMsg).toString());
		String errMsg;
		if (commandResult.errorMsg == null) {
			errMsg = InstallMessage.UNINSTALL_FAILED_INVALID_PACKAGE;
		}else if (commandResult.errorMsg.contains("Permission denied")) {
			errMsg = InstallMessage.UNINSTALL_FAILED_PERMISSION_DENIED;
		} else {
			errMsg = InstallMessage.UNINSTALL_FAILED_INTERNAL_ERROR;
		}
		reportFailed(response, new InstallException(errMsg));
	}


	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				String packageName = intent.getDataString().split(":")[1];
				reportUninstallSuccess(packageName);
			}
		}
	};

	private void reportUninstallSuccess(String pkgName) {
		Log.e("newApp", "reportUninstallSuccess:"+pkgName);
		if (TextUtils.isEmpty(pkgName)) {
			throw new IllegalArgumentException("Uninstall package name is empty!");
		}
		synchronized (mUninstallInstallMap) {
			InstallResponse response = mUninstallInstallMap.get(pkgName);
			if (null == response) {
				//直接返回，response为空也没法通知外面
				return;
			}
			reportSuccess(response, null);
			mUninstallInstallMap.remove(pkgName);
		}
	}
}
