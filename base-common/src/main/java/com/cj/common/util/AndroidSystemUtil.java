package com.cj.common.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by mayikang on 2018/8/21.
 * 系统工具类
 */

public class AndroidSystemUtil {

    private AndroidSystemUtil(){

    }

    private static class Holder{
        private static final AndroidSystemUtil instance=new AndroidSystemUtil();
    }

    public static AndroidSystemUtil getInstance(){
        return Holder.instance;
    }

    //APP版本名 manifest 中 versionName
    private  String appVersionName;
    //App版本号 manifest 中 versionCode
    private int appVersionCode;

    public  int getAppVersionCode(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext().getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            appVersionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionCode;
    }


    /**
     * 获取app版本名
     * @param context
     * @return
     */
    public  String getAppVersionName(Context context) {

        if (appVersionName == null) {
            PackageManager manager = context.getPackageManager();
            try {
                android.content.pm.PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                appVersionName = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (appVersionName == null) {
            return "";
        } else {
            return appVersionName;
        }
    }

    //获取android系统版本
    public int getSystemVersion(){
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取app名称
     * @param context
     * @return
     */
    public String getAppName(Context context){
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }

        return (String) packageManager.getApplicationLabel(applicationInfo);

    }

}
