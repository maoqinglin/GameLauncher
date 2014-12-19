package com.ireadygo.app.gamelauncher.mygame.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.mygame.data.GameLauncherAppState;
import com.ireadygo.app.gamelauncher.mygame.info.FolderInfo;
import com.ireadygo.app.gamelauncher.mygame.info.ShortcutInfo;
import com.ireadygo.app.gamelauncher.mygame.ui.view.Folder;

public class ImageHelper {

	private static final String TAG = "ImageHelper";
	private static final float scaleFactor = 16; // 背景图片缩放比例
	private static final float radius = 4; // 设置模糊度

	// 文件夹预览图参数
	private static final int ICON_COUNT = 4;// 可显示的缩略图数
	private static final int NUM_COL = 2;// 每行显示的个数
	private static final int PADDING = 4;// 内边距
	private static final int MARGIN = 7;// 外边距的值

	public static Drawable blur(Context context, Rect rect, Bitmap bkg) {
		Bitmap overlay = Bitmap.createBitmap((int) (rect.width() / scaleFactor), (int) (rect.height() / scaleFactor),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.translate(-rect.left / scaleFactor, -rect.top / scaleFactor);
		canvas.scale(1 / scaleFactor, 1 / scaleFactor);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);

		canvas.drawBitmap(bkg, 0, 0, paint);
		overlay = FastBlur.doBlur(overlay, (int) radius, true); // 模糊处理

		Bitmap scaleBmp = Bitmap.createScaledBitmap(overlay, rect.width(), rect.height(), true);
		overlay.recycle();
		overlay = null;

		Bitmap roundBmp = getRoundedCornerBitmap(scaleBmp);
		scaleBmp.recycle();
		scaleBmp = null;

		BitmapDrawable drawable = new BitmapDrawable(context.getResources(), roundBmp);
		drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		return drawable;
	}
	
	public static void blur(Context context, Folder folder, Bitmap bkg) {
        Bitmap overlay = Bitmap.createBitmap((int) (folder.getWidth() / scaleFactor),
                (int) (folder.getHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-folder.getLeft() / scaleFactor, -folder.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);

        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true); // 模糊处理
        
        Bitmap scaleBmp = Bitmap.createScaledBitmap(overlay,folder.getWidth(), folder.getHeight(),true);
        overlay.recycle();
        overlay = null;
        
//        Bitmap roundBmp = getRoundedCornerBitmap(scaleBmp);
//        scaleBmp.recycle();
//        scaleBmp = null;
        
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), scaleBmp);
        drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        folder.setBackground(drawable);
    }

	/**
	 * 圆角处理
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		// 创建一个指定宽度和高度的空位图对象
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
				android.graphics.Bitmap.Config.ARGB_8888);
		// 用该位图创建画布
		Canvas canvas = new Canvas(output);
		// 画笔对象
		final Paint paint = new Paint();
		// 画笔的颜色
		final int color = 0xff424242;
		// 矩形区域对象
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		// 未知
		final RectF rectF = new RectF(rect);
		// 拐角的半径
		final float roundPx = 20;
		// 消除锯齿
		paint.setAntiAlias(true);
		// 画布背景色
		canvas.drawARGB(0, 0, 0, 0);
		// 设置画笔颜色
		paint.setColor(color);
		// 绘制圆角矩形
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		// 未知
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// 把该图片绘制在该圆角矩形区域中
		canvas.drawBitmap(bitmap, rect, rect, paint);
		// 最终在画布上呈现的就是该圆角矩形图片，然后我们返回该Bitmap对象
		return output;
	}

	public static void updateFolderIcon(Context context, FolderInfo folderInfo) {
		if (null == folderInfo) {
			return;
		}
		float x, y;
		final Resources resources = context.getResources();
		Bitmap closebmp = BitmapFactory.decodeResource(resources, R.drawable.icon_folder_bg); // 获取FolderIcon关闭时的背景图
		Bitmap openbmp = BitmapFactory.decodeResource(resources, R.drawable.icon_folder_bg); // 获取FolderIcon打开时的背景图

		int iconWidth = closebmp.getWidth(); // icon的宽度
		int iconHeight = closebmp.getHeight();
		Bitmap folderclose = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
		Bitmap folderopen = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(folderclose);
		canvas.drawBitmap(closebmp, 0, 0, null); // 绘制背景
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		float scaleWidth = (iconWidth - MARGIN * 2) / NUM_COL - 2 * PADDING; // 计算缩略图的宽(高与宽相同)
		float scale = (scaleWidth / iconWidth); // 计算缩放比例
		matrix.postScale(scale, scale); // 设置缩放比例
		for (int i = 0; i < ICON_COUNT; i++) {
			if (i < folderInfo.contents.size()) {
				x = MARGIN + PADDING * (2 * (i % NUM_COL) + 1) + scaleWidth * (i % NUM_COL);
				y = MARGIN + PADDING * (2 * (i / NUM_COL) + 1) + scaleWidth * (i / NUM_COL);
				ShortcutInfo scInfo = (ShortcutInfo) folderInfo.contents.get(i);
				Bitmap iconbmp = scInfo.getIcon(GameLauncherAppState.getInstance(context).getIconCache()); // 获取缩略图标
				Bitmap scalebmp = Bitmap.createBitmap(iconbmp, 0, 0, iconWidth, iconHeight, matrix, true);
				canvas.drawBitmap(scalebmp, x, y, null);
			}
		}
		// mCloseIcon = new FastBitmapDrawable(folderclose);
		// //将bitmap转换为Drawable
		// setCompoundDrawablesWithIntrinsicBounds(null, mCloseIcon, null,
		// null);
		canvas = new Canvas(folderopen);
		canvas.drawBitmap(folderclose, 0, 0, null);
		canvas.drawBitmap(openbmp, 0, 0, null);
		folderInfo.appIcon = openbmp;
		// mOpenIcon = new FastBitmapDrawable(folderopen); //绘制open图片
	}
}
