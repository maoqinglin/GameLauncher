package com.snail.appstore.openapi.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class AppTimeUploadVO implements Parcelable {

	/**
	 * 免租游戏时长上传VO
	 */
	private String packageName;
	private long playingTime;

	public AppTimeUploadVO() {
	}

	public AppTimeUploadVO(Parcel pl) {
		packageName = pl.readString();
		playingTime = pl.readLong();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public long getPlayingTime() {
		return playingTime;
	}

	public void setPlayingTime(long playingTime) {
		this.playingTime = playingTime;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void readFromParcel(Parcel in) {
		packageName = in.readString();
		playingTime = in.readLong();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(packageName);
		dest.writeLong(playingTime);
	}

	public static final Parcelable.Creator<AppTimeUploadVO> CREATOR = new Parcelable.Creator<AppTimeUploadVO>() {

		@Override
		public AppTimeUploadVO createFromParcel(Parcel source) {
			return new AppTimeUploadVO(source);
		}

		@Override
		public AppTimeUploadVO[] newArray(int size) {
			return new AppTimeUploadVO[size];
		}

	};
}
