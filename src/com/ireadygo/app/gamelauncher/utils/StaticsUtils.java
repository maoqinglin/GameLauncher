package com.ireadygo.app.gamelauncher.utils;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.snail.statistics.SnailStatistics;


public class StaticsUtils {
	public static final String STATICS_URL = "http://sqm.woniu.com/actionimp.json";
	private static final String OPEN_GAME_CHANNEL_ID = "8";
	private static final String ACT_SUCCESS = "1";
	private static final String ACT_FAILED = "0";
	private static String mac = "", ip = "";
	private static String account = "";


	/**
	 * 用户登录动作，
	 * @param usedTime--登录花费的时间 actResult--登录成功结果
	 */
	public static void onLogin(long usedTime,boolean actResult) {
		String baseUrl = buildBaseUrl(Action.ON_ACCOUNT_LOGIN,
				GameLauncherConfig.MY_APPID,
				booleanToString(actResult));
		String url = baseUrl;
		send(url,true,String.valueOf(usedTime));
	}

	/**
	 * 切换至前台
	 */
	public static void onResume() {
		send(buildStaticsUrl(Action.ON_RESUME, GameLauncherConfig.MY_APPID, ACT_SUCCESS), true);
	}


	/**
	 * 切换至后台
	 * @param pauseTime--上次处于前台的持续时间
	 */
	public static void onPause(long pauseTime) {
		String baseUrl = buildBaseUrl(Action.ON_PAUSE, GameLauncherConfig.MY_APPID, "");
		String url = baseUrl;
		send(url, true, String.valueOf(pauseTime));
	}

	/**
	 * 设备激活
	 * 传入设备激活时的设备信息
	 */
	public static void DeviceActive() {
		send(buildStaticsUrl(Action.ACTIVE_DEVICE_INFO,GameLauncherConfig.MY_APPID, ""));
	}

	/**
	 * 打开应用本身
	 */
	public static void onCreate() {
		send(buildStaticsUrl(Action.ONCREATE,GameLauncherConfig.MY_APPID, ""));
	}

	/**
	 * 应用关闭
	 * @param lastTime--启动至关闭的时间
	 */
	public static void onDestroy(long lastTime) {
		String baseUrl = buildBaseUrl(Action.ON_DESTROY, GameLauncherConfig.MY_APPID, "");
		String url = baseUrl;
		send(url, true, String.valueOf(lastTime));
	}


	/**
	 * 进入应用详情界面
	 * 
	 * @param game_id
	 */
	public static void onAppDetailOpen(String game_id) {
		send(buildStaticsUrl(Action.OPEN_APP_DETAIL, game_id, ""));
	}


    /**
     * 记录免商店内启动的游戏
     */
    public static void openGameInFreeStore(String game_id) {
        send(buildStaticsUrl(Action.LAUNCH_APP_IN_FREE_STORE,game_id, ""));
    }

    /**
     * 记录免商店外打开免商店的游戏
     *
     * @param game_id
     */
    public static void openGame(String game_id) {
        send(buildStaticsUrl(Action.OPEN_GAME, game_id, ""));
    }


    /**
     * 开始下载
     *
     * @param game_id
     */
    public static void beginDownload(String game_id) {
        send(buildStaticsUrl(Action.DOWNLOAD_START, game_id, ""));
    }

    /**
     * 下载结束以及结果
     *
     * @param game_id
     * @param act_result
     */
    public static void downloadResult(String game_id, boolean act_result) {
        send(buildStaticsUrl(Action.DOWNLOAD_RESULT, game_id, booleanToString(act_result)));
    }

	/**
	 * 安装成功
	 * 
	 * @param game_id
	 */
	public static void installSuccess(String game_id) {
		send(buildStaticsUrl(Action.APP_INSTALL_SUCCESS, game_id, ""));
	}

	/**
	 * 更新成功
	 * 
	 * @param game_id
	 */
	public static void updateSuccess(String game_id) {
		send(buildStaticsUrl(Action.APP_UPDATE_SUCCESS,game_id, ""));
	}


	private static String buildStaticsUrl(Enum<Action> actId, String gameID, String actResult) {
		return buildBaseUrl(actId, gameID, actResult);
	}

	private static String buildBaseUrl(Enum<Action> actId, String gameID, String actResult) {

		return getBaseUrl() 
				+ "?"
				+ "game_id=" + gameID
				+ "&server_id="
				+ "&act_id=" + actId
				+ "&act_result="+ actResult
				+ "&act_time="
				+ "&acount=" + AccountManager.getInstance().getAccount(GameLauncherApplication.getApplication())
				+ "&role_name="
				+ "&c_ip=" + int2ip(DeviceUtil.getNetworkIp(GameLauncherApplication.getApplication()))
				+ "&c_type=19"
				+ "&server_ip="
				+ "&mac_addr=" + DeviceUtil.getMacAddr(GameLauncherApplication.getApplication());
	}

	public static String getBaseUrl() {
		return STATICS_URL;
	}

	private static void send(String url) {
		send(url, false, null);
	}

	private static void send(String url, boolean b) {
		send(url, b, null);
	}

	private static void send(String url, boolean b, String addData) {
		SnailStatistics.commitOneEvent(GameLauncherApplication.getApplication(), url, b, addData,GameLauncherConfig.CHANNEL);
	}


	private static String booleanToString(boolean value) {
		return value ? ACT_SUCCESS : ACT_FAILED;
	}

	public static String int2ip(long ipInt) {
		return String.valueOf(ipInt & 0xFF) + "." 
				+ ((ipInt >> 8) & 0xFF) + "." 
				+ ((ipInt >> 16) & 0xFF) + "."
				+ ((ipInt >> 24) & 0xFF);
	}

	private enum Action {
		//免帐号登录成功
		ON_ACCOUNT_LOGIN(2),
		// app后台切前台，actionResult固定为1
		ON_RESUME(10),
		// app前台切后台，extend_data传从程序进入前台到切到后台的时间
		ON_PAUSE(20),
		//设备激活时的设备信息
		ACTIVE_DEVICE_INFO(30),
		// 打开自己
		ONCREATE(32),
		//应用关闭，extend_data传入程序从启动到关闭的时间
		ON_DESTROY(33),
		// 打开单个app详情页面
		OPEN_APP_DETAIL(36002),
		//在免商店内启动游戏
		LAUNCH_APP_IN_FREE_STORE(36003),
		// 监控免商店外启动免商店游戏的情况
		OPEN_GAME(36101),
		// app安装成功
		APP_INSTALL_SUCCESS(50510),
		// 下载开始
		DOWNLOAD_START(50512),
		// 下载结束，actresult 成功 1 失败 0
		DOWNLOAD_RESULT(50513),
		// app更新成功信息
		APP_UPDATE_SUCCESS(50514);

		private int id;

		Action(int id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return String.valueOf(id);
		}
	}
}
