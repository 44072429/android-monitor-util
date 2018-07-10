package android.ys.com.monitor_util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.ys.com.monitor_util.util.BaseConfig;

/**
 * 视频显示组件
 */
public class VideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder holder;

	/** 渲染场景的画刷对象 */
	private Paint paint;

	/** 清除背景的画刷对象 */
	private Paint clrPaint;

	/** 渲染标记 */
	private boolean rendering = true;

	private boolean preRendering = false;

	private int sfWidth, sfHeight;

	/** 视频索引标记 */
	private int index = -1;

	/** 视频显示区域 */
	private Rect rect;

	/** 视频截图标记 */
	private boolean isVideoSnap;

	/** 视频录像管理对象 */
	private static final MediaRecordManager recordManager = MediaRecordManager.singleton();

	/** 视频状态显示相关 */
	private boolean isShowing = false;

	/** 视频状态线程 */
	private Thread vdstateThread = null;
	private VideoStateTask stateTask = null;

	/** 视频状态值 */
	private int videoState = 0;

	public VideoSurfaceView(Context context) {
		super(context);

		initSurface();
	}

	public VideoSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initSurface();
	}

	public VideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initSurface();
	}

	private void initSurface() {
		holder = this.getHolder();
		holder.addCallback(this);

		paint = new Paint();
		clrPaint = new Paint();
		clrPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));

		preRendering = false;
		rendering = false;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		sfWidth = this.getWidth();
		sfHeight = this.getHeight();
//		LogTools.addLogI("VideoSurfaceView.surfaceCreated", String.format("w=%d h=%d", sfWidth, sfHeight));
		processRenderArea(sfWidth, sfHeight);
		// surface再次显示时,分析是否播放视频
		if (preRendering && !rendering) {
			startRender();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		sfWidth = this.getWidth();
		sfHeight = this.getHeight();
//		LogTools.addLogI("VideoSurfaceView.surfaceChanged", String.format("w=%d h=%d", sfWidth, sfHeight));
		processRenderArea(sfWidth, sfHeight);
	}

	public void processRenderArea(int w, int h) {
		float dcw = 640.0f;
		float dch = 480.0f;
		float ratioW = w / dcw;
		float ratioH = h / dch;
		float lamda = ratioW < ratioH ? ratioW : ratioH;
		int rw = (int) (dcw * lamda);
		int rh = (int) (dch * lamda);

		int sx = (w - rw) / 2;
		int sy = (h - rh) / 2;
		rect = new Rect(sx, sy, sx + rw, sy + rh);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stopRender();
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * 开始渲染
	 */
	public void startRender() {
		if (!(index >= 0 && index < MediaClientConst.Max_Buffer)) {
//			LogTools.addLogE("VideoSurfaceView.startRender", "索引值错误");
			return;
		}

		// 用子线程启动渲染入口逻辑,处理视频状态显示不对的情况
		// 渲染任务和状态显示任务同时对canvas绘制,会出现状态显示不对的情况
		// 等待一定时间用于显示视频[播放中]状态
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				preRendering = true;
				rendering = true;

				// 关闭状态显示
				if (isShowing && vdstateThread != null) {
					isShowing = false;
					if (vdstateThread.isAlive()) {
						vdstateThread.interrupt();
					}
					vdstateThread = null;
				}
			}
		}).start();
	}

	/**
	 * 停止渲染
	 */
	public void stopRender() {
		rendering = false;
	}

	/**
	 * 关闭surface
	 */
	public void closeSurfaceRender() {
		stopRender();
		preRendering = false;
	}

	/**
	 * 是否渲染中
	 * 
	 * @return
	 */
	public boolean isRendering() {
		return rendering;
	}

	/**
	 * 视频截图
	 */
	public void snap() {
		isVideoSnap = true;
	}

	public void renderEx(Canvas canvas, byte data[], int dataLength) {
		// long lastTick = System.currentTimeMillis();
		H264DecoderTask decoder = DecoderTaskManager.singleton().getDecoder(index);
		Bitmap aFrame = decoder.decodeFrame(data, dataLength);
		if (aFrame == null)
			return;
		// long curTick = System.currentTimeMillis();
		// System.out.println("dtime=" + (curTick - lastTick));

		if (isVideoSnap) {
			LinkEventProxyManager.getProxy("video_snap").callBack(aFrame, index);
			isVideoSnap = false;
		}

		// 背景清空
		// canvas.drawPaint(clrPaint);

		// 渲染一帧
		canvas.drawBitmap(aFrame, null, rect, paint);
	}

	public void doRender(byte data[], int dataLen) {
		// 如果是视频状态就不渲染帧,处理状态显示不对的情况
		if (isShowing)
			return;

		Canvas canvas = holder.lockCanvas();
		if (canvas != null) {
			renderEx(canvas, data, dataLen);
		}
		if (canvas != null)
			holder.unlockCanvasAndPost(canvas);

		// 如果处于录像状态,则写入录像数据
		if (recordManager.isRecording() && recordManager.getVideoIndex() == index) {
			recordManager.offerFrame(data, dataLen);
		}
	}

	private class VideoStateTask implements Runnable {
		private final String LST_BAR[] = { ".", "..", "..." };

		private Paint tmpPaint;

		private Point tmpPnt;

		private long lasttick;

		private int tickstate;

		private boolean addState;

		// *******************视频连接等待相关******************
		// 连接开始计时的时刻点
		private long startTickCount;
		// 是否需要连接记时
		private boolean needTimeCount;

		public VideoStateTask() {
			tmpPaint = new Paint();
			tmpPaint.setTextSize(32);
			tmpPaint.setColor(0xffffffff);
			tmpPnt = new Point();

			lasttick = System.currentTimeMillis();
			tickstate = 0;
			startTickCount = lasttick;
			needTimeCount = false;
		}

		@Override
		public void run() {
			while (isShowing && !rendering) {
				// 视频连接超时后,就关闭连接
				if (needTimeCount) {
					long curtick = System.currentTimeMillis();
					if (curtick - startTickCount > 40000) {
						needTimeCount = false;
						startTickCount = curtick;
						// 调用链式回调事件
						LinkEventProxyManager.getProxy("video_socket").callBack("" + index,
								SocketState.Socket_LoginFailure);
					}
				}

				Canvas canvas = null;
				try {
					canvas = holder.lockCanvas();
					
					if (canvas != null) {
						long tickcount = System.currentTimeMillis();
						if (tickcount - lasttick > 500) {
							lasttick = tickcount;
							tickstate++;
							if (tickstate > 2)
								tickstate = 0;
						}

						tmpPnt.x = sfWidth >> 1;
						tmpPnt.y = sfHeight >> 1;

						String text = null;
						addState = false;
						if (videoState == SocketState.Socket_Connect) {
							text = "视频连接中";
							addState = true;
						} else if (videoState == SocketState.Socket_Error) {
							text = "视频连接出错!";
						} else if (videoState == SocketState.Socket_Close) {
							text = "连接关闭!";
						} else if (videoState == SocketState.Socket_LoginFailure) {
							text = "连接失败!";
						} else if (videoState == SocketState.Socket_LoginSuccess) {
							text = "视频播放中";
							addState = true;
						}
						if (text != null) {
							// 背景清空
							canvas.drawPaint(clrPaint);
							// 居中显示
							BaseConfig.translateCenter(tmpPaint, text, tmpPnt);
							if (addState) {
								text += LST_BAR[tickstate];
							}
							canvas.drawText(text, tmpPnt.x, tmpPnt.y, tmpPaint);
						}
					}
					Thread.sleep(50);
				} catch (Exception e) {
					// LogTools.addLogE("VideoStateTask","run?" +
					// e.getMessage());
				} finally {
					if (canvas != null)
						holder.unlockCanvasAndPost(canvas);
				}
			}
		}

		public void resetTick() {
			lasttick = System.currentTimeMillis();
			tickstate = 0;

			needTimeCount = false;
			startTickCount = lasttick;
		}

		/**
		 * 连接视频超过一定时间后,需要关半没响应的视频连接
		 */
		public void startConnectTimeCount() {
			startTickCount = System.currentTimeMillis();
			needTimeCount = true;
		}
	}

	public void setShowing(boolean showing) {
		if (showing) {
			isShowing = true;
			videoState = 0;
			stateTask = new VideoStateTask();
			vdstateThread = new Thread(stateTask);
			vdstateThread.start();
		} else {
			isShowing = false;
			if (vdstateThread != null && vdstateThread.isAlive()) {
				vdstateThread.interrupt();
			}
			stateTask = null;
			vdstateThread = null;
			videoState = 0;
		}
	}

	public void setVideoState(int state) {
//		LogTools.addLogI("VideoSurfaceView", "setVideoState?state=" + state);
		videoState = state;
		if (stateTask != null) {
			stateTask.resetTick();
		}
		if (videoState == SocketState.Socket_Connect) {
			if (stateTask != null) {
				stateTask.startConnectTimeCount();
			}
		}
	}
}
