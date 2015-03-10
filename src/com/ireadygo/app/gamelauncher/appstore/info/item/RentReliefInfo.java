package com.ireadygo.app.gamelauncher.appstore.info.item;

public class RentReliefInfo {

	private Long appTime;
	private Long appRemainTime;
	private Long targetTime;//目标时间(秒)
	private String renewalMoney;
	private String expirationDate;//租期到期时间
	private Integer remainExpirationMonth;//租期到期时间剩余月数

	public Long getAppTime() {
		return appTime;
	}

	public void setAppTime(Long appTime) {
		this.appTime = appTime;
	}

	public Long getAppRemainTime() {
		return appRemainTime;
	}

	public void setAppRemainTime(Long appRemainTime) {
		this.appRemainTime = appRemainTime;
	}

	public String getRenewalMoney() {
		return renewalMoney;
	}

	public void setRenewalMoney(String renewalMoney) {
		this.renewalMoney = renewalMoney;
	}

	public Long getTargetTime() {
		return targetTime;
	}

	public void setTargetTime(Long targetTime) {
		this.targetTime = targetTime;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Integer getRemainExpirationMonth() {
		return remainExpirationMonth;
	}

	public void setRemainExpirationMonth(Integer remainExpirationMonth) {
		this.remainExpirationMonth = remainExpirationMonth;
	}

}
