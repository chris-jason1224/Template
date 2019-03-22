package com.cj.fun_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothService {

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;

    private ConnectThread mConnectThread;/**作为Client端，连接其他蓝牙设备**/
    private ConnectedThread mConnectedThread;/**作为Server端，等待其他蓝牙设备连接**/

    private Context context;

    //当前状态
    private int mState;

    /**打印机16进制命令**/
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

    /**构造方法**/
    public BluetoothService(Context context, Handler handler) {
        this.context = context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //初始化时蓝牙状态
        mState = BTCenter.BTState.STATE_NONE;
        mHandler = handler;
    }

    /**修改状态**/
    private synchronized void setState(int state) {
        mState = state;
        mHandler.obtainMessage(BTCenter.KeyCode.CODE_STATE_CHANGE, state, 0).sendToTarget();
    }

    /**获取当前蓝牙连接状态*/
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
        setState(BTCenter.BTState.STATE_LISTEN);

    }

    /**开启连接线程，作为C端连接其他远程蓝牙设备*/
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

        //修改当前状态为正在连接中
        setState(BTCenter.BTState.STATE_CONNECTING);

    }

    /**设备连接成功后回调**/
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

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        //连接成功后，保存mac地址，用于下一次自动连接
        app.getSpf().edit().putString("LinkedBTMAC", device.getAddress()).commit();

        setState(BTCenter.BTState.STATE_CONNECTED);
    }

    /**停止所有线程**/
    public synchronized void stop() {

        setState(BTCenter.BTState.STATE_NONE);
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
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != BTCenter.BTState.STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**连接失败后回调**/
    private void connectionFailed() {

        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(BTCenter.BTState.STATE_CONNECT_FAILED);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(BTCenter.BTState.STATE_DISCONNECTED);
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != BTCenter.BTState.STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case BTCenter.BTState.STATE_LISTEN:
                            case BTCenter.BTState.STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case BTCenter.BTState.STATE_NONE:
                            case BTCenter.BTState.STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            //通过 Socket 连接设备
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
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
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // Start the service over to restart listening mode
                BluetoothService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
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
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    byte[] buffer = new byte[256];
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    //当bytes=-1表示蓝牙断开连接，并且会抛出异常

                    if (bytes > 0) {
                        // Send the obtained bytes to the UI Activity
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget();
                    } else {
                        Log.e(TAG, "蓝牙断开连接");
                        connectionLost();

                        //不用再尝试重连了
//                        if(mState != STATE_NONE)
//                        {
//                            Log.e(TAG, "disconnected");
//                            // Start the service over to restart listening mode
//                            BluetoothService.this.start();
//                        }

                        break;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "蓝牙断开连接", e);

                    connectionLost();
                    //不用再尝试重连了

                    //add by chongqing jinou
//                    if(mState != STATE_NONE)
//                    {
//                        // Start the service over to restart listening mode
//                        BluetoothService.this.start();
//                    }
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
