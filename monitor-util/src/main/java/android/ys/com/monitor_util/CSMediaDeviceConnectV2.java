package android.ys.com.monitor_util;

import android.ys.com.monitor_util.MediaDefine;
import android.ys.com.monitor_util.MediaOutput;
import android.ys.com.monitor_util.PacketUtil;

/**
 * 设备连接消息包
 */
public class CSMediaDeviceConnectV2 extends MediaOutput {
	/** 设备id号 */
	public int deviceId = 0;

	/** 通道id号 */
	public int channelId = 0;

	/** 流索引 */
	public int streamId = 0;

	public CSMediaDeviceConnectV2(int deviceId, int channelId, int streamId) {
		this.deviceId = deviceId;
		this.channelId = channelId & 0x0000FFFF;
		this.streamId = 1;
	}

	@Override
	public int getPacketId() {
		return 10031;
	}

	@Override
	public int getBodySize() {
		return PacketUtil.sizeOfInt() + PacketUtil.sizeOfInt() + PacketUtil.sizeOfInt() + 128;
	}

	@Override
	public void processOutput() {
		writeBodyInt(deviceId);
		writeBodyInt(channelId);
		writeBodyInt(streamId);
		byte[] token = new byte[128];
		this.writeBodyBytes(token,128);
	}
}
