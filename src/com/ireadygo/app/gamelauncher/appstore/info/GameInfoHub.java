package com.ireadygo.app.gamelauncher.appstore.info;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.aidl.rentfree.AppTimeUploadResultItem;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
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
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.snail.appstore.openapi.exception.HttpStatusCodeException;

public class GameInfoHub implements IGameInfo {

	private static GameInfoHub sInstance;
	private RemoteInfo mRemoteInfo;
	private MemoryInfo mMemoryInfo;
	private ImageLoader mImageLoader;
	private Context mContext;
	private GameData mGameData;

	public static GameInfoHub instance(Context context) {
		if (null == sInstance) {
			synchronized (GameInfoHub.class) {
				if (null == sInstance) {
					sInstance = new GameInfoHub(context);
				}
			}
		}
		return sInstance;
	}

	private GameInfoHub(Context context) {
		mContext = context;
		mRemoteInfo = new RemoteInfo(context);
		mMemoryInfo = new MemoryInfo(context);
		mImageLoader = ImageLoader.getInstance();
		mGameData = GameData.getInstance(mContext);
		configImageLoader();
	}

	private void configImageLoader() {
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		DisplayImageOptions options = getDisplayImageOptions();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
				.memoryCacheSize(am.getMemoryClass() * 1024 * 1024 / 8).threadPoolSize(5)
				.denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).threadPriority(Thread.MIN_PRIORITY)
				.defaultDisplayImageOptions(options).build();
		mImageLoader.init(config);
	}

	public DisplayImageOptions getDisplayImageOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	@Override
	public int obtainChildrenCount(String parentItemId) throws InfoSourceException {
		return 0;
	}

	@Override
	public ArrayList<AppEntity> obtainChildren(int dataType, String id, int page) throws InfoSourceException {
		return mRemoteInfo.obtainChildren(dataType,id, page);
	}

	@Override
	public AppEntity obtainItemById(String appId) throws InfoSourceException {
		AppEntity app = mGameData.getGameById(appId);
		if (app == null || app.isDldPathEmpty(mContext) || TextUtils.isEmpty(app.getDownloadPath())) {
			return mRemoteInfo.obtainItemById(appId);
		}
		return app;
	}

	@Override
	public List<CategoryInfo> obtainCategorys() throws InfoSourceException {
		if (!NetworkUtils.isNetworkConnected(mContext)) {
			try {
				return mMemoryInfo.obtainCategorys();
			} catch (InfoSourceException e) {
				throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR);
			}
		} else {
			if (!isCategorysCachedExpired()) {
				try {
					return mMemoryInfo.obtainCategorys();
				} catch (InfoSourceException e) {
					List<CategoryInfo> result = mRemoteInfo.obtainCategorys();
					mMemoryInfo.cachedCategoryItems(result);
					return result;
				}
			} else {
				List<CategoryInfo> result = mRemoteInfo.obtainCategorys();
				mMemoryInfo.cachedCategoryItems(result);
				return result;
			}
		}
	}

	@Override
	public List<CollectionInfo> obtainCollection(int page) throws InfoSourceException {
		if (!NetworkUtils.isNetworkConnected(mContext)) {
			try {
				return mMemoryInfo.obtainCollection(page);
			} catch (InfoSourceException e) {
				throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR);
			}
		} else {
			if (!isCollectionsCachedExpired()) {
				try {
					return mMemoryInfo.obtainCollection(page);
				} catch (InfoSourceException e) {
					List<CollectionInfo> result = mRemoteInfo.obtainCollection(page);
					mMemoryInfo.cachedCollectionItems(page, result);
					return result;
				}
			} else {
				List<CollectionInfo> result = mRemoteInfo.obtainCollection(page);
				mMemoryInfo.cachedCollectionItems(page, result);
				return result;
			}
		}
	}

	@Override
	public List<KeywordItem> obtainKeywords() throws InfoSourceException {
		if (!NetworkUtils.isNetworkConnected(mContext)) {
			try {
				return mMemoryInfo.obtainKeywords();
			} catch (InfoSourceException e) {
				throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR);
			}
		} else {
			if (!isKeywordCachedExpired()) {
				try {
					return mMemoryInfo.obtainKeywords();
				} catch (InfoSourceException e) {
					List<KeywordItem> result = mRemoteInfo.obtainKeywords();
					mMemoryInfo.cachedKeywordItems(result);
					return result;
				}
			} else {
				List<KeywordItem> result = mRemoteInfo.obtainKeywords();
				mMemoryInfo.cachedKeywordItems(result);
				return result;
			}
		}
	}

	@Override
	public List<String> obtainKeywordsByWord(String word, int iPlatformId, String cAppType) throws InfoSourceException {
		return mRemoteInfo.obtainKeywordsByWord(word, iPlatformId, cAppType);
	}

	@Override
	public String obtainDownloadUrl(long id) throws InfoSourceException {
		return mRemoteInfo.obtainDownloadUrl(id);
	}

	@Override
	public List<AppEntity> searchByKeyword(String word, int page, int number, int iPlatformId, String cAppType, String cDynamic) throws InfoSourceException {
		return mRemoteInfo.searchByKeyword(word, page, number, iPlatformId, cAppType, cDynamic);
	}

	@Override
	public List<BannerItem> obtainBannerList(int page) throws InfoSourceException {
		if (!NetworkUtils.isNetworkConnected(mContext)) {
			try {
				return mMemoryInfo.obtainBannerList(page);
			} catch (InfoSourceException e) {
				throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR);
			}
		} else {
			if (!isBannersCachedExpired()) {
				try {
					return mMemoryInfo.obtainBannerList(page);
				} catch (InfoSourceException e) {
					List<BannerItem> result = mRemoteInfo.obtainBannerList(page);
					mMemoryInfo.cachedBannerItems(page, result);
					return result;
				}
			} else {
				List<BannerItem> result = mRemoteInfo.obtainBannerList(page);
				mMemoryInfo.cachedBannerItems(page, result);
				return result;
			}
		}
	}

	@Override
	public List<AppEntity> obtainUpdatableApp(List<AppEntity> appList) throws InfoSourceException {
		return mRemoteInfo.obtainUpdatableApp(appList);
	}

	@Override
	public List<Long> mapAppWithFreeStore(List<AppEntity> appList) throws InfoSourceException {
		return mRemoteInfo.mapAppWithFreeStore(appList);
	}

	@Override
	public UserInfoItem getUserInfo() throws InfoSourceException {
		return mRemoteInfo.getUserInfo();
	}

	@Override
	public List<UserHeaderImgItem> getUserHeaderImgItems() throws InfoSourceException {
		return mRemoteInfo.getUserHeaderImgItems();
	}

	@Override
	public void saveUserInfo(String nickName, String cSex, String cPhoto, String cPhone, String birthday)
			throws InfoSourceException {
		mRemoteInfo.saveUserInfo(nickName, cSex, cPhoto, cPhone, birthday);
	}

	@Override
	public void purchaseMuchSlot(int slotConfigId) throws InfoSourceException {
		mRemoteInfo.purchaseMuchSlot(slotConfigId);
	}

	@Override
	public List<SlotConfigItem> getUserSlotConfigItems() throws InfoSourceException {
		return mRemoteInfo.getUserSlotConfigItems();
	}

	@Override
	public void bindAppToSlot(String pkgName) throws InfoSourceException {
		mRemoteInfo.bindAppToSlot(pkgName);
	}

	@Override
	public void unbindAppToSlot(String pkgName) throws InfoSourceException {
		mRemoteInfo.unbindAppToSlot(pkgName);
	}

	@Override
	public List<UserSlotInfoItem> getUserSlotInfoItems() throws InfoSourceException {
		return mRemoteInfo.getUserSlotInfoItems();
	}

	@Override
	public String getUserNickName(String uId) throws InfoSourceException {
		return mRemoteInfo.getUserNickName(uId);
	}

	@Override
	public void initSlotWithAccount() throws InfoSourceException {
		mRemoteInfo.initSlotWithAccount();
	}

	@Override
	public int[] getUserSlotNum() throws InfoSourceException {
		return mRemoteInfo.getUserSlotNum();
	}

	private boolean isCategorysCachedExpired() {
		return (System.currentTimeMillis() - PreferenceUtils.getCategoryExpiredTime() > GameLauncherConfig.CATEGORY_CACHED_EXPIRED_TIME);
	}

	private boolean isCollectionsCachedExpired() {
		return (System.currentTimeMillis() - PreferenceUtils.getCollectionExpiredTime() > GameLauncherConfig.COLLECTION_CACHED_EXPIRED_TIME);
	}

	private boolean isBannersCachedExpired() {
		return (System.currentTimeMillis() - PreferenceUtils.getBannerExpiredTime() > GameLauncherConfig.BANNER_CACHED_EXPIRED_TIME);
	}

	private boolean isKeywordCachedExpired() {
		return (System.currentTimeMillis() - PreferenceUtils.getKeywordExpiredTime() > GameLauncherConfig.KEYWORD_CACHED_EXPIRED_TIME);
	}

	private boolean isFeeConfigCachedExpired() {
		return (System.currentTimeMillis() - PreferenceUtils.getFeeConfigExpiredTime() > GameLauncherConfig.FEECONFIG_CACHED_EXPIRED_TIME);
	}

	@Override
	public int[] rechargeSlotTicket(String num) throws InfoSourceException {
		return mRemoteInfo.rechargeSlotTicket(num);
	}

	@Override
	public int rechargeRabbitTicket(String num, String password) throws InfoSourceException {
		return mRemoteInfo.rechargeRabbitTicket(num, password);
	}

	@Override
	public void bindTelToRabbit(String phone) throws InfoSourceException {
		mRemoteInfo.bindTelToRabbit(phone);
	}

	@Override
	public QuotaItem calculateQuota(int money, int type) throws InfoSourceException {
		return mRemoteInfo.calculateQuota(money, type);
	}

	@Override
	public BindPhoneItem getBindPhoneNum() throws InfoSourceException {
		return mRemoteInfo.getBindPhoneNum();
	}

	@Override
	public List<FeeConfigItem> getFeeConfig() throws InfoSourceException {
		if(!NetworkUtils.isNetworkConnected(mContext)) {
			try {
				return mMemoryInfo.getFeeConfig();
			} catch (InfoSourceException e) {
				throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR);
			}
		} else {
			if (!isFeeConfigCachedExpired()) {
				try {
					return mMemoryInfo.getFeeConfig();
				} catch (InfoSourceException e) {
					List<FeeConfigItem> result = mRemoteInfo.getFeeConfig();
					mMemoryInfo.cachedFeeConfigItems(result);
					return result;
				}
			} else {
				List<FeeConfigItem> result = mRemoteInfo.getFeeConfig();
				mMemoryInfo.cachedFeeConfigItems(result);
				return result;
			}
		}
	}

	@Override
	public RechargePhoneItem rechargePhone(long id, String phoneNum, int feeNum) throws InfoSourceException {
		return mRemoteInfo.rechargePhone(id, phoneNum, feeNum);
	}

	@Override
	public boolean checkBSSAccount(String phone) {
		return mRemoteInfo.checkBSSAccount(phone);
	}

	@Override
	public AppEntity obtainItemByIdFrmRemote(String appId) throws InfoSourceException {
		return mRemoteInfo.obtainItemById(appId);
	}

	@Override
	public String[] uploadGeituiInfo(String clientId, String appId, String channelId, String phoneType)
			throws InfoSourceException {
		return mRemoteInfo.uploadGeituiInfo(clientId, appId, channelId, phoneType);
	}

	@Override
	public void getSmsCode(String phoneNum) throws InfoSourceException {
		mRemoteInfo.getSmsCode(phoneNum);
	}

	@Override
	public void bindFreeFlowPhoneNum(String phoneNum, String smsCode) throws InfoSourceException {
		mRemoteInfo.bindFreeFlowPhoneNum(phoneNum, smsCode);
	}

	@Override
	public void unbindFreeFlowPhoneNum(String phoneNum) throws InfoSourceException {
		mRemoteInfo.unbindFreeFlowPhoneNum(phoneNum);
	}

	@Override
	public FreeFlowStatusItem getFreeFlowStatus(String phoneNum) throws InfoSourceException {
		return mRemoteInfo.getFreeFlowStatus(phoneNum);
	}

	@Override
	public SubscribeResultItem subScribeFreeFlow(String phoneNum) throws InfoSourceException {
		return mRemoteInfo.subScribeFreeFlow(phoneNum);
	}

	@Override
	public List<AgentAppItem> getAgentAppItems(String appIds) throws InfoSourceException {
		return mRemoteInfo.getAgentAppItems(appIds);
	}

	@Override
	public String getAgentDownloadUrl(String appId) throws InfoSourceException {
		return mRemoteInfo.getAgentDownloadUrl(appId);
	}

	@Override
	public void cleanCached() {
		mMemoryInfo.cleanCached();
	}

	@Override
	public List<AppEntity> getPreLoadList() throws InfoSourceException {
		return mRemoteInfo.getPreLoadList();
	}

	@Override
	public void activateBox(String cSN) throws InfoSourceException {
		mRemoteInfo.activateBox(cSN);
	}

	@Override
	public List<String> getRentReliefAppList() throws InfoSourceException {
		return mRemoteInfo.getRentReliefAppList();
	}

	@Override
	public RentReliefInfo getRentReliefAppTime() throws InfoSourceException {
		return mRemoteInfo.getRentReliefAppTime();
	}

	@Override
	public AppTimeUploadResultItem saveAppTime(String cPackage, long nAppTime,String cReqId,String sign) throws InfoSourceException {
		return mRemoteInfo.saveAppTime(cPackage, nAppTime,cReqId,sign);
	}

	@Override
	public String getSaleType(String cSN) throws InfoSourceException {
		return mRemoteInfo.getSaleType(cSN);
	}

	@Override
	public void bindAccount() throws InfoSourceException {
		mRemoteInfo.bindAccount();
	}

	@Override
	public String getSNCorrespondBindAccount(String cSN) throws InfoSourceException {
		return mRemoteInfo.getSNCorrespondBindAccount(cSN);
	}

	@Override
	public String getPaymentSign() throws InfoSourceException {
		return mRemoteInfo.getPaymentSign();
	}

	@Override
	public String bindPayment() throws InfoSourceException {
		return mRemoteInfo.bindPayment();
	}

	@Override
	public void bindTicket(String cTicketCode, String cTicketPwd, String cMac) throws InfoSourceException {
		mRemoteInfo.bindTicket(cTicketCode, cTicketPwd, cMac);
	}

	@Override
	public String getRentReliefMonths() throws InfoSourceException {
		return mRemoteInfo.getRentReliefMonths();
	}

	@Override
	public String getRebateMoney() throws InfoSourceException {
		return mRemoteInfo.getRebateMoney();
	}

	@Override
	public List<AppEntity> getCommonApp() throws InfoSourceException {
		return mRemoteInfo.getCommonApp();
	}

}
