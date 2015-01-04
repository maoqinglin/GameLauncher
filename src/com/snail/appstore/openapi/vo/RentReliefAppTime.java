package com.snail.appstore.openapi.vo;

public class RentReliefAppTime {

	/**
	 * 获取游戏时长VO
	 */
	private Long NAppTime;// 已玩游戏时间(秒)
	private Long NAppRemainTime;// 剩免租金时间(秒)
	private String CRenewalMoney;//租金

	public Long getNAppTime() {
		if(NAppTime == null){
			return 0L;
		}
		return NAppTime;
	}

	public void setNAppTime(Long nAppTime) {
		NAppTime = nAppTime;
	}

	public Long getNAppRemainTime() {
		if(NAppRemainTime == null){
			return 0L;
		}
		return NAppRemainTime;
	}

	public void setNAppRemainTime(Long nAppRemainTime) {
		NAppRemainTime = nAppRemainTime;
	}

	public String getCRenewalMoney() {
		return CRenewalMoney;
	}

	public void setCRenewalMoney(String cRenewalMoney) {
		CRenewalMoney = cRenewalMoney;
	}
}
