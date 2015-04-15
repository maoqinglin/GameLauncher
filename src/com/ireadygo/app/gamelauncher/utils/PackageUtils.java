package com.ireadygo.app.gamelauncher.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.game.utils.IconDecorater;
import com.umeng.analytics.MobclickAgent;

public class PackageUtils {

	private static final int FLAG_FORWARD_LOCK = 1 << 29;
	private static final int INSTALL_LOCATION_AUTO = 0;
	private static final int INSTALL_LOCATION_UNSPECIFIED = -1;
	private static final int INSTALL_LOCATION_PREFER_EXTERNAL = 2;

	/**
	 * 查询手机内非系统应用
	 * 
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getNonSystemApps(Context context) {
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);
			// 判断是否为非系统预装的应用程序
			if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				// customs applications
				apps.add(pak);
			}
		}
		return apps;
	}

	public static boolean selfIsSystemApp(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			return ((packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0);
		} catch (NameNotFoundException e) {
			throw new IllegalStateException();
		}
	}

	public static boolean isCanMove(Context context, String pkgName) {
		boolean canBe = false;
		int appInstallLocation = INSTALL_LOCATION_UNSPECIFIED;
		ApplicationInfo info = getAppInfo(context, pkgName);

		if (info == null) {
			return canBe;
		}

		try {
			Field field = ApplicationInfo.class.getDeclaredField("installLocation");
			field.setAccessible(true);
			appInstallLocation = field.getInt(info);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		if ((info.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
			canBe = true;
		} else {
			if ((info.flags & FLAG_FORWARD_LOCK) == 0 && (info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				if (appInstallLocation == INSTALL_LOCATION_PREFER_EXTERNAL
						|| appInstallLocation == INSTALL_LOCATION_AUTO) {
					canBe = true;
				} else if (appInstallLocation == INSTALL_LOCATION_UNSPECIFIED) {

				}
			}
		}

		return canBe;
	}

	public static ApplicationInfo getAppInfo(Context context, String pkgName) {

		PackageManager pm = context.getPackageManager();
		ApplicationInfo info = null;
		try {
			info = pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
		}

		return info;
	}

	public static PackageInfo getPkgInfo(Context context, String pkgName) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = null;
		try {
			pkgInfo = pm.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return pkgInfo;
	}

	public static String getGameName(PackageInfo pkgInfo, Context context) {
		String name = "";
		if (null == pkgInfo) {
			return name;
		}
		PackageManager pm = context.getPackageManager();
		if (null != pm) {
			name = pm.getApplicationLabel(pkgInfo.applicationInfo).toString();
		}
		return name;
	}

	public static String getGameMD5(PackageInfo pkgInfo) {
		String md5 = "";
		if (null == pkgInfo) {
			return md5;
		}
		String path = pkgInfo.applicationInfo.publicSourceDir;
		try {
			md5 = Md5Util.generateFileMD5(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5;
	}

	public static String getSingInfo(Context context, String pkgName) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
			Signature[] signs = packageInfo.signatures;
			Signature sign = signs[0];
			return parseSignature(sign.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String parseSignature(byte[] signature) {
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory
					.generateCertificate(new ByteArrayInputStream(signature));
			return cert.getPublicKey().toString();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getIconUrl(PackageInfo pkgInfo, Context context) {
		String iconPath = "";
		if (null == pkgInfo) {
			return iconPath;
		}
		PackageManager pm = context.getPackageManager();
		if (null != pm) {
			Intent mainIntent = pm.getLaunchIntentForPackage(pkgInfo.packageName);
			if(mainIntent != null){
				final ResolveInfo resolveInfo = pm.resolveActivity(mainIntent, 0);
				if(resolveInfo != null){
					Bitmap icon = new IconDecorater(context).decorateIcon(resolveInfo);
					if (null != icon) {
						iconPath = copyImage2Local(context, icon);
					}	
				}
			}
		}
		return iconPath;
	}

	public static String copyImage2Local(Context context, Bitmap icon) {
		String filePath = "";
		filePath = context.getFilesDir().getAbsolutePath() + File.separator
				+ new Random(System.currentTimeMillis()).nextLong() + ".png";
		if (!PictureUtil.saveBitmap(context, icon, filePath)) {
			filePath = "";
		}
		return filePath;
	}

	/**
	 * 打开包名对应的应用
	 * 
	 * @param context
	 * @param pkgName
	 *            应用的包名
	 */
	public static void launchApp(Context context, String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			Log.e("liu.js", "打开应用时传入的包名错误！pkgName=" + pkgName);
			return;
		}
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
		if (intent != null) {
			SoundPoolManager.instance(context).play(SoundPoolManager.SOUND_ENTER);
			context.startActivity(intent);
		}
	}

	/**
	 * 判断包名所对应的应用是否安装在SD卡上
	 * 
	 * @param packageName
	 * @return, true if install on SD card
	 */
	public static boolean isInstallOnSDCard(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo;
		try {
			appInfo = pm.getApplicationInfo(packageName, 0);
			if ((appInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
				return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isSystemApp(Context context, String packageName) throws NameNotFoundException {
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		android.content.pm.ApplicationInfo app = context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo;
		return isSystemApp(app);
	}

	public static boolean isSystemApp(android.content.pm.ApplicationInfo app) {
		return ((app.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0)
				|| ((app.flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}

	public static void unInstallApp(Context context, String pkgName) {
		if (!TextUtils.isEmpty(pkgName)) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("PkgName", pkgName);
			MobclickAgent.onEvent(context, "uninstall_app", map);

			Uri packageUri = Uri.parse("package:" + pkgName);
			Intent deleteIntent = new Intent();
			deleteIntent.setAction(Intent.ACTION_DELETE);
			deleteIntent.setData(packageUri);
			deleteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(deleteIntent);
		}
	}
}
