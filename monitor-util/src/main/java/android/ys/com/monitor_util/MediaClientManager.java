package android.ys.com.monitor_util;

import android.ys.com.monitor_util.util.LogTools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 流媒体管理对象
 */
public class MediaClientManager {
	private final ExecutorService service = Executors.newFixedThreadPool(MediaClientConst.Max_Buffer);

	private int activeClient = 0;

	private MediaClient client[];

	private MediaClientManager() {
		client = new MediaClient[MediaClientConst.Max_Buffer];
	}

	private static class MediaClientManagerMaker {
		private static final MediaClientManager instance = new MediaClientManager();
	}

	public static MediaClientManager singleton() {
		return MediaClientManagerMaker.instance;
	}

	public void initClient(int index, MediaConnectData cntData) {
		if (!(index >= 0 && index < client.length)) {
			LogTools.addLogE("MediaClientManager.initClient", "索引值错误");
			return;
		}
		if (cntData == null || !cntData.isValid()) {
			LogTools.addLogE("MediaClientManager.initClient", "数据错误");
			return;
		}
		if (client[index] != null && client[index].isActive()) {
			LogTools.addLogI("MediaClientManager.initClient", "强制关闭中...");
			client[index].closeConnect();
			client[index] = null;
		}

		LogTools.addLogI("MediaClientManager.initClient", "连接创建中...");
		client[index] = new MediaClient(index);
		client[index].init(cntData);

		activeClient++;
	}

	public void finalizeClient(int index) {
		if (!(index >= 0 && index < client.length)) {
			LogTools.addLogE("MediaClientManager.finalizeClient", "索引值错误");
			return;
		}
		if (client[index] != null && client[index].isActive()) {
			LogTools.addLogI("MediaClientManager.finalizeClient", "强制关闭中...");
			client[index].closeConnect();
			client[index] = null;
		}
		client[index] = null;

		activeClient--;
	}

	public ExecutorService getExecutorService() {
		return service;
	}

	public MediaClient getMediaClient(int videoIndex) {
		if (videoIndex >= 0 && videoIndex < MediaClientConst.Max_Buffer) {
			return client[videoIndex];
		}
		return null;
	}
}
