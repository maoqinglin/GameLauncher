package com.ireadygo.app.gamelauncher.ui.settings;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class SettingsInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Drawable itemBg;
	
	private Drawable itemIcon;

	private String itemName;
	
	private String tip;

	private String intentAction;

	public SettingsInfo(){

	}

	public SettingsInfo(Drawable itemBg, Drawable itemIcon, String itemName, String tip, String intentAction) {
		super();
		this.itemBg = itemBg;
		this.itemIcon = itemIcon;
		this.itemName = itemName;
		this.tip = tip;
		this.intentAction = intentAction;
	}

	public Drawable getItemBg() {
		return itemBg;
	}

	public void setItemBg(Drawable itemBg) {
		this.itemBg = itemBg;
	}

	public Drawable getItemIcon() {
		return itemIcon;
	}

	public void setItemIcon(Drawable itemIcon) {
		this.itemIcon = itemIcon;
	}

	
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getIntentAction() {
		return intentAction;
	}

	public void setIntentAction(String intentAction) {
		this.intentAction = intentAction;
	}
}
