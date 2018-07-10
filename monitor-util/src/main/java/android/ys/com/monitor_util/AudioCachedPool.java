package android.ys.com.monitor_util;

public class AudioCachedPool {
	/**缓冲队列*/
	private AudioCachedQueue queue;
	
	/**缓冲池编号*/
	private int index;
	
	/**上次记录的时间 用于渲染等待的时间*/
	private long lastrt;
	
	public AudioCachedPool(int index,int cachedCount,int minBufferSize) {
		this.index = index;
		lastrt = 0;
		queue = new AudioCachedQueue(cachedCount,minBufferSize);
	}
	
	/**
	 * 添加数据
	 * @param data
	 * @param dataLength
	 * @param rt
	 * @return
	 */
	public synchronized boolean addBuffer(byte data[],int dataLength,long rt) {
		if (!queue.hasEmptyCached()) {
			System.out.println("音频缓冲区已满，清空 index=" + index);
			//[情形:进数据的速度,快于取数据的速度]
			//缓冲区已满,直接清除缓冲区
			queue.clear();
			//上次记录时间清零
			lastrt = 0;
		}
		
		//默认等待20ms
		long renderTime = 20;
		if (lastrt == 0) {
			lastrt = rt;
		} else {
			renderTime = rt - lastrt;
			lastrt = rt;
		}
		
		//添加数据
		queue.offerElement(data, dataLength, renderTime);
		
		return true;
	} 
	
	/**
	 * 按顺序提取一个缓冲对象
	 * @return
	 */
	public synchronized AudioCachedCell getBuffer() {
		if (queue.getElements() == 0) {
			return null;
		}
		AudioCachedCell cell = queue.pollElement();
		return cell;
	}

	public synchronized void destroyCached() {
		queue = null;
		index = 0;
		lastrt = 0;
	}
	
	/**
	 * 得到缓冲池编号
	 * @return
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * 状态重置
	 */
	public void reset() {
		lastrt = 0;
		queue.clear();
	}
}
