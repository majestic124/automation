package com.automation.data.receivers;

import static android.provider.Telephony.Sms.Intents.WAP_PUSH_DELIVER_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

public class MmsIntentProcessor extends BroadcastReceiver {
	public void onReceive(Context ctx, Intent intent) {

		try {
			if (WAP_PUSH_DELIVER_ACTION.equals(intent.getAction())) {// We will receive WAP_PUSH_DELIVER_ACTION on Android 4.4+ if set as the
				// default SMS application.
				// TODO store MMS/WAP Push to the normal inbox
			} else {
				throw new IllegalStateException("Unexpected intent: " + intent);
			}
		} catch(Exception ex) {
			Timber.tag(MmsIntentProcessor.class.getSimpleName()).e(ex);
		}
	}
}