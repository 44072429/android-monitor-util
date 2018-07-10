package android.ys.com.monitor_util;

/**
 * 高速缓冲队列
 */
public class CachedQueue {
	/** 缓冲队列 */
	private CachedCell queue[];

	/** 队列长度 */
	private int queueLen;

	/** 头部[指向下一个可写的空位置][队列至少要有1个空位,否则数据混乱] */
	private int front;

	/** 尾部[指向下一个可读的位置] */
	private int rear;

	public CachedQueue(int count) {
		if (!(count > 19 && count < 101)) {
			return;
		}
		queueLen = count;
		queue = new CachedCell[count];
		for (int i = 0; i < count; i++) {
			queue[i] = new CachedCell();
		}
	}

	/**
	 * 得到元素数量
	 * 
	 * @return
	 */
	public int getElements() {
		if (front == rear) {
			return 0;
		}
		if (front > rear) {
			return front - rear;
		} else {
			return queueLen - rear + 1 + front;
		}
	}

	/**
	 * 得到缓冲数据
	 * 
	 * @return
	 */
	public CachedCell pollElement() {
		if (front == rear)
			return null;
		int index = rear++;
		if (rear >= queueLen)
			rear = 0;
		return queue[index];
	}

	/**
	 * 添加缓冲数据
	 * 
	 * @param data
	 *            数据数组
	 * @param dataLength
	 *            数据长度
	 * @param rt
	 *            渲染的允许时间
	 */
	public boolean offerElement(byte data[], int dataLength, long rt) {
		// 至少保证头部和尾部有一个空位
		int index = -1;
		if (front == rear) { // 头尾相等
			index = front;
			front++;
			if (front >= queueLen)
				front = 0;
		} else if (front > rear) { // 头在前,尾在后
			if (front + 1 < queueLen) {
				index = front;
				front++;
			} else if (rear > 0) { // 头部不能追到尾部
				index = front;
				front = 0;
			}
		} else { // 头在后,尾在前
			if (front + 1 < rear) { // 头不能追到尾部
				index = front;
				front++;
			}
		}

		if (index == -1)
			return false;

		CachedCell cell = queue[index];
		cell.addBuffer(data, dataLength, rt);

		return true;
	}

	/**
	 * 是否有空的缓冲区,如果没有说明缓冲区已满
	 * 
	 * @return
	 */
	public boolean hasEmptyCached() {
		if (front == rear) { // 头尾相等
			return true;
		} else if (front > rear) { // 头在前,尾在后
			if (front + 1 < queueLen) {
				return true;
			} else if (rear > 0) { // 头部不能追到尾部
				return true;
			}
		} else { // 头在后,尾在前
			if (front + 1 < rear) { // 头不能追到尾部
				return true;
			}
		}
		return false;
	}

	/**
	 * 得到缓冲长度大小
	 * 
	 * @return
	 */
	public int getQueueLen() {
		return queueLen;
	}

	/**
	 * 清空数据
	 */
	public void clear() {
		front = 0;
		rear = 0;
	}
}
