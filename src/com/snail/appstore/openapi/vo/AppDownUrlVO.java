package com.snail.appstore.openapi.vo;

/**
 * APP下载地址VO
 *
 * @author gewq
 * @version 1.0 2014-6-12
 */
public class AppDownUrlVO {
	
	private Long NAppId; // 游戏ID
	private String SGameName; // 游戏名称
	private String CDownloadUrl; // 下载地址
	
	public Long getNAppId() {
		if (NAppId == null) {
			return 0L;
		}
		return NAppId;
	}
	public void setNAppId(Long nAppId) {
		NAppId = nAppId;
	}
	public String getSGameName() {
		return SGameName;
	}
	public void setSGameName(String sGameName) {
		SGameName = sGameName;
	}
	public String getCDownloadUrl() {
		return CDownloadUrl;
	}
	public void setCDownloadUrl(String cDownloadUrl) {
		CDownloadUrl = cDownloadUrl;
	}
	
	
	
}
