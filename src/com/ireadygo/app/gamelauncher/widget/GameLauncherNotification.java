package com.ireadygo.app.gamelauncher.widget;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.pushmsg.SnailPushMessage;
import com.ireadygo.app.gamelauncher.account.pushmsg.SnailPushMessage.Type;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.manager.GameStateManager;
import com.ireadygo.app.gamelauncher.ui.detail.GameDetailActivity;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;

public class GameLauncherNotification {
	private static final int TYPE_DOWNLOADING_NOTIFICATION = 0;
	private static final int TYPE_DOWNLOADED_NOTIFICATION = 1;
	private static final int TYPE_UPGRADE_NOTIFICATION = 2;
	private static final int TYPE_SLOT_EXPIRED_NOTIFICATION = 3;
	private static final int TYPE_MSG_NOTIFICATION = 4;
	private static final String LITTLE_TITLE_SEPERATOR = ",";
	private static final String ACTION_CLEAR_DOWNLOAD_COMPLETED_NUM = "CLEAR_DOWNLOAD_COMPLETED_NUM";
	private static final String INTENT_EXTRA = "FragmentTag";
	private static final String COLLECTION = "COLLECTION";
	private Context mContext;
	private NotificationManager mManager = null;
	private Map<Integer, Notification> mNotificationMap = new HashMap<Integer, Notification>();
	private GameStateManager mGameStateManager;
	private static int mDownloadCompleteNum = 0;

	public GameLauncherNotification(Context context,GameStateManager gsm) {
		mContext = context;
		mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mGameStateManager = gsm;
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_CLEAR_DOWNLOAD_COMPLETED_NUM);
		mContext.registerReceiver(mReceiver, intentFilter);
	}

	public void shutdown() {
		mContext.unregisterReceiver(mReceiver);
	}

	private PendingIntent setPendingIntentMsg(int id,SnailPushMessage msg,int type) {
		Intent intent = getIntentByMsgType(msg);
		return PendingIntent.getActivity(GameLauncherApplication.getApplication(), type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private Intent getIntentByMsgType(SnailPushMessage msg) {
		int type = msg.getType();
		String pageId = msg.getPageId();
		switch (msg.getType()) {
		case Type.GOTO_COMP_PAGE:
			return getSkipCategoryIntent(pageId);
		case Type.GOTO_GAME_DETAIL:
			return getSkipDetailIntent(pageId);
		case Type.RELATED_ACTIVITIES_PAGE:
			String url = msg.getUrl();
			if (!TextUtils.isEmpty(url)) {
				return getSkipWebIntent(url);
			}
		case Type.APP_DOWNLOAD:
			return getSkipDetailIntent(pageId);
		default:
			return new Intent();
		}
	}

	private Intent getSkipDetailIntent(String appId) {
		Intent intent = new Intent();
		intent.setClass(GameLauncherApplication.getApplication().getCurrentActivity(), GameDetailActivity.class);
		intent.putExtra(GameLauncherConfig.APP_ID, appId);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
	}

	private Intent getSkipWebIntent(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		return intent;
	}

	private Intent getSkipCategoryIntent(String categoryId) {
		Anchor anchor = new Anchor(Destination.CATEGORY_DETAIL);
		anchor.setArgs1(categoryId);
		return anchor.getIntent();
	}


	private PendingIntent setPendingIntentUpgrade(int type) {
		Anchor anchor = new Anchor(Destination.STORE_GAME_MANAGE);
		Intent intent = anchor.getIntent();
		return PendingIntent.getActivity(mContext, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private PendingIntent setPendingIntentToDownloadManager(int type) {
		Anchor anchor = new Anchor(Destination.STORE_GAME_MANAGE);
		Intent intent = anchor.getIntent();
		return PendingIntent.getActivity(mContext, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private PendingIntent setPendingIntentToAllApp(int type) {
		Intent intent = new Intent(mContext, null);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		return PendingIntent.getActivity(mContext, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private RemoteViews setRemoteViews() {
//		final RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.download_notification);
//		
//		view.setTextViewText(R.id.name, dldItem.getName());
//		view.setTextViewText(R.id.text, mContext.getString(R.string.status_begin_download));
//		view.setProgressBar(R.id.progress, 100, 
//				dldItem.getTotalSize() == 0 ? 0 : (int)(dldItem.getDldedSize() * 100 / dldItem.getTotalSize()), false);
//		view.setTextViewText(R.id.text, mContext.getString(R.string.status_downloaded) 
//				+ (dldItem.getTotalSize() == 0 ? 0 : (int)(dldItem.getDldedSize() * 100 / dldItem.getTotalSize())) + "%");
//		
//		try {
//			view.setImageViewBitmap(R.id.icon, PictureUtil.mergerBitmap(mContext, infoSource.retrieveCachedAppIcon(dldItem.getId())));
//		} catch (CacheNotFoundException e) {
//			infoSource.retrieveRemoteAppIcon(dldItem.getId(), new DEResponse<Bitmap, IInfoSource.InfoSourceException>() {
//				
//				@Override
//				public void onSuccessful(Bitmap bm) {
//					view.setImageViewBitmap(R.id.icon, PictureUtil.mergerBitmap(mContext, bm));
//				}
//				
//				@Override
//				public void onFailed(InfoSourceException e) {
//					view.setImageViewResource(R.id.icon, R.drawable.icon);
//				}
//			});
//		}
		return null;
	}
	
	/*
	 * 添加下载中的通知
	 */
	public void addDownloadingNotification() {
		String titles[] = parseDownloadingListToString();
		if (null == titles) {
			//没有正在下载的任务，移除通知
			mManager.cancel(TYPE_DOWNLOADING_NOTIFICATION);
			mNotificationMap.remove(TYPE_DOWNLOADING_NOTIFICATION);
			return;
		}
		Notification notification = mNotificationMap.get(TYPE_DOWNLOADING_NOTIFICATION);
		if (null == notification) {
			notification = new Notification();
		}
		notification.icon = R.drawable.notification_download_logo;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(mContext, titles[1], titles[0], setPendingIntentToDownloadManager(TYPE_DOWNLOADING_NOTIFICATION));
		mNotificationMap.put(TYPE_DOWNLOADING_NOTIFICATION, notification);
		mManager.notify(TYPE_DOWNLOADING_NOTIFICATION, mNotificationMap.get(TYPE_DOWNLOADING_NOTIFICATION));
	}

	/*
	 * 添加下载完成通知
	 */
	public void addDownloadedNotification() {
		mDownloadCompleteNum++;
		Notification notification = mNotificationMap.get(TYPE_DOWNLOADED_NOTIFICATION);
		if (null == notification) {
			notification = new Notification();
		}
		notification.icon = R.drawable.notification_download_logo;
		notification.flags = notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(ACTION_CLEAR_DOWNLOAD_COMPLETED_NUM);
		notification.deleteIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
		notification.setLatestEventInfo(mContext, mContext.getString(R.string.notification_downloaded_big_title),
				mDownloadCompleteNum + mContext.getString(R.string.notification_downloaded_little_title),
				setPendingIntentToDownloadManager(TYPE_DOWNLOADED_NOTIFICATION));
		mNotificationMap.put(TYPE_DOWNLOADED_NOTIFICATION, notification);
		mManager.notify(TYPE_DOWNLOADED_NOTIFICATION, mNotificationMap.get(TYPE_DOWNLOADED_NOTIFICATION));
	}

	public void removeAllNotification() {
		for (int id : mNotificationMap.keySet()) {
			mManager.cancel(id);
		}
		mNotificationMap.clear();
	}

	/**
	 * 添加消息通知
	 * @param title
	 * @param content
	 */
	public void addMsgNotification(int id,SnailPushMessage msg) {
		Notification notification = notification = new Notification(R.drawable.push, msg.getTitle(), System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_VIBRATE;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(mContext, msg.getTitle(), msg.getContent(), setPendingIntentMsg(id,msg,TYPE_MSG_NOTIFICATION));
		mNotificationMap.put(TYPE_MSG_NOTIFICATION, notification);
		mManager.notify(TYPE_MSG_NOTIFICATION, notification);
	}

	/*
	 * 添加可升级通知
	 */
	public void addUpgradeNotification(int count) {
		Notification notification = mNotificationMap.get(TYPE_UPGRADE_NOTIFICATION);
		if (null == notification) {
			notification = new Notification(R.drawable.notification_upgrade_logo, mContext.getString(R.string.notification_upgrade_big_title), System.currentTimeMillis());
		}
		notification.defaults = Notification.DEFAULT_VIBRATE;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(mContext, mContext.getString(R.string.notification_upgrade_big_title)
				, count+mContext.getString(R.string.notification_upgrade_little_title), setPendingIntentUpgrade(TYPE_UPGRADE_NOTIFICATION));
		mNotificationMap.put(TYPE_UPGRADE_NOTIFICATION, notification);
		mManager.notify(TYPE_UPGRADE_NOTIFICATION, mNotificationMap.get(TYPE_UPGRADE_NOTIFICATION));
	}

	/*
	 * 添加卡槽即将到期通知
	 */
	public void addSlotExpiredNotification(int days) {
		Notification notification = mNotificationMap.get(TYPE_SLOT_EXPIRED_NOTIFICATION);
		if (null == notification) {
			notification = new Notification(R.drawable.notification_upgrade_logo, mContext.getString(R.string.notification_slot_expired_big_title), System.currentTimeMillis());
		}
		notification.defaults = Notification.DEFAULT_VIBRATE;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		String littleTitle;
		if (days <= 0) {
			littleTitle = mContext.getString(R.string.notification_slot_expired_today_title);
		} else {
			littleTitle = String.format(mContext.getString(R.string.notification_slot_expired_little_title), days);
		}
		notification.setLatestEventInfo(mContext, mContext.getString(R.string.notification_slot_expired_big_title)
				,littleTitle, setPendingIntentToAllApp(TYPE_SLOT_EXPIRED_NOTIFICATION));
		mNotificationMap.put(TYPE_SLOT_EXPIRED_NOTIFICATION, notification);
		mManager.notify(TYPE_SLOT_EXPIRED_NOTIFICATION, mNotificationMap.get(TYPE_SLOT_EXPIRED_NOTIFICATION));
	}
	

	/*
	 * 根据下载列表，生成通知语句
	 */
	private String[] parseDownloadingListToString() {
		String[] result = new String[2];
		HashMap<String, GameState> downloadingList = mGameStateManager.getDownloadingState();
		int downloadingNum = 0;
		int pausedNum = 0;
		int queuingNum = 0;
		int errorNum = 0;
		for (Map.Entry<String,GameState> entry : downloadingList.entrySet()) {
			GameState state = entry.getValue();
			if (GameState.TRANSFERING.equals(state)) {
				downloadingNum++;
			} else if (GameState.PAUSED.equals(state)) {
				pausedNum++;
			} else if (GameState.QUEUING.equals(state)) {
				queuingNum++;
			} else if (GameState.ERROR.equals(state)) {
				errorNum++;
			}
		}
		//构造小标题---x个下载中，x个暂停
		StringBuffer littleTitle = new StringBuffer();
		if (downloadingNum > 0) {
			littleTitle.append(downloadingNum).append(mContext.getString(R.string.notification_downloading_apps));
		} else {
			//没有正在下载的任务，返回空
			return null;
		}
		if (queuingNum > 0) {
			if (littleTitle.length() > 0) {
				littleTitle.append(LITTLE_TITLE_SEPERATOR);
			}
			littleTitle.append(queuingNum).append(mContext.getString(R.string.notification_queue_apps));
		}
		if (pausedNum > 0) {
			if (littleTitle.length() > 0) {
				littleTitle.append(LITTLE_TITLE_SEPERATOR);
			}
			littleTitle.append(pausedNum).append(mContext.getString(R.string.notification_pause_apps));
		}
		if (errorNum > 0) {
			if (littleTitle.length() > 0) {
				littleTitle.append(LITTLE_TITLE_SEPERATOR);
			}
			littleTitle.append(errorNum).append(mContext.getString(R.string.notification_failed_apps));
		}
		result[0] = littleTitle.toString();

		//构造大标题---x个下载任务
		result[1] = new StringBuffer()
		.append(downloadingList.size())
		.append(mContext.getString(R.string.notification_downloading_big_title))
		.toString();

		return result;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_CLEAR_DOWNLOAD_COMPLETED_NUM.equals(action)) {
				mDownloadCompleteNum = 0;
			}
		}
	};

}
