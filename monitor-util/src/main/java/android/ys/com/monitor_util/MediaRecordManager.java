package android.ys.com.monitor_util;

import com.vss.vssmobile.decoder.Mp4EncManager;

/**
 * 视频录像管理类
 */
public class MediaRecordManager {
	private static MediaRecordManager instance;

	private boolean isRecording;

	private int videoIndex;

	private String fileName;

	private final Mp4EncManager mp4Enc = Mp4EncManager.singleton();

	private MediaRecordManager() {

	}

	public static MediaRecordManager singleton() {
		if (instance == null) {
			synchronized (MediaRecordManager.class) {
				MediaRecordManager objTemp = instance;
				if (objTemp == null) {
					objTemp = new MediaRecordManager();
					instance = objTemp;
				}
			}
		}
		return instance;
	}

	public static MediaRecordManager getInstance() {
		return instance;
	}

	/**
	 * 开始mp4录像
	 * 
	 * @param sltIndex
	 *            录像的视频索引值
	 * @param absFileName
	 *            [文件绝对路径]
	 * @param width
	 *            [视频宽度]
	 * @param height
	 *            [视频高度]
	 * @param fps
	 *            [帧率]
	 */
	public synchronized void startRecord(int sltIndex, String absFileName, int width, int height, int fps) {
		mp4Enc.startRecord(absFileName, width, height, fps);
		videoIndex = sltIndex;
		isRecording = true;
		fileName = absFileName;
	}

	public synchronized void stopRecord() {
		isRecording = false;
		videoIndex = -1;
		fileName = null;
		mp4Enc.stopRecord();
	}

	public void reaseRecord() {
		isRecording = false;
		videoIndex = -1;
		fileName = null;
		mp4Enc.releaseRecord();
	}

	public boolean isRecording() {
		return isRecording;
	}

	public int getVideoIndex() {
		return videoIndex;
	}

	/**
	 * 输入h264帧数据
	 * 
	 * @param data
	 */
	public void offerFrame(byte frame[], int frameLength) {
		mp4Enc.offerFrame(frame, frameLength);
	}

	/**
	 * 输入acc或amr音频数据
	 */
	public void offerAudio(byte audio[], int audioLength) {
		mp4Enc.offerAudio(audio, audioLength);
	}

	public String getVideoFileName() {
		return fileName;
	}
}
