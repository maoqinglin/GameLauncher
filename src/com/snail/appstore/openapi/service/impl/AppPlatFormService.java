package com.snail.appstore.openapi.service.impl;

import static com.snail.appstore.openapi.AppPlatFormConfig.ACCOUNT_SAVE_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.ACCOUT_NICKNAME_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.GAME_BANNER_LIST_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.GAME_BY_CATEGORY_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.GAME_BY_COLLECTION_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.GAME_CATEGORY_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.GAME_COLLECTION_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.GAME_DETAIL_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.GAME_DOWNLOAD_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.GAME_LIST_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.GAME_MAPPING_LIST_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.GAME_UPDATE_LIST_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.KEYWORD_AMOUNT_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.KEYWORD_LIST_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_BSS_ACCOUNT_CHECK;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_CONFIG_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_SLOT_BIND_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_SLOT_DONATE_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_SLOT_UNBIND_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_TICKET_FEE_CONFIG;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_TICKET_GET_BIND_PHONE;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_TICKET_PHONE_BIND;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_TICKET_PHONE_RECHARGE;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_TICKET_QUOTA;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_TICKET_RECHARGE;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_USER_PURCHASE_SLOT_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.MUCH_USE_SLOT_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_CAPPTYPE;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_CDYNAMIC;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_CIDENTITY;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_CMAINTYPE;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_CPACKAGES;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_CURRENTPAGE;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_ID;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_IPLATFORMID;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_IVERSIONCODE;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_NAPPID;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_NUMBER;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_NUSERID;
import static com.snail.appstore.openapi.AppPlatFormConfig.PARAMETER_SKEYWORD;
import static com.snail.appstore.openapi.AppPlatFormConfig.USERHEADER_LIST_URL;
import static com.snail.appstore.openapi.AppPlatFormConfig.USER_ACCOUNT_URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.utils.DeviceUtil;
import com.snail.appstore.openapi.AppPlatFormConfig;
import com.snail.appstore.openapi.base.vo.JSONResultVO;
import com.snail.appstore.openapi.base.vo.ResultVO;
import com.snail.appstore.openapi.exception.HttpStatusCodeException;
import com.snail.appstore.openapi.json.JSONException;
import com.snail.appstore.openapi.service.IAppPlatFormService;
import com.snail.appstore.openapi.util.HttpUtil;
import com.snail.appstore.openapi.util.UrlParameterUtil;
import com.snail.appstore.openapi.vo.AgentAppListItemVO;
import com.snail.appstore.openapi.vo.AppBannerVO;
import com.snail.appstore.openapi.vo.AppCategoryVO;
import com.snail.appstore.openapi.vo.AppCollectionVO;
import com.snail.appstore.openapi.vo.AppDetailVO;
import com.snail.appstore.openapi.vo.AppDownUrlVO;
import com.snail.appstore.openapi.vo.AppHotwordVO;
import com.snail.appstore.openapi.vo.AppListItemVO;
import com.snail.appstore.openapi.vo.AppMappingVO;
import com.snail.appstore.openapi.vo.AppUpdateVO;
import com.snail.appstore.openapi.vo.BindPhoneVO;
import com.snail.appstore.openapi.vo.FeeConfigVO;
import com.snail.appstore.openapi.vo.FlowStatusVO;
import com.snail.appstore.openapi.vo.KeyWordVO;
import com.snail.appstore.openapi.vo.MuchSlotConfigVO;
import com.snail.appstore.openapi.vo.MuchUserAppSlotVO;
import com.snail.appstore.openapi.vo.PreLoadItemVO;
import com.snail.appstore.openapi.vo.QuotaVO;
import com.snail.appstore.openapi.vo.RechargePhoneVO;
import com.snail.appstore.openapi.vo.RentReliefAppTime;
import com.snail.appstore.openapi.vo.RentReliefAppVO;
import com.snail.appstore.openapi.vo.SlotRechargeVO;
import com.snail.appstore.openapi.vo.SubscribeResultVO;
import com.snail.appstore.openapi.vo.UserBasicVO;
import com.snail.appstore.openapi.vo.UserHeaderVO;

/**
 * 平台OPEN-API服务类
 * 
 * @author gewq
 * @version 1.0 2014-6-12
 */
public class AppPlatFormService implements IAppPlatFormService {

	private final Context mContext;
	private final AccountManager mAccountManager;

	public AppPlatFormService(Context context) {
		mContext = context;
		mAccountManager = AccountManager.getInstance();
	}

	/**
	 * 获取IMEI号
	 * @return
	 */
	private String getIMEI() {
		String imei = DeviceUtil.getIMEI(mContext);
		return imei;
	}

	/**
	 * 
	 * @return
	 */
	private String getMacAddr() {
		String mac = DeviceUtil.getMacAddr(mContext);
		return mac;
	}

	/**
	 * 加入身份验证
	 * 
	 * @param map
	 */
	private void addAuthentication(HashMap<String, String> map) {
		map.put(PARAMETER_NAPPID, AppPlatFormConfig.DEFAULT_APP_ID);
		map.put(PARAMETER_NUSERID, mAccountManager.getLoginUni(mContext) == null ? "" : mAccountManager.getLoginUni(mContext));
		map.put(PARAMETER_CIDENTITY, mAccountManager.getSessionId(mContext) == null ? "" : mAccountManager.getSessionId(mContext));
	}

	/**
	 * 帐号ID与Session为空判断
	 * 
	 * @return
	 */
	private boolean isAccountIdAndSessionIdNull() {
		return isAccountIdNull() && isSessionNull();
	}

	/**
	 * 帐号Id为空判断
	 * 
	 * @return
	 */
	private boolean isAccountIdNull() {
		return TextUtils.isEmpty(mAccountManager.getLoginUni(mContext));
	}

	/**
	 * Session为空判断
	 * 
	 * @return
	 */
	private boolean isSessionNull() {
		return TextUtils.isEmpty(mAccountManager.getSessionId(mContext));
	}

	public ResultVO getGameList(String sKeyWord, Integer currentPage, int number, int iPlatformId, String cAppType, String cDynamic) throws HttpStatusCodeException,
			JSONException, Exception {
		if (null == sKeyWord) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put(PARAMETER_SKEYWORD, sKeyWord);
		parameterMap.put(PARAMETER_CURRENTPAGE, currentPage == null ? "1" : currentPage.toString());
		parameterMap.put(PARAMETER_NUMBER, String.valueOf(number));
		parameterMap.put(PARAMETER_IPLATFORMID, String.valueOf(iPlatformId));
		parameterMap.put(PARAMETER_CDYNAMIC, cDynamic);
		if(!TextUtils.isEmpty(cAppType)){
			parameterMap.put(PARAMETER_CAPPTYPE, cAppType);
		}
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(GAME_LIST_URL, parameterMap));
		return new JSONResultVO(sResultJson, AppListItemVO.class);
	}

	public ResultVO getKeywordList(String sKeyWord, int iPlatformId, String cAppType) throws HttpStatusCodeException, JSONException, Exception {
		if (null == sKeyWord) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put(PARAMETER_SKEYWORD, sKeyWord);
		parameterMap.put(PARAMETER_IPLATFORMID, String.valueOf(iPlatformId));
		if(!TextUtils.isEmpty(cAppType)){
			parameterMap.put(PARAMETER_CAPPTYPE, cAppType);
		}
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(KEYWORD_LIST_URL, parameterMap));
		return new JSONResultVO(sResultJson, KeyWordVO.class);
	}

	public ResultVO getKeywordAttr() throws HttpStatusCodeException, JSONException, Exception {
		HashMap<String, String> parameterMap = new HashMap<String, String>();

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(KEYWORD_AMOUNT_URL, parameterMap));
		return new JSONResultVO(sResultJson, AppHotwordVO.class);
	}

	public ResultVO getGameCategory() throws HttpStatusCodeException, JSONException, Exception {
		HashMap<String, String> parameterMap = new HashMap<String, String>();

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(GAME_CATEGORY_URL, parameterMap));
		return new JSONResultVO(sResultJson, AppCategoryVO.class);
	}

	public ResultVO getGameCollection(Integer currentPage) throws HttpStatusCodeException, JSONException, Exception {
		List<String> paramsList = new ArrayList<String>();
		paramsList.add(currentPage == null ? "1" : String.valueOf(currentPage));

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(GAME_COLLECTION_URL, paramsList,parameterMap));
		return new JSONResultVO(sResultJson, AppCollectionVO.class);
	}

	public ResultVO getAppListByCategory(Long nCategoryId, Integer currentPage) throws HttpStatusCodeException,
			JSONException, Exception {
		if (null == nCategoryId) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		addAuthentication(parameterMap);

		List<String> paramsList = new ArrayList<String>();
		paramsList.add(String.valueOf(nCategoryId));
		paramsList.add(currentPage == null ? "1" : String.valueOf(currentPage));
		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(GAME_BY_CATEGORY_URL, paramsList,
				parameterMap));
		return new JSONResultVO(sResultJson, AppListItemVO.class);
	}

	public ResultVO getAppListByCollection(Long nCollectionId, Integer currentPage) throws HttpStatusCodeException,
			JSONException, Exception {
		if (null == nCollectionId) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();

		addAuthentication(parameterMap);

		List<String> paramsList = new ArrayList<String>();
		paramsList.add(String.valueOf(nCollectionId));
		paramsList.add(currentPage == null ? "1" : String.valueOf(currentPage));
		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(GAME_BY_COLLECTION_URL, paramsList,
				parameterMap));
		return new JSONResultVO(sResultJson, AppListItemVO.class);
	}
	
	public ResultVO getAppDetail(Long nAppId) throws HttpStatusCodeException, JSONException, Exception {
		if (null == nAppId) {
			throw new NullPointerException("the parameter should not be null");
		}
		List<String> paramsList = new ArrayList<String>();
		paramsList.add(String.valueOf(nAppId/AppPlatFormConfig.MILLION));
		paramsList.add(String.valueOf(nAppId/AppPlatFormConfig.THOUSAND));
		paramsList.add(String.valueOf(nAppId));
		paramsList.add("detail");
		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetDetailUrl(GAME_DETAIL_URL, paramsList));
		return new JSONResultVO(sResultJson, AppDetailVO.class);
	}

	public ResultVO getDownLoadUrl(Long nAppId) throws HttpStatusCodeException, JSONException, Exception {
		if (null == nAppId) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put(PARAMETER_ID, String.valueOf(nAppId));

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(GAME_DOWNLOAD_URL, parameterMap));
		return new JSONResultVO(sResultJson, AppDownUrlVO.class);
	}

	public ResultVO getAccountInfo() throws HttpStatusCodeException, JSONException, Exception {
		if (isAccountIdAndSessionIdNull()) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(USER_ACCOUNT_URL, parameterMap));
		return new JSONResultVO(sResultJson, UserBasicVO.class);
	}

	public ResultVO getBannerList(Integer currentPage) throws HttpStatusCodeException, JSONException, Exception {
		List<String> paramsList = new ArrayList<String>();
		paramsList.add(currentPage == null ? "1" : String.valueOf(currentPage));

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(GAME_BANNER_LIST_URL, paramsList,parameterMap));
		return new JSONResultVO(sResultJson, AppBannerVO.class);
	}

	public ResultVO getAppUpdateList(String cPackages, String iVersioncodes, String iPlatFormid,String cAppType)
			throws HttpStatusCodeException, JSONException, Exception {
		if (null == cPackages || null == iVersioncodes) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put(PARAMETER_CPACKAGES, cPackages);
		parameterMap.put(PARAMETER_IVERSIONCODE, iVersioncodes);
		parameterMap.put(PARAMETER_IPLATFORMID, iPlatFormid);
		parameterMap.put(PARAMETER_CAPPTYPE, cAppType);
		
		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateUrl(GAME_UPDATE_LIST_URL, parameterMap),
				parameterMap);
		return new JSONResultVO(sResultJson, AppUpdateVO.class);
	}

	public ResultVO getAppMappingList(String cPackages, String iVersioncodes, String iPlatFormid,String cAppType)
			throws HttpStatusCodeException, JSONException, Exception {
		if (null == cPackages || null == iVersioncodes) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put(PARAMETER_CPACKAGES, cPackages);
		parameterMap.put(PARAMETER_IVERSIONCODE, iVersioncodes);
		parameterMap.put(PARAMETER_IPLATFORMID, iPlatFormid);
		parameterMap.put(PARAMETER_CAPPTYPE, cAppType);

		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateUrl(GAME_UPDATE_LIST_URL, parameterMap),
				parameterMap);
		return new JSONResultVO(sResultJson, AppMappingVO.class);
	}

	public ResultVO getUserHeaderList() throws HttpStatusCodeException, JSONException, Exception {
		HashMap<String, String> parameterMap = new HashMap<String, String>();

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(USERHEADER_LIST_URL, parameterMap));
		return new JSONResultVO(sResultJson, UserHeaderVO.class);
	}

	public ResultVO getUserNickName(String uId) throws HttpStatusCodeException, JSONException, Exception {
		if (isAccountIdAndSessionIdNull()) {
			throw new NullPointerException("the parameter should not be null");
		}

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("nUId", uId);

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(ACCOUT_NICKNAME_URL, parameterMap));
		return new JSONResultVO(sResultJson, null);
	}

	public ResultVO saveUserInfo(String nickName, String cSex, String cPhoto, String cPhone, String birthday)
			throws HttpStatusCodeException, Exception {
		if (isAccountIdAndSessionIdNull()) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("sNickName", nickName);
		parameterMap.put("cSex", cSex);
		parameterMap.put("cPhoto", cPhoto);
		parameterMap.put("cPhone", cPhone);
		parameterMap.put("sBirthday", birthday);

		addAuthentication(parameterMap);
		String sResultJson = HttpUtil
				.doPost(UrlParameterUtil.generateUrl(ACCOUNT_SAVE_URL, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	public ResultVO getMuchSlotConfigList() throws HttpStatusCodeException, Exception {
		HashMap<String, String> parameterMap = new HashMap<String, String>();

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(MUCH_CONFIG_URL, parameterMap));
		return new JSONResultVO(sResultJson, MuchSlotConfigVO.class);
	}

	public ResultVO getAppUserMuchSlotList() throws HttpStatusCodeException, Exception {
		if (isAccountIdNull()) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put("cImei", getIMEI());
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(MUCH_USE_SLOT_URL, parameterMap));
		return new JSONResultVO(sResultJson, MuchUserAppSlotVO.class);
	}

	public ResultVO purchaseMuchSlot(Integer ISlotConfigId) throws HttpStatusCodeException, Exception {
		if (isAccountIdAndSessionIdNull() || null == ISlotConfigId) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("iSlotConfigId", String.valueOf(ISlotConfigId));

		addAuthentication(parameterMap);
		parameterMap.put("cImei", getIMEI());
		parameterMap.put("cMac", getMacAddr());

		String sResultJson = HttpUtil.doPost(
				UrlParameterUtil.generateGetUrl(MUCH_USER_PURCHASE_SLOT_URL, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	public ResultVO bindAppMuchSlot(String CPackage) throws HttpStatusCodeException, Exception {
		if (isAccountIdAndSessionIdNull() || null == CPackage) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cPackage", CPackage);
		parameterMap.put("cImei", getIMEI());
		parameterMap.put("cMac", getMacAddr());

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(MUCH_SLOT_BIND_URL, parameterMap),
				parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	public ResultVO unBindAppMuchSlot(String CPackage) throws HttpStatusCodeException, Exception {
		if (isAccountIdAndSessionIdNull() || null == CPackage) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cPackage", CPackage);
		parameterMap.put("cImei", getIMEI());

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(MUCH_SLOT_UNBIND_URL, parameterMap),
				parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	public ResultVO donateUserSlot() throws HttpStatusCodeException, Exception {
		if (isAccountIdNull()) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put("cImei", getIMEI());
		parameterMap.put("cMac", getMacAddr());

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(MUCH_SLOT_DONATE_URL, parameterMap),
				parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO rechargeSlotTicket(String ticketNum) throws HttpStatusCodeException,
			Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(ticketNum)) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cImei", getIMEI());
		parameterMap.put("cMac", getMacAddr());
		parameterMap.put("cTicketCode", ticketNum);

		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.MUCH_SLOT_RECHARGE_URL, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, SlotRechargeVO.class);
	}

	@Override
	public ResultVO rechargeRabbitTicket(String ticketNum, String ticketPassword) throws HttpStatusCodeException,
			Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(ticketNum)) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cImei", getIMEI());
		parameterMap.put("cMac", getMacAddr());
		parameterMap.put("cTicketCode", ticketNum);

		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(MUCH_TICKET_RECHARGE, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO bindTelToRabbit(String phone) throws HttpStatusCodeException, Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(phone)) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cImei", getIMEI());

		parameterMap.put("cPhoneNum", phone);
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(MUCH_TICKET_PHONE_BIND, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO calculateQuota(Integer rabbitMoney, int moneyForm) throws HttpStatusCodeException, Exception {
		if(isAccountIdAndSessionIdNull() || rabbitMoney == null) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cImei", getIMEI());
		parameterMap.put("iRabbitMoney", String.valueOf(rabbitMoney));
		if(moneyForm > -1 && moneyForm < 3) {
			parameterMap.put("cMoneyForm", String.valueOf(moneyForm));
		} else {
			parameterMap.put("cMoneyForm", "0");
		}

		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(MUCH_TICKET_QUOTA, parameterMap));
		return new JSONResultVO(sResultJson, QuotaVO.class);
	}

	@Override
	public ResultVO getBindPhoneNum() throws HttpStatusCodeException, Exception {
		if(isAccountIdAndSessionIdNull()) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cImei", getIMEI());

		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(MUCH_TICKET_GET_BIND_PHONE, parameterMap));
		return new JSONResultVO(sResultJson, BindPhoneVO.class);
	}

	@Override
	public ResultVO getFeeConfig() throws HttpStatusCodeException, Exception {
		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(MUCH_TICKET_FEE_CONFIG, new HashMap<String, String>()));
		return new JSONResultVO(sResultJson, FeeConfigVO.class);
	}

	@Override
	public ResultVO rechargePhone(Long mobileFreeId, String phone, Integer feeNum) throws HttpStatusCodeException,
			Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(phone) || mobileFreeId == null || feeNum == null) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("nMobileFeeId", String.valueOf(mobileFreeId));
		parameterMap.put("cPhoneNum", phone);
		if(feeNum <= 0) {
			parameterMap.put("iFeeNum", "1");
		} else {
			parameterMap.put("iFeeNum", String.valueOf(feeNum));
		}

		parameterMap.put("cImei", getIMEI());
		parameterMap.put("cMac", getMacAddr());

		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(MUCH_TICKET_PHONE_RECHARGE, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, RechargePhoneVO.class);
	}

	@Override
	public ResultVO checkBSS(String phone) throws HttpStatusCodeException, Exception {
		if(TextUtils.isEmpty(phone)) {
			throw new NullPointerException("the parameter should not be null");
		}

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cPhoneNum", phone);

		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(MUCH_BSS_ACCOUNT_CHECK, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO uploadGetuiInfo(String clientId, String appId, String channelId, String phoneType)
			throws HttpStatusCodeException, Exception {
		if(TextUtils.isEmpty(clientId)
				|| TextUtils.isEmpty(appId)
				|| TextUtils.isEmpty(channelId)
				|| TextUtils.isEmpty(phoneType)) {
			throw new NullPointerException("the parameter should not be null");
		}

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cCID", clientId);
		parameterMap.put("cAppID", appId);
		parameterMap.put("cChannelID", channelId);
		parameterMap.put("cPhoneType", phoneType);
		parameterMap.put("cIMEI", getIMEI());
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.MUCH_UPLOAD_GETUI_INFO, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO getSmsCode(String phoneNum) throws HttpStatusCodeException, Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(phoneNum)) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cPhoneNum", phoneNum);
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.MUCH_USER_SMSCODE, parameterMap));
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO bindFreeFlowPhoneNum(String phoneNum, String smsCode) throws HttpStatusCodeException, Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(smsCode)) {
			throw new NullPointerException("the parameter should not be null");
		}

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cPhoneNum", phoneNum);
		parameterMap.put("cSmsCode", smsCode);
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.MUCH_USER_PHONENUM_BIND, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO unbindFreeFlowPhoneNum(String phoneNum) throws HttpStatusCodeException, Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(phoneNum)) {
			throw new NullPointerException("the parameter should not be null");
		}

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cPhoneNum", phoneNum);
		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.MUCH_USER_PHONENUM_UNBIND, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO getFreeFlowStatus(String phoneNum) throws HttpStatusCodeException, Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(phoneNum)) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cPhoneNum", phoneNum);
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.MUCH_USER_FLOW_STATUS, parameterMap));
		return new JSONResultVO(sResultJson, FlowStatusVO.class);
	}

	@Override
	public ResultVO enableFreeFlow(String phoneNum, String productCode, String month) throws HttpStatusCodeException,
			Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(phoneNum)) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("cPhoneNum", phoneNum);
		if (!TextUtils.isEmpty(productCode)) {
			parameterMap.put("cProductCode", productCode);
		}
		if (!TextUtils.isEmpty(month)) {
			parameterMap.put("cMonth", month);
		}
		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doPost(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.MUCH_USER_FLOW_SUBSCRIBE, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, SubscribeResultVO.class);
	}

	@Override
	public ResultVO getAgentApps(String appIds) throws HttpStatusCodeException, Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(appIds)) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("appIds", appIds);
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.MUCH_USER_AGENT_APP, parameterMap));
		return new JSONResultVO(sResultJson, AgentAppListItemVO.class);
	}

	@Override
	public ResultVO getAgentAppDownloadUrl(String appId) throws HttpStatusCodeException, Exception {
		if(isAccountIdAndSessionIdNull() || TextUtils.isEmpty(appId)) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("appId", appId);
		addAuthentication(parameterMap);

		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.MUCH_USER_AGENT_DOWNURL, parameterMap));
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO getPreLoadList() throws HttpStatusCodeException,
			JSONException, Exception {
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.MUCH_PRELOAD_LIST, parameterMap));
		return new JSONResultVO(sResultJson, PreLoadItemVO.class);
	}

	@Override
	public ResultVO activateBox(String activateCode) throws HttpStatusCodeException, Exception {
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		addAuthentication(parameterMap);
		parameterMap.put("cActiveCode", activateCode);
		String sResultJson = HttpUtil.doPost(
				UrlParameterUtil.generateUrl(AppPlatFormConfig.ACTIVATE_BOX_URL, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO getRentReliefAppList() throws HttpStatusCodeException, Exception {
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.RENT_RELIEF_APP_LIST_URL, parameterMap));
		return new JSONResultVO(sResultJson, RentReliefAppVO.class);
	}

	@Override
	public ResultVO getRentReliefAppTime() throws HttpStatusCodeException, Exception {
		if(isAccountIdAndSessionIdNull()) {
			throw new NullPointerException("the parameter should not be null");
		}
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doGet(UrlParameterUtil.generateGetUrl(AppPlatFormConfig.RENT_RELIEF_APP_TIME_URL,
				parameterMap));
		return new JSONResultVO(sResultJson, RentReliefAppTime.class);
	}

	@Override
	public ResultVO saveAppTime(String cPackage, long nAppTime,String cReqId,String sign) throws HttpStatusCodeException,Exception {
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		addAuthentication(parameterMap);
		parameterMap.put("cPackage", cPackage);
		parameterMap.put("nAppTime", String.valueOf(nAppTime));
		parameterMap.put("cReqId", cReqId);
		parameterMap.put("cSign", sign);
		
		String sResultJson = HttpUtil.doPost(
				UrlParameterUtil.generateUrl(AppPlatFormConfig.SAVE_APP_TIME_URL, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO renewalBox() throws HttpStatusCodeException, Exception {
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doPost(
				UrlParameterUtil.generateUrl(AppPlatFormConfig.RENEWAL_BOX, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}

	@Override
	public ResultVO appPayment(String nAppId, String cAppOrder, String cAppAccuntId, String cGoodId, String sGoodName,
			Integer iGoodNum, Integer nMoney) throws HttpStatusCodeException, Exception {
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("nAppId", String.valueOf(nAppId));
		parameterMap.put("cAppOrder", cAppOrder);
		parameterMap.put("cAppAccuntId", cAppAccuntId);
		parameterMap.put("cGoodId", cGoodId);
		parameterMap.put("sGoodName", sGoodName);
		parameterMap.put("iGoodNum", String.valueOf(iGoodNum));
		parameterMap.put("nMoney", String.valueOf(nMoney));

		addAuthentication(parameterMap);
		String sResultJson = HttpUtil.doPost(
				UrlParameterUtil.generateUrl(AppPlatFormConfig.APP_PAYMENT_URL, parameterMap), parameterMap);
		return new JSONResultVO(sResultJson, null);
	}
}
