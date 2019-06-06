package com.cj.common.ipc;

import android.app.Service;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.cj.common.ipc.lifecycle.ProcessMainServiceLifecycleObserver;
import com.cj.common.util.LooperUtil;
import com.cj.common.util.ProcessUtil;
import com.cj.log.CJLog;

import java.math.MathContext;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 主进程Service Server端
 */
public class ProcessMainService extends Service implements LifecycleOwner {

    private LifecycleRegistry lifecycleRegistry;

    private RemoteCallbackList<IDataCallback> callbackList = null;

    //进程注册表，程序运行期间，进程可能会挂掉、重启，进程id会变化，但是进程名唯一
    private HashMap<String,Integer> processMap = null;

    //消息缓存队列
    private LinkedBlockingQueue<PostDataEntity> queue;
    //消息处理线程
    private MessageTask messageTask;

    public ProcessMainService() {
    }

    /*******LifeCycle start******/
    @Override
    public void onCreate() {
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        lifecycleRegistry.addObserver(new ProcessMainServiceLifecycleObserver());

        //注册DataBus接收器
        DataBus.get().with(DataBusKey.ProcessMainReceiveDataEvent.getKey(), DataBusKey.ProcessMainReceiveDataEvent.getT()).observe(this, receiver);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Fixme 验证自定义aidl权限
        if (checkCallingOrSelfPermission("com.cj.common.PROCESS_MAIN_SERVICE_PERMISSION") != PackageManager.PERMISSION_GRANTED) {
            return null;
        }


        callbackList = new RemoteCallbackList<>();
        processMap = new HashMap<>();

        queue = new LinkedBlockingQueue<>();

        messageTask = new MessageTask();
        messageTask.start();

        //绑定服务后开启数据接收器
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);

        if(intent!=null){
            String pName = intent.getStringExtra("pName");
            processMap.put(pName, ProcessUtil.getPidByProcessName(this,pName));
        }


        return mProcessMainBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(intent!=null){
            String pName = intent.getStringExtra("pName");
            processMap.remove(pName);
        }

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callbackList != null) {
            callbackList.kill();
        }

        if (messageTask != null) {
            messageTask.setRun(false);
        }

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    /*******LifeCycle end******/

    IBinder mProcessMainBinder = new IProcessCenter.Stub() {
        @Override
        public void registerProcessInfo(int pid, String pName, IDataCallback callback) throws RemoteException {
            if (callbackList != null) {
                callbackList.register(callback);
            }
        }

        @Override
        public void unRegisterProcessInfo(int pid, String pName, IDataCallback callback) throws RemoteException {
            if (callbackList != null) {
                callbackList.unregister(callback);
                processMap.remove(pName);
            }
        }

        @Override
        public void postData(PostDataEntity entity) throws RemoteException {
            //Client端进程通过此方法，像Server主进程发送消息
            //todo 使用DataBus分发消息到主进程各个接收器
            if (entity != null) {
                final String data = entity.getData();
                //切换到主线程，发送数据到主进程中每一个注册了DataBusKey.ProcessMainDataEvent的接收器
                LooperUtil.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DataBus.get().with(DataBusKey.ProcessMainDataEvent.getKey(), DataBusKey.ProcessMainDataEvent.getT()).setValue(data);
                    }
                });

            }
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            /**验证包名*/
            String packageName = null;

            if (getPackageManager() != null) {
                String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
                if (packages != null && packages.length > 0) {
                    packageName = packages[0];
                }
                //fixme 这里要按照实际包名来验证
                if (packageName == null || !packageName.startsWith("com.cj")) {
                    Log.e("onTransact()", "拒绝调用：" + packageName);
                    return false;
                }
            }

            return super.onTransact(code, data, reply, flags);
        }
    };


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }


    //回调消息给子进程
    private void notify2SubProcess(PostDataEntity entity) {

        if (callbackList == null) {
            return;
        }

        int len = callbackList.beginBroadcast();
        for (int i = 0; i < len; i++) {
            try {
                callbackList.getBroadcastItem(i).onReceiveFromMainProcess(entity);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        callbackList.finishBroadcast();
    }

    //缓存消息入队
    private synchronized void joinMessage(PostDataEntity data) {
        if (queue != null) {
            if (!queue.contains(data)) {
                try {
                    queue.put(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //缓存消息处理线程
    private class MessageTask extends Thread {

        private boolean isRun = true;

        public void setRun(boolean run) {
            isRun = run;
        }

        @Override
        public void run() {

            while (isRun) {
                if (queue.size() > 0) {
                    try {
                        PostDataEntity data = queue.take();
                        notify2SubProcess(data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    }

    //主进程数据接收器
    private Observer<String> receiver = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String s) {
            //将接收消息加入缓存队列
            PostDataEntity entity = new PostDataEntity(System.currentTimeMillis(), s);
            joinMessage(entity);
        }
    };

}
