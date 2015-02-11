/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ireadygo.app.gamelauncher.game.info;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherSettings;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherSettings.Favorites;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;

/**
 * Represents an item in the launcher.
 */
public class ItemInfo {

    public static final int NO_ID = -1;

    /**
     * The id in the settings database for this item
     */
    public long id = NO_ID;

    /**
     * One of {@link GameLauncherSettings.Favorites#ITEM_TYPE_APPLICATION},
     * {@link GameLauncherSettings.Favorites#ITEM_TYPE_SHORTCUT},
     * {@link GameLauncherSettings.Favorites#ITEM_TYPE_FOLDER}, or
     * {@link GameLauncherSettings.Favorites#ITEM_TYPE_APPWIDGET}.
     */
    public int itemType;

    /**
     * The id of the container that holds this item. For the desktop, this will
     * be {@link GameLauncherSettings.Favorites#CONTAINER_DESKTOP}. For the all
     * applications folder it will be {@link #NO_ID} (since it is not stored in
     * the settings DB). For user folders it will be the id of the folder.
     */
    public long container = Favorites.CONTAINER_DESKTOP;

    /**
     * Iindicates the screen in which the shortcut appears.
     */
    public long screenId = 0;

    public int cellSortId = -1;

    /**
     * Indicates the X cell span.
     */
    public int spanX = 1;

    /**
     * Indicates the Y cell span.
     */
    public int spanY = 1;

    /**
     * Indicates the minimum X cell span.
     */
    int minSpanX = 1;

    /**
     * Indicates the minimum Y cell span.
     */
    int minSpanY = 1;

    /**
     * Indicates that this item needs to be updated in the db
     */
    public boolean requiresDbUpdate = false;

    /**
     * Title of the item
     */
    public CharSequence title;

    /**
     * The position of the item in a drag-and-drop operation.
     */
    public int[] dropPos = null;

    public Bitmap appIcon;
    
    public String packageName;
    
    /**
     * default type app
     */
    public int appType = 0;
    
    public boolean isSystemApp;
    
    public boolean isVisiable = true;

    public int displayMode = 0;

    public ItemInfo() {
    }

    ItemInfo(ItemInfo info) {
        id = info.id;
        spanX = info.spanX;
        spanY = info.spanY;
        screenId = info.screenId;
        itemType = info.itemType;
        container = info.container;
    }

    public Intent getIntent() {
//        throw new RuntimeException("Unexpected Intent");
        return null;
    }

    /**
     * Write the fields of this item to the DB
     * 
     * @param values
     */
    public void onAddToDatabase(ContentValues values) {
        values.put(GameLauncherSettings.BaseLauncherColumns.ITEM_TYPE, itemType);
        values.put(GameLauncherSettings.Favorites.CONTAINER, container);
        values.put(GameLauncherSettings.Favorites.SCREEN, screenId);
        values.put(GameLauncherSettings.Favorites.CELL_SORT_ID, cellSortId);
        values.put(GameLauncherSettings.Favorites.SPANX, spanX);
        values.put(GameLauncherSettings.Favorites.SPANY, spanY);
        values.put(GameLauncherSettings.Favorites.APP_TYPE, appType);
    }

    public void updateValuesWithSortId(ContentValues values, int sortId) {
        if (sortId >= 0) {
            values.put(GameLauncherSettings.Favorites.CELL_SORT_ID, sortId);
        }
    }

    public static byte[] flattenBitmap(Bitmap bitmap) {
        // Try go guesstimate how much space the icon will take when serialized
        // to avoid unnecessary allocations/copies during the write.
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            Log.w("Favorite", "Could not write icon");
            return null;
        }
    }

    public static void writeBitmap(ContentValues values, Bitmap bitmap) {
        if (bitmap != null) {
        	int iconWidth = GameLauncherApplication.getApplication().getResources().getDimensionPixelSize(R.dimen.mygame_game_width);
            int iconHeigth = GameLauncherApplication.getApplication().getResources().getDimensionPixelSize(R.dimen.mygame_game_height);
            if (bitmap.getHeight() > iconHeigth || bitmap.getWidth() > iconWidth) {
            	bitmap = PictureUtil.zoomImage(bitmap, iconWidth, iconHeigth);
            }
            byte[] data = flattenBitmap(bitmap);
            values.put(GameLauncherSettings.Favorites.ICON, data);
        }
    }

    /**
     * It is very important that sub-classes implement this if they contain any
     * references to the activity (anything in the view hierarchy etc.). If not,
     * leaks can result since ItemInfo objects persist across rotation and can
     * hence leak by holding stale references to the old view hierarchy /
     * activity.
     */
    public void unbind() {
    }

    public int getCellSortId() {
        return cellSortId;
    }

    public void setCellSortId(int cellSortId) {
        this.cellSortId = cellSortId;
    }

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public Bitmap getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Bitmap appIcon) {
        this.appIcon = appIcon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ItemInfo)) {
            return false;
        }
        if(null == packageName){
        	return false;
        }
        ItemInfo lhs = (ItemInfo) o;
        return packageName.equals(lhs.packageName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
        return result;
    }
    
    @Override
	public String toString() {
		return "ItemInfo [id=" + id + ", itemType=" + itemType + ", container=" + container + ", screenId=" + screenId
				+ ", cellSortId=" + cellSortId + ", spanX=" + spanX + ", spanY=" + spanY + ", minSpanX=" + minSpanX
				+ ", minSpanY=" + minSpanY + ", requiresDbUpdate=" + requiresDbUpdate + ", title=" + title
				+ ", dropPos=" + Arrays.toString(dropPos) + ", appIcon=" + appIcon + ", packageName=" + packageName
				+ ", appType=" + appType + "]";
	}

}
