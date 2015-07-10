package com.ireadygo.app.gamelauncher.appstore.info;

import java.util.ArrayList;
import java.util.List;

import com.ireadygo.app.gamelauncher.aidl.rentfree.AppTimeUploadResultItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.AgentAppItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.BannerItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.BindPhoneItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryInfo;
import com.ireadygo.app.gamelauncher.appstore.info.item.CollectionInfo;
import com.ireadygo.app.gamelauncher.appstore.info.item.FeeConfigItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.FreeFlowStatusItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.KeywordItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.QuotaItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.RechargePhoneItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.RentReliefInfo;
import com.ireadygo.app.gamelauncher.appstore.info.item.SlotConfigItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.SubscribeResultItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserHeaderImgItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserSlotInfoItem;
import com.snail.appstore.openapi.exception.HttpStatusCodeException;

public interface IGameInfo {

	//获取指定id的合集或分类下游戏的个数
	int obtainChildrenCount(int type,String id) throws InfoSourceException;

	//获取指定分类的应用列表
	ArrayList<AppEntity> obtainChildren(int dataType, String id,int page) throws InfoSourceException;

	ArrayList<AppEntity> obtainCategotyChildren(String id, int page,String iPlatformId, int number)throws InfoSourceException;

	//获取分类标签游戏列表
	ArrayList<AppEntity> obtainCategotyTagChildren(String id, int page)throws InfoSourceException;
	//获取应用详细信息
	AppEntity obtainItemById(String appId) throws InfoSourceException;

	//强制从远程获取游戏详细信息
	AppEntity obtainItemByIdFrmRemote(String appId) throws InfoSourceException;

	//获取所有分类信息
	List<CategoryInfo> obtainCategorys() throws InfoSourceException;
	//获取合集信息
	
	List<CollectionInfo> obtainCollection(int page) throws InfoSourceException;
	
	//获取关键字列表
	List<KeywordItem> obtainKeywords() throws InfoSourceException;

	//按关键字获取关键字列表
	List<String> obtainKeywordsByWord(String word, int iPlatformId, String cAppType) throws InfoSourceException;

	//获取应用下载地址
	String obtainDownloadUrl(long id) throws InfoSourceException;

	//指定关键字执行搜索
	List<AppEntity> searchByKeyword(String word, int page, int number, int iPlatformId, String cAppType, String cDynamic) throws InfoSourceException;

	//指定拼音搜索
	List<AppEntity> searchByPinYin(String word, int page, int number, int iPlatformId, String cAppType) throws InfoSourceException;

	//获取指定页的banner图片
	List<BannerItem> obtainBannerList(int page) throws InfoSourceException;

	//查询是否有更新的应用
	List<AppEntity> obtainUpdatableApp(List<AppEntity> appList) throws InfoSourceException;

	//查询应用是否为免商店中的游戏
	List<Long> mapAppWithFreeStore(List<AppEntity> appList) throws InfoSourceException;

	//获取帐号信息
	UserInfoItem getUserInfo() throws InfoSourceException;

	//获取头像列表
	List<UserHeaderImgItem> getUserHeaderImgItems() throws InfoSourceException;

	//保存用户编辑后的个人信息
	void saveUserInfo(String nickName, String cSex, String cPhoto, String cPhone, String birthday) throws InfoSourceException;

	//购买卡槽
	void purchaseMuchSlot(int slotConfigId) throws InfoSourceException;

	//获取卡槽配置
	List<SlotConfigItem> getUserSlotConfigItems() throws InfoSourceException;

	//绑定卡槽
	void bindAppToSlot(String pkgName) throws InfoSourceException;
	
	//解绑卡槽
	void unbindAppToSlot(String pkgName) throws InfoSourceException;

	//获取指定帐号的卡槽信息
	List<UserSlotInfoItem> getUserSlotInfoItems() throws InfoSourceException;

	//通过帐号获取昵称
	String getUserNickName(String uId) throws InfoSourceException;

	//初始化帐号卡槽数量
	void initSlotWithAccount() throws InfoSourceException;

	//获取帐号卡槽总数
	int[] getUserSlotNum() throws InfoSourceException;

	//卡槽券充值
	int[] rechargeSlotTicket(String ticketNum) throws InfoSourceException;

	//兔兔券充值
	int rechargeRabbitTicket(String num, String password) throws InfoSourceException;

	//绑定手机号
	void bindTelToRabbit(String phone) throws InfoSourceException;

	//计算兔兔券和兔兔币配额
	QuotaItem calculateQuota(int money, int type) throws InfoSourceException;

	//获取绑定的手机号码
	BindPhoneItem getBindPhoneNum() throws InfoSourceException;

	//免机170资费配置
	List<FeeConfigItem> getFeeConfig() throws InfoSourceException;

	//170充值
	RechargePhoneItem rechargePhone(long id, String phoneNum, int feeNum) throws InfoSourceException;

	//上报终端个推相关信息，返回需要设置的tags
	String[] uploadGeituiInfo(String clienId, String appId, String channelId,String phoneType, String nAppVersion) throws InfoSourceException;

	//BSS账号验证
	boolean checkBSSAccount(String phone);

	//免流量请求短信验证码
	void getSmsCode(String phoneNum) throws InfoSourceException;

	//免流量绑定手机
	void bindFreeFlowPhoneNum(String phoneNum,String smsCode) throws InfoSourceException;

	//免流量解绑手机
	void unbindFreeFlowPhoneNum(String phoneNum) throws InfoSourceException;

	//查询免流量状态
	FreeFlowStatusItem getFreeFlowStatus(String phoneNum) throws InfoSourceException;

	//开通免流量
	SubscribeResultItem subScribeFreeFlow(String phoneNum) throws InfoSourceException;

	//获取代理应用列表
	List<AgentAppItem> getAgentAppItems(String appIds) throws InfoSourceException;

	//获取代理应用下载地址
	String getAgentDownloadUrl(String appId) throws InfoSourceException;

	//获取预装列表
	List<AppEntity> getPreLoadList() throws InfoSourceException;

	//常用应用
	List<AppEntity> getCommonApp() throws InfoSourceException;

	// 激活OBOX
	void activateBox(String cSN) throws InfoSourceException;

	// 减免租金应用列表
	List<String> getRentReliefAppList() throws InfoSourceException;

	// 获取游戏时长
	RentReliefInfo getRentReliefAppTime() throws InfoSourceException;

	// 设置游戏时长
	AppTimeUploadResultItem saveAppTime(String cPackage, long nAppTime,String cReqId,String sign) throws InfoSourceException;

	//查询指定SN号对应渠道信息接口
	String getSaleType(String cSN) throws InfoSourceException;

	//绑定免账号
	void bindAccount() throws HttpStatusCodeException, Exception;

	//查询指定SN号对应绑定的免账号接口
	String getSNCorrespondBindAccount(String cSN) throws InfoSourceException;

	//查询指定免账号绑定支付宝快充接口
	String getPaymentSign() throws InfoSourceException;

	// 绑定支付宝快充
	String bindPayment() throws InfoSourceException;

	//绑定代金券
	void bindTicket(String cTicketCode, String cTicketPwd, String cMac) throws InfoSourceException;

	//查询已抵扣次年月租月数的接口
	String getRentReliefMonths() throws InfoSourceException;

	//查询已返还月度租金总额的接口
	String getRebateMoney() throws InfoSourceException;

	//查询兔兔礼券和蜗牛点券的接口
	String[] queryTicketInfo() throws InfoSourceException;

	//清除缓存
	void cleanCached();


	public static class InfoSourceException extends Exception {

		private static final long serialVersionUID = 1086871491154125351L;
		public static final String MSG_UNKNOWN_CLIENT_ERROR ="UNKNOWN_CLIENT_ERROR";
		public static final String MSG_UNKNOWN_SERVER_ERROR ="UNKNOWN_SERVER_ERROR";
		public static final String MSG_NETWORK_ERROR ="NETWORK_ERROR";
		public static final String MSG_SLOT_NOT_ENOUGH_ERROR ="SLOT_NOT_ENOUGH_ERROR";
		public static final String MSG_APP_HAS_BIND_ERROR ="APP_HAS_BIND_ERROR";
		public static final String MSG_SLOT_HAS_INIT_ERROR ="SLOT_HAS_INIT_ERROR";
		public static final String MSG_PERMISSION_ERROR ="PERMISSION_ERROR";
		public static final String MSG_ACCOUNT_ERROR = "ACCOUNT_ERROR";
		public static final String MSG_UNKNOWN_ERROR = "UNKNOWN_ERROR";
		public static final String MSG_NO_ENOUGH_MONEY_ERROR = "NO_ENOUGH_MONEY_ERROR";

		public static final String MSG_RABBIT_TICKET_NOT_EXIST_ERROR = "RABBIT_TICKET_NOT_EXIST_ERROR";
		public static final String MSG_RABBIT_TICKET_USED_ERROR = "MSG_RABBIT_TICKET_USED_ERROR";
		public static final String MSG_RABBIT_TICKET_PASSWORD_WRONG_ERROR = "RABBIT_TICKET_PASSWORD_WRONG_ERROR";
		public static final String MSG_RABBIT_TICKET_TYPE_MATCH_ERROR = "RABBIT_TICKET_TYPE_MATCH_ERROR";
		public static final String MSG_RABBIT_TICKET_ONE_BIND_ERROR = "RABBIT_TICKET_ONE_BIND_ERROR";

		public static final String MSG_SLOT_TICKET_NOT_EXIST_ERROR = "SLOT_TICKET_NOT_EXIST_ERROR";
		public static final String MSG_SLOT_TICKET_USED_ERROR = "SLOT_TICKET_USED_ERROR";
		public static final String MSG_SLOT_TICKET_TYPE_MATCH_ERROR = "SLOT_TICKET_TYPE_MATCH_ERROR";
		public static final String MSG_SLOT_TICKET_ONE_BIND_ERROR = "SLOT_TICKET_ONE_BIND_ERROR";

		public static final String MSG_ILLEGALITY_BSS_ACCOUNT_ERROR = "ILLEGALITY_BSS_ACCOUNT_ERROR";
		public static final String MSG_PHONE_ALREADY_BIND_ERROR = "PHONE_ALREADY_BIND_ERROR";
		public static final String MSG_PHONE_BIND_FAILED_ERROR = "PHONE_BIND_FAILED_ERROR";
		public static final String MSG_BIND_PHONE_WITHOUT_RECHARGE_ERROR = "BIND_PHONE_WITHOUT_RECHARGE_ERROR";

		public static final String MSG_MONEY_NOT_ENOUGH_ERROR = "MSG_MONEY_NOT_ENOUGH_ERROR";
		public static final String MSG_PHONE_NOT_BIND_ERROR = "MSG_PHONE_NOT_BIND_ERROR";
		public static final String MSG_FEE_LIMINT_ERROR = "MSG_FEE_LIMINT_ERROR";

		public static final String MSG_CACHED_EXPIRED = "CACHED_EXPIRED";
		public static final String MSG_NO_CACHED_DATA = "MSG_NO_CACHED_DATA";
		public static final String MSG_ACCOUNT_OUTDATE = "ACCOUNT_OUTDATE";
		public static final String MSG_IMEI_NOT_KNOWN = "IMEI_NOT_KNOWN";

		public static final String MSG_SMS_CODE_ERROR = "SMS_CODE_ERROR";
		public static final String MSG_FREE_FLOW_NOT_SUPPORT = "FREE_FLOW_NOT_SUPPORT";
		public static final String MSG_FREE_FLOW_HAS_OPENED = "FREE_FLOW_HAS_OPENED";
		public static final String MSG_FREE_FLOW_EXPIRED = "FREE_FLOW_EXPIRED";
		public static final String MSG_FREE_FLOW_FAILED = "FREE_FLOW_FAILED";
		public static final String MSG_FREE_FLOW_SMS_USE_OUT = "FREE_FLOW_SMS_USE_OUT";
		public static final String MSG_AGENT_APP_NOT_FOUND = "AGENT_APP_NOT_FOUND";
		
		public static final String MSG_BOX_NOT_EXIST = "BOX_NOT_EXIST";
		public static final String MSG_BOX_INACTIVATE_OR_DISCONTINUED = "BOX_INACTIVATE_OR_DISCONTINUED";
		public static final String MSG_BOX_HAS_BIND_OTHER_ACCOUNT = "BOX_HAS_BIND_OTHER_ACCOUNT";
		public static final String MSG_BOX_UNBIND_ACCOUNT = "BOX_UNBIND_ACCOUNT";
		public static final String MSG_ACCOUNT_UNBIND_PAYMENT_SIGN = "ACCOUNT_UNBIND_PAYMENT_SIGN";
		public static final String MSG_BOX_PARAMETERS_SUBMITTED = "BOX_PARAMETERS_SUBMITTED";
		public static final String MSG_BOX_TICKET_INVALID = "BOX_TICKET_INVALID";
		public static final String MSG_BOX_TICKET_ALREADY_IN_USE = "BOX_TICKET_ALREADY_IN_USE";
		public static final String MSG_BOX_TICKET_HAS_BINDING = "BOX_TICKET_HAS_BINDING";


		public InfoSourceException() {
			super();
		}

		public InfoSourceException(String detailMessage,
				Throwable throwable) {
			super(detailMessage, throwable);
		}

		public InfoSourceException(String detailMessage) {
			super(detailMessage);
		}

		public InfoSourceException(Throwable throwable) {
			super(throwable);
		}
	}
}
