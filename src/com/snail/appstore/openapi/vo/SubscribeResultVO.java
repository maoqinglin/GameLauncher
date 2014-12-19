package com.snail.appstore.openapi.vo;

import java.sql.Timestamp;

public class SubscribeResultVO {

	// “dOpen” : "2014-06-26 00:00:00" , 开通时间
	// "dEnd": "2014-06-26 00:00:00" , 结束时间
	// "nPhoneNum": "13009327993", 手机类型
	// "cType": "2"免流量类型 开通类型，0：流量池，1：流量包页面，2：流量包接口，3：代理，4：流量池、流量包混合

	private Timestamp DOpen;
	private Timestamp DEnd;
	private String NPhoneNum;
	private String CType;
	public Timestamp getDOpen() {
		return DOpen;
	}
	public void setDOpen(Timestamp dOpen) {
		DOpen = dOpen;
	}
	public Timestamp getDEnd() {
		return DEnd;
	}
	public void setDEnd(Timestamp dEnd) {
		DEnd = dEnd;
	}
	public String getNPhoneNum() {
		return NPhoneNum;
	}
	public void setNPhoneNum(String nPhoneNum) {
		NPhoneNum = nPhoneNum;
	}
	public String getCType() {
		return CType;
	}
	public void setCType(String cType) {
		CType = cType;
	}


}
