package com.snail.appstore.openapi.vo;

/**
 * 魔奇卡槽配置
 * @author gewq
 * @version 1.0 2014-7-17
 */
public class MuchSlotConfigVO {
	/**卡槽类型 0 月 1年**/
	public final static String SLOT_MONTH_TYPE = "0";
	public final static String SLOT_YEAR_TYPE = "1";
	
	private Integer ISlotConfigId; // 卡槽配置ID
	private Integer ISlotAmount; // 卡槽数量
	private Integer ISlotPrice; // 卡槽价格
	private Integer ITimeUnit; // 时间单位
	private String CSlotType; // 卡槽类型 0 月 1年

	public Integer getISlotConfigId() {
		if (ISlotConfigId == null) {
			return 0;
		}
		return ISlotConfigId;
	}

	public void setISlotConfigId(Integer iSlotConfigId) {
		ISlotConfigId = iSlotConfigId;
	}

	public Integer getISlotAmount() {
		if (ISlotAmount == null) {
			return 0;
		}
		return ISlotAmount;
	}

	public void setISlotAmount(Integer iSlotAmount) {
		ISlotAmount = iSlotAmount;
	}

	public Integer getISlotPrice() {
		if (ISlotPrice == null) {
			return 0;
		}
		return ISlotPrice;
	}

	public void setISlotPrice(Integer iSlotPrice) {
		ISlotPrice = iSlotPrice;
	}

	public String getCSlotType() {
		return CSlotType;
	}

	public void setCSlotType(String cSlotType) {
		CSlotType = cSlotType;
	}

	public Integer getITimeUnit() {
		if (ITimeUnit == null) {
			return 0;
		}
		return ITimeUnit;
	}

	public void setITimeUnit(Integer iTimeUnit) {
		ITimeUnit = iTimeUnit;
	}

}
