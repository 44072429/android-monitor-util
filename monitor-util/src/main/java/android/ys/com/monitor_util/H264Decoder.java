package android.ys.com.monitor_util;

import java.nio.ByteBuffer;

import com.vss.vssmobile.decoder.H264Dec;

import android.graphics.Bitmap;

/**
 * H264解析类
 */
public class H264Decoder {
	/** 解析帧宽度 */
	private int width = 0;

	/** 解析帧高度 */
	private int height = 0;

	/** 解析器初始化返回标记 */
	private long handle = 0;

	/** 解析器返回帧宽高度数据 */
	private int btsWH[] = new int[4];

	/** 帧缓冲数据 */
	private byte frame_buffer[] = null;

	/** 帧缓冲数据nio对象 */
	private ByteBuffer buffer = null;

	/** 是否初始化 */
	private boolean initialized = false;

	private int index;

	public H264Decoder(int width, int height, int index) {
		this.width = width;
		this.height = height;
		this.index = index;

		initialize();
	}

	private void initialize() {
		try {
			initH264Decoder();

			frame_buffer = new byte[width * height * 4];
			buffer = ByteBuffer.wrap(frame_buffer);

			initialized = true;
		} catch (Exception e) {
			initialized = false;
			e.printStackTrace();
		}

	}

	/**
	 * 解析数据到Bitmap
	 * 
	 * @param data
	 * @return
	 */
	public Bitmap decodeFrame(byte data[]) {
		if (!initialized || data == null)
			return null;

		// 调用jni组件解码帧数据
		int i = H264Dec.DecoderNal(handle, data, data.length, btsWH, frame_buffer);
		if (i <= 0)
			return null;
		int j = btsWH[2];
		int k = btsWH[3];
		if (j > 0 && k > 0) {
			// byte btsData[] = new byte[2 * (j * k)];
			Bitmap bmp = Bitmap.createBitmap(j, k, Bitmap.Config.RGB_565);
			// System.arraycopy(m_imageData, 0, btsData, 0, 2 * (j * k));
			bmp.copyPixelsFromBuffer(buffer);
			buffer.clear();
			return bmp;
		}
		return null;
	}

	/**
	 * 解析数据到Bitmap
	 * 
	 * @param data
	 * @return
	 */
	public Bitmap decodeFrameEx(byte data[], int dataLen) {
		if (!initialized || data == null)
			return null;

		// 调用jni组件解码帧数据
		int i = H264Dec.DecoderNal(handle, data, dataLen, btsWH, frame_buffer);
		if (i <= 0)
			return null;
		int j = btsWH[2];
		int k = btsWH[3];
		if (j > 0 && k > 0) {
			// byte btsData[] = new byte[2 * (j * k)];
			Bitmap bmp = Bitmap.createBitmap(j, k, Bitmap.Config.RGB_565);
			// System.arraycopy(m_imageData, 0, btsData, 0, 2 * (j * k));
			bmp.copyPixelsFromBuffer(buffer);
			buffer.clear();
			return bmp;
		}
		return null;
	}

	public int getIndex() {
		return index;
	}

	private void initH264Decoder() {
		handle = H264Dec.InitDecoder();
		// LogTools.addLogI("H264Decoder.initH264Decoder",
		// String.format("index=%d handle=%d", index,handle));
	}

	public void finalizeDecoder() {
		H264Dec.UninitDecoder(handle);
		// LogTools.addLogI("H264Decoder.finalizeH264Decoder",
		// String.format("index=%d handle=%d", index,handle));
	}
}
