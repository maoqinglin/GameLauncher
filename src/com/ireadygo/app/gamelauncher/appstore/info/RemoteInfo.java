package com.ireadygo.app.gamelauncher.appstore.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AgentAppItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.BannerItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.BindPhoneItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.CategoryItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.FeeConfigItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.FreeFlowStatusItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.info.item.KeywordItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.QuotaItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.RechargePhoneItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.RentReliefItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.SlotConfigItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.SubscribeResultItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserHeaderImgItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserSlotInfoItem;
import com.ireadygo.app.gamelauncher.appstore.manager.FreeFlowManager;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.snail.appstore.openapi.AppPlatFormClient;
import com.snail.appstore.openapi.AppPlatFormConfig;
import com.snail.appstore.openapi.base.vo.PageListVO;
import com.snail.appstore.openapi.base.vo.ResultVO;
import com.snail.appstore.openapi.exception.HttpStatusCodeException;
import com.snail.appstore.openapi.json.JSONException;
import com.snail.appstore.openapi.service.IAppPlatFormService;
import com.snail.appstore.openapi.vo.AgentAppListItemVO;
import com.snail.appstore.openapi.vo.AppBannerVO;
import com.snail.appstore.openapi.vo.AppCollectionVO;
import com.snail.appstore.openapi.vo.AppDetailVO;
import com.snail.appstore.openapi.vo.AppDownUrlVO;
import com.snail.appstore.openapi.vo.AppHotwordVO;
import com.snail.appstore.openapi.vo.AppListItemVO;
import com.snail.appstore.openapi.vo.AppMappingVO;
import com.snail.appstore.openapi.vo.AppTimeUploadResultVO;
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

public class RemoteInfo implements IGameInfo {

	private static final String TAG = "RemoteInfo";

	private Context mContext;
	private static final String APP_SECRET = "AW9JLSINEI7ELL";
	private static final String APP_KEY = "sdflkcxjvxc_e";
//	private static final String APP_HOST_URL = "http://116.213.130.66";
//	private static final String APP_HOST_URL = "http://116.213.130.12";
//	private static final String APP_HOST_URL = "http://open.app.snail.com";
	private static final String APP_HOST_URL = "http://api.app.snail.com";

	private IAppPlatFormService mAppPlatFormService;
	private static final int RESULT_SUCCESS_CODE = 1;
	private static final int RESULT_HAS_INIT_SLOT_CODE = 8604;
	private static final int RESULT_SLOT_NOT_ENOUGH_CODE = 8601;
	private static final int RESULT_APP_HAS_BIND_CODE = 8602;
	private static final int RESULT_UNKNOWN_ERROR_CODE = 1000;
	private static final int RESULT_NO_PERMISSION_CODE = 5005;
	private static final int RESULT_PERMISSION_ERROR_CODE = 1001;
	private static final int RESULT_ACCOUNT_ERROR_CODE = 5118;
	private static final int RESULT_NO_ENOUGH_MONEY = 5112;

	private static final int RESULT_RABBIT_TICKET_NOT_EXIST = 8710;
	private static final int RESULT_RABBIT_TICKET_USED = 8711;
	private static final int RESULT_RABBIT_TICKET_PASSWORD_WRONG = 8716;
	private static final int RESULT_RABBIT_TICKET_TYPE_MATCH = 8719;
	private static final int RESULT_RABBIT_TICKET_ONE_BIND = 8720;

	private static final int RESULT_SLOT_TICKET_NOT_EXIST = 8724;
	private static final int RESULT_SLOT_TICKET_USED = 8725;
	private static final int RESULT_SLOT_TICKET_TYPE_MATCH = 8726;
	private static final int RESULT_SLOT_TICKET_BIND_FAILED = 8727;

	private static final int RESULT_ILLEGALITY_BSS_ACCOUNT = 5134;
	private static final int RESULT_PHONE_ALREADY_BIND = 8714;
	private static final int RESULT_PHONE_BIND_FAILED = 8715;
	private static final int RESULT_BIND_PHONE_WITHOUT_RECHARGE = 8721;

	private static final int RESULT_PHONE_NOT_BIND = 8713;
	private static final int RESULT_IMEI_NOT_KNOWN = 8712;
	private static final int RESULT_FEE_LIMINT = 8017;
	private static final int RESULT_ACCOUNT_OUTDATE = 1008;

	private static final int RESULT_SMS_CODE_ERROR = 5206;
	private static final int RESULT_FREE_FLOW_NOT_SUPPORT = 5006;
	private static final int RESULT_FREE_FLOW_HAS_OPEND = 5009;
	private static final int RESULT_FREE_FLOW_EXPIRED = 5010;
	private static final int RESULT_FREE_FLOW_FAILED = 5145;
	private static final int RESULT_SMS_USE_OUT = 10006;
	private static final int RESULT_AGENT_APP_NOT_FOUND = 5001;
	

	private static final String PARAM_DIVIDER = ",";
	private static final String KEY_REQ_TIME = "dTime";
	private static final String KEY_SLOT_NUM = "iSlotNum";
	private static final String VALUE_NEED_UPDATE = "1";


	public RemoteInfo(Context context) {
		mContext = context;
		initAppPlatform();
	}

	private void initAppPlatform() {
		AppPlatFormConfig.setHttpHost(APP_HOST_URL);
		AppPlatFormConfig.setAppSecret(APP_SECRET);
		AppPlatFormConfig.setApiKey(APP_KEY);
		mAppPlatFormService = AppPlatFormClient.getInstant(mContext).getAppPlatFormService();
	}

	@Override
	public int obtainChildrenCount(String parentItemId) throws InfoSourceException {
		return 0;
	}

	/*
	 * 获取分类下的应用列表
	 * 参数：分类的ID
	 */

	@Override
	public ArrayList<AppEntity> obtainChildren(String id,int page) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getAppListByCategory(Long.parseLong(id),page);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				StringBuffer appIds = new StringBuffer();
				HashMap<String, AppEntity> cachedChildrenList = new HashMap<String, AppEntity>();
				ArrayList<AppEntity> result = new ArrayList<AppEntity>();
				if (resultVO.getObj() != null) {
					PageListVO pageListVO = (PageListVO)resultVO.getObj();
					List<AppListItemVO> appListItemVOs = (List<AppListItemVO>)pageListVO.getList();
					for (AppListItemVO appListItemVO : appListItemVOs) {
						AppEntity app = listItemToAppEntity(appListItemVO);
						result.add(app);
						appIds.append(app.getAppId()).append(",");
						cachedChildrenList.put(app.getAppId(), app);
					}
					if (GameLauncherConfig.ENABLE_FREE_FLOW
							&& FreeFlowManager.getInstance(mContext).isProxyMode() 
							&& appIds.length() > 0) {
						List<AgentAppItem> agentAppItems = getAgentAppItems(appIds.toString());
						for (AgentAppItem agentAppItem : agentAppItems) {
							AppEntity agentApp = cachedChildrenList.get(String.valueOf(agentAppItem.getNAppId()));
							if (agentApp != null) {
								agentApp.setFreeFlag(agentAppItem.getiFlowFree());
							}
						}
					}
				}
				return result;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	//获取指定ID应用的详情
	@Override
	public AppEntity obtainItemById(String appId) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getAppDetail(Long.parseLong(appId));
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getObj() == null) {
					return null;
				}
				AppDetailVO appDetailVO = (AppDetailVO)resultVO.getObj();
				AppEntity appDetail = detailToAppEntity(appDetailVO);
				//先获取默认的下载地址
				String downloadUrl = obtainNormalDownloadUrl(Long.parseLong(appDetail.getAppId()));
				appDetail.setDownloadPath(downloadUrl);
				GameData.getInstance(mContext).updateDownloadPath(appDetail.getPkgName(), downloadUrl);
				//免流量模式下，获取免流量
				if (GameLauncherConfig.ENABLE_FREE_FLOW && !FreeFlowManager.getInstance(mContext).isFreeFlowDisable()) {
					String freeflowDldUrl = obtainFreeFlowDownloadUrl(Long.parseLong(appDetail.getAppId()));
					appDetail.setFreeflowDldPath(freeflowDldUrl);
					GameData.getInstance(mContext).updateFreeflowDownloadPath(appDetail.getPkgName(), freeflowDldUrl);
					if (FreeFlowManager.getInstance(mContext).isProxyMode()) {//代理模式下，需要重新更新免流量标志
						//刷新免流量标志
						List<AgentAppItem> agentAppItems = getAgentAppItems(appDetail.getAppId());
						if (agentAppItems != null && agentAppItems.size() > 0) {
							appDetail.setFreeFlag(agentAppItems.get(0).getiFlowFree());
						}
					}
				}
				return appDetail;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}


	/*
	 * 获取所有的分类
	 */
	@Override
	public List<CategoryItem> obtainCategorys() throws InfoSourceException {
		ResultVO resultVO;
		try {
			resultVO = mAppPlatFormService.getGameCategory();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				List<CategoryItem> results = new ArrayList<CategoryItem>();
				if (resultVO.getObj() != null) {
					List<AppCollectionVO> appCollectionVOs = (List<AppCollectionVO>)resultVO.getObj();
					for (AppCollectionVO collection : appCollectionVOs) {
						results.add(collectionItemToCategory(collection));
					}
				}
				return results;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	/*
	 * 获取指定页的合集信息
	 */
	@Override
	public List<CategoryItem> obtainCollection(int page) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getGameCollection(page);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				List<CategoryItem> result = new ArrayList<CategoryItem>();
				if (resultVO.getObj() != null) {
					PageListVO pageListVO = (PageListVO)resultVO.getObj();
					List<AppCollectionVO> appCollectionVOs = (ArrayList<AppCollectionVO>)pageListVO.getList();
					for (AppCollectionVO appCollectionVO : appCollectionVOs) {
						result.add(collectionItemToCategory(appCollectionVO));
					}
				}
				return result;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}


	/*
	 * 获取关键字列表
	 */
	@Override
	public List<KeywordItem> obtainKeywords() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getKeywordAttr();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				List<KeywordItem> result = new ArrayList<KeywordItem>();
				if (resultVO.getObj() != null) {
					List<AppHotwordVO> appHotwordVOs = (ArrayList<AppHotwordVO>)resultVO.getObj();
					for (AppHotwordVO appHotwordVO : appHotwordVOs) {
						result.add(AppHotwordVOToKeywordItem(appHotwordVO));
					}
				}
				return result;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}


	/*
	 * 根据关键字获取关键字列表
	 */
	@Override
	public List<String> obtainKeywordsByWord(String word) throws InfoSourceException {
			ResultVO resultVO;
			try {
				resultVO = mAppPlatFormService.getKeywordList(word);
				if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
					List<String> result = new ArrayList<String>();
					if (resultVO.getObj() != null) {
						List<KeyWordVO> keywords = (List<KeyWordVO>)resultVO.getObj();
						for (KeyWordVO keyWord : keywords) {
							result.add(keyWord.getSKeyWord());
						}
					}
					return result;
				}
				String errMsg = processRemoteResultCode(resultVO.getCode());
				throw new InfoSourceException(errMsg);
			} catch (InfoSourceException e) {
				throw e;
			} catch (HttpStatusCodeException e) {
				throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
			} catch (JSONException e) {
				throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
			} catch (Exception e) {
				throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
			}
	}

	/*
	 * 获取指定ID游戏的下载地址（根据当前免流量状态，返回对应的地址）
	 */
	@Override
	public String obtainDownloadUrl(long id) throws InfoSourceException {
			try {
				if (GameLauncherConfig.ENABLE_FREE_FLOW && FreeFlowManager.getInstance(mContext).isProxyMode()) {
					String agentDownloadUrl = getAgentDownloadUrl(String.valueOf(id));//代理模式，直接重新获取下载地址
					if (TextUtils.isEmpty(agentDownloadUrl)) {//获取失败，返回默认下载地址
						return obtainNormalDownloadUrl(id);
					}
					return agentDownloadUrl;
				} else {
					String normalDldPaht = obtainNormalDownloadUrl(id);
					if (TextUtils.isEmpty(normalDldPaht)) {
						return new String();
					}
					if (GameLauncherConfig.ENABLE_FREE_FLOW && FreeFlowManager.getInstance(mContext).isUnProxyMode()) {
						return FreeFlowManager.getInstance(mContext).replaceDomain(normalDldPaht);//非代理模式，替换获取到的下载地址的域名
					} else {
						return normalDldPaht;
					}
				}
			} catch (InfoSourceException e) {
				throw e;
			} catch (HttpStatusCodeException e) {
				throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
			} catch (Exception e) {
				throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
			}
	}

	private String obtainFreeFlowDownloadUrl(long id) throws InfoSourceException {
		try {
			if (GameLauncherConfig.ENABLE_FREE_FLOW && FreeFlowManager.getInstance(mContext).isProxyMode()) {
				String agentDownloadUrl = getAgentDownloadUrl(String.valueOf(id));//代理模式，直接重新获取下载地址
				if (TextUtils.isEmpty(agentDownloadUrl)) {//获取失败，返回默认下载地址
					return new String();
				}
				return agentDownloadUrl;
			} else {
				String normalDldPaht = obtainNormalDownloadUrl(id);
				if (TextUtils.isEmpty(normalDldPaht)) {
					return new String();
				}
				if (GameLauncherConfig.ENABLE_FREE_FLOW && FreeFlowManager.getInstance(mContext).isUnProxyMode()) {
					return FreeFlowManager.getInstance(mContext).replaceDomain(normalDldPaht);//非代理模式，替换获取到的下载地址的域名
				} else {
					return normalDldPaht;
				}
			}
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	private String obtainNormalDownloadUrl(long id) throws InfoSourceException {
		ResultVO resultVO;
		try {
			resultVO = mAppPlatFormService.getDownLoadUrl(id);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				AppDownUrlVO urlVO = (AppDownUrlVO)resultVO.getObj();
				if (null != urlVO) {
					return urlVO.getCDownloadUrl();
				}
				return new String();
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
}

	/*
	 * 根据关键字获取搜索结果
	 */
	@Override
	public List<AppEntity> searchByKeyword(String word, int page, int number) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getGameList(word, page, number);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				List<AppEntity> results = new ArrayList<AppEntity>();
				if (resultVO.getObj() != null) {
					PageListVO pageListVO = (PageListVO)resultVO.getObj();
					List<AppListItemVO> appListItemVOs = (List<AppListItemVO>)pageListVO.getList();
					for (AppListItemVO item : appListItemVOs) {
						results.add(listItemToAppEntity(item));
					}
				}
				return results;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	/*
	 * 获取推荐页列表
	 */
	@Override
	public List<BannerItem> obtainBannerList(int page) throws InfoSourceException {
		ResultVO resultVO;
		try {
			resultVO = mAppPlatFormService.getBannerList(page);
			StringBuffer appIds = new StringBuffer();
			HashMap<Long, BannerItem> cachedBannerItemMap = new HashMap<Long, BannerItem>();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				List<BannerItem> results = new ArrayList<BannerItem>();
				if (resultVO.getObj() != null) {
					PageListVO pageListVO = (PageListVO)resultVO.getObj();
					List<AppBannerVO> appBannerVOs = (List<AppBannerVO>)pageListVO.getList();
					for (AppBannerVO item : appBannerVOs) {
						BannerItem bannerItem = AppBannerToBannerItem(item);
						results.add(bannerItem);
						if (AppEntity.BANNER_TYPE_GAME.equals(bannerItem.getCType())) {
							appIds.append(bannerItem.getNParamId()).append(",");
							cachedBannerItemMap.put(bannerItem.getNParamId(), bannerItem);
						}
					}
					//代理模式下，需要替换列表的免流量标志
					if (GameLauncherConfig.ENABLE_FREE_FLOW
							&& FreeFlowManager.getInstance(mContext).isProxyMode()
							&& appIds.length() > 0) {
						List<AgentAppItem> agentItems = getAgentAppItems(appIds.toString());
						for (AgentAppItem agentAppItem : agentItems) {
							BannerItem bannerItem = cachedBannerItemMap.get(agentAppItem.getNAppId());
							if (bannerItem != null) {
								bannerItem.setIFlowFree(agentAppItem.getiFlowFree());
							}
						}
					}
				}
				return results;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	//查询应用是否有更新
	@Override
	public List<AppEntity> obtainUpdatableApp(List<AppEntity> appList) throws InfoSourceException {
		StringBuffer sbPkgNameList = new StringBuffer();
		StringBuffer sbVersionCodeList = new StringBuffer();

		for (AppEntity app : appList) {
			sbPkgNameList.append(app.getPkgName()).append(PARAM_DIVIDER);
			sbVersionCodeList.append(app.getVersionCode()).append(PARAM_DIVIDER);
		}
		try {
			ResultVO resultVO = mAppPlatFormService.getAppUpdateList(sbPkgNameList.toString(), sbVersionCodeList.toString(), "1");
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				List<AppEntity> results = new ArrayList<AppEntity>();
				if (resultVO.getObj() != null) {
					List<AppUpdateVO> appUpdateVOs = (List<AppUpdateVO>)resultVO.getObj();
					for (AppUpdateVO item : appUpdateVOs) {
						if (!TextUtils.isEmpty(item.getCUpdate()) && item.getCUpdate().equals(VALUE_NEED_UPDATE)) {
							results.add(appUpdateItemToAppEntity(item));
						}
					}
				}
				return results;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	//查询当前应用是否属于免商店
	//传入参数AppEntity需要包含pkgName 和 curVersionCode两项
	//返回匹配上的游戏ID
	@Override
	public List<Long> mapAppWithFreeStore(List<AppEntity> appList) throws InfoSourceException {
		StringBuffer sbPkgNameList = new StringBuffer();
		StringBuffer sbVersionCodeList = new StringBuffer();
		for (AppEntity app : appList) {
			sbPkgNameList.append(app.getPkgName()).append(PARAM_DIVIDER);
			sbVersionCodeList.append(app.getVersionCode()).append(PARAM_DIVIDER);
		}
		try {
			ResultVO resultVO = mAppPlatFormService.getAppMappingList(sbPkgNameList.toString(), sbVersionCodeList.toString(), "1");
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				List<Long> results = new ArrayList<Long>();
				if (resultVO.getObj() != null) {
					List<AppMappingVO> appMappingVOs = (List<AppMappingVO>)resultVO.getObj();
					for (AppMappingVO appMappingVO : appMappingVOs) {
						results.add(appMappingVO.getNAppId());
					}
				}
				return results;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public UserInfoItem getUserInfo() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getAccountInfo();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getObj() == null) {
					return new UserInfoItem();
				}
				UserBasicVO userBasicVO = (UserBasicVO)resultVO.getObj();
				return userBasicVOToUserInfoItem(userBasicVO);
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}




	@Override
	public List<UserHeaderImgItem> getUserHeaderImgItems() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getUserHeaderList();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getObj() == null) {
					return new ArrayList<UserHeaderImgItem>();
				}
				List<UserHeaderVO> userHeaderVOs = (List<UserHeaderVO>)resultVO.getObj();
				List<UserHeaderImgItem> result = new ArrayList<UserHeaderImgItem>();
				for (UserHeaderVO userHeaderVO : userHeaderVOs) {
					result.add(userHeaderVoToUserHeaderImgItem(userHeaderVO));
				}
				return result;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void saveUserInfo(String url, String nickName, String sex, String age, String email,
			String birthday) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.saveUserInfo(
					url,
					nickName,
					sex,
					age,
					email,
					birthday);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void purchaseMuchSlot(int slotConfigId) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.purchaseMuchSlot(slotConfigId);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public List<SlotConfigItem> getUserSlotConfigItems() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getMuchSlotConfigList();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getObj() == null) {
					return new ArrayList<SlotConfigItem>();
				}
				List<MuchSlotConfigVO> muchSlotConfigVOs = (List<MuchSlotConfigVO>)resultVO.getObj();
				List<SlotConfigItem> result = new ArrayList<SlotConfigItem>();
				for (MuchSlotConfigVO muchSlotConfigVO : muchSlotConfigVOs) {
					result.add(muchSlotConfigVOToSlotConfigItem(muchSlotConfigVO));
				}
				return result;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void bindAppToSlot(String pkgName) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.bindAppMuchSlot(pkgName);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void unbindAppToSlot(String pkgName) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.unBindAppMuchSlot(pkgName);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public List<UserSlotInfoItem> getUserSlotInfoItems() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getAppUserMuchSlotList();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				Long reqTime = 0L;
				int slotNum = 0;
				if (resultVO.getValue() != null) {
					JSONObject value = new JSONObject(resultVO.getValue());
					reqTime = value.getLong(KEY_REQ_TIME);
					slotNum = value.getInt(KEY_SLOT_NUM);
				}
				if (resultVO.getObj() == null) {
					return new ArrayList<UserSlotInfoItem>();
				}
				List<MuchUserAppSlotVO> muchUserAppSlotVOs = (List<MuchUserAppSlotVO>)resultVO.getObj();
				List<UserSlotInfoItem> result = new ArrayList<UserSlotInfoItem>();
				for (MuchUserAppSlotVO muchUserAppSlotVO : muchUserAppSlotVOs) {
					UserSlotInfoItem item = muchUserSlotInfoItemToUserSlotInfoItem(muchUserAppSlotVO);
					item.setReqTime(reqTime);
					item.setSlotNum(slotNum);
					result.add(item);
				}
				return result;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}


	@Override
	public String getUserNickName(String uId) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getUserNickName(String.valueOf(uId));
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getValue() == null) {
					return null;
				}
				return (String)resultVO.getValue();
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void initSlotWithAccount() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.donateUserSlot();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public int[] getUserSlotNum() throws InfoSourceException {
		int [] result = new int[2];
		try {
			ResultVO resultVO = mAppPlatFormService.getAppUserMuchSlotList();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getValue() != null) {
					JSONObject value = new JSONObject(resultVO.getValue());
					result[0] = value.getInt(KEY_SLOT_NUM);//总卡槽数
					if (resultVO.getObj() == null) {
						result[1] = 0;
					} else {
						List<MuchUserAppSlotVO> muchUserAppSlotVOs = (List<MuchUserAppSlotVO>)resultVO.getObj();
						result[1] = muchUserAppSlotVOs.size();//已使用的卡槽数
					}
				} else {
					throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_SERVER_ERROR);
				}
				return result;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public int[] rechargeSlotTicket(String ticketNum) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.rechargeSlotTicket(ticketNum);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				int[] result = new int[2];
				if (resultVO.getObj() != null) {
					SlotRechargeVO slotRechargeVO = (SlotRechargeVO)resultVO.getObj();
					result[0] = slotRechargeVO.getISlotAmount();//充值成功的卡槽数
					result[1] = slotRechargeVO.getILimitTime();//充值成功的卡槽到期时间（天）
					return result;
				}
				throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_SERVER_ERROR);
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public int rechargeRabbitTicket(String num, String password) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.rechargeRabbitTicket(num, password);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getValue() != null) {
					String chargeAmount = resultVO.getValue();
					int result = Integer.parseInt(chargeAmount);//兔兔券充值成功金额
					return result;
				}
				throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_SERVER_ERROR);
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void bindTelToRabbit(String phone) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.bindTelToRabbit(phone);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public QuotaItem calculateQuota(int money, int type) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.calculateQuota(Integer.valueOf(money), type);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if(resultVO.getObj() != null) {
					return quotaVoToQuotaItem((QuotaVO)resultVO.getObj());
				}
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public BindPhoneItem getBindPhoneNum() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getBindPhoneNum();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if(resultVO.getObj() != null) {
					return bindPhoneVoTobindPhoneItem((BindPhoneVO)resultVO.getObj());
				}
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public List<FeeConfigItem> getFeeConfig() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getFeeConfig();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if(resultVO.getObj() != null) {
					List<FeeConfigItem> feeConfigItems = new ArrayList<FeeConfigItem>();
					List<FeeConfigVO> feeConfigVOs = (List<FeeConfigVO>)resultVO.getObj();
					for (FeeConfigVO feeConfigVO : feeConfigVOs) {
						feeConfigItems.add(feeConfigVOTofeeConfigItem(feeConfigVO));
					}
					return feeConfigItems;
				}
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public RechargePhoneItem rechargePhone(long id, String phoneNum, int feeNum) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.rechargePhone(Long.valueOf(id), phoneNum, feeNum);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if(resultVO.getObj() != null) {
					return rechargePhoneVOTorechargePhoneItem((RechargePhoneVO)resultVO.getObj());
				}
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public boolean checkBSSAccount(String phone) {
		boolean isSuccess = false;
		try {
			ResultVO resultVO = mAppPlatFormService.checkBSS(phone);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				isSuccess = true;
				return isSuccess;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			return isSuccess;
		} catch (HttpStatusCodeException e) {
			return isSuccess;
		} catch (Exception e) {
			return isSuccess;
		}
	}

	private String processRemoteResultCode(int code) {

		switch (code) {
		case RESULT_APP_HAS_BIND_CODE:
			return InfoSourceException.MSG_APP_HAS_BIND_ERROR;
		case RESULT_HAS_INIT_SLOT_CODE:
			return InfoSourceException.MSG_SLOT_HAS_INIT_ERROR;
		case RESULT_SLOT_NOT_ENOUGH_CODE:
			return InfoSourceException.MSG_SLOT_NOT_ENOUGH_ERROR;
		case RESULT_PERMISSION_ERROR_CODE:
		case RESULT_NO_PERMISSION_CODE:
			return InfoSourceException.MSG_PERMISSION_ERROR;
		case RESULT_UNKNOWN_ERROR_CODE:
			return InfoSourceException.MSG_UNKNOWN_SERVER_ERROR;
		case RESULT_ACCOUNT_ERROR_CODE:
			return InfoSourceException.MSG_ACCOUNT_ERROR;
		case RESULT_NO_ENOUGH_MONEY:
			return InfoSourceException.MSG_NO_ENOUGH_MONEY_ERROR;
		case RESULT_RABBIT_TICKET_NOT_EXIST:
			return InfoSourceException.MSG_RABBIT_TICKET_NOT_EXIST_ERROR;
		case RESULT_RABBIT_TICKET_PASSWORD_WRONG:
			return InfoSourceException.MSG_RABBIT_TICKET_PASSWORD_WRONG_ERROR;
		case RESULT_RABBIT_TICKET_USED:
			return InfoSourceException.MSG_RABBIT_TICKET_USED_ERROR;
		case RESULT_RABBIT_TICKET_TYPE_MATCH:
			return InfoSourceException.MSG_RABBIT_TICKET_TYPE_MATCH_ERROR;
		case RESULT_RABBIT_TICKET_ONE_BIND:
			return InfoSourceException.MSG_RABBIT_TICKET_ONE_BIND_ERROR;
		case RESULT_ILLEGALITY_BSS_ACCOUNT:
			return InfoSourceException.MSG_ILLEGALITY_BSS_ACCOUNT_ERROR;
		case RESULT_PHONE_ALREADY_BIND:
			return InfoSourceException.MSG_PHONE_ALREADY_BIND_ERROR;
		case RESULT_PHONE_BIND_FAILED:
			return InfoSourceException.MSG_PHONE_BIND_FAILED_ERROR;
		case RESULT_PHONE_NOT_BIND:
			return InfoSourceException.MSG_PHONE_NOT_BIND_ERROR;
		case RESULT_BIND_PHONE_WITHOUT_RECHARGE:
			return InfoSourceException.MSG_BIND_PHONE_WITHOUT_RECHARGE_ERROR;
		case RESULT_FEE_LIMINT:
			return InfoSourceException.MSG_FEE_LIMINT_ERROR;
		case RESULT_SLOT_TICKET_BIND_FAILED:
			return InfoSourceException.MSG_SLOT_TICKET_ONE_BIND_ERROR;
		case RESULT_SLOT_TICKET_NOT_EXIST:
			return InfoSourceException.MSG_SLOT_TICKET_NOT_EXIST_ERROR;
		case RESULT_SLOT_TICKET_TYPE_MATCH:
			return InfoSourceException.MSG_SLOT_TICKET_TYPE_MATCH_ERROR;
		case RESULT_SLOT_TICKET_USED:
			return InfoSourceException.MSG_SLOT_TICKET_USED_ERROR;
		case RESULT_ACCOUNT_OUTDATE:
			return InfoSourceException.MSG_ACCOUNT_OUTDATE;
		case RESULT_IMEI_NOT_KNOWN:
			return InfoSourceException.MSG_IMEI_NOT_KNOWN;
		case RESULT_SMS_CODE_ERROR:
			return InfoSourceException.MSG_SMS_CODE_ERROR;
		case RESULT_FREE_FLOW_NOT_SUPPORT:
			return InfoSourceException.MSG_FREE_FLOW_NOT_SUPPORT;
		case RESULT_FREE_FLOW_EXPIRED:
			return InfoSourceException.MSG_FREE_FLOW_EXPIRED;
		case RESULT_FREE_FLOW_FAILED:
			return InfoSourceException.MSG_FREE_FLOW_FAILED;
		case RESULT_FREE_FLOW_HAS_OPEND:
			return InfoSourceException.MSG_FREE_FLOW_HAS_OPENED;
		case RESULT_SMS_USE_OUT:
			return InfoSourceException.MSG_FREE_FLOW_SMS_USE_OUT;
		case RESULT_AGENT_APP_NOT_FOUND:
			return InfoSourceException.MSG_AGENT_APP_NOT_FOUND;
		default:
			return InfoSourceException.MSG_UNKNOWN_ERROR;
		}
	}


	private CategoryItem collectionItemToCategory(AppCollectionVO item) {
		return new CategoryItem(
				item.getNCollectionId(), 
				item.getSCollectionName(),
				item.getSCollectionDec(),
				item.getCPicUrl(),
				item.getCPosterIcon(),
				item.getCPosterPic());
	}

	private RechargePhoneItem rechargePhoneVOTorechargePhoneItem(RechargePhoneVO rechargePhoneVO) {
		RechargePhoneItem rechargePhoneItem = new RechargePhoneItem();
		rechargePhoneItem.setCOrderId(rechargePhoneVO.getCOrderId());
		rechargePhoneItem.setICurrency(rechargePhoneVO.getICurrency());
		rechargePhoneItem.setIDiffCurrency(rechargePhoneVO.getIDiffCurrency());
		rechargePhoneItem.setIRabbitTicketMoney(rechargePhoneVO.getIRabbitTicketMoney());
		return rechargePhoneItem;
	}

	private FeeConfigItem feeConfigVOTofeeConfigItem(FeeConfigVO feeConfigVO) {
		FeeConfigItem feeConfigItem = new FeeConfigItem();
		feeConfigItem.setCMobileFeeType(feeConfigVO.getCMobileFeeType());
		feeConfigItem.setILimit(feeConfigVO.getILimit());
		feeConfigItem.setIMobileFeeMoney(feeConfigVO.getIMobileFeeMoney());
		feeConfigItem.setNMobileFeeId(feeConfigVO.getNMobileFeeId());
		feeConfigItem.setSInfo(feeConfigVO.getSInfo());
		feeConfigItem.setSMobileFeeName(feeConfigVO.getSMobileFeeName());
		return feeConfigItem;
	}

	private BindPhoneItem bindPhoneVoTobindPhoneItem(BindPhoneVO bindPhoneVO) {
		BindPhoneItem bindPhoneItem = new BindPhoneItem();
		bindPhoneItem.setBindPhoneNum(bindPhoneVO.getcBindPhoneNum());
		bindPhoneItem.setPhoneNum(bindPhoneVO.getcPhoneNum());
		return bindPhoneItem;
	}

	private QuotaItem quotaVoToQuotaItem(QuotaVO item) {
		QuotaItem quota = new QuotaItem();
		quota.setIConsumeRabbitCoin(Integer.valueOf(item.getIConsumeRabbitCoin()));
		quota.setIConsumeRabbitTicket(Integer.valueOf(item.getIConsumeRabbitTicket()));
		quota.setIDiffMoney(Integer.valueOf(item.getIDiffMoney()));
		return quota;
	}

	private AppEntity detailToAppEntity(AppDetailVO item) {
		AppEntity app = new AppEntity();
		app.setAppId(item.getNAppId().toString());
		app.setName(item.getSGameName());
		app.setRemoteIconUrl(item.getCIcon());
		app.setVersionName(item.getCVersionName());
		app.setVersionCode(item.getIVersionCode());
		app.setPkgName(item.getCPackage());
		app.setTotalSize(item.getISize());
		app.setSign(item.getCMd5());
		app.setDescription(item.getSGameDesc());
		app.setScreenshotUrl(item.getCPicUrl());
		app.setScreenshotDirection(item.getCAppScreen());
		app.setPosterIconUrl(item.getCPosterIcon());
		app.setPosterBgUrl(item.getCPosterPic());
		if (GameLauncherConfig.ENABLE_FREE_FLOW && FreeFlowManager.getInstance(mContext).isUnProxyMode()) {
			app.setFreeFlag(item.getIFlowFree());
		}
		return app;
	}

	private AppEntity listItemToAppEntity(AppListItemVO item) {
		AppEntity app = new AppEntity();
		app.setAppId(String.valueOf(item.getNAppId()));
		app.setPkgName(item.getCPackage());
		app.setRemoteIconUrl(item.getCIcon());
		app.setVersionName(item.getCVersionName());
		app.setName(item.getSGameName());
		app.setDescription(item.getSGameDesc());
		app.setTotalSize(item.getISize());
		app.setPosterIconUrl(item.getCPosterIcon());
		app.setPosterBgUrl(item.getCPosterPic());
		if (GameLauncherConfig.ENABLE_FREE_FLOW && FreeFlowManager.getInstance(mContext).isUnProxyMode()) {
			app.setFreeFlag(item.getIFlowFree());
		}
		return app;
	}

	private BannerItem AppBannerToBannerItem(AppBannerVO appBannerVO) {
		BannerItem bannerItem = new BannerItem();
		bannerItem.setSInfo(appBannerVO.getSInfo());
		bannerItem.setCType(appBannerVO.getCType());
		bannerItem.setCPostion(appBannerVO.getCPostion());
		bannerItem.setNParamId(appBannerVO.getNParamId());
		bannerItem.setCPicUrl(appBannerVO.getCPicUrl());
		bannerItem.setCHtmlUrl(appBannerVO.getCHtmlUrl());
		bannerItem.setIRefId(appBannerVO.getIRefId());
		
		bannerItem.setSGameName(appBannerVO.getSGameName());
		bannerItem.setCIcon(appBannerVO.getCIcon());
		bannerItem.setCVersionName(appBannerVO.getCVersionName());
		bannerItem.setIVersionCode(appBannerVO.getIVersionCode());
		bannerItem.setCPackage(appBannerVO.getCPackage());
		if (GameLauncherConfig.ENABLE_FREE_FLOW && FreeFlowManager.getInstance(mContext).isUnProxyMode()) {
			bannerItem.setIFlowFree(appBannerVO.getIFlowFree());
		}
		bannerItem.setCMd5(appBannerVO.getCMd5());
		bannerItem.setCMark(appBannerVO.getCMark());
		bannerItem.setISize(appBannerVO.getISize());
		bannerItem.setCPosterIcon(appBannerVO.getCPosterIcon());
		bannerItem.setCPosterPic(appBannerVO.getCPosterPic());
		return bannerItem;
	}


	private AppEntity appUpdateItemToAppEntity(AppUpdateVO item) {
		AppEntity app = new AppEntity();
		app.setAppId(String.valueOf(item.getNAppId()));
		app.setName(item.getSGameName());
		app.setRemoteIconUrl(item.getCIcon());
		app.setNewVersionCode(item.getIVersionCode());
		app.setNewVersionName(item.getsVersionName());
		app.setPkgName(item.getCPackage());
		app.setFreeFlag(item.getIFlowFree());
		app.setTotalSize(item.getiSize());
		app.setSign(item.getCMd5());
		app.setIsUpdateable(1);
		app.setGameState(GameState.UPGRADEABLE);
		return app;
	}

	private UserInfoItem userBasicVOToUserInfoItem(UserBasicVO item) {
		UserInfoItem userInfoItem = new UserInfoItem();
		userInfoItem.setCAge(item.getCAge());
		userInfoItem.setCEmail(item.getCEmail());
		userInfoItem.setCEmailStatus(item.getCEmailStatus());
		userInfoItem.setCPhone(item.getCPhone());
		userInfoItem.setCPhoto(item.getCPhoto());
		userInfoItem.setCSex(item.getCSex());
		userInfoItem.setDBirthday(item.getDBirthday());
		userInfoItem.setSNickname(item.getSNickname());
		userInfoItem.setiPoints(item.getIPoints());
		return userInfoItem;
	}

	private UserHeaderImgItem userHeaderVoToUserHeaderImgItem(UserHeaderVO item) {
		return new UserHeaderImgItem(item.getCImgUrl(), item.getSDesc());
	}

	private SlotConfigItem muchSlotConfigVOToSlotConfigItem(MuchSlotConfigVO item) {
		SlotConfigItem slotConfigItem = new SlotConfigItem();
		slotConfigItem.setCSlotType(item.getCSlotType());
		slotConfigItem.setISlotAmount(item.getISlotAmount());
		slotConfigItem.setISlotConfigId(item.getISlotConfigId());
		slotConfigItem.setISlotPrice(item.getISlotPrice());
		slotConfigItem.setITimeUnit(item.getITimeUnit());
		return slotConfigItem;
	}

	private UserSlotInfoItem muchUserSlotInfoItemToUserSlotInfoItem(MuchUserAppSlotVO item) {
		UserSlotInfoItem userSlotInfoItem = new UserSlotInfoItem();
		userSlotInfoItem.setCPackage(item.getCPackage());
		userSlotInfoItem.setDExpire(item.getDExpire());
		userSlotInfoItem.setDOpen(item.getDOpen());
		userSlotInfoItem.setNUserId(item.getNUserId());
		return userSlotInfoItem;
	}

	@Override
	public AppEntity obtainItemByIdFrmRemote(String appId) throws InfoSourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] uploadGeituiInfo(String clientId, String appId, String channelId, String phoneType)
			throws InfoSourceException {
		ResultVO resultVO;
		try {
			resultVO = mAppPlatFormService.uploadGetuiInfo(clientId, appId, channelId, phoneType);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getValue() != null) {
					String tags = (String)resultVO.getValue();
					String tag[] = tags.split(PARAM_DIVIDER);
					return tag;
				}
				return null;
			}
				String errMsg = processRemoteResultCode(resultVO.getCode());
				throw new InfoSourceException(errMsg);
			} catch (InfoSourceException e) {
				throw e;
			} catch (HttpStatusCodeException e) {
				throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
			} catch (Exception e) {
				throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
			}
		}

	@Override
	public void getSmsCode(String phoneNum) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getSmsCode(phoneNum);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void bindFreeFlowPhoneNum(String phoneNum, String smsCode) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.bindFreeFlowPhoneNum(phoneNum, smsCode);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				PreferenceUtils.saveFreeFlowBindPhoneNum(phoneNum);
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void unbindFreeFlowPhoneNum(String phoneNum) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.unbindFreeFlowPhoneNum(phoneNum);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				PreferenceUtils.saveFreeFlowBindPhoneNum("");
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public FreeFlowStatusItem getFreeFlowStatus(String phoneNum) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getFreeFlowStatus(phoneNum);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getObj() == null) {
					return new FreeFlowStatusItem();
				}
				FlowStatusVO flowStatusVO = (FlowStatusVO)resultVO.getObj();
				return flowStatusToFreeFlowStatusItem(flowStatusVO);
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public SubscribeResultItem subScribeFreeFlow(String phoneNum) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.enableFreeFlow(phoneNum, null, null);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getObj() == null) {
					throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_SERVER_ERROR);
				}
				SubscribeResultVO subscribeResultVO = (SubscribeResultVO)resultVO.getObj();
				return subscribeResultToSubscribeResultItem(subscribeResultVO);
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public List<AgentAppItem> getAgentAppItems(String appIds) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getAgentApps(appIds);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				if (resultVO.getObj() == null) {
					return new ArrayList<AgentAppItem>();
				}
				List<AgentAppListItemVO> agentAppListItemVOs = (List<AgentAppListItemVO>)resultVO.getObj();
				List<AgentAppItem> result = new ArrayList<AgentAppItem>();
				for (AgentAppListItemVO agentAppListItemVO : agentAppListItemVOs) {
					result.add(agentAppListItemToAgentAppItem(agentAppListItemVO));
				}
				return result;
			} else if (resultVO.getCode() == RESULT_AGENT_APP_NOT_FOUND) {
				return new ArrayList<AgentAppItem>();
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public String getAgentDownloadUrl(String appId) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getAgentAppDownloadUrl(appId);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE && resultVO.getValue() != null) {
				return (String)resultVO.getValue();
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	private FreeFlowStatusItem flowStatusToFreeFlowStatusItem(FlowStatusVO flowStatusVO) {
		FreeFlowStatusItem result = new FreeFlowStatusItem();
		result.setmDomainUrl(flowStatusVO.getCOrientationDomain());
		result.setmEndDate(flowStatusVO.getDEnd());
		result.setmFreeFlowType(flowStatusVO.getCType());
		result.setmIsEnableFreeFlow(flowStatusVO.isBOpened());
		result.setmOperators(flowStatusVO.getSOperaters());
		result.setmPhoneNum(flowStatusVO.getNPhoneNum());
		result.setmProvince(flowStatusVO.getSProvince());
		result.setmServiceName(flowStatusVO.getCServiceName());
		result.setmUrl(flowStatusVO.getCUrl());
		return result;
	}

	private SubscribeResultItem subscribeResultToSubscribeResultItem(SubscribeResultVO subscribeResultVO) {
		SubscribeResultItem result = new SubscribeResultItem();
		result.setmEndDate(subscribeResultVO.getDEnd());
		result.setmFreeFlowType(subscribeResultVO.getCType());
		result.setmOpenDate(subscribeResultVO.getDOpen());
		result.setmPhoneNum(subscribeResultVO.getNPhoneNum());
		return result;
	}

	private AgentAppItem agentAppListItemToAgentAppItem(AgentAppListItemVO agentAppListItemVO) {
		AgentAppItem agentAppItem = new AgentAppItem();
		agentAppItem.setCDownloadUrl(agentAppListItemVO.getCDownloadUrl());
		agentAppItem.setCMD5(agentAppListItemVO.getCMD5());
		agentAppItem.setCPackage(agentAppListItemVO.getCPackage());
		agentAppItem.setCVersionName(agentAppListItemVO.getCVersionName());
		agentAppItem.setiFlowFree(agentAppListItemVO.getiFlowFree());
		agentAppItem.setIVersionCode(agentAppListItemVO.getIVersionCode());
		agentAppItem.setNAppId(agentAppListItemVO.getNAppId());
		agentAppItem.setSGameName(agentAppListItemVO.getSGameName());
		return agentAppItem;
	}

	private KeywordItem AppHotwordVOToKeywordItem(AppHotwordVO appHotwordVO) {
		KeywordItem keywordItem = new KeywordItem();
		keywordItem.setCPosterIcon(appHotwordVO.getCPosterIcon());
		keywordItem.setINum(appHotwordVO.getINum());
		keywordItem.setNAppId(appHotwordVO.getNAppId());
		keywordItem.setSKeyWord(appHotwordVO.getSKeyWord());
		return keywordItem;
	}

	private AppEntity preLoadItemVOToAppEntity(PreLoadItemVO preLoadItemVO) {
		AppEntity app = new AppEntity();
		app.setAppId(String.valueOf(preLoadItemVO.getNAppId()));
		app.setPosterIconUrl(preLoadItemVO.getCPosterIcon());
		app.setPosterBgUrl(preLoadItemVO.getCPosterPic());
		app.setName(preLoadItemVO.getSAppName());
		return app;
	}

	@Override
	public void cleanCached() {
		
	}

	@Override
	public List<AppEntity> getPreLoadList() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getPreLoadList();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				StringBuffer appIds = new StringBuffer();
				HashMap<String, AppEntity> cachedChildrenList = new HashMap<String, AppEntity>();
				ArrayList<AppEntity> result = new ArrayList<AppEntity>();
				if (resultVO.getObj() != null) {
					List<PreLoadItemVO> preLoadItemVOs = (List<PreLoadItemVO>)resultVO.getObj();
					for (PreLoadItemVO preLoadItem : preLoadItemVOs) {
						AppEntity app = preLoadItemVOToAppEntity(preLoadItem);
						result.add(app);
						appIds.append(app.getAppId()).append(",");
						cachedChildrenList.put(app.getAppId(), app);
					}
					if (GameLauncherConfig.ENABLE_FREE_FLOW
							&& FreeFlowManager.getInstance(mContext).isProxyMode() 
							&& appIds.length() > 0) {
						List<AgentAppItem> agentAppItems = getAgentAppItems(appIds.toString());
						for (AgentAppItem agentAppItem : agentAppItems) {
							AppEntity agentApp = cachedChildrenList.get(String.valueOf(agentAppItem.getNAppId()));
							if (agentApp != null) {
								agentApp.setFreeFlag(agentAppItem.getiFlowFree());
							}
						}
					}
				}
				return result;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void activateBox(String activateCode) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.activateBox(activateCode);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public List<String> getRentReliefAppList() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getRentReliefAppList();
			
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				List<String> results = new ArrayList<String>();
				if (resultVO.getObj() != null) {
					List<RentReliefAppVO> appRentReliefVOs = (List<RentReliefAppVO>)resultVO.getObj();
					for (RentReliefAppVO item : appRentReliefVOs) {
						if (!TextUtils.isEmpty(item.getSPackage())) {
							results.add(item.getSPackage());
						}
					}
				}
				return results;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public RentReliefItem getRentReliefAppTime() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.getRentReliefAppTime();
			
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				RentReliefItem item = new RentReliefItem();
				if (resultVO.getObj() != null) {
					RentReliefAppTime rentAppTime = (RentReliefAppTime)resultVO.getObj();
					item.setAppTime(Long.valueOf(rentAppTime.getNAppTime()));
					item.setAppRemainTime(Long.valueOf(rentAppTime.getNAppRemainTime()));
					item.setRenewalMoney(rentAppTime.getCRenewalMoney());
					Log.d("lmq", "rentAppTime= "+rentAppTime.getNAppTime()+"---remainTime = "+rentAppTime.getNAppRemainTime()+"--rentmoney = "+rentAppTime.getCRenewalMoney());
				}
				return item;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public AppTimeUploadResultVO saveAppTime(String cPackage, long nAppTime,String cReqId,String sign) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.saveAppTime(cPackage, nAppTime,cReqId,sign);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				AppTimeUploadResultVO uploadResult = new AppTimeUploadResultVO();
				uploadResult.setPackageName(cPackage);
				uploadResult.setResult(AppTimeUploadResultVO.SUCCESS);
				return uploadResult;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void renewalBox() throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.renewalBox();
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}

	@Override
	public void appPayment(String nAppId, String cAppOrder, String cAppAccuntId, String cGoodId, String sGoodName,
			Integer iGoodNum, Integer nMoney) throws InfoSourceException {
		try {
			ResultVO resultVO = mAppPlatFormService.appPayment(nAppId, cAppOrder, cAppAccuntId, cGoodId, sGoodName, iGoodNum, nMoney);
			if (resultVO.getCode() == RESULT_SUCCESS_CODE) {
				return;
			}
			String errMsg = processRemoteResultCode(resultVO.getCode());
			throw new InfoSourceException(errMsg);
		} catch (InfoSourceException e) {
			throw e;
		} catch (HttpStatusCodeException e) {
			throw new InfoSourceException(InfoSourceException.MSG_NETWORK_ERROR,e.getCause());
		} catch (JSONException e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_CLIENT_ERROR,e.getCause());
		} catch (Exception e) {
			throw new InfoSourceException(InfoSourceException.MSG_UNKNOWN_ERROR,e.getCause());
		}
	}
}
