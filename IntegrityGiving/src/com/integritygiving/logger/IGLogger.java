package com.integritygiving.logger;

import com.integritygiving.configuration.Configuration;

import android.util.Log;

public class IGLogger {

	private static final String TAG = "IG";

	public static void d(Object obj, String msg) {
		if (Configuration.DEV_MODE) {
			Log.d(TAG, obj.getClass() + " : " + msg);
		}
	}

	public static void e(Object obj, String msg) {
		if (Configuration.DEV_MODE) {
			Log.e(TAG, obj.getClass() + " : " + msg);
		}
	}
}
