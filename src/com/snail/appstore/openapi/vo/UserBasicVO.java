package com.snail.appstore.openapi.vo;

import java.util.Date;

public class UserBasicVO {

	private String CPhoto; // 用户头像
	private String SNickname; // 昵称
	private String CSex; // 性别，1：男，2：女
	private String CAge; // 年龄，1：16-20，2：21-25，3：26-30，4：31-35，5：36-40，6：41-45，7：46-50，8：51-55，9：56-60
	private String CEmail; // 邮箱
	private String CEmailStatus; // 箱邮绑定状态，0：未绑定，1：绑定
	private String CPhone; // 绑定的手机号码
	private Date DBirthday; // 生日
	private Integer IPoints; // 积分


	public Date getDBirthday() {
		return DBirthday;
	}

	public void setDBirthday(Date dBirthday) {
		DBirthday = dBirthday;
	}

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

	public Integer getIPoints() {
		if (IPoints == null) {
			return 0;
		}
		return IPoints;
	}
	
	public void setIPoints(Integer iPoints) {
		IPoints = iPoints;
	}
	
}
