package com.cj.common.bus;

/**
 * Created by mayikang on 2018/8/23.
 * 保存数据总线的key
 */

public class DataBusKey {



    //支付宝支付结果 数据发射、接收 key
    public static final DataBusEntity<String> AliPayResult = new DataBusEntity<>("AliPayResult",String.class);

    //微信支付支付结果 数据发射、接收 key
    public static final DataBusEntity<String> WeChatPayResult = new DataBusEntity<>("WeChatPayResult",String.class);

}
