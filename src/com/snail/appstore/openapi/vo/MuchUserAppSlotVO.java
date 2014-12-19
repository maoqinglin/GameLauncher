package com.snail.appstore.openapi.vo;

import java.sql.Timestamp;


/**
 * 魔奇用户应用卡槽VO
 * 
 * @author gewq
 * @version 1.0 2014-7-17
 */
public class MuchUserAppSlotVO {

	private Long NUserId; // 用户ID
	private String CPackage; // 包名
	private Timestamp DOpen; // 开通时间
	private Timestamp DExpire; // 到期时间

	public Long getNUserId() {
		if (NUserId == null) {
			return 0L;
		}
		return NUserId;
	}

	public void setNUserId(Long nUserId) {
		NUserId = nUserId;
	}

	public String getCPackage() {
		return CPackage;
	}

	public void setCPackage(String cPackage) {
		CPackage = cPackage;
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
}
