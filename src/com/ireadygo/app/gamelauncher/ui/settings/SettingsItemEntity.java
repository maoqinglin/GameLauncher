package com.ireadygo.app.gamelauncher.ui.settings;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class SettingsItemEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Drawable itemIcon;

	private String itemName;
	
	private String intentAction;
	
	public SettingsItemEntity(Drawable itemIcon, String itemName, String intentAction) {
		super();
		this.itemIcon = itemIcon;
		this.itemName = itemName;
		this.intentAction = intentAction;
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
	
	public String getIntentAction() {
		return intentAction;
	}

	public void setIntentAction(String intentAction) {
		this.intentAction = intentAction;
	}
}
