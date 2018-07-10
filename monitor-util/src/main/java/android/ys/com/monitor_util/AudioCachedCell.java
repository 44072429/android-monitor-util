package android.ys.com.monitor_util;

public class AudioCachedCell {
	public int cachedSize;

	/** 渲染需要的时间毫秒 */
	public long renderTime;

	/** 缓冲区 */
	public byte buffer[];

	/** 数据长度单元 */
	public int dtLength;

	public AudioCachedCell(int cachedSize) {
		this.cachedSize = cachedSize;
		this.buffer = new byte[cachedSize];
	}

	public void addBuffer(byte data[], int dataLength, long rt) {
		// 如果缓冲区大小不够,扩充N倍
		if (dataLength > cachedSize) {
			int times = 0;
			for (int i = 2; i < 10; i++) {
				if (i * cachedSize > dataLength) {
					times = i;
					break;
				}
			}
			if (times == 0)
				return;
			// 缓冲大小扩充
			cachedSize = times * cachedSize;
			buffer = new byte[cachedSize];
		}

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
