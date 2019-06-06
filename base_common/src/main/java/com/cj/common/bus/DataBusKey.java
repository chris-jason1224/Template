package com.cj.common.bus;

import android.content.Intent;

import java.util.HashMap;

/**
 * Created by mayikang on 2018/8/23.
 * 保存数据总线的key
 */

public class DataBusKey {

    private DataBusKey(){

    }

    //支付宝支付结果 数据发射、接收 key
    public static final DataBusEntity<String> AliPayResult = new DataBusEntity<>("AliPayResult",String.class);

    //微信支付支付结果 数据发射、接收 key
    public static final DataBusEntity<String> WeChatPayResult = new DataBusEntity<>("WeChatPayResult",String.class);

    //微信分享结果 数据发射、接收 key
    public static final DataBusEntity<String> WeChatShareResult = new DataBusEntity<>("WeChatShareResult",String.class);

    //微信授权结果 数据发射、接收 key
    public static final DataBusEntity<HashMap> WeChatAuthResult = new DataBusEntity<>("WeChatAuthResult",HashMap.class);


    //蓝牙连接、配对事件 数据发射、接收 key
    public static final DataBusEntity<Intent> BluetoothEvent = new DataBusEntity<>("BluetoothEvent",Intent.class);

    //主进程接收消息key
    public static final DataBusEntity<String> ProcessMainReceiveDataEvent = new DataBusEntity<>("ProcessMainReceiveDataEvent",String.class);

    //主进程消息通信key
    public static final DataBusEntity<String> ProcessMainDataEvent = new DataBusEntity<>("ProcessMainDataEvent",String.class);

    //子进程消息通信key
    public static final DataBusEntity<String> ProcessSubDataEvent = new DataBusEntity<>("ProcessSubDataEvent",String.class);




}
