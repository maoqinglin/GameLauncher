package com.snail.appstore.openapi.service;

import com.snail.appstore.openapi.base.vo.ResultVO;
import com.snail.appstore.openapi.exception.HttpStatusCodeException;
import com.snail.appstore.openapi.json.JSONException;

/**
 * 免商店平台OPEN-API接口
 * 
 * @author gewq
 * @version 1.0 2014-6-12
 */
public interface IAppPlatFormService {

	/**
	 * 关键字搜索返回应用列表 GET
	 * 
	 * @param sKeyWord
	 *            关键字
	 * @param currentPage
	 *            当前页码
	 * @param number
	 *            每页显示记录数
	 * @return 返回应用列表结果集 成功：code:1，obj:PageListVO<List<AppListItemVO>,PageVO> 失败
	 *         code: 错误码
	 * @throws JSONException
	 * @throws HttpStatusCodeException
	 * @throws HttpStatusCodeException
	 *             , JSONException, Exception
	 */
	public ResultVO getGameList(String sKeyWord, Integer currentPage, int number,int iPlatformId, String cAppType, String cDynamic) throws HttpStatusCodeException,
		JSONException, Exception;

	/**
	 * 关键字搜索关键字列表 GET
	 * 
	 * @param sKeyWord
	 *            关键字
	 * @return 返回关键字列表 成功：code:1，obj:List<KeyWordVO> 失败 code: 错误码
	 * @throws HttpStatusCodeException
	 *             , JSONException, Exception
	 */
	public ResultVO getKeywordList(String sKeyWord, int iPlatformId, String cAppType) throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 获取关键字数量 GET
	 * 
	 * @return 成功：code:1，obj:List<AppHotwordVO> 失败 code: 错误码
	 * @throws HttpStatusCodeException
	 *             , JSONException, Exception
	 */
	public ResultVO getKeywordAttr() throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 获取游戏分类 GET
	 * 
	 * @return 成功：code:1，obj:List<AppCollectionVO> 失败 code: 错误码
	 * @throws HttpStatusCodeException
	 *             , JSONException, Exception
	 */
	public ResultVO getGameCategory() throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 获取游戏合集 GET
	 * 
	 * @param currentPage
	 *            当前页码
	 * @return 成功：code:1，obj:PageListVO<List<AppCollectionVO>,PageVO> 失败 code:
	 *         错误码
	 * @throws HttpStatusCodeException
	 *             , JSONException, Exception
	 */
	public ResultVO getGameCollection(Integer currentPage) throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 获取分类下应用 GET
	 * 
	 * @param nCategoryId
	 *            分类id
	 * @param currentPage
	 *            当前页码
	 * @return 成功：code:1，obj:PageListVO<List<AppListItemVO>,PageVO> 失败 code: 错误码
	 */
	public ResultVO getAppListByCategory(Long nCategoryId, Integer currentPage) throws HttpStatusCodeException, JSONException,
		Exception;

	/**
	 * 
	 * @param nCollectionId 合集ID
	 * @param currentPage 当前页码
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws JSONException
	 * @throws Exception
	 */
	public ResultVO getAppListByCollection(Long nCollectionId, Integer currentPage) throws HttpStatusCodeException,
			JSONException, Exception;

	/**
	 * 获取游戏详情 GET
	 * 
	 * @param nAppId
	 *            游戏ID
	 * @return 成功：code:1，obj:List<AppDetailVO> 失败 code: 错误码
	 */
	public ResultVO getAppDetail(Long nAppId) throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 获取游戏下载地址 GET
	 * 
	 * @param nAppId
	 *            游戏ID
	 * @return 成功：code:1，obj:List<AppDownUrlVO> 失败 code: 错误码
	 */
	public ResultVO getDownLoadUrl(Long nAppId) throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 用户账号信息 GET
	 * 
	 * @param nUserId
	 *            用户ID
	 * @return 成功：code:1，obj:List<UserBasicVO> 失败 code: 错误码
	 */
	public ResultVO getAccountInfo() throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 获取推荐页
	 * 
	 * @param currentPage
	 *            当前页
	 * @return 成功：code:1，obj:PageListVO<List<AppBannerVO>,PageVO> 失败 code: 错误码
	 * @throws HttpStatusCodeException
	 *             , JSONException, Exception
	 */
	public ResultVO getBannerList(Integer currentPage) throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 
	 * @param cPackages 应用包名，以逗号分隔
	 * @param iVersioncodes 相匹配的应用当前版本号，以逗号分隔
	 * @param iPlatFormId 
	 * @param cAppType 应用类型 1,游戏; 2, 应用 为空则返回所有
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws JSONException
	 * @throws Exception
	 */
	public ResultVO getAppUpdateList(String cPackages, String iVersioncodes, String iPlatFormId, String cAppType) throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 获取应用匹配列表
	 * 
	 * @param cPackages
	 *            应用包名，以逗号分隔
	 * @param iVersioncodes
	 *            相匹配的应用当前版本号，以逗号分隔
	 * @return
	 * @throws HttpStatusCodeException
	 *             , JSONException, Exception
	 */
	public ResultVO getAppMappingList(String cPackages, String iVersioncodes, String iPlatFormId, String cAppType) throws HttpStatusCodeException, JSONException, Exception;
	/**
	 * 获取用户头像列表
	 * 
	 * @return 成功：code:1，obj:List<UserHeaderVO>失败 code: 错误码
	 * @throws HttpStatusCodeException
	 * @throws JSONException
	 * @throws Exception
	 */
	public ResultVO getUserHeaderList() throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 获取用户昵称
	 * 
	 * @param account
	 *            用户账号
	 * @param uId 需查询昵称的帐号uid
	 * @return 成功：code:1, value:<String> 失败 code: 错误码
	 * @throws HttpStatusCodeException
	 * @throws JSONException
	 * @throws Exception
	 */
	public ResultVO getUserNickName(String uId) throws HttpStatusCodeException, JSONException, Exception;

	/**
	 * 保存用户信息
	 * 
	 * @param url
	 *            头像url地址
	 * @param nickName
	 *            别名
	 * @param sex
	 *            性别
	 * @param age
	 *            年龄
	 * @param email
	 *            邮箱
	 * @param birthday
	 *            生日
	 * @return 成功：code:1 失败 code: 错误码
	 * @throws Exception
	 * @throws HttpStatusCodeException
	 */
	public ResultVO
		saveUserInfo(String nickName, String cSex, String cPhoto, String cPhone, String birthday)
			throws HttpStatusCodeException, Exception;

	/**
	 * 购买卡槽
	 * 
	 * @return  成功 { "code": 1, "msg": "OK" } 失败 { "code": 错误码 }
	 * 
	 * @param ISlotConfigId
	 *            卡槽配置ID
	 * @throws Exception
	 * @throws HttpStatusCodeException
	 */
	public ResultVO purchaseMuchSlot(Integer ISlotConfigId) throws HttpStatusCodeException, Exception;

	/**
	 * 获取魔奇卡槽配置
	 * 
	 * @return  成功 { "code": 1, "obj": list<MuchSlotConfigVO> } 失败 { "code":
	 *            错误码 }
	 * @throws Exception
	 * 
	 */
	public ResultVO getMuchSlotConfigList() throws Exception;

	/**
	 * 绑定卡槽
	 * 
	 * @return 
	 *            成功 { "code": 1, "msg": "OK" } 失败 { "code": 错误码 }
	 * @param CPackage
	 *            包名
	 * @throws Exception
	 * @throws HttpStatusCodeException
	 */
	public ResultVO bindAppMuchSlot(String CPackage) throws HttpStatusCodeException, Exception;

	/**
	 * 解绑卡槽
	 * 
	 * @return 
	 *            成功 { "code": 1, "msg": "OK" } 失败 { "code": 错误码 }
	 * @param CPackage
	 *            包名
	 * @throws Exception
	 * @throws HttpStatusCodeException
	 */
	public ResultVO unBindAppMuchSlot(String CPackage) throws HttpStatusCodeException, Exception;

	/**
	 * 获取该用户的卡槽列表
	 * 
	 * @return 
	 *            成功 { "code": 1, "obj": List<MuchUserAppSlotVO> } 失败 { "code":
	 *            错误码 }
	 * @throws Exception
	 * @throws HttpStatusCodeException
	 */
	public ResultVO getAppUserMuchSlotList() throws HttpStatusCodeException, Exception;

	/**
	 * 初始化用户卡槽数量
	 * 
	 * @return 
	 *            成功 { "code": 1, "msg": "OK" } 失败 { "code": 错误码 }
	 * @throws Exception 
	 * @throws HttpStatusCodeException 
	 */

	public ResultVO donateUserSlot() throws HttpStatusCodeException, Exception;

	/**
	 * 卡槽券充值
	 * @param 卡槽券码
	 * @return 成功{ "code": 1, "msg": "OK",“obj”:{ "iSlotAmount":123,"iLimitTime":60}}  失败 { "code": 错误码 }
	 * @throws Exception
	 * @throws HttpStatusCodeException
	 */
	public ResultVO rechargeSlotTicket(String ticketNum) throws HttpStatusCodeException,Exception;

	/**
	 * 兔兔券充值
	 * @param ticketNum 兔兔券码
	 * @param ticketPassword 兔兔券密钥
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO rechargeRabbitTicket(String ticketNum, String ticketPassword) throws HttpStatusCodeException, Exception;

	/**
	 * 绑定手机号
	 * @param phone 手机号码
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO bindTelToRabbit(String phone) throws HttpStatusCodeException, Exception;

	/**
	 * 计算兔兔券和兔兔币配额
	 * @param rabbitMoney 
	 * @param moneyForm
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO calculateQuota(Integer rabbitMoney, int moneyForm) throws HttpStatusCodeException, Exception;

	/**
	 * 获取绑定手机号
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO getBindPhoneNum() throws HttpStatusCodeException, Exception;

	/**
	 * 免机170资费配置
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO getFeeConfig() throws HttpStatusCodeException, Exception;

	/**
	 * 170充值
	 * @param mobileFreeId 资费ID
	 * @param phone 手机号码
	 * @return 
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO rechargePhone(Long mobileFreeId, String phone, Integer feeNum) throws HttpStatusCodeException, Exception;

	/**
	 * BSS账号验证
	 * @param phone
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO checkBSS(String phone) throws HttpStatusCodeException, Exception;

	/**
<<<<<<< HEAD
	 * 上传终端的个推相关信息
	 * @param clientId
	 * @param appId
	 * @param channelId
	 * @param phoneType
=======
	 * 
	 * @param phoneNum
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO getSmsCode(String phoneNum) throws HttpStatusCodeException, Exception;

	/**
	 * @param phoneNum
	 * @param smsCode
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO bindFreeFlowPhoneNum(String phoneNum,String smsCode) throws HttpStatusCodeException, Exception;

	/**
	 * 
	 * @param phoneNum
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO unbindFreeFlowPhoneNum(String phoneNum) throws HttpStatusCodeException, Exception;

	/**
	 * 
	 * @param phoneNum
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO getFreeFlowStatus(String phoneNum) throws HttpStatusCodeException, Exception;

	/**
	 * 
	 * @param phoneNum
	 * @param productCode
	 * @param month
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO enableFreeFlow(String phoneNum, String productCode, String month) throws HttpStatusCodeException, Exception;
	
	
	/**
	 * 
	 * @param appIds
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO getAgentApps(String appIds) throws HttpStatusCodeException, Exception;


	/**
	 * 上传终端个推信息
	 * @param clientId
	 * @param appId
	 * @param channelId
	 * @param phoneType
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO uploadGetuiInfo(String clientId,String appId,String channelId,String phoneType) throws HttpStatusCodeException, Exception;
	
	
	/**
	 * 获取代理模式下应用的下载地址
	 * @param appId
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO getAgentAppDownloadUrl(String appId) throws HttpStatusCodeException, Exception;

	/**
	 * 获取预装列表
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO getPreLoadList() throws HttpStatusCodeException,Exception;
	
	/**
	 * 激活主机
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO activateBox(String activateCode) throws HttpStatusCodeException,Exception;
	
	/**
	 * 获取减免租金应用列表
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO getRentReliefAppList() throws HttpStatusCodeException,Exception;
	
	/**
	 * 获取游戏时长
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO getRentReliefAppTime() throws HttpStatusCodeException,Exception;
	
	/**
	 * 设置游戏时长
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO saveAppTime(String cPackage,long nAppTime,String cReqId,String sign) throws HttpStatusCodeException,Exception;
	
	/**
	 * 续费
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	public ResultVO renewalBox() throws HttpStatusCodeException,Exception;
	
	/**
	 * 主机应用支付
	 * @param nAppId       应用id
	 * @param cAppOrder    应用订单
	 * @param cAppAccuntId 应用帐号
	 * @param cGoodId      物品id
	 * @param sGoodName    物品名称
	 * @param iGoodNum     物品数量
	 * @param nMoney       金额
	 * @return
	 * @throws HttpStatusCodeException
	 * @throws Exception
	 */
	
	public ResultVO appPayment(String nAppId,String cAppOrder,String cAppAccuntId,String cGoodId,String sGoodName,Integer iGoodNum,Integer nMoney) throws HttpStatusCodeException,Exception;
	
}
