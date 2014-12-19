package com.snail.appstore.openapi.vo;

/**
 * 领福利地址VO
 *
 * @author gewq
 * @version 1.0 2014-6-18
 */
public class AwardUrlVO {
	
	private String CAwardUrl;// 领福利地址

	public AwardUrlVO() {
		// TODO Auto-generated constructor stub
	}

	public AwardUrlVO(String cAwardUrl) {
		super();
		CAwardUrl = cAwardUrl;
	}

	public String getCAwardUrl() {
		return CAwardUrl;
	}

	public void setCAwardUrl(String cAwardUrl) {
		CAwardUrl = cAwardUrl;
	}

}
