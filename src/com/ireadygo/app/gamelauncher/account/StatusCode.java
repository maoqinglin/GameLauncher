package com.ireadygo.app.gamelauncher.account;

public class StatusCode {
	/** 操作成功 **/
	public static final int OPERATION_SUCCESS = 0;

	/** 未初始化 **/
	public static final int UNINITIALIZED = -1;

	/** 登陆取消 **/
	public static final int LOGIN_CANCELED = -2;

	/** 未知错误 **/
	public static final int UNKNOWN_ERROR = -3;

	/** 发送短信失败 **/
	public static final int SEND_SMS_FAILED = -4;

	/** 获取UUID失败 **/
	public static final int GET_UUID_FAILED = -5;

	/** 短信上行通道关闭 **/
	public static final int SMS_UPLINK_CHANNEL_CLOSED = -6;

	/** 服务器返回数据异常 **/
	public static final int SERVICE_DATA_ERROR = -7;

	/** 操作失败 **/
	public static final int OPERATION_FAILED = -8;

	/** 网络异常 **/
	public static final int NETWORK_ERROR = -9;

	/** 一键注册失败 **/
	public static final int ONE_KEY_REGISTER_FAILED = -10;

	/** 登陆失败 **/
	public static final int LOGIN_FAILED = -11;

	/** 账号冻结 **/
	public static final int ACCOUNT_FROZEN = -12;

	/** 账号锁定 **/
	public static final int ACCOUNT_LOCK = -13;

	/** 密码错误 **/
	public static final int PASSWORD_ERROR = -14;

	/** 注册短信发送超时 **/
	public static final int REGISTER_SMS_TIMEOUT = -15;

	/** 无法连接服务器 **/
	public static final int UNABLE_CONNECT_SERVER = -16;

	/** 登陆验证失败 **/
	public static final int LOGIN_FAILED_VALIDATION = -17;

	/** 注册失败 **/
	public static final int REGISTER_FAILED = -18;

	/** 身份不被信任 **/
	public static final int IDENTITY_NOT_TRUSTED = -19;

	/** 无法完成一键注册 **/
	public static final int UNABLE_COMPLETE_ONE_KEY_REGISTER = -20;

	/** 参数为空 **/
	public static final int PARAMETER_IS_EMPTY = -1000;

	/** 需要先登陆 **/
	public static final int SHOULD_LOGIN_FIRST = -1001;

	/** 支付取消 **/
	public static final int PAY_CANCEL = -2002;

	/** 订单已提交 **/
	public static final int ORDER_HAS_SUBMITTED = -2003;

	/** 关闭充值页面 **/
	public static final int CLOSE_RECHARGE_PAGE = -2004;

	/** 支付失败（现已不会返回此状态码，而是会返回具体失败原因的状态码） **/
	public static final int PAY_FAILED = -2005;

	/** 响应时间超时 **/
	public static final int REPONSE_TIMEOUT = 1003;

	/** 参数字符非法 **/
	public static final int PARAMETER_ILLEGAL = 1006;

	/** 缺少参数 **/
	public static final int PARAMETER_MISSING = 1007;

	/** 用户身份过期或不存在 **/
	public static final int USER_IDENTITY_EXPIRED = 1008;

	/** 资源存储错误 **/
	public static final int RESOURCES_STORED_ERROR = 1009;

	/** 订单编号已存在 **/
	public static final int ORDER_NUMBER_EXISTS = 8001;

	/** 调用计费服务出错 **/
	public static final int BILLING_SERVICE_ERROR = 10001;

	/** 调用计费服务参数缺少 **/
	public static final int BILLING_SERVICE_PARAMETER_MISSING = 10002;

	/** 调用计费服务返回结果为空 **/
	public static final int BILLING_SERVICE_RETURN_NULL = 10003;

	/** 调用计费登陆服务异常 **/
	public static final int BILLING_SERVICE_LOGIN_ERROR = 10007;

	/** 调用计费登出服务异常 **/
	public static final int BILLING_SERVICE_LOGOUT_ERROR = 10008;

	/** 调用计费支付服务异常 **/
	public static final int BILLING_SERVICE_PAYMENT_ERROR = 10009;

	/** 调用计费余额查询服务异常 **/
	public static final int BILLING_SERVICE_BALANCE_QUERY_ERROR = 10010;

	/** 调用计费加点服务异常 **/
	public static final int BILLING_SERVICE_PLUS_POINT_ERROR = 10011;

}
