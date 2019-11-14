package com.cj.common.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.cj.utils.list.ListUtil;

import java.util.List;

/**
 * Created by mayikang on 2018/8/21.
 * 系统工具类
 */

public class AppSystemUtil {

    private AppSystemUtil() {

    }

    private static class Holder {
        private static final AppSystemUtil instance = new AppSystemUtil();
    }

    public static AppSystemUtil getInstance() {
        return Holder.instance;
    }

    //APP版本名 manifest 中 versionName
    private String appVersionName;
    //App版本号 manifest 中 versionCode
    private int appVersionCode;

    public int getAppVersionCode(Context ctx) {
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
     *
     * @param context
     * @return
     */
    public String getAppVersionName(Context context) {

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
    public int getSystemVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取app名称
     *
     * @param context
     * @return
     */
    public String getAppName(Context context) {
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

    /**
     * 注意：因为推送服务等设置为运行在另外一个进程，这导致本Application会被实例化两次。
     * 而有些操作我们需要让应用的主进程时才进行，所以用到了这个方法
     */
    public boolean isInMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断app是否处于前台
     *
     * @param context
     * @return
     */
    public boolean isAppForeground(Context context) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //5.0以上版本
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            //获取系统当前运行的系统集合
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();

            if (!ListUtil.isEmpty(runningProcesses)) {
                //通过RunningProcess判断有一个缺陷，当app的某一个Service设置成START_STICKY时 importance值会一直是100，无法判断
                for (ActivityManager.RunningAppProcessInfo runningProcess : runningProcesses) {
                    String[] pkgList = runningProcess.pkgList;
                    if (pkgList != null && pkgList.length > 0) {
                        if (TextUtils.equals(pkgList[0], context.getPackageName()) && runningProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            return true;
                        }
                    }

                }
            }

        } else {

            //5.0以下，app处于前台时会置于RunningTask栈顶，5.0以上被废弃
            //获取RunningTasks需要get task权限
            List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);
            if (!ListUtil.isEmpty(task)) {
                ComponentName info = task.get(0).topActivity;
                if (null != info) {
                    return TextUtils.equals(info.getPackageName(), context.getPackageName());
                }
            }
        }

        return false;
    }

}
