package android.ys.com.monitor_util;

import java.util.HashMap;

/**
 * 事件管理类
 */
public class LinkEventProxyManager {
	private static final HashMap<String,LinkEventProxy>
	              hmLink = new HashMap<String,LinkEventProxy>();
	
	/**
	 * 得到 LinkEventProxy 对象
	 * @param key
	 * @return
	 */
	public static LinkEventProxy getProxy(String key) {
		LinkEventProxy proxy = hmLink.get(key);
		if (proxy == null) {
			proxy = new LinkEventProxy();
			hmLink.put(key, proxy);
		}
		return proxy;
	}
	
	/**
	 * 删除一个LinkEventProxy 对象
	 * @param key
	 * @return
	 */
	public static boolean removeProxy(String key) {
		if (hmLink.containsKey(key)) {
			hmLink.remove(key);
			return true;
		}
		return false;
	}
}
