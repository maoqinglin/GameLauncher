package com.ireadygo.app.gamelauncher.ui.wx;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.activity.BaseGuideActivity;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;

public class WeiXinQRcodeActivity extends BaseGuideActivity {

	private static final String ACTION_WX_PUBLICMANAGER = "action.ireadygo.app.wxpublicmanager";
	private static final String ACTION_GAMELAUNCHER = "action.ireadygo.app.gamelauncher";
	private static final String KEY_EXPIRETIME = "EXPIRETIME";
	private static final String KEY_URL = "URL";
	private static final String KEY_CMD = "CMD";
	private static final String KEY_ITEM = "ITEM";
	private static final String VALUE_QR = "QR";
	private ImageView mQRCodeView;
	private long mExpiretime = 1800;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String cmd = intent.getStringExtra(KEY_CMD);
			String msg = intent.getStringExtra(KEY_ITEM);

			if(VALUE_QR.equals(cmd)) {
				try {
					JSONObject json = new JSONObject(msg);
					String url = json.getString(KEY_URL);
					long time = json.getLong(KEY_EXPIRETIME);
					mQRCodeView.setImageBitmap(createQRImage(url, mQRCodeView.getWidth(), mQRCodeView.getHeight()));

					if(time != 0) {
						mExpiretime = time;
					}
					PreferenceUtils.saveWxQrUrlExpiretime(System.currentTimeMillis());
					PreferenceUtils.saveWxQrUrl(url);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wx_qrcode_activity);

		init();
	}


	private void init() {
		initHeaderView(R.string.wx_title);
		mQRCodeView = (ImageView) findViewById(R.id.wx_qrcode_image);

		IntentFilter filter = new IntentFilter(ACTION_GAMELAUNCHER);
		registerReceiver(mReceiver, filter);

		String url = PreferenceUtils.getWxQrUrl();
		if(System.currentTimeMillis() - PreferenceUtils.getWxQrUrlExpiretime() > mExpiretime
				|| TextUtils.isEmpty(url)) {
			queryWxQrUrl();
		} else {
			mQRCodeView.setImageBitmap(createQRImage(url, mQRCodeView.getWidth(), mQRCodeView.getHeight()));
		}
	}

	private void queryWxQrUrl() {
		Intent intent = new Intent(ACTION_WX_PUBLICMANAGER);
		intent.putExtra(KEY_CMD, VALUE_QR);
		sendBroadcast(intent);
	}

	/**
     * 二维码生成
     * @param url
     * @param width
     * @param height
     * @return
     */
	private Bitmap createQRImage(String url, int width, int height) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
			int[] pixels = new int[width * height];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
}
