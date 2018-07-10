package android.ys.com.monitor_util;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.ys.com.monitor_util.util.LogTools;

@SuppressWarnings("deprecation")
public class MediaAudioParams {
	/** 采样频率 */
	public int sampleRateInHz;

	/** 通道 */
	public int channelConfig;

	/** 采样位数 */
	public int audioFormat;

	/** 音频数据类型 */
	public int audioType;

	/** 分片时间[每隔多少ms采集一次数据] */
	public int ptime;

	/** 音频在IWAVStreamInfo数组中的索引 4字节数字 [如果为-1说明此流媒体无音频流] */
	private int audioIndex;

	public MediaAudioParams() {
		clear();
	}

	/**
	 * 解析音频参数数据<br>
	 * 通道数量 4字节数字，1表示单通道<br>
	 * 位率 4字节数字，如16 <br>
	 * 采样率 4字节数字，如8000 <br>
	 * 分片时间 4字节数字，1000表示1秒传输一次，一般SDP中如果指定a=ptime <br>
	 * 20表示20毫秒采集一次，特殊程序需要关注，app不需要关注 <br>
	 * 
	 * @param dsrc
	 */
	public boolean decodeFromBuffer(byte dsrc[]) {
		if (dsrc == null || dsrc.length < 16)
			return false;
		try {
			int tmpIndex = 0;
			byte tmpBts[] = new byte[4];

			System.arraycopy(dsrc, tmpIndex, tmpBts, 0, 4);
			int chnCount = PacketUtil.bytesToInt(tmpBts);
			tmpIndex += 4;

			System.arraycopy(dsrc, tmpIndex, tmpBts, 0, 4);
			int bitRate = PacketUtil.bytesToInt(tmpBts);
			tmpIndex += 4;

			System.arraycopy(dsrc, tmpIndex, tmpBts, 0, 4);
			int sampleRate = PacketUtil.bytesToInt(tmpBts);
			tmpIndex += 4;

			System.arraycopy(dsrc, tmpIndex, tmpBts, 0, 4);
			int tmpPtime = PacketUtil.bytesToInt(tmpBts);
			tmpIndex += 4;

			// 初始化通道
			if (chnCount == 1) {
				channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
			} else {
				channelConfig = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
			}

			// 初始化位率
			if (bitRate == 16) {
				audioFormat = AudioFormat.ENCODING_PCM_16BIT;
			} else {
				audioFormat = AudioFormat.ENCODING_PCM_8BIT;
			}

			// 初始化采样频率
			sampleRateInHz = sampleRate;

			// 分片时间
			ptime = tmpPtime;

			return true;
		} catch (Exception e) {
			LogTools.addLogE("AudioConfig.decodeFromBuffer", e.getMessage());
			return false;
		}
	}

	/**
	 * 得到最小音频缓冲单元大小[音频播放]
	 * 
	 * @return
	 */
	public int getAudioTrackMinBufferSize() {
		return AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
	}

	public void setAudioIndex(int audioIndex) {
		this.audioIndex = audioIndex;
	}

	/**
	 * 是否有音频流数据
	 * 
	 * @return
	 */
	public boolean hasAudioStream() {
		return audioIndex == 0 || audioIndex == 1;
	}

	/**
	 * 清除音频属性参数
	 * 
	 * @return
	 */
	public void clear() {
		sampleRateInHz = 8000;
		channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		audioType = MediaType.type_g711a;
		audioIndex = -1;
	}
}
