package com.vss.vssmobile.decoder;

public class H264Dec {
	static {
		try {
			System.loadLibrary("H264Android");
		} catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
			System.out.println("loadLibrary(H264Android)," + localUnsatisfiedLinkError.getMessage());
		}
	}

	public static native int DecoderNal(long handle, byte h264Frame[], int h264Len, int outWH[], byte outFrame[]);

	public static synchronized native long InitDecoder();

	public static synchronized native long UninitDecoder(long handle);
}
