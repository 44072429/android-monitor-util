package android.ys.com.monitor_util;

import android.ys.com.monitor_util.util.LogTools;

import java.io.IOException;
import java.io.InputStream;

/**
 * 多媒体服务器<br>
 * 消息包工厂类
 */
public class TMediaPacketFactory {
	private int index_tag;

	private TMediaPacketFactory(int index) {
		this.index_tag = index;
	}

	private static class TMediaPacketFactoryMaker {
		private static final TMediaPacketFactory instance[] = new TMediaPacketFactory[] { new TMediaPacketFactory(0),
				new TMediaPacketFactory(1), new TMediaPacketFactory(2), new TMediaPacketFactory(3) };
	}

	public static TMediaPacketFactory singleton(int index) {
		if (index >= 0 && index < 4) {
			return TMediaPacketFactoryMaker.instance[index];
		}
		return null;
	}

	private InputStream stream = null;

	private MediaClient client = null;

	public void setInputStream(InputStream stream) {
		this.stream = stream;
	}

	public void setClient(MediaClient client) {
		this.client = client;
	}

	/**
	 * 消息包头部共20个字节<br>
	 * 消息头 4个字节<br>
	 * 消息号 4个字节<br>
	 * 数据内容长度 4个字节<br>
	 * 设备数据 4个字节<br>
	 * 通道数据 4个字节
	 */
	private static final int Head_Length = 20;

	/** 消息头部缓冲区 */
	private final byte Head_Buffer[] = new byte[Head_Length];

	/** 消息头标记缓冲 */
	private final byte Data_Front[] = new byte[4];

	/** 消息ID缓冲 */
	private final byte Msg_Id[] = new byte[4];

	/** 消息长度缓冲 */
	private final byte Data_Len[] = new byte[4];

	/** 内容数据缓冲 */
//	private final byte Data_Body[] = new byte[110 * 1024];
	private byte Data_Body[];

	/** 消息尾标记缓冲 */
	private final byte Data_Rear[] = new byte[4];

	public void creatSCPacket() {
		int nCode = 0;
		try {
			nCode = 1;
			// 读取消息包头部(共20个字节)
			fill(Head_Buffer, 0, Head_Length);

			// 解析头部内容
			System.arraycopy(Head_Buffer, 0, Data_Front, 0, 4);

			// 解析消息包号
			System.arraycopy(Head_Buffer, 4, Msg_Id, 0, 4);
			int msgId = PacketUtil.bytesToInt(Msg_Id);

			// 解析实体内容长度
			System.arraycopy(Head_Buffer, 8, Data_Len, 0, 4);
			int dataLen = PacketUtil.bytesToInt(Data_Len);

			// 解析设备号
			// ...

			// 解析通道号
			// ...

			nCode = 2;
			// 读取实体内容数据
			Data_Body = new byte[dataLen];
			fill(Data_Body, 0, dataLen);

			// 读取消息尾部数据[4个字节]
			fill(Data_Rear, 0, 4);

			nCode = 3;
			// 消息处理
			switch (msgId) {
			case MediaDefine.SC_Main_Frame: {
				nCode = 4;
				MediaInput msg = new SCMediaMainFrame(index_tag);
				nCode = 5;
				msg.processInput(msgId, Data_Body, dataLen);
				nCode = 6;
				msg.execute();
				nCode = 7;
			}
				break;
			case MediaDefine.SC_Sub_Frame: {
				nCode = 10;
				MediaInput msg = new SCMediaSubFrame(index_tag);
				nCode = 11;
				msg.processInput(msgId, Data_Body, dataLen);
				nCode = 12;
				msg.execute();
				nCode = 13;
			}
				break;
			default: {
				nCode = 14;
				System.out.println("TMediaPacketFactory 无法识别的消息号:" + msgId);
			}
			}
		} catch (IOException e1) {
			LogTools.addLogE("TMediaPacketFactory.creatSCPacket", e1.getMessage());
			LinkEventProxyManager.getProxy("video_socket").callBack("" + index_tag, SocketState.Socket_Read_Error);
		} catch (Exception e2) {
			LogTools.addLogE("TMediaPacketFactory.creatSCPacket" + "nCode=" + nCode, e2.getMessage());
			LinkEventProxyManager.getProxy("video_socket").callBack("" + index_tag, SocketState.Packet_Exe_Error);
		}
	}

	/**
	 * 读取数据到Buffer缓冲区中
	 * 
	 * @param buffer
	 * @param offset
	 * @param length
	 * @return
	 * @throws IOException
	 */
	private int fill(byte buffer[], int offset, int length) throws IOException {
		int sum = 0, len;
		while (sum < length) {
			len = stream.read(buffer, offset + sum, length - sum);
			if (len < 0) {
				throw new IOException("end of stream");
			} else
				sum += len;
		}
		return sum;
	}
}
