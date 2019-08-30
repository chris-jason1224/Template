package com.cj.bluetooth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cj.common.bus.ModuleBus;

import gen.com.cj.bus.Gen$fun_bluetooth$Interface;


/**
 * Author:chris - jason
 * Date:2019/3/23.
 * Package:com.cj.fun_bluetooth
 * 蓝牙状态变化 广播接收器
 */
public class BTReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //广播监听到状态变化后，即刻发送事件通知 BTCenter
        ModuleBus.getInstance().of(Gen$fun_bluetooth$Interface.class).Gen$BluetoothEvent$Method().post(intent);
    }
}
