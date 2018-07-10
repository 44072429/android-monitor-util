package android.ys.com.monitor_util;

/**
 * 设备连接消息包
 */
public class CSMediaDeviceConnect extends MediaOutput {
	/** 设备id号 */
	public int deviceId = 0;

	/** 通道id号 */
	public int channelId = 0;

	/** 终端编号 */
	public int endId = 0;

	public CSMediaDeviceConnect(int dev, int chl, int tid) {
		this.deviceId = dev;
		this.channelId = chl;
		this.endId = tid;
	}

	@Override
	public int getPacketId() {
		return MediaDefine.CS_Device_Connect;
	}

	@Override
	public int getBodySize() {
		return PacketUtil.sizeOfInt() + PacketUtil.sizeOfInt() + PacketUtil.sizeOfInt();
	}

	@Override
	public void processOutput() {
		writeBodyInt(deviceId);
		writeBodyInt(channelId);
		writeBodyInt(endId);
	}

}
