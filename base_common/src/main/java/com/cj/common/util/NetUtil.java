package com.cj.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * Created by mayikang on 17/2/6.
 * 网络检查工具
 */

public class NetUtil {
    private NetUtil() {

        /* cannot be instantiated */
        throw new UnsupportedOperationException("NetUtil 无法实例化");
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        //可能报空
        NetworkInfo info =cm.getActiveNetworkInfo();

        if(info!=null && info.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }

        return false;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        activity.startActivityForResult(intent, 0);
    }

    /**
     * 判断当前网络类型
     * <p>
     * 1、NETWORK_TYPE_1xRTT： 常量值：7 网络类型：1xRTT
     * 2、NETWORK_TYPE_CDMA ： 常量值：4 网络类型： CDMA （电信2g）
     * 3、NETWORK_TYPE_EDGE： 常量值：2 网络类型：EDGE（移动2g）
     * 4、NETWORK_TYPE_EHRPD： 常量值：14 网络类型：eHRPD
     * 5、NETWORK_TYPE_EVDO_0： 常量值：5 网络类型：EVDO 版本0.（电信3g）
     * 6、NETWORK_TYPE_EVDO_A： 常量值：6 网络类型：EVDO 版本A （电信3g）
     * 7、NETWORK_TYPE_EVDO_B： 常量值：12 网络类型：EVDO 版本B（电信3g）
     * 8、NETWORK_TYPE_GPRS： 常量值：1 网络类型：GPRS （联通2g）
     * 9、NETWORK_TYPE_HSDPA： 常量值：8 网络类型：HSDPA（联通3g）
     * 10、NETWORK_TYPE_HSPA： 常量值：10 网络类型：HSPA
     * 11、NETWORK_TYPE_HSPAP： 常量值：15 网络类型：HSPA+
     * 12、NETWORK_TYPE_HSUPA： 常量值：9 网络类型：HSUPA
     * 13、NETWORK_TYPE_IDEN： 常量值：11 网络类型：iDen
     * 14、NETWORK_TYPE_LTE： 常量值：13 网络类型：LTE(3g到4g的一个过渡，称为准4g)
     * 15、NETWORK_TYPE_UMTS： 常量值：3 网络类型：UMTS（联通3g）
     * 16、NETWORK_TYPE_UNKNOWN：常量值：0 网络类型：未知
     *
     * @param context
     * @return 返回值
     * -1：无网络连接
     * 0：位置网络类型
     * 1：wifi
     * 2：2g
     * 3：3g
     * 4：4g
     */
    public static int getCurrentNetType(Context context) {
        int type = -1;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = null;
        if(cm!=null){
            info = cm.getActiveNetworkInfo();
        }

        //无网络时
        if (info == null) {
            type = -1;
            return type;
        }

        //有网络时
        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = 1;
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = 2;
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = 3;
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                type = 4;
            } else {
                type = 0;
            }

        }
        return type;
    }
}