package com.ireadygo.app.gamelauncher.account;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.account.pushmsg.SnailPushMessage;
import com.ireadygo.app.gamelauncher.account.pushmsg.SnailPushMessageColumns;
import com.ireadygo.app.gamelauncher.account.pushmsg.SnailPushMessageProvider;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;
import com.snail.appstore.openapi.accountstatus.AccountStatusManager;
import com.snailgame.mobilesdk.LoginResultListener;
import com.snailgame.mobilesdk.OnInitCompleteListener;
import com.snailgame.mobilesdk.OnQueryBalanceListener;
import com.snailgame.mobilesdk.SnailCommplatform;
import com.snailgame.sdkcore.open.InitCompleteListener;

public class AccountManager {
	private static final int MAX_NUM_MESSAGE = 20;
	private boolean mIsInitSuccess = false;
	private static AccountManager mAccountManager = new AccountManager();
	private static final String TAG = "AccountManager";

	private AccountManager() {
	}

	public static AccountManager getInstance() {
		return mAccountManager;
	}

	/** 初始化 **/
	public void init(Activity activity, InitCompleteListener listener) {
		SnailCommplatform.getInstance().snailInit(activity, GameLauncherConfig.getChennelId(), listener);
	}

	public void init(final Activity activity,String channelId) {
		SnailCommplatform.getInstance().snailInit(activity, channelId, new InitCompleteListener() {
			@Override
			public void onComplete(int arg0) {
				switch (arg0) {
				case OnInitCompleteListener.FLAG_NORMAL:
					mIsInitSuccess = true;
					AccountStatusManager.getInstance().setLoginData(getLoginUni(activity), getSessionId(activity));
					break;
				case OnInitCompleteListener.FLAG_FORCE_CLOSE:
				default:
					Log.e(TAG, "One Key Login failed!");
					break;
				}
			}
		});
	}

	/** 一键登陆 **/
	public void oneKeyLogin(final Activity activity, final LoginResultListener listener) {
		if (!mIsInitSuccess) {
			init(activity, new InitCompleteListener() {

				@Override
				public void onComplete(int arg0) {
					switch (arg0) {
					case OnInitCompleteListener.FLAG_NORMAL:
						mIsInitSuccess = true;
						SnailCommplatform.getInstance().snailOneKeyLogin(activity, listener);
						break;
					case OnInitCompleteListener.FLAG_FORCE_CLOSE:
					default:
						Log.e(TAG, "One Key Login failed!");
						listener.onFailure(StatusCode.UNINITIALIZED);
						break;
					}
				}
			});
		} else {
			SnailCommplatform.getInstance().snailOneKeyLogin(activity, listener);
		}
	}

	/** 普通登陆 **/
	public void generalLogin(final Activity activity, final String account, final String password,
			final LoginResultListener listener) {
		if (!mIsInitSuccess) {
			init(activity, new InitCompleteListener() {

				@Override
				public void onComplete(int arg0) {
					switch (arg0) {
					case OnInitCompleteListener.FLAG_NORMAL:
						mIsInitSuccess = true;
						SnailCommplatform.getInstance().snailGeneralLogin(activity, account, password, listener);
						break;
					case OnInitCompleteListener.FLAG_FORCE_CLOSE:
					default:
						Log.e(TAG, "generalLogin failed!");
						listener.onFailure(StatusCode.UNINITIALIZED);
						break;
					}
				}
			});
		} else {
			SnailCommplatform.getInstance().snailGeneralLogin(activity, account, password, listener);
		}
	}

	/** 普通注册 **/
	public void generalRegister(final Activity activity, final String account, final String password,
			final LoginResultListener listener) {
		if (!mIsInitSuccess) {
			init(activity, new InitCompleteListener() {

				@Override
				public void onComplete(int arg0) {
					switch (arg0) {
					case OnInitCompleteListener.FLAG_NORMAL:
						mIsInitSuccess = true;
						SnailCommplatform.getInstance().snailGeneralRegister(activity, account, password, listener);
						break;
					case OnInitCompleteListener.FLAG_FORCE_CLOSE:
					default:
						Log.e(TAG, "generalLogin failed!");
						listener.onFailure(StatusCode.UNINITIALIZED);
						break;
					}
				}
			});
		} else {
			SnailCommplatform.getInstance().snailGeneralRegister(activity, account, password, listener);
		}
	}

	public void gotoChangePwdPage(Activity activity, boolean landscape) {
		SnailCommplatform.getInstance().snailChangePsw(activity, landscape);
	}

	/** 注销 **/
	public void logout(Activity activity) {
		SnailCommplatform.getInstance().snailLogout(activity);
		PreferenceUtils.setBSSAccount(false);
		PreferenceUtils.saveBindPhoneNum("");
		PreferenceUtils.savePhoneNum("");
		PreferenceUtils.setSlotNum(GameLauncherConfig.DEFAULT_SLOT_NUM);
		AccountStatusManager.getInstance().clearLoginData();
	}

	/** 销毁 **/
	public void destory() {
		SnailCommplatform.getInstance().snailDestroy();
		PreferenceUtils.setBSSAccount(false);
	}

	/** 查询余额 **/
	public void queryBalance(Context context, OnQueryBalanceListener listener) {
		SnailCommplatform.getInstance().snailQueryBalance(context, listener);
	}

	/** 跳转支付列表 **/
	public void gotoCharge(Activity activity, boolean isLandscape) {
		SnailCommplatform.getInstance().snailGoToCharge(activity, isLandscape);
	}

	/** 获取账号 **/
	public String getAccount(Context context) {
		return SnailCommplatform.getInstance().getAccount(context);
	}

	/** 判断是否是登陆状态 **/
	public boolean isLogined(Context context) {
		return SnailCommplatform.getInstance().isLogined(context);
	}

	/** 获取用户唯一标识 **/
	public String getLoginUni(Context context) {
		return SnailCommplatform.getInstance().getLoginUin(context);
	}

	/** 获取SessionId **/
	public String getSessionId(Context context) {
		return SnailCommplatform.getInstance().getSessionId(context);
	}

	/** 获取用户昵称 **/
	public String getNickName(Context context) {
		return SnailCommplatform.getInstance().getNickName(context);
	}

	/** 获取用户头像名称 **/
	public String getPhotoName(Context context) {
		return SnailCommplatform.getInstance().getPhoto(context);
	}

	public void uploadGetuiInfo(final Context context) {
		GameLauncherThreadPool.getCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				//上传终端个推相关信息
				try {
					String tags[] = GameInfoHub.instance(context).uploadGeituiInfo(
							GameLauncherConfig.GETUI_CLIENTID,
							GameLauncherConfig.GETUI_APPID,
							GameLauncherConfig.getChennelId(),
							GameLauncherConfig.PHONE_TYPE);
					//根据返回的tag，设置个推的tag
					if (tags != null && tags.length > 0) {
						Tag[] tagParam = new Tag[tags.length];
						for (int i = 0; i < tags.length; i++) {
							Tag t = new Tag();
							t.setName(tags[i]);
							tagParam[i] = t;
						}
						int result = PushManager.getInstance().setTag(context, tagParam);
						Log.w(TAG, "PushManager setTag result:"+result);
					}
				} catch (InfoSourceException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SnailPushMessage queryNotificationById(Context context, long id) {
		Uri uri = Uri.withAppendedPath(SnailPushMessageProvider.URI_ITEM, String.valueOf(id));
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		ArrayList<SnailPushMessage> messageList = getMessageListByCursor(cursor);
		if (messageList != null && !messageList.isEmpty()) {
			return messageList.get(0);
		}
		return null;
	}

	public ArrayList<SnailPushMessage> queryAllNotification(Context context) {
		Cursor cursor = context.getContentResolver().query(ContentUris.withAppendedId(SnailPushMessageProvider.URI_LIMIT, MAX_NUM_MESSAGE), 
				null, null, null, SnailPushMessageColumns.COLUMN_MSG_CREATE_DATE + " DESC");
		return getMessageListByCursor(cursor);
	}

	/**
	 * 插入新增消息
	 * @param context
	 * @param message
	 */
	public int addSnailPushMessage(Context context, SnailPushMessage message) {
		ContentValues cv = new ContentValues();
		ContentResolver resolver = context.getContentResolver();
		cv.put(SnailPushMessageColumns.COLUMN_MSG_TITLE, message.getTitle());
		cv.put(SnailPushMessageColumns.COLUMN_MSG_CONTENT, message.getContent());
		cv.put(SnailPushMessageColumns.COLUMN_MSG_CREATE_DATE, System.currentTimeMillis());
		cv.put(SnailPushMessageColumns.COLUMN_MSG_TYPE, message.getType());
		cv.put(SnailPushMessageColumns.COLUMN_MSG_TEXT, message.getText());
		cv.put(SnailPushMessageColumns.COLUMN_MSG_PAGE_ID, message.getPageId());
		cv.put(SnailPushMessageColumns.COLUMN_MSG_PAGE_TITLE, message.getPageTitle());
		cv.put(SnailPushMessageColumns.COLUMN_MSG_URL, message.getUrl());
		Uri insertUri = resolver.insert(SnailPushMessageProvider.URI, cv);
		return Integer.valueOf(insertUri.getLastPathSegment());
	}

	/**
	 * 删除单条消息
	 * @param context
	 * @param message
	 */
	public int deleteSnailPushMsg(Context context, int id) {
		ContentResolver cr = context.getContentResolver();
		int count = cr.delete(SnailPushMessageProvider.URI, SnailPushMessageColumns._ID + " = " + String.valueOf(id), null);
		return count;
	}

	public int deleteExceptNewestSnailPushMsg(Context context) {
		return 0;
	}

	private ArrayList<SnailPushMessage> getMessageListByCursor(Cursor cursor) {
		ArrayList<SnailPushMessage> messageList = new ArrayList<SnailPushMessage>();
		if (cursor.moveToFirst()) {
			int idIndex = cursor.getColumnIndex(SnailPushMessageColumns._ID);
			int titleIndex = cursor.getColumnIndex(SnailPushMessageColumns.COLUMN_MSG_TITLE);
			int contentIndex = cursor.getColumnIndex(SnailPushMessageColumns.COLUMN_MSG_CONTENT);
			int createDateIndex = cursor.getColumnIndex(SnailPushMessageColumns.COLUMN_MSG_CREATE_DATE);
			int typeIndex = cursor.getColumnIndex(SnailPushMessageColumns.COLUMN_MSG_TYPE);
			int urlIndex = cursor.getColumnIndex(SnailPushMessageColumns.COLUMN_MSG_URL);
			int pageIdIdex = cursor.getColumnIndex(SnailPushMessageColumns.COLUMN_MSG_PAGE_ID);
			int pageTitleIndex = cursor.getColumnIndex(SnailPushMessageColumns.COLUMN_MSG_PAGE_TITLE);
			int textIndex = cursor.getColumnIndex(SnailPushMessageColumns.COLUMN_MSG_TEXT);
			do {
				int id = cursor.getInt(idIndex);
				String content = cursor.getString(contentIndex);
				String title = cursor.getString(titleIndex);
				long createDate = cursor.getLong(createDateIndex);
				int type = cursor.getInt(typeIndex);
				String pageId = cursor.getString(pageIdIdex);
				String pageTitle = cursor.getString(pageTitleIndex);
				String url = cursor.getString(urlIndex);
				String text = cursor.getString(textIndex);

				SnailPushMessage pushMsg = new SnailPushMessage(createDate, type, url, pageId, pageTitle, text);
				pushMsg.setId(id);
				pushMsg.setContent(content);
				pushMsg.setTitle(title);
				messageList.add(pushMsg);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return messageList;
	}

	public SnailPushMessage stringToSnailPushMessage(String msg) {
		SnailPushMessage message = new SnailPushMessage();
		try {
			JSONObject jsonObject = new JSONObject(msg);
			message.setTitle(jsonObject.optString(MESSAGE_JSON_KEY.title));
			message.setContent(jsonObject.optString(MESSAGE_JSON_KEY.content));

			JSONObject jsonMsgObj = jsonObject.getJSONObject(MESSAGE_JSON_KEY.expand_message);
			message.setCreateDate(System.currentTimeMillis());
			message.setUrl(jsonMsgObj.optString(MESSAGE_JSON_KEY.url));
			message.setPageId(jsonMsgObj.optString(MESSAGE_JSON_KEY.pageId));
			message.setPageTitle(jsonMsgObj.optString(MESSAGE_JSON_KEY.pageTitle));
			message.setText(jsonMsgObj.optString(MESSAGE_JSON_KEY.text));
			message.setType(jsonMsgObj.optInt(MESSAGE_JSON_KEY.type));
			return message;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	private interface MESSAGE_JSON_KEY {
		String title = "title";
		String content = "content";
		String expand_message = "expand_message";
		String type = "type";
		String url = "url";
		String pageId = "pageId";
		String pageTitle = "pageTitle";
		String text = "text";
	}
}
