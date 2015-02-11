package com.ireadygo.app.gamelauncher.aidl.rentfree;

import android.os.Parcel;
import android.os.Parcelable;

public class AppTimeUploadResultItem implements Parcelable {

	/**
	 * 免租游戏时长上传结果
	 */
	private String packageName;
	private int result;
	public static final int SUCCESS = 0;
	public static final int FAIL = -1;

	public AppTimeUploadResultItem() {
	}

	public AppTimeUploadResultItem(Parcel pl) {
		packageName = pl.readString();
		result = pl.readInt();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void readFromParcel(Parcel in) {
		packageName = in.readString();
		result = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(packageName);
		dest.writeInt(result);
	}

	public static final Parcelable.Creator<AppTimeUploadResultItem> CREATOR = new Parcelable.Creator<AppTimeUploadResultItem>() {

		@Override
		public AppTimeUploadResultItem createFromParcel(Parcel source) {
			return new AppTimeUploadResultItem(source);
		}

		@Override
		public AppTimeUploadResultItem[] newArray(int size) {
			return new AppTimeUploadResultItem[size];
		}

	};
}
