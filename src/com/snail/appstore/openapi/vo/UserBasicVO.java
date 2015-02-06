package com.snail.appstore.openapi.vo;

import java.util.Date;

public class UserBasicVO {

	private Date DBirthday; // 生日
	private String CSex; // 性别，1：男，2：女
	private String CImsi; // 移动用户识别码
	private Date DUpdate; // 更新时间
	private Date DCreate; // 生成时间
	private Long NUserId;// 用户ID
	private String CAccount; // 用户账号
	private Integer IIntegral; // 积分
	private Integer IMoney; // 蜗牛币
	private String CPhoto; // 用户头像
	private String SNickname; // 昵称
	private String CPhone; // 绑定的手机号码

	public String getCPhoto() {
		return CPhoto;
	}

	public void setCPhoto(String cPhoto) {
		CPhoto = cPhoto;
	}

	public String getSNickname() {
		return SNickname;
	}

	public void setSNickname(String sNickname) {
		SNickname = sNickname;
	}

	public String getCPhone() {
		return CPhone;
	}

	public void setCPhone(String cPhone) {
		CPhone = cPhone;
	}

	public Long getNUserId() {
		if (NUserId == null) {
			return 0L;
		}
		return NUserId;
	}

	public void setNUserId(Long nUserId) {
		NUserId = nUserId;
	}

	public String getCAccount() {
		return CAccount;
	}

	public void setCAccount(String cAccount) {
		CAccount = cAccount;
	}

	public Integer getIIntegral() {
		if (IIntegral == null) {
			return 0;
		}
		return IIntegral;
	}

	public void setIIntegral(Integer iIntegral) {
		IIntegral = iIntegral;
	}

	public Integer getIMoney() {
		if (IMoney == null) {
			return 0;
		}
		return IMoney;
	}

	public void setIMoney(Integer iMoney) {
		IMoney = iMoney;
	}

	public Date getDBirthday() {
		if (DBirthday == null) {
			return new Date();
		}
		return DBirthday;
	}

	public void setDBirthday(Date dBirthday) {
		DBirthday = dBirthday;
	}

	public String getCSex() {
		return CSex;
	}

	public void setCSex(String cSex) {
		CSex = cSex;
	}

	public String getCImsi() {
		return CImsi;
	}

	public void setCImsi(String cImsi) {
		CImsi = cImsi;
	}

	public Date getDUpdate() {
		if (DUpdate == null) {
			return new Date();
		}
		return DUpdate;
	}

	public void setDUpdate(Date dUpdate) {
		DUpdate = dUpdate;
	}

	public Date getDCreate() {
		if (DCreate == null) {
			return new Date();
		}
		return DCreate;
	}

	public void setDCreate(Date dCreate) {
		DCreate = dCreate;
	}

}
