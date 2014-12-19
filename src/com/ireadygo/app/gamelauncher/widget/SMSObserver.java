package com.ireadygo.app.gamelauncher.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.text.TextUtils;

public class SMSObserver extends BroadcastReceiver {
	public static final String ACTION_RECEIVE_SNAIL_SMS = "com.ireadygo.app.action.receive_sms";
	public static final String KEY_SMS_CONTENT = "sms_content";
	private static final String SNAIL_MESSAGE_KEYWORD = "蜗牛公司";
	private static final String SMS_KEYWORD = "验证码";
	public SMSObserver() {

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] pduses = (Object[]) intent.getExtras().get("pdus");
		for (Object pdus : pduses) {
			byte[] pdusmessage = (byte[]) pdus;
			SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);
			String message = sms.getMessageBody();
			if (!TextUtils.isEmpty(message) 
					&& message.contains(SNAIL_MESSAGE_KEYWORD)
					&& message.contains(SMS_KEYWORD)) {
				String validCode = getDigitals(message);
				Intent smsIntent = new Intent(ACTION_RECEIVE_SNAIL_SMS);
				smsIntent.putExtra(KEY_SMS_CONTENT, validCode);
				context.sendBroadcast(smsIntent);
				break;
			}
		}
	}

	public static String getDigitals(String msg) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(msg);
		return m.replaceAll("").trim();
	}

}
