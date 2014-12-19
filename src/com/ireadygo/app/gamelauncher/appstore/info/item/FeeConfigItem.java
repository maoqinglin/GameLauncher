package com.ireadygo.app.gamelauncher.appstore.info.item;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class FeeConfigItem implements Serializable, Parcelable {

	private static final long serialVersionUID = 6508210202804309509L;

	private long NMobileFeeId;
	private String SMobileFeeName;
	private String CMobileFeeType;
	private int IMobileFeeMoney;
	private int ILimit;
	private String SInfo;

	public long getNMobileFeeId() {
		return NMobileFeeId;
	}

	public void setNMobileFeeId(long nMobileFeeId) {
		NMobileFeeId = nMobileFeeId;
	}

	public String getSMobileFeeName() {
		return SMobileFeeName;
	}

	public void setSMobileFeeName(String sMobileFeeName) {
		SMobileFeeName = sMobileFeeName;
	}

	public String getCMobileFeeType() {
		return CMobileFeeType;
	}

	public void setCMobileFeeType(String cMobileFeeType) {
		CMobileFeeType = cMobileFeeType;
	}

	public int getIMobileFeeMoney() {
		return IMobileFeeMoney;
	}

	public void setIMobileFeeMoney(int iMobileFeeMoney) {
		IMobileFeeMoney = iMobileFeeMoney;
	}

	public int getILimit() {
		return ILimit;
	}

	public void setILimit(int iLimit) {
		ILimit = iLimit;
	}

	public String getSInfo() {
		return SInfo;
	}

	public void setSInfo(String sInfo) {
		SInfo = sInfo;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<FeeConfigItem> CREATOR = new Creator<FeeConfigItem>() {
		public FeeConfigItem createFromParcel(Parcel source) {
			FeeConfigItem feeConfigItem = new FeeConfigItem();
			feeConfigItem.CMobileFeeType = source.readString();
			feeConfigItem.ILimit = source.readInt();
			feeConfigItem.IMobileFeeMoney = source.readInt();
			feeConfigItem.NMobileFeeId = source.readLong();
			feeConfigItem.SInfo = source.readString();
			feeConfigItem.SMobileFeeName = source.readString();
			return feeConfigItem;
		}

		public FeeConfigItem[] newArray(int size) {
			return new FeeConfigItem[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(CMobileFeeType);
		dest.writeInt(ILimit);
		dest.writeInt(IMobileFeeMoney);
		dest.writeLong(NMobileFeeId);
		dest.writeString(SInfo);
		dest.writeString(SMobileFeeName);
	}

}
