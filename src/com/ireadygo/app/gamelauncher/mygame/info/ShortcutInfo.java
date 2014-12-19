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

package com.ireadygo.app.gamelauncher.mygame.info;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.util.Log;

import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherSettings;
import com.ireadygo.app.gamelauncher.mygame.utils.IconCache;

/**
 * Represents a launchable icon on the workspaces and in folders.
 */
public class ShortcutInfo extends ItemInfo {

    /**
     * The intent used to start the application.
     */
    public Intent intent;

    /**
     * Indicates whether the icon comes from an application's resource (if false)
     * or from a custom Bitmap (if true.)
     */
    public boolean customIcon;

    /**
     * Indicates whether we're using the default fallback icon instead of something from the
     * app.
     */
    public boolean usingFallbackIcon;

    /**
     * If isShortcut=true and customIcon=false, this contains a reference to the
     * shortcut icon as an application's resource.
     */
    public Intent.ShortcutIconResource iconResource;

    /**
     * The application icon.
     */
    private Bitmap mIcon;

    long firstInstallTime;
    public int flags = 0;

    public ShortcutInfo() {
//        itemType = GameLauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
    }

    public Intent getIntent() {
        return intent;
    }
    
    public ShortcutInfo(Context context, ShortcutInfo info) {
        super(info);
        setTitle(info.getTitle().toString());
        intent = new Intent(info.intent);
        if (info.iconResource != null) {
            iconResource = new Intent.ShortcutIconResource();
            iconResource.packageName = info.iconResource.packageName;
            iconResource.resourceName = info.iconResource.resourceName;
        }
        mIcon = info.mIcon; // TODO: should make a copy here.  maybe we don't need this ctor at all
        customIcon = info.customIcon;
        initFlagsAndFirstInstallTime(
                getPackageInfo(context, intent.getComponent().getPackageName()));
    }

    /** TODO: Remove this.  It's only called by ApplicationInfo.makeShortcut. */
    public ShortcutInfo(AppInfo info) {
        super(info);
        setTitle(info.getTitle().toString());
        intent = new Intent(info.intent);
        customIcon = false;
        flags = info.flags;
        firstInstallTime = info.firstInstallTime;
    }

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            Log.d("ShortcutInfo", "PackageManager.getPackageInfo failed for " + packageName);
        }
        return pi;
    }

    public void initFlagsAndFirstInstallTime(PackageInfo pi) {
        flags = AppInfo.initFlags(pi);
        firstInstallTime = AppInfo.initFirstInstallTime(pi);
    }

    public void setIcon(Bitmap b) {
        mIcon = b;
    }

    public Bitmap getIcon(IconCache iconCache) {
        if (mIcon == null) {
            updateIcon(iconCache);
        }
        return mIcon;
    }

    public void updateIcon(IconCache iconCache) {
        mIcon = iconCache.getIcon(intent);
        usingFallbackIcon = iconCache.isDefaultIcon(mIcon);
    }

    /**
     * Creates the application intent based on a component name and various launch flags.
     * Sets {@link #itemType} to {@link GameLauncherSettings.BaseLauncherColumns#ITEM_TYPE_APPLICATION}.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    final void setActivity(Context context, ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
        itemType = GameLauncherSettings.BaseLauncherColumns.ITEM_TYPE_APPLICATION;
        initFlagsAndFirstInstallTime(
                getPackageInfo(context, intent.getComponent().getPackageName()));
    }

    @Override
    public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);

        String titleStr = getTitle() != null ? getTitle().toString() : null;
        values.put(GameLauncherSettings.BaseLauncherColumns.TITLE, titleStr);

        String uri = intent != null ? intent.toUri(0) : null;
        values.put(GameLauncherSettings.BaseLauncherColumns.INTENT, uri);

        if (customIcon) {
            values.put(GameLauncherSettings.BaseLauncherColumns.ICON_TYPE,
                    GameLauncherSettings.BaseLauncherColumns.ICON_TYPE_BITMAP);
            writeBitmap(values, mIcon);
        } else {
            if (!usingFallbackIcon) {
                writeBitmap(values, mIcon);
            }
            values.put(GameLauncherSettings.BaseLauncherColumns.ICON_TYPE,
                    GameLauncherSettings.BaseLauncherColumns.ICON_TYPE_RESOURCE);
            if (iconResource != null) {
                values.put(GameLauncherSettings.BaseLauncherColumns.ICON_PACKAGE,
                        iconResource.packageName);
                values.put(GameLauncherSettings.BaseLauncherColumns.ICON_RESOURCE,
                        iconResource.resourceName);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ShortcutInfo)) {
            return false;
        }
        if(null == packageName || title == null){
            return false;
        }
        ShortcutInfo lhs = (ShortcutInfo) o;
        return packageName.equals(lhs.packageName) && title.equals(lhs.title);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageName == null) ? 0 : packageName.hashCode()+title.hashCode());
        return result;
    }
    @Override
	public String toString() {
		return "ShortcutInfo [intent=" + intent + ", customIcon=" + customIcon + ", usingFallbackIcon="
				+ usingFallbackIcon + ", iconResource=" + iconResource + ", mIcon=" + mIcon + ", firstInstallTime="
				+ firstInstallTime + ", flags=" + flags + "]";
	}

    public static void dumpShortcutInfoList(String tag, String label,
            ArrayList<ShortcutInfo> list) {
        Log.d(tag, label + " size=" + list.size());
        for (ShortcutInfo info: list) {
            Log.d(tag, "   title=\"" + info.getTitle() + " icon=" + info.mIcon
                    + " customIcon=" + info.customIcon);
        }
    }
}

