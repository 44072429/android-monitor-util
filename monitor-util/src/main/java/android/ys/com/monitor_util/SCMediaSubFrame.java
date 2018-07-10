package android.ys.com.monitor_util;

/**
 * 次帧消息包<br>
 * 数据类型 4字节,20002视频，20003音频 <br>
 * 编码类型 4字节数字 pts <br>
 * 8字节数字 dts <br>
 * 8字节数字 保留 4字节 <br>
 * 实际h264帧数据或音频帧数据 长度要根据包头中长度，减掉以上字段长度获得
 */
public class SCMediaSubFrame extends MediaInput {
//	public static final int Frame_Size = 110 * 1024;
//	public static final byte Frame_Data0[] = new byte[Frame_Size];
//	public static final byte Frame_Data1[] = new byte[Frame_Size];
//	public static final byte Frame_Data2[] = new byte[Frame_Size];
//	public static final byte Frame_Data3[] = new byte[Frame_Size];

	// 数据类型 4字节,20002视频，20003音频
	public int dataType;

	// 编码类型 4字节数字
	public int codeType;

	// pts 8字节数字 [录像的时刻 unix时间 毫秒]
	public long pts;

	// dts 8字节数字
	public long dts;

	// 保留 4字节
	public int reserver;

	// throw 4 bits data for windows environment
	public int throwData;

	// 实际h264帧数据或音频帧数据 长度要根据包头中长度，减掉以上字段长度获得
	public int frameLen;
	public byte frameData[];

	private int index_tag;

	public SCMediaSubFrame(int index) {
		this.index_tag = index;
	}

	@Override
	public boolean processInput(int msgId, byte[] body, int bodyLength) {
		this.msgId = msgId;
		this.body = body;
		this.index = 0;
		this.bodyLength = bodyLength;

		frameLen = bodyLength - (4 + 4 + 8 + 8 + 8);
//		frameData = getBuffer();
		frameData = new byte[frameLen];
		// 数据类型
		dataType = readInt();
		// 编码类型
		codeType = readInt();

		pts = readLong();

		dts = readLong();

		reserver = readInt();

		throwData = readInt();

		readBytes(frameData, frameLen);

//		Log.e("Main", "dataType==" + dataType + "  codeType==" + codeType + "  pts==" + pts + "  frameData.length=="
//				+ frameLen);
		
		return false;
	}

	@Override
	public boolean execute() {
		if (dataType < 0) {
			System.out.println("index_tag=" + index_tag + "error:" + dataType);
			return false;
		}

		if (dataType == MediaDefine.Video_Type) { // 视频数据
			/**
			 * 渲染需要考虑的因素有: 1.网络很慢的情况,导致的延时问题 2.解码速度跟不上,导致的延时问题 3.解码过快,导致视频不连续的问题
			 */
			/***
			 * //分析数据类型 byte btsType[] = new byte[4];
			 * System.arraycopy(frameData, 0, btsType, 0, btsType.length); byte
			 * value = frameData[4]; byte f = (byte) (value & 0x80); byte nri =
			 * (byte) (value & 0x60); byte type = (byte) (value & 0x1F);
			 * System.out.println(String.format("f=%d nri=%d type=%d",
			 * f,nri,type));
			 ***/
			// 缓冲机制实现渲染

			CachedPool pool = CachedPoolManager.singleton().getCachedPool(index_tag);
			if (pool != null) {
				pool.addBuffer(frameData, frameLen, pts);
			}
			return true;
		} else if (dataType == MediaDefine.Audio_Type) { // 音频数据
			MediaAudioPlayManager audioManager = MediaAudioPlayManager.singleton();
			// 当视频播放界面只选择了播放当前索引的音频,才加到音频缓冲区中去
			// 音频缓冲池可能被其它线程销毁,因此需要加异常扑获机机制
			if (audioManager.getPlayIndex() == index_tag) {
				try {
					audioManager.addAudioBuffer(frameData, frameLen, pts);
				} catch (Exception e) {

				}
			}
			return true;
		}
		return false;
	}

	// private byte[] getBuffer() {
	// if (index_tag == 0)
	// return Frame_Data0;
	// if (index_tag == 1)
	// return Frame_Data1;
	// if (index_tag == 2)
	// return Frame_Data2;
	// if (index_tag == 3)
	// return Frame_Data3;
	// return null;
	// }
}
