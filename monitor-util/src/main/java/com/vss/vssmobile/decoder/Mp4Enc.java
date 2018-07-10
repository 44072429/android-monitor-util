package com.vss.vssmobile.decoder;

/**
 * 参考1： http://blog.csdn.net/guojin08/article/details/27555473 <br>
 * 参考2: http://blog.csdn.net/xipiaoyouzi/article/details/37599759 <br>
 * 参考3: http://blog.csdn.net/sdvch/article/details/38348673 <br>
 * android支持的媒体格式:<br>
 * 参考: http://blog.csdn.net/ddna/article/details/5173481 <br>
 */
public class Mp4Enc {
	public static long handle;

	static {
		try {
			System.loadLibrary("Mp4Enc");
			handle = getInstance();
		} catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
			System.out.println("loadLibrary(Mp4Enc)," + localUnsatisfiedLinkError.getMessage());
		}
	}

	public static native long InsertAudioBuffer(long instance, byte audioBuffer[], long audioLength);

	public static native long InsertVideoBuffer(long instance, byte videoBuffer[], long videoLength);

	public static native long ReleaseInstance(long instance);

	public static native long SetVideoFrameRate(long instance, long frameRate);

	public static native long SetVideoSize(long instance, int width, int height);

	public static native long StartRead(long instance, String fileName);

	public static native long getInstance();

	public static native long startwrite(long instance, String fileName);

	public static native long stop(long instance);
}
