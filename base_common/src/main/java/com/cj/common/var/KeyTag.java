package com.cj.common.var;

/**
 * Created by mayikang on 2018/7/22.
 */

public class KeyTag {

    //页面路由中包含次标识符，表示需要登录
    public static final String TAG_USER_NEED_LOGIN="/USR/";

    //本地广播传递登录结果时intent的key
    public static final String TAG_LOGIN_RESULT="TAG_LOGIN_RESULT";

    //本地广播传递登录结果时的intent filter
    public static final String TAG_LOGIN_FILTER="TAG_LOGIN_FILTER";

    //存储在本地的token
    public static final String TAG_USER_TOKEN="TAG_USER_TOKEN";

    //存储在本地的连接过的蓝牙设备的mac地址
    public static final String TAG_LINKED_BT_DEVICE_MAC_ADDRESS = "TAG_LINKED_BT_DEVICE_MAC_ADDRESS";

}
