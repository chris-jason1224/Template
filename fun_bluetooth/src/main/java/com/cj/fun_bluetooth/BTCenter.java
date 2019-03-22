package com.cj.fun_bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.cj.common.base.BaseApp;
import com.cj.common.provider.fun$bluetooth.BTState;
import com.cj.common.provider.fun$bluetooth.BTStateObserver;
import com.cj.common.util.DiskCacheUtil;
import com.cj.log.CJLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Set;

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
public class BTCenter {

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
    private BluetoothService mBTService;
    private BluetoothAdapter mBTAdapter;
    private static List<BTStateObserver> observerList;

    public static void init(List<BTStateObserver> observers){
        observerList =observers;
    }

    //构造方法
    private BTCenter() {

        mBTHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                //区分 通知message和 statemessage
                int what = msg.what;

                //通知事件，一般业务上的需求在这里处理
                if (KeyCode.CODE_NOTIFY == what) {

                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        /**
                         * mess对应BTCenter.NotifyEvent
                         */
                        String mess = bundle.getString("msg");//信息
                        String name = bundle.getString("remote_bt_device_name");//远程设备的name
                        String address = bundle.getString("remote_bt_device_mac");//远程设备的mac address

                        //蓝牙正在连接中
                        if(TextUtils.equals(mess, NotifyEvent.EVENT_CONNECTING)){

                        }

                        //蓝牙连接失败
                        if(TextUtils.equals(mess,NotifyEvent.EVENT_CONNECT_FAILED)){

                        }
                        //连接成功
                        if(TextUtils.equals(mess,NotifyEvent.EVENT_CONNECT_SUCCESS)){

                        }
                        //连接丢失（断开）
                        if(TextUtils.equals(mess,NotifyEvent.EVENT_CONNECT_LOST)){

                        }

                    }

                    return;
                }

                //状态改变回调，表征真实的蓝牙状态
                if (KeyCode.CODE_STATE_CHANGE == what) {
                    int state = msg.arg1;//蓝牙连接状态
                    //分发蓝牙变化状态
                    dispatchState(state);
                    return;
                }

                //接收到远程设备的消息
                if (KeyCode.CODE_DATA_READ == what) {
                    //接受到的数据 256字节缓冲
                    byte[] buffer_read = (byte[]) msg.obj;

                    return;
                }

                //向远程设备发送消息的回执
                if (KeyCode.CODE_DATA_WRITE == what) {

                    //刚刚发送出去的数据
                    byte[] buffer_write = (byte[]) msg.obj;


                    return;
                }


            }
        };

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        mBTService = new BluetoothService(mBTHandler);

    }

    public static BTCenter getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final BTCenter instance = new BTCenter();
    }

    /*****蓝牙操作方法******/

    //判断当前设备是否支持蓝牙
    public boolean isSupportBT() {
        //设备不支持蓝牙功能
        if (mBTAdapter == null) {
            CJLog.getInstance().log_e("当前设备不支持蓝牙功能");
            return false;
        }
        return true;
    }

    //开启扫描
    public boolean scan() {
        //设备不支持蓝牙会直接返回false
        if (!isSupportBT()) {
            return false;
        }

        //打开蓝牙适配器
        enableBT();

        return mBTAdapter.startDiscovery();
    }

    //开启蓝牙
    public void enableBT() {

        if (!isSupportBT()) {
            return;
        }

        //蓝牙未开启，请求开启蓝牙
        if (!mBTAdapter.isEnabled()) {
            //直接开启蓝牙
            boolean res = mBTAdapter.enable();
            //直接打开失败，或弹出一个框让用户选择开启，开启结果在Activity.onActivityResult中回调
            if (!res) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //获取栈顶的Activity
                Activity topAct = BaseApp.getInstance().getCurrentActivity();
                if (topAct != null) {
                    //通过栈顶activity来请求开启蓝牙功能
                    topAct.startActivityForResult(intent, 0);
                }
            }
        }
    }

    //关闭蓝牙
    public void disableBT() {
        if (!isSupportBT()) {
            return;
        }
        //关闭扫描
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
        //关闭蓝牙适配器
        mBTAdapter.disable();
    }

    //连接一个远程设备
    public void connect(@NonNull String mac) {
        //获取远程设备
        BluetoothDevice device = mBTAdapter.getRemoteDevice(mac);
        if (device != null) {
            //已配对的设备才能发起连接
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                mBTService.connect(device);
            }
        }
    }

    //蓝牙自动连接上一次的设备
    public void autoConnect() {
        if (!isSupportBT()) {
            return;
        }

        //获取上一次连接的蓝牙打印机设备mac地址
        String pairedMAC = DiskCacheUtil.getInstance().getBTDeviceAddress();
        //获取已经配对的设备列表
        Set<BluetoothDevice> pairedDeviceList = mBTAdapter.getBondedDevices();

        //该设备之前连接过打印机 且 查询出了已配对的设备列表 则 直接连接之前的旧设备
        if (!TextUtils.isEmpty(pairedMAC) && pairedDeviceList != null && pairedDeviceList.size() > 0) {
            BluetoothDevice targetDevice = null;//连接目标的mac地址
            for (BluetoothDevice device : pairedDeviceList) {
                if (TextUtils.equals(device.getAddress(), pairedMAC)) {
                    targetDevice = device;
                    //跳出for 循环
                    break;
                }
            }
            //当前蓝牙未连接、目标设备不为空、目标设备已配对 -->自动连接之前的设备
            if (mBTService.getState() != BTCenter.BTState.STATE_CONNECTED
                    && targetDevice != null
                    && targetDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                //其实发起蓝牙连接
                mBTService.connect(targetDevice);
            }
        }
    }


    //分发消息
    private void dispatchState(int state){

        if(observerList==null){
            return;
        }

        for (BTStateObserver observer : observerList){
            observer.onStateChanged(new com.cj.common.provider.fun$bluetooth.BTState(state));
        }

    }

}
