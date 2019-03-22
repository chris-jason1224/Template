package com.cj.business.pay.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import com.cj.log.CJLog;

/**
 * Author:chris - jason
 * Date:2019/2/2.
 * Package:com.cj.business.pay.lifecycle
 * 支付，生命周期观察者
 */

public class PayEventLifecycleObserver implements LifecycleObserver {

    private String tag = "PayEventLifeCycle - ";

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