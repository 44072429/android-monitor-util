package android.ys.com.monitor_util;

/**
 * 高性能缓冲单元
 */
public class CachedCell {
	public static final int Cached_Size = 256 * 1024;

	/** 渲染需要的时间毫秒 */
	public long renderTime;

	/** 缓冲区 */
	public byte buffer[] = new byte[Cached_Size];

	/** 数据长度单元 */
	public int dtLength;

	public CachedCell() {

	}

	public void addBuffer(byte data[], int dataLength, long rt) {
		if (dataLength > Cached_Size)
			return;
		for (int i = 0; i < dataLength; i++) {
			buffer[i] = data[i];
		}
		dtLength = dataLength;
		renderTime = rt;
	}

	public void clear() {
		dtLength = 0;
	}
}
