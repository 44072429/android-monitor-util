package android.ys.com.monitor_util;

/**
 * socket状态枚举
 * 
 * @author Ray 2011-11-11
 */
public final class SocketState {

	/** socket状态连接成功 */
	public static final int Socket_Connect = 1;

	/** socket关闭 */
	public static final int Socket_Close = 2;

	/** socket出错 */
	public static final int Socket_Error = 3;

	/** 登陆成功 */
	public static final int Socket_LoginSuccess = 4;

	/** 登录失败 */
	public static final int Socket_LoginFailure = 5;

	/** 录像查询结果 */
	public static final int VideoQuery_Result = 6;

	/** 录像查询超时 */
	public static final int VideoQuery_OverTime = 7;

	/** 回放开始 */
	public static final int VideoReplay_Start = 8;

	/** 读取数据异常 */
	public static final int Socket_Read_Error = 9;

	/** 消息包处理异常 */
	public static final int Packet_Exe_Error = 10;

}
