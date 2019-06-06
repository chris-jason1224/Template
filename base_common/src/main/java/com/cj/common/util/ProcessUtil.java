package com.cj.common.util;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.cj.utils.list.ListUtil;

import java.util.List;

/**
 * Author:chris - jason
 * Date:2019-06-03.
 * Package:com.cj.common.util
 */
public class ProcessUtil {

    /**
     * 通过进程号 获取当前进程名
     *
     * @return
     */
    public static String getProcessNameByPid(Context context, int pid) {
        ActivityManager ams = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (ams != null) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = ams.getRunningAppProcesses();
            if (!ListUtil.isEmpty(processInfos)) {
                for (ActivityManager.RunningAppProcessInfo info : processInfos) {
                    if (info.pid == pid) {
                        return info.processName;
                    }
                }
            }
        }

        return "";
    }


    /**
     * 通过进程名 获取当前进程的进程号
     * @param context
     * @param pName
     * @return
     */
    public static int getPidByProcessName(Context context, String pName) {
        ActivityManager ams = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (ams != null) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = ams.getRunningAppProcesses();
            if (!ListUtil.isEmpty(processInfos)) {
                for (ActivityManager.RunningAppProcessInfo info : processInfos) {
                    if (TextUtils.equals(info.processName,pName)) {
                        return info.pid;
                    }
                }
            }
        }

        return -1;
    }








}
