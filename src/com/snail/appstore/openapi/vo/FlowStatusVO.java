package com.snail.appstore.openapi.vo;

import java.sql.Timestamp;


public class FlowStatusVO {

	private String CServiceName;
	private Timestamp DEnd;
	private Boolean BFreeFlow;
	private String COrientationDomain;
	private String NPhoneNum;
	private String SOperaters;
	private String CUrl;
	private Boolean BOpened;
	private String SProvince;
	private String CType;
	public String getCServiceName() {
		return CServiceName;
	}
	public void setCServiceName(String cServiceName) {
		CServiceName = cServiceName;
	}
	public Timestamp getDEnd() {
		return DEnd;
	}
	public void setDEnd(Timestamp dEnd) {
		DEnd = dEnd;
	}
	public Boolean isBFreeFlow() {
		if (BFreeFlow == null) {
			return false;
		}
		return BFreeFlow;
	}
	public void setBFreeFlow(Boolean bFreeFlow) {
		BFreeFlow = bFreeFlow;
	}
	public String getCOrientationDomain() {
		return COrientationDomain;
	}
	public void setCOrientationDomain(String cOrientationDomain) {
		COrientationDomain = cOrientationDomain;
	}
	public String getNPhoneNum() {
		return NPhoneNum;
	}
	public void setNPhoneNum(String nPhoneNum) {
		NPhoneNum = nPhoneNum;
	}
	public String getSOperaters() {
		return SOperaters;
	}
	public void setSOperaters(String sOperaters) {
		SOperaters = sOperaters;
	}
	public String getCUrl() {
		return CUrl;
	}
	public void setCUrl(String cUrl) {
		CUrl = cUrl;
	}
	public Boolean isBOpened() {
		if (BOpened == null) {
			return false;
		}
		return BOpened;
	}
	public void setBOpened(Boolean bOpened) {
		BOpened = bOpened;
	}
	public String getSProvince() {
		return SProvince;
	}
	public void setSProvince(String sProvince) {
		SProvince = sProvince;
	}
	public String getCType() {
		return CType;
	}
	public void setCType(String cType) {
		CType = cType;
	}


}
