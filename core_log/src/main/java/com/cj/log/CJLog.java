package com.cj.log;

import android.os.Environment;
import android.text.TextUtils;

import com.cj.manager.basement.BaseApplication;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mayikang on 2018/8/20.
 */

public class CJLog {

    //日志等级
    private int Log_Level_Normal = 1;//普通日志

    private int Log_Level_Error =2;//错误信息日志


    /**
     * //todo 日志加密、筛选、定时上传
     *   推送命令触发日志回捞
     */


    private CJLog() {
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private static class Holder {
        private static final CJLog instance = new CJLog();
    }

    public static CJLog getInstance() {
        return Holder.instance;
    }

    public void log_e(String message) {
        if(BuildConfig.DEBUG){
            Logger.e(message);
        }

    }

    public void log_d(String message) {
        Logger.d(message);
    }

    public void log_json(String json) {
        Logger.json(json);
    }

    //将日志保存为文件
    public void log_file(String content){
        try {
            saveExceptionToSDCard(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveExceptionToSDCard(String content) throws IOException {

        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            CJLog.getInstance().log_e("sd卡信息异常，无法写入日志");
            return;
        }

        String path = BaseApplication.getInstance().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/api/log/";
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(current));

        File file = new File(path + "_api_" + time + ".txt");

        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println();
            pw.println(content);
            pw.println();
            pw.close();
            CJLog.getInstance().log_e("日志保存成功:--> " + file.getAbsolutePath());
        } catch (Exception e) {
            CJLog.getInstance().log_e("日志保存失败");
            CJLog.getInstance().log_e(e.getMessage());
        }
    }


}
