package com.ireadygo.app.gamelauncher.appstore.info.item;

import java.util.Date;

public class UserInfoItem {
	public static final String MALE = "1";
	public static final String FEMALE = "2";
	
	private String CPhoto;
	private String SNickname;
	private String CSex;
	private String CAge;
	private String CEmail;
	private String CEmailStatus;
	private String CPhone;
	private Date DBirthday;
	private Integer iPoints;
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
	public String getCSex() {
		return CSex;
	}
	public void setCSex(String cSex) {
		CSex = cSex;
	}
	public String getCAge() {
		return CAge;
	}
	public void setCAge(String cAge) {
		CAge = cAge;
	}
	public String getCEmail() {
		return CEmail;
	}
	public void setCEmail(String cEmail) {
		CEmail = cEmail;
	}
	public String getCEmailStatus() {
		return CEmailStatus;
	}
	public void setCEmailStatus(String cEmailStatus) {
		CEmailStatus = cEmailStatus;
	}
	public String getCPhone() {
		return CPhone;
	}
	public void setCPhone(String cPhone) {
		CPhone = cPhone;
	}
	public Date getDBirthday() {
		return DBirthday;
	}
	public void setDBirthday(Date dBirthday) {
		DBirthday = dBirthday;
	}
	public Integer getiPoints() {
		return iPoints;
	}
	public void setiPoints(Integer iPoints) {
		this.iPoints = iPoints;
	}

	
}
