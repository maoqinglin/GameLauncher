package com.snail.appstore.openapi.vo;

public class FeeConfigVO {

	private Long NMobileFeeId;
	private String SMobileFeeName;
	private String CMobileFeeType;
	private Integer IMobileFeeMoney;
	private Integer ILimit;
	private String SInfo;

	public Long getNMobileFeeId() {
		if (NMobileFeeId == null) {
			return 0L;
		}
		return NMobileFeeId;
	}

	public void setNMobileFeeId(Long nMobileFeeId) {
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

	public Integer getIMobileFeeMoney() {
		if (IMobileFeeMoney == null) {
			return 0;
		}
		return IMobileFeeMoney;
	}

	public void setIMobileFeeMoney(Integer iMobileFeeMoney) {
		IMobileFeeMoney = iMobileFeeMoney;
	}

	public Integer getILimit() {
		if (ILimit == null) {
			return 0;
		}
		return ILimit;
	}

	public void setILimit(Integer iLimit) {
		ILimit = iLimit;
	}

	public String getSInfo() {
		return SInfo;
	}

	public void setSInfo(String sInfo) {
		SInfo = sInfo;
	}

}
