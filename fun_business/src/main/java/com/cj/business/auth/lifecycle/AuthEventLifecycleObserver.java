package com.cj.business.auth.lifecycle;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.cj.log.CJLog;

/**
 * Author:chris - jason
 * Date:2019/2/12.
 * Package:com.cj.business.auth.lifecycle
 */

public class AuthEventLifecycleObserver implements LifecycleObserver {

    private String tag = "AuthEventLifeCycle - ";

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
