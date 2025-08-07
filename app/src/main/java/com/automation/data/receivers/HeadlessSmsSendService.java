package com.automation.data.receivers;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import timber.log.Timber;

public class HeadlessSmsSendService extends IntentService {
	private static final String CLASS_NAME = HeadlessSmsSendService.class.getName();

	public HeadlessSmsSendService() {
		super(CLASS_NAME);
	}

	@SuppressLint("TimberArgCount")
	protected void onHandleIntent(Intent i) {
		Timber.tag(HeadlessSmsSendService.class.getSimpleName()).d("HeadlessSmsSendService :: received intent.  No action will be taken (none implemented) [Intent: action=%s, data=%s, subject=%s, msg=%s], ${i.getAction()}, ${i.getDataString()}, i.getStringExtra(Intent.EXTRA_SUBJECT), i.getStringExtra(Intent.EXTRA_TEXT))");
	}
}