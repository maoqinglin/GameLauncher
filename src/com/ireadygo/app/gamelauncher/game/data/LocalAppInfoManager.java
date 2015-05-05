package com.ireadygo.app.gamelauncher.game.data;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import com.ireadygo.app.gamelauncher.game.data.GameLauncherSettings.Favorites;
import com.ireadygo.app.gamelauncher.game.info.ItemInfo;

public class LocalAppInfoManager {
    
    public static final String TAG = "LocalAppInfoManager";
    private final Context mContext;
    private PackageManager mPackageManager;

    private HashSet<String> mFilterPkgSet = new HashSet<String>();
    public LocalAppInfoManager(Context context){
        mContext = context;
        mPackageManager = context.getPackageManager();
        initFilterPkgSet();
    }

	private void initFilterPkgSet() {
		mFilterPkgSet.add("com.android.stk");// SIM
		mFilterPkgSet.add("com.google.android.inputmethod.pinyin");
		mFilterPkgSet.add("com.google.android.inputmethod.latin");
		mFilterPkgSet.add("com.google.android.inputmethod.latin.dictionarypack");
		mFilterPkgSet.add("com.mediatek.videoplayer");
		
		mFilterPkgSet.add("com.android.contacts");
		mFilterPkgSet.add("com.android.dialer");
		mFilterPkgSet.add("com.android.mms");
		mFilterPkgSet.add("com.android.email");
		mFilterPkgSet.add("com.android.calendar");
		mFilterPkgSet.add("com.android.calculator2");
		mFilterPkgSet.add("com.android.deskclock");
		mFilterPkgSet.add("com.android.settings");
		mFilterPkgSet.add("com.android.soundrecorder");
		mFilterPkgSet.add("com.mediatek.FMRadio");
		mFilterPkgSet.add("om.android.flashlight");
		mFilterPkgSet.add("com.android.quicksearchbox");
		mFilterPkgSet.add("com.ireadygo.app.key.ui");
		mFilterPkgSet.add("com.ireadygo.app.keyadapter");
		mFilterPkgSet.add("com.android.gallery3d");
		mFilterPkgSet.add("com.android.camera2");
		mFilterPkgSet.add("com.android.music");
		mFilterPkgSet.add("com.mediatek.filemanager");
		mFilterPkgSet.add("com.android.wfd");
		mFilterPkgSet.add("com.android.vending");
		mFilterPkgSet.add("com.google.android.gms");
		mFilterPkgSet.add("com.ireadygo.app.systemupgrade");
		mFilterPkgSet.add("com.mediatek.bluetooth");
		mFilterPkgSet.add("com.google.android.play.games");
		mFilterPkgSet.add("com.mediatek.bluetooth");
		mFilterPkgSet.add("com.nvidia.ota");
		mFilterPkgSet.add("com.broadcom.bt.app.bt3d");
		mFilterPkgSet.add("com.sohu.inputmethod.sogouoem");
		mFilterPkgSet.add("com.widevine.demo");
		mFilterPkgSet.add("com.ireadygo.app.videorecorder");
		mFilterPkgSet.add("com.snail.bigscreen");
        mFilterPkgSet.add("com.android.mira4u");
		mFilterPkgSet.add(mContext.getPackageName());
	}

	public HashSet<String> getFilterPkgSet() {
		return mFilterPkgSet;
	}

	public void initDatabase() {
        ArrayList<ItemInfoObject> dbItems = loadItemsFromDB();
        ArrayList<ItemInfoObject> mainItems = queryAllApps();
        for (ItemInfoObject item : dbItems) {
            if (Favorites.ITEM_TYPE_FOLDER == item.itemType) {
                continue;
            }
            if (mainItems.contains(item)) {
                mainItems.remove(item);
            }
        }
        insertIntoDB(mainItems);
    }
    
    private ArrayList<ItemInfoObject> loadItemsFromDB() {
        ArrayList<ItemInfoObject> items = new ArrayList<ItemInfoObject>();
        final ContentResolver cr = mContext.getContentResolver();
        Cursor c = cr.query(Favorites.CONTENT_URI, new String[] {
                Favorites._ID, Favorites.INTENT, Favorites.ITEM_TYPE, Favorites.CONTAINER,Favorites.PACKAGE_NAME,Favorites.CELL_SORT_ID,
                Favorites.SCREEN,Favorites.SPANX, Favorites.SPANY
        }, null, null, null);

        final int idIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites._ID);
        final int intentIndex = c.getColumnIndexOrThrow(Favorites.INTENT);
        final int itemTypeIndex = c.getColumnIndexOrThrow(Favorites.ITEM_TYPE);
        final int containerIndex = c.getColumnIndexOrThrow(Favorites.CONTAINER);
        final int screenIndex = c.getColumnIndexOrThrow(Favorites.SCREEN);
        final int spanXIndex = c.getColumnIndexOrThrow(Favorites.SPANX);
        final int spanYIndex = c.getColumnIndexOrThrow(Favorites.SPANY);
        final int pkgNameIndex = c.getColumnIndexOrThrow(Favorites.PACKAGE_NAME);
        final int cellSortIdIndex = c.getColumnIndexOrThrow(Favorites.CELL_SORT_ID);
        String intentDescription;
        Intent intent;
        try {
            while (c.moveToNext()) {
                ItemInfoObject item = new ItemInfoObject();
                item.id = c.getInt(idIndex);
                item.itemType = c.getInt(itemTypeIndex);
                item.container = c.getInt(containerIndex);
                switch (item.itemType) {
                    case Favorites.ITEM_TYPE_APPLICATION:
                    case Favorites.ITEM_TYPE_SHORTCUT:
                        intentDescription = c.getString(intentIndex);
                        try {
                            intent = Intent.parseUri(intentDescription, 0);
                        } catch (URISyntaxException e) {
                            continue;
                        }
                        item.packageName = getPackageName(intent);
                        item.className = getClassName(intent);
                        break;
                    case Favorites.ITEM_TYPE_FOLDER:
                    default:
                        break;
                }
                item.spanX = c.getInt(spanXIndex);
                item.spanY = c.getInt(spanYIndex);
                item.screen = c.getInt(screenIndex);
                item.packageName = c.getString(pkgNameIndex);
                item.cellSortId = c.getInt(cellSortIdIndex);
                items.add(item);
            }
        } catch (Exception e) {
            items.clear();
        } finally {
            c.close();
        }
        return items;
    }
    
    private ArrayList<ItemInfoObject> queryAllApps() {
        ArrayList<ItemInfoObject> items = new ArrayList<ItemInfoObject>();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = mPackageManager.queryIntentActivities(mainIntent, 0);
        int size = infos.size();
        Log.e("lmq", "queryAllApps---size = "+size);
        for (int i = 0; i < size; i++) {
            if (mFilterPkgSet.contains((infos.get(i).activityInfo.applicationInfo.packageName))) {
                continue;
            }
            ItemInfoObject item = new ItemInfoObject();
            item.packageName = infos.get(i).activityInfo.applicationInfo.packageName;
            item.className = infos.get(i).activityInfo.name;
            items.add(item);
        }
        return items;
    }
    
    private void insertIntoDB(final ArrayList<ItemInfoObject> items) {
        if (null == items) {
            return;
        }
        //过滤数据
        
        GameLauncherProvider provider = GameLauncherAppState.getLauncherProvider();
        ContentValues[] values = new ContentValues[items.size()];
        for (int i = 0; i < items.size(); i++) {
            ContentValues value = buildInsertItem(mPackageManager, items.get(i), provider.generateNewItemId(),provider.generateNewCellSortId());
            if (value.size() != 0) {
                values[i] = value;
            }
        }
        // values[values.length - 1] = buildMuchGameShortcut(init, provider);
        provider.bulkInsert(Favorites.CONTENT_URI_NO_NOTIFICATION, values);
    }
    
    private ContentValues buildInsertItem(PackageManager packageManager, ItemInfoObject item, long newID,long newCellSortId) {
        ContentValues values = new ContentValues();
        values.clear();
        ActivityInfo info;
        try {
            ComponentName cn;
            try {
                cn = new ComponentName(item.packageName, item.className);
                info = packageManager.getActivityInfo(cn, 0);
            } catch (PackageManager.NameNotFoundException nnfe) {
                String[] packages = packageManager.currentToCanonicalPackageNames(new String[] { item.packageName });
                cn = new ComponentName(packages[0], item.className);
                info = packageManager.getActivityInfo(cn, 0);
            }

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            values.put(Favorites._ID, newID);
            values.put(Favorites.CELL_SORT_ID, newCellSortId);
            values.put(Favorites.INTENT, intent.toUri(0));
            values.put(Favorites.PACKAGE_NAME, info.packageName);
            values.put(Favorites.APP_TYPE, Favorites.APP_TYPE_APPLICATION);
            values.put(Favorites.CONTAINER, Favorites.CONTAINER_DESKTOP);
            values.put(Favorites.TITLE, info.loadLabel(packageManager).toString());
            values.put(Favorites.ITEM_TYPE, Favorites.ITEM_TYPE_APPLICATION);
            values.put(Favorites.SCREEN, item.screen);
            values.put(Favorites.SPANX, 1);
            values.put(Favorites.SPANY, 1);
            Bitmap bitmap = GameLauncherAppState.getInstance(mContext).getIconCache().getIcon(intent);
            ItemInfo.writeBitmap(values, bitmap);
            return values;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Unable to add item: " + item.packageName + "/" + item.className, e);
        }
        return values;
    }

    private String getPackageName(Intent intent) {
        if (intent != null) {
            String packageName = intent.getPackage();
            if (packageName == null && intent.getComponent() != null) {
                packageName = intent.getComponent().getPackageName();
            }
            if (packageName != null) {
                return packageName;
            }
        }
        return "";
    }

    private String getClassName(Intent intent) {
        String className = "";
        if (intent != null) {
            if (intent.getComponent() != null) {
                className = intent.getComponent().getClassName();
            }
        }
        return className;
    }
    
    public class ItemInfoObject {
        String packageName;
        String className;
        Intent intent;
        String title;
        int screen;
        int spanX;
        int spanY;
        int cellSortId;
        int container;
        int itemType;
        int id;

        @Override
        public String toString() {
            return "ItemInfoObject [packageName=" + packageName + ", className=" + className + ", intent=" + intent
                    + ", title=" + title + ", screen=" + screen + ", spanX=" + spanX + ", spanY=" + spanY
                    + ", cellSortId=" + cellSortId + ", container=" + container + ", itemType=" + itemType + ", id="
                    + id + "]";
        }

        public ItemInfoObject() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof ItemInfoObject)) {
                return false;
            }

            ItemInfoObject lhs = (ItemInfoObject) o;
            return packageName.equals(lhs.packageName) && className.equals(lhs.className);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((className == null) ? 0 : className.hashCode());
            result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
            return result;
        }
    }
}
