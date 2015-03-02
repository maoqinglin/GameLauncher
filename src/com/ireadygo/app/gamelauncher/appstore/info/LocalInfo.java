package com.ireadygo.app.gamelauncher.appstore.info;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.aidl.rentfree.AppTimeUploadResultItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.AgentAppItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.BannerItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.BindPhoneItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.CollectionInfo;
import com.ireadygo.app.gamelauncher.appstore.info.item.FeeConfigItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.FreeFlowStatusItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.KeywordItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.QuotaItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.RechargePhoneItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.RentReliefItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.SlotConfigItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.SubscribeResultItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserHeaderImgItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserSlotInfoItem;

public class LocalInfo implements IGameInfo {

	private static final String CACHED_KEY_WORDS_NAME = "CACHED_KEY_WORDS";
	private static final String CACHED_CATEGORYS_NAME = "CACHED_CATEGORYS";
	private static final String CACHED_COLLECTIONS_NAME = "CACHED_COLLECTIONS";
	private static final String CACHED_BANNERLIST_NAME = "CACHED_BANNERLIST";
	private static final String CACHED_FEECONFIGLIST_NAME = "CACHED_FEECONFIGLIST";
	private static final String CACHED_KEY_WORDS = "CACHED_KEY_WORDS";
	private static final String CACHED_CATEGORYS = "CACHED_CATEGORYS";
	private static final String CACHED_COLLECTIONS = "CACHED_COLLECTIONS";
	private static final String CACHED_BANNERLIST = "CACHED_BANNERLIST";
	private static final String CACHED_FEECONFIGLIST = "CACHED_BANNERLIST";
	private static final int MAX_CACHED_ITEMS = 30;
	private static final String ITEM_DIVIDER = ",";
	private static final String ITEM_INNER_DIVIDER = "@";

	private Context mContext;

	public LocalInfo(Context context) {
		mContext = context;
	}

	@Override
	public int obtainChildrenCount(String parentItemId) throws InfoSourceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<AppEntity> obtainChildren(int dataType, String id, int page) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AppEntity obtainItemById(String appId) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CategoryItem> obtainCategorys() throws InfoSourceException {
		List<CategoryItem> categoryItems = getCachedCategorysFrmFile();
		if (categoryItems.size() == 0) {
			throw new InfoSourceException(InfoSourceException.MSG_NO_CACHED_DATA);
		}
		return categoryItems;
	}

	@Override
	public List<CollectionInfo> obtainCollection(int page) throws InfoSourceException {
		List<CollectionInfo> collectionItems = getCachedCollectionsFrmFile();
		if (collectionItems.size() == 0) {
			throw new InfoSourceException(InfoSourceException.MSG_NO_CACHED_DATA);
		}
		return collectionItems;
	}

	@Override
	public List<KeywordItem> obtainKeywords() throws InfoSourceException {
		List<KeywordItem> keywords = getCachedKeywordsFrmFile();
		if (keywords.size() == 0) {
			throw new InfoSourceException(InfoSourceException.MSG_NO_CACHED_DATA);
		}
		return keywords;
	}

	@Override
	public List<String> obtainKeywordsByWord(String word, int iPlatformId, String cAppType) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String obtainDownloadUrl(long id) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AppEntity> searchByKeyword(String word, int page, int number, int iPlatformId, String cAppType, String cDynamic) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BannerItem> obtainBannerList(int page) throws InfoSourceException {
		List<BannerItem> bannerItemList = getCachedBannerListFrmFile();
		if (bannerItemList.size() == 0) {
			throw new InfoSourceException(InfoSourceException.MSG_NO_CACHED_DATA);
		}
		return bannerItemList;
	}

	@Override
	public List<AppEntity> obtainUpdatableApp(List<AppEntity> appList) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> mapAppWithFreeStore(List<AppEntity> appList) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserInfoItem getUserInfo() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserHeaderImgItem> getUserHeaderImgItems() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveUserInfo(String nickName, String cSex, String cPhoto, String cPhone, String birthday) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void purchaseMuchSlot(int slotConfigId) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<SlotConfigItem> getUserSlotConfigItems() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bindAppToSlot(String pkgName) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unbindAppToSlot(String pkgName) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<UserSlotInfoItem> getUserSlotInfoItems() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserNickName(String uId) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initSlotWithAccount() throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getUserSlotNum() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void cachedKeywordsToFile(List<KeywordItem> keywordList) {
		if (keywordList == null || keywordList.size() == 0) {
			return;
		}
		Editor editor = mContext.getSharedPreferences(CACHED_KEY_WORDS_NAME,Context.MODE_PRIVATE).edit();
		JSONArray jsonArray = new JSONArray();
		for (KeywordItem item : keywordList) {
			JSONObject jsonObject = KeywordItemToJsonObject(item);
			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}
		editor.putString(CACHED_KEY_WORDS, jsonArray.toString());
		editor.commit();
	}

	private List<KeywordItem> getCachedKeywordsFrmFile() {
		String keywords = mContext.getSharedPreferences(CACHED_KEY_WORDS_NAME,Context.MODE_PRIVATE).getString(CACHED_KEY_WORDS, "");
		if (TextUtils.isEmpty(keywords)) {
			return new ArrayList<KeywordItem>();
		}
		try {
			JSONArray keywordJsonArray = new JSONArray(keywords);
			ArrayList<KeywordItem> result = new ArrayList<KeywordItem>();
			for (int i = 0; i < keywordJsonArray.length(); i++) {
				KeywordItem item = JsonObjectToKeywordItem(keywordJsonArray.getJSONObject(i));
				result.add(item);
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<KeywordItem>();
		}
	}

	public void cachedCategorysToFile(List<CategoryItem> categorys) {
		if (categorys == null || categorys.size() == 0) {
			return;
		}
		Editor editor = mContext.getSharedPreferences(CACHED_CATEGORYS_NAME,Context.MODE_PRIVATE).edit();
		StringBuffer sb = new StringBuffer();
		int length = Math.min(categorys.size(), MAX_CACHED_ITEMS);
		for (int i = 0; i < length; i++) {
			CategoryItem item = categorys.get(i);
			sb.append(item.getCategoryId())
			.append(ITEM_INNER_DIVIDER)
			.append(item.getCatetoryName())
			.append(ITEM_INNER_DIVIDER)
			.append(item.getCategoryDes())
			.append(ITEM_INNER_DIVIDER)
			.append(item.getIconUrl())
			.append(ITEM_INNER_DIVIDER)
			.append(item.getPosterIconUrl())
			.append(ITEM_INNER_DIVIDER)
			.append(item.getPosterBgUrl())
			.append(ITEM_DIVIDER);
		}
		editor.putString(CACHED_CATEGORYS, sb.toString());
		editor.commit();
	}

	private List<CategoryItem> getCachedCategorysFrmFile() {
		String categorys = mContext.getSharedPreferences(CACHED_CATEGORYS_NAME,Context.MODE_PRIVATE).getString(CACHED_CATEGORYS, "");
		if (TextUtils.isEmpty(categorys)) {
			return new ArrayList<CategoryItem>();
		}
		String[] categotyList = categorys.split(ITEM_DIVIDER);
		ArrayList<CategoryItem> result = new ArrayList<CategoryItem>();
		for (String category : categotyList) {
			String[] categoryItems = category.split(ITEM_INNER_DIVIDER);
			if (categoryItems.length == 4) {
				CategoryItem item = new CategoryItem(Integer.valueOf((categoryItems[0])),
						categoryItems[1],
						categoryItems[2],
						categoryItems[3],
						categoryItems[4],
						categoryItems[5]);
				result.add(item);
			}
		}
		return result;
	}

	public void cachedCollectionToFile(List<CollectionInfo> collections) {
		if (collections == null || collections.size() == 0) {
			return;
		}
		Editor editor = mContext.getSharedPreferences(CACHED_COLLECTIONS_NAME,Context.MODE_PRIVATE).edit();
		StringBuffer sb = new StringBuffer();
		int length = Math.min(collections.size(), MAX_CACHED_ITEMS);
		for (int i = 0; i < length; i++) {
			CollectionInfo item = collections.get(i);
			sb.append(item.getCollectionId())
			.append(ITEM_INNER_DIVIDER)
			.append(item.getCollectionName())
			.append(ITEM_INNER_DIVIDER)
			.append(item.getCollectionDes())
			.append(ITEM_INNER_DIVIDER)
			.append(item.getIconUrl())
			.append(ITEM_INNER_DIVIDER)
			.append(item.getPosterIconUrl())
			.append(ITEM_INNER_DIVIDER)
			.append(item.getPosterBgUrl())
			.append(ITEM_DIVIDER);
		}
		editor.putString(CACHED_COLLECTIONS, sb.toString());
		editor.commit();
	}

	private List<CollectionInfo> getCachedCollectionsFrmFile() {
		String collections = mContext.getSharedPreferences(CACHED_COLLECTIONS_NAME,Context.MODE_PRIVATE).getString(CACHED_COLLECTIONS, "");
		if (TextUtils.isEmpty(collections)) {
			return new ArrayList<CollectionInfo>();
		}
		String[] collectionList = collections.split(ITEM_DIVIDER);
		ArrayList<CollectionInfo> result = new ArrayList<CollectionInfo>();
		for (String collection : collectionList) {
			String[] collectionItems = collection.split(ITEM_INNER_DIVIDER);
			if (collectionItems.length == 6) {
				CollectionInfo item = new CollectionInfo(Integer.valueOf(collectionItems[0]),
						collectionItems[1],
						collectionItems[2],
						collectionItems[3],
						collectionItems[4],
						collectionItems[5]);
				result.add(item);
			}
		}
		return result;
	}

	public void cachedBannerListToFile(List<BannerItem> bannerItems) {
		if (bannerItems == null || bannerItems.size() == 0) {
			return;
		}
		Editor editor = mContext.getSharedPreferences(CACHED_BANNERLIST_NAME,Context.MODE_PRIVATE).edit();
		JSONArray jsonArray = new JSONArray();
		for (BannerItem item : bannerItems) {
			JSONObject jsonObject = bannerItemToJsonObject(item);
			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}
		editor.putString(CACHED_BANNERLIST, jsonArray.toString());
		editor.commit();
	}

	private void clearBannerCached() {
		Editor editor = mContext.getSharedPreferences(CACHED_BANNERLIST_NAME,Context.MODE_PRIVATE).edit();
		editor.putString(CACHED_BANNERLIST, "");
		editor.commit();
	}

	private List<BannerItem> getCachedBannerListFrmFile() {
		String bannerList = mContext.getSharedPreferences(CACHED_BANNERLIST_NAME,Context.MODE_PRIVATE)
				.getString(CACHED_BANNERLIST, "");
		if (TextUtils.isEmpty(bannerList)) {
			return new ArrayList<BannerItem>();
		}
		JSONArray bannerJsonArray;
		try {
			bannerJsonArray = new JSONArray(bannerList);
			ArrayList<BannerItem> result = new ArrayList<BannerItem>();
			for (int i = 0; i < bannerJsonArray.length(); i++) {
				BannerItem item = jsonObjectToBannerItem(bannerJsonArray.getJSONObject(i));
				result.add(item);
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<BannerItem>();
		}
	}

	public void cachedFeeConfigListToFile(List<FeeConfigItem> feeConfigItems) {
		if (feeConfigItems == null || feeConfigItems.isEmpty()) {
			return;
		}
		Editor editor = mContext.getSharedPreferences(CACHED_FEECONFIGLIST_NAME,Context.MODE_PRIVATE).edit();
		JSONArray jsonArray = new JSONArray();
		for (FeeConfigItem item : feeConfigItems) {
			JSONObject jsonObject = feeConfigItemToJsonObject(item);
			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}
		editor.putString(CACHED_FEECONFIGLIST, jsonArray.toString());
		editor.commit();
	}

	private List<FeeConfigItem> getCachedFeeConfigItemsFrmFile() {
		String feeConfigItemList = mContext.getSharedPreferences(CACHED_FEECONFIGLIST_NAME,Context.MODE_PRIVATE).getString(CACHED_FEECONFIGLIST, "");
		if (TextUtils.isEmpty(feeConfigItemList)) {
			return new ArrayList<FeeConfigItem>();
		}
		String[] feeConfigList = feeConfigItemList.split(ITEM_DIVIDER);
		ArrayList<FeeConfigItem> result = new ArrayList<FeeConfigItem>();
		for (String feeConfigItem : feeConfigList) {
			String[] feeConfigItems = feeConfigItem.split(ITEM_INNER_DIVIDER);
			if (feeConfigItems.length == 6) {
				FeeConfigItem configItem = new FeeConfigItem();
				configItem.setNMobileFeeId(Long.valueOf(feeConfigItems[0]));
				configItem.setSMobileFeeName(feeConfigItems[1]);
				configItem.setCMobileFeeType(feeConfigItems[2]);
				configItem.setIMobileFeeMoney(Integer.valueOf(feeConfigItems[3]));
				configItem.setILimit(Integer.valueOf(feeConfigItems[4]));
				configItem.setSInfo(feeConfigItems[5]);
				result.add(configItem);
			}
		}
		return result;
	}

	@Override
	public int rechargeRabbitTicket(String num, String password) throws InfoSourceException {
		return 0;
	}

	@Override
	public void bindTelToRabbit(String phone) throws InfoSourceException {
	}

	@Override
	public QuotaItem calculateQuota(int money, int type) throws InfoSourceException {
		return null;
	}

	@Override
	public BindPhoneItem getBindPhoneNum() throws InfoSourceException {
		return null;
	}

	@Override
	public List<FeeConfigItem> getFeeConfig() throws InfoSourceException {
		List<FeeConfigItem> feeConfigItems = getCachedFeeConfigItemsFrmFile();
		if(feeConfigItems.isEmpty()) {
			throw new InfoSourceException(InfoSourceException.MSG_NO_CACHED_DATA);
		}
		return feeConfigItems;
	}

	@Override
	public RechargePhoneItem rechargePhone(long id, String phoneNum, int feeNum) throws InfoSourceException {
		return null;
	}

	@Override
	public boolean checkBSSAccount(String phone) {
		// TODO Auto-generated method stub
		return false;
	}

	public JSONObject bannerItemToJsonObject(BannerItem bannerItem) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(BANNER_JSON_KEY.SInfo, bannerItem.getSInfo());
			jsonObject.put(BANNER_JSON_KEY.CType, bannerItem.getCType());
			jsonObject.put(BANNER_JSON_KEY.CPostion, bannerItem.getCPostion());
			jsonObject.put(BANNER_JSON_KEY.NParamId, bannerItem.getNParamId());
			jsonObject.put(BANNER_JSON_KEY.CPicUrl, bannerItem.getCPicUrl());
			jsonObject.put(BANNER_JSON_KEY.CHtmlUrl, bannerItem.getCHtmlUrl());
			jsonObject.put(BANNER_JSON_KEY.IRefId, bannerItem.getIRefId());
			jsonObject.put(BANNER_JSON_KEY.SAppName, bannerItem.getSAppName());
			jsonObject.put(BANNER_JSON_KEY.CIcon, bannerItem.getCIcon());
			jsonObject.put(BANNER_JSON_KEY.CVersionName, bannerItem.getCVersionName());
			jsonObject.put(BANNER_JSON_KEY.IVersionCode, bannerItem.getIVersionCode());
			jsonObject.put(BANNER_JSON_KEY.CPackage, bannerItem.getCPackage());
			jsonObject.put(BANNER_JSON_KEY.IFlowFree, bannerItem.getIFlowFree());
			jsonObject.put(BANNER_JSON_KEY.CMd5, bannerItem.getCMd5());
			jsonObject.put(BANNER_JSON_KEY.CMark, bannerItem.getCMark());
			jsonObject.put(BANNER_JSON_KEY.ISize, bannerItem.getISize());
			
			jsonObject.put(BANNER_JSON_KEY.NAppId, bannerItem.getNAppId());
			jsonObject.put(BANNER_JSON_KEY.ICategoryId, bannerItem.getICategoryId());
			jsonObject.put(BANNER_JSON_KEY.IBannerId, bannerItem.getIBannerId());
			jsonObject.put(BANNER_JSON_KEY.CDownloadUrl, bannerItem.getCDownloadUrl());
			jsonObject.put(BANNER_JSON_KEY.SPayDesc, bannerItem.getSPayDesc());
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BannerItem jsonObjectToBannerItem(JSONObject object) {
		BannerItem bannerItem = new BannerItem();
		if (object == null) {
			return bannerItem;
		}
		bannerItem.setSInfo(object.optString(BANNER_JSON_KEY.SInfo));
		bannerItem.setCType(object.optString(BANNER_JSON_KEY.CType));
		bannerItem.setCPostion(object.optString(BANNER_JSON_KEY.CPostion));
		bannerItem.setNParamId(object.optLong(BANNER_JSON_KEY.NParamId));
		bannerItem.setCPicUrl(object.optString(BANNER_JSON_KEY.CPicUrl));
		bannerItem.setCHtmlUrl(object.optString(BANNER_JSON_KEY.CHtmlUrl));
		bannerItem.setIRefId(object.optInt(BANNER_JSON_KEY.IRefId));
		bannerItem.setSAppName(object.optString(BANNER_JSON_KEY.SAppName));
		bannerItem.setCIcon(object.optString(BANNER_JSON_KEY.CIcon));
		bannerItem.setIVersionCode(object.optLong(BANNER_JSON_KEY.IVersionCode));
		bannerItem.setCPackage(object.optString(BANNER_JSON_KEY.CPackage));
		bannerItem.setIFlowFree(object.optInt(BANNER_JSON_KEY.IFlowFree));
		bannerItem.setCMd5(object.optString(BANNER_JSON_KEY.CMd5));
		bannerItem.setCMark(object.optString(BANNER_JSON_KEY.CMark));
		bannerItem.setISize(object.optInt(BANNER_JSON_KEY.ISize));
		
		bannerItem.setNAppId(object.optLong(BANNER_JSON_KEY.NAppId));
		bannerItem.setICategoryId(object.optInt(BANNER_JSON_KEY.ICategoryId));
		bannerItem.setIBannerId(object.optInt(BANNER_JSON_KEY.IBannerId));
		bannerItem.setCDownloadUrl(object.optString(BANNER_JSON_KEY.CDownloadUrl));
		bannerItem.setSPayDesc(object.optString(BANNER_JSON_KEY.SPayDesc));
		return bannerItem;
	}

	public FeeConfigItem jsonObjectToFeeConfigItem(JSONObject object) {
		FeeConfigItem feeConfigItem = new FeeConfigItem();
		if(object == null) {
			return feeConfigItem;
		}

		feeConfigItem.setNMobileFeeId(object.optLong(FEECONFIG_JSON_KEY.NMobileFeeId));
		feeConfigItem.setSMobileFeeName(object.optString(FEECONFIG_JSON_KEY.SMobileFeeName));
		feeConfigItem.setCMobileFeeType(object.optString(FEECONFIG_JSON_KEY.CMobileFeeType));
		feeConfigItem.setIMobileFeeMoney(object.optInt(FEECONFIG_JSON_KEY.IMobileFeeMoney));
		feeConfigItem.setILimit(object.optInt(FEECONFIG_JSON_KEY.ILimit));
		feeConfigItem.setSInfo(object.optString(FEECONFIG_JSON_KEY.SInfo));
		return feeConfigItem;
	}

	public JSONObject feeConfigItemToJsonObject(FeeConfigItem feeConfigItem) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(FEECONFIG_JSON_KEY.NMobileFeeId, feeConfigItem.getNMobileFeeId());
			jsonObject.put(FEECONFIG_JSON_KEY.SMobileFeeName, feeConfigItem.getSMobileFeeName());
			jsonObject.put(FEECONFIG_JSON_KEY.CMobileFeeType, feeConfigItem.getCMobileFeeType());
			jsonObject.put(FEECONFIG_JSON_KEY.IMobileFeeMoney, feeConfigItem.getIMobileFeeMoney());
			jsonObject.put(FEECONFIG_JSON_KEY.ILimit, feeConfigItem.getILimit());
			jsonObject.put(FEECONFIG_JSON_KEY.SInfo, feeConfigItem.getSInfo());
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject KeywordItemToJsonObject(KeywordItem keywordItem) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(KEYWORD_JSON_KEY.INum,keywordItem.getINum());
			jsonObject.put(KEYWORD_JSON_KEY.NAppId,keywordItem.getNAppId());
			jsonObject.put(KEYWORD_JSON_KEY.SKeyWord,keywordItem.getSKeyWord());
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public KeywordItem JsonObjectToKeywordItem(JSONObject object) {
		KeywordItem keywordItem = new KeywordItem();
		if(object == null) {
			return keywordItem;
		}

		keywordItem.setINum(object.optInt(KEYWORD_JSON_KEY.INum));
		keywordItem.setNAppId(object.optInt(KEYWORD_JSON_KEY.NAppId));
		keywordItem.setSKeyWord(object.optString(KEYWORD_JSON_KEY.SKeyWord));
		return keywordItem;
	}

	private interface FEECONFIG_JSON_KEY {
		String NMobileFeeId = "NMobileFeeId";
		String SMobileFeeName = "SMobileFeeName";
		String CMobileFeeType = "CMobileFeeType";
		String IMobileFeeMoney = "IMobileFeeMoney";
		String ILimit = "ILimit";
		String SInfo = "SInfo";
	}

	private interface BANNER_JSON_KEY {
		String SInfo = "SInfo"; // 简述
		String CType = "CType"; // 类型: 1,游戏; 2,合集; 3,小编
		String CPostion = "CPostion"; // 广告栏位置: 1, 上;2, 中;3, 下
		String NParamId = "NParamId"; // 参数ID: 当type为1时,此为游戏ID; 当type为2时,此为集合ID; 为3时,为游戏ID
		String CPicUrl = "CPicUrl"; // 宣传图片地址
		String CHtmlUrl = "CHtmlUrl"; // 静态页面url
		String IRefId = "IRefId"; // 关联广告栏ID

		String SAppName = "SAppName"; // 游戏名称
		String CIcon = "CIcon"; // 游戏图标
		String CVersionName = "CVersionName"; // 当前版本名称
		String IVersionCode = "IVersionCode"; // 当前版本号
		String CPackage = "CPackage"; // 游戏包名
		String IFlowFree = "IFlowFree"; // 0 下载流量免费 1玩游戏流量免费 2 下载流量免费,玩游戏流量免费 10 其他
		String CMd5 = "CMd5"; // 文件MD5
		String CMark = "CMark";//专题标识: 0:不显示	1:首发 2:合集 3:热门  4:推荐 5:独家  6:精品
		String ISize = "ISize";//文件大小
		String NAppId = "NAppId";
		String IBannerId = "IBannerId";
		String CDownloadUrl = "CDownloadUrl";
		String ICategoryId = "ICategoryId";
		String SPayDesc = "SPayDesc";
	}

	private interface KEYWORD_JSON_KEY {
		String SKeyWord = "SKeyWord";// 热词
		String INum = "INum";// 次数
		String CPosterIcon = "CPosterIcon";//海报图标地址
		String NAppId = "NAppId";//热词对应应用的ID
	}

	@Override
	public int[] rechargeSlotTicket(String ticketNum) throws InfoSourceException {
		return null;
	}

	@Override
	public AppEntity obtainItemByIdFrmRemote(String appId) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] uploadGeituiInfo(String clienId, String appId, String channelId, String phoneType)
			throws InfoSourceException {
		return null;
	}

	@Override
	public void getSmsCode(String phoneNum) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bindFreeFlowPhoneNum(String phoneNum, String smsCode) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unbindFreeFlowPhoneNum(String phoneNum) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FreeFlowStatusItem getFreeFlowStatus(String phoneNum) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubscribeResultItem subScribeFreeFlow(String phoneNum) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AgentAppItem> getAgentAppItems(String appIds) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAgentDownloadUrl(String appId) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cleanCached() {
		clearBannerCached();
	}

	@Override
	public List<AppEntity> getPreLoadList() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void activateBox(String activateCode) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getRentReliefAppList() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RentReliefItem getRentReliefAppTime() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AppTimeUploadResultItem saveAppTime(String cPackage, long nAppTime,String cReqId,String sign) throws InfoSourceException {
		return null;
	}

	@Override
	public void renewalBox() throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void appPayment(String nAppId, String cAppOrder, String cAppAccuntId, String cGoodId, String sGoodName,
			Integer iGoodNum, Integer nMoney) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

}
