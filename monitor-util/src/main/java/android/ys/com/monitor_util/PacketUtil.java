package android.ys.com.monitor_util;

import java.nio.charset.Charset;

/**
 * 消息包工具类
 */
public class PacketUtil {
	/**
	 * 返回byte所点用的字节大小
	 * 
	 * @param value
	 * @return
	 */
	public static int sizeOfByte() {
		return 1;
	}

	/**
	 * 返回short所点用的字节大小
	 * 
	 * @param value
	 * @return
	 */
	public static int sizeOfShort() {
		return 2;
	}

	/**
	 * 返回char所点用的字节大小
	 * 
	 * @param value
	 * @return
	 */
	public static int sizeOfChar() {
		return 2;
	}

	/**
	 * 返回int所点用的字节大小
	 * 
	 * @param value
	 * @return
	 */
	public static int sizeOfInt() {
		return 4;
	}

	/**
	 * 返回long所点用的字节大小
	 * 
	 * @param value
	 * @return
	 */
	public static int sizeOfLong() {
		return 8;
	}

	/**
	 * 返回float所点用的字节大小
	 * 
	 * @param value
	 * @return
	 */
	public static int sizeOfFloat() {
		return 4;
	}

	/**
	 * 返回double所点用的字节大小
	 * 
	 * @param value
	 * @return
	 */
	public static int sizeOfDouble() {
		return 8;
	}

	/**
	 * byte数组转short数据
	 * 
	 * @param bts
	 * @return
	 */
	public static short bytesToShort(byte bts[]) {
		return (short) (((bts[1] & 0xff) << 8) | (bts[0] & 0xff));
	}

	/**
	 * byte数组转char数据
	 * 
	 * @param bts
	 * @return
	 */
	public static char bytesToChar(byte bts[]) {
		return (char) (((bts[1] & 0xff) << 8) | (bts[0] & 0xff));
	}

	/**
	 * byte数组转int数据
	 * 
	 * @param bts
	 * @return
	 */
	public static int bytesToInt(byte bts[]) {
		return (int) (((bts[3] & 0xff) << 24) | ((bts[2] & 0xff) << 16)
				| ((bts[1] & 0xff) << 8) | ((bts[0] & 0xff)));
	}

	/**
	 * byte数组转long数据
	 * 
	 * @param bts
	 * @return
	 */
	public static long bytesToLong(byte bts[]) {
		return (0xffL & (long) bts[0]) | (0xff00L & ((long) bts[1] << 8))
				| (0xff0000L & ((long) bts[2] << 16))
				| (0xff000000L & ((long) bts[3] << 24))
				| (0xff00000000L & ((long) bts[4] << 32))
				| (0xff0000000000L & ((long) bts[5] << 40))
				| (0xff000000000000L & ((long) bts[6] << 48))
				| (0xff00000000000000L & ((long) bts[7] << 56));
	}

	/**
	 * byte数组转float数据
	 * 
	 * @param bts
	 * @return
	 */
	public static float bytesToFloat(byte bts[]) {
		return Float.intBitsToFloat(bytesToInt(bts));
	}

	/**
	 * byte数组转double数据
	 * 
	 * @param bts
	 * @return
	 */
	public static double bytesToDouble(byte bts[]) {
		return Double.longBitsToDouble(bytesToLong(bts));
	}

	/**
	 * byte数组转String数据
	 * 
	 * @param bts
	 * @param charsetName
	 * @return
	 */
	public static String bytesToString(byte bts[], String charsetName) {
		return new String(bts, Charset.forName(charsetName));
	}

	/**
	 * char数据转byte数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] charToBytes(char value) {
		byte[] bts = new byte[2];
		bts[0] = (byte) (value);
		bts[1] = (byte) (value >> 8);
		return bts;
	}

	/**
	 * short数据转byte数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] shortToBytes(short value) {
		byte[] bts = new byte[2];
		bts[0] = (byte) (value & 0xff);
		bts[1] = (byte) ((value & 0xff00) >> 8);
		return bts;
	}

	/**
	 * int数据转byte数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] intToBytes(int value) {
		byte[] bts = new byte[4];
		bts[0] = (byte) (value & 0xff);
		bts[1] = (byte) ((value & 0xff00) >> 8);
		bts[2] = (byte) ((value & 0xff0000) >> 16);
		bts[3] = (byte) ((value & 0xff000000) >> 24);
		return bts;
	}

	/**
	 * long数据转byte数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] longToBytes(long value) {
		byte[] bts = new byte[8];
		bts[0] = (byte) (value & 0xff);
		bts[1] = (byte) ((value >> 8) & 0xff);
		bts[2] = (byte) ((value >> 16) & 0xff);
		bts[3] = (byte) ((value >> 24) & 0xff);
		bts[4] = (byte) ((value >> 32) & 0xff);
		bts[5] = (byte) ((value >> 40) & 0xff);
		bts[6] = (byte) ((value >> 48) & 0xff);
		bts[7] = (byte) ((value >> 56) & 0xff);
		return bts;
	}

	/**
	 * float数据转byte数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] floatToBytes(float value) {
		int intBits = Float.floatToIntBits(value);
		return intToBytes(intBits);
	}

	/**
	 * float数据转byte数组
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] doubleToBytes(double value) {
		long lngBits = Double.doubleToLongBits(value);
		return longToBytes(lngBits);
	}

	/**
	 * String数据转byte数组
	 * 
	 * @param data
	 * @param charsetName
	 * @return
	 */
	public static byte[] stringToBytes(String data, String charsetName) {
		Charset charset = Charset.forName(charsetName);
		return data.getBytes(charset);
	}
}
