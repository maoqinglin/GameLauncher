package com.snail.appstore.openapi.vo;

import java.util.Date;

public class PreLoadItemVO {

	private Date DCreate;
	private Integer IPlatformId;
    private Date DUpdate;
    private String CDelFlag;
    private String SInfo;//简述
    private String CStatus;//状态: 0, 无效;1, 有效
    private Long NPreinstallId;//预装ID
    private Integer ISortValue;
	private Long NAppId;//游戏ID
	private String SAppName;//游戏名称
	private String CPosterIcon;// 海报图标
	private String CPosterPic;// 海报图片

	public PreLoadItemVO() {
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

	public String getSAppName() {
		return SAppName;
	}

	public void setSAppName(String sAppName) {
		SAppName = sAppName;
	}

	public Date getDCreate() {
		if(DCreate == null){
			return new Date();
		}
		return DCreate;
	}

	public void setDCreate(Date dCreate) {
		DCreate = dCreate;
	}

	public Integer getIPlatformId() {
		if(IPlatformId == null){
			return 0;
		}
		return IPlatformId;
	}

	public void setIPlatformId(Integer iPlatformId) {
		IPlatformId = iPlatformId;
	}

	public Date getDUpdate() {
		if(DUpdate == null){
			return new Date();
		}
		return DUpdate;
	}

	public void setDUpdate(Date dUpdate) {
		DUpdate = dUpdate;
	}

	public String getCDelFlag() {
		return CDelFlag;
	}

	public void setCDelFlag(String cDelFlag) {
		CDelFlag = cDelFlag;
	}

	public String getSInfo() {
		return SInfo;
	}

	public void setSInfo(String sInfo) {
		SInfo = sInfo;
	}

	public String getCStatus() {
		return CStatus;
	}

	public void setCStatus(String cStatus) {
		CStatus = cStatus;
	}

	public Long getNPreinstallId() {
		if(NPreinstallId == null){
			return 0L;
		}
		return NPreinstallId;
	}

	public void setNPreinstallId(Long nPreinstallId) {
		NPreinstallId = nPreinstallId;
	}

	public Integer getISortValue() {
		if(ISortValue == null){
			return 0;
		}
		return ISortValue;
	}

	public void setISortValue(Integer iSortValue) {
		ISortValue = iSortValue;
	}

	public String getCPosterIcon() {
		return CPosterIcon;
	}

	public void setCPosterIcon(String cPosterIcon) {
		CPosterIcon = cPosterIcon;
	}

	public String getCPosterPic() {
		return CPosterPic;
	}

	public void setCPosterPic(String cPosterPic) {
		CPosterPic = cPosterPic;
	}
}

