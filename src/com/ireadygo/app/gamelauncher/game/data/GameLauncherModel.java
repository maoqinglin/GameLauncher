package com.ireadygo.app.gamelauncher.game.data;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherSettings.Favorites;
import com.ireadygo.app.gamelauncher.game.info.FolderInfo;
import com.ireadygo.app.gamelauncher.game.info.ItemInfo;
import com.ireadygo.app.gamelauncher.game.info.ShortcutInfo;
import com.ireadygo.app.gamelauncher.game.utils.DeferredHandler;
import com.ireadygo.app.gamelauncher.game.utils.IconCache;
import com.ireadygo.app.gamelauncher.game.utils.Utilities;
import com.ireadygo.app.gamelauncher.ui.AppFragment;
import com.ireadygo.app.gamelauncher.ui.GameFragment;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

@SuppressLint("NewApi")

public class GameLauncherModel{
    public static final String TAG = "AppDao";
    private static final String ACTION_LOAD_DATA_COMPLETE = "com.ireadygo.app.gamelauncher.ACTION_LOAD_DATA_COMPLETE";
    private Context mContext;
    private PackageManager mPkgManager;
    private boolean mAppsCanBeOnRemoveableStorage;
    private IconCache mIconCache;
    private Bitmap mDefaultIcon;
    static final Object sBgLock = new Object();
    private final Object mLock = new Object();
    private LoaderTask mLoaderTask;
    private static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");
    static {
        sWorkerThread.start();
    }
    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());
    private DeferredHandler mHandler = new DeferredHandler();
    private ArrayList<Callbacks> mCallbacks = new ArrayList<Callbacks>();
    private LocalAppInfoManager mLocalAppInfoManager;
    private final List<ItemInfo> gameInfos = new LinkedList<ItemInfo>();
    private final List<ItemInfo> appInfos = new LinkedList<ItemInfo>();
    final HashMap<Long, FolderInfo> sBgFolders = new HashMap<Long, FolderInfo>();
    private boolean mDataHasLoaded;
    public GameLauncherModel(Context context, IconCache iconCache) {
        mContext = context;
        mPkgManager = mContext.getPackageManager();
        mAppsCanBeOnRemoveableStorage = Environment.isExternalStorageRemovable();
        mIconCache = iconCache;
        mDefaultIcon = Utilities.createIconBitmap(mIconCache.getFullResDefaultActivityIcon(), context);
        mLocalAppInfoManager = new LocalAppInfoManager(mContext);
    }

    /**
     * Set this as the current Launcher activity object for the loader.
     */
    public void addCallback(Callbacks callback) {
        if(null == callback){
            return;
        }
        synchronized (mLock) {
            Callbacks tmpCallback = null;
            for (Callbacks existCall : mCallbacks) {
                if (existCall instanceof AppFragment && callback instanceof AppFragment) {
                    tmpCallback = existCall;
                    break;
                }
                if (existCall instanceof GameFragment && callback instanceof GameFragment) {
                    tmpCallback = existCall;
                    break;
                }
            }
            if (tmpCallback != null) {
                mCallbacks.remove(tmpCallback);

            }
            mCallbacks.add(callback);
        }
    }

    public void removeCallback(){
        synchronized (mLock) {
            mCallbacks.clear();
        }
    }
    
    public void removeCallback(Callbacks callback){
        synchronized (mLock) {
            mCallbacks.remove(callback);
        }
    }

    public interface Callbacks {
        public void bindGames(List<ItemInfo> infos);
        public void bindApps(List<ItemInfo> infos);
        public void bindFolders(HashMap<Long,FolderInfo> folders);
        public void gameAddOrUpdate(ItemInfo info,boolean isAdd);
        public void gameRemove(ItemInfo info);
    }
    
    class LoaderTask implements Runnable {
        private boolean mStopped;
        private boolean mIsLaunching;
        
        private int mDataType;
     // sBgFolders is all FolderInfos created by LauncherModel. Passed to bindFolders()

        public LoaderTask() {
            // TODO Auto-generated constructor stub
        }

        public LoaderTask(int dataType) {
            mDataType = dataType;
        }
        boolean isLaunching() {
            return mIsLaunching;
        }
        @Override
        public void run() {
            synchronized (LoaderTask.class) {
                if(!mDataHasLoaded){
                loadAllData();
                }
                if(mDataType == Favorites.APP_TYPE_GAME){
                bindMyGame();
                }else if(mDataType == Favorites.APP_TYPE_APPLICATION){
                bindMyApp();
                }
                bindFolders();
            }
        }

        public void loadAllData() {
            if (!hasInit()) {
                GameLauncherAppState.getLauncherProvider().loadDefaultFavoritesIfNecessary(R.xml.much_default_workspace);
                mLocalAppInfoManager.initDatabase();
                setInitFlag();
            }
            sendLoadFinishBroadcast();
            loadWorkspace();
            checkEmptyFolders(appInfos);
            checkEmptyFolders(gameInfos);
            mDataHasLoaded = true;
        }

        private void checkEmptyFolders(final List<ItemInfo> appList) {
            if(appList == null || appList.size() == 0){
                return ;
            }
            List<ItemInfo> emptyFolders = new ArrayList<ItemInfo>();
            List<ItemInfo> onlyItemInFolders = new ArrayList<ItemInfo>();
            for (ItemInfo appItem : appList) {
                if (appItem instanceof FolderInfo) {
                    FolderInfo folderInfo = (FolderInfo) appItem;
                    int size = folderInfo.contents.size();
                    if (size <= 1) {
                        if (size == 1) {
                            // 将唯一的item添加到我的应用
                            ItemInfo onlyItem = folderInfo.contents.get(0);
                            onlyItem.cellSortId = folderInfo.cellSortId;
                            onlyItem.container = Favorites.CONTAINER_DESKTOP;
                            onlyItemInFolders.add(onlyItem);
                            // 清空文件夹数据
                            folderInfo.contents.clear();
                            updateItemInDatabase(mContext, onlyItem);
                        }
                        if (getFolderItemsFromDb(folderInfo.id) > 1) {// 文件夹容错处理，防止误删
                            continue;
                        }
                        // 删除内存值
                        emptyFolders.add(folderInfo);
                        // 删除数据库记录
                        deleteItem(mContext, folderInfo);
                    }
                }
            }
            
            for(ItemInfo item :emptyFolders){
                sBgFolders.remove(item);
            }
            appList.removeAll(emptyFolders);
            appList.addAll(onlyItemInFolders);
        }

		private void sendLoadFinishBroadcast() {
			Intent intent = new Intent(ACTION_LOAD_DATA_COMPLETE);
			mContext.sendBroadcast(intent);
		}

		/** Returns whether this is an upgradge path */
		private boolean loadWorkspace() {
			final Context context = mContext;
			final ContentResolver contentResolver = context.getContentResolver();
			final PackageManager manager = context.getPackageManager();

			// Check if we need to do any upgrade-path logic
			boolean loadedOldDb = false;

			synchronized (sBgLock) {
				clearSBgDataStructures();

				final ArrayList<Long> itemsToRemove = new ArrayList<Long>();
				final Uri contentUri = GameLauncherSettings.Favorites.CONTENT_URI;
				final Cursor c = contentResolver.query(contentUri, null, null, null,
						GameLauncherSettings.Favorites.CELL_SORT_ID + " asc");
				try {
					final int idIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites._ID);
					final int intentIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.INTENT);
					final int packageIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.PACKAGE_NAME);
					final int titleIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.TITLE);
					final int iconTypeIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.ICON_TYPE);
					final int iconIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.ICON);
					final int iconPackageIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.ICON_PACKAGE);
					final int iconResourceIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.ICON_RESOURCE);
					final int containerIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.CONTAINER);
					final int itemTypeIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.ITEM_TYPE);
					final int screenIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.SCREEN);
					final int cellSortIdIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.CELL_SORT_ID);
					final int appTypeIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.APP_TYPE);
					final int displayModeIndex = c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.DISPLAY_MODE);

					ShortcutInfo info = null;
					String intentDescription;
					int container;
					long id;
					Intent intent;
					int appType;
					while (!mStopped && c.moveToNext()) {
						AtomicBoolean deleteOnItemOverlap = new AtomicBoolean(false);
						try {
							int displayMode = c.getInt(displayModeIndex);
							if (displayMode == GameLauncherSettings.Favorites.DONOT_DISPLAY) {
								continue;
							}
							int itemType = c.getInt(itemTypeIndex);

							switch (itemType) {
							case GameLauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
							case GameLauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
								id = c.getLong(idIndex);
								intentDescription = c.getString(intentIndex);
								try {
									intent = Intent.parseUri(intentDescription, 0);
									ComponentName cn = intent.getComponent();
									// if (cn != null &&
									// !isValidPackageComponent(manager, cn)) {
									// if (!mAppsCanBeOnRemoveableStorage) {
									// // Log the invalid package, and remove
									// // it from the db
									// // Launcher.addDumpLog(TAG,
									// // "Invalid package removed: " + cn,
									// // true);
									// itemsToRemove.add(id);
									// } else {
									// // If apps can be on external storage,
									// // then we just
									// // leave them for the user to remove
									// // (maybe add
									// // visual treatment to it)
									// // Launcher.addDumpLog(TAG,
									// // "Invalid package found: " + cn,
									// // true);
									// }
									// continue;
									// }
								} catch (URISyntaxException e) {
									// Launcher.addDumpLog(TAG, "Invalid uri: "
									// +
									// intentDescription, true);
									continue;
								}

								if (itemType == GameLauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
									info = getShortcutInfo(manager, intent, context, c, iconIndex, titleIndex, null);
								} else {
									/*
									 * info = getShortcutInfo(c, context,
									 * iconTypeIndex, iconPackageIndex,
									 * iconResourceIndex, iconIndex,
									 * titleIndex);
									 */

									// App shortcuts that used to be
									// automatically
									// added to Launcher
									// didn't always have the correct intent
									// flags
									// set, so do that
									// here
									if (intent.getAction() != null && intent.getCategories() != null
											&& intent.getAction().equals(Intent.ACTION_MAIN)
											&& intent.getCategories().contains(Intent.CATEGORY_LAUNCHER)) {
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
												| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
									}
								}

								if (info != null) {
									info.id = id;
									info.intent = intent;
									container = c.getInt(containerIndex);
									info.container = container;
									info.screenId = c.getInt(screenIndex);
									info.cellSortId = c.getInt(cellSortIdIndex);
									info.spanX = 1;
									info.spanY = 1;
									appType = c.getInt(appTypeIndex);
									info.appType = appType;
									info.packageName = c.getString(packageIndex);
									info.isSystemApp = PackageUtils.isSystemApp(mContext, info.packageName);
									// check & update map of what's occupied
									deleteOnItemOverlap.set(false);

									GameLauncherAppState.getInstance(mContext).getIconDecorater()
											.observeIconNeedUpdated(info, info.appIcon, info.intent.getComponent());
									switch (container) {
									case GameLauncherSettings.Favorites.CONTAINER_DESKTOP:
										switch (appType) {
										case GameLauncherSettings.Favorites.APP_TYPE_GAME:
											gameInfos.add(info);
											break;
										case GameLauncherSettings.Favorites.APP_TYPE_APPLICATION:// 容器为我的桌面并且为我的应用
											appInfos.add(info);
											break;
										default:
											appInfos.add(info);
											break;
										}
										break;
									default:
										// Item is in a user folder
										FolderInfo folderInfo = findOrMakeFolder(sBgFolders, container);
										folderInfo.add(info);
										break;
									}
									// sBgItemsIdMap.put(info.id, info);

									// now that we've loaded everthing
									// re-save it
									// with the
									// icon in case it disappears somehow.
									// queueIconToBeChecked(sBgDbIconCache,
									// info, c,
									// iconIndex);
								} else {
									throw new RuntimeException("Unexpected null ShortcutInfo");
								}
								break;

							case GameLauncherSettings.Favorites.ITEM_TYPE_FOLDER:
								id = c.getLong(idIndex);
								FolderInfo folderInfo = findOrMakeFolder(sBgFolders, id);
								folderInfo.title = c.getString(titleIndex);
								folderInfo.id = id;
								container = c.getInt(containerIndex);
								folderInfo.container = container;
								folderInfo.screenId = c.getInt(screenIndex);
								folderInfo.spanX = 1;
								folderInfo.spanY = 1;
								folderInfo.cellSortId = c.getInt(cellSortIdIndex);
								appType = c.getInt(appTypeIndex);
								switch (container) {
								case GameLauncherSettings.Favorites.CONTAINER_DESKTOP:
									switch (appType) {
									case GameLauncherSettings.Favorites.APP_TYPE_APPLICATION: // 我的应用中的文件夹
										appInfos.add(folderInfo);
										break;
									case GameLauncherSettings.Favorites.APP_TYPE_GAME: // 我的游戏中的文件夹
										gameInfos.add(folderInfo);
										break;
									default:
										appInfos.add(folderInfo);
										break;
									}

									sBgFolders.put(folderInfo.id, folderInfo);
									break;
								}
							}
						} catch (Exception e) {
							// Launcher.addDumpLog(TAG,
							// "Desktop items loading interrupted: " + e, true);
						}
					}
				} finally {
					if (c != null) {
						c.close();
					}
				}

				// Break early if we've stopped loading
				/*
				 * if (mStopped) { clearSBgDataStructures(); return false; }
				 */

				if (itemsToRemove.size() > 0) {
					ContentProviderClient client = contentResolver
							.acquireContentProviderClient(GameLauncherSettings.Favorites.CONTENT_URI);
					// Remove dead items
					for (long id : itemsToRemove) {
						/*
						 * if (DEBUG_LOADERS) { Log.d(TAG, "Removed id = " +
						 * id); }
						 */
						// Don't notify content observers
						try {
							client.delete(GameLauncherSettings.Favorites.getContentUri(id, false), null, null);
						} catch (RemoteException e) {
							Log.w(TAG, "Could not remove id = " + id);
						}
					}
				}

			}
			return loadedOldDb;
		}

		public void bindMyGame() {
			final Runnable r = new Runnable() {

				@Override
				public void run() {
					for (Callbacks callbacks : mCallbacks) {
						if (callbacks != null) {
							callbacks.bindGames(gameInfos);
						}
					}
				}
			};

			runOnMainThread(r);
		}

		public void bindMyApp() {
			final Runnable r = new Runnable() {

				@Override
				public void run() {
					for (Callbacks callbacks : mCallbacks) {
						if (callbacks != null) {
							callbacks.bindApps(appInfos);
						}
					}
				}
			};

			runOnMainThread(r);
		}

		private void bindFolders() {
            if (!sBgFolders.isEmpty()) {
                final Runnable r = new Runnable() {
                    public void run() {
                        for(Callbacks callbacks :mCallbacks){
                            if (callbacks != null) {
                                callbacks.bindFolders(sBgFolders);
                            }
                        }
                    }
                };
                runOnMainThread(r);
            }
        }
        
        /**
         * Gets the callbacks object.  If we've been stopped, or if the launcher object
         * has somehow been garbage collected, return null instead.  Pass in the Callbacks
         * object that was around when the deferred message was scheduled, and if there's
         * a new Callbacks object around then also return null.  This will save us from
         * calling onto it with data that will be ignored.
         */
       /* Callbacks tryGetCallbacks(Callbacks oldCallbacks) {
            synchronized (mLock) {
                if (mStopped) {
                    return null;
                }

                if (mCallbacks == null) {
                    return null;
                }

                final Callbacks callbacks = mCallbacks.get();
                if (callbacks != oldCallbacks) {
                    return null;
                }
                if (callbacks == null) {
                    Log.w(TAG, "no mCallbacks");
                    return null;
                }

                return callbacks;
            }
        }*/

        public void stopLocked() {
            synchronized (LoaderTask.this) {
                mStopped = true;
                this.notify();
            }
        }
        
        /** Clears all the sBg data structures */
        private void clearSBgDataStructures() {
            synchronized (sBgLock) {
                gameInfos.clear();
                appInfos.clear();
            }
        }
    }

    public int getFolderItemsFromDb(long container) {
        int count = 0;
        if (container <= 0) {
            return count;
        }
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(GameLauncherSettings.Favorites.CONTENT_URI, null,
                GameLauncherSettings.Favorites.CONTAINER + "=?", new String[] { String.valueOf(container) }, null);
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    public void stopLoader() {
        synchronized (mLock) {
            if (mLoaderTask != null) {
                mLoaderTask.stopLocked();
            }
        }
    }

    private void runOnMainThread(Runnable r) {
        runOnMainThread(r, 0);
    }

    private void runOnMainThread(Runnable r, int type) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            // If we are on the worker thread, post onto the main handler
            mHandler.post(r);
        } else {
            r.run();
        }
    }

    /**
     * Runs the specified runnable immediately if called from the worker thread,
     * otherwise it is posted on the worker thread handler.
     */
    private static void runOnWorkerThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            // If we are not on the worker thread, then post to the worker
            // handler
            sWorker.post(r);
        }
    }

    private boolean hasInit() {
        String spKey = GameLauncherAppState.getSharedPreferencesKey();
        SharedPreferences sp = mContext.getSharedPreferences(spKey, Context.MODE_PRIVATE);
        return sp.getBoolean(GameLauncherProvider.LOCAL_DATA_INIT, false);
    }

    private void setInitFlag() {
        String spKey = GameLauncherAppState.getSharedPreferencesKey();
        SharedPreferences sp = mContext.getSharedPreferences(spKey, Context.MODE_PRIVATE);
        sp.edit().putBoolean(GameLauncherProvider.LOCAL_DATA_INIT, true).commit();
    }
    
 // If there is already a loader task running, tell it to stop.
    // returns true if isLaunching() was true on the old task
    private boolean stopLoaderLocked() {
        boolean isLaunching = false;
        LoaderTask oldTask = mLoaderTask;
        if (oldTask != null) {
            if (oldTask.isLaunching()) {
                isLaunching = true;
            }
            oldTask.stopLocked();
        }
        return isLaunching;
    }

    public void startLoader() {
        mDataHasLoaded = false;
        synchronized (mLock) {

//            // Don't bother to start the thread if we know it's not going to do anything
//            if (mCallbacks != null && mCallbacks.size() > 0) {
//                // If there is already one running, tell it to stop.
//                // also, don't downgrade isLaunching if we're already running
////                isLaunching = isLaunching || stopLoaderLocked();
//            }
            mLoaderTask = new LoaderTask();
            sWorkerThread.setPriority(Thread.NORM_PRIORITY);
            sWorker.post(mLoaderTask);
        }
    }
	public void startLoader(int dataType) {
		synchronized (mLock) {
			mLoaderTask = new LoaderTask(dataType);
			if (!appInfos.isEmpty() && Favorites.APP_TYPE_APPLICATION == dataType) {
				mLoaderTask.bindMyApp();
				mLoaderTask.bindFolders();
				return;
			}
			if (!gameInfos.isEmpty() && Favorites.APP_TYPE_GAME == dataType) {
				mLoaderTask.bindMyGame();
				return;
			}
            sWorkerThread.setPriority(Thread.NORM_PRIORITY);
            sWorker.post(mLoaderTask);
        }
    }

    private boolean isValidPackageComponent(PackageManager pm, ComponentName cn) {
        if (cn == null) {
            return false;
        }

        try {
            // Skip if the application is disabled
            PackageInfo pi = pm.getPackageInfo(cn.getPackageName(), 0);
            if (!pi.applicationInfo.enabled) {
                return false;
            }

            // Check the activity
            //TODO by linmaoqing
//            return (pm.getActivityInfo(cn, 0) != null);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
    
    /**
     * This is called from the code that adds shortcuts from the intent receiver.  This
     * doesn't have a Cursor, but
     */
    public ShortcutInfo getShortcutInfo(PackageManager manager, Intent intent, Context context) {
        return getShortcutInfo(manager, intent, context, null, -1, -1, null);
    }

    /**
     * Make an ShortcutInfo object for a shortcut that is an application.
     * 
     * If c is not null, then it will be used to fill in missing data like the
     * title and icon.
     */
    public ShortcutInfo getShortcutInfo(PackageManager manager, Intent intent, Context context, Cursor c,
            int iconIndex, int titleIndex, HashMap<Object, CharSequence> labelCache) {
        ComponentName componentName = intent.getComponent();
        final ShortcutInfo info = new ShortcutInfo();
        if (componentName != null && !isValidPackageComponent(manager, componentName)) {
            Log.d(TAG, "Invalid package found in getShortcutInfo: " + componentName);
            return null;
        } else {
            try {
                PackageInfo pi = manager.getPackageInfo(componentName.getPackageName(), 0);
                info.initFlagsAndFirstInstallTime(pi);
            } catch (NameNotFoundException e) {
                Log.d(TAG, "getPackInfo failed for package " + componentName.getPackageName());
            }
        }

        // TODO: See if the PackageManager knows about this case. If it doesn't
        // then return null & delete this.

        // the resource -- This may implicitly give us back the fallback icon,
        // but don't worry about that. All we're doing with usingFallbackIcon is
        // to avoid saving lots of copies of that in the database, and most apps
        // have icons anyway.

        // Attempt to use queryIntentActivities to get the ResolveInfo (with
        // IntentFilter info) and
        // if that fails, or is ambiguious, fallback to the standard way of
        // getting the resolve info
        // via resolveActivity().
        Bitmap icon = null;
        ResolveInfo resolveInfo = null;
        ComponentName oldComponent = intent.getComponent();
        Intent newIntent = new Intent(intent.getAction(), null);
        newIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        newIntent.setPackage(oldComponent.getPackageName());
        List<ResolveInfo> infos = manager.queryIntentActivities(newIntent, 0);
        for (ResolveInfo i : infos) {
            ComponentName cn = new ComponentName(i.activityInfo.packageName, i.activityInfo.name);
            if (cn.equals(oldComponent)) {
                resolveInfo = i;
            }
        }
        if (resolveInfo == null) {
            resolveInfo = manager.resolveActivity(intent, 0);
        }
        // if (resolveInfo != null) {
        // icon = mIconCache.getIcon(componentName, resolveInfo, labelCache);
        // }
        // the db
        if (icon == null) {
            if (c != null) {
                icon = getIconFromCursor(c, iconIndex, context);
            }
        }
        // the fallback icon
        /*
         * if (icon == null) { icon = getFallbackIcon(); info.usingFallbackIcon
         * = true; }
         */
        info.setIcon(icon);
        info.appIcon = icon;
        // from the resource
        if (resolveInfo != null) {
            ComponentName key = getComponentNameFromResolveInfo(resolveInfo);
            if (labelCache != null && labelCache.containsKey(key)) {
                info.title = labelCache.get(key);
            } else {
                info.title = resolveInfo.activityInfo.loadLabel(manager);
                if (labelCache != null) {
                    labelCache.put(key, info.title);
                }
            }
        }
        // from the db
        if (info.title == null) {
            if (c != null) {
                info.title = c.getString(titleIndex);
            }
        }
        // fall back to the class name of the activity
        if (info.title == null) {
            info.title = componentName.getClassName();
        }
        info.itemType = GameLauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
        return info;
    }

    /*
     * public Bitmap getFallbackIcon() { return
     * Bitmap.createBitmap(mDefaultIcon); }
     */
    
    /**
     * Return an existing FolderInfo object if we have encountered this ID previously,
     * or make a new one.
     */
    private static FolderInfo findOrMakeFolder(HashMap<Long, FolderInfo> folders, long id) {
        // See if a placeholder was created for us already
        FolderInfo folderInfo = folders.get(id);
        if (folderInfo == null) {
            // No placeholder -- create a new instance
            folderInfo = new FolderInfo();
            folders.put(id, folderInfo);
        }
        return folderInfo;
    }

    public static ComponentName getComponentNameFromResolveInfo(ResolveInfo info) {
        if (info.activityInfo != null) {
            return new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
        } else {
            return new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name);
        }
    }

    Bitmap getIconFromCursor(Cursor c, int iconIndex, Context context) {
        @SuppressWarnings("all")
        // suppress dead code warning
        final boolean debug = false;
        if (debug) {
            Log.d(TAG,
                    "getIconFromCursor app="
                            + c.getString(c.getColumnIndexOrThrow(GameLauncherSettings.Favorites.TITLE)));
        }
        byte[] data = c.getBlob(iconIndex);
        try {
            return Utilities.createIconBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), context);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Add an item to the database in a specified container. Sets the container,
     * screen, cellX and cellY fields of the item. Also assigns an ID to the
     * item.
     */
    public void addItemToDatabase(Context context, final ItemInfo item, final boolean notify) {

        final ContentValues values = new ContentValues();
        final ContentResolver cr = context.getContentResolver();
        item.onAddToDatabase(values);

        item.id = GameLauncherAppState.getLauncherProvider().generateNewItemId();
        values.put(Favorites._ID, item.id);
        values.put(Favorites.INTENT, item.getIntent().toUri(0));
        values.put(Favorites.PACKAGE_NAME, item.packageName);
        values.put(Favorites.TITLE, item.title.toString());
        values.put(Favorites.DISPLAY_MODE, item.displayMode);
        values.put(Favorites.APP_TYPE,item.appType);
        Bitmap posterIcon = GameData.getInstance(mContext).getPosterIconByPkgName(item.packageName);
        if (posterIcon == null) {
            posterIcon = GameLauncherAppState.getInstance(mContext).getIconCache().getIcon(item.getIntent());
        }
        int iconWidth = mContext.getResources().getDimensionPixelSize(R.dimen.common_app_item_small_icon_width);
        int iconHeigth = mContext.getResources().getDimensionPixelSize(R.dimen.common_app_item_small_icon_height);
        if (posterIcon.getHeight() > iconHeigth || posterIcon.getWidth() > iconWidth) {
        	ItemInfo.writeBitmap(values, PictureUtil.zoomImage(posterIcon, iconWidth, iconHeigth));
        } else {
        ItemInfo.writeBitmap(values, posterIcon);
        }
        item.cellSortId = (int)GameLauncherAppState.getLauncherProvider().generateNewCellSortId();
        item.updateValuesWithSortId(values, item.cellSortId);
        
        final ShortcutInfo shortcutInfo = getShortcutInfo(values,item.getIntent(),posterIcon);
        
        Runnable r = new Runnable() {
            public void run() {
                cr.insert(notify ? Favorites.CONTENT_URI
                        : Favorites.CONTENT_URI_NO_NOTIFICATION, values);
                bindGameAdd(shortcutInfo,true);
            }
        };
        runOnWorkerThread(r);
    }
    
    /**
     * Add an item to the database in a specified container. Sets the container,
     * screen, cellX and cellY fields of the item. Also assigns an ID to the
     * item.
     */
    public void addFolderItemToDatabase(Context context, final ItemInfo item, final boolean notify) {

        final ContentValues values = new ContentValues();
        final ContentResolver cr = context.getContentResolver();
        item.onAddToDatabase(values);

        item.id = GameLauncherAppState.getLauncherProvider().generateNewItemId();
        values.put(Favorites._ID, item.id);
        values.put(Favorites.TITLE, item.title.toString());
        item.updateValuesWithSortId(values, item.cellSortId);
        
        Runnable r = new Runnable() {
            public void run() {
                cr.insert(notify ? Favorites.CONTENT_URI
                        : Favorites.CONTENT_URI_NO_NOTIFICATION, values);
            }
        };
        runOnWorkerThread(r);
    }

    private ShortcutInfo getShortcutInfo(ContentValues values,Intent intent,Bitmap bitmap) {
        ShortcutInfo info = new ShortcutInfo();
        info.id = values.getAsInteger(Favorites._ID);
        info.cellSortId = values.getAsInteger(Favorites.CELL_SORT_ID);
        info.packageName = values.getAsString(Favorites.PACKAGE_NAME);
        info.intent = intent;
        info.title = values.getAsString(Favorites.TITLE);
        info.appIcon = bitmap;
        info.container = values.getAsInteger(Favorites.CONTAINER);
        info.itemType = values.getAsInteger(Favorites.ITEM_TYPE);
        info.appType = values.getAsInteger(Favorites.APP_TYPE);
        return info;
    }

    /**
     * Removes the specified item from the database
     * 
     * @param context
     * @param item
     */
    public void deleteItemFromDatabase(Context context, final ItemInfo item) {
        final ContentResolver cr = context.getContentResolver();
        final Uri uriToDelete = GameLauncherSettings.Favorites.getContentUri(item.id, false);

        Runnable r = new Runnable() {
            public void run() {
                int row = cr.delete(uriToDelete, null, null);
                // Lock on mBgLock *after* the db operation
                if(row > 0){
                    synchronized (sBgLock) {
                        bindGameRemove(item);
                    }
                }
            }
        };
        runOnWorkerThread(r);
    }
    
    public static void deleteItem(Context context, final ItemInfo item) {
        final ContentResolver cr = context.getContentResolver();
        final Uri uriToDelete = GameLauncherSettings.Favorites.getContentUri(item.id, false);

        Runnable r = new Runnable() {
            public void run() {
                int row = cr.delete(uriToDelete, null, null);
            }
        };
        runOnWorkerThread(r);
    }
    
    public static void updateItemInDatabaseByFolderName(Context context, final ItemInfo item) {
        final ContentValues values = new ContentValues();
        values.put(GameLauncherSettings.Favorites.TITLE, String.valueOf(item.title));
        updateItemInDatabaseHelper(context, values, item, "updateItemInDatabase");
    }

    public void updateItemByGameUpdate(Context context, final ItemInfo oldItem, final ItemInfo newItem) {
        final ContentResolver cr = context.getContentResolver();
        final Uri uriToUpdate = GameLauncherSettings.Favorites.getContentUri(oldItem.id, false);
        final ContentValues values = new ContentValues();

        values.put(Favorites.INTENT, newItem.getIntent().toUri(0));
        Bitmap bitmap = GameLauncherAppState.getInstance(mContext).getIconCache().getIcon(newItem.getIntent());
        ItemInfo.writeBitmap(values, bitmap);

        ((ShortcutInfo) oldItem).intent = ((ShortcutInfo) newItem).intent;
        oldItem.appIcon = bitmap;
        cr.update(uriToUpdate, values, null, null);
    }

    /**
     * Update an item to the database in a specified container.
     */
    public static void updateItemInDatabase(Context context, final ItemInfo item) {
        final ContentValues values = new ContentValues();
//        item.onAddToDatabase(values);
        item.updateValuesWithSortId(values, item.cellSortId);
        values.put(Favorites.CONTAINER, item.container);
        updateItemInDatabaseHelper(context, values, item, "updateItemInDatabase");
    }

    public static void updateItemInDatabaseHelper(Context context, final ContentValues values, final ItemInfo item,
            final String callingFunction) {
        final long itemId = item.id;
        final Uri uri = GameLauncherSettings.Favorites.getContentUri(itemId, false);
        final ContentResolver cr = context.getContentResolver();

        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        Runnable r = new Runnable() {
            public void run() {
                cr.update(uri, values, null, null);
                // updateItemArrays(item, itemId, stackTrace);
            }
        };
        runOnWorkerThread(r);
    }

    /**
     * Move items in the DB to a new <container, screen, cellX, cellY>. We
     * assume that the cellX, cellY have already been updated on the ItemInfos.
     */
    public static void moveItemsInDatabase(Context context, final ArrayList<ItemInfo> items, final long container,
            final int screen) {

        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        int count = items.size();

        for (int i = 0; i < count; i++) {
            ItemInfo item = items.get(i);
            item.container = container;

            final ContentValues values = new ContentValues();
            values.put(GameLauncherSettings.Favorites.CELL_SORT_ID, item.getCellSortId());
            values.put(GameLauncherSettings.Favorites.CONTAINER, item.container);
            values.put(GameLauncherSettings.Favorites.SCREEN, item.screenId);

            contentValues.add(values);
        }
        updateItemsInDatabaseHelper(context, contentValues, items, "moveItemInDatabase");
    }
    
    /**
     * Move an item in the DB to a new <container, screen, cellX, cellY>
     */
    public static void moveItemInDatabase(Context context, final ItemInfo item) {
        final ContentValues values = new ContentValues();
        values.put(GameLauncherSettings.Favorites.CONTAINER, item.container);
        values.put(GameLauncherSettings.Favorites.TITLE, String.valueOf(item.title));
        updateItemInDatabaseHelper(context, values, item, "moveItemInDatabase");
    }

    public static void updateItemsInDatabaseHelper(Context context, final ArrayList<ContentValues> valuesList,
            final ArrayList<ItemInfo> items, final String callingFunction) {
        final ContentResolver cr = context.getContentResolver();

        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        Runnable r = new Runnable() {
            public void run() {
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                int count = items.size();
                for (int i = 0; i < count; i++) {
                    ItemInfo item = items.get(i);
                    final long itemId = item.id;
                    final Uri uri = GameLauncherSettings.Favorites.getContentUri(itemId, false);
                    ContentValues values = valuesList.get(i);

                    ops.add(ContentProviderOperation.newUpdate(uri).withValues(values).build());
                    // updateItemArrays(item, itemId, stackTrace);

                }
                try {
                    cr.applyBatch(GameLauncherProvider.AUTHORITY, ops);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                } catch (OperationApplicationException e1) {
                    e1.printStackTrace();
                }
            }
        };
        runOnWorkerThread(r);
    }
    

	public synchronized void handleGameAddOrUpdate(final String pkgName,final int displayMode,final int appType) {
		if (isSelfApp(pkgName) || !isPkgEnable(pkgName)) {
			return;
		}

		// 应用更新不做操作，没有就插入
		Runnable r = new Runnable() {
			public void run() {
				Cursor cursor = getCursorByPkgName(pkgName);
				try {
					if (null == cursor || !cursor.moveToFirst()) {
						ItemInfo info = createInfoByPackage(pkgName);
						if (info != null) {
							info.appType = appType;
							info.displayMode = displayMode;
							addItemToDatabase(mContext, info, false); // 已经初始化好了cellSortId;
						}
					}
				} finally {
					if (null != cursor) {
						cursor.close();
					}
				}
			}
		};
		runOnWorkerThread(r);
	}

	public synchronized void handleGameUpdate(final String pkgName) {
		if (isSelfApp(pkgName) || !isPkgEnable(pkgName)) {
			return;
		}

		// 应用更新不做操作，没有就插入
		Runnable r = new Runnable() {
			public void run() {
				Cursor cursor = getCursorByPkgName(pkgName);
				try {
					if (null != cursor && cursor.moveToFirst()) {
						ItemInfo newInfo = createInfoByPackage(pkgName);
						if (newInfo != null) {
							ItemInfo cursorInfo = transferCursorToItemInfo(cursor);
							updateItemByGameUpdate(mContext, cursorInfo, newInfo);
							bindGameAdd(cursorInfo, false);
						}
					}
				} finally {
					if (null != cursor) {
						cursor.close();
					}
				}
			}
		};
		runOnWorkerThread(r);
	}

	private boolean isPkgEnable(String pkgName) {
		try {
			int enableflag = mPkgManager.getApplicationEnabledSetting(pkgName);
			return (enableflag == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || enableflag == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private boolean isSelfApp(final String pkgName) {
		boolean isSelf = false;
		// 不对本应用进行监听
		if (!TextUtils.isEmpty(pkgName) && mContext.getPackageName().equals(pkgName)) {
			isSelf = true;
		}
		return isSelf;
	}

	private Cursor getCursorByPkgName(final String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return null;
		}
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(GameLauncherSettings.Favorites.CONTENT_URI, null,
				GameLauncherSettings.Favorites.PACKAGE_NAME + "=?", new String[] { pkgName }, null);
		return cursor;
	}

	protected ItemInfo createInfoByPackage(String pkgName) {
		if (TextUtils.isEmpty(pkgName) || !isPkgEnable(pkgName)) {
			return null;
		}
		try {
			Intent intent = mPkgManager.getLaunchIntentForPackage(pkgName);
			if (intent == null) {
				return null;
			}
			PackageInfo info = mPkgManager.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityInfos = info.activities;
			for (ActivityInfo activityInfo : activityInfos) {
				if (pkgName.equals(activityInfo.packageName)) {
					ShortcutInfo shortcutInfo = new ShortcutInfo();
					shortcutInfo.packageName = pkgName;
					shortcutInfo.intent = intent;
					shortcutInfo.itemType = Favorites.APP_TYPE_APPLICATION;
					shortcutInfo.title = activityInfo.loadLabel(mPkgManager);
					if (info.applicationInfo.enabled) {
						shortcutInfo.displayMode = Favorites.DISPLAY;
					} else {
						shortcutInfo.displayMode = Favorites.DONOT_DISPLAY;
					}
					return shortcutInfo;
				}
			}
			return null;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected ItemInfo createInfoByIntent(Intent intent) {
		ShortcutInfo info = new ShortcutInfo();
		ActivityInfo activityInfo = null;
		try {
			activityInfo = mPkgManager.getActivityInfo(intent.getComponent(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		info.packageName = activityInfo.packageName;
		info.intent = intent;
		info.itemType = Favorites.ITEM_TYPE_APPLICATION;
		info.title = activityInfo.loadLabel(mPkgManager);
		if (activityInfo.enabled) {
			info.displayMode = Favorites.DISPLAY;
		} else {
			info.displayMode = Favorites.DONOT_DISPLAY;
		}
		return info;
	}

	public synchronized void handleGameRemove(final String pkgName) {
		if (isSelfApp(pkgName)) {
			return;
		}
		Runnable r = new Runnable() {
			public void run() {
				synchronized (mLock) {
					Cursor cursor = null;
					try {
						cursor = getCursorByPkgName(pkgName);
						if (null != cursor && cursor.moveToFirst()) {
							ItemInfo info = transferCursorToItemInfo(cursor);
							deleteItemFromDatabase(mContext, info);
						}
					} finally {
						if (null != cursor) {
							cursor.close();
						}
					}
				}
			}
		};
		runOnWorkerThread(r);
	}

	public ItemInfo transferCursorToItemInfo(Cursor cursor) {
		ShortcutInfo info = new ShortcutInfo();
		if (cursor != null) {
			try {
				info.id = cursor.getLong(cursor.getColumnIndexOrThrow(Favorites._ID));
				info.cellSortId = cursor.getInt(cursor.getColumnIndexOrThrow(Favorites.CELL_SORT_ID));
				info.container = cursor.getInt(cursor.getColumnIndexOrThrow(Favorites.CONTAINER));
				info.itemType = cursor.getInt(cursor.getColumnIndexOrThrow(Favorites.ITEM_TYPE));
				info.appIcon = getIconFromCursor(cursor, cursor.getColumnIndexOrThrow(Favorites.ICON), mContext);
				info.title = cursor.getString(cursor.getColumnIndexOrThrow(Favorites.TITLE));
				String intentDes = cursor.getString(cursor.getColumnIndexOrThrow(Favorites.INTENT));
				Intent intent = Intent.parseUri(intentDes, 0);
				info.intent = intent;
				info.appType = cursor.getInt(cursor.getColumnIndexOrThrow(Favorites.APP_TYPE));
				info.packageName = cursor.getString(cursor.getColumnIndexOrThrow(Favorites.PACKAGE_NAME));
				info.displayMode = cursor.getInt(cursor.getColumnIndexOrThrow(Favorites.DISPLAY_MODE));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return info;
	}

    private synchronized void bindGameAdd(final ItemInfo info,final boolean isAdd){
        dataSync(info, true);
		final Runnable r = new Runnable() {

			@Override
			public void run() {
				for (Callbacks callbacks : mCallbacks) {
					if (callbacks != null) {
						callbacks.gameAddOrUpdate(info, isAdd);
					}
				}
			}
		};
		runOnMainThread(r);
	}

    private synchronized void bindGameRemove(final ItemInfo info){
        dataSync(info, false);
		final Runnable r = new Runnable() {

			@Override
			public void run() {
				for (Callbacks callbacks : mCallbacks) {
					if (callbacks != null) {
						callbacks.gameRemove(info);
					}
				}
			}
		};
		runOnMainThread(r);
    }

	private synchronized void dataSync(ItemInfo info, boolean isAdd) {
		if (info != null) {
			if (isAdd) {
				if (info.appType == Favorites.APP_TYPE_APPLICATION) {
					if (dataFilter(appInfos, info)) {
						return;
					}
					appInfos.add(info);
				} else if (info.appType == Favorites.APP_TYPE_GAME) {
					if (dataFilter(gameInfos, info)) {
						return;
					}
					gameInfos.add(info);
				}
			} else {
				updateDataByGameRemove(appInfos, info);
				updateDataByGameRemove(gameInfos, info);
			}
		}
	}

	private boolean dataFilter(final List<ItemInfo> appList, final ItemInfo info) {
		boolean isExist = false;
		for (ItemInfo appItem : appList) {
			if (appItem instanceof ShortcutInfo && appItem.equals(info)) {
				isExist = true;
				updateInfo((ShortcutInfo) appItem, (ShortcutInfo) info);
			}
		}
		return isExist;
	}

	private void updateInfo(ShortcutInfo oriInfo, ShortcutInfo newInfo) {
		oriInfo.intent = newInfo.intent;
		oriInfo.appIcon = newInfo.appIcon;
	}

	private void updateDataByGameRemove(final List<ItemInfo> appInfos, final ItemInfo info) {
		if (null != info && null != appInfos) {
			int size = appInfos.size();
			for (int i = 0; i < size; i++) {
				ItemInfo item = appInfos.get(i);
				if (null != item) {
					if (item instanceof ShortcutInfo) {
						ShortcutInfo appShortcutInfo = (ShortcutInfo) item;
						if (appShortcutInfo.equals(info)) {
							appInfos.remove(item);
							break;
						}
					}
				}
			}
		}
	}

	private synchronized void updateAppTypeAndDisplayMode(Cursor cursor, int appType, int displayMode) {
		int itemId = cursor.getInt(cursor.getColumnIndex(Favorites._ID));
		final Uri uri = GameLauncherSettings.Favorites.getContentUri(itemId, false);
		final ContentResolver cr = mContext.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Favorites.APP_TYPE, appType);
		values.put(Favorites.DISPLAY_MODE, displayMode);
		cr.update(uri, values, null, null);
	}

	private synchronized void updateAppModifiedTime(Cursor cursor, long modifiedTime) {
		int itemId = cursor.getInt(cursor.getColumnIndex(Favorites._ID));
		final Uri uri = GameLauncherSettings.Favorites.getContentUri(itemId, false);
		final ContentResolver cr = mContext.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Favorites.MODIFIED, modifiedTime);
		cr.update(uri, values, null, null);
	}

	public static class CellSortIdComparator implements Comparator<ItemInfo> {
		CellSortIdComparator() {

		}

		@Override
		public int compare(ItemInfo lhs, ItemInfo rhs) {
			return lhs.cellSortId - rhs.cellSortId;
		}
	};

	public synchronized void updateInstalledAppInfo(final String pkgName, final int displayMode, final int appType) {
		Runnable r = new Runnable() {
			public void run() {
				synchronized (mLock) {
					Cursor cursor = null;
					try {
						cursor = getCursorByPkgName(pkgName);
						if (null == cursor || !cursor.moveToFirst()) {
							// 还没有加入数据库
							ItemInfo info = createInfoByPackage(pkgName);
							if (info != null) {
								info.appType = appType;
								info.displayMode = displayMode;
								addItemToDatabase(mContext, info, false); // 已经初始化好了cellSortId;
							}
						} else {
							int preDisplayMode = cursor.getInt(cursor.getColumnIndex(Favorites.DISPLAY_MODE));
							int preAppType = cursor.getInt(cursor.getColumnIndex(Favorites.APP_TYPE));
							if ((preDisplayMode == displayMode) && (preAppType == appType)) {
								return;
							}
							updateAppTypeAndDisplayMode(cursor, appType, displayMode);
							cursor = updateCursorByPkgName(pkgName);
							if (cursor != null) {
								ItemInfo info = transferCursorToItemInfo(cursor);
								bindGameRemove(info);
								if (Favorites.DONOT_DISPLAY != displayMode) {
									bindGameAdd(info, true);
								}
							}
						}
					} finally {
						if (null != cursor) {
							cursor.close();
						}
					}
				}
			}
		};
		runOnWorkerThread(r);
	}

	public synchronized void updateModifiedTime(final String pkgName,final long modifiedTime) {
		Runnable r = new Runnable() {
			public void run() {
				synchronized (mLock) {
					Cursor cursor = null;
					try {
						cursor = getCursorByPkgName(pkgName);
						if (null == cursor || !cursor.moveToFirst()) {
							//还没有加入数据库
//							return;
						} else {
							updateAppModifiedTime(cursor,modifiedTime);
						}
					} finally {
						if (null != cursor) {
							cursor.close();
						}
					}
				}
			}
		};
		runOnWorkerThread(r);
	}

	private Cursor updateCursorByPkgName(String pkgName) {
		if (TextUtils.isEmpty(pkgName)) {
			return null;
		}
		Cursor cursor = getCursorByPkgName(pkgName);
		if (cursor == null || !cursor.moveToFirst()) {
			return null;
		}
		return cursor;
	}
}
