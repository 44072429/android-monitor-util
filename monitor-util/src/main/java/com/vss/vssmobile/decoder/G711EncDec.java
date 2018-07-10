package com.vss.vssmobile.decoder;

import android.ys.com.monitor_util.util.LogTools;

public class G711EncDec {
	static {
		try {
			System.loadLibrary("g711");
			LogTools.addLogI("G711EncDec", "G711EncDec.loadLibrary?success");
		} catch (Exception e) {
			LogTools.addLogE("G711EncDec", "G711EncDec.loadLibrary?" + e.getMessage());
		}
	}

	public static native int UVoiceEncode(byte speechData[], byte bitStreamData[], int srcLen);

	public static native int UVoiceDecode(byte bitStreamData[], byte speechData[], int srcLen);

	public static native int AVoiceEncode(byte speechData[], byte bitStreamData[], int srcLen);

	public static native int AVoiceDecode(byte bitStreamData[], byte speechData[], int srcLen);
}
