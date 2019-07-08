package com.cj.common.ipc.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.cj.log.CJLog;

/**
 * Author:chris - jason
 * Date:2019/6/4.
 * Package:com.cj.business.auth.lifecycle
 * 主进程ProcessMainService生命周期观察者
 */

public class ProcessMainServiceLifecycleObserver implements LifecycleObserver {

    private String tag = "ProcessMainServiceLifecycleObserver - ";

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        CJLog.getInstance().log_e(tag+"Create");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        CJLog.getInstance().log_e(tag+"onStart");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        CJLog.getInstance().log_e(tag+"onResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        CJLog.getInstance().log_e(tag+"onPause");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        CJLog.getInstance().log_e(tag+"onStop");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        CJLog.getInstance().log_e(tag+"onDestroy");
    }
}
