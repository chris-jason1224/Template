package com.cj.common.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.cj.common.exception.NotInitException;
import com.cj.common.util.ProcessUtil;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * Author:chris - jason
 * Date:2019-05-31.
 * Package:com.cj.common.ipc
 * 用于子进程与主进程通信工具
 * 子进程发送消息到主进程：connect2MainProcess
 */
public class IPC {

    private Context mContext;
    private IProcessCenter processCenter;

    private final int MAX_RETRY = 3;//binder断开后最大重连次数
    private int currentRetry = 0;//当前重连次数

    private int pid;//当前进程id
    private String pName;//当前进程名

    private boolean isBindOk = false;//bindService成功标志位

    private boolean isConnectOK = false;//服务是否连接成功

    private LinkedBlockingQueue<PostDataEntity> queue;//消息缓存
    private PollTask pollTask;//缓存消息分发线程


    /**
     * @param context Activity is best
     */
    public IPC(Context context) {
        this.mContext = context;

        //初始化工具类时获取一次进程信息即可
        this.pid = android.os.Process.myPid();
        pName = ProcessUtil.getProcessName(mContext, pid);

        queue = new LinkedBlockingQueue<>();

        //启动缓存处理线程
        if (pollTask == null) {
            pollTask = new PollTask();
            pollTask.start();
        }

        //连接主进程服务
        //@remark 连接服务是一个耗时的过程，且不可在子线程中执行，应该先实例化IPC类，再调用发送数据方法
        connect2MainProcess();
    }

    //连接进程service
    private void connect2MainProcess() {
        if (mContext == null) {
            throw new NotInitException(IPC.class);
        }
        Intent intent = new Intent(mContext, ProcessMainService.class);
        isBindOk = mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    //重连服务
    private void reconnect() {

        if (mContext == null) {
            throw new NotInitException(IPC.class);
        }

        if (currentRetry > MAX_RETRY) {
            return;
        }

        currentRetry++;
        Intent intent = new Intent(mContext, ProcessMainService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 建议在Activity.onDestroy方法中调用该方法，解绑服务，释放资源
     */
    public void shutdown() {

        if (isBindOk) {
            mContext.unbindService(mServiceConnection);
            mServiceConnection = null;
            processCenter = null;
            isBindOk = false;
            isConnectOK = false;
            currentRetry = MAX_RETRY + 1;
        }

        //结束掉缓存处理线程
        if (pollTask != null) {
            pollTask.setPoll(false);
        }

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            processCenter = IProcessCenter.Stub.asInterface(service);
            if (processCenter != null) {
                try {
                    //注册死亡监听
                    processCenter.asBinder().linkToDeath(deathRecipient, 0);
                    //注册接口回调
                    processCenter.registerProcessInfo(pid, pName, dataCallback);

                    isConnectOK = true;

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (processCenter != null) {
                try {
                    //取消注册接口回调
                    processCenter.unRegisterProcessInfo(pid, pName, dataCallback);

                    isConnectOK = false;

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //子进程接收主进程数据的回调方法
    private IDataCallback dataCallback = new IDataCallback.Stub() {
        @Override
        public void onReceiveFromMainProcess(PostDataEntity entity) throws RemoteException {
            if (entity != null) {
                String data = entity.getData();
                //发送数据到子进程（实例化该类的进程）各个注册了DataBusKey.ProcessSubEvent接收器的地方
                DataBus.get().with(DataBusKey.ProcessSubDataEvent.getKey(), DataBusKey.ProcessSubDataEvent.getT()).setValue(data);
            }
        }
    };

    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (processCenter != null) {
                //取消注册死亡监听
                processCenter.asBinder().unlinkToDeath(this, 0);
                processCenter = null;
                //重连
                reconnect();
            }
        }
    };


    /**
     * 子进程发送数据到主进程
     *
     * @param entity
     */
    public synchronized void notify2MainProcess(PostDataEntity entity) {
        //加入消息缓存队列
        try {
            if (!queue.contains(entity)) {
                queue.put(entity);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //消息队列处理线程
    private class PollTask extends Thread {

        private boolean isPoll = true;

        public void setPoll(boolean poll) {
            isPoll = poll;
        }

        @Override
        public void run() {
            while (isPoll) {
                //绑定服务成功、连接服务成功、队列中有缓存
                if (isBindOk && isConnectOK && queue.size() > 0) {
                    //取出队首元素，queue为空时会阻塞该线程
                    try {
                        PostDataEntity data = queue.take();
                        if (processCenter != null && processCenter.asBinder().isBinderAlive()) {
                            try {
                                processCenter.postData(data);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
