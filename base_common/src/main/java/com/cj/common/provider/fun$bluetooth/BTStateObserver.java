package com.cj.common.provider.fun$bluetooth;

/**
 * Author:chris - jason
 * Date:2019/3/22.
 * Package:com.cj.common.provider.fun$bluetooth
 * 蓝牙状态变化实时观察者
 */
public interface BTStateObserver {

    //蓝牙连接状态事回调
    void onStateChanged(BTState btState);

}
