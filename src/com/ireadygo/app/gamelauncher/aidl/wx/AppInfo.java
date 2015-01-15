package com.ireadygo.app.gamelauncher.aidl.wx;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Parcelable, Serializable {

	private static final long serialVersionUID = -7014295264154025629L;
	private String mAppId;
	private String mAppName;
	private String mIconUrl;
	private String mVersionName;
	private long mVersionCode;
	private String mPkgName;
	private long mTotalSize;
	private int mFlowfreeFlag;
	private String mMD5;
	private int mGameStatus;
	private String mDesc;
	private String mPicUrl;
	private String mDownloadUrl;
	private long mDownloadCount;

	public AppInfo() {}

	private AppInfo(Parcel src) {
		readFromParcel(src);
	}

	public String getAppId() {
		return mAppId;
	}

	public void setAppId(String appId) {
		mAppId = appId;
	}

	public String getPkgName() {
		return mPkgName;
	}

	public void setPkgName(String pkgName) {
		mPkgName = pkgName;
	}

	public String getAppName() {
		return mAppName;
	}

	public void setAppName(String appName) {
		mAppName = appName;
	}

	public long getTotalSize() {
		return mTotalSize;
	}

	public void setTotalSize(long size) {
		mTotalSize = size;
	}

	public String getIconUrl() {
		return mIconUrl;
	}

	public void setPicUrl(String iconUrl) {
		mPicUrl = iconUrl;
	}

	public String getPicUrl() {
		return mPicUrl;
	}

	public void setIconUrl(String iconUrl) {
		mIconUrl = iconUrl;
	}

	public String getDownloadUrl() {
		return mDownloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		mDownloadUrl = downloadUrl;
	}

	public String getVersionName() {
		return mVersionName;
	}

	public void setVersionName(String versionName) {
		mVersionName = versionName;
	}

	public long getVersionCode() {
		return mVersionCode;
	}

	public void setVersionCode(long versionCode) {
		mVersionCode = versionCode;
	}

	public int getFlowfreeFlag() {
		return mFlowfreeFlag;
	}

	public void setFlowfreeFlag(int flowfreeFlag) {
		mFlowfreeFlag = flowfreeFlag;
	}

	public int getGameStatus() {
		return mGameStatus;
	}

	public void setGameStatus(int gameStatus) {
		mGameStatus = gameStatus;
	}

	public String getMD5() {
		return mMD5;
	}

	public void setMD5(String mD5) {
		mMD5 = mD5;
	}

	public long getDownloadCount() {
		return mDownloadCount;
	}

	public void setDownloadCount(long downloadCount) {
		mDownloadCount = downloadCount;
	}

	public String getDesc() {
		return mDesc;
	}

	public void setDesc(String desc) {
		mDesc = desc;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mAppId);
		dest.writeString(mPkgName);
		dest.writeString(mAppName);
		dest.writeLong(mTotalSize);
		dest.writeString(mIconUrl);
		dest.writeString(mDownloadUrl);
		dest.writeString(mVersionName);
		dest.writeLong(mVersionCode);
		dest.writeInt(mFlowfreeFlag);
		dest.writeInt(mGameStatus);
		dest.writeString(mMD5);
		dest.writeLong(mDownloadCount);
		dest.writeString(mDesc);
		dest.writeString(mPicUrl);
	}

	public void readFromParcel(Parcel src) {
		mAppId = src.readString();
		mPkgName = src.readString();
		mAppName = src.readString();
		mTotalSize = src.readLong();
		mIconUrl = src.readString();
		mDownloadUrl = src.readString();
		mVersionName = src.readString();
		mVersionCode = src.readLong();
		mFlowfreeFlag = src.readInt();
		mGameStatus = src.readInt();
		mMD5 = src.readString();
		mDownloadCount = src.readLong();
		mDesc = src.readString();
		mPicUrl = src.readString();
	}

	public static final Parcelable.Creator<AppInfo> CREATOR = new Parcelable.Creator<AppInfo>() {
		public AppInfo createFromParcel(Parcel src) {
			return new AppInfo(src);
		}

		public AppInfo[] newArray(int size) {
			return new AppInfo[size];
		}
	};
}
