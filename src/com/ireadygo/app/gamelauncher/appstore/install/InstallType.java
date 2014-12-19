package com.ireadygo.app.gamelauncher.appstore.install;

public interface InstallType {
	String INSTALL_TYPE_APK = "APK";
	String INSTALL_TYPE_APK_PATCH = "APK_PATCH";//增量升级包，暂未使用
	String INSTALL_TYPE_APK_WITH_DATA = "ZIP_APK_WITH_DATA";
}
