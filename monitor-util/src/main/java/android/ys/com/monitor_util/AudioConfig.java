package android.ys.com.monitor_util;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.ys.com.monitor_util.util.LogTools;

@SuppressWarnings("deprecation")
public class AudioConfig {
	private static AudioConfig instance = null;

	private AudioConfig() {
		sampleRateInHz = 8000;
		channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	}

	public static AudioConfig singleton() {
		if (instance == null) {
			synchronized (AudioConfig.class) {
				AudioConfig objTemp = instance;
				if (objTemp == null) {
					objTemp = new AudioConfig();
					instance = objTemp;
				}
			}
		}
		return instance;
	}

	/** 音频播放缓冲池单元数目 */
	public static final int Audio_Track_Count = 500;

	/** 音频采集缓冲池单元数目 */
	public static final int Audio_Record_Count = 500;

	/** 音频缓冲池单元记录大小 */
	public static final int Audio_Cell_Size = 400;

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

	/**
	 * 音频数据大小[因android各个音频设备,数据缓冲大小不一样,而设备那边需要指定大小]
	 */
	public int audioCellSize;

	/**
	 * 初始化采样参数
	 * 
	 * @param rateInHz
	 * @param chlConfig
	 * @param audioFmt
	 * @param adtype
	 */
	public void initParams(int rateInHz, int chlConfig, int audioFmt, int adtype) {
		sampleRateInHz = rateInHz;
		channelConfig = chlConfig;
		audioFormat = audioFmt;
		audioType = adtype;
		audioCellSize = 0;
	}

	/**
	 * 得到最小音频缓冲单元大小[音频播放]
	 * 
	 * @return
	 */
	public int getAudioTrackMinBufferSize() {
		return AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
	}

	/**
	 * 得到最小音频缓冲单元大小[音频采集]
	 * 
	 * @return
	 */
	public int getAudioRecordMinBufferSize() {
		return AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
	}

	/**
	 * 得到音频数据大小[消息包相关]
	 * 
	 * @return
	 */
	public int getAudioCellSize() {
		return audioCellSize;
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
		audioCellSize = 0;
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
}
