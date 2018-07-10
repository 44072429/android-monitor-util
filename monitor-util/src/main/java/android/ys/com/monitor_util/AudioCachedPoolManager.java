package android.ys.com.monitor_util;

public class AudioCachedPoolManager {
	private static AudioCachedPoolManager instance = null;
	
	private AudioCachedPoolManager() {
		
	}
	
	public static AudioCachedPoolManager singleton() {
		if (instance == null) {
			synchronized(AudioCachedPoolManager.class) {
				AudioCachedPoolManager objTemp = instance;
				if (objTemp == null) {
					objTemp = new AudioCachedPoolManager();
					instance = objTemp;
				}
			}
		}
		return instance;
	}
	
	/**读数据缓冲池*/
	private AudioCachedPool inputPool;
	
	/**写数据缓冲池*/
	private AudioCachedPool outputPool;
	
	/**
	 * 初始化读数据缓冲池
	 * @param cachedCount 缓冲数目
	 * @return
	 */
	public boolean initInputPool(int cachedCount) {
		if (inputPool == null) {
			inputPool = new AudioCachedPool(0,cachedCount,AudioConfig.Audio_Cell_Size);
		}
		return true;
	}
	
	/**
	 * 初始化写数据缓冲池
	 * @param cachedCount 缓冲数目
	 * @return
	 */
	public boolean initOutputPool(int cachedCount) {
		if (outputPool == null) {
			outputPool = new AudioCachedPool(1,cachedCount,AudioConfig.Audio_Cell_Size);
		}
		return true;
	}
	
	public boolean destroyInputPool() {
		if (inputPool != null) {
			inputPool.destroyCached();
			inputPool = null;
			return true;
		}
		return false;
	}
	
	public boolean destroyOutputPool() {
		if (outputPool != null) {
			outputPool.destroyCached();
			outputPool = null;
			return true;
		}
		return false;
	}
	
	public AudioCachedPool getInputPool() {
		return inputPool;
	}
	
	public AudioCachedPool getOutputPool() {
		return outputPool;
	}
}

