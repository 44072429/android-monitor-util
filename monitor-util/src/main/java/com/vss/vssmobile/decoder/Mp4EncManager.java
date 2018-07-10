package com.vss.vssmobile.decoder;

public class Mp4EncManager {
	private long handle;

	private boolean isEncoding;

	private static boolean isInitialized = false;

	private Mp4EncManager() {
		initMp4Enc();
	}

	private static class Mp4EncManagerMaker {
		private static final Mp4EncManager instance = new Mp4EncManager();
	}

	public static Mp4EncManager singleton() {
		return Mp4EncManagerMaker.instance;
	}

	/**
	 * mp4录像机初始化
	 */
	private void initMp4Enc() {
		handle = Mp4Enc.handle;
		isEncoding = false;
		isInitialized = true;
	}

	/**
	 * mp4录像机终止化<br>
	 * mp4录像机全局维一性,不可同时有多个录像在运行<br>
	 * 释放一次C内存空间就行<br>
	 * 注意isInitialized是静态变量不能由singleton方法后调用<br>
	 * 因为如果mp4录像机没有初始化调用singleton方法后会初始化,重新初始化C内存空间<br>
	 * 没有必要，同时也是错误的<br>
	 */
	public static void finalizeMp4Enc() {
		if (isInitialized) {
			Mp4Enc.ReleaseInstance(Mp4Enc.handle);
			isInitialized = false;
		}
	}

	/**
	 * 开始mp4录像
	 * 
	 * @param absFileName
	 *            [文件绝对路径]
	 * @param width
	 *            [视频宽度]
	 * @param height
	 *            [视频高度]
	 * @param fps
	 *            [帧率]
	 */
	public void startRecord(String absFileName, int width, int height, int fps) {
		Mp4Enc.startwrite(handle, absFileName);
		Mp4Enc.SetVideoSize(handle, width, height);
		Mp4Enc.SetVideoFrameRate(handle, fps);
		isEncoding = true;
	}

	/**
	 * 结束mp4录像
	 */
	public void stopRecord() {
		isEncoding = false;
		Mp4Enc.stop(handle);
	}

	/**
	 * 释放mp4编码器对象
	 */
	public void releaseRecord() {
		isEncoding = false;
		Mp4Enc.ReleaseInstance(handle);
	}

	/**
	 * 输入h264帧数据
	 * 
	 * @param data
	 */
	public void offerFrame(byte frame[], int frameLength) {
		if (frame != null && isEncoding) {
			Mp4Enc.InsertVideoBuffer(handle, frame, frameLength);
		}
	}

	/**
	 * 输入acc或amr音频数据
	 */
	public void offerAudio(byte audio[], int audioLength) {
		if (audio != null && isEncoding) {
			Mp4Enc.InsertAudioBuffer(handle, audio, audioLength);
		}
	}

	public boolean isEncoding() {
		return isEncoding;
	}

}
