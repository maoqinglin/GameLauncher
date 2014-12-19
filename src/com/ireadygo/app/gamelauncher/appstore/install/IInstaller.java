package com.ireadygo.app.gamelauncher.appstore.install;

public interface IInstaller {

	// 安装操作的类型
	String STEP_INSTALL = "INSTALL";
	String STEP_UPGRADE = "UPGRAGE";
	String STEP_UNZIP   = "UNZIP";
	String STEP_MERGE   = "MERGE";

	//参数一：安装过程的响应回调函数
	//参数二：安装文件名，全路径
	//参数三：附属参数，标识安装的类型等
	public void install(InstallResponse response,String file,Object ... params);
	public void uninstall(InstallResponse response,String pkgName);
	public void shutdown();

	public interface InstallResponse {
		void onInstallSuccessfully(Object info);//安装成功，传出操作的结果信息
		void onInstallFailed(InstallException ie);//安装失败，传出异常消息
		void onInstallStepStart(String step);
		void onInstallProgressChange(String step,int progress);
	}

	public class InstallException extends Exception {
		private static final long serialVersionUID = 5314569603798498053L;
		public static final String MSG_INSTALL_FAILED = "install game failed";

		public InstallException() {
			super();
		}

		public InstallException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

		public InstallException(String detailMessage) {
			super(detailMessage);
		}

		public InstallException(Throwable throwable) {
			super(throwable);
		}
	}
}
