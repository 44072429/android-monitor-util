package android.ys.com.monitor_util;

/**
 * 多媒体消息编号
 */
public class MediaDefine {
	/** 设备连接消息号 */
	public static final int CS_Device_Connect = 10020;

	/** 主帧消息号 */
	public static final int SC_Main_Frame = 23000;

	/** CS次帧消息号 */
	public static final int CS_Sub_Frame = 20001;

	/** CS请求媒体消息包号 */
	public static final int CS_Query_Media = 23001;

	/** SC次帧消息号 */
	public static final int SC_Sub_Frame = 20001;

	/** 视频类型 */
	public static final int Video_Type = 20002;

	/** 音频类型 */
	public static final int Audio_Type = 20003;

	/** 云端控制命令消息包 */
	public static final int Cloud_Command = 21000;

	/** CS录像查询 */
	public static final int CS_Query_Video = 10009;

	/** SC返回录像查询结果 */
	public static final int SC_Query_Video = 10010;

	/** 心跳消息 */
	public static final int CS_Heart_Beat = 21104;

	/** 开始回放前命令消息 */
	public static final int CS_Start_Replay = 10020;

	/** 启动回放消息 */
	public static final int CS_Play_ByTime = 10023;

	/** 回放录像定位 */
	public static final int CS_Seek_ByTime = 10022;

	/** 回放改变速率 */
	public static final int CS_Change_Speed = 10024;
}
