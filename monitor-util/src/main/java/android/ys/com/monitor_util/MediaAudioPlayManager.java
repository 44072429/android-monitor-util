package android.ys.com.monitor_util;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.ys.com.monitor_util.util.LogTools;

import com.vss.vssmobile.decoder.G711EncDec;

public class MediaAudioPlayManager {
	private static MediaAudioPlayManager instance = null;

	private MediaAudioPlayManager() {

	}

	public static MediaAudioPlayManager singleton() {
		if (instance == null) {
			synchronized (MediaAudioPlayManager.class) {
				MediaAudioPlayManager objTemp = instance;
				if (objTemp == null) {
					objTemp = new MediaAudioPlayManager();
					instance = objTemp;
				}
			}
		}

		return instance;
	}

	private AudioCachedPool cachedPool = null;

	private int playIndex = -1;

	private boolean isPlaying = false;

	private AudioPlayTask playTask = null;

	private Thread playThread = null;

	/**
	 * 初始化缓冲区
	 */
	private void initializeAudioCachedPool() {
		if (cachedPool == null) {
			cachedPool = new AudioCachedPool(0, AudioConfig.Audio_Track_Count, AudioConfig.Audio_Cell_Size);
		}
	}

	/**
	 * 销毁缓冲区
	 */
	@SuppressWarnings("unused")
	private void finalizeAudioCachedPool() {
		if (cachedPool != null) {
			cachedPool.destroyCached();
			cachedPool = null;
		}
	}

	/**
	 * 设置选择播放音频index
	 * 
	 * @param playIndex
	 */
	private void setPlayIndex(int playIndex) {
		this.playIndex = playIndex;
	}

	public int getPlayIndex() {
		return playIndex;
	}

	/**
	 * 将数据添加音频缓冲池中
	 * 
	 * @param audioBuffer
	 * @param dtLength
	 * @param pts
	 */
	public void addAudioBuffer(byte audioBuffer[], int dtLength, long pts) {
		if (cachedPool != null) {
			cachedPool.addBuffer(audioBuffer, dtLength, pts);
		}
	}

	/**
	 * 音频控制命令
	 * 
	 * @param playIndex
	 *            播放的音频索引值[0-3]
	 * @param playing
	 *            是否播放中
	 * @param stop
	 *            是否需要关闭音频播放
	 */
	public void processAudioCommand(int playIndex, boolean playing, boolean stop) {
		if (stop) {
			stopAudioPlaying();
			return;
		}
		if (!(playIndex >= 0 && playIndex < 4)) {
			return;
		}

		if (playing) {
			// 选择的流索引没有音频流
			MediaAudioParams audioParams = MediaAudioParamsManager.singleton().getAudioParams(playIndex);
			if (!audioParams.hasAudioStream()) {
				return;
			}

			// 分析缓冲区要不要清空,不同音频切换就要清空
			if (this.playIndex != playIndex) {
				if (cachedPool != null)
					cachedPool.reset();
			}

			// 选择状态
			setPlayIndex(playIndex);
			isPlaying = true;

			// 如果已经是播放状态了就不创建音频对象了,直接设置音频播放就行
			if (playThread != null && playThread.isAlive()) {
				return;
			}

			// 开始音频播放
			startAudioPlaying();
		} else {
			// 分析缓冲区要不要清空,不同音频切换就要清空
			if (this.playIndex != playIndex) {
				if (cachedPool != null)
					cachedPool.reset();
			}

			// 选择状态
			setPlayIndex(playIndex);
			isPlaying = false;
		}
	}

	/**
	 * 开始音频播放
	 */
	private void startAudioPlaying() {
		// 如果缓冲区为空,就初始化缓冲区
		if (cachedPool == null) {
			initializeAudioCachedPool();
		}

		// 启动音频播放线程
		playTask = new AudioPlayTask(playIndex);
		playThread = new Thread(playTask);
		playThread.start();
	}

	/**
	 * 停止音频播放
	 */
	private void stopAudioPlaying() {
		try {
			isPlaying = false;
			playIndex = -1;

			// 停止音频播放
			try {
				if (playThread != null && playThread.isAlive()) {
					playThread.interrupt();
				}
				playThread = null;
			} catch (Exception e1) {
			}

			// 释放音轨对象
			try {
				if (playTask != null) {
					playTask.reaseAudioTrack();
					playTask = null;
				}
			} catch (Exception e2) {
			}
		} catch (Exception ex) {
			LogTools.addLogE("MediaAudioPlayManager.stopAudioPlaying", ex.getMessage());
		}
	}

	private class AudioPlayTask implements Runnable {
		private MediaAudioParams audioParams = null;

		private AudioTrack audioTrack = null;

		// 音频播放需要的最小缓冲大小
		private int minBufferSize = 0;

		// 音频编码类型 g711a,g711u
		private int codeType = 0;

		// g711数据解码缓冲区
		private byte pcm_buffer[] = null;

		public AudioPlayTask(int index) {
			if (index >= 0 && index < 4) {
				try {
					audioParams = MediaAudioParamsManager.singleton().getAudioParams(index);

					minBufferSize = audioParams.getAudioTrackMinBufferSize();

					codeType = audioParams.audioType;

					pcm_buffer = new byte[minBufferSize];

					// 初始化音轨对象
					audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, audioParams.sampleRateInHz,
							audioParams.channelConfig, audioParams.audioFormat, minBufferSize, AudioTrack.MODE_STREAM);
				} catch (Exception e) {
					Log.e("error", "AudioPlayTask.create?" + e.getMessage());
					audioParams = null;
					audioTrack = null;
					codeType = 0;
					pcm_buffer = null;
				}
			}
		}

		@Override
		public void run() {
			if (audioParams == null || audioTrack == null) {
				LogTools.addLogE("AudioPlayTask", "播放参数为空!");
				return;
			}

			if (cachedPool == null) {
				LogTools.addLogE("AudioPlayTask", "音频缓冲池为空!");
				return;
			}

			try {
				// 等待3秒种缓冲
				Thread.sleep(3000);

				audioTrack.play();

				while (true) {
					// 音频播放暂停时,就等待
					if (!isPlaying) {
						Thread.sleep(10);
						continue;
					}

					// 从缓冲池取音频数据
					AudioCachedCell cell = cachedPool.getBuffer();
					if (cell != null) {
						int decCount = 0;
						// g711a数据解码为pcm音频数据
						if (codeType == MediaType.type_g711a) {
							decCount = G711EncDec.AVoiceDecode(cell.buffer, pcm_buffer, cell.dtLength);
						}
						// g711u数据解码为pcm音频数据
						else if (codeType == MediaType.type_g711u) {
							decCount = G711EncDec.UVoiceDecode(cell.buffer, pcm_buffer, cell.dtLength);
						}
						// 播放pcm数据
						if (decCount > 0) {
							audioTrack.write(pcm_buffer, 0, decCount);
						}
					}
				}
			} catch (Exception e) {
				LogTools.addLogE("AudioPlayTask", "error?" + e.getMessage());
			}
		}

		public void reaseAudioTrack() {
			if (audioTrack != null) {
				audioTrack.stop();
				audioTrack.release();
				audioTrack = null;
			}
		}
	}
}
