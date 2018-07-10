package android.ys.com.monitor_util;

import android.graphics.Bitmap;
import android.ys.com.monitor_util.util.LogTools;

public class H264DecoderTask implements Runnable {
	private int index;

	/** 私有解析器 */
	private H264Decoder decoder = null;

	private boolean isRunning = false;

	/** 缓冲池对象 */
	private CachedPool pool = null;

	/** 渲染对象 */
	private VideoSurfaceView surface = null;

	private Thread decoderThread = null;

	protected H264DecoderTask(int width, int height, int index) {
		this.index = index;

		decoder = new H264Decoder(width, height, index);

		CachedPoolManager.singleton().initCachedPool(index);
		pool = CachedPoolManager.singleton().getCachedPool(index);
	}

	public void startDecode() {
		isRunning = true;

		pool.reset();
		decoderThread = new Thread(this);
		decoderThread.start();
	}

	public void stopDecode() {
		isRunning = false;

		// 解码需要等待decoderThread线程终止后,才可将docoder解码器释放C内容空间
		if (decoderThread != null && decoderThread.isAlive()) {
			try {
				decoderThread.interrupt();
				decoderThread.join();
			} catch (InterruptedException e) {
			}
		}
		decoderThread = null;
		pool = null;

		// 释放解码器C内容空间,因decoderThread线程在解码器释放后,可能还会执行,因此需要
		// 等待decoderThread结束后才能释放
		if (decoder != null) {
			decoder.finalizeDecoder();
			decoder = null;
		}
	}

	public Bitmap decodeFrame(byte data[], int dataLength) {
		Bitmap bmp = decoder.decodeFrameEx(data, dataLength);
		return bmp;
	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				long lasttick = System.currentTimeMillis();
				// CachedCell数据有可能被缓冲池修改,记录一下当前帧渲染的时间
				CachedCell cell = pool.getBuffer();
				if (cell != null) {
					long rt = cell.renderTime;
					surface.doRender(cell.buffer, cell.dtLength);
					long dtime = rt - (System.currentTimeMillis() - lasttick);
					if (dtime > 0) {
						// System.out.println(String.format("wait???rt=%d
						// dtime=%d", rt,dtime));
						Thread.sleep(dtime);
					}
				} else {
					Thread.sleep(55);
				}
			}
		} catch (Exception e) {
			// stopDecode();
			LogTools.addLogE("H264DecoderTask.run", e.getMessage());
		}
	}

	public void setSurface(VideoSurfaceView surface) {
		this.surface = surface;
	}

	public void doRender(byte data[], int dataLen) {
		if (surface != null) {
			surface.doRender(data, dataLen);
		}
	}
}
