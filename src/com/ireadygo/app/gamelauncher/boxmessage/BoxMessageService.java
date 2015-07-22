package com.ireadygo.app.gamelauncher.boxmessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.boxmessage.data.BoxMessage;
import com.ireadygo.app.gamelauncher.boxmessage.data.BroadcastMsg;
import com.ireadygo.app.gamelauncher.boxmessage.data.DBManager;
import com.ireadygo.app.gamelauncher.boxmessage.data.DBManager.MessageColumn;
import com.ireadygo.app.gamelauncher.boxmessage.data.NotificationMsg;
import com.ireadygo.app.gamelauncher.boxmessage.ui.GlobalMessageView;
import com.ireadygo.app.gamelauncher.utils.PictureUtil;

public class BoxMessageService extends NotificationListenerService {

	public static final int TYPE_CHANGE_ADD = 0;
	public static final int TYPE_CHANGE_DEL = 1;
	public static final int TYPE_CHANGE_UPDATE = 2;
	private static final int MSG_GLOBAL_MESSAGE_SHOW = 3;
	private static final int MSG_GLOBAL_MESSAGE_HIDE = 4;
	private static final int DELAY_TIME = 5000;
	private static final String CHANGE_ACTION_BOXMESSAGE = "com.ireadygo.app.boxmessage.change";
	private static final String BIND_ACTION_BOXMESSAGE = "com.ireadygo.app.gamelauncher.boxmessage.BoxMessageService";
	private static final String ACTION_BOX_MESSAGE = "com.ireadygo.app.boxmessage";
	
	private static final List<String> sPkgList = new ArrayList<String>();

	private BoxMessageLocalBinder mBoxMessageLocalBinder = new BoxMessageLocalBinder();
	private List<BoxMessage> mBoxMessageList = new ArrayList<BoxMessage>();
	private DBManager mDBManager;
	private LocalBroadcastManager mBroadcastManager;
	private GlobalMessageView mGlobalMessageView;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GLOBAL_MESSAGE_HIDE:
				if(mGlobalMessageView.isActive() || !mGlobalMessageView.isShow()) {
					return;
				}
				mGlobalMessageView.hide();
				break;

			case MSG_GLOBAL_MESSAGE_SHOW:
				if(mGlobalMessageView.isActive() || mGlobalMessageView.isShow()) {
					mHandler.sendEmptyMessage(MSG_GLOBAL_MESSAGE_SHOW);
					return;
				}
				BoxMessage boxMsg = mBoxMessageList.get(0);
				mGlobalMessageView.show(boxMsg.icon, boxMsg.title);
				mHandler.sendEmptyMessageDelayed(MSG_GLOBAL_MESSAGE_HIDE, DELAY_TIME);
				break;
			default:
				break;
			}
			
		};
	};

	Comparator<BoxMessage> comp = new Comparator<BoxMessage>() {
		
		@Override
		public int compare(BoxMessage lhs, BoxMessage rhs) {
			if(lhs.time < rhs.time) {
				return -1;
			} else if(lhs.time > rhs.time) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	static {
		String[] pkgNames = GameLauncherApplication.getApplication()
				.getResources().getStringArray(R.array.boxmessage_white_list);
		for (String name : pkgNames) {
			sPkgList.add(name);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("chenrui", "BoxMessageService ~~~ onCreate~~~");
		mGlobalMessageView = GlobalMessageView.getInstance(this);
		init();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i("chenrui", "Action ~~~ " + intent.getAction());
		if(BIND_ACTION_BOXMESSAGE.equals(intent.getAction())) {
			Log.i("chenrui", "BindSuccess!!!!!!!!");
			return mBoxMessageLocalBinder;
		}
		return super.onBind(intent);
	}

	private void init() {
		Log.i("chenrui", "BoxMessageService ~~~ init~~~~");
		mDBManager = DBManager.getInstance(this);
		mBroadcastManager = LocalBroadcastManager.getInstance(this);

		mBoxMessageList.clear();
		mBoxMessageList.addAll(mDBManager.getAllBroadcastMsg());

		Collections.sort(mBoxMessageList, comp);
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		if(sbn != null) {
			if(isWhiteList(sbn.getPackageName())) {
				NotificationMsg notificationMsg = toNotificationMsg(sbn);
				addNotificationMsgs(notificationMsg);
				updateBoxMessage();
				sendBoxMessageChangeBroadcast(TYPE_CHANGE_ADD);
				sendHandlerAddMsg();
			}
		}
	}

	private void sendHandlerAddMsg() {
		if(mHandler.hasMessages(MSG_GLOBAL_MESSAGE_SHOW)) {
			mHandler.removeMessages(MSG_GLOBAL_MESSAGE_SHOW);
		}
		mHandler.sendEmptyMessage(MSG_GLOBAL_MESSAGE_SHOW);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		//TODO
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null) {
			Log.i("chenrui", "~~~~~~onStartCommand~~~ Action : " + intent.getAction());
			processIntent(intent);
		}
		return START_STICKY;
	}

	private void processIntent(Intent intent) {
		if(ACTION_BOX_MESSAGE.equals(intent.getAction())) {
			BroadcastMsg broadcastMsg = toBroadcastMsg(intent);
			mBoxMessageList.add(0, broadcastMsg);
			updateBoxMessage();
			sendBoxMessageChangeBroadcast(TYPE_CHANGE_ADD);
			sendHandlerAddMsg();
		}
	}

	private void updateBoxMessage() {
		if(mBoxMessageList.size() > 30) {
			BoxMessage msg = mBoxMessageList.get(mBoxMessageList.size() - 1);
			if(msg instanceof BroadcastMsg) {
				mDBManager.removeBroadcastMsg(msg.id);
			}

			if(msg instanceof NotificationMsg){
				cancelNotification(msg.pkgName, null, msg.id);
			}

			mBoxMessageList.remove(msg);
		}
	}

	private void addNotificationMsgs(NotificationMsg notificationMsg) {
		for (BoxMessage msg : mBoxMessageList) {
			if(msg.id == notificationMsg.id && msg.pkgName.equals(notificationMsg.pkgName)) {
				mBoxMessageList.remove(msg);
				break;
			}
		}

		mBoxMessageList.add(0, notificationMsg);
	}

	private NotificationMsg toNotificationMsg(StatusBarNotification sbn) {
		NotificationMsg notificationMsg = new NotificationMsg();
		notificationMsg.id = sbn.getId();
		notificationMsg.pkgName = sbn.getPackageName();
		notificationMsg.title = sbn.getNotification().tickerText.toString();
		notificationMsg.contentIntent = sbn.getNotification().contentIntent;
		notificationMsg.time = sbn.getNotification().when;
		if(notificationMsg.contentIntent == null) {
			notificationMsg.isRead = BoxMessage.HAS_READ;
		} else {
			notificationMsg.isRead = BoxMessage.HAS_NOT_READ;
		}
		if(sbn.getNotification().largeIcon != null) {
			notificationMsg.icon = sbn.getNotification().largeIcon;
		} else {
			try {
				Drawable drawable = getPackageManager().getApplicationIcon(sbn.getPackageName());
				notificationMsg.icon = PictureUtil.drawableToBitmap(drawable);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				notificationMsg.icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			}
		}
		return notificationMsg;
	}

	private BroadcastMsg toBroadcastMsg(Intent intent) {
		BroadcastMsg broadcastMsg = new BroadcastMsg();
		broadcastMsg.title = intent.getStringExtra(MessageColumn.TITLE);
		broadcastMsg.pkgName = intent.getStringExtra(MessageColumn.PKG_NAME);
		broadcastMsg.skipFlag = intent.getStringExtra(MessageColumn.SKIP_FLAG);
		broadcastMsg.skipType = intent.getIntExtra(MessageColumn.SKIP_TYPE, -1);
		broadcastMsg.time = System.currentTimeMillis();
		broadcastMsg.isRead = BoxMessage.HAS_NOT_READ;
		broadcastMsg.icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		broadcastMsg.id = mDBManager.insertBroadcastMsg(broadcastMsg);
		return broadcastMsg;
	}

	private boolean isWhiteList(String pkgName) {
		return sPkgList.contains(pkgName);
	}

	private void sendBoxMessageChangeBroadcast(int type) {
		Intent intent = new Intent(CHANGE_ACTION_BOXMESSAGE);
		intent.putExtra("type", type);
		mBroadcastManager.sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		Log.i("chenrui", "BoxMessageService ~~~ onDestroy~~~~");
		mBoxMessageList.clear();
//		cancelAllNotifications();
		super.onDestroy();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("chenrui", "BoxMessageService ~~~ onUnbind~~~~");
		return super.onUnbind(intent);
	}
	
	public final class BoxMessageLocalBinder extends Binder {

		List<BoxMessage> getAllBoxMessage() {
			return mBoxMessageList;
		}

		int getUnReadMsgCount() {
			int count = 0;
			for (BoxMessage msg : mBoxMessageList) {
				if(msg.isRead == BoxMessage.HAS_NOT_READ) {
					count++;
				}
			}
			return count;
		}

		void removeBoxMessage(String pkgName, int id) {
			for (BoxMessage msg : mBoxMessageList) {
				if(msg.id == id && msg.pkgName.equals(pkgName)) {
					
					if(msg instanceof BroadcastMsg) {
						mDBManager.removeBroadcastMsg(id);
					}
					
					if(msg instanceof NotificationMsg) {
						cancelNotification(pkgName, null, id);
					}
					
					mBoxMessageList.remove(msg);
					sendBoxMessageChangeBroadcast(TYPE_CHANGE_DEL);
					return;
				}
			}
		}

		void setMsgReadStatus(String pkgName, int id, boolean isRead) {
			for (BoxMessage msg : mBoxMessageList) {
				if(msg.id == id && msg.pkgName.equals(pkgName)) {
					if(isRead) {
						msg.isRead = BoxMessage.HAS_READ;
					} else {
						msg.isRead = BoxMessage.HAS_NOT_READ;
					}

					if(msg instanceof BroadcastMsg) {
						mDBManager.updateBroadcastMsg((BroadcastMsg)msg);
					}
					sendBoxMessageChangeBroadcast(TYPE_CHANGE_UPDATE);
					return;
				}
			}
		}
	}
}
