package com.cj.log.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.cj.log.CJLog;
import com.cj.log.view.CrashDialog;
import com.cj.manager.basement.BaseApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static android.os.Process.myPid;

/**
 * Created by admin on 2018/3/16.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static Context context;
    private String PATH;//存储路径
    private String FILE_PREFIX;//文件前缀名
    private String FILE_NAME_SUFFIX = ".txt";//文件后缀名


    //文件前缀+timestamp+后缀
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private CrashHandler(Context context) {

        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();

        //磁盘/Android/data/
        PATH = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/crash/log/";
        FILE_PREFIX = "_crash_";
    }


    private static class Holder {
        private static final CrashHandler INSTANCE = new CrashHandler(context);
    }

    public static CrashHandler getInstance(@NonNull Context c) {
        context = c;
        return Holder.INSTANCE;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        final boolean handled = handleException(ex);
        if (!handled && mDefaultCrashHandler != null) {
            //如果自己没处理交给系统处理
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {

            //弹出提示框告知用户app崩溃，由用户选择是否重启app
            new Thread() {
                @Override
                public void run() {
                    //为这个子线程初始化一个消息队列
                    Looper.prepare();
                    showDialog();
                    //开启消息队列
                    Looper.loop();
                }
            }.start();

        }
    }


    //自动重启app
    private void restartApp() {
        int time = 2000;
        try {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //先结束进程，再重新拉起
                    android.os.Process.killProcess(myPid());
                }
            }, time);

            //1秒后再重启进程
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = getLauncherIntent(context);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent restartIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + time, restartIntent);
            CJLog.getInstance().log_e("app即将自动重启");
        } catch (Exception e) {
            CJLog.getInstance().log_e("crash之后重启app失败");
        }

    }

    private void showDialog() {
        if(BaseApplication.getInstance().getCurrentActivity()!=null){
            CrashDialog dialog = new CrashDialog(BaseApplication.getInstance().getCurrentActivity(), new CrashDialog.ClickCallback() {
                @Override
                public void onClickClose() {
                    exit();
                }

                @Override
                public void onClickRestart() {
                    //重启app
                    restartApp();
                }
            });
            dialog.show();
        }

    }

    /**
     * @return 处理了该异常返回true, 否则false
     */
    private boolean handleException(Throwable ex) {

        if (ex == null) {
            return false;
        }
        //保存日志文件
        try {
            saveExceptionToSDCard(ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    private void saveExceptionToSDCard(Throwable ex) throws IOException {

        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            CJLog.getInstance().log_e("sd卡信息异常，无法写入崩溃日志");
            return;
        }

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(current));

        File file = new File(PATH + FILE_PREFIX + time + FILE_NAME_SUFFIX);

        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            pw.println();
            pw.println(getPhoneInformation());
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
            CJLog.getInstance().log_e("崩溃日志保存成功:--> " + file.getAbsolutePath());
        } catch (Exception e) {
            CJLog.getInstance().log_e("崩溃日志保存失败");
            CJLog.getInstance().log_e(e.getMessage());
        }
    }

    //获取手机设备信息
    private String getPhoneInformation() {

        StringBuffer sb = new StringBuffer();
        //获取app的版本号
        sb.append("App version name:")
                .append(getPackageInfo(context).versionName)
                .append(", version code:")
                .append(getPackageInfo(context).versionCode)
                .append("\n");

        //Android版本号
        sb.append("Android OS Version: ");
        sb.append(Build.VERSION.RELEASE);
        sb.append("_");
        sb.append(Build.VERSION.SDK_INT).append("\n");

        //手机制造商
        sb.append("Vendor: ");
        sb.append(Build.MANUFACTURER).append("\n");

        //手机型号
        sb.append("Model: ");
        sb.append(Build.MODEL).append("\n");

        //CPU架构
        sb.append("CPU ABI:");
        sb.append(android.os.Build.CPU_ABI);

        sb.append("\n");

        return sb.toString();
    }


    private PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }


    private Intent getLauncherIntent(Context context) {
        PackageManager pkm = context.getPackageManager();
        if (pkm != null) {
            Intent intent = pkm.getLaunchIntentForPackage(getPackageInfo(context).packageName);
            return intent;
        }

        return null;
    }

    private void exit() {
        List<Activity> list = BaseApplication.getInstance().getActivityTaskStack();
        if (list != null && list.size()>0) {
            try {
                Iterator activityIterator = list.iterator();
                while (activityIterator.hasNext()) {
                    Activity activity = (Activity) activityIterator.next();
                    activity.finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }


    }

}
