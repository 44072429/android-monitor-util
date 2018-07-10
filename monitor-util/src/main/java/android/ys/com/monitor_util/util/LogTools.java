package android.ys.com.monitor_util.util;

import android.util.Log;

public final class LogTools {

	public static final String LOG_Tag = "BQFrame";

	public static void addLogW(String className, String msg) {
		Log.w(LOG_Tag, className + "?" + msg);
	}

	public static void addLogI(String className, String msg) {
		Log.i(LOG_Tag, className + "?" + msg);
	}

	public static void addLogE(String className, String msg) {
		Log.e(LOG_Tag, className + "?" + msg);
	}
}
