package com.cj;
import com.cj.annontations.bus.ModuleEventCenter;
import com.cj.annontations.bus.EventRegister;

import java.util.HashMap;

/**
 * Author:chris - jason
 * Date:2019-08-30.
 * Package:com.cj
 */
@ModuleEventCenter
public class FunBusinessEventCenter {

    //微信支付支付结果 数据发射、接收 key
    @EventRegister(type = String.class)
    public  String WeChatPayResult;

    //微信分享结果 数据发射、接收 key
    @EventRegister(type = String.class)
    public  String WeChatShareResult;

    //微信授权结果 数据发射、接收 key
    @EventRegister(type = HashMap.class)
    public  String WeChatAuthResult;

}
