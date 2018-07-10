package android.ys.com.monitor_util;

import android.ys.com.monitor_util.util.LogTools;

public class DecoderTaskManager {
	public H264DecoderTask decoderTask[] = new H264DecoderTask[MediaClientConst.Max_Buffer];

	private DecoderTaskManager() {

	}

	private static class DecoderTaskManagerMaker {
		private static final DecoderTaskManager instance = new DecoderTaskManager();
	}

	public static DecoderTaskManager singleton() {
		return DecoderTaskManagerMaker.instance;
	}

	public void initDecoderTask(int width, int height, int index) {
		if (!(index >= 0 && index < decoderTask.length)) {
			LogTools.addLogE("DecoderTaskManager.initDecoderTask", "索引值错误");
			return;
		}
		if (!(width >= 100 && width <= 1920) || !(height >= 100 && height <= 1080)) {
			LogTools.addLogE("DecoderTaskManager.initDecoderTask", "解码宽度和高度不对");
			return;
		}
		if (decoderTask[index] != null) {
			LogTools.addLogI("DecoderTaskManager.initDecoderTask", "上次解码停止中...");
			decoderTask[index].stopDecode();
			decoderTask[index] = null;
		}
		LogTools.addLogI("DecoderTaskManager.initDecoderTask", "解码任务创建中...");
		decoderTask[index] = new H264DecoderTask(width, height, index);
	}

	public void startDecoderTask(int index) {
		if (!(index >= 0 && index < decoderTask.length)) {
			LogTools.addLogE("DecoderTaskManager.startDecoderTask", "索引值错误");
			return;
		}
		if (decoderTask[index] == null) {
			LogTools.addLogE("DecoderTaskManager.startDecoderTask", "解码任务为空");
			return;
		}

		LogTools.addLogI("DecoderTaskManager.startDecoderTask", "解码任务启动");
		decoderTask[index].startDecode();
	}

	public void stopDecoderTask(int index) {
		if (!(index >= 0 && index < decoderTask.length)) {
			LogTools.addLogE("DecoderTaskManager.stopDecoderTask", "索引值错误");
			return;
		}

		if (decoderTask[index] == null) {
			LogTools.addLogE("DecoderTaskManager.stopDecoderTask", "解码任务为空");
			return;
		}

		LogTools.addLogI("DecoderTaskManager.stopDecoderTask", "解码任务停止");
		decoderTask[index].stopDecode();
	}

	public H264DecoderTask getDecoder(int index) {
		return decoderTask[index];
	}
}
