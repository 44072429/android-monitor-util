package android.ys.com.monitor_util;

/**
 * 主帧消息包<br>
 * 含义 大小（字节） IWAVFormatInfo 大小见下面描述 <br>
 * 设备号 4字节数字 <br>
 * 通道号 4字节数字 <br>
 * 
 * IWAVFormatInfo <br>
 * 含义 大小（字节） <br>
 * 流个数（通常是2，表示有一个音频一个视频） 4字节数字 <br>
 * 保留 4字节 <br>
 * IWAVStreamInfo 6个IWAVStreamInfo[具体内容见下面描述] <br>
 * 视频在IWAVStreamInfo数组中的索引 4字节数字 <br>
 * 音频在IWAVStreamInfo数组中的索引 4字节数字 <br>
 * 录像文件时长单位毫秒 8字节数字，手机没用 <br>
 * 开始时间 8字节数字，手机没用 <br>
 * 开始日期 8字节数字，手机没用 <br>
 * 解码器名 256字节，手机没用 <br>
 * 
 * IWAVStreamInfo <br>
 * 含义 大小（字节） <br>
 * 流类型 4字节数字，1音频，2视频 <br>
 * 编码类型 4字节数字，28表示h264,65542表示g711u,65543表示g711a <br>
 * 流类标记 4字节数字，没用到 <br>
 * 视频宽 4字节数字 <br>
 * 视频高 4字节数字 <br>
 * profile 4字节数字 <br>
 * level 4字节数字 <br>
 * 宽纵比宽 4字节数字 <br>
 * 宽纵比高 4字节数字 <br>
 * 保留 4字节 <br>
 * 保留 32字节 <br>
 * 码率 4字节数字 <br>
 * 附加数据大小 4字节数字 <br>
 * 附加数据 4096字节 <br>
 * 保留 16字节 <br>
 * 保留数据 40字节 <br>
 **/
public class SCMediaMainFrame extends MediaInput {
	public IWAVFormatInfo wavFmt;

	// 设备号 4字节数字
	public int deviceId;

	// 通道号 4字节数字
	public int channelId;

	private int index_tag;

	public SCMediaMainFrame(int index_tag) {
		this.index_tag = index_tag;
	}

	@Override
	public boolean processInput(int msgId, byte[] body, int bodyLength) {
		this.msgId = msgId;
		this.body = body;
		this.index = 0;
		this.bodyLength = bodyLength;

		wavFmt = new IWAVFormatInfo();
		wavFmt.read();

		deviceId = readInt();

		channelId = readInt();

		// System.out.println("index=" + index);

		return false;
	}

	@Override
	public boolean execute() {
		System.out.println("SCMediaMainFrame.execute" + "index_tag=" + index_tag);

		// 分析流类型,如果有音频流则初始化音频参数数据
		try {
			MediaAudioParams audioParams = MediaAudioParamsManager.singleton().getAudioParams(index_tag);
			if (wavFmt.audioIndex >= 0 && wavFmt.audioIndex < 6) {
				audioParams.setAudioIndex(wavFmt.audioIndex);
				audioParams.decodeFromBuffer(wavFmt.wavInfo[wavFmt.audioIndex].rsbytes_2);
			} else {
				audioParams.setAudioIndex(wavFmt.audioIndex);
			}
		} catch (Exception e) {
			System.out.println("SCMediaMainFrame.execute error?" + e.getMessage());
		}

		// 调用回调事件
		LinkEventProxyManager.getProxy("video_socket").callBack("" + index_tag, SocketState.Socket_LoginSuccess);

		return false;
	}

	public class IWAVFormatInfo {
		/** 流个数（通常是2，表示有一个音频一个视频） 4字节数字 */
		int streamCnt;

		/** 保留 4字节 */
		int reserver;

		/** 6个IWAVStreamInfo 信息描述 */
		IWAVStreamInfo wavInfo[] = new IWAVStreamInfo[6];

		// 视频在IWAVStreamInfo数组中的索引 4字节数字
		int videoIndex;

		// 音频在IWAVStreamInfo数组中的索引 4字节数字
		int audioIndex;

		// 录像文件时长单位毫秒 8字节数字，手机没用
		int recoderTime;

		// 开始时间 8字节数字，手机没用
		int startTime[] = new int[2];

		// 开始日期 8字节数字，手机没用
		int endTime[] = new int[2];

		// 解码器名 256字节，手机没用
		byte decodeName[] = new byte[256];

		public void read() {
			streamCnt = readInt();

			reserver = readInt();

			for (int i = 0; i < wavInfo.length; i++) {
				wavInfo[i] = new IWAVStreamInfo();
				wavInfo[i].read();
			}

			videoIndex = readInt();

			audioIndex = readInt();

			// System.out.println("videoIndex=" + videoIndex + " audioIndex="
			// + audioIndex);

			recoderTime = readInt();

			for (int i = 0; i < startTime.length; i++) {
				startTime[i] = readInt();
			}

			for (int i = 0; i < endTime.length; i++) {
				endTime[i] = readInt();
			}

			readBytes(decodeName, decodeName.length);
		}
	}

	public class IWAVStreamInfo {
		/** 流类型 4字节数字，1音频，2视频 */
		int streamType;

		/** 编码类型 4字节数字，28表示h264,65542表示g711u,65543表示g711a */
		int encodeType;

		/** 流类标记 4字节数字，没用到 */
		int streamTag;

		/** 视频宽 4字节数字 */
		int videoWidth;

		/** 视频高 4字节数字 */
		int videoHeight;

		/** profile 4字节数字 */
		int profile;

		/** level 4字节数字 */
		int level;

		/** 宽纵比宽 4字节数字 */
		int wratio;

		/** 宽纵比高 4字节数字 */
		int hratio;

		/** 保留 4字节 */
		int reserve;

		/** 保留 32字节 */
		byte rsbyte[] = new byte[32];

		/** 码率 4字节数字 */
		int codeRatio;

		/** 附加数据大小 4字节数字 */
		int attachSize;

		/** 附加数据 4096字节 */
		byte attachData[] = new byte[4096];

		/** 保留 16字节 */
		byte rsbytes_1[] = new byte[16];

		/** 保留数据 40字节 */
		byte rsbytes_2[] = new byte[40];

		public void read() {
			streamType = readInt();

			encodeType = readInt();

			streamTag = readInt();

			videoWidth = readInt();

			videoHeight = readInt();

			profile = readInt();

			level = readInt();

			wratio = readInt();

			hratio = readInt();

			reserve = readInt();

			readBytes(rsbyte, rsbyte.length);

			codeRatio = readInt();

			attachSize = readInt();

			readBytes(attachData, attachData.length);

			readBytes(rsbytes_1, rsbytes_1.length);

			readBytes(rsbytes_2, rsbytes_2.length);

			// System.out.println("encodeType=" + encodeType);
			// System.out.println("videoWidth=" + videoWidth + " videoHeight="
			// + videoHeight);
		}
	}
}
