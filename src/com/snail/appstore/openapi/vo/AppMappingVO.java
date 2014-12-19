package com.snail.appstore.openapi.vo;

/**
 * 游戏匹配VO
 * @author gewq
 * @version 1.0 2014-6-24
 */
public class AppMappingVO {

	private Long NAppId; // 游戏ID
	private String SGameName; // 游戏名称
	private String CIcon; // 游戏图标

	public AppMappingVO() {
		// TODO Auto-generated constructor stub
	}

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

	public String getCIcon() {
		return CIcon;
	}

	public void setCIcon(String cIcon) {
		CIcon = cIcon;
	}

}
