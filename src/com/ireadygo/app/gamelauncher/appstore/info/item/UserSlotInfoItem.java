package com.ireadygo.app.gamelauncher.appstore.info.item;

import java.sql.Timestamp;


public class UserSlotInfoItem {

	private long NUserId;
	private String CPackage;
	private int ISlotSeqNum;
	private Timestamp DOpen;
	private Timestamp DExpire;
	private long ReqTime;//服务器返回的请求时间
	private int slotNum;

	public long getNUserId() {
		return NUserId;
	}
	public void setNUserId(long nUserId) {
		NUserId = nUserId;
	}
	public String getCPackage() {
		return CPackage;
	}
	public void setCPackage(String cPackage) {
		CPackage = cPackage;
	}
	public int getISlotSeqNum() {
		return ISlotSeqNum;
	}
	public void setISlotSeqNum(int iSlotSeqNum) {
		ISlotSeqNum = iSlotSeqNum;
	}
	public Timestamp getDOpen() {
		return DOpen;
	}
	public void setDOpen(Timestamp dOpen) {
		DOpen = dOpen;
	}
	public Timestamp getDExpire() {
		return DExpire;
	}
	public void setDExpire(Timestamp dExpire) {
		DExpire = dExpire;
	}
	public long getReqTime() {
		return ReqTime;
	}
	public void setReqTime(long reqTime) {
		ReqTime = reqTime;
	}
	public int getSlotNum() {
		return slotNum;
	}
	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}

	
}
