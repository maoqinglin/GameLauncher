package com.ireadygo.app.gamelauncher.ui.gamecommunity;

import java.io.Serializable;

import android.graphics.Bitmap;

public class Announcement implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Bitmap poster;
	public Bitmap getPoster() {
		return poster;
	}
	public void setPoster(Bitmap poster) {
		this.poster = poster;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	private String content;
	private String reportDate;

}
