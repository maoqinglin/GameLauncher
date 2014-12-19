package com.snail.appstore.openapi.vo;

public class RechargePhoneVO {

	private Long COrderId;
	private Integer ICurrency;
	private Integer IRabbitTicketMoney;
	private Integer IDiffCurrency;

	public Long getCOrderId() {
		if (COrderId == null) {
			return 0L;
		}
		return COrderId;
	}

	public void setCOrderId(Long cOrderId) {
		COrderId = cOrderId;
	}

	public Integer getICurrency() {
		if (ICurrency == null) {
			return 0;
		}
		return ICurrency;
	}

	public void setICurrency(Integer iCurrency) {
		ICurrency = iCurrency;
	}

	public Integer getIRabbitTicketMoney() {
		if (IRabbitTicketMoney == null) {
			return 0;
		}
		return IRabbitTicketMoney;
	}

	public void setIRabbitTicketMoney(Integer iRabbitTicketMoney) {
		IRabbitTicketMoney = iRabbitTicketMoney;
	}

	public Integer getIDiffCurrency() {
		if (IDiffCurrency == null) {
			return 0;
		}
		return IDiffCurrency;
	}

	public void setIDiffCurrency(Integer iDiffCurrency) {
		IDiffCurrency = iDiffCurrency;
	}

}
