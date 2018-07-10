package android.ys.com.monitor_util;

import android.util.Log;

/**
 * 云端控制命令消息包
 */
public class CSMediaCloudCommand extends MediaOutput {
	/** 设备号 */
	public int deviceId;

	/** 通道号 */
	public int channelId;

	/** 命令类型 */
	public int commandType;

	/** 附加参数1 0-255 0表示没速度，255表示最快速度，或表示预置位号 */
	public byte attach1;

	/** 附加参数2 保留 */
	public byte attach2;

	/** 2字节 用于字节对齐 */
	public short extraSize;

	@Override
	public int getPacketId() {
		return MediaDefine.Cloud_Command;
	}

	@Override
	public int getBodySize() {
		return PacketUtil.sizeOfInt() * 3 + PacketUtil.sizeOfByte() * 2 + PacketUtil.sizeOfShort();
	}

	@Override
	public void processOutput() {
		Log.e("Main", "deviceId=="+deviceId+"  channelId=="+channelId+"  commandType=="+commandType+"  attach1=="+attach1);
		writeBodyInt(deviceId);
		writeBodyInt(channelId);
		writeBodyInt(commandType);
		writeBodyByte(attach1);
		writeBodyByte(attach2);
		writeBodyShort(extraSize);
	}

	// ***********************云端控件命令枚举**************************************
	public static final int _YS_PTZ_CMD_PRESET_DEL = 21002; // 预置位删除
	public static final int _YS_PTZ_CMD_PRESET_CALL = 21003; // 预置位调用
	public static final int _YS_PTZ_CMD_TOUR_START = 21004; // 巡航开启
	public static final int _YS_PTZ_CMD_TOUR_STOP = 21005; // 巡航结束

	public static final int _YS_PTZ_CMD_LEFT = 21006; // 左
	public static final int _YS_PTZ_CMD_RIGHT = 21007; // 右
	public static final int _YS_PTZ_CMD_TOP = 21008; // 上
	public static final int _YS_PTZ_CMD_BOTTOM = 21009; // 下

	public static final int _YS_PTZ_CMD_LEFT_TOP = 21010; // 左上
	public static final int _YS_PTZ_CMD_LEFT_BOTTOM = 21011; // 左下
	public static final int _YS_PTZ_CMD_RIGHT_TOP = 21012; // 右上
	public static final int _YS_PTZ_CMD_RIGHT_BOTTOM = 21013; // 右下
	public static final int _YS_PTZ_CMD_CENTER = 21014; // 复位

	public static final int _YS_PTZ_CMD_ZOOM1 = 21015; // 拉远，即人物越来越远
	public static final int _YS_PTZ_CMD_ZOOM2 = 21016; // 拉近

	public static final int _YS_PTZ_CMD_FOCUS1 = 21017; // 焦距缩短，即离得越近看的越清晰
	public static final int _YS_PTZ_CMD_FOCUS2 = 21018; // 焦距变长
	public static final int _YS_PTZ_CMD_IRIS1 = 21019; // 光圈变小，即图像会变暗
	public static final int _YS_PTZ_CMD_IRIS2 = 21020; // 光圈变大
}
