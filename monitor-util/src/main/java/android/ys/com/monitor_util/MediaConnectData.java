package android.ys.com.monitor_util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 流媒体数据结构体
 */
public class MediaConnectData implements Parcelable {
	/**服务端ip地址*/
	public String ip = "0.0.0.0";
	
	/**服务端端口号*/
	public int port = 0;
	
	/**设备id号*/
	public int deviceId = 0;
	
	/**通道id号*/
	public int channelId = 0;
	
	/**终端编号*/
	public int endId = 0;
	
	/**连接到设备的id编号[用于播放状态显示]*/
	public int orgDataId = 0;
	
	public MediaConnectData() {
	}
	
	public void clear() {
		ip = "0.0.0.0";
		port = 0;
		deviceId = 0;
		channelId = 0;
		endId = 0;
		orgDataId = 0;
	}
	
	public boolean isValid() {
		if (ip == null) return false;
		if (!(port > 0 && port < 65535)) return false;
		if (deviceId <= 0) return false;
		if (channelId < 0) return false;
		if (endId < 0) return false;
		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ip);
		dest.writeInt(port);
		dest.writeInt(deviceId);
		dest.writeInt(channelId);
		dest.writeInt(endId);
		dest.writeInt(orgDataId);
	}
	
	/**
	 * 静态构造类必须是 static,final标记的,<br>
	 * 且 CREATEOR名字不能改,必须大写,读写顺序要一致 <br>
	 */
	public static final Creator<MediaConnectData> CREATOR =
			new Creator<MediaConnectData>(){
				@Override
				public MediaConnectData createFromParcel(Parcel source) {
					MediaConnectData cntData = new MediaConnectData();
					
					cntData.ip = source.readString();
					cntData.port = source.readInt();
					cntData.deviceId = source.readInt();
					cntData.channelId = source.readInt();
					cntData.endId = source.readInt();
					cntData.orgDataId = source.readInt();
					
					return cntData;
				}

				@Override
				public MediaConnectData[] newArray(int size) {
					return new MediaConnectData[size];
				}
	};
}
