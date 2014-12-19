package com.ireadygo.app.gamelauncher.appstore.info.item;

public class SlotConfigItem {
	public static final String SLOT_MONTH_TYPE = "0";
	public static final String SLOT_YEAR_TYPE = "1";

	private Integer ISlotConfigId;
	private Integer ISlotAmount;
	private Integer ISlotPrice;
	private Integer ITimeUnit;
	private String CSlotType;

	public Integer getISlotConfigId() {
		return ISlotConfigId;
	}

	public void setISlotConfigId(Integer iSlotConfigId) {
		ISlotConfigId = iSlotConfigId;
	}

	public Integer getISlotAmount() {
		return ISlotAmount;
	}

	public void setISlotAmount(Integer iSlotAmount) {
		ISlotAmount = iSlotAmount;
	}

	public Integer getISlotPrice() {
		return ISlotPrice;
	}

	public void setISlotPrice(Integer iSlotPrice) {
		ISlotPrice = iSlotPrice;
	}

	public Integer getITimeUnit() {
		return ITimeUnit;
	}

	public void setITimeUnit(Integer iTimeUnit) {
		ITimeUnit = iTimeUnit;
	}

	public String getCSlotType() {
		return CSlotType;
	}

	public void setCSlotType(String cSlotType) {
		CSlotType = cSlotType;
	}

}
