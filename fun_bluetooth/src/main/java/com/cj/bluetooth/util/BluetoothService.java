package com.cj.bluetooth.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cj.common.util.DiskCacheUtil;
import com.cj.bluetooth.BTCenter;
import com.cj.log.CJLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothService {

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;

    /**
     * 作为Server端，等待其他蓝牙设备连接
     **/
    private AcceptThread mAcceptThread;

    /**
     * 作为Client端，连接其他蓝牙设备
     **/
    private ConnectThread mConnectThread;

    /**
     * C、S端连接成功后，管理连接BluetoothSocket，进行数据读写传输
     */
    private ConnectedThread mConnectedThread;

    private static final String NAME = "BTPrinter";

    //当前状态
    private int mState;

    /**
     * 打印机16进制命令
     **/
    public static final byte[][] byteCommands = {
            {0x1b, 0x40},// 复位打印机
            {0x1b, 0x4d, 0x00},// 标准ASCII字体
            {0x1b, 0x4d, 0x01},// 压缩ASCII字体
            {0x1d, 0x21, 0x00},// 字体不放大
            {0x1d, 0x21, 0x02},// 宽高加倍
            {0x1d, 0x21, 0x11},// 宽高加倍
//            { 0x1d, 0x21, 0x11 },// 宽高加倍
            {0x1b, 0x45, 0x00},// 取消加粗模式
            {0x1b, 0x45, 0x01},// 选择加粗模式
            {0x1b, 0x7b, 0x00},// 取消倒置打印
            {0x1b, 0x7b, 0x01},// 选择倒置打印
            {0x1d, 0x42, 0x00},// 取消黑白反显
            {0x1d, 0x42, 0x01},// 选择黑白反显
            {0x1b, 0x56, 0x00},// 取消顺时针旋转90°
            {0x1b, 0x56, 0x01},// 选择顺时针旋转90°

            {0x1b, 0x61, 0x30},// 左对齐
            {0x1b, 0x61, 0x31},// 居中对齐
            {0x1b, 0x61, 0x32},// 右对齐
//            { 0x1b, 0x69 },// 切纸
    };


    public void print(int i) {
        write(byteCommands[i]);
    }

    public void printReset() {
        if (getState() != BTCenter.BTState.STATE_CONNECTED) {
            return;
        }
        write(byteCommands[0]);
    }

//    public void printDensity(int density){
//    	if(density<1)
//    		density = 4;
//        if(density>8)
//        	density = 8;
//        byte[] send = new byte[3];//ESC m n
//        send[0] = 0x1B;
//        send[1] = 0x6D;
//    	send[2] = (byte) density;
//    	write(send);
//    }

    public void printSize(int size) {
        if (getState() != BTCenter.BTState.STATE_CONNECTED) {
            return;
        }
        switch (size) {
            case 1:
                write(byteCommands[4]);
                break;
            case 2:
                write(byteCommands[5]);
                break;
            default:
                write(byteCommands[3]);
                break;
        }
    }

    public void printLeft() {
        if (getState() != BTCenter.BTState.STATE_CONNECTED) {
            return;
        }
        write(byteCommands[14]);
    }

    public void printRight() {
        if (getState() != BTCenter.BTState.STATE_CONNECTED) {
            return;
        }
        write(byteCommands[16]);
    }

    public void printCenter() {
        if (getState() != BTCenter.BTState.STATE_CONNECTED) {
            return;
        }
        write(byteCommands[15]);
    }

    /**
     * 构造方法
     **/
    public BluetoothService(@NonNull Handler handler,BluetoothAdapter adapter) {
        mAdapter = adapter;
        mHandler = handler;
        //初始化时蓝牙状态
        mState = BTCenter.BTState.STATE_NONE;
    }

    /**
     * 修改状态
     **/
    private synchronized void setState(int state) {
        mState = state;
        mHandler.obtainMessage(BTCenter.KeyCode.CODE_STATE_CHANGE, state, 0).sendToTarget();
    }

    /**
     * 发出通知事件
     *
     * @param device
     * @param notifyEvent
     */
    private synchronized void sendNotify(String notifyEvent, @Nullable BluetoothDevice device) {

        Message msg = mHandler.obtainMessage(BTCenter.KeyCode.CODE_NOTIFY);
        Bundle bundle = new Bundle();
        bundle.putString("msg", notifyEvent);
        if (device != null) {
            bundle.putString("remote_bt_device_name", device.getName());
            bundle.putString("remote_bt_device_mac", device.getAddress());
        }
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * 获取当前蓝牙连接状态
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        //先取消其他线程

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }

        //等待其他设备连接
        setState(BTCenter.BTState.STATE_LISTENING);

    }

    /**
     * 开启连接线程，作为C端连接其他远程蓝牙设备
     */
    public synchronized void connect(BluetoothDevice device) {

        //断开作为C端已经连接的设备
        if (mState == BTCenter.BTState.STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        //断开作为S端已经连接的设备
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // 获取 socket 对象
        mConnectThread = new ConnectThread(device);
        //连接 socket
        mConnectThread.start();

        //发出通知事件
        sendNotify(BTCenter.NotifyEvent.EVENT_CONNECTING,null);

        //修改当前状态为正在连接中
        setState(BTCenter.BTState.STATE_CONNECTING);

    }

    /**
     * 设备连接成功后回调
     **/
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

        /**断开作为C端连接的设备**/
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        /**断开作为S端连接的设备**/
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        /**关闭接收外部连接请求的线程**/
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        /**重启一个线程来管理连接的设备**/
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        //连接成功后，保存mac地址，用于下一次自动连接
        DiskCacheUtil.getInstance().saveBTDeviceAddress(device.getAddress());

        //发出通知事件
        sendNotify(BTCenter.NotifyEvent.EVENT_CONNECT_SUCCESS,device);
        //修改蓝牙连接状态
        setState(BTCenter.BTState.STATE_CONNECTED);
    }

    /**
     * 停止所有线程
     **/
    public synchronized void stop() {

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        setState(BTCenter.BTState.STATE_NONE);
    }


    /**
     * 写出数据
     **/
    public void write(byte[] out) {
        ConnectedThread r;

        synchronized (this) {
            if (mState != BTCenter.BTState.STATE_CONNECTED) {
                return;
            }

            r = mConnectedThread;
        }

        //调用ConnectedThread的写出方法
        r.write(out);
    }

    /**
     * 连接失败后回调
     **/
    private void connectionFailed() {

        sendNotify(BTCenter.NotifyEvent.EVENT_CONNECT_FAILED,null);
        setState(BTCenter.BTState.STATE_NONE);
    }

    /**
     * 连接断开后回调
     **/
    private void connectionLost() {

        sendNotify(BTCenter.NotifyEvent.EVENT_CONNECT_LOST,null);

        setState(BTCenter.BTState.STATE_NONE);
    }

    /**
     * 作为S端等待其他远程设备连接
     **/
    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                CJLog.getInstance().log_e("listen() failed");
            }
            mmServerSocket = tmp;
        }

        public void run() {

            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != BTCenter.BTState.STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    CJLog.getInstance().log_e("accept() failed");
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {

                            case BTCenter.BTState.STATE_LISTENING:
                            case BTCenter.BTState.STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice());
                                break;

                            case BTCenter.BTState.STATE_NONE:
                            case BTCenter.BTState.STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {

                                }
                                break;
                        }
                    }
                }
            }

        }

        public void cancel() {

            try {
                mmServerSocket.close();
            } catch (IOException e) {

            }
        }
    }


    /**
     * 作为C端主动连接其他设备
     **/
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            //通过 Socket 连接设备
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {

            }
            mmSocket = tmp;
        }

        public void run() {

            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {

                }
                // Start the service over to restart listening mode
                //BluetoothService.this.start();
                return;
            }

            // 连接成功后结束该线程
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {

            }
        }

    }

    /**
     * 蓝牙连接成功后，数据传输线程
     **/
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            int bytes;
            /**
             * read(byte[]) 读取流是阻塞的方法，直到读取到内容
             * write(byte[]) 通常不会阻塞，如果远程设备调用read不够快导致中间缓冲区满，也可能会阻塞
             * 所以线程主循环要用来读取输入流
             */
            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    //当bytes=-1表示蓝牙断开连接，并且会抛出异常

                    if (bytes > 0) {
                        //作为S端，接收到输入流中传输的数据
                        mHandler.obtainMessage(BTCenter.KeyCode.CODE_DATA_READ, bytes, -1, buffer).sendToTarget();
                    } else {
                        CJLog.getInstance().log_e("蓝牙断开连接");
                        connectionLost();

                        break;
                    }
                } catch (IOException e) {
                    CJLog.getInstance().log_e("蓝牙断开连接");

                    connectionLost();

                    break;
                }
            }
        }

        /**
         * 发送数据
         **/
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                //作为C端向输出流写出数据
                mHandler.obtainMessage(BTCenter.KeyCode.CODE_DATA_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {

            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {

            }
        }
    }
}
