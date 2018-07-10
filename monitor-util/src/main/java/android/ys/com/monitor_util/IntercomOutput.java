package android.ys.com.monitor_util;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * 多媒体服务器,实时对讲消息<br>
 * 写消息包基类
 */
@SuppressWarnings("unused")
public abstract class IntercomOutput {
	/** 最大缓冲区大小 */
	private static final int Max_Buffer_Size = 10 * 1024;

	/** 缓冲数据 */
	private final byte buffer[] = new byte[Max_Buffer_Size];

	/** 缓冲数据标记 */
	private int index = 0;

	/** 实体数据 json数据 utf-8格式 */
	protected byte body[] = null;

	/** 实体数据长度 */
	protected int length = 0;

	/**
	 * 得到消息包编号
	 * 
	 * @return
	 */
	public abstract int getPacketId();

	/**
	 * 得到实体内容大小
	 */
	public abstract int getBodySize();

	/**
	 * 处理写数据
	 * 
	 * @param os
	 */
	public abstract void processOutput();

	/**
	 * 向服务端发送消息
	 * 
	 * @param os
	 * @return
	 */
	public boolean sendPacket(OutputStream os) {
		try {
			// 初始化实体内容缓冲区
			if (body == null) {
				int size = getBodySize();
				body = new byte[size];
			}

			// 清除数据
			clear();

			// 将消息数据写入到实体缓冲区
			processOutput();

			// 写消息头
			writeBytes(PacketHeader.header, 4);

			// 写消息号
			writeInt(getPacketId());

			// 写数据内容长度大小
			writeInt(length);

			// 写设备数据默认为0
			writeInt(0);

			// 写通道数据默认为0
			writeInt(0);

			// 写数据内容
			writeBytes(body, length);

			// 写消息尾
			writeBytes(PacketHeader.rear, 4);

			// 将缓冲数据写入到流通道上
			Log.e("Main", "buffer==" + Arrays.toString(buffer));
			os.write(buffer, 0, index);

			// 向服务端推送数据
			os.flush();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 写byte数据
	 * 
	 * @param value
	 */
	private void writeByte(byte value) {
		buffer[index++] = value;
	}

	/**
	 * 写char数据
	 * 
	 * @param value
	 */
	private void writeChar(char value) {
		byte bts[] = PacketUtil.charToBytes(value);
		buffer[index++] = bts[0];
		buffer[index++] = bts[1];
	}

	/**
	 * 写short数据
	 * 
	 * @param value
	 */
	private void writeShort(short value) {
		byte bts[] = PacketUtil.shortToBytes(value);
		buffer[index++] = bts[0];
		buffer[index++] = bts[1];
	}

	/**
	 * 写int数据
	 * 
	 * @param value
	 */
	private void writeInt(int value) {
		byte bts[] = PacketUtil.intToBytes(value);
		buffer[index++] = bts[0];
		buffer[index++] = bts[1];
		buffer[index++] = bts[2];
		buffer[index++] = bts[3];
	}

	/**
	 * 写long数据
	 * 
	 * @param value
	 */
	private void writeLong(long value) {
		byte bts[] = PacketUtil.longToBytes(value);
		for (int i = 0; i < 8; i++) {
			buffer[index++] = bts[i];
		}
	}

	/**
	 * 写float数据
	 * 
	 * @param value
	 */
	private void writeFloat(float value) {
		byte bts[] = PacketUtil.floatToBytes(value);
		buffer[index++] = bts[0];
		buffer[index++] = bts[1];
		buffer[index++] = bts[2];
		buffer[index++] = bts[3];
	}

	/**
	 * 写double数据
	 * 
	 * @param value
	 */
	private void writeDouble(double value) {
		byte bts[] = PacketUtil.doubleToBytes(value);
		for (int i = 0; i < 8; i++) {
			buffer[index++] = bts[i];
		}
	}

	/**
	 * 写byte数组
	 * 
	 * @param data
	 * @param len
	 */
	private void writeBytes(byte data[], int len) {
		for (int i = 0; i < len; i++) {
			buffer[index++] = data[i];
		}
	}

	/**
	 * 写byte数据
	 * 
	 * @param value
	 */
	protected void writeBodyByte(byte value) {
		body[length++] = value;
	}

	/**
	 * 写char数据
	 * 
	 * @param value
	 */
	protected void writeBodyChar(char value) {
		byte bts[] = PacketUtil.charToBytes(value);
		body[length++] = bts[0];
		body[length++] = bts[1];
	}

	/**
	 * 写short数据
	 * 
	 * @param value
	 */
	protected void writeBodyShort(short value) {
		byte bts[] = PacketUtil.shortToBytes(value);
		body[length++] = bts[0];
		body[length++] = bts[1];
	}

	/**
	 * 写int数据
	 * 
	 * @param value
	 */
	protected void writeBodyInt(int value) {
		byte bts[] = PacketUtil.intToBytes(value);
		body[length++] = bts[0];
		body[length++] = bts[1];
		body[length++] = bts[2];
		body[length++] = bts[3];
	}

	/**
	 * 写long数据
	 * 
	 * @param value
	 */
	protected void writeBodyLong(long value) {
		byte bts[] = PacketUtil.longToBytes(value);
		for (int i = 0; i < 8; i++) {
			body[length++] = bts[i];
		}
	}

	/**
	 * 写float数据
	 * 
	 * @param value
	 */
	protected void writeBodyFloat(float value) {
		byte bts[] = PacketUtil.floatToBytes(value);
		body[length++] = bts[0];
		body[length++] = bts[1];
		body[length++] = bts[2];
		body[length++] = bts[3];
	}

	/**
	 * 写double数据
	 * 
	 * @param value
	 */
	protected void writeBodyDouble(double value) {
		byte bts[] = PacketUtil.doubleToBytes(value);
		for (int i = 0; i < 8; i++) {
			body[length++] = bts[i];
		}
	}

	/**
	 * 写byte数组
	 * 
	 * @param data
	 * @param len
	 */
	protected void writeBodyBytes(byte data[], int len) {
		for (int i = 0; i < len; i++) {
			body[length++] = data[i];
		}
	}

	/**
	 * 消息包数据清除
	 */
	private void clear() {
		index = 0;
		length = 0;
	}
}
