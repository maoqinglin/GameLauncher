package com.snail.appstore.openapi.vo;

public class SlotRechargeVO {

	private Integer ISlotAmount = 0;
	private Integer ILimitTime = 0;

	public Integer getISlotAmount() {
		if (ISlotAmount == null) {
			return 0;
		}
		return ISlotAmount;
	}
	public void setISlotAmount(Integer slotAmount) {
		ISlotAmount = slotAmount;
	}

	public Integer getILimitTime() {
		if (ILimitTime == null) {
			return 0;
		}
		return ILimitTime;
	}

	public void setILimitTime(Integer limitTime) {
		ILimitTime = limitTime;
	}


}
