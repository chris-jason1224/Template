package com.cj.bluetooth;

import android.app.Activity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cj.bluetooth.lifecycle.BTEventLifecycleObserver;
import com.cj.bluetooth.view.PopBTScanDialog;
import com.cj.common.base.BaseApp;
import com.cj.common.bus.ModuleBus;
import com.cj.common.provider.fun$bluetooth.BTStateObserver;
import com.cj.common.util.kv.DiskCacheUtil;
import com.cj.bluetooth.receiver.BTReceiver;
import com.cj.bluetooth.util.BluetoothService;
import com.cj.log.CJLog;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gen.com.cj.bus.Gen$fun_bluetooth$Interface;

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

    //Handler消息分类
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

    private PopBTScanDialog mDialog;//弹出扫描框
    private List<BluetoothDevice> pairedList;//已配对蓝牙设备集合
    private List<BluetoothDevice> unPairedList;//未配对蓝牙设备集合
    private static List<BTStateObserver> observerList;//蓝牙观察者集合
    //构造方法
    private BTCenter() {
        observerList = new ArrayList<>();

        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        lifecycleRegistry.addObserver(new BTEventLifecycleObserver());
        //注册DataBus接收器
        ModuleBus.getInstance().of(Gen$fun_bluetooth$Interface.class).Gen$BluetoothEvent$Method().observe(this,btEventObserver);
        initBT();
    }

    public static BTCenter getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final BTCenter instance = new BTCenter();
    }

    //注册观察者回调接口
    public  void register(BTStateObserver observer) {
        if (observerList != null && !observerList.contains(observer)) {
            observerList.add(observer);
        }
    }
    //取消注册回调接口
    public void unRegister(BTStateObserver observer){
        if (observerList != null && observerList.contains(observer)) {
            observerList.remove(observer);
        }
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

        if (mDialog != null) {
            mDialog.show();
        }

        //扫描框弹出后，开始接收蓝牙事件，扫描框关闭后，暂停接收蓝牙事件
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        //停止正在扫描的动作，重新开始扫描
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }

        return mBTAdapter.startDiscovery();
    }

    //开启蓝牙
    public boolean enableBT() {

        if (!isSupportBT()) {
            return false;
        }
        //用户打开蓝牙是个异步过程，最好是使用扫描前请用户开启蓝牙功能

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

        if (!enableBT()) {
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
                //自动连接发起蓝牙连接
                mBTService.connect(targetDevice);
            }
        }
    }

    //分发消息
    private void dispatchState(int state) {

        if (observerList == null) {
            return;
        }

        for (BTStateObserver observer: observerList) {
            observer.onStateChanged(new com.cj.common.provider.fun$bluetooth.BTState(state));
        }

    }

    public int getBTState() {
        return mBTService.getState();
    }

    //打印数据
    public void print(String message) {

        //不支持蓝牙模块
        if (!isSupportBT()) {
            return;
        }

        //蓝牙未开启
        if (!enableBT()) {
            return;
        }


        if (mBTService == null) {
            return;
        }

        //已连接才能打印
        if (mBTService.getState() != STATE_CONNECTED) {
            return;
        }
        //检查打印数据
        if (message.length() > 0) {
            byte[] send;
            try {
                send = message.getBytes("GB2312");
            } catch (UnsupportedEncodingException e) {
                send = message.getBytes();
            }
            mBTService.write(send);
        }

    }

    public void printLeft(){
        mBTService.printLeft();
    }
    public void printRight(){
        mBTService.printRight();
    }
    public void printCenter(){
        mBTService.printCenter();
    }
    public void printSize(int size){
        mBTService.printSize(size);
    }

    /**
     *             String start = "*** 懒购外卖 ***\n\n";
     *             bt.printCenter();
     *             bt.printSize(1);
     *             bt.printMessage(start);
     *
     *             //订单小号
     *             String StoreOrderNo = "NO:1234567890abc%$#@" + "\n\n";
     *             bt.printCenter();
     *             bt.printSize(1);
     *             bt.printMessage(StoreOrderNo);
     *
     *             //店铺名字
     *             String storeName = "韩式烤肉" + "\n\n";
     *             bt.printCenter();
     *             bt.printSize(1);
     *             bt.printMessage(storeName);
     *
     *             //下单时间
     *             String payTime = "支付时间:" + "2019-03-24 12:26" + "\n\n";
     *             bt.printLeft();
     *             bt.printSize(0);
     *             bt.printMessage(payTime);
     */

    /****
     *     //title
     *     String start = "*** 懒购外卖 ***\n\n";
     *                 mBTService.printCenter();
     *                 mBTService.printSize(1);
     *     print(start);
     *
     *     //订单小号
     *     String StoreOrderNo = orderBean.getStoreOrderNo() + "\n\n";
     *                 mBTService.printCenter();
     *                 mBTService.printSize(1);
     *     print(StoreOrderNo);
     *
     *     //店铺名字
     *     String storeName = orderBean.getShopBean().getName() + "\n\n";
     *                 mBTService.printCenter();
     *                 mBTService.printSize(1);
     *     print(storeName);
     *
     *     //订单号
     *     String orderNum = "订单号:" + orderBean.getName() + "\n\n";
     *                 mBTService.printLeft();
     *                 mBTService.printSize(0);
     *     print(orderNum);
     *
     *     //下单时间
     *     String payTime = "支付时间:" + orderBean.getInsertTime() + "\n\n";
     *                 mBTService.printLeft();
     *                 mBTService.printSize(0);
     *     print(payTime);
     *
     */


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
                    //连接成功后会返回 name + address
                    String name = bundle.getString("remote_bt_device_name");//远程设备的name
                    String address = bundle.getString("remote_bt_device_mac");//远程设备的mac address

                    //蓝牙正在连接中
                    if (TextUtils.equals(mess, NotifyEvent.EVENT_CONNECTING)) {
                        t("蓝牙正在连接");
                    }

                    //蓝牙连接失败
                    if (TextUtils.equals(mess, NotifyEvent.EVENT_CONNECT_FAILED)) {
                        t("蓝牙连接失败");
                    }
                    //连接成功
                    if (TextUtils.equals(mess, NotifyEvent.EVENT_CONNECT_SUCCESS)) {
                        t("蓝牙连接成功");
                    }
                    //连接丢失（断开）
                    if (TextUtils.equals(mess, NotifyEvent.EVENT_CONNECT_LOST)) {
                        t("蓝牙连接断开");
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
                    CJLog.getInstance().log_e("蓝牙开始扫描");
                    //蓝牙开始扫描，清空数据
                    pairedList.clear();
                    unPairedList.clear();

                    if (mDialog != null) {
                        mDialog.setScanProgress(1);
                    }
                }

                //扫描结束
                if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                    CJLog.getInstance().log_e("蓝牙扫描结束");
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
                            t("蓝牙正在配对中");
                            break;
                        //配对成功
                        case BluetoothDevice.BOND_BONDED:
                            scan();
                            break;
                    }
                    return;
                }

                /***连接相关******/

                //接收到请求连接
                if (TextUtils.equals(action, "quest_for_bt_connect")) {
                    BluetoothDevice remote_device = intent.getParcelableExtra("remote_device");
                    if (remote_device != null && !TextUtils.isEmpty(remote_device.getAddress())) {
                        connect(remote_device.getAddress());
                    }
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


    private void t(String msg) {
        Toast.makeText(BaseApp.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

}
