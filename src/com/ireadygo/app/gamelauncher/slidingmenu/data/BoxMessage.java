package com.ireadygo.app.gamelauncher.slidingmenu.data;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class BoxMessage implements Parcelable, Serializable{

	private static final long serialVersionUID = -455484200731752322L;

	public static final int HAS_NOT_READ = 0;

	public static final int HAS_READ = 1;

	public int id;

	public String title;

	public String pkgName;

	public long time;
	
	public int isRead = HAS_NOT_READ;

	public String tag = "";
	
	public Bitmap icon;

	public BoxMessage() {}

	public BoxMessage(Parcel src) {
		readFromParcel(src);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeString(pkgName);
		dest.writeLong(time);
		dest.writeInt(isRead);
		dest.writeString(tag);
		dest.writeParcelable(icon, flags);
	}
	
	public void readFromParcel(Parcel src) {
		id = src.readInt();
		title = src.readString();
		pkgName = src.readString();
		time = src.readLong();
		isRead = src.readInt();
		tag = src.readString();
		icon = src.readParcelable(Bitmap.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<BoxMessage> CREATOR = new Parcelable.Creator<BoxMessage>() {
		public BoxMessage createFromParcel(Parcel src) {
			return new BoxMessage(src);
		}

		public BoxMessage[] newArray(int size) {
			return new BoxMessage[size];
		}
	};
}
