package com.cj.common.provider.fun$bluetooth;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * fun-bluetooth对外提供蓝牙服务的约束接口
 * 蓝牙功能流程：
 *   0、开启蓝牙（这一步放在具体的某一个页面上执行）
 *   1、扫描可用蓝牙
 *   2、点击为配对蓝牙进行配对
 *   3、点击已配对蓝牙进行连接
 *   4、传输数据
 */
public interface IBTProvider extends IProvider {

     //注册蓝牙状态变化观察者
     void registerBTStateObserver(BTStateObserver observer);

     //发起蓝牙扫描
     void scan();

     /**
      * @param address 远程设备的mac地址
      */
     void connect(String address);

     //断开连接
     void disConnect(String address);

     //自动连接上一次的设备
     void autoConnectTo();

     //获取蓝牙连接状态
     int getBTState();

     void printMessage(@NonNull String message);

     void printLeft();

     void printRight();

     void printCenter();

     void printSize(int size);
}
