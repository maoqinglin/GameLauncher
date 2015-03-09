package com.ireadygo.app.gamelauncher.appstore.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.content.Context;

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
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.snail.appstore.openapi.exception.HttpStatusCodeException;

public class MemoryInfo implements IGameInfo {

	private ArrayList<CategoryInfo> mCachedCategoryItems = new ArrayList<CategoryInfo>();
	private HashMap<Integer, List<CollectionInfo>> mCachedCollectionItems = new HashMap<Integer, List<CollectionInfo>>();
	private HashMap<Integer, List<BannerItem>> mCachedBannerItems = new HashMap<Integer, List<BannerItem>>();
	private ArrayList<KeywordItem> mCachedKeywordItems = new ArrayList<KeywordItem>();
	private List<FeeConfigItem> mCachedFeeConfigItems = new ArrayList<FeeConfigItem>();
	private HashMap<String, Integer> mCachedCategoryItemCount = new HashMap<String, Integer>();//id-count

	private LocalInfo mLocalInfo;

	public MemoryInfo(Context context) {
		mCachedCategoryItems.clear();
		mCachedCollectionItems.clear();
		mCachedBannerItems.clear();
		mCachedKeywordItems.clear();
		mCachedFeeConfigItems.clear();
		mLocalInfo = new LocalInfo(context);
	}

	@Override
	public int obtainChildrenCount(int type,String id) throws InfoSourceException {
		Integer categoryCount = 0;
		if (mCachedCategoryItemCount.size() > 0) {
			categoryCount = mCachedCategoryItemCount.get(id);
			if (categoryCount == null) {
				categoryCount = mLocalInfo.obtainChildrenCount(type, id);
				cachedCategoryItemCount(id, categoryCount);
			}
			return categoryCount;
		}
		categoryCount = mLocalInfo.obtainChildrenCount(type, id);
		cachedCategoryItemCount(id, categoryCount);
		return categoryCount;
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
	public List<CategoryInfo> obtainCategorys() throws InfoSourceException {
		if (mCachedCategoryItems.size() > 0) {
			return mCachedCategoryItems;
		}
		List<CategoryInfo> categorys = mLocalInfo.obtainCategorys();
		cachedMemoryCategoryItems(categorys);
		return categorys;
	}

	@Override
	public List<CollectionInfo> obtainCollection(int page) throws InfoSourceException {
		if (mCachedCollectionItems.size() > 0) {
			List<CollectionInfo> result = mCachedCollectionItems.get(page);
			if (result != null) {
				return result;
			}
			throw new InfoSourceException(InfoSourceException.MSG_NO_CACHED_DATA);
		}
		List<CollectionInfo> collections = mLocalInfo.obtainCollection(1);
		cachedMemoryCollectionItems(1, collections);
		return collections;
	}

	@Override
	public List<KeywordItem> obtainKeywords() throws InfoSourceException {
		if (mCachedKeywordItems.size() > 0) {
			return mCachedKeywordItems;
		}
		List<KeywordItem> keywords = mLocalInfo.obtainKeywords();
		cachedMemoryKeywordItems(keywords);
		return keywords;
	}

	@Override
	public List<String> obtainKeywordsByWord(String word, int iPlatformId, String cAppType) throws InfoSourceException {
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
		if (mCachedBannerItems.size() > 0) {
			List<BannerItem> result = mCachedBannerItems.get(page);
			if (result != null) {
				return result;
			}
			throw new InfoSourceException(InfoSourceException.MSG_NO_CACHED_DATA);
		}
		List<BannerItem> bannerItems = mLocalInfo.obtainBannerList(1);
		cachedMemoryBannerItems(1, bannerItems);
		return bannerItems;

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

	public void cachedCategoryItemCount(String id,int count) {
		mCachedCategoryItemCount.put(id, count);
		PreferenceUtils.setCategoryItemCountExpiredTime(System.currentTimeMillis());
		mLocalInfo.cachedCategoryItemCount(id,count);
	}

	public void cachedCategoryItems(List<CategoryInfo> categoryItems) {
		mCachedCategoryItems.clear();
		for (CategoryInfo item : categoryItems) {
			mCachedCategoryItems.add(item);
		}
		PreferenceUtils.setCategoryExpiredTime(System.currentTimeMillis());
		mLocalInfo.cachedCategorysToFile(categoryItems);
	}

	public void cachedBannerItems(int page,List<BannerItem> bannerItems) {
		List<BannerItem> cachedBannerItems = mCachedBannerItems.get(page);
		if (cachedBannerItems != null) {
			cachedBannerItems.clear();
		} else {
			cachedBannerItems = new ArrayList<BannerItem>();
		}
		for (BannerItem item : bannerItems) {
			cachedBannerItems.add(item);
		}
		mCachedBannerItems.put(page, cachedBannerItems);
		PreferenceUtils.setBannerExpiredTime(System.currentTimeMillis());
		if (page == 1) {
			mLocalInfo.cachedBannerListToFile(cachedBannerItems);
		}
	}

	public void cachedCollectionItems(int page,List<CollectionInfo> collectionItems) {
		List<CollectionInfo> cachedCollection = mCachedCollectionItems.get(page);
		if (cachedCollection != null) {
			cachedCollection.clear();
		} else {
			cachedCollection = new ArrayList<CollectionInfo>();
		}
		for (CollectionInfo item : collectionItems) {
			cachedCollection.add(item);
		}
		mCachedCollectionItems.put(page, cachedCollection);
		PreferenceUtils.setCollectionExpiredTime(System.currentTimeMillis());
		if (page == 1) {
			mLocalInfo.cachedCollectionToFile(collectionItems);
		}
	}

	public void cachedKeywordItems(List<KeywordItem> keywords) {
		mCachedKeywordItems.clear();
		for (KeywordItem keyword : keywords) {
			mCachedKeywordItems.add(keyword);
		}
		PreferenceUtils.setKeywordExpiredTime(System.currentTimeMillis());
		mLocalInfo.cachedKeywordsToFile(keywords);
	}

	public void cachedFeeConfigItems(List<FeeConfigItem> feeConfigItems) {
		mCachedFeeConfigItems.clear();
		for (FeeConfigItem feeConfigItem : feeConfigItems) {
			mCachedFeeConfigItems.add(feeConfigItem);
		}
		PreferenceUtils.setFeeConfigExpiredTime(System.currentTimeMillis());
		mLocalInfo.cachedFeeConfigListToFile(feeConfigItems);
	}

	public void cachedMemoryCategoryItems(List<CategoryInfo> categoryItems) {
		mCachedCategoryItems.clear();
		for (CategoryInfo item : categoryItems) {
			mCachedCategoryItems.add(item);
		}
	}

	public void cachedMemoryBannerItems(int page,List<BannerItem> bannerItems) {
		List<BannerItem> cachedBannerItems = mCachedBannerItems.get(page);
		if (cachedBannerItems != null) {
			cachedBannerItems.clear();
		} else {
			cachedBannerItems = new ArrayList<BannerItem>();
		}
		for (BannerItem item : bannerItems) {
			cachedBannerItems.add(item);
		}
		mCachedBannerItems.put(page, cachedBannerItems);
	}

	public void cachedMemoryCollectionItems(int page,List<CollectionInfo> collectionItems) {
		List<CollectionInfo> cachedCollection = mCachedCollectionItems.get(page);
		if (cachedCollection != null) {
			cachedCollection.clear();
		} else {
			cachedCollection = new ArrayList<CollectionInfo>();
		}
		for (CollectionInfo item : collectionItems) {
			cachedCollection.add(item);
		}
		mCachedCollectionItems.put(page, cachedCollection);
	}

	public void cachedMemoryKeywordItems(List<KeywordItem> keywords) {
		mCachedKeywordItems.clear();
		for (KeywordItem keyword : keywords) {
			mCachedKeywordItems.add(keyword);
		}
	}

	public void cachedMemoryFeeConfigItems(List<FeeConfigItem> feeConfigItems) {
		mCachedFeeConfigItems.clear();
		for (FeeConfigItem configItem : feeConfigItems) {
			mCachedFeeConfigItems.add(configItem);
		}
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
		if (mCachedFeeConfigItems.size() > 0) {
			return mCachedFeeConfigItems;
		}
		List<FeeConfigItem> feeConfigItems = mLocalInfo.getFeeConfig();
		cachedMemoryFeeConfigItems(feeConfigItems);
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
		mCachedBannerItems.clear();
		mLocalInfo.cleanCached();
	}

	@Override
	public List<AppEntity> getPreLoadList() throws InfoSourceException {
		return null;
	}

	@Override
	public void activateBox(String cSN) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getRentReliefAppList() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RentReliefInfo getRentReliefAppTime() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AppTimeUploadResultItem saveAppTime(String cPackage, long nAppTime,String cReqId,String sign) throws InfoSourceException {
		return null;
	}

	@Override
	public String getSaleType(String cSN) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bindAccount() throws HttpStatusCodeException, Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSNCorrespondBindAccount(String cSN) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPaymentSign() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bindPayment() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bindTicket(String cTicketCode, String cTicketPwd, String cMac) throws InfoSourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRentReliefMonths() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRebateMoney() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AppEntity> getCommonApp() throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

}
