package com.cj.fun_bluetooth;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

    @IntDef({CODE_STATE_CHANGE,CODE_NOTIFY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface KeyCode {

        int CODE_STATE_CHANGE = 1;//蓝牙状态发生变化 key

        int CODE_NOTIFY = 2;//发布通知 key

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
    @StringDef({EVENT_CONNECTING,EVENT_CONNECT_FAILED,EVENT_CONNECT_SUCCESS,EVENT_CONNECT_LOST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NotifyEvent{
        String EVENT_CONNECTING ="EVENT_CONNECTING";//正在连接中事件
        String EVENT_CONNECT_FAILED ="EVENT_CONNECT_FAILED";//连接失败事件
        String EVENT_CONNECT_SUCCESS ="EVENT_CONNECT_SUCCESS";//连接成功事件
        String EVENT_CONNECT_LOST ="EVENT_CONNECT_LOST";//连接丢失（断开）事件
    }

    private BTCenter() {

    }

    public static BTCenter getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final BTCenter instance = new BTCenter();
    }


}
