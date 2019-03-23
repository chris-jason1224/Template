package com.cj.bluetooth;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Log;

import com.cj.bluetooth.lifecycle.BTEvnetLifecycleObserver;
import com.cj.bluetooth.view.PopBTScanDialog;
import com.cj.common.base.BaseApp;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.cj.common.provider.fun$bluetooth.BTStateObserver;
import com.cj.common.util.DiskCacheUtil;
import com.cj.bluetooth.receiver.BTReceiver;
import com.cj.bluetooth.util.BluetoothService;
import com.cj.log.CJLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.cj.bluetooth.BTCenter.BTState.STATE_CONNECTED;
import static com.cj.bluetooth.BTCenter.BTState.STATE_CONNECTING;
import static com.cj.bluetooth.BTCenter.BTState.STATE_LISTENING;
import static com.cj.bluetooth.BTCenter.BTState.STATE_NONE;
import static com.cj.bluetooth.BTCenter.KeyCode.CODE_NOTIFY;
import static com.cj.bluetooth.BTCenter.KeyCode.CODE_STATE_CHANGE;
import static com.cj.bluetooth.BTCenter.NotifyEvent.EVENT_CONNECTING;
import static com.cj.bluetooth.BTCenter.NotifyEvent.EVENT_CONNECT_FAILED;
import static com.cj.bluetooth.BTCenter.NotifyEvent.EVENT_CONNECT_LOST;
import static com.cj.bluetooth.BTCenter.NotifyEvent.EVENT_CONNECT_SUCCESS;

/**
 * Author:chris - jason
 * Date:2019/3/22.
 * Package:com.cj.fun_bluetooth
 */
public class BTCenter implements LifecycleOwner {


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


    private LifecycleRegistry lifecycleRegistry;
    private BTHandler mBTHandler;
    private BluetoothService mBTService;
    private BluetoothAdapter mBTAdapter;
    private BTReceiver mBTReceiver;
    private IntentFilter mIntentFilter;

    private PopBTScanDialog mDialog;

    private List<BluetoothDevice> pairedList;//已配对蓝牙设备集合
    private List<BluetoothDevice> unPairedList;//未配对蓝牙设备集合

    private static List<BTStateObserver> observerList;

    public static void init(List<BTStateObserver> observers) {
        observerList = observers;
    }


    //构造方法
    private BTCenter() {

        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        lifecycleRegistry.addObserver(new BTEvnetLifecycleObserver());

        //注册DataBus接收器
        DataBus.get().with(DataBusKey.BluetoothEvent.getKey(), DataBusKey.BluetoothEvent.getT()).observe(this, btEventObserver);

        initBT();
    }

    public static BTCenter getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final BTCenter instance = new BTCenter();
    }


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }


    /*****蓝牙操作方法******/

    private void initBT() {

        mBTHandler = new BTHandler();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTService = new BluetoothService(mBTHandler, mBTAdapter);

        //初始化蓝牙广播接收器
        mBTReceiver = new BTReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mIntentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        mIntentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        mIntentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        mIntentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
        mIntentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");

        pairedList = new ArrayList<>();
        unPairedList = new ArrayList<>();

    }


    //判断当前设备是否支持蓝牙
    public boolean isSupportBT() {
        //设备不支持蓝牙功能
        if (mBTAdapter == null) {
            CJLog.getInstance().log_e("当前设备不支持蓝牙功能");
            return false;
        }
        return true;
    }

    //扫描附近可用蓝牙
    public synchronized boolean scan() {

        //打开蓝牙
        if (!enableBT()) {
            return false;
        }


        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }

        //注册广播接收器
        try {
            Activity topAct = BaseApp.getInstance().getCurrentActivity();
            if (topAct != null) {
                topAct.registerReceiver(mBTReceiver, mIntentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //显示正在扫描蓝牙的弹框
        if (mDialog == null && BaseApp.getInstance().getCurrentActivity() != null) {

            mDialog = new PopBTScanDialog(BaseApp.getInstance().getCurrentActivity());

            //当dialog 关闭后，强制销毁dialog，避免切换栈顶Activity导致窗体泄露
            mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mDialog = null;
                    //扫描框弹出后，开始接收蓝牙事件，扫描框关闭后，暂停接收蓝牙事件
                    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
                }
            });
        }

        if(mDialog!=null && !mDialog.isShowing()){

            mDialog.show();

        }

        //扫描框弹出后，开始接收蓝牙事件，扫描框关闭后，暂停接收蓝牙事件
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);

        return mBTAdapter.startDiscovery();
    }


    //开启蓝牙
    public boolean enableBT() {

        if (!isSupportBT()) {
            return false;
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


        return mBTAdapter.isEnabled();
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
    private void dispatchState(int state) {

        if (observerList == null) {
            return;
        }

        for (BTStateObserver observer : observerList) {
            observer.onStateChanged(new com.cj.common.provider.fun$bluetooth.BTState(state));
        }

    }

    public int getBTState() {
        return mBTService.getState();
    }


    //使用Handler来接收BluetoothService传递的消息，该Handler属于子线程
    private class BTHandler extends Handler {

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
                    if (TextUtils.equals(mess, NotifyEvent.EVENT_CONNECTING)) {

                    }

                    //蓝牙连接失败
                    if (TextUtils.equals(mess, NotifyEvent.EVENT_CONNECT_FAILED)) {

                    }
                    //连接成功
                    if (TextUtils.equals(mess, NotifyEvent.EVENT_CONNECT_SUCCESS)) {

                    }
                    //连接丢失（断开）
                    if (TextUtils.equals(mess, NotifyEvent.EVENT_CONNECT_LOST)) {

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
    }


    //蓝牙事件观察者 接收BTReceiver发射的 intent 数据
    //蓝牙扫描相关事件
    //蓝牙配对相关事件
    private Observer<Intent> btEventObserver = new Observer<Intent>() {
        @Override
        public void onChanged(@Nullable Intent intent) {

            if (intent != null) {

                String action = intent.getAction();


                /*****扫描相关*******/
                //扫描开始
                if ("android.bluetooth.adapter.action.DISCOVERY_STARTED".equals(action)) {
                    CJLog.getInstance().log_e("开始扫描");
                    //蓝牙开始扫描，清空数据
                    pairedList.clear();
                    unPairedList.clear();

                    if (mDialog != null) {
                        mDialog.setScanProgress(1);
                    }

                }

                //扫描结束
                if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                    //取消注册监听
                    try {
                        // unregisterReceiver(btReceiver);
                    } catch (Exception e) {
                        CJLog.getInstance().log_e("扫描结束异常:" + e.toString());
                    } finally {

                        if (mDialog != null) {
                            mDialog.setDeviceList(pairedList, unPairedList);
                            mDialog.setScanProgress(2);
                        }

                    }

                    return;
                }

                //找到设备
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //获取设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        int state = device.getBondState();

                        //已配对设备
                        if (12 == state && !pairedList.contains(device)) {
                            pairedList.add(device);
                        }
                        //未配对设备
                        if (10 == state && !unPairedList.contains(device)) {
                            unPairedList.add(device);
                        }

                    }

                    return;
                }

                /******配对相关*******/

                //绑定设备（配对）
                if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

                    int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);

                    switch (state) {
                        //配对失败
                        case BluetoothDevice.BOND_NONE:
                            scan();
                            break;
                        //正在配对中
                        case BluetoothDevice.BOND_BONDING:

                            break;
                        //配对成功
                        case BluetoothDevice.BOND_BONDED:
                            scan();
                            break;
                    }

                    return;
                }


                if (action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                    Log.e("gg", "android.bluetooth.device.action.ACL_DISCONNECTED");
                }

                if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                    Log.e("gg", "android.bluetooth.device.action.ACL_CONNECTED");
                }

                if (action.equals("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED")) {
                    Log.e("gg", "android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
                }


            }


        }
    };


}
