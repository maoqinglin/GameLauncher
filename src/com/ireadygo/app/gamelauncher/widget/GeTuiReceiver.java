package com.ireadygo.app.gamelauncher.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.account.PushMsgProcessor;
import com.ireadygo.app.gamelauncher.account.pushmsg.SnailPushMessage;
import com.ireadygo.app.gamelauncher.account.pushmsg.SnailPushMessage.Type;

public class GeTuiReceiver extends BroadcastReceiver {

	private final AccountManager mAccountManager = AccountManager.getInstance();
	private static final String TAG = "GeTuiReceiver";
	private static final String ACTION_MSG_UPDATE = "com.ireadygo.app.gamelauncher.msg.update";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			switch (bundle.getInt(PushConsts.CMD_ACTION)) {
			case PushConsts.GET_MSG_DATA:
			// 获取透传（payload）数据
			byte[] payload = bundle.getByteArray("payload");
			if (payload != null) {
				//接收处理透传（payload）数据
				String data = new String(payload);
				Log.d("lmq", "data = "+data);
				SnailPushMessage spMsg = mAccountManager.stringToSnailPushMessage(data);
				if(spMsg != null) {
					if(spMsg.getType() == Type.APP_DOWNLOAD) {
						if(!GameLauncherConfig.ONLINE_DOWNLOAD_OPEN) {
							return;
						}
					}
					//后台处理推送消息,屏蔽消息，由消息盒子处理  modify by linmaoqing 2015-7-16 
//					PushMsgProcessor.getInstance().handlePushMsg(spMsg);
					
					//发送boxmessage
					PushMsgProcessor.getInstance().sendBoxMessageBroadcast(spMsg, data);
					//添加本地消息通知
					int id = mAccountManager.addSnailPushMessage(context, spMsg);
					if(id > 0 && GameLauncher.hasInit()) {
//							GameLauncher.instance().getGameManager().getGameLauncherNotification()
//									.addMsgNotification(id, spMsg); //屏蔽通知  modify by linmaoqing 2015-7-13 
						Intent send = new Intent(ACTION_MSG_UPDATE);
						send.putExtra("NOTIFICATION_ID", id);
						LocalBroadcastManager.getInstance(context).sendBroadcast(send);
					}
				} else {
					Log.e(TAG, "The JSONString Format is Wrong : " + data);
				}
			}
			break;
			default:
				break;
			}
		}
	}
}
