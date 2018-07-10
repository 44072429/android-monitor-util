package android.ys.com.monitor_util;

import android.ys.com.monitor_util.util.LogTools;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 多媒体服务器客户端处理类
 */
public class MediaClient implements Runnable {
	/** 定义输入流，以接收服务器端传来的数据 */
	private InputStream inputStream;

	/** 定义输出流，以向服务器端发送数据 */
	private OutputStream outputStream;

	/** 上次心跳的时间 */
	private long startHeartTime;

	/** 服务端ip地址 */
	public String ip = "liupanping00.6655.la";

	/** 服务端端口号 */
	public int port = 21002;

	/** socket对象 */
	public Socket socket;

	/** 是否已经连接成功 */
	private boolean isConnect;

	/** 心跳包时间内容 */
	private static final int heart_wait_time = 30000;

	/** 是否已经连接成功 */
	private boolean isLogined = false;

	/** 连接数据 */
	private MediaConnectData cntData;

	private int index;

	private final LinkEventProxy proxy = LinkEventProxyManager.getProxy("video_socket");

	protected MediaClient(int index) {
		this.index = index;
	}

	@Override
	public void run() {
		try {
			connect();

			TMediaPacketFactory.singleton(index).setInputStream(inputStream);
			TMediaPacketFactory.singleton(index).setClient(this);

			// 等N毫秒为读数据准备
			Thread.sleep(200);

			while (isConnect) {
				/***
				 * long tickCount = System.currentTimeMillis(); if (tickCount -
				 * startHeartTime > heart_wait_time) { startHeartTime =
				 * tickCount; }
				 **/

				processInput();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void init(MediaConnectData cntData) {
		// 高16位为1代表子码流,0代码主码流
		LogTools.addLogE("MediaClient.init", "ip==" + cntData.ip + "  port==" + cntData.port);
		cntData.channelId = (1 << 16) + cntData.channelId;
		cntData.endId = 0;
		this.cntData = cntData;
		this.ip = cntData.ip;
		this.port = cntData.port;

		// 加入到线程池中,执行任务
		MediaClientManager.singleton().getExecutorService().submit(this);
	}

	private void connect() {
		try {
			System.out.println("MediaClient ip--" + ip);

			socket = new Socket(ip, port);

			inputStream = socket.getInputStream();

			outputStream = socket.getOutputStream();

			startHeartTime = System.currentTimeMillis();

			isConnect = true;

			proxy.callBack("" + index, SocketState.Socket_Connect);

			// 连接到设备
			connectToDevice();
		} catch (Exception e) {
			proxy.callBack("" + index + "#" + e.getMessage(), SocketState.Socket_Error);
		}
	}

	/** 设备连接 */
	private void connectToDevice() {
		if (isLogined)
			return;

		MediaOutput msg = new CSMediaDeviceConnect(cntData.deviceId, cntData.channelId, cntData.endId);
		msg.sendPacket(outputStream);

		isLogined = true;
	}

	@SuppressWarnings("unused")
	private void heartBeat() {
		System.out.println("MediaClient hearBeat!");
	}

	private void processInput() {
		try {
			TMediaPacketFactory.singleton(index).creatSCPacket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭连接
	 */
	public void closeConnect() {
		try {
			isConnect = false;
			isLogined = false;

			// 关闭连接，释放端口资源
			if (socket != null) {
				socket.close();
				socket = null;
			}

			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}

			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("MediaClient 断开连接!");
		proxy.callBack("" + index, SocketState.Socket_Close);
	}

	public int getIndex() {
		return index;
	}

	public void setIsLogined(boolean logined) {
		this.isLogined = logined;
	}

	public boolean isActive() {
		return isConnect && isLogined;
	}

	public void updateWaitTime(int waitTime) {
		/***
		 * if (!(waitTime >= 40 && waitTime <= 100)) { this.waitTime = 40; }
		 * else { this.waitTime = waitTime; }
		 ***/
	}

	/**
	 * 得到设备连接数据
	 * 
	 * @return
	 */
	public MediaConnectData getMediaConnectData() {
		return cntData;
	}

	/**
	 * 发送消息包
	 * 
	 * @param msg
	 */
	public boolean sendPacket(MediaOutput msg) {
		if (isActive() && msg != null) {
			return msg.sendPacket(outputStream);
		}
		return false;
	}
}
