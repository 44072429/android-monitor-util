package android.ys.com.monitor_util;

/**
 * 多媒体服务器<br>
 * 读消息包基类
 */
public abstract class MediaInput {
	/** 消息号 */
	public int msgId = 0;

	/** 实体数据缓冲区 */
	protected byte body[] = null;

	protected int bodyLength = 0;

	/** 实体数据读标记 */
	protected int index = 0;

	/** 处理内容实体数据 */
	public abstract boolean processInput(int msgId, byte body[], int bodyLength);

	/** 消息处理逻辑 */
	public abstract boolean execute();

	/**
	 * 得到消息包大小[20个字节的消息包头,内容数据,4个字节的消息包尾]
	 * 
	 * @return
	 */
	public int getPacketSize() {
		return 20 + bodyLength + 4;
	}

	/**
	 * 读取byte数据
	 * 
	 * @return
	 */
	protected byte readByte() {
		byte v = body[index++];
		return v;
	}

	/**
	 * 读取short数据
	 * 
	 * @return
	 */
	protected short readShort() {
		byte bts[] = new byte[2];
		System.arraycopy(this.body, index, bts, 0, 2);
		index += 2;
		return PacketUtil.bytesToShort(bts);
	}

	/**
	 * 读取char数据
	 * 
	 * @return
	 */
	protected char readChar() {
		byte bts[] = new byte[2];
		System.arraycopy(this.body, index, bts, 0, 2);
		index += 2;
		return PacketUtil.bytesToChar(bts);
	}

	/**
	 * 读取int数据
	 * 
	 * @return
	 */
	protected int readInt() {
		byte bts[] = new byte[4];
		System.arraycopy(this.body, index, bts, 0, 4);
		index += 4;
		return PacketUtil.bytesToInt(bts);
	}

	/**
	 * 读取long数据
	 * 
	 * @return
	 */
	protected long readLong() {
		byte bts[] = new byte[8];
		System.arraycopy(this.body, index, bts, 0, 8);
		index += 8;
		return PacketUtil.bytesToLong(bts);
	}

	/**
	 * 读取float数据
	 * 
	 * @return
	 */
	protected float readFloat() {
		byte bts[] = new byte[4];
		System.arraycopy(this.body, index, bts, 0, 4);
		index += 4;
		return PacketUtil.bytesToFloat(bts);
	}

	/**
	 * 读取double数据
	 * 
	 * @return
	 */
	protected double readDouble() {
		byte bts[] = new byte[8];
		System.arraycopy(this.body, index, bts, 0, 8);
		index += 8;
		return PacketUtil.bytesToDouble(bts);
	}

	/**
	 * 读取byte数组
	 * 
	 * @return
	 */
	protected void readBytes(byte data[], int len) {

		System.arraycopy(this.body, index, data, 0, len);
		index += len;
	}
}
