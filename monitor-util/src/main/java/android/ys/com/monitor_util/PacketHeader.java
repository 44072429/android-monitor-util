package android.ys.com.monitor_util;

/**
 * 消息头尾标记类
 */
public final class PacketHeader {
	public static final byte header[] = {'I','W','H','D'};
	
	public static final byte rear[] = {'I','W','E','D'};
	
	/**
	 * 得到消息头数据长度
	 * @return
	 */
	public static int getHeaderSize() {
		return 4;
	}
	
	/**
	 * 得到消息尾数据长度
	 * @return
	 */
	public static int getRearSize() {
		return 4;
	}
}
