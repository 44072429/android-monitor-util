package android.ys.com.monitor_util;

/**
 * 缓冲池管理
 */
public class CachedPoolManager {
	private static CachedPoolManager instance = null;

	private CachedPoolManager() {

	}

	public static CachedPoolManager singleton() {
		if (instance == null) {
			synchronized (CachedPoolManager.class) {
				CachedPoolManager objTemp = instance;
				if (objTemp == null) {
					objTemp = new CachedPoolManager();
					instance = objTemp;
				}
			}
		}
		return instance;
	}

	/** 缓冲池数组 */
	private CachedPool pools[] = new CachedPool[4];

	/**
	 * 初始化缓冲池
	 * 
	 * @param index
	 * @return
	 */
	public boolean initCachedPool(int index) {
		if (pools[index] == null) {
			pools[index] = new CachedPool(index, 51);
		}
		return true;
	}

	/**
	 * 销毁缓冲池
	 * 
	 * @param index
	 * @return
	 */
	public boolean destroyCachedPool(int index) {
		if (pools[index] != null) {
			pools[index].destroyCached();
			pools[index] = null;
		}
		return true;
	}

	/**
	 * 得到缓冲池
	 * 
	 * @param index
	 * @return
	 */
	public CachedPool getCachedPool(int index) {
		return pools[index];
	}
}
