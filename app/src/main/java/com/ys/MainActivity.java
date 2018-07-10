package com.ys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.ys.com.android_monitor_util.R;
import android.ys.com.monitor_util.DecoderTaskManager;
import android.ys.com.monitor_util.LinkEventProxy;
import android.ys.com.monitor_util.LinkEventProxyManager;
import android.ys.com.monitor_util.MediaAudioPlayManager;
import android.ys.com.monitor_util.MediaClientManager;
import android.ys.com.monitor_util.MediaConnectData;
import android.ys.com.monitor_util.MediaRecordManager;
import android.ys.com.monitor_util.OnLinkListener;
import android.ys.com.monitor_util.SocketState;
import android.ys.com.monitor_util.VideoSurfaceView;
import android.ys.com.monitor_util.util.LogTools;
import android.ys.com.monitor_util.util.ScreenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.video_surface)
    VideoSurfaceView video_surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /*
        * 初始化操作，必须作
        * */
        ScreenUtils.init(this);

        video_surface.setIndex(0);
        openVideo();
    }

    /** Socket连接状态 */
    private void onSocketState(Object obj, int state) {
        switch (state) {
            case SocketState.Socket_Connect: {
                if (obj instanceof String) {
                    int index = Integer.valueOf((String) obj);
                    // 通过index判断是第几路视频
                    video_surface.setVideoState(SocketState.Socket_Connect);
                }
            }
            break;
            case SocketState.Socket_Error: {
                if (obj instanceof String) {
                    String value = (String) obj;
                    String strLst[] = value.split("#");
                    int sltIdx = -1;
                    if (strLst[0] != null) {
                        sltIdx = Integer.valueOf(strLst[0]);
                    }
                    video_surface.setVideoState(SocketState.Socket_Error);
                }
            }
            break;
            case SocketState.Socket_Close: {
                if (obj instanceof String) {
                    int sltIdx = Integer.valueOf((String) obj);
                    video_surface.setVideoState(SocketState.Socket_Close);
                }
            }
            break;
            case SocketState.Socket_LoginFailure: { // 连接到设备超时,关闭连接
                try {
                    String value = (String) obj;
                    int sltIdx = Integer.valueOf(value);
                    if (!(sltIdx >= 0 && sltIdx < 4))
                        return;

                    // 判断视频显示状态,防止重复操作
                    if (video_surface.getVisibility() == View.INVISIBLE) {
                        return;
                    }

                    // 提示
//                    ToastUtils.showShort(context, "连接超时，视频关闭");

                    // 关闭视频
                    closeVideo();
                } catch (Exception e) {
                    LogTools.addLogE("VideoDialog.onSocketState", e.getMessage());
                }
            }
            break;
            case SocketState.Socket_LoginSuccess: {
                // 得到索引值
                String value = (String) obj;
                String vls[] = value.split("#");
                int sltIdx = Integer.valueOf(vls[0]);

                // 渲染场景类
                video_surface.setVideoState(SocketState.Socket_LoginSuccess);

                // 启动解码任务
                DecoderTaskManager decManager = DecoderTaskManager.singleton();
                decManager.initDecoderTask(1920, 1080, sltIdx);
                decManager.startDecoderTask(sltIdx);
                decManager.getDecoder(sltIdx).setSurface(video_surface);

                // 渲染任务启动
                video_surface.startRender();
            }
            break;
            case SocketState.Socket_Read_Error: { // Socket数据读取异常
                try {
                    String value = (String) obj;
                    int sltIdx = Integer.valueOf(value);
                    if (!(sltIdx >= 0 && sltIdx < 4))
                        return;

                    // 判断视频显示状态,防止重复操作
                    if (video_surface.getVisibility() == View.INVISIBLE) {
                        return;
                    }

                    // 关闭视频
                   closeVideo();
//                    // 显示添加标记
//                    showHideAddMarker(View.VISIBLE, sltIdx);
//                    // 隐藏视频窗体
//                    showVideoTag(View.INVISIBLE, sltIdx);
//                    // 视频打开失败，隐藏视频详情
//                    hideVideoInfo(sltIdx);
                } catch (Exception e) {
                    LogTools.addLogE("VideoDialog.onSocketState", e.getMessage());
                }
            }
            break;
        }
    }

    private void openVideo() {

        int index = 0;
        try {
            // 删除对象,防止重复调用
            LinkEventProxyManager.removeProxy("video_socket");
            // 初始化对象
            LinkEventProxy proxy = LinkEventProxyManager.getProxy("video_socket");
            proxy.addLinkEvent(new OnLinkListener() {
                @Override
                public void callBackEvent(final Object obj, final int operate) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onSocketState(obj, operate);
                            }
                        });
                    } catch (Exception e) {
                        LogTools.addLogE("VideoDialog.initVideoSocketEvent", e.getMessage());
                    }
                }
            });

            // 初始化连接数据
            MediaConnectData cntData = new MediaConnectData();
            cntData.ip = "222.74.27.54";
            cntData.port = 21002;
            cntData.deviceId = 226;
            cntData.channelId = 1; // 主码流

            // 初始化流媒体对象
            MediaClientManager.singleton().initClient(index, cntData);
        } catch (Exception e) {
            LogTools.addLogE("openVideo", e.getMessage());
        }
    }

    /**
     * 关闭视频
     */
    private void closeVideo() {

        int index = 0;

        // 关闭socket
        MediaClientManager.singleton().finalizeClient(index);

        // 关闭渲染
        video_surface.closeSurfaceRender();

        // 停止解码
        DecoderTaskManager.singleton().stopDecoderTask(index);

        // 如果录像机是在录像中,则停止录像
        MediaRecordManager recordManager = MediaRecordManager.singleton();
        if (recordManager.isRecording() && recordManager.getVideoIndex() == index) {
            recordManager.stopRecord();
        }

        // 如果有音频播放则关闭当前音频播放
        MediaAudioPlayManager playManager = MediaAudioPlayManager.singleton();
        int playIndex = playManager.getPlayIndex();
        if (playIndex == index) {
            playManager.processAudioCommand(-1, false, true);
        }
    }
}
