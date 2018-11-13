package com.cj.common.util;

import android.os.Handler;
import android.os.Looper;
//帮助切换到主线程工具类
public class LooperUtil extends Handler {
    //默认实例化主线程
    private static LooperUtil instance = new LooperUtil(Looper.getMainLooper());

    protected LooperUtil(Looper looper) {
        super(looper);
    }

    public static LooperUtil getInstance() {
        return instance;
    }

    public  void runOnUiThread(Runnable runnable) {
        //判断当前线程是否为主线程，不是则切换到主线程
        if(Looper.getMainLooper().equals(Looper.myLooper())) {
            runnable.run();
        } else {
            instance.post(runnable);
        }

    }
}