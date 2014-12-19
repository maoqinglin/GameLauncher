package com.ireadygo.app.gamelauncher.appstore.info.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.appstore.manager.FreeFlowManager;

public class AppEntity implements Parcelable, Serializable {
	private static final long serialVersionUID = 563834692100527381L;
	public static final int CAN_UPGRADE = 1;
	public static final int CAN_NOT_UPGRADE = 0;
	public static final int CAME_FROM_FREE_STORE = 1;
	public static final int NOT_CAME_FROM_FREE_STORE = 0;
	public static final int IN_FREE_STORE = 1;
	public static final int NOT_IN_FREE_STORE = 0;
	public static final int OCCUPY_SLOT = 0;//占卡槽
	public static final int NOT_OCCUPY_SLOT = 1;//不占卡槽
	public static final String VERTICAL_PIC = "1";//截图是竖屏
	public static final String HORIZONTAL_PIC = "2";//截图是竖屏

	public static final String BANNER_TYPE_GAME = "1";
	public static final String BANNER_TYPE_COMP = "2";
	public static final String BANNER_TYPE_REDACT = "3";


	public static final int FLAG_DLD_FREE 				= 0;
	public static final int FLAG_PLAY_FREE 				= 1;
	public static final int FLAG_DLD_AND_PLAY_FREE 		= 2;
	public static final int FLAG_UPDATE_FREE 			= 3;
	public static final int FLAG_DLD_AND_UPDATE_FREE 	= 4;
	public static final int FLAG_UPDATE_AND_PLAY_FREE 	= 5;
	public static final int FLAG_DLD_UPDATE_PLAY_FREE 	= 6;
	public static final int FLAG_OTHER 					= 10;

	private String appId;

	private String deviceType = Build.MODEL;

	private String name;
	private String pkgName;
	private String fileName;
	private String downloadPath;
	private String savedPath;
	private GameState gameState = GameState.DEFAULT;
	private long totalSize;
	private long downloadSize;
	private int downloadSpeed;
	private long curVersionCode;
	private String curVersionName;
	private long newVersionCode;
	private String newVersionName;
	private long createTime;
	private String sign;
	private String remoteIconUrl;
	private String localIconUrl;
	private int isUpdateable;
	private String description;//游戏简介
	private int freeFlag = AppEntity.FLAG_OTHER;//免属性标志
	private String screenshotUrl;//游戏截屏图片地址
	private int status;//状态，上架或下架
	private long downloadCounts;//下载次数
	private int isInFreeStore;//是否在免商店中
	private int isComeFrmFreeStore;//是否来自免商店，即从免商店下载的 0--非来自免商店，1--来自免商店
	private int isOccupySlot;//是否占卡槽
	private String screenshotDirection = "";//截图的方向
	private String freeflowDldPath;
	private String posterIconUrl;//海报图标地址
	private String posterBgUrl;//海报背景地址
//	private GameManagerException mException;

	public static final Parcelable.Creator<AppEntity> CREATOR = new Parcelable.Creator<AppEntity>() {
		public AppEntity createFromParcel(Parcel src) {
			return new AppEntity(src);
		}

		public AppEntity[] newArray(int size) {
			return new AppEntity[size];
		}
	};

	public AppEntity() {

	}

	private AppEntity(Parcel src) {
		readFromParcel(src);
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public String getSavedPath() {
		return savedPath;
	}

	public void setSavedPath(String savedPath) {
		this.savedPath = savedPath;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public long getDownloadSize() {
		return downloadSize;
	}

	public void setDownloadSize(long dldedSize) {
		this.downloadSize = dldedSize;
	}

	public long getVersionCode() {
		return curVersionCode;
	}

	public void setVersionCode(long versionCode) {
		this.curVersionCode = versionCode;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getRemoteIconUrl() {
		return remoteIconUrl;
	}

	public void setRemoteIconUrl(String iconUrl) {
		this.remoteIconUrl = iconUrl;
	}

	public String getVersionName() {
		return curVersionName;
	}

	public void setVersionName(String versionName) {
		this.curVersionName = versionName;
	}

	public int getIsUpdateable() {
		return isUpdateable;
	}

	public void setIsUpdateable(int isUpdateable) {
		this.isUpdateable = isUpdateable;
	}

	public int getDownloadSpeed() {
		return downloadSpeed;
	}

	public void setDownloadSpeed(int downloadSpeed) {
		this.downloadSpeed = downloadSpeed;
	}

	@Override
	public String toString() {
		return "AppEntity [appId=" + appId + ", name=" + name + ", deviceType=" + deviceType + ", pkgName=" + pkgName
				+ ", fileName=" + fileName + ", downloadPath=" + downloadPath + ", savedPath=" + savedPath
				+ ", gameState=" + gameState + ", totalSize=" + totalSize + ", downloadSize=" + downloadSize
				+ ", downloadSpeed=" + downloadSpeed + ", versionCode=" + curVersionCode + ", versionName="
				+ curVersionName + ", createTime=" + createTime + ", sign=" + sign + ", iconUrl=" + remoteIconUrl
				+ ", isUpdateable=" + isUpdateable + ", newVersionName" + newVersionName + ", newVersionCode="
				+ newVersionCode + "description=" + description + "sceenshotUrl=" + screenshotUrl + " freeFlag="
				+ freeFlag + " status=" + status + " downloadCounts=" + downloadCounts + "isInFreeStore"
				+ isInFreeStore + "isComeFrmFreeStore" + isComeFrmFreeStore + "]";
	}

	public static enum PkgType {
		UNKNOW, APK, INCREMENT_APK, ZIP, INCREMENT_ZIP
	}

	public void copyFrom(AppEntity app) {
		this.appId = app.getAppId();
		this.name = app.getName();
		this.pkgName = app.getPkgName();
		this.fileName = app.getFileName();
		this.downloadPath = app.getDownloadPath();
		this.savedPath = app.getSavedPath();
		this.gameState = app.getGameState();
		this.totalSize = app.getTotalSize();
		this.downloadSize = app.getDownloadSize();
		this.downloadSpeed = app.getDownloadSpeed();
		this.curVersionCode = app.getVersionCode();
		this.curVersionName = app.getVersionName();
		this.createTime = app.getCreateTime();
		this.sign = app.getSign();
		this.remoteIconUrl = app.getRemoteIconUrl();
		this.localIconUrl = app.getLocalIconUrl();
		this.isUpdateable = app.getIsUpdateable();
		this.description = app.getDescription();
		this.screenshotUrl = app.getScreenshotUrl();
		this.status = app.getStatus();
		this.freeFlag = app.getFreeFlag();
		this.isComeFrmFreeStore = app.getIsComeFrmFreeStore();
		this.isOccupySlot = app.getIsOccupySlot();
		this.downloadCounts = app.getDownloadCounts();
		this.isInFreeStore = app.getIsInFreeStore();
		this.screenshotDirection = app.getScreenshotDirection();
		this.freeflowDldPath = app.getFreeflowDldPath();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(appId);
		dest.writeString(name);
		dest.writeString(pkgName);
		dest.writeString(deviceType);
		dest.writeString(fileName);
		dest.writeString(downloadPath);
		dest.writeString(savedPath);
		dest.writeString(gameState.toString());
		dest.writeString(curVersionName);
		dest.writeString(newVersionName);
		dest.writeString(sign);
		dest.writeString(remoteIconUrl);
		dest.writeString(localIconUrl);
		dest.writeString(description);
		dest.writeString(screenshotUrl);
		dest.writeLong(totalSize);
		dest.writeLong(downloadSize);
		dest.writeLong(createTime);
		dest.writeLong(downloadCounts);
		dest.writeInt(downloadSpeed);
		dest.writeLong(curVersionCode);
		dest.writeInt(isUpdateable);
		dest.writeLong(newVersionCode);
		dest.writeInt(status);
		dest.writeInt(freeFlag);
		dest.writeInt(isInFreeStore);
		dest.writeInt(isComeFrmFreeStore);
		dest.writeInt(isOccupySlot);
		dest.writeString(screenshotDirection);
		dest.writeString(freeflowDldPath);
	}

	public void readFromParcel(Parcel src) {
		appId = src.readString();
		name = src.readString();
		pkgName = src.readString();
		deviceType = src.readString();
		fileName = src.readString();
		downloadPath = src.readString();
		savedPath = src.readString();
		gameState = GameState.valueOf(src.readString());
		curVersionName = src.readString();
		newVersionName = src.readString();
		sign = src.readString();
		remoteIconUrl = src.readString();
		localIconUrl = src.readString();
		description = src.readString();
		screenshotUrl = src.readString();
		totalSize = src.readLong();
		downloadSize = src.readLong();
		createTime = src.readLong();
		downloadCounts = src.readLong();
		downloadSpeed = src.readInt();
		curVersionCode = src.readLong();
		isUpdateable = src.readInt();
		newVersionCode = src.readLong();
		status = src.readInt();
		freeFlag = src.readInt();
		isInFreeStore = src.readInt();
		isComeFrmFreeStore = src.readInt();
		isOccupySlot = src.readInt();
		screenshotDirection = src.readString();
		freeflowDldPath = src.readString();
	}

	public long getNewVersionCode() {
		return newVersionCode;
	}

	public void setNewVersionCode(long newVersionCode) {
		this.newVersionCode = newVersionCode;
	}

	public String getNewVersionName() {
		return newVersionName;
	}

	public void setNewVersionName(String newVersionName) {
		this.newVersionName = newVersionName;
	}

	public String getLocalIconUrl() {
		return localIconUrl;
	}

	public void setLocalIconUrl(String localIconUrl) {
		this.localIconUrl = localIconUrl;
	}

	// public GameManagerException getException() {
	// return mException;
	// }
	//
	// public void setException(GameManagerException exception) {
	// this.mException = exception;
	// }

	public static AppEntity wrap(String appId, String appName, String pkgName, String downloadPath, String apkSize,
			String versionCode, String versionName, String iconUrl, String md5, String pkgType) {
		AppEntity app = new AppEntity();
		app.setAppId(appId);
		app.setName(appName);
		app.setPkgName(pkgName);
		app.setDownloadPath(downloadPath);
		if (TextUtils.isDigitsOnly(apkSize)) {
			app.setTotalSize(Integer.parseInt(apkSize));
		}
		if (TextUtils.isDigitsOnly(versionCode)) {
			app.setVersionCode(Integer.parseInt(versionCode));
		}
		app.setVersionName(versionName);
		app.setRemoteIconUrl(iconUrl);
		return app;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFreeFlag() {
		return freeFlag;
	}

	public void setFreeFlag(int freeFlag) {
		this.freeFlag = freeFlag;
	}

	public List<String> getSceenshotUrlList() {
		List<String> urlList = new ArrayList<String>();
		if (!TextUtils.isEmpty(screenshotUrl)) {
			String[] picUrls = screenshotUrl.split("\\|");
			for (String string : picUrls) {
				urlList.add(string);
			}
		}
		return urlList;
	}

	public String getScreenshotUrl() {
		return screenshotUrl;
	}

	public void setScreenshotUrl(String screenshorUrl) {
		this.screenshotUrl = screenshorUrl;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getDownloadCounts() {
		return downloadCounts;
	}

	public void setDownloadCounts(long downloadCounts) {
		this.downloadCounts = downloadCounts;
	}

	public int getIsInFreeStore() {
		return isInFreeStore;
	}

	public void setIsInFreeStore(int isInFreeStore) {
		this.isInFreeStore = isInFreeStore;
	}

	public int getIsComeFrmFreeStore() {
		return isComeFrmFreeStore;
	}

	public void setIsComeFrmFreeStore(int isComeFrmFreeStore) {
		this.isComeFrmFreeStore = isComeFrmFreeStore;
	}

	public int getIsOccupySlot() {
		return isOccupySlot;
	}

	public void setIsOccupySlot(int isOccupySlot) {
		this.isOccupySlot = isOccupySlot;
	}

	public String getScreenshotDirection() {
		return screenshotDirection;
	}

	public void setScreenshotDirection(String screenshotDirection) {
		this.screenshotDirection = screenshotDirection;
	}

	public boolean isScreenshotVertical() {
		if (TextUtils.isEmpty(screenshotDirection)) {
			return false;
		}
		return VERTICAL_PIC.equals(screenshotDirection);
	}

	public String getFreeflowDldPath() {
		return freeflowDldPath;
	}

	public void setFreeflowDldPath(String freeflowDldPath) {
		this.freeflowDldPath = freeflowDldPath;
	}

	public boolean isDldPathEmpty(Context context) {
		if (GameLauncherConfig.ENABLE_FREE_FLOW && !FreeFlowManager.getInstance(context).isFreeFlowDisable()) {
			return TextUtils.isEmpty(freeflowDldPath);
		}
		return TextUtils.isEmpty(downloadPath);
	}

	public String getActualDldPath(Context context) {
		if (GameLauncherConfig.ENABLE_FREE_FLOW 
				&& !FreeFlowManager.getInstance(context).isFreeFlowDisable()
				&& !TextUtils.isEmpty(freeflowDldPath) ) {
			return freeflowDldPath;
		}
		return downloadPath;
	}

	public String getPosterIconUrl() {
		return posterIconUrl;
	}

	public void setPosterIconUrl(String posterIconUrl) {
		this.posterIconUrl = posterIconUrl;
	}

	public String getPosterBgUrl() {
		return posterBgUrl;
	}

	public void setPosterBgUrl(String posterBgUrl) {
		this.posterBgUrl = posterBgUrl;
	}
	
}
