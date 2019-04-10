package com.cj.common.util;

/**
 * Created by mayikang on 2018/7/24.
 */

import com.cj.common.var.KeyTag;
import com.cj.utils.safe.AESUtil;

/**
 * 磁盘缓存管理类
 * 存取本地 K-V 类型的用户数据
 */
public class DiskCacheUtil {

    private DiskCacheUtil() {

    }

    private static class Holder {
        private static final DiskCacheUtil instance = new DiskCacheUtil();
    }

    public static DiskCacheUtil getInstance() {
        return Holder.instance;
    }


    /**********用户登录Token相关 start ********************/

    //存储token
    public void saveToken(String token) {
        String encrypt = AESUtil.getInstance().encrypt(token);
        SPFUtil.getInstance().saveString(KeyTag.TAG_USER_TOKEN, encrypt);
    }

    //获取token
    public String getToken() {
        //加密后存储的数据
        String encrypt = SPFUtil.getInstance().getString(KeyTag.TAG_USER_TOKEN, "");
        //解密
        String decrypt = AESUtil.getInstance().decrypt(encrypt);

        return decrypt;
    }

    //删除token
    public void deleteToken() {
        SPFUtil.getInstance().removeOne(KeyTag.TAG_USER_TOKEN);
    }


    //清除用户本地数据
    public void clearUserVestige() {
        //1.删除登录token
        deleteToken();

        //2.删除用户信息

    }


    /**********用户登录Token相关 end ********************/


    //保存连接过的蓝牙设备地址
    public void saveBTDeviceAddress(String address) {
        String encrypt = AESUtil.getInstance().encrypt(address);
        SPFUtil.getInstance().saveString(KeyTag.TAG_LINKED_BT_DEVICE_MAC_ADDRESS, encrypt);
    }

    //获取连接过的蓝牙设备地址
    public String getBTDeviceAddress() {
        String encrypt = SPFUtil.getInstance().getString(KeyTag.TAG_LINKED_BT_DEVICE_MAC_ADDRESS, "");
        return AESUtil.getInstance().decrypt(encrypt);
    }


}
