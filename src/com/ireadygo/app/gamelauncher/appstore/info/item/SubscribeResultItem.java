package com.ireadygo.app.gamelauncher.appstore.info.item;

import java.sql.Timestamp;

public class SubscribeResultItem {

//    “dOpen” : "2014-06-26 00:00:00" ,  开通时间
//    "dEnd": "2014-06-26 00:00:00" ,    结束时间
//    "nPhoneNum": "13009327993",     手机类型
//    "cType": "2"免流量类型 开通类型，0：流量池，1：流量包页面，2：流量包接口，3：代理，4：流量池、流量包混合

	private Timestamp mOpenDate;
	private Timestamp mEndDate;
	private String mPhoneNum;
	private String mFreeFlowType;
	public Timestamp getmOpenDate() {
		return mOpenDate;
	}
	public void setmOpenDate(Timestamp mOpenDate) {
		this.mOpenDate = mOpenDate;
	}
	public Timestamp getmEndDate() {
		return mEndDate;
	}
	public void setmEndDate(Timestamp mEndDate) {
		this.mEndDate = mEndDate;
	}
	public String getmPhoneNum() {
		return mPhoneNum;
	}
	public void setmPhoneNum(String mPhoneNum) {
		this.mPhoneNum = mPhoneNum;
	}
	public String getmFreeFlowType() {
		return mFreeFlowType;
	}
	public void setmFreeFlowType(String mFreeFlowType) {
		this.mFreeFlowType = mFreeFlowType;
	}
}
