package com.cj.log;

import android.text.TextUtils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.w3c.dom.Text;

import java.security.Key;

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

}
