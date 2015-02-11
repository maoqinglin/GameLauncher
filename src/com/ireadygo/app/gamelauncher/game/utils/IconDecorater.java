package com.ireadygo.app.gamelauncher.game.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings.SettingNotFoundException;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.game.info.ItemInfo;
import com.ireadygo.app.gamelauncher.game.info.ShortcutInfo;
import com.ireadygo.app.gamelauncher.game.utils.LoadThemeUtil.ThemeApp;

public class IconDecorater {

	private static final String TAG = IconDecorater.class.getSimpleName();

	private final String MTK_ACTION_UNREAD_CHANGED = "com.mediatek.action.UNREAD_CHANGED";
	private final String MTK_EXTRA_UNREAD_COMPONENT = "com.mediatek.intent.extra.UNREAD_COMPONENT";
	private final String MTK_EXTRA_UNREAD_NUMBER = "com.mediatek.intent.extra.UNREAD_NUMBER";

	private final String MMS_CLASSNAME = "com.android.mms.ui.BootActivity";
	private final String CONTACTS_CLASSNAME = "com.android.contacts.activities.DialtactsActivity";
	private final String EMAIL_CLASSNAME = "com.android.email.activity.Welcome";
	private final String CALENDAR_CLASSNAME = "com.android.calendar.AllInOneActivity";

	private final String EMAIL_UNREAD_KEY = "com_android_email_mtk_unread";
	private final String CONTACTS_UNREAD_KEY = "com_android_contacts_mtk_unread";
	private final String MMS_UNREAD_KEY = "com_android_mms_mtk_unread";
	private final String CALENDAR_UNREAD_KEY = "com_android_calendar_mtk_unread";
	private final String INVALID_KEY = "INVALID_KEY";
	private static final String ACTION_UPDATE_UI = "com.ireadygo.app.gamelauncher.ACTION_UPDATE_UI";
	private static final String EXTRA_CLS_NAME = "classname";
	private static final String EXTRA_UNREAD_COUNT = "unread";

	private ArrayList<ThemeApp> mThemeApps = new ArrayList<ThemeApp>();
	private final HashMap<String, WeakReference<ItemInfo>> mObservedItemInfo = new HashMap<String, WeakReference<ItemInfo>>();
	private final HashMap<String, Bitmap> mObservedIcons = new HashMap<String, Bitmap>();
	private Context mContext;
	private PackageManager mPackageManager;
	private int mIconDpi;
	private Bitmap mIconPattern;
	private Bitmap mIconMask;
	private Bitmap mHighligth;

	private boolean mIconFlag = true;
	private static final SpannableStringBuilder EXCEED_STRING = new SpannableStringBuilder("99+");

	/**
	 * Generate a text contains specified span to display the unread information
	 * when the value is more than 99, do not use toString to convert it to
	 * string, that may cause the span invalid.
	 */
	static {
		EXCEED_STRING.setSpan(new SuperscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		EXCEED_STRING.setSpan(new AbsoluteSizeSpan(22), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public IconDecorater(Context context) {
		mContext = context;
		mPackageManager = context.getPackageManager();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		mIconDpi = activityManager.getLauncherLargeIconDensity();
		mThemeApps.addAll(LoadThemeUtil.loadThemeInfos(context));
		mIconPattern = LoadThemeUtil.getIconPattern(context);
		mIconMask = LoadThemeUtil.getIconMask(context);
		mHighligth = LoadThemeUtil.getIconHighlight(context);

		mContext.registerReceiver(mUnreadReceiver, new IntentFilter(MTK_ACTION_UNREAD_CHANGED));
		// initIconFlag();
	}

	public Bitmap decorateIcon(ResolveInfo info) {
		Bitmap icon = null;
		ThemeApp app = new ThemeApp(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
		if (mThemeApps.contains(app)) {
			ThemeApp item = mThemeApps.get(mThemeApps.indexOf(app));
			icon = LoadThemeUtil.getThemeIcon(mContext, item);
		}

		if (icon == null) {
			icon = drawableToBitmap(getFullResIcon(info));
			//TODO 新版应用图标采用海报
//			icon = decorateIcon(mContext.getResources(), icon);
		}

		return icon;
	}


	public void observeIconNeedUpdated(ItemInfo info, Bitmap icon, ComponentName componentName) {
		String className = componentName == null ? "" : componentName.getClassName();
		if (!canIconBeObserved(className)) {
			return;
		}

		String key = getUnreadQueryKey(className);
		int count = queryUnreadNum(key);
		Bitmap bitmap = markCountIcon(mContext.getResources(), icon, count);
		info.setAppIcon(bitmap);
		synchronized (mObservedItemInfo) {
			mObservedItemInfo.remove(className);
			mObservedIcons.remove(className);
			mObservedItemInfo.put(className, new WeakReference<ItemInfo>(info));
			mObservedIcons.put(className, icon);
		}
		broadcastToUpdateUI(className,count);
	}

	private void broadcastToUpdateUI(String className,int count) {
		Intent intent = new Intent(ACTION_UPDATE_UI);
		intent.putExtra(EXTRA_CLS_NAME, className);
		intent.putExtra(EXTRA_UNREAD_COUNT, count);
		mContext.sendBroadcast(intent);
	}

	private int queryUnreadNum(String key) {
		int unreadNum = 0;
		try {
			unreadNum = android.provider.Settings.System.getInt(mContext.getContentResolver(), key);
		} catch (SettingNotFoundException e) {
			// ignore
		}

		return unreadNum;
	}

	private Bitmap decorateIconNum(Bitmap icon, String className) {
		String key = getUnreadQueryKey(className);
		int count = queryUnreadNum(key);
		return markCountIcon(mContext.getResources(), icon, count);
	}

	private String getUnreadQueryKey(String className) {
		String key = INVALID_KEY;
		if (MMS_CLASSNAME.equals(className)) {
			key = MMS_UNREAD_KEY;
		} else if (CONTACTS_CLASSNAME.equals(className)) {
			key = CONTACTS_UNREAD_KEY;
		} else if (EMAIL_CLASSNAME.equals(className)) {
			key = EMAIL_UNREAD_KEY;
		}

		return key;
	}

	private boolean canIconBeObserved(String className) {
		return MMS_CLASSNAME.equals(className) || CONTACTS_CLASSNAME.equals(className)
				|| EMAIL_CLASSNAME.equals(className);
	}

	public void shutdown() {
		mContext.unregisterReceiver(mUnreadReceiver);
	}

	private BroadcastReceiver mUnreadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ComponentName componentName = (ComponentName) intent.getExtras().get(MTK_EXTRA_UNREAD_COMPONENT);
			int unreadNum = intent.getIntExtra(MTK_EXTRA_UNREAD_NUMBER, 0);
			String clsName = componentName.getClassName();
			synchronized (mObservedItemInfo) {
				WeakReference<ItemInfo> wItemInfo = mObservedItemInfo.get(clsName);
				if (wItemInfo != null) {
					ItemInfo itemInfo = wItemInfo.get();
					if (itemInfo == null) {
						mObservedItemInfo.remove(clsName);
						mObservedIcons.remove(clsName);
						return;
					}
					Bitmap newIcon = markCountIcon(mContext.getResources(), mObservedIcons.get(clsName), unreadNum);
					itemInfo.setAppIcon(newIcon);
					broadcastToUpdateUI(clsName,unreadNum);
				}
			}
		}
	};

	public Bitmap decorateIcon(Resources resources, Bitmap icon) {
		return decorateIcon(resources, icon, mIconPattern);
	}

	public Bitmap decorateIcon(Resources resources, Bitmap icon, Bitmap bottom) {
		int appIconSize = (int) resources.getDimension(R.dimen.mygame_app_icon_size);
		Bitmap bitmapIcon = icon;
//		int widthD = (int) (bitmapIcon.getWidth() * 0.04);
//		int heightD = (int) (bitmapIcon.getHeight() * 0.04);
//		bitmapIcon = PictureUtil.cutBitmap(bitmapIcon, new Rect(widthD, heightD, bitmapIcon.getWidth() - 2 * widthD,
//				bitmapIcon.getHeight() - 2 * heightD), bitmapIcon.getConfig());
		if (icon.getWidth() != appIconSize || icon.getHeight() != appIconSize) {
			bitmapIcon = Bitmap.createScaledBitmap(icon, appIconSize, appIconSize, true);
		}

		Bitmap maskedIcon = maskIcon(mIconMask.getWidth(), mIconMask.getHeight(), bitmapIcon, mIconMask);
		return overlayBitmaps(bottom.getWidth(), bottom.getHeight(), bottom, maskedIcon,mHighligth); //加底
//		return overlayBitmaps(maskedIcon.getWidth(), maskedIcon.getHeight(), maskedIcon,mHighligth); //不加底
	}

	public Drawable decorateIcon(Resources resources, Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return new BitmapDrawable(resources, decorateIcon(resources, ((BitmapDrawable) drawable).getBitmap()));
		}

		Log.w(TAG, "Argument must be BitmapDrawable type, so can't be decorated.");
		return drawable;
	}

	private Bitmap maskIcon(int width, int height, Bitmap icon, Bitmap mask) {
		float left = Math.abs(icon.getWidth() - mask.getWidth()) / 2;
		float top = Math.abs(icon.getHeight() - mask.getHeight()) / 2;
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(mask, 0, 0, null);

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(icon, left, top, paint);
//		canvas.drawBitmap(icon, 0, 0, paint);
		return result;
	}

	public Bitmap overlayBitmaps(int width, int height, Bitmap... bitmaps) {
		int maxWidth = 0;
		for (Bitmap bitmap : bitmaps) {
			if (bitmap.getWidth() >= maxWidth) {
				maxWidth = bitmap.getWidth();
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		for (Bitmap bitmap : bitmaps) {
			float left = Math.abs(maxWidth - bitmap.getWidth()) / 2;
			float top = Math.abs(maxWidth - bitmap.getHeight()) / 2;
			canvas.drawBitmap(bitmap, left, top, null);
		}
		return result;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	private Bitmap markCountIcon(Resources resources, Bitmap icon, int count) {
		if (count < 1) {
			return icon;
		}
		// 初始化画布
		int appIconSize = icon.getWidth();
		Bitmap contactIcon = Bitmap.createBitmap(appIconSize, appIconSize, Config.ARGB_8888);

		Canvas canvas = new Canvas(contactIcon);
		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖动
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
		Rect src = new Rect(0, 0, appIconSize, appIconSize);
		Rect dst = new Rect(0, 0, appIconSize, appIconSize);
		canvas.drawBitmap(icon, src, dst, iconPaint);
		Bitmap indicate = BitmapFactory.decodeResource(resources, R.drawable.much_new_number_bg);
		canvas.drawBitmap(indicate, appIconSize - indicate.getWidth(), 0, iconPaint);
		// 启用抗锯齿和使用设备的文本字距
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(Color.WHITE);
		countPaint.setTextSize(20f);
		countPaint.setTypeface(Typeface.DEFAULT_BOLD);
		if (count < 10) {
			canvas.drawText(String.valueOf(count), appIconSize - 2 * indicate.getWidth() / 3, 25, countPaint);
		} else {
			if (count > 99) {
				count = 99;
			}
			canvas.drawText(String.valueOf(count), appIconSize - 6 * indicate.getWidth() / 7, 25, countPaint);
		}
		return contactIcon;
	}

	public Drawable getFullResDefaultActivityIcon() {
		return getFullResIcon(Resources.getSystem(), android.R.mipmap.sym_def_app_icon);
	}

	public Drawable getFullResIcon(Resources resources, int iconId) {
		Drawable d;
		try {
			d = resources.getDrawableForDensity(iconId, mIconDpi);
		} catch (Resources.NotFoundException e) {
			d = null;
		}

		return (d != null) ? d : getFullResDefaultActivityIcon();
	}

	public Drawable getFullResIcon(ResolveInfo info) {
		return getFullResIcon(info.activityInfo);
	}

	public Drawable getFullResIcon(ActivityInfo info) {

		Resources resources;
		try {
			resources = mPackageManager.getResourcesForApplication(info.applicationInfo);
		} catch (PackageManager.NameNotFoundException e) {
			resources = null;
		}
		if (resources != null) {
			int iconId = info.getIconResource();
			if (iconId != 0) {
				return getFullResIcon(resources, iconId);
			}
		}
		return getFullResDefaultActivityIcon();
	}

	public void reLoadIconPattern() {
		mIconPattern = LoadThemeUtil.getIconPattern(mContext);
	}
	// public void initIconFlag(){
	// mIconFlag = MuchConfig.getInstance().isLauncherShortcutNeedBg();
	// }
	
    public int observeFolderIconNeedUpdated(ArrayList<ShortcutInfo> infos) {
        int unReadCount = 0;
        if (infos != null) {
            if (infos.isEmpty()) {
                return 0;
            }
            for (ShortcutInfo info : infos) {
                String className = info.getIntent().getComponent() == null ? "" : info.getIntent().getComponent()
                        .getClassName();
                if (!canIconBeObserved(className)) {
                    continue;
                }

                String key = getUnreadQueryKey(className);
                unReadCount = queryUnreadNum(key);
                if (unReadCount > 0) {
                    break; // 如果有未读记录，不需要遍历后面的item
                }
            }
        }
        return unReadCount;
    }
}
