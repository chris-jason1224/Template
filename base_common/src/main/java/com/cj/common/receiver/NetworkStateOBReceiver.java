package com.cj.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cj.common.util.NetUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayikang on 2018/7/24.
 * api level 28 google移除 CONNECTIVITY_ACTION，改为NetworkCallback
 */

public class NetworkStateOBReceiver extends BroadcastReceiver{

    private static List<OnNetworkChangedListener> listeners = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        //网络已经发生变化
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            //将网络状态回调给前台
            for (OnNetworkChangedListener listener:listeners){
                if(listener==null){
                    continue;
                }
                listener.onNetworkChanged(NetUtil.isConnected(context),NetUtil.getCurrentNetType(context));
            }
        }
    }


    /**
     * 暴露回调接口给前台使用
     * @param onNetworkChangedListener
     * //注意：Receiver接收完之后，会被回收，listener可能为空
     */
    public static void setOnNetworkChangedListener(OnNetworkChangedListener onNetworkChangedListener) {
            if(!listeners.contains(onNetworkChangedListener)){
                listeners.add(onNetworkChangedListener);
            }
    }

    public static void unRegisterNetworkChangedListener(OnNetworkChangedListener onNetworkChangedListener){
        //移除回调时可能正在发生网络改变
        synchronized (listeners){
            if(listeners.contains(onNetworkChangedListener)){
                listeners.remove(onNetworkChangedListener);
            }
        }
    }

    //回调接口
    public interface OnNetworkChangedListener{
        /**
         * @param isConnected 当前是否有网络连接
         * @param type 当前连接的类型
         */
         void onNetworkChanged(boolean isConnected, int type);

    }

}
