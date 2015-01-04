package com.snail.appstore.openapi;

/**
 * 免商店OPEN-API平台配置
 * 
 * @author gewq
 * @version 1.0 2014-6-12
 */
public class AppPlatFormConfig {

	/** 系统默认编码 */
	public final static String DEFAULT_ENCODING = "UTF-8";

	/**
	 * 成功
	 */
	public final static int SUCCEED = 1;

	/** 应用签名 */
	public static final String PARAMETER_SIGN = "sign";
	/** api访问KEY */
	public static final String PARAMETER_API_KEY = "apiKey";
	/** 请求时间 */
	public static final String PARAMETER_REQ_TIME = "reqTime";

	/**
	 * 请求地址
	 */
	public final static String GAME_LIST_URL = "/obox/cms/game/list";// 
	public final static String KEYWORD_LIST_URL = "/obox/cms/game/keyword/list"; // 关键字请求地址
	public final static String KEYWORD_AMOUNT_URL = "/obox/cms/game/keyword/info";// 关键字信息请求地址
	public final static String GAME_CATEGORY_URL = "/obox/cms/game/category";// 游戏分类信息请求地址
	public final static String GAME_COLLECTION_URL = "/obox/cms/game/collection";// 游戏合集信息请求地址
	public final static String GAME_BY_CATEGORY_URL = "/obox/cms/game/category/list";// 分类游戏列表请求地址
	public final static String GAME_DETAIL_URL = "/obox/cms/game";// 游戏详情请求地址
	public final static String GAME_DOWNLOAD_URL = "/obox/cms/game/downloadurl";// 游戏下载请求地址
	public final static String USER_ACCOUNT_URL = "/much/user/account/info";// 用户账号请求地址
	public final static String GAME_BANNER_LIST_URL = "/obox/cms/banner/list";// 获取推荐页
	public final static String GAME_UPDATE_LIST_URL = "/obox/cms/game/update/list";// 获取更新列表
	public final static String GAME_MAPPING_LIST_URL = "/obox/cms/game/mapping/list";// 获取匹配列表
	public final static String ACCOUT_NICKNAME_URL = "/much/user/nickname";// 获取昵称
	public final static String USERHEADER_LIST_URL = "/obox/cms/header/list";// 获取用户头像列表
	public final static String ACCOUNT_SAVE_URL = "/much/user/info";// 保存账号信息

	public final static String MUCH_CONFIG_URL = "/much/user/slot/config";// 卡槽配置
	public final static String MUCH_USE_SLOT_URL = "/much/user/slot/list";// 卡槽列表
	public final static String MUCH_USER_PURCHASE_SLOT_URL = "/much/user/slot/purchase";// 购买卡槽
	public final static String MUCH_SLOT_BIND_URL = "/much/user/slot/bind";// 卡槽绑定
	public final static String MUCH_SLOT_UNBIND_URL = "/much/user/slot/unbind";// 卡槽解绑
	public final static String MUCH_SLOT_DONATE_URL = "/much/user/slot/donate";// 用户赠送
	public final static String MUCH_SLOT_RECHARGE_URL = "/much/user/slot/ticket/recharge";// 卡槽充值

	public final static String MUCH_TICKET_RECHARGE = "/much/user/rabbitticket/recharge";// 兔兔券充值
	public final static String MUCH_TICKET_PHONE_BIND = "/much/user/rabbitticket/bind";// 绑定手机号
	public final static String MUCH_TICKET_QUOTA = "/much/user/rabbitticket/quota";// 计算兔兔券和兔兔币配额
	public final static String MUCH_TICKET_GET_BIND_PHONE = "/much/user/rabbitticket/phonenum"; //获取绑定手机号
	public final static String MUCH_TICKET_FEE_CONFIG = "/much/user/rabbitticket/fee/config";//免机170资费配置
	public final static String MUCH_TICKET_PHONE_RECHARGE = "/much/user/rabbitticket/phone/recharge"; //170充值
	public final static String MUCH_BSS_ACCOUNT_CHECK = "/much/user//bss/account/check";//BSS账号验证
	public final static String MUCH_UPLOAD_GETUI_INFO = "/much/user/msg/getui";//个推配置上传接口
	public final static String MUCH_USER_SMSCODE = "/much/user/smscode";//获取短信验证码
	public final static String MUCH_USER_PHONENUM_BIND = "/much/user/phonenum/bind";//绑定免流量手机号
	public final static String MUCH_USER_PHONENUM_UNBIND = "/much/user/phonenum/unbind";//解绑免流量手机号
	public final static String MUCH_USER_FLOW_STATUS = "/much/user/flow/status";//查询免流量状态
	public final static String MUCH_USER_FLOW_SUBSCRIBE = "/much/user/flow/subscribe";//开通免流量
	public final static String MUCH_USER_AGENT_APP = "/much/user/agent/app";//获取代理模式的应用列表
	public final static String MUCH_USER_AGENT_DOWNURL = "/much/user/agent/downurl";//获取代理应用的下载地址
	public final static String MUCH_PRELOAD_LIST = "/obox/cms/preload/list";//获取预装列表

	//主机以租代售接口
	public final static String ACTIVATE_BOX_URL = "/obox/client/active";//激活
	public final static String RENT_RELIEF_APP_LIST_URL = "/obox/cms/rentReliefApp/list";//减免租金应用列表
	public final static String RENT_RELIEF_APP_TIME_URL = "/obox/client/rentReliefAppTime";//获取游戏时长
	public final static String SAVE_APP_TIME_URL = "/obox/client/saveAppTimeLog";//设置游戏时长
	public final static String RENEWAL_BOX = "/obox/client/renewal";//续费
	public final static String APP_PAYMENT_URL = "/obox/client/appPayment";//主机应用支付
	
	public final static String PARAMETER_SKEYWORD = "sKeyWord"; // 关键字参数
	public final static String PARAMETER_NUSERID = "nUserId";// 用户ID
	public final static String PARAMETER_CIDENTITY = "cIdentity";// 身份串
	public final static String PARAMETER_NAPPID = "nAppId";// 附加id
	

	public final static String PARAMETER_CURRENTPAGE = "currentPage";// 当前页码
	public final static String PARAMETER_NUMBER = "number";// 每页显示条数
	public final static String PARAMETER_CPACKAGES = "cPackages";// 应用包名参数，以逗号分隔
	public final static String PARAMETER_IVERSIONCODE = "iVersioncodes";// 应用版本号,以逗号分隔
	public final static String PARAMETER_CMAINTYPE = "cMainType";// 应用版本号,以逗号分隔

	public static final String PARAMETER_ID = "id";

	public static final String DEFAULT_HTTP_HOST = "http://api.app.snail.com";
	public static final String DEFAULT_APP_ID = "2";

	/** Http Open Api Host */
	public static String HTTP_HOST = DEFAULT_HTTP_HOST;

	/** 应用ID */
	public static Integer APP_ID = null;

	/** 应用KEY */
	public static String API_KEY = null;

	/** 应用密钥 */
	public static String APP_SECRET = null;

	public static void setHttpHost(String httpHost) {
		HTTP_HOST = httpHost;
	}

	public static void setAppId(Integer appId) {
		APP_ID = appId;
	}

	public static void setApiKey(String apiKey) {
		API_KEY = apiKey;
	}

	public static void setAppSecret(String appSecret) {
		APP_SECRET = appSecret;
	}

}
