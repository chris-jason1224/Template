package com.cj.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cj.common.util.NetUtil;

/**
 * Created by mayikang on 2018/7/24.
 */

public class NetworkStateOBReceiver extends BroadcastReceiver{

    private static OnNetworkChangedListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {

        //网络已经发生变化
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            //将网络状态回调给前台
            if(listener!=null){
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
        listener=onNetworkChangedListener;
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
