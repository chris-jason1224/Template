package com.cj.common.ipc;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.cj.common.util.LooperUtil;
import com.cj.log.CJLog;

/**
 * 主进程Service
 */
public class ProcessMainService extends Service {

    private RemoteCallbackList<IDataCallback> callbackList=null;
    private SparseArray<String> pidArray = null;

    public ProcessMainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Fixme 验证自定义aidl权限
        if(checkCallingOrSelfPermission("com.cj.common.PROCESS_MAIN_SERVICE_PERMISSION") != PackageManager.PERMISSION_GRANTED){
            return null;
        }
        callbackList = new RemoteCallbackList<>();
        pidArray = new SparseArray<>();

        return mProcessMainBinder;
    }

    IBinder mProcessMainBinder = new IProcessCenter.Stub() {
        @Override
        public void registerProcessInfo(int pid,String pName,IDataCallback callback) throws RemoteException {
            if(callbackList!=null){
                callbackList.register(callback);
                pidArray.put(pid,pName);
            }
        }

        @Override
        public void unRegisterProcessInfo(int pid, String pName,IDataCallback callback) throws RemoteException {
            if(callbackList!=null){
                callbackList.unregister(callback);
                pidArray.delete(pid);
            }
        }

        @Override
        public void postData(PostDataEntity entity) throws RemoteException {
            //作为Server端，Client端进程通过此方法，像Server主进程发送消息
            //todo 使用DataBus分发消息到主进程各个接收器
            if(entity!=null){
                final String data =entity.getData();
                CJLog.getInstance().log_e(data);
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

            if(getPackageManager()!=null){
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(callbackList!=null){
            callbackList.kill();
        }
    }

    //回调消息给子进程
    public void notify2SubProcess(PostDataEntity entity){

        if(callbackList==null){
            return;
        }

        int len=callbackList.beginBroadcast();
        for (int i=0;i<len;i++){
            try {
                callbackList.getBroadcastItem(i).onReceiveFromMainProcess(entity);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        callbackList.finishBroadcast();
    }


}
