package android.ys.com.monitor_util;

public class MediaAudioParamsManager {
	private static MediaAudioParamsManager instance = null;
	
	private MediaAudioParamsManager() {
		initAudioParams();
	}
	
	public static MediaAudioParamsManager singleton() {
		if (instance == null) {
			synchronized (MediaAudioParamsManager.class) {
				MediaAudioParamsManager objTemp = instance;
				if (objTemp == null) {
					objTemp = new MediaAudioParamsManager();
					instance = objTemp;
				}
			}
		}
		return instance;
	}
	
	/**音频播放属性数据,用于四个音频数据属性记录*/
	private MediaAudioParams audioParams[] = null;
	
	private void initAudioParams() {
		audioParams = new MediaAudioParams[4];
		for (int i = 0; i < audioParams.length; i++) {
			audioParams[i] = new MediaAudioParams();
		}
	}
	
	public MediaAudioParams getAudioParams(int index) {
		if (index >= 0 && index < 4) {
			return audioParams[index];
		}
		return null;
	}
}
