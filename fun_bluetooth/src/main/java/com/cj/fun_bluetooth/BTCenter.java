package com.cj.fun_bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.Key;

import static com.cj.fun_bluetooth.BTCenter.BTState.STATE_CONNECTED;
import static com.cj.fun_bluetooth.BTCenter.BTState.STATE_CONNECTING;
import static com.cj.fun_bluetooth.BTCenter.BTState.STATE_LISTENING;
import static com.cj.fun_bluetooth.BTCenter.BTState.STATE_NONE;
import static com.cj.fun_bluetooth.BTCenter.KeyCode.CODE_NOTIFY;
import static com.cj.fun_bluetooth.BTCenter.KeyCode.CODE_STATE_CHANGE;
import static com.cj.fun_bluetooth.BTCenter.NotifyEvent.EVENT_CONNECTING;
import static com.cj.fun_bluetooth.BTCenter.NotifyEvent.EVENT_CONNECT_FAILED;
import static com.cj.fun_bluetooth.BTCenter.NotifyEvent.EVENT_CONNECT_LOST;
import static com.cj.fun_bluetooth.BTCenter.NotifyEvent.EVENT_CONNECT_SUCCESS;

/**
 * Author:chris - jason
 * Date:2019/3/22.
 * Package:com.cj.fun_bluetooth
 */
public class BTCenter implements IBT {

    //Handler what
    @IntDef({CODE_STATE_CHANGE, CODE_NOTIFY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface KeyCode {

        int CODE_STATE_CHANGE = 1;//蓝牙状态发生变化 key

        int CODE_NOTIFY = 2;//发布通知 key

        int CODE_DATA_READ = 3;//数据传输

        int CODE_DATA_WRITE = 4;//数据传输


    }

    //蓝牙连接状态字典值
    @IntDef({STATE_NONE, STATE_LISTENING, STATE_CONNECTING, STATE_CONNECTED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BTState {
        int STATE_NONE = 6;             //蓝牙未连接
        int STATE_LISTENING = 7;        //作为S端正在等待其他设备连接我
        int STATE_CONNECTING = 8;       //蓝牙正在连接中
        int STATE_CONNECTED = 9;        //蓝牙已连接
    }

    //蓝牙通知事件
    @StringDef({EVENT_CONNECTING, EVENT_CONNECT_FAILED, EVENT_CONNECT_SUCCESS, EVENT_CONNECT_LOST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NotifyEvent {
        String EVENT_CONNECTING = "EVENT_CONNECTING";//正在连接中事件
        String EVENT_CONNECT_FAILED = "EVENT_CONNECT_FAILED";//连接失败事件
        String EVENT_CONNECT_SUCCESS = "EVENT_CONNECT_SUCCESS";//连接成功事件
        String EVENT_CONNECT_LOST = "EVENT_CONNECT_LOST";//连接丢失（断开）事件
    }


    private Handler mBTHandler;


    private BTCenter() {

        mBTHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                //区分 通知message和 statemessage
                int what = msg.what;

                //通知消息
                if (KeyCode.CODE_NOTIFY == what) {

                    Bundle bundle = msg.getData();
                    if(bundle!=null){
                        /**
                         * mess对应BTCenter.NotifyEvent
                         */
                        String mess = bundle.getString("msg");//信息

                        String name = bundle.getString("remote_bt_device_name");//远程设备的name
                        String address = bundle.getString("remote_bt_device_mac");//远程设备的mac address

                    }


                    return;
                }

                //状态改变的消息
                if (KeyCode.CODE_STATE_CHANGE == what) {

                    int state = msg.arg1;//蓝牙连接状态

                    return;
                }

                //接收到远程设备的消息
                if (KeyCode.CODE_DATA_READ == what) {

                    //接受到的数据 256字节缓冲
                    byte[] buffer_read = (byte[]) msg.obj;

                    return;
                }

                //向远程设备发送消息的回执
                if (KeyCode.CODE_DATA_WRITE == what){

                    //刚刚发送出去的数据
                    byte[] buffer_write = (byte[]) msg.obj;


                    return;
                }


            }
        };

    }

    public static BTCenter getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final BTCenter instance = new BTCenter();
    }


}
