package com.cj.bluetooth;

import android.content.Intent;

import com.cj.annontations.bus.ModuleEventCenter;
import com.cj.annontations.bus.EventRegister;
/**
 * Author:chris - jason
 * Date:2019-08-29.
 * Package:com.cj.bluetooth
 */
@ModuleEventCenter
public class FunBluetoothEventCenter {

    //蓝牙连接、配对事件 数据发射、接收 key
    @EventRegister(type = Intent.class)
    public String BluetoothEvent;

}
